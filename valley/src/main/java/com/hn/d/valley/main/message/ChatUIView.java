package com.hn.d.valley.main.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIContentView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
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
        mGroupView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mChatRootLayout.showEmojiLayout();
            }
        });
        mChatControl = new ChatControl(mActivity, mViewHolder);
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
                MessageBuilder.createEmptyMessage(account, sessionType, System.currentTimeMillis()),
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
    public void onViewHide() {
        super.onViewHide();
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE,
                SessionTypeEnum.None);
    }
}
