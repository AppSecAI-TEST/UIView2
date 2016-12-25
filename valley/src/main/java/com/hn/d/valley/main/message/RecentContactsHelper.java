package com.hn.d.valley.main.message;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.uiview.github.swipe.RBaseMenuAdapter;
import com.angcyo.uiview.github.swipe.recyclerview.Closeable;
import com.angcyo.uiview.github.swipe.recyclerview.OnSwipeMenuItemClickListener;
import com.angcyo.uiview.github.swipe.recyclerview.SwipeMenu;
import com.angcyo.uiview.github.swipe.recyclerview.SwipeMenuCreator;
import com.angcyo.uiview.github.swipe.recyclerview.SwipeMenuItem;
import com.angcyo.uiview.github.swipe.recyclerview.SwipeMenuRecyclerView;
import com.angcyo.uiview.github.tablayout.MsgView;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.utils.TimeUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.helper.TeamNotificationHelper;
import com.hn.d.valley.nim.RNim;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by angcyo on 2016-12-25.
 */

public class RecentContactsHelper {

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

    public RecentContactsHelper(Context context, Action1<RecentContact> itemAction) {
        mContext = context;
        this.itemAction = itemAction;
        mRecentContactsAdapter = new RecentContactsAdapter(mContext, null);
    }

    public void init(final View rootView) {
        mViewHolder = new RBaseViewHolder(rootView);
        mSwipeMenuRecyclerView = mViewHolder.v(R.id.swipe_recycler_view);
        mRefreshLayout = mViewHolder.v(R.id.refresh_layout);

        mRefreshLayout.setRefreshDirection(RefreshLayout.TOP);

        final RBaseItemDecoration itemDecoration = new RBaseItemDecoration();
        itemDecoration.setMarginStart(mContext.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi));
        mSwipeMenuRecyclerView.addItemDecoration(itemDecoration);
        mSwipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSwipeMenuRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        mSwipeMenuRecyclerView.setAdapter(mRecentContactsAdapter);
        mSwipeMenuRecyclerView.setSwipeMenuItemClickListener(new OnSwipeMenuItemClickListener() {
            @Override
            public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition,
                                    @SwipeMenuRecyclerView.DirectionMode int direction, SwipeMenuItem menuItem) {
                closeable.smoothCloseMenu();
                //T_.show(adapterPosition + " -- " + menuPosition + " " + menuItem.getText());
                int tag = (int) menuItem.getTag();
                final RecentContact recentContact = mRecentContactsAdapter.getAllDatas().get(adapterPosition - 1);
                if (tag == MENU_DELETE) {
                    RNim.deleteRecentContact(recentContact);
                } else if (tag == MENU_ADD_TOP) {
                    RNim.addRecentContactTag(recentContact, IS_TOP);
                } else if (tag == MENU_RM_TOP) {
                    RNim.removeRecentContactTag(recentContact, IS_TOP);
                }
            }
        });
    }

    public void setRecentContact(List<RecentContact> recentContact) {
        if (recentContact.size() > 4) {
            RNim.addRecentContactTag(recentContact.get(2), IS_TOP);
            RNim.addRecentContactTag(recentContact.get(3), IS_TOP);
        }
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
            if (RNim.isRecentContactTag(mAllDatas.get(position - 1), IS_TOP)) {
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
                        T_.show("搜索...");
                    }
                });
                return;
            }

            final RecentContact bean = mAllDatas.get(position - 1);

            final NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();
            final String fromAccount = bean.getFromAccount();

            //头像
            DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.ico_view),
                    userInfoCache.getUserInfo(fromAccount).getAvatar());
            //昵称
            holder.tv(R.id.recent_name_view).setText(userInfoCache.getUserDisplayName(fromAccount));

            //最后一条消息内容
            MoonUtil.show(mContext, holder.tv(R.id.msg_content_view), getShowContent(bean));

            //消息发送状态
            updateMsgStatus(bean, holder.imgV(R.id.msg_status_view));

            //时间
            String timeString = TimeUtil.getTimeShowString(bean.getTime(), true);
            holder.tv(R.id.msg_time_view).setText(timeString);

            //未读数量
            int unreadNum = bean.getUnreadCount();
            showUnreadNum((MsgView) holder.v(R.id.msg_num_view), unreadNum);

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

        private void showUnreadNum(final MsgView msgView, int num) {
            if (msgView == null) {
                return;
            }
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) msgView.getLayoutParams();
            DisplayMetrics dm = msgView.getResources().getDisplayMetrics();
            if (num <= 0) {//圆点,设置默认宽高
//                msgView.setStrokeWidth(0);
//                msgView.setText("");
//
//                lp.width = (int) (5 * dm.density);
//                lp.height = (int) (5 * dm.density);
//                msgView.setLayoutParams(lp);
                msgView.setVisibility(View.INVISIBLE);
            } else {
                msgView.setVisibility(View.VISIBLE);
                lp.height = (int) (18 * dm.density);
                if (num > 0 && num < 10) {//圆
                    lp.width = (int) (18 * dm.density);
                    msgView.setText(num + "");
                } else if (num > 9 && num < 100) {//圆角矩形,圆角是高度的一半,设置默认padding
                    lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    msgView.setPadding((int) (6 * dm.density), 0, (int) (6 * dm.density), 0);
                    msgView.setText(num + "");
                } else {//数字超过两位,显示99+
                    lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    msgView.setPadding((int) (6 * dm.density), 0, (int) (6 * dm.density), 0);
                    msgView.setText("99+");
                }
                msgView.setLayoutParams(lp);
            }
        }

        private void updateMsgStatus(final RecentContact recent, final ImageView imageView) {
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
