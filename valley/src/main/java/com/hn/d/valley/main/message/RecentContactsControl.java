package com.hn.d.valley.main.message;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.uiview.github.swipe.RBaseMenuAdapter;
import com.angcyo.uiview.github.swipe.recyclerview.Closeable;
import com.angcyo.uiview.github.swipe.recyclerview.OnSwipeMenuItemClickListener;
import com.angcyo.uiview.github.swipe.recyclerview.SwipeMenu;
import com.angcyo.uiview.github.swipe.recyclerview.SwipeMenuCreator;
import com.angcyo.uiview.github.swipe.recyclerview.SwipeMenuItem;
import com.angcyo.uiview.github.swipe.recyclerview.SwipeMenuRecyclerView;
import com.angcyo.uiview.github.tablayout.MsgView;
import com.angcyo.uiview.github.tablayout.UnreadMsgUtils;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.utils.TimeUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.cache.MsgCache;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.RecentContactsCache;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.control.UnreadMessageControl;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.helper.TeamNotificationHelper;
import com.hn.d.valley.nim.NoticeAttachment;
import com.hn.d.valley.nim.CustomBean;
import com.hn.d.valley.nim.RNim;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by angcyo on 2016-12-25.
 */

public class RecentContactsControl {

    public static final int ITEM_TYPE_NORMAL = 0x0001;
    public static final int ITEM_TYPE_TOP = ITEM_TYPE_NORMAL << 1;
    public static final int ITEM_TYPE_NOREAD = ITEM_TYPE_TOP << 1;
    public static final int ITEM_TYPE_SEARCH = 0x1000;

    public static final int MENU_DELETE = 1;
    public static final int MENU_ADD_TOP = 2;
    public static final int MENU_RM_TOP = 3;
    public static final int MENU_NO_READ = 4;

    /**
     * 会话是否置顶
     */
    public static final long IS_TOP = 0x00100;

    RBaseViewHolder mViewHolder;

    SwipeMenuRecyclerView mSwipeMenuRecyclerView;
    RefreshLayout mRefreshLayout;
    RecentContactsAdapter mRecentContactsAdapter;
    Context mContext;
    Action1<RecentContact> itemChatAction;
    Action1<RecentContact> itemAddContactsAction;
    Action1<RecentContact> itemCommentAction;
    Action0 searchAction;

