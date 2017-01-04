package com.hn.d.valley.main.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIContentView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.AmapBean;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.other.AmapUIView;
import com.hn.d.valley.widget.HnLoading;
import com.hn.d.valley.widget.HnRefreshLayout;
import com.lzy.imagepicker.ImagePickerHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：聊天界面
 * 创建人员：Robi
 * 创建时间：2016/12/27 17:46
 * 修改人员：Robi
 * 修改时间：2016/12/27 17:46
 * 修改备注：
 * Version: 1.0.0
 */
public class ChatUIView extends UIContentView implements IAudioRecordCallback {

    protected AudioRecorder audioMessageHelper;
    String account;
    @BindView(R.id.group_view)
    RadioGroup mGroupView;
    @BindView(R.id.chat_root_layout)
    RSoftInputLayout mChatRootLayout;
    @BindView(R.id.input_view)
    ExEditText mInputView;
    @BindView(R.id.record_view)
    TextView mRecordView;
    ChatControl mChatControl;
    SessionTypeEnum sessionType;
    @BindView(R.id.refresh_layout)
    HnRefreshLayout mRefreshLayout;
    @BindView(R.id.recycler_view)
    RRecyclerView mRecyclerView;
    @BindView(R.id.message_expression_view)
    RadioButton mMessageExpressionView;
    @BindView(R.id.message_add_view)
    RadioButton mMessageAddView;
    @BindView(R.id.send_view)
    TextView mSendView;
    @BindView(R.id.timer)
    Chronometer mTimer;
    @BindView(R.id.timer_tip)
    TextView mTimerTip;
    @BindView(R.id.timer_tip_container)
    LinearLayout mTimerTipContainer;
    @BindView(R.id.layoutPlayAudio)
    FrameLayout mLayoutPlayAudio;
    @BindView(R.id.over_layout)
    FrameLayout mOverLayout;
    @BindView(R.id.emoji_control_layout)
    RelativeLayout mEmojiControlLayout;
    @BindView(R.id.command_control_layout)
    RelativeLayout mCommandControlLayout;
    private String mLastInputText = "";
    private boolean touched;
    private boolean started;
    private boolean cancelled;
    private EmojiLayoutControl mEmojiLayoutControl;
    private CommandLayoutControl mCommandLayoutControl;
    private int mLastId = View.NO_ID;

    public ChatUIView(String account, SessionTypeEnum sessionType) {
        this.account = account;
        this.sessionType = sessionType;
    }

    /**
     * @param account     聊天对象账户
     * @param sessionType 聊天类型, 群聊, 单聊
     */
    public static void start(ILayout mLayout, String account, SessionTypeEnum sessionType) {
        mLayout.startIView(new ChatUIView(account, sessionType), new UIParam().setLaunchMode(UIParam.SINGLE_TOP));
    }

    public static MsgService msgService() {
        return NIMClient.getService(MsgService.class);
    }

    // 上滑取消录音判断
    private static boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        if (event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth()
                || event.getRawY() < location[1] - 40) {
            return true;
        }

        return false;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_chat_layout);
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();
        mBaseRootLayout.fitsSystemWindows(false);

        mChatControl = new ChatControl(mActivity, mViewHolder, this);
        mEmojiLayoutControl = new EmojiLayoutControl(mViewHolder, new EmojiLayoutControl.OnEmojiSelectListener() {
            @Override
            public void onEmojiText(String emoji) {
                final int selectionStart = mInputView.getSelectionStart();
                mInputView.getText().insert(selectionStart, emoji);
                MoonUtil.show(mActivity, mInputView, mInputView.getText().toString());
                mInputView.setSelection(selectionStart + emoji.length());
                mInputView.requestFocus();
            }
        });

        mCommandLayoutControl = new CommandLayoutControl(mActivity, mViewHolder, createCommandItems());

        initRefreshLayout();

