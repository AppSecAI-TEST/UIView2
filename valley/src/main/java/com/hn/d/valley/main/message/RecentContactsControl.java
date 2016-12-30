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
import com.hn.d.valley.cache.MsgCache;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.RecentContactsCache;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.helper.TeamNotificationHelper;
import com.hn.d.valley.nim.RNim;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by angcyo on 2016-12-25.
 */

public class RecentContactsControl {

    public static final int ITEM_TYPE_NORMAL = 0x0001;
    public static final int ITEM_TYPE_TOP = 0x0002;
    public static final int ITEM_TYPE_SEARCH = 0x1000;

    public static final int MENU_DELETE = 1;
    public static final int MENU_ADD_TOP = 2;
    public static final int MENU_RM_TOP = 3;

    /**
     * 会话是否置顶
     */
    public static final long IS_TOP = 0x00100;

    RBaseViewHolder mViewHolder;

    SwipeMenuRecyclerView mSwipeMenuRecyclerView;
    RefreshLayout mRefreshLayout;
    RecentContactsAdapter mRecentContactsAdapter;
    Context mContext;
    Action1<RecentContact> itemAction;
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
                    recentContact.setMsgStatus(MsgStatusEnum.success);
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

            RBaseViewHolder.fill(menuItem, deleteItem);
            RBaseViewHolder.fill(menuItem, topItem);

            deleteItem.setBackgroundDrawable(R.drawable.base_red_color_bg_selector)
                    .setText("删除").setTag(MENU_DELETE);
            topItem.setBackgroundDrawable(R.drawable.base_orange_color_bg_selector);

            if (viewType == ITEM_TYPE_TOP) {
                topItem.setText("取消置顶").setTag(MENU_RM_TOP);
            } else {
                topItem.setText("置顶").setTag(MENU_ADD_TOP);
            }

            swipeRightMenu.addMenuItem(topItem);
            swipeRightMenu.addMenuItem(deleteItem);
        }
    };

    public RecentContactsControl(Context context, Action1<RecentContact> itemAction, Action0 searchAction) {
        mContext = context;
        this.itemAction = itemAction;
        this.searchAction = searchAction;
        mRecentContactsAdapter = new RecentContactsAdapter(mContext, null);

        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(mMessageObserver, true);
    }

    private int getTopItemCount() {
        return 1;
    }

    public void unLoad() {
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(mMessageObserver, false);
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
                    mRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RecentContactsCache.instance().buildCache();
                        }
                    }, 1000);
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
                                            @SwipeMenuRecyclerView.DirectionMode int direction, SwipeMenuItem menuItem) {
                        closeable.smoothCloseMenu();
                        //T_.show(adapterPosition + " -- " + menuPosition + " " + menuItem.getText());
                        int tag = (int) menuItem.getTag();
                        final RecentContact recentContact = mRecentContactsAdapter.getAllDatas().get(adapterPosition - getTopItemCount());
                        if (tag == MENU_DELETE) {
                            MsgCache.notifyNoreadNum(RecentContactsCache.instance().getTotalUnreadCount() - recentContact.getUnreadCount());
                            mViewHolder.post(new Runnable() {
                                @Override
                                public void run() {
                                    mRecentContactsAdapter.deleteItem(recentContact);
                                    RNim.deleteRecentContact(recentContact);
                                }
                            });
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
//        if (!recentContact.isEmpty()) {
//            Collections.sort(recentContact, new Comparator<RecentContact>() {
//                @Override
//                public int compare(RecentContact o1, RecentContact o2) {
//                    if (RNim.isRecentContactTag(o2, IS_TOP)) {
//                        return 1;
//                    }
//                    if (RNim.isRecentContactTag(o1, IS_TOP)) {
//                        return -1;
//                    }
//                    return 0;
//                }
//            });
//        }
        mRecentContactsAdapter.resetData(recentContact);
    }

    /**
     * 移除会话
     */
    public void removeRecentContact(final RecentContact recentContact) {
        mRecentContactsAdapter.deleteItem(recentContact);
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
            if (RNim.isRecentContactTag(mAllDatas.get(position - getTopItemCount()), IS_TOP)) {
                return ITEM_TYPE_TOP;
            }
            return ITEM_TYPE_NORMAL;
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            if (viewType == ITEM_TYPE_SEARCH) {
                return R.layout.item_recent_search;
            }
            return R.layout.item_recent_contacts;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, final RecentContact b) {
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
                    itemAction.call(bean);
                }
            });
        }

        private void showUnreadNum(RBaseViewHolder holder, RecentContact bean) {
            int unreadNum = bean.getUnreadCount();
            UnreadMsgUtils.showUnreadNum((MsgView) holder.v(R.id.msg_num_view), unreadNum);
        }

        /**
         * 头像, 昵称信息
         */
        private void updateUserInfo(RBaseViewHolder holder, RecentContact bean) {
            final NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();
            TeamDataCache teamDataCache = TeamDataCache.getInstance();

            final String fromAccount = bean.getFromAccount();
            final String contactId = bean.getContactId();

            if (bean.getSessionType() == SessionTypeEnum.P2P) {
                //单聊
                if (userInfoCache != null) {

                    final NimUserInfo userInfo = userInfoCache.getUserInfo(contactId);
                    if (userInfo == null) {
                        DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.ico_view), "");
                        holder.tv(R.id.recent_name_view).setText(contactId);
                    } else {
                        //头像
                        DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.ico_view), userInfo.getAvatar());
                        //昵称
                        holder.tv(R.id.recent_name_view).setText(userInfoCache.getUserDisplayName(contactId));
                    }
                } else {
                    DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.ico_view), "");
                    holder.tv(R.id.recent_name_view).setText(contactId);
                }
            } else if (bean.getSessionType() == SessionTypeEnum.Team) {
                //群聊
                if (teamDataCache != null) {
                    final Team teamById = teamDataCache.getTeamById(contactId);
                    if (teamById == null) {
                        //头像
                        DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.ico_view), "");
                        //昵称
                        holder.tv(R.id.recent_name_view).setText(contactId);
                    } else {
                        //头像
                        DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.ico_view), teamById.getIcon());
                        //昵称
                        holder.tv(R.id.recent_name_view).setText(teamDataCache.getTeamName(contactId));
                    }

                } else {
                    DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.ico_view), "");
                    holder.tv(R.id.recent_name_view).setText(contactId);
                }
            }
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
                    List<String> uuids = new ArrayList<>();
                    uuids.add(recent.getRecentMessageId());
                    List<IMMessage> messages = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
                    if (messages != null && messages.size() > 0) {
                        return messages.get(0).getContent();
                    }
                    return "[通知提醒]";
                case notification:
                    return TeamNotificationHelper.getTeamNotificationText(recent.getContactId(),
                            recent.getFromAccount(),
                            (NotificationAttachment) recent.getAttachment());
                case avchat:
                    return "[会议]";
                default:
                    return "[自定义消息]";
            }
        }
    }
}
