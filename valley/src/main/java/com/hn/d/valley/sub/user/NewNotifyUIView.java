package com.hn.d.valley.sub.user;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.rsen.PlaceholderView;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.cache.MsgCache;
import com.hn.d.valley.cache.RecentContactsCache;
import com.hn.d.valley.control.MediaTypeControl;
import com.hn.d.valley.control.UnreadMessageControl;
import com.hn.d.valley.nim.CustomBean;
import com.hn.d.valley.nim.NoticeAttachment;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：动态通知界面
 * 创建人员：Robi
 * 创建时间：2017/01/13 12:05
 * 修改人员：Robi
 * 修改时间：2017/01/13 12:05
 * 修改备注：
 * Version: 1.0.0
 */
public final class NewNotifyUIView extends SingleRecyclerUIView<IMMessage> {

    String mSessionId;
    SessionTypeEnum mSessionTypeEnum;

    public NewNotifyUIView(String sessionId, SessionTypeEnum sessionTypeEnum) {
        mSessionId = sessionId;
        mSessionTypeEnum = sessionTypeEnum;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        getAllMessage();
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mRefreshLayout.setRefreshDirection(RefreshLayout.TOP);
        mRefreshLayout.setTopView(new PlaceholderView(mActivity));
        mRefreshLayout.setNotifyListener(false);
        mRExBaseAdapter.setEnableLoadMore(false);
    }

    @Override
    protected RecyclerView.ItemDecoration initItemDecoration() {
        RBaseItemDecoration itemDecoration = super.createBaseItemDecoration();
        itemDecoration.setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi));
        return itemDecoration;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setShowBackImageView(true)
                .setTitleString(mActivity.getString(R.string.dynamic_notify))
                .addRightItem(TitleBarPattern.TitleBarItem.build(mActivity.getString(R.string.clear_all),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UnreadMessageControl.removeMessageUnread(Constant.comment);
                                MsgCache.notifyNoreadNum(RecentContactsCache.instance().getTotalUnreadCount()
                                        + UnreadMessageControl.getUnreadCount());
                                onUILoadDataEnd();
                                NIMClient.getService(MsgService.class)
                                        .clearChattingHistory(Constant.comment, SessionTypeEnum.P2P);
                            }
                        }));
    }

    @Override
    protected RExBaseAdapter<String, IMMessage, String> initRExBaseAdapter() {
        return new NotifyAdapter(mActivity);
    }

    private void getAllMessage() {
        showLoadView();
        NIMClient.getService(MsgService.class).queryMessageListEx(
                getEmptyMessage(),
                QueryDirectionEnum.QUERY_OLD, mActivity.getResources().getInteger(R.integer.message_limit)
                , true)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, final List<IMMessage> result, Throwable exception) {
                        hideLoadView();
                        if (code == ResponseCode.RES_SUCCESS && result != null) {
                            //T_.show("消息条数:" + result.size());
                            if (result.size() == 0) {
                                showEmptyLayout();
                                return;
                            }
                            showContentLayout();
                            List<IMMessage> beans = new ArrayList<>();
                            List<String> users = new ArrayList<>();
                            for (IMMessage message : result) {
                                if (message.getMsgType() == MsgTypeEnum.custom) {
                                    MsgAttachment attachment = message.getAttachment();
                                    if (attachment instanceof NoticeAttachment) {
                                        beans.add(message);
                                        try {
                                            users.add(((NoticeAttachment) attachment).getBean().getUid());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                            Collections.reverse(beans);

                            mRExBaseAdapter.resetData(beans);
                        } else {
                            showEmptyLayout();
                        }
                    }
                });
    }

    @NonNull
    private IMMessage getEmptyMessage() {
        return MessageBuilder.createEmptyMessage(mSessionId, mSessionTypeEnum, System.currentTimeMillis());
    }

    private CustomBean getBean(IMMessage bean) {
        NoticeAttachment attachment = (NoticeAttachment) bean.getAttachment();
        CustomBean customBean = attachment.getBean();
        return customBean;
    }

    public class NotifyAdapter extends RExBaseAdapter<String, IMMessage, String> {

        public NotifyAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_new_notify;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, IMMessage dataBean) {
            super.onBindDataView(holder, posInData, dataBean);
            final CustomBean customBean = getBean(dataBean);
            holder.fillView(customBean);
            SimpleDraweeView mediaImageView = holder.v(R.id.media_image_view);
            MediaTypeControl.initMedia(customBean.getMedia(),
                    customBean.getMedia_type(), mediaImageView, null);
            View contentView = holder.v(R.id.content);
            contentView.setVisibility(View.GONE);
            if ("1".equalsIgnoreCase(customBean.getMedia_type())) {
                mediaImageView.setVisibility(View.GONE);
                contentView.setVisibility(View.VISIBLE);
            }
            holder.v(R.id.user_info_root_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new DynamicDetailUIView2(customBean.getItem_id()));
                }
            });
        }

    }
}