//        mRecyclerView.setOnInterceptTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    mChatRootLayout.requestBackPressed();
//                }
//                return false;
//            }
//        });
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mChatRootLayout.requestBackPressed();
                }
                return false;
            }
        });

        mChatRootLayout.setKeyboardHeight(Hawk.get(Constant.KEYBOARD_HEIGHT, 0));
        mChatRootLayout.addOnEmojiLayoutChangeListener(new RSoftInputLayout.OnEmojiLayoutChangeListener() {
            @Override
            public void onEmojiLayoutChange(boolean isEmojiShow, boolean isKeyboardShow, int height) {
                L.w("表情:" + isEmojiShow + " 键盘:" + isKeyboardShow + " 高度:" + height);
                if (isKeyboardShow) {
                    mChatControl.scrollToEnd();
                    Hawk.put(Constant.KEYBOARD_HEIGHT, height);
                }

                if (isKeyboardShow || !isEmojiShow) {
                    mMessageAddView.setChecked(false);
                    mMessageExpressionView.setChecked(false);
                }

                if (isEmojiShow) {
                    mChatControl.scrollToEnd();
                    mCommandLayoutControl.fixHeight(height);
                    mCommandLayoutControl.init();
                }

                if (isKeyboardShow || isEmojiShow) {
                    if (mRecordView.getVisibility() == View.VISIBLE) {
                        onMessageVoiceBox(false);
                    }
                }
            }
        });

        initAudioRecordButton();
    }

    private List<CommandLayoutControl.CommandItemInfo> createCommandItems() {
        List<CommandLayoutControl.CommandItemInfo> items = new ArrayList<>();
        items.add(new CommandLayoutControl.CommandItemInfo(R.drawable.nim_message_plus_photo_normal, "图片", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送图片
                ImagePickerHelper.startImagePicker(mActivity, false, false, 9);
            }
        }));
        items.add(new CommandLayoutControl.CommandItemInfo(R.drawable.nim_message_plus_video_normal, "视频", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //视频
            }
        }));
        items.add(new CommandLayoutControl.CommandItemInfo(R.drawable.nim_message_plus_location_normal, "位置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //位置
                startIView(new AmapUIView(new Action1<AmapBean>() {
                    @Override
                    public void call(AmapBean bean) {
                        final IMMessage locationMessage = MessageBuilder.createLocationMessage(account, sessionType, bean.latitude, bean.longitude, bean.address);
                        sendMessage(locationMessage);
//                        final IMMessage message = MessageBuilder.createTextMessage(account, sessionType, "测试");
//                        sendMessage(message);
                    }
                }, null, true));
            }
        }));
        items.add(new CommandLayoutControl.CommandItemInfo(R.drawable.message_plus_audio_chat_normal, "语音通话", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //语音通话
            }
        }));
        items.add(new CommandLayoutControl.CommandItemInfo(R.drawable.message_plus_video_chat_normal, "视频通话", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //视频通话
            }
        }));
        items.add(new CommandLayoutControl.CommandItemInfo(R.drawable.message_plus_rts_normal, "白板", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //白板
            }
        }));
        items.add(new CommandLayoutControl.CommandItemInfo(R.drawable.message_plus_snapchat_normal, "阅后即焚", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //阅后即焚
            }
        }));
        items.add(new CommandLayoutControl.CommandItemInfo(R.drawable.message_plus_guess_normal, "猜拳", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //猜拳
            }
        }));
        items.add(new CommandLayoutControl.CommandItemInfo(R.drawable.message_plus_file_normal, "发送文件", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送文件
            }
        }));
        items.add(new CommandLayoutControl.CommandItemInfo(R.drawable.message_plus_tip_normal, "Tip", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tip
            }
        }));
        return items;
    }

    /**
     * ****************************** 语音 ***********************************
     */
    private void initAudioRecordButton() {
        mRecordView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touched = true;
                    initAudioRecord();
                    onStartAudioRecord();
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                        || event.getAction() == MotionEvent.ACTION_UP) {
                    touched = false;
                    onEndAudioRecord(isCancelled(v, event));
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    touched = false;
                    cancelAudioRecord(isCancelled(v, event));
                }

                return true;
            }
        });
    }

    /**
     * 取消语音录制
     *
     * @param cancel
     */
    private void cancelAudioRecord(boolean cancel) {
        // reject
        if (!started) {
            return;
        }
        // no change
        if (cancelled == cancel) {
            return;
        }

        cancelled = cancel;
        updateTimerTip(cancel);
    }

    /**
     * 开始语音录制
     */
    private void onStartAudioRecord() {
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        started = audioMessageHelper.startRecord();
        cancelled = false;
        if (!started) {
            T_.show(mActivity.getString(R.string.recording_init_failed));
            return;
        }

        if (!touched) {
            return;
        }

        mRecordView.setText(R.string.record_audio_end);

        updateTimerTip(false); // 初始化语音动画状态
        playAudioRecordAnim();
    }

    /**
     * 开始语音录制动画
     */
    private void playAudioRecordAnim() {
        mOverLayout.setVisibility(View.VISIBLE);
        mTimer.setBase(SystemClock.elapsedRealtime());
        mTimer.start();
    }

    /**
     * 结束语音录制动画
     */
    private void stopAudioRecordAnim() {
        mOverLayout.setVisibility(View.GONE);
        mTimer.stop();
        mTimer.setBase(SystemClock.elapsedRealtime());
    }

    /**
     * 结束语音录制
     *
     * @param cancel
     */
    private void onEndAudioRecord(boolean cancel) {
        mActivity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        audioMessageHelper.completeRecord(cancel);
        mRecordView.setText(R.string.record_audio);
        stopAudioRecordAnim();
    }

    /**
     * 正在进行语音录制和取消语音录制，界面展示
     *
     * @param cancel
     */
    private void updateTimerTip(boolean cancel) {
        if (cancel) {
            mTimerTip.setText(mActivity.getString(R.string.recording_cancel_tip));
            mTimerTipContainer.setBackgroundResource(R.drawable.nim_cancel_record_red_bg);
        } else {
            mTimerTip.setText(mActivity.getString(R.string.recording_cancel));
            mTimerTipContainer.setBackgroundResource(0);
        }
    }

    /**
     * 初始化AudioRecord
     */
    private void initAudioRecord() {
        if (audioMessageHelper == null) {
            audioMessageHelper = new AudioRecorder(mActivity, RecordType.AAC, AudioRecorder.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND, this);
        }
    }

    /**
     * 上拉, 下拉
     */
    private void initRefreshLayout() {
        mRefreshLayout.setBottomView(new EmptyView(mActivity));
        mRefreshLayout.addRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(@RefreshLayout.Direction int direction) {
                if (direction == RefreshLayout.TOP) {
                    //加载历史记录
                    List<IMMessage> allDatas = mChatControl.mChatAdapter.getAllDatas();
                    IMMessage lastMessage = null;
                    if (allDatas.isEmpty()) {
                        lastMessage = getEmptyMessage();
                    } else {
                        lastMessage = allDatas.get(0);
                    }
                    msgService().queryMessageListEx(lastMessage,
                            QueryDirectionEnum.QUERY_OLD, mActivity.getResources().getInteger(R.integer.message_limit)
                            , true)
                            .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                                @Override
                                public void onResult(int code, List<IMMessage> result, Throwable exception) {
                                    mRefreshLayout.setRefreshEnd();
                                    if (code == ResponseCode.RES_SUCCESS) {
                                        if (result.size() == 0) {
                                            mRefreshLayout.setRefreshDirection(RefreshLayout.BOTTOM);
                                        } else {
                                            mRefreshLayout.setRefreshDirection(RefreshLayout.BOTH);
                                            mChatControl.mChatAdapter.getAllDatas().addAll(0, result);
                                            mChatControl.mChatAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                } else {
                    //显示键盘
                    mRefreshLayout.setRefreshEnd();
                    if (mInputView.getVisibility() == View.VISIBLE) {
                        mChatRootLayout.showSoftInput(mInputView);
                    }
                }
            }
        });
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(NimUserInfoCache.getInstance().getUserDisplayName(account))
                .setShowBackImageView(true);
    }

    /**
     * 切换语音输入
     */
    @OnCheckedChanged(R.id.message_voice_box)
    public void onMessageVoiceBox(boolean isChecked) {
        mRecordView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        if (isChecked) {
            mLastInputText = mInputView.getText().toString();
            mInputView.setText("");
            mChatRootLayout.requestBackPressed();
        } else {
            mInputView.setText(mLastInputText);
            MoonUtil.show(mActivity, mInputView, mLastInputText);
            mInputView.setSelection(mLastInputText.length());
        }
    }

    public boolean isRecording() {
        return audioMessageHelper != null && audioMessageHelper.isRecording();
    }

    @Override
    public boolean onBackPressed() {
        return mChatRootLayout.requestBackPressed();
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        mChatControl.onLoad();
        msgService().queryMessageListEx(
                getEmptyMessage(),
                QueryDirectionEnum.QUERY_OLD, mActivity.getResources().getInteger(R.integer.message_limit)
                , true)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            mChatControl.resetData(result);
                        }
                    }
                });
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        mChatControl.onUnload();
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        msgService().setChattingAccount(account, sessionType);
        if (mChatControl != null) {
            mChatControl.mChatAdapter.notifyDataSetChanged();
        }
    }

    @NonNull
    private IMMessage getEmptyMessage() {
        return MessageBuilder.createEmptyMessage(account, sessionType, System.currentTimeMillis());
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        msgService().setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, sessionType);
    }

    /**
     * 表情功能切换
     */
    @OnClick({R.id.message_expression_view, R.id.message_add_view})
    public void onMessageClick(CompoundButton view) {
        if (isRecording()) {
            view.setChecked(false);
            return;
        }

        switch (view.getId()) {
            case R.id.message_expression_view:

                if (!mChatRootLayout.isEmojiShow()) {
                    mChatRootLayout.showEmojiLayout();
                } else if (mLastId == R.id.message_expression_view && mChatRootLayout.isEmojiShow()) {
                    mChatRootLayout.hideEmojiLayout();
                    return;
                }
                mLastId = R.id.message_expression_view;

                mCommandControlLayout.setVisibility(View.GONE);
                mEmojiControlLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.message_add_view:
                if (!mChatRootLayout.isEmojiShow()) {
                    mChatRootLayout.showEmojiLayout();
                } else if (mLastId == R.id.message_add_view && mChatRootLayout.isEmojiShow()) {
                    mChatRootLayout.hideEmojiLayout();
                    return;
                }
                mLastId = R.id.message_add_view;

                mCommandControlLayout.setVisibility(View.VISIBLE);
                mEmojiControlLayout.setVisibility(View.GONE);
                mCommandLayoutControl.requestLayout();
                break;
        }
    }

    /**
     * 输入框文本变化
     */
    @OnTextChanged(R.id.input_view)
    public void onInputTextChanged(Editable editable) {
        mSendView.setVisibility(TextUtils.isEmpty(editable) ? View.GONE : View.VISIBLE);
    }

    /**
     * 发送消息
     */
    @OnClick(R.id.send_view)
    public void onSendClick() {
        final String string = mInputView.getText().toString();
        if (TextUtils.isEmpty(string)) {
            return;
        }
        IMMessage imMessage = createTextMessage(string);
        sendMessage(imMessage);
        mInputView.setText("");
    }

    private void sendMessage(IMMessage message) {
        msgService().sendMessage(message, false);
        mChatControl.addData(message);
    }

    @NonNull
    private IMMessage createTextMessage(String text) {
        return MessageBuilder.createTextMessage(account, sessionType, text);
    }

    @Override
    public void onRecordReady() {

    }

    @Override
    public void onRecordStart(File audioFile, RecordType recordType) {

    }

    @Override
    public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {
        IMMessage audioMessage = MessageBuilder.createAudioMessage(account, sessionType, audioFile, audioLength);
        sendMessage(audioMessage);
    }

    @Override
    public void onRecordFail() {

    }

    @Override
    public void onRecordCancel() {

    }

    @Override
    public void onRecordReachedMaxTime(final int maxTime) {
        stopAudioRecordAnim();

        startIView(UIDialog.build()
                .setDialogContent(mActivity.getString(R.string.recording_max_time))
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        audioMessageHelper.handleEndRecord(true, maxTime);
                    }
                })
                .setGravity(Gravity.CENTER));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ArrayList<String> images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data);
        final boolean origin = ImagePickerHelper.isOrigin(requestCode, resultCode, data);
        if (images.isEmpty()) {
            return;
        }
        HnLoading.show(mILayout);
        Luban.luban(mActivity, images)
                .subscribe(new Action1<ArrayList<String>>() {
                    @Override
                    public void call(ArrayList<String> strings) {
                        final IMMessage imageMessage =
                                MessageBuilder.createImageMessage(account, sessionType, new File(strings.get(0)));
                        sendMessage(imageMessage);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HnLoading.hide();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        HnLoading.hide();
                    }
                });
    }

    class EmptyView extends View {

        public EmptyView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(1, (int) ResUtil.dpToPx(mActivity, 30));
        }
    }
}
