package com.hn.d.valley.main.message;

import android.content.Context;
import android.view.View;

import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.utils.TimeUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.widget.HnRefreshLayout;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

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
public class ChatControl {

    RBaseViewHolder mViewHolder;
    ChatAdapter mChatAdapter;
    RRecyclerView mRecyclerView;
    HnRefreshLayout mRefreshLayout;
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
            mChatAdapter.appendData(messages);
            mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount());
        }
    };


    public ChatControl(Context context, RBaseViewHolder viewHolder) {
        mViewHolder = viewHolder;
        mRecyclerView = mViewHolder.v(R.id.recycler_view);
        mRefreshLayout = mViewHolder.v(R.id.refresh_layout);
        mChatAdapter = new ChatAdapter(context, null);
        mRecyclerView.setItemAnim(false);
        mRecyclerView.setAdapter(mChatAdapter);
    }

    public void onLoad() {
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
    }

    public void onUnload() {
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
    }

    public void resetData(List<IMMessage> messages) {
        mChatAdapter.resetData(messages);
    }

    class ChatAdapter extends RBaseAdapter<IMMessage> {


        public ChatAdapter(Context context, List<IMMessage> datas) {
            super(context, datas);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_chat_msg_layout;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, IMMessage bean) {
            NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();

            String avatar = "";
            View itemRootLayout = holder.v(R.id.item_root_layout);
            if (bean.getDirect() == MsgDirectionEnum.In) {
                //收到的消息
                itemRootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                itemRootLayout.setBackgroundResource(R.drawable.bubble_box_left_selector);
                if (userInfoCache != null) {
                    avatar = userInfoCache.getUserInfo(bean.getFromAccount()).getAvatar();
                }
            } else {
                //发出去的消息
                itemRootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                itemRootLayout.setBackgroundResource(R.drawable.bubble_box_right_selector);
                avatar = UserCache.instance().getAvatar();
            }

            //头像
            DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.msg_ico_view), avatar);

            //消息内容
            MoonUtil.show(mContext, holder.tv(R.id.msg_text_view), bean.getContent());

            //时间
            String timeString = TimeUtil.getTimeShowString(bean.getTime(), false);
            holder.tv(R.id.msg_time_view).setText(timeString);

        }


    }
}