    /**
     * 监听消息的状态改变
     */
    Observer<IMMessage> mMessageObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage imMessage) {
            //消息状态发生了改变
            List<RecentContact> allDatas = mRecentContactsAdapter.getAllDatas();
            for (int i = 0; i < allDatas.size(); i++) {
                RecentContact recentContact = allDatas.get(i);
                if (TextUtils.equals(recentContact.getRecentMessageId(), imMessage.getUuid())) {
                    recentContact.setMsgStatus(imMessage.getStatus());
                    mRecentContactsAdapter.notifyItemChanged(i + getTopItemCount());
                    RBaseViewHolder viewHolder = (RBaseViewHolder) mSwipeMenuRecyclerView.findViewHolderForAdapterPosition(i + getTopItemCount());
                    if (viewHolder != null) {
                        mRecentContactsAdapter.updateMsgStatus(recentContact, viewHolder);
                    }
                    break;
                }
            }
        }
    };

    /**
     * 滑动菜单构造器
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            if (viewType == ITEM_TYPE_SEARCH) {
                return;
            }

            int padd = mContext.getResources().getDimensionPixelOffset(R.dimen.base_xxhdpi);
            SwipeMenuItem menuItem = new SwipeMenuItem(mContext)
                    .setWidth(-2)
                    .setHeight(-1)
                    .setPaddLeft(padd)
                    .setPaddRight(padd);

            SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
            SwipeMenuItem topItem = new SwipeMenuItem(mContext);
            SwipeMenuItem noReadItem = new SwipeMenuItem(mContext);

            RBaseViewHolder.fill(menuItem, deleteItem);
            RBaseViewHolder.fill(menuItem, topItem);
            RBaseViewHolder.fill(menuItem, noReadItem);

            deleteItem.setBackgroundDrawable(R.drawable.base_red_color_bg_selector)
                    .setText("删除").setTag(MENU_DELETE);
            topItem.setBackgroundDrawable(R.drawable.base_dark_color_bg_selector);
            noReadItem.setBackgroundDrawable(R.drawable.base_orange_color_bg_selector);

            if ((viewType & ITEM_TYPE_TOP) == ITEM_TYPE_TOP) {
                topItem.setText("取消置顶").setTag(MENU_RM_TOP);
            } else {
                topItem.setText("置顶").setTag(MENU_ADD_TOP);
            }

            if ((viewType & ITEM_TYPE_NOREAD) == ITEM_TYPE_NOREAD) {
                noReadItem.setText("标为已读").setTag(MENU_NO_READ);
            } else {
                noReadItem.setText("标为未读").setTag(MENU_NO_READ);
            }

            swipeRightMenu.addMenuItem(topItem);
            swipeRightMenu.addMenuItem(noReadItem);
            swipeRightMenu.addMenuItem(deleteItem);
        }
    };

    public RecentContactsControl(Context context, Action1<RecentContact> itemChatAction, Action0 searchAction) {
        mContext = context;
        this.itemChatAction = itemChatAction;
        this.searchAction = searchAction;
        mRecentContactsAdapter = new RecentContactsAdapter(mContext, null);

        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(mMessageObserver, true);
    }

    public static RecentContactsInfo getRecentContactsInfo(RecentContact bean) {
        return getRecentContactsInfo(bean.getContactId(), bean.getFromAccount(), bean.getSessionType(), bean.getContent());
    }

    public static RecentContactsInfo getRecentContactsInfo(IMMessage bean) {
        return getRecentContactsInfo(bean.getSessionId(), bean.getFromAccount(), bean.getSessionType(), bean.getContent());
    }

    public static RecentContactsInfo getRecentContactsInfo(String contactId, String fromAccount,
                                                           SessionTypeEnum sessionTypeEnum, String lastContent) {
        final NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();
        TeamDataCache teamDataCache = TeamDataCache.getInstance();

        RecentContactsInfo info = new RecentContactsInfo();
        info.lastContent = lastContent;

        if (sessionTypeEnum == SessionTypeEnum.P2P) {
            //单聊
            if (userInfoCache != null) {

                final NimUserInfo userInfo = userInfoCache.getUserInfo(contactId);
                if (userInfo == null) {
                    info.name = contactId;
                } else {
                    info.name = userInfoCache.getUserDisplayName(contactId);
                    info.icoUrl = userInfo.getAvatar();
                }
            } else {
                info.name = contactId;
            }
        } else if (sessionTypeEnum == SessionTypeEnum.Team) {
            //群聊
            if (teamDataCache != null) {
                final Team teamById = teamDataCache.getTeamById(contactId);
                if (teamById == null) {
                    info.name = contactId + fromAccount;
                } else {
                    info.name = teamDataCache.getTeamName(contactId) +
                            teamDataCache.getTeamMemberDisplayName(contactId, fromAccount);
                    info.icoUrl = teamById.getIcon();
                }

            } else {
                info.name = contactId;
            }
        }
        return info;
    }

    public static IMMessage getMessageFromUuid(String uuid) {
        if (TextUtils.isEmpty(uuid)) {
            return null;
        }
        List<String> uuids = new ArrayList<>();
        uuids.add(uuid);
        List<IMMessage> messages = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
        if (messages.size() > 0) {
            return messages.get(messages.size() - 1);
        }
        return null;
    }

    /**
     * 是否是添加好友的消息类型
     */
    private static boolean isAddContact(RecentContact recent) {
        return Constant.add_contact.equalsIgnoreCase(recent.getContactId());
    }

    /**
     * 是否是 回复/评论 的消息类型
     */
    private static boolean isComment(RecentContact recent) {
        return Constant.comment.equalsIgnoreCase(recent.getContactId());
    }

    private int getTopItemCount() {
        return 1;
    }

    public void unLoad() {
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(mMessageObserver, false);
    }


    public void setItemChatAction(Action1<RecentContact> itemChatAction) {
        this.itemChatAction = itemChatAction;
    }

    public void setItemAddContactsAction(Action1<RecentContact> itemAddContactsAction) {
        this.itemAddContactsAction = itemAddContactsAction;
    }

    public void setItemCommentAction(Action1<RecentContact> itemCommentAction) {
        this.itemCommentAction = itemCommentAction;
    }

    /**
     * 结束刷新
     */
    public void setRefreshEnd() {
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshEnd();
        }
    }

    public void init(final View rootView) {
        mViewHolder = new RBaseViewHolder(rootView);
        mSwipeMenuRecyclerView = mViewHolder.v(R.id.swipe_recycler_view);
        mRefreshLayout = mViewHolder.v(R.id.refresh_layout);

        mRefreshLayout.setRefreshDirection(RefreshLayout.TOP);
        mRefreshLayout.addRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(@RefreshLayout.Direction int direction) {
                if (direction == RefreshLayout.TOP) {
                    RecentContactsCache.instance().buildCache();
                }
            }
        });

        final RBaseItemDecoration itemDecoration = new RBaseItemDecoration();
        itemDecoration.setMarginStart(mContext.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi));
        mSwipeMenuRecyclerView.addItemDecoration(itemDecoration);
        mSwipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSwipeMenuRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        mSwipeMenuRecyclerView.setAdapter(mRecentContactsAdapter);
        mSwipeMenuRecyclerView.setSwipeMenuItemClickListener(
                new OnSwipeMenuItemClickListener() {
                    @Override
                    public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition,
                                            @SwipeMenuRecyclerView.DirectionMode int direction,
                                            SwipeMenuItem menuItem) {
                        closeable.smoothCloseMenu();
                        //T_.show(adapterPosition + " -- " + menuPosition + " " + menuItem.getText());
                        int tag = (int) menuItem.getTag();
                        final RecentContact recentContact = mRecentContactsAdapter.getAllDatas().get(adapterPosition - getTopItemCount());
                        String messageId = recentContact.getRecentMessageId();
                        String contactId = recentContact.getContactId();

                        if (tag == MENU_DELETE) {
                            UnreadMessageControl.removeMessageUnread(contactId);
                            MsgCache.notifyNoreadNum(RecentContactsCache.instance().getTotalUnreadCount()
                                    + UnreadMessageControl.getUnreadCount()
                                    - recentContact.getUnreadCount());
                            mViewHolder.post(new Runnable() {
                                @Override
                                public void run() {
                                    mRecentContactsAdapter.deleteItem(recentContact);
                                    RNim.deleteRecentContact(recentContact);
                                }
                            });
                        } else if (tag == MENU_NO_READ) {

                            if (recentContact.getUnreadCount() + UnreadMessageControl.getMessageUnreadCount(messageId) > 0) {
                                recentContact.setMsgStatus(MsgStatusEnum.read);
                                NIMClient.getService(MsgService.class).clearUnreadCount(contactId,
                                        recentContact.getSessionType());
                                UnreadMessageControl.removeMessageUnread(contactId);
                            } else {
                                recentContact.setMsgStatus(MsgStatusEnum.unread);
//                                MsgCache.instance().setMsgUnread(messageId,
//                                        contactId,
//                                        recentContact.getSessionType());
                                UnreadMessageControl.addMessageUnread(messageId, contactId);
                            }

//                            final RecyclerView.ViewHolder viewHolder = mSwipeMenuRecyclerView.findViewHolderForAdapterPosition(adapterPosition);
//                            if (viewHolder != null) {
//                                viewHolder.itemView.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mRecentContactsAdapter.showUnreadNum(itemView, recentContact);
//                                    }
//                                });
//                            }
                            //更新导航页,未读消息数量
                            MsgCache.notifyNoreadNum();

                            mRecentContactsAdapter.notifyItemChanged(adapterPosition);
                        } else {
                            if (tag == MENU_ADD_TOP) {
                                RNim.addRecentContactTag(recentContact, IS_TOP);
                            } else if (tag == MENU_RM_TOP) {
                                RNim.removeRecentContactTag(recentContact, IS_TOP);
                            }

                            mViewHolder.post(new Runnable() {
                                @Override
                                public void run() {
                                    setRecentContact(mRecentContactsAdapter.getAllDatas());
                                }
                            });
                        }

                    }
                }
        );
    }

    public void setRecentContact(List<RecentContact> recentContact) {
        if (!recentContact.isEmpty()) {
            Collections.sort(recentContact, new Comparator<RecentContact>() {
                @Override
                public int compare(RecentContact o1, RecentContact o2) {
                    if (RNim.isRecentContactTag(o2, IS_TOP)) {
                        return 1;
                    }
                    if (RNim.isRecentContactTag(o1, IS_TOP)) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
        mRecentContactsAdapter.resetData(recentContact);
    }

    /**
     * 移除会话
     */
    public void removeRecentContact(final RecentContact recentContact) {
        mRecentContactsAdapter.deleteItem(recentContact);
    }

    public static class RecentContactsInfo {
        public String name = "";//显示的名称
        public String icoUrl = "";//显示的头像
        public String lastContent = "";//最后一条信息

        public RecentContactsInfo(String name, String icoUrl) {
            this.name = name;
            this.icoUrl = icoUrl;
        }

        public RecentContactsInfo(String name) {
            this.name = name;
        }

        public RecentContactsInfo() {
        }
    }

    /**
     * 数据适配器
     */
    private class RecentContactsAdapter extends RBaseMenuAdapter<RecentContact> {

        public RecentContactsAdapter(Context context, List<RecentContact> datas) {
            super(context, datas);
        }

        @Override
        public int getItemCount() {
            int count = super.getItemCount();
            count += (count == 0 ? 0 : 1);
            return count;
        }

        @Override
        public int getItemType(int position) {
            if (position == 0) {
                return ITEM_TYPE_SEARCH;
            }
            int viewType = 0;
            final RecentContact contact = mAllDatas.get(position - getTopItemCount());
            if (RNim.isRecentContactTag(contact, IS_TOP)) {
                viewType |= ITEM_TYPE_TOP;
            } else {
                viewType |= ITEM_TYPE_NORMAL;
            }

            int unreadCount = contact.getUnreadCount() + UnreadMessageControl.getMessageUnreadCount(contact.getRecentMessageId());
            if (unreadCount > 0) {
                viewType |= ITEM_TYPE_NOREAD;
            } else {
                viewType &= ~ITEM_TYPE_NORMAL;
            }

            return viewType;
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            if (viewType == ITEM_TYPE_SEARCH) {
                return R.layout.item_recent_search;
            }
            return R.layout.item_recent_contacts;
        }

        @Override
        protected void onBindView(final RBaseViewHolder holder, final int position, final RecentContact b) {
            if (holder.getItemViewType() == ITEM_TYPE_SEARCH) {
                holder.v(R.id.search_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //T_.show("搜索...");
                        searchAction.call();
                    }
                });
                return;
            }

            final RecentContact bean = mAllDatas.get(position - getTopItemCount());

            updateUserInfo(holder, bean);

            //最后一条消息内容
            MoonUtil.show(mContext, holder.tv(R.id.msg_content_view), getShowContent(bean));

            //消息发送状态
            updateMsgStatus(bean, holder);

            //时间
            String timeString = TimeUtil.getTimeShowString(bean.getTime(), true);
            holder.tv(R.id.msg_time_view).setText(timeString);

            //未读数量
            showUnreadNum(holder, bean);

            //会话item
            final View itemLayout = holder.v(R.id.item_root_layout);
            if (RNim.isRecentContactTag(bean, IS_TOP)) {
                itemLayout.setBackgroundResource(R.drawable.base_main_color_reverse_bg_selector);
            } else {
                itemLayout.setBackgroundResource(R.drawable.base_main_color_bg_selector);
            }
            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UnreadMessageControl.removeMessageUnread(bean.getContactId());
                    NIMClient.getService(MsgService.class).clearUnreadCount(bean.getContactId(),
                            bean.getSessionType());
                    notifyItemChanged(position);
                    if (isAddContact(bean)) {
                        if (itemAddContactsAction != null) {
                            itemAddContactsAction.call(bean);
                        }
                    } else if (isComment(bean)) {
                        if (itemCommentAction != null) {
                            itemCommentAction.call(bean);
                        }
                    } else {
                        if (itemChatAction != null) {
                            itemChatAction.call(bean);
                        }
                    }
                }
            });
        }

        private void showUnreadNum(RBaseViewHolder holder, RecentContact bean) {
            int unreadNum = bean.getUnreadCount() + UnreadMessageControl.getMessageUnreadCount(bean.getRecentMessageId());
            UnreadMsgUtils.showUnreadNum((MsgView) holder.v(R.id.msg_num_view), unreadNum);
        }

        /**
         * 头像, 昵称信息
         */
        private void updateUserInfo(RBaseViewHolder holder, RecentContact bean) {
            final RecentContactsInfo recentContactsInfo = getRecentContactsInfo(bean);

            DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.ico_view), recentContactsInfo.icoUrl);
            holder.tv(R.id.recent_name_view).setText(recentContactsInfo.name);
        }

        /**
         * 消息状态
         */
        private void updateMsgStatus(final RecentContact recent, final RBaseViewHolder holder) {
            final ImageView imageView = holder.imgV(R.id.msg_status_view);
            MsgStatusEnum status = recent.getMsgStatus();
            switch (status) {
                case fail:
                    imageView.setImageResource(R.drawable.nim_g_ic_failed_small);
                    imageView.setVisibility(View.VISIBLE);
                    break;
                case sending:
                    imageView.setImageResource(R.drawable.nim_recent_contact_ic_sending);
                    imageView.setVisibility(View.VISIBLE);
                    break;
                default:
                    imageView.setVisibility(View.GONE);
                    break;
            }
        }

        /**
         * 最后一条消息内容
         */
        private String getShowContent(final RecentContact recent) {
            IMMessage message;
            MsgAttachment attachment = recent.getAttachment();

            switch (recent.getMsgType()) {
                case text:
                    return recent.getContent();
                case image:
                    return "[图片]";
                case video:
                    return "[视频]";
                case audio:
                    return "[语音消息]";
                case location:
                    return "[位置]";
                case file:
                    return "[文件]";
                case tip:
                    message = getMessageFromUuid(recent.getRecentMessageId());

//                    List<String> uuids = new ArrayList<>();
//                    uuids.add(recent.getRecentMessageId());
//                    List<IMMessage> messages = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
//                    if (messages != null && messages.size() > 0) {
//                        return messages.get(0).getContent();
//                    }
                    if (message != null) {
                        return message.getContent();
                    }
                    return "[通知提醒]";
                case notification:
                    return TeamNotificationHelper.getTeamNotificationText(recent.getContactId(),
                            recent.getFromAccount(),
                            (NotificationAttachment) attachment);
                case avchat:
                    return "[会议]";
                case custom:
                    if (isAddContact(recent)) {
                        //添加好友通知
                        if (attachment instanceof NoticeAttachment) {
                            CustomBean bean = ((NoticeAttachment) attachment).getBean();
                            if (bean != null) {
                                String tip = bean.getTip();
                                if (TextUtils.isEmpty(tip)) {
                                    return bean.getUsername() + " " + bean.getMsg();
                                }
                                return tip + " " + bean.getMsg();
                            }
                        }
                    } else if (isComment(recent)) {
                        //动态通知
                        if (attachment instanceof NoticeAttachment) {
                            CustomBean bean = ((NoticeAttachment) attachment).getBean();
                            if (bean != null) {
                                return bean.getMsg();
                            }
                        }
                    }
                    return "[自定义消息]";
                default:
                    return "[未知类型消息]";
            }
        }
    }
}
