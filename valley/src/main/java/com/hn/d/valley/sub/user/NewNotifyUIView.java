package com.hn.d.valley.sub.user;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.rsen.PlaceholderView;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.widget.RImageView;
import com.angcyo.uiview.widget.RTextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.MsgCache;
import com.hn.d.valley.cache.RecentContactsCache;
import com.hn.d.valley.control.MediaTypeControl;
import com.hn.d.valley.control.UnreadMessageControl;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.hn.d.valley.main.found.sub.InformationDetailUIView;
import com.hn.d.valley.main.message.attachment.CustomAttachmentType;
import com.hn.d.valley.main.message.attachment.DiscussRewardAttachment;
import com.hn.d.valley.main.message.attachment.DynamicMsg;
import com.hn.d.valley.main.message.attachment.DynamicMsgAttachment;
import com.hn.d.valley.main.message.attachment.GrabedMsgAttachment;
import com.hn.d.valley.main.message.attachment.LikeMsg;
import com.hn.d.valley.main.message.attachment.LikeMsgAttachment;
import com.hn.d.valley.main.message.attachment.RedPacketGrabedMsg;
import com.hn.d.valley.main.message.slide.ISlideHelper;
import com.hn.d.valley.main.message.slide.holder.OneSlideViewHolder;
import com.hn.d.valley.main.message.slide.holder.SlideViewHolder;
import com.hn.d.valley.nim.CustomBean;
import com.hn.d.valley.nim.NoticeAttachment;
import com.hn.d.valley.service.UserService;
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

    private NotifyAdapter mNotifyAdapter;

    private LinearLayout ll_bottom;
    private TextView tv_selected;
    private RTextView btn_delete;

    public NewNotifyUIView() {
        mSessionId = "14";
        mSessionTypeEnum = SessionTypeEnum.P2P;
    }

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
                .addRightItem(TitleBarPattern.TitleBarItem.build(mActivity.getString(R.string.text_edit),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editItems();
                            }
                        }).setVisibility(View.GONE));
    }

    @Override
    protected RExBaseAdapter<String, IMMessage, String> initRExBaseAdapter() {
        mNotifyAdapter = new NotifyAdapter(mActivity);
        return mNotifyAdapter;
    }

    @Override
    protected void initBaseEmptyLayout(View view) {
        super.initBaseEmptyLayout(view);
        mViewHolder.tv(R.id.base_empty_tip_view).setText("暂无动态通知哦~");
        mViewHolder.imgV(R.id.base_empty_image_view).setImageResource(R.drawable.image_wutongzhi);
    }

    @Override
    protected void inflateRecyclerRootLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        View root = inflater.inflate(R.layout.view_member_select, baseContentLayout);
        mRefreshLayout = (RefreshLayout) root.findViewById(R.id.refresh_layout);
        mRecyclerView = (RRecyclerView) root.findViewById(R.id.recycler_view);
        ll_bottom = (LinearLayout) root.findViewById(R.id.ll_bottom);
        tv_selected = (TextView) root.findViewById(R.id.tv_selected);
        btn_delete = (RTextView) root.findViewById(R.id.btn_send);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteIMMessage(mNotifyAdapter.getSelectorData());
            }
        });
    }

    private void deleteIMMessage(List<IMMessage> imMessages) {
        UnreadMessageControl.removeMessageUnread(Constant.comment);
        MsgCache.notifyNoreadNum(RecentContactsCache.instance().getTotalUnreadCount()
                + UnreadMessageControl.getUnreadCount());
        mNotifyAdapter.deleteItems(imMessages);
        if (mNotifyAdapter.getDataCount() == 0) {
            onUILoadDataEnd();
            animBottom(false);
            getUITitleBarContainer().getRightControlLayout().getChildAt(0).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onLayoutStateChanged(LayoutState fromState, LayoutState toState) {
        super.onLayoutStateChanged(fromState, toState);
        if (toState != LayoutState.CONTENT) {
            getUITitleBarContainer().hideRightItem(0);
        }
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
                                    } else if (attachment instanceof LikeMsgAttachment) {
                                        beans.add(message);
                                    } else if (attachment instanceof DynamicMsgAttachment) {
                                        beans.add(message);
                                    } else if (attachment instanceof GrabedMsgAttachment) {
                                        beans.add(message);
                                    } else if (attachment instanceof DiscussRewardAttachment) {
                                        beans.add(message);
                                    }
                                }
                            }

                            Collections.reverse(beans);

                            if (beans.isEmpty()) {
                                showEmptyLayout();
                            } else {
                                showContentLayout();
                                mRExBaseAdapter.resetData(beans);
                                getUITitleBarContainer().showRightItem(0);
                            }
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

    private void animBottom(boolean show) {
        if (ll_bottom.getVisibility() == View.GONE) {
            ll_bottom.setVisibility(View.VISIBLE);
        }

        float start = show ? ScreenUtil.dip2px(48) : 0;
        float end = show ? 0 : ScreenUtil.dip2px(48);
        ObjectAnimator animator = ObjectAnimator.ofFloat(ll_bottom, "translationY", start, end);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    private void editItems() {
        TextView selectNum = (TextView) getUITitleBarContainer().getRightControlLayout().getChildAt(0);
        if (getString(R.string.text_edit).equals(selectNum.getText().toString())) {
            selectNum.setText(getString(R.string.cancel));
            mNotifyAdapter.slideOpen();
        } else if (getString(R.string.cancel).equals(selectNum.getText().toString())) {
            selectNum.setText(getString(R.string.text_edit));
            mNotifyAdapter.slideClose();
        }
    }

    /**
     * 是否处于编辑模式
     */
    private boolean isInEditModel() {
        TextView selectNum = (TextView) getUITitleBarContainer().getRightControlLayout().getChildAt(0);
        return getString(R.string.cancel).equals(selectNum.getText().toString());
    }

    public class NotifyAdapter extends RExBaseAdapter<String, IMMessage, String> {

        private SparseBooleanArray mCheckStats = new SparseBooleanArray();
        private ISlideHelper mISlideHelper = new ISlideHelper();
        private boolean isEditable = true;

        public NotifyAdapter(Context context) {
            super(context);
            setModel(RModelAdapter.MODEL_MULTI);
        }

        public void slideOpen() {
            isEditable = true;
            mISlideHelper.slideOpen();
            tv_selected.setText(String.format(getString(R.string.text_already_selected_item), 0));
            animBottom(true);
        }

        public void slideClose() {
            tv_selected.setText(String.format(getString(R.string.text_already_selected_item), 0));
            mISlideHelper.slideClose();
            isEditable = true;
            animBottom(false);
            mCheckStats.clear();
            unSelectorAll(true);
        }

        public void deleteItems(List<IMMessage> msgs) {
            for (IMMessage msg : msgs) {
                NIMClient.getService(MsgService.class).deleteChattingHistory(msg);
                mNotifyAdapter.deleteItem(msg);
            }
            if (isInEditModel()) {
                editItems();
            }
        }

        public void clearSelected() {
            mCheckStats.clear();
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_new_notify;
        }

        @NonNull
        @Override
        protected RBaseViewHolder createBaseViewHolder(int viewType, View itemView) {
            SlideViewHolder slideViewHolder = new OneSlideViewHolder(itemView, viewType);
            mISlideHelper.add(slideViewHolder);
            return slideViewHolder;
        }

        @Override
        protected void onBindDataView(final RBaseViewHolder holder, final int posInData, final IMMessage dataBean) {
            super.onBindDataView(holder, posInData, dataBean);
            String media = "", media_type = "", item_id = "", msg = "", created = "", avatar = "", type = "";
            MsgAttachment attachment = dataBean.getAttachment();
            RImageView mediaImageView = holder.v(R.id.media_image_view);
            final SimpleDraweeView avatarView = holder.v(R.id.avatar);

            ((OneSlideViewHolder) holder).bind();

            if (attachment instanceof DynamicMsgAttachment) {
                DynamicMsg dynamicMsg = ((DynamicMsgAttachment) attachment).getDynamicMsg();
                media = dynamicMsg.getMedia();//媒体内容
                media_type = dynamicMsg.getMedia_type();//媒体类型
                item_id = dynamicMsg.getItem_id();//数据id
                created = String.valueOf(dynamicMsg.getCreated());//时间
                msg = dynamicMsg.getMsg();//显示的消息
                avatar = dynamicMsg.getAvatar();//头像
                type = dynamicMsg.getType();
            } else if (attachment instanceof LikeMsgAttachment) {
                LikeMsg likeMsg = ((LikeMsgAttachment) attachment).getLikeMsg();
                media = likeMsg.getMedia();//媒体内容
                media_type = likeMsg.getMedia_type();//媒体类型
                item_id = likeMsg.getItem_id();//数据id
                created = String.valueOf(likeMsg.getCreated());//时间
                msg = likeMsg.getMsg();//显示的消息
                avatar = likeMsg.getAvatar();//头像
                type = likeMsg.getType();
            } else if (attachment instanceof NoticeAttachment) {
                final CustomBean customBean = getBean(dataBean);
//                holder.fillView(customBean);
                media = customBean.getMedia();
                media_type = customBean.getMedia_type();
                type = customBean.getType();
                item_id = customBean.getItem_id();
                msg = customBean.getMsg();
                avatar = customBean.getAvatar();
                created = String.valueOf(customBean.getCreated());
            } else if (attachment instanceof GrabedMsgAttachment) {
                RedPacketGrabedMsg grabedMsg = ((GrabedMsgAttachment) attachment).getGrabedMsg();
                msg = grabedMsg.getMsg();
                type = grabedMsg.getExtend_type();
                int graber = grabedMsg.getGraber();

                media = "grabedmsg";
                created = String.valueOf(grabedMsg.getCreated());
                media_type = type;
                item_id = grabedMsg.getExtend().getDiscuss_id();
                fillAvatar(avatarView, graber);
            } else if (attachment instanceof DiscussRewardAttachment) {
                DiscussRewardMsg rewardmsg = ((DiscussRewardAttachment) attachment).getDiscussRewardMsg();
                media = rewardmsg.getExtend_type();
                media_type = rewardmsg.getExtend_type();
                type = rewardmsg.getExtend_type();
                item_id = rewardmsg.getDiscuss_id();
                msg = rewardmsg.getMsg();
                avatar = rewardmsg.getAvatar();
                created = String.valueOf(rewardmsg.getCreated());
            }

            MediaTypeControl.initMedia(media, media_type, mediaImageView, null);

            DraweeViewUtil.resize(avatarView, avatar);

//            TextView contentView = holder.tv(R.id.content);
//            contentView.setVisibility(View.GONE);
//            if ("1".equalsIgnoreCase(media_type)) {
//                mediaImageView.setVisibility(View.GONE);
//                contentView.setVisibility(View.VISIBLE);
//            }

            holder.tv(R.id.created).setText(created);
            holder.tv(R.id.msg).setText(msg);

            final String finalItem_id = item_id;
            final String finalType = type;
            ((OneSlideViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (isInEditModel()) {
                        return false;
                    }
                    UIBottomItemDialog.build()
                            .addItem(getString(R.string.delete_text), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    List<IMMessage> datas = new ArrayList<>();
                                    datas.add(dataBean);
                                    deleteIMMessage(datas);
                                }
                            })
                            .showDialog(mParentILayout);
                    return true;
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startIView(new DynamicDetailUIView2(customBean.getItem_id()));
                    if (isInEditModel()) {
                        final CheckBox checkBox = holder.v(R.id.cb_friend_addfirend);
                        setSelectorPosition(posInData, checkBox);
                        int tag = (int) checkBox.getTag();
                        boolean selector = isPositionSelector(posInData);
                        if (selector) {
                            mCheckStats.put(tag, true);
                        } else {
                            mCheckStats.delete(tag);
                        }
                        tv_selected.setText(String.format(getString(R.string.text_already_selected_item), mCheckStats.size()));
                    } else {
                        //资讯
                        if ("news".equals(finalType)) {
                            startIView(new InformationDetailUIView(finalItem_id));
                        } else if ("discuss".equals(finalType)) {
                            // 动态
                            UserDiscussItemControl.jumpToDynamicDetailUIView(mILayout, finalItem_id, false, false, false);
                        } else if (CustomAttachmentType.GRABREDBAG.equals(finalType) || CustomAttachmentType.DISCUSS_REWARD.equals(finalType)) {
                            UserDiscussItemControl.jumpToDynamicDetailUIView(mILayout, finalItem_id, false, false, false);
                        }
                    }
                }
            });
        }

        private void fillAvatar(final SimpleDraweeView avatarView, int graber) {
            RRetrofit.create(UserService.class)
                    .userInfo(Param.buildMap("to_uid:" + graber))
                    .compose(Rx.transformer(UserInfoBean.class))
                    .subscribe(new RSubscriber<UserInfoBean>() {
                        @Override
                        public void onSucceed(UserInfoBean bean) {
                            super.onSucceed(bean);
                            DraweeViewUtil.resize(avatarView, bean.getAvatar());
                        }
                    });
        }

        @Override
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, final int position, IMMessage bean) {
            super.onBindModelView(model, isSelector, holder, position, bean);
            if (!isEditable) {
                return;
            }

            final CheckBox checkBox = holder.v(R.id.cb_friend_addfirend);
            checkBox.setTag(position);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectorPosition(position, checkBox);
                    int tag = (int) checkBox.getTag();
                    boolean selector = isPositionSelector(position);
                    if (selector) {
                        mCheckStats.put(tag, true);
                    } else {
                        mCheckStats.delete(tag);
                    }
                    tv_selected.setText(String.format(getString(R.string.text_already_selected_item), mCheckStats.size()));
                }
            };

            checkBox.setOnClickListener(listener);
            checkBox.setChecked(mCheckStats.get(position, false));
        }

        @Override
        protected boolean onUnSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
            final CheckBox checkBox = viewHolder.v(R.id.cb_friend_addfirend);
            checkBox.setChecked(false);
            checkBox.setTag(position);
            mCheckStats.delete(position);
            return true;
        }

    }

}
