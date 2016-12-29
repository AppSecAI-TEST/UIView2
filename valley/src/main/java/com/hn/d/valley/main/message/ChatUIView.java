package com.hn.d.valley.main.message;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIContentView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.widget.HnRefreshLayout;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;

import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

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
public class ChatUIView extends UIContentView {

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

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_chat_layout);
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();
        mChatControl = new ChatControl(mActivity, mViewHolder);
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
                    NIMClient.getService(MsgService.class).queryMessageListEx(lastMessage,
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

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mChatRootLayout.requestBackPressed();
                }
                return false;
            }
        });

        mChatRootLayout.addOnEmojiLayoutChangeListener(new RSoftInputLayout.OnEmojiLayoutChangeListener() {
            @Override
            public void onEmojiLayoutChange(boolean isEmojiShow, boolean isKeyboardShow, int height) {
                if (isKeyboardShow) {
                    mChatControl.scrollToEnd();
                    mMessageAddView.setChecked(false);
                    mMessageExpressionView.setChecked(false);
                }
                if (isEmojiShow) {
                    mChatControl.scrollToEnd();
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
        mInputView.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        mRecordView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        if (isChecked) {
            hideSoftInput();
        }
    }

    @Override
    public boolean onBackPressed() {
        return mChatRootLayout.requestBackPressed();
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        mChatControl.onLoad();
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        mChatControl.onUnload();
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        NIMClient.getService(MsgService.class).setChattingAccount(account, sessionType);
        NIMClient.getService(MsgService.class).queryMessageListEx(
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

    @NonNull
    private IMMessage getEmptyMessage() {
        return MessageBuilder.createEmptyMessage(account, sessionType, System.currentTimeMillis());
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE,
                SessionTypeEnum.None);
    }

    @OnClick({R.id.message_expression_view, R.id.message_add_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.message_expression_view:
                if (!mChatRootLayout.isEmojiShow()) {
                    mChatRootLayout.showEmojiLayout();
                }
                break;
            case R.id.message_add_view:
                if (!mChatRootLayout.isEmojiShow()) {
                    mChatRootLayout.showEmojiLayout();
                }
                break;
        }
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
