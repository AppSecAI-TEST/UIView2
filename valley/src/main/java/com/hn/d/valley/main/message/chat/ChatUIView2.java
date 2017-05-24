package com.hn.d.valley.main.message.chat;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.rsen.PlaceholderView;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.file.FileUtil;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.event.LastMessageEvent;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.control.UnreadMessageControl;
import com.hn.d.valley.emoji.IEmoticonSelectedListener;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.message.session.CommandLayoutControl;
import com.hn.d.valley.main.message.session.Container;
import com.hn.d.valley.main.message.session.EmojiLayoutControl;
import com.hn.d.valley.main.message.session.CommandItemInfo;
import com.hn.d.valley.main.message.session.ImageCommandItem;
import com.hn.d.valley.main.message.session.SessionCustomization;
import com.hn.d.valley.main.message.attachment.CustomExpressionAttachment;
import com.hn.d.valley.main.message.attachment.CustomExpressionMsg;
import com.hn.d.valley.main.message.session.RecentContactsControl;
import com.hn.d.valley.main.message.session.SessionProxy;
import com.hn.d.valley.main.message.session.VideoCommandItem;
import com.hn.d.valley.skin.SkinUtils;
import com.hn.d.valley.widget.HnRefreshLayout;
import com.hwangjr.rxbus.annotation.Subscribe;
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
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by hewking on 2017/3/16.
 */
public class ChatUIView2 extends BaseContentUIView implements IAudioRecordCallback ,SessionProxy{

    protected static final String KEY_SESSION_ID = "key_account";
    protected static final String KEY_SESSION_TYPE = "key_sessiontype";
    protected static final String KEY_SESSION_CUSTOMIZATION = "key_session_customization";

    protected static final String KEY_ANCHOR = "anchor";
    protected static final String KEY_AITMESSAGES = "aitMessages";

    @BindView(R.id.input_view)
    protected ExEditText mInputView;
    protected ChatControl2 mChatControl;
    protected SessionTypeEnum sessionType;
    protected AudioRecorder audioMessageHelper;
    protected String mSessionId;
    protected IMMessage mAnchor;
    protected SessionCustomization mCustomization;

    @BindView(R.id.group_view)
    RadioGroup mGroupView;
    @BindView(R.id.chat_root_layout)
    RSoftInputLayout mChatRootLayout;
    @BindView(R.id.record_view)
    TextView mRecordView;
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
    @BindView(R.id.message_voice_box)
    CheckBox mMessageVoiceBox;
    @BindView(R.id.ll_warp_add_view)
    LinearLayout wrap_add_view;

    QueryDirectionEnum direction;
    boolean firstLoad = true;
    private String mLastInputText = "";
    private boolean touched;
    private boolean started;
    private boolean cancelled;
    private EmojiLayoutControl mEmojiLayoutControl;
    private CommandLayoutControl mCommandLayoutControl;
    private int mLastId = View.NO_ID;

    private RequestCallbackWrapper<List<IMMessage>> requestCallback = new RequestCallbackWrapper<List<IMMessage>>() {
        @Override
        public void onResult(int code, List<IMMessage> result, Throwable exception) {
            if (code == ResponseCode.RES_SUCCESS) {
                mChatControl.resetData(result);
            }

            if (code != ResponseCode.RES_SUCCESS || exception != null) {
                if (direction == QueryDirectionEnum.QUERY_OLD) {
                    mChatControl.mChatAdapter.fetchMoreFailed();
                } else if (direction == QueryDirectionEnum.QUERY_NEW) {
                    mChatControl.mChatAdapter.loadMoreFail();
                }

                return;
            }

            if (result != null) {
                onMessageLoaded(result);
            }
        }
    };


    public ChatUIView2() {
    }

