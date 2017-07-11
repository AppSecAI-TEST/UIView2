package com.hn.d.valley.main.message;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.event.LastMessageEvent;
import com.hn.d.valley.main.message.audio.MessageAudioControl;
import com.hn.d.valley.utils.RBus;
import com.hn.d.valley.widget.HnRefreshLayout;
import com.lzy.imagepicker.bean.ImageItem;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/29 11:31
 * 修改人员：Robi
 * 修改时间：2016/12/29 11:31
 * 修改备注：
 * Version: 1.0.0
 */
@Deprecated
public class ChatControl {


    public RRecyclerView mRecyclerView;
    HnRefreshLayout mRefreshLayout;
    UIBaseView mUIBaseView;

    public RBaseViewHolder mViewHolder;

    String mSessionId = "";

    Context mContext;

    public ChatAdapter mChatAdapter;


    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
            for (IMMessage message : messages) {
                if (TextUtils.equals(mSessionId, message.getSessionId())) {
                    mChatAdapter.appendData(messages);
                    mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount());
                } else {
                    RBus.post(new LastMessageEvent(message));
                }
            }
        }
    };
    Observer<IMMessage> mMessageObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage imMessage) {
            //消息状态发生了改变
            List<IMMessage> allDatas = mChatAdapter.getAllDatas();
            for (int i = 0; i < allDatas.size(); i++) {
                if (allDatas.get(i).isTheSame(imMessage)) {
                    mChatAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    };

    public ChatControl(Context context, RBaseViewHolder viewHolder, UIBaseView uiBaseView) {
        mContext = context;
        mViewHolder = viewHolder;
        mUIBaseView = uiBaseView;
        mRecyclerView = mViewHolder.v(R.id.recycler_view);
        mRefreshLayout = mViewHolder.v(R.id.refresh_layout);
        mChatAdapter = new ChatAdapter(context, null,viewHolder,uiBaseView);
        mRecyclerView.setItemAnim(false);
        mRecyclerView.setAdapter(mChatAdapter);
    }



    public void onLoad(String sessionId) {
        this.mSessionId = sessionId;
        onUnload();
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
        NIMClient.getService(MsgServiceObserve.class)
                .observeMsgStatus(mMessageObserver, true);
    }

    public void onUnload() {
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
        MessageAudioControl.getInstance(mContext).stopAudio();
    }

    public void resetData(List<IMMessage> messages) {
        mChatAdapter.resetData(messages);
        scrollToEnd();
    }

    public void addData(IMMessage message) {
        mChatAdapter.addLastItem(message);
        scrollToEnd();
    }

    public void scrollToEnd() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);
        } else {
            mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
        }
    }



    public static class Images {
        public int positon;
        public ArrayList<ImageItem> images;

    }


}
