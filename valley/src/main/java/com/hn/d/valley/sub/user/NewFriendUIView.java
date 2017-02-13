package com.hn.d.valley.sub.user;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.OverLayCardLayoutManager;
import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.RenRenCallback;
import com.angcyo.uiview.resources.ResUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.MsgCache;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.nim.CustomAttachment;
import com.hn.d.valley.nim.CustomBean;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.widget.HnGenderView;
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
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：新朋友
 * 创建人员：Robi
 * 创建时间：2017/01/13 12:05
 * 修改人员：Robi
 * 修改时间：2017/01/13 12:05
 * 修改备注：
 * Version: 1.0.0
 */
public final class NewFriendUIView extends BaseContentUIView {

    String mSessionId;
    SessionTypeEnum mSessionTypeEnum;
    @BindView(R.id.recycler_view)
    RRecyclerView mRecyclerView;
    @BindView(R.id.control_layout)
    LinearLayout mControlLayout;
    private FriendAdapter mFriendAdapter;
    private RenRenCallback mRenRenCallback;

    public NewFriendUIView(String sessionId, SessionTypeEnum sessionTypeEnum) {
        mSessionId = sessionId;
        mSessionTypeEnum = sessionTypeEnum;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_new_friend);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mRecyclerView.setItemAnim(false);
        mRecyclerView.setLayoutManager(new OverLayCardLayoutManager(mActivity)
                .setTopOffset((int) ResUtil.dpToPx(mActivity, 20)));
        mFriendAdapter = new FriendAdapter(mActivity);
        mRecyclerView.setAdapter(mFriendAdapter);

        mRenRenCallback = new RenRenCallback();
        new ItemTouchHelper(mRenRenCallback).attachToRecyclerView(mRecyclerView);

        mRenRenCallback.setSwipeListener(new RenRenCallback.OnSwipeListener() {
            @Override
            public void onSwiped(int adapterPosition, int direction) {
                List<IMMessage> allDatas = mFriendAdapter.getAllDatas();
                if (direction == ItemTouchHelper.DOWN || direction == ItemTouchHelper.UP) {
                    allDatas.add(0, allDatas.remove(adapterPosition));
                    mFriendAdapter.notifyDataSetChanged();
                } else {
                    final IMMessage remove = allDatas.remove(adapterPosition);
                    CustomBean bean = getBean(remove);
                    if (direction == ItemTouchHelper.LEFT) {
                        MsgCache.instance().deleteMessage(remove);
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        add(RRetrofit.create(UserInfoService.class)
                                .attention(Param.buildMap("uid:" + UserCache.getUserAccount(),
                                        "to_uid:" + bean.getUid()))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onEnd() {
                                        super.onEnd();
                                        MsgCache.instance().deleteMessage(remove);
                                    }
                                }));
                    }
                }
                mFriendAdapter.notifyDataSetChanged();
                if (mFriendAdapter.getItemCount() == 0) {
                    showEmptyLayout();
                }
            }

            @Override
            public void onSwipeTo(RecyclerView.ViewHolder viewHolder, float offset) {
                if (offset < -50) {
                    viewHolder.itemView.findViewById(R.id.like).setVisibility(View.INVISIBLE);
                    viewHolder.itemView.findViewById(R.id.not_like).setVisibility(View.VISIBLE);
                } else if (offset > 50) {
                    viewHolder.itemView.findViewById(R.id.like).setVisibility(View.VISIBLE);
                    viewHolder.itemView.findViewById(R.id.not_like).setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.itemView.findViewById(R.id.like).setVisibility(View.INVISIBLE);
                    viewHolder.itemView.findViewById(R.id.not_like).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        getAllMessage();
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setShowBackImageView(true).setTitleString(mActivity.getString(R.string.new_frient_text));
    }

    private void getAllMessage() {
        NIMClient.getService(MsgService.class).queryMessageListEx(
                getEmptyMessage(),
                QueryDirectionEnum.QUERY_OLD, mActivity.getResources().getInteger(R.integer.message_limit)
                , true)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
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
                                    if (attachment instanceof CustomAttachment) {
                                        beans.add(message);

                                        try {
                                            users.add(((CustomAttachment) attachment).getBean().getUid());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            mFriendAdapter.resetData(beans);

                            updateUserInfo(users);
                        } else {
                            showEmptyLayout();
                        }
                    }
                });
    }

    private void updateUserInfo(List<String> users) {
        NimUserInfoCache.getInstance().fetchUserInfoFromRemote(users);
    }


    @NonNull
    private IMMessage getEmptyMessage() {
        return MessageBuilder.createEmptyMessage(mSessionId, mSessionTypeEnum, System.currentTimeMillis());
    }

    @OnClick({R.id.disagree_view, R.id.attention_view})
    public void onControlClick(View view) {
        switch (view.getId()) {
            case R.id.disagree_view:
                //不喜欢
                mRenRenCallback.toLeft(mRecyclerView);
                break;
            case R.id.attention_view:
                //喜欢
                mRenRenCallback.toRight(mRecyclerView);
                break;
        }
    }

    private CustomBean getBean(IMMessage bean) {
        CustomAttachment attachment = (CustomAttachment) bean.getAttachment();
        CustomBean customBean = attachment.getBean();
        return customBean;
    }

    public class FriendAdapter extends RBaseAdapter<IMMessage> {

        public FriendAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_new_friend;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, IMMessage bean) {
            CustomBean customBean = getBean(bean);
            holder.fillView(customBean);
            holder.v(R.id.auth).setVisibility("1".equalsIgnoreCase(customBean.getIs_auth()) ? View.VISIBLE : View.GONE);
            HnGenderView genderView = holder.v(R.id.grade);
            genderView.setGender(customBean.getSex(), customBean.getGrade());
        }
    }
}