    /**
     * @param sessionId   聊天对象账户
     * @param sessionType 聊天类型, 群聊, 单聊
     */
    public static void start(ILayout mLayout, String sessionId, SessionTypeEnum sessionType, SessionCustomization customization) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SESSION_ID, sessionId);
        bundle.putInt(KEY_SESSION_TYPE, sessionType.getValue());
        bundle.putSerializable(KEY_SESSION_CUSTOMIZATION,customization);
        mLayout.startIView(new ChatUIView2(), new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
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
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mBaseRootLayout.fitsSystemWindows(false);

        mChatControl = new ChatControl2(mActivity, mViewHolder, this);

        if (mCustomization != null) {
            if (!mCustomization.isShowInputPanel()) {
                RelativeLayout emoji_root_layout = mViewHolder.v(R.id.emoji_root_layout);
                LinearLayout input_control_layout = mViewHolder.v(R.id.input_control_layout);
                emoji_root_layout.setVisibility(View.GONE);
                input_control_layout.setVisibility(View.GONE);
            }
        }

        mEmojiLayoutControl = new EmojiLayoutControl(mViewHolder, new IEmoticonSelectedListener() {
            @Override
            public void onEmojiSelected(String emoji) {
                if (emoji.equals("/DEL")) {
                    mInputView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                } else {
                    final int selectionStart = mInputView.getSelectionStart();
                    mInputView.getText().insert(selectionStart, emoji);
                    MoonUtil.show(mActivity, mInputView, mInputView.getText().toString());
                    mInputView.setSelection(selectionStart + emoji.length());
                    mInputView.requestFocus();
                }
            }

            @Override
            public void onStickerSelected(String categoryName, String stickerName) {
//                T_.show(categoryName + ": " + stickerName);
                CustomExpressionMsg expressionMsg = new CustomExpressionMsg(FileUtil.getFileNameNoEx(stickerName));
                CustomExpressionAttachment attachment = new CustomExpressionAttachment(expressionMsg);
                IMMessage message = MessageBuilder.createCustomMessage(mSessionId, sessionType, "贴图表情",attachment );
                sendMessage(message);
            }
        });

        Container container = new Container(mActivity,mSessionId,sessionType, mParentILayout,this);
        mCommandLayoutControl = new CommandLayoutControl(container, mViewHolder, createCommandItems());

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
                    SkinUtils.setExpressView(mMessageExpressionView);
                }

                if (isEmojiShow) {
                    mChatControl.scrollToEnd();
                    //mCommandLayoutControl.fixHeight(height);
                    mCommandLayoutControl.init();
//                    mRecyclerView.requestLayout();
//                    mRecyclerView.getLayoutManager().requestLayout();
                }

                if (isKeyboardShow || isEmojiShow) {
                    if (mRecordView.getVisibility() == View.VISIBLE) {
                        //onMessageVoiceBox(false);
                        mMessageVoiceBox.setChecked(false);
                    }
                }
            }
        });

        initAudioRecordButton();

        updateSkin();//更新皮肤资源
    }

    protected List<CommandItemInfo> createCommandItems() {
        List<CommandItemInfo> items = new ArrayList<>();

        items.add(new ImageCommandItem());
        items.add(new VideoCommandItem());

        if (mCustomization != null) {
            items.addAll(mCustomization.createItems());
        }

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

//        started = audioMessageHelper.startRecord();
        // nimsdk 3.7.0 无返回值
        audioMessageHelper.startRecord();
        cancelled = false;
//        if (!started) {
//            T_.show(mActivity.getString(R.string.recording_init_failed));
//            return;
//        }

        if (!touched) {
            return;
        }

        mRecordView.setText(R.string.record_audio_end);
        mRecordView.setBackgroundResource(R.drawable.shape_round_dark_color);

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
        mRecordView.setBackgroundResource(R.drawable.shape_round_line_color);
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
        mRefreshLayout.setBottomView(new PlaceholderView(mActivity));
        // 不显示键盘
        if (!mCustomization.isShowInputPanel()) {
            return;
        }
        mRefreshLayout.addOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(@RefreshLayout.Direction int direction) {
                if (direction == RefreshLayout.TOP) {

                    onRefreshFetch();

                } else {
                    //显示键盘
                    mRefreshLayout.setRefreshEnd(false);
                    if (mInputView.getVisibility() == View.VISIBLE) {
                        mChatRootLayout.showSoftInput(mInputView);
                    }
                }
            }
        });
    }

    private void onRefreshFetch() {
        //加载历史记录
        List<IMMessage> allDatas = mChatControl.mChatAdapter.getAllDatas();
        IMMessage lastMessage = null;
        if (allDatas.isEmpty()) {
            lastMessage = getEmptyMessage();
        } else {
            lastMessage = allDatas.get(0);
        }

        fetchAnchorAndScrollTo(lastMessage);
    }

    protected void fetchAnchorAndScrollTo(IMMessage lastMessage) {
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
//                                mChatControl.mChatAdapter.getAllDatas().addAll(0, result);
//                                mChatControl.mChatAdapter.notifyDataSetChanged();
                                mChatControl.mChatAdapter.fetchMoreComplete(mRecyclerView, result);
                            }
                        }
                    }
                });
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString("")
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
            SkinUtils.setKeyboardView(mMessageVoiceBox);

        } else {
            mInputView.setText(mLastInputText);
            MoonUtil.show(mActivity, mInputView, mLastInputText);
            mInputView.setSelection(mLastInputText.length());
            SkinUtils.setVoiceView(mMessageVoiceBox);
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
        mChatControl.onLoad(mSessionId);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        mChatControl.onUnload();
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);

    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);

        if (param.mBundle != null) {
            parseBundle(param.mBundle);

        }
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);

        if (bundle != null) {
            final String lastId = mSessionId;

            setTitleString(NimUserInfoCache.getInstance().getUserDisplayName(mSessionId));

//            NimUserInfoCache.getInstance().getUserInfoFromRemote(mSessionId, new com.netease.nimlib.sdk.RequestCallback<NimUserInfo>() {
//                @Override
//                public void onSuccess(NimUserInfo param) {
//                    setTitleString(param.getName());
//                }
//
//                @Override
//                public void onFailed(int code) {
//
//                }
//
//                @Override
//                public void onException(Throwable exception) {
//
//                }
//            });

            loadFirst(lastId);
        }

        UnreadMessageControl.removeMessageUnread(mSessionId);

        msgService().setChattingAccount(mSessionId, sessionType);
        mActivity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL); // 默认使用听筒播放

    }

    private void loadFirst(String lastId) {
        if (TextUtils.equals(lastId, mSessionId)) {

            if (mAnchor == null) {
                loadFromLocal();
            } else {
                loadFromAnchor();
            }


        }
        if (mChatControl != null) {
            mChatControl.mChatAdapter.notifyDataSetChanged();
        }
    }

    private void loadFromAnchor() {
        this.direction = QueryDirectionEnum.QUERY_NEW;
        msgService().queryMessageListEx(
                mAnchor,
                direction, mActivity.getResources().getInteger(R.integer.message_limit)
                , true)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        if (code != ResponseCode.RES_SUCCESS || exception != null) {
                            return;
                        }

                        onAnchorContextMessageLoaded(result);
                    }
                });

    }

    private void onAnchorContextMessageLoaded(List<IMMessage> result) {

        if (result == null) {
            return;
        }

        if (firstLoad && mAnchor != null) {
            result.add(0, mAnchor);
        }

        mChatControl.mChatAdapter.resetData(result);

        firstLoad = false;

    }

    private void loadFromLocal() {
        this.direction = QueryDirectionEnum.QUERY_OLD;
        msgService().queryMessageListEx(
                anchor(),
                direction, mActivity.getResources().getInteger(R.integer.message_limit)
                , true)
                .setCallback(requestCallback);
    }

    private void onMessageLoaded(List<IMMessage> result) {

    }

    protected void parseBundle(Bundle bundle) {
        mSessionId = bundle.getString(KEY_SESSION_ID);
        sessionType = SessionTypeEnum.typeOfValue(bundle.getInt(KEY_SESSION_TYPE));
        mAnchor = (IMMessage) bundle.getSerializable(KEY_ANCHOR);
        mCustomization = (SessionCustomization) bundle.getSerializable(KEY_SESSION_CUSTOMIZATION);
    }

    @NonNull
    protected IMMessage getEmptyMessage() {
        return MessageBuilder.createEmptyMessage(mSessionId, sessionType, System.currentTimeMillis());
    }

    private IMMessage anchor() {
        if (mChatControl.mChatAdapter.getAllDatas().size() == 0) {
            return mAnchor == null ? getEmptyMessage() : mAnchor;
        } else {
            return mChatControl.mChatAdapter.getAllDatas().get(0);
        }
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
                    SkinUtils.setKeyboardView(mMessageExpressionView);
                } else if (mLastId == R.id.message_expression_view && mChatRootLayout.isEmojiShow()) {
                    mChatRootLayout.hideEmojiLayout();
                    SkinUtils.setExpressView(mMessageExpressionView);
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
        boolean isEmpty = TextUtils.isEmpty(editable);
        mSendView.setEnabled(!isEmpty);
        if (!isEmpty) {
            mSendView.setVisibility(View.VISIBLE);
            mMessageAddView.setVisibility(View.GONE);
            wrap_add_view.setVisibility(View.GONE);
        } else {
            mMessageAddView.setVisibility(View.VISIBLE);
            mSendView.setVisibility(View.GONE);
            wrap_add_view.setVisibility(View.VISIBLE);
        }
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

    public boolean sendMessage(IMMessage message){
        msgService().sendMessage(message, false);
        mChatControl.addData(message);
        return true;
    }

    @NonNull
    protected IMMessage createTextMessage(String text) {
        return MessageBuilder.createTextMessage(mSessionId, sessionType, text);
    }

    @Override
    public void onRecordReady() {

    }

    @Override
    public void onRecordStart(File audioFile, RecordType recordType) {

    }

    @Override
    public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {
        IMMessage audioMessage = MessageBuilder.createAudioMessage(mSessionId, sessionType, audioFile, audioLength);
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

        mCommandLayoutControl.onActivityResult(requestCode,resultCode,data);
    }


//    @Subscribe
//    public void onEvent(LastContactsEvent lastContactsEvent) {
//        if (lastContactsEvent.mRecentContact != null) {
//            final View layout = mViewHolder.v(R.id.recent_contact_layout);
//            final TextView content = mViewHolder.v(R.id.recent_recent_content_view);
//
//            final RecentContactsControl.RecentContactsInfo userInfo = RecentContactsControl.getRecentContactsInfo(lastContactsEvent.mRecentContact);
//            MoonUtil.show(mActivity, content, userInfo.name + ":" + userInfo.lastContent);
//
//            if (layout.getVisibility() == View.GONE) {
//                layout.setVisibility(View.VISIBLE);
//                layout.setTranslationY(-layout.getMeasuredHeight());
//                ViewCompat.animate(layout).translationY(0).setDuration(UIBaseView.DEFAULT_ANIM_TIME).start();
//            }
//            layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    T_.show(userInfo.lastContent);
//                }
//            });
//        }
//    }

    @Subscribe
    public void onEvent(final LastMessageEvent lastMessageEvent) {
        if (lastMessageEvent.mMessage != null) {
            final View layout = mViewHolder.v(R.id.recent_contact_layout);
            final TextView content = mViewHolder.v(R.id.recent_recent_content_view);

            final RecentContactsControl.RecentContactsInfo info = RecentContactsControl.getRecentContactsInfo(lastMessageEvent.mMessage);

            MoonUtil.show(mActivity, content, info.name + ":" + info.lastContent);

            if (layout.getVisibility() == View.GONE) {
                layout.setVisibility(View.VISIBLE);
                post(new Runnable() {
                    @Override
                    public void run() {
                        layout.setTranslationY(-layout.getMeasuredHeight());
                        ViewCompat.animate(layout).translationY(0).setDuration(UIBaseView.DEFAULT_ANIM_TIME).start();
                    }
                });
            }
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_SESSION_ID, lastMessageEvent.mMessage.getSessionId());
                    bundle.putInt(KEY_SESSION_TYPE, lastMessageEvent.mMessage.getSessionType().getValue());
                    onViewShow(bundle);

                    ViewCompat.animate(layout).translationY(-layout.getMeasuredHeight())
                            .setDuration(UIBaseView.DEFAULT_ANIM_TIME)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    layout.setVisibility(View.GONE);
                                }
                            }).start();
                }
            });
        }
    }

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);
        SkinUtils.setExpressView(mMessageExpressionView);
        SkinUtils.setAddView(mMessageAddView);
        SkinUtils.setVoiceView(mMessageVoiceBox);

        ResUtil.setBgDrawable(mSendView, ResUtil.generateRippleRoundMaskDrawable(density() * 3,
                Color.WHITE,
                SkinHelper.getSkin().getThemeDarkColor(),
                getColor(R.color.colorDisable),
                SkinHelper.getSkin().getThemeSubColor()));
    }
}
