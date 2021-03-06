package com.hn.d.valley.main.message.session;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;

import com.angcyo.library.utils.L;
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
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.TimeUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.MsgCache;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.RecentContactsCache;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UnreadMessageControl;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.helper.TeamNotificationHelper;
import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.hn.d.valley.main.message.SessionSettingDelegate;
import com.hn.d.valley.main.message.attachment.CustomExpressionAttachment;
import com.hn.d.valley.main.message.attachment.DiscussRecommAttachment;
import com.hn.d.valley.main.message.attachment.DiscussRewardAttachment;
import com.hn.d.valley.main.message.attachment.DynamicDetailAttachment;
import com.hn.d.valley.main.message.attachment.DynamicMsgAttachment;
import com.hn.d.valley.main.message.attachment.GiftReceiveAttachment;
import com.hn.d.valley.main.message.attachment.GrabedMsgAttachment;
import com.hn.d.valley.main.message.attachment.HotSpotInfoAttachment;
import com.hn.d.valley.main.message.attachment.InviteUploadProfileAttachment;
import com.hn.d.valley.main.message.attachment.KLGCoinConsumeAttachment;
import com.hn.d.valley.main.message.attachment.LikeMsgAttachment;
import com.hn.d.valley.main.message.attachment.OnlineVideoForwardAttachment;
import com.hn.d.valley.main.message.attachment.PersonalCard;
import com.hn.d.valley.main.message.attachment.PersonalCardAttachment;
import com.hn.d.valley.main.message.attachment.RechargeMsgAttachment;
import com.hn.d.valley.main.message.attachment.RedPacketAttachment;
import com.hn.d.valley.main.message.attachment.RefundMsgAttachment;
import com.hn.d.valley.main.message.attachment.ShareNewsAttachment;
import com.hn.d.valley.main.message.attachment.SystemPushAttachment;
import com.hn.d.valley.main.message.attachment.WithDrawalAttachment;
import com.hn.d.valley.main.message.attachment.WithDrawalFailAttachment;
import com.hn.d.valley.nim.CustomBean;
import com.hn.d.valley.nim.NoticeAttachment;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.sub.user.DiscussRewardMsg;
import com.hn.d.valley.widget.HnExTextView;
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

import io.realm.Realm;
import rx.functions.Action0;
import rx.functions.Action1;

import static com.hn.d.valley.helper.TeamNotificationHelper.buildGiftReceiveMsgNotification;
import static com.hn.d.valley.helper.TeamNotificationHelper.buildGrabedMsgNotification;

/**
 * Created by angcyo on 2016-12-25.
 */

public class RecentContactsControl {

    private static final String TAG = RecentContactsControl.class.getSimpleName();

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
    Action0 searchAction;

    /**
     * 监听消息的状态改变
     */
    Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage imMessage) {

            L.i(TAG, "statusObserver " + imMessage.getContent());

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

    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeMsgStatus(statusObserver, register);
    }

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
        registerObservers(true);
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
                    info.name = teamDataCache.getTeamName(contactId) /*+
                            teamDataCache.getTeamMemberDisplayName(contactId, fromAccount)*/;
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

    /**
     * 是否恐龙君
     */
    private static boolean isKLJ(RecentContact recent) {
        return Constant.klj.equalsIgnoreCase(recent.getContactId());
    }

    /**
     * 是否钱包消息
     */
    private static boolean isWalletJ(RecentContact recent) {
        return Constant.wallet.equalsIgnoreCase(recent.getContactId());
    }

    private static boolean isHotNews(RecentContact recent) {
        return Constant.hot_news.equalsIgnoreCase(recent.getContactId());
    }

    private int getTopItemCount() {
        return 1;
    }

    public void unLoad() {
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(statusObserver, false);
    }


    public void setItemChatAction(Action1<RecentContact> itemChatAction) {
        this.itemChatAction = itemChatAction;
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
        mRefreshLayout.addOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(@RefreshLayout.Direction int direction) {
                if (direction == RefreshLayout.TOP) {
                    RecentContactsCache.instance().buildCache();
                }
            }
        });

        final RBaseItemDecoration itemDecoration = new RBaseItemDecoration();
        itemDecoration.setMarginStart(mContext.getResources().getDimensionPixelOffset(R.dimen.base_65dpi));
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
//                                        mRecentContactsAdapter.showUnreadNum(mViewHolder, recentContact);
//                                    }
//                                });
//                            }
                            //更新导航页,未读消息数量
                            MsgCache.notifyNoreadNum();

                            mRecentContactsAdapter.notifyItemChanged(adapterPosition);
                        } else {
                            if (tag == MENU_ADD_TOP) {
                                RNim.addRecentContactTag(recentContact, IS_TOP);

//                                SessionSettingDelegate.getInstance().setTop(recentContact.getContactId(), recentContact.getSessionType(), 1);

                            } else if (tag == MENU_RM_TOP) {
                                RNim.removeRecentContactTag(recentContact, IS_TOP);

//                                SessionSettingDelegate.getInstance().setTop(recentContact.getContactId(), recentContact.getSessionType(), 0);

                            }

                            mViewHolder.post(new Runnable() {
                                @Override
                                public void run() {
                                    RecentContactsCache.instance().buildCache();
//                                    setRecentContact(mRecentContactsAdapter.getAllDatas());
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
//                    if (SessionSettingDelegate.getInstance().checkTop(o1.getContactId())) {
//                        RNim.addRecentContactTag(o1, IS_TOP);
//                    } else {
//                        if (RNim.isRecentContactTag(o1, IS_TOP)) {
//                            RNim.removeRecentContactTag(o1, IS_TOP);
//                        }
//                    }
//
//                    if (SessionSettingDelegate.getInstance().checkTop(o2.getContactId())) {
//                        RNim.addRecentContactTag(o2, IS_TOP);
//                    } else {
//                        if (RNim.isRecentContactTag(o2, IS_TOP)) {
//                            RNim.removeRecentContactTag(o2, IS_TOP);
//                        }
//                    }

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
        mRecentContactsAdapter.resetData(new ArrayList<>(recentContact));
    }

    /**
     * 移除会话
     */
    public void removeRecentContact(final RecentContact recentContact) {
        mRecentContactsAdapter.deleteItem(recentContact);
    }

    private String bindCustomSession(RecentContact recent, MsgAttachment attachment) {
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
                    // 设置新的动态通知
                    final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
                    RRealm.exe(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            L.i(TAG, "setNew_notification true");
                            userInfoBean.setNew_notification(true);
                        }
                    });
                    return bean.getMsg();
                }
            } else if (attachment instanceof LikeMsgAttachment) {
                return ((LikeMsgAttachment)attachment).getLikeMsg().getMsg();
            } else if (attachment instanceof DynamicMsgAttachment) {
                return ((DynamicMsgAttachment) attachment).getDynamicMsg().getMsg();
            }
        } else if (attachment instanceof PersonalCardAttachment) {
            PersonalCard card = ((PersonalCardAttachment) attachment).getPersonalCard();
            if (card != null && !TextUtils.isEmpty(card.getMsg())) {
                return card.getMsg();
            }
            return "[名片]";
        } else if (attachment instanceof RedPacketAttachment) {
            return "红包消息";
        } else if (attachment instanceof GrabedMsgAttachment) {
            return buildGrabedMsgNotification(mContext,(GrabedMsgAttachment) attachment);
        } else if (attachment instanceof HotSpotInfoAttachment) {
            return ((HotSpotInfoAttachment) attachment).getHotSpotInfo().getMsg();
        } else if (attachment instanceof DynamicDetailAttachment) {
            return ((DynamicDetailAttachment) attachment).getDynamicMsg().getMsg();
        } else if (attachment instanceof SystemPushAttachment.SingleTextSPAttachment) {
            return ((SystemPushAttachment) attachment).getSystemPush().getMsg();
        } else if (attachment instanceof RefundMsgAttachment) {
            return ((RefundMsgAttachment)attachment).getRefundMsg().getMsg();
        } else if (attachment instanceof InviteUploadProfileAttachment) {
            return ((InviteUploadProfileAttachment)attachment).getInviteUploadMsg().getMsg();
        } else if (attachment instanceof DiscussRecommAttachment) {
            return ((DiscussRecommAttachment)attachment).getRecommendMsg().getMsg();
        } else if (attachment instanceof CustomExpressionAttachment) {
            return "[动画表情]";
        }else if (attachment instanceof KLGCoinConsumeAttachment) {
            return ((KLGCoinConsumeAttachment)attachment).getConsumeMsg().getMsg();
        }else if (attachment instanceof WithDrawalFailAttachment) {
            return ((WithDrawalFailAttachment)attachment).getWithDrawalMsg().getMsg();
        }else if (attachment instanceof WithDrawalAttachment) {
            return ((WithDrawalAttachment)attachment).getWithDrawalMsg().getMsg();
        } else if (attachment instanceof RechargeMsgAttachment) {
            return ((RechargeMsgAttachment)attachment).getRechargeMsg().getMsg();
        } else if (attachment instanceof GiftReceiveAttachment) {
            String msg = buildGiftReceiveMsgNotification(recent, (GiftReceiveAttachment) attachment);
            if (msg != null) return msg;
        } else if (attachment instanceof ShareNewsAttachment) {
            return "[分享资讯]" + ((ShareNewsAttachment) attachment).getNewsMsg().getTitle();
        } else if (attachment instanceof OnlineVideoForwardAttachment) {
            return "[视频]";
        } else if (attachment instanceof DiscussRewardAttachment) {
            DiscussRewardMsg rewardMsg = ((DiscussRewardAttachment) attachment).getDiscussRewardMsg();
            return rewardMsg.getMsg();
        }
        return "[自定义消息]";
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
        public void resetData(List<RecentContact> datas) {
            if (datas == null) {
                this.mAllDatas = new ArrayList<>();
            } else {
                this.mAllDatas.clear();
                this.mAllDatas.addAll(datas);
            }
            notifyDataSetChanged();
        }

        @Override
        protected void onBindView(final RBaseViewHolder holder, final int position, final RecentContact b) {
            if (holder.getItemViewType() == ITEM_TYPE_SEARCH) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
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

//            IMMessage imMessage = getMessageFromUuid(bean.getRecentMessageId());
//            assert imMessage != null;
//            Map<String, Object> remoteExtension = imMessage.getRemoteExtension();
//            String ait = "";
//            if ( remoteExtension!= null) {
//                String ait_text = (String) remoteExtension.get("@");
//
//                if (ait_text.contains(UserCache.instance().getUserInfoBean().getUsername())) {
//                    ait = imMessage.getPushContent();
//                }
//            }

            String fromId = bean.getFromAccount();
            HnExTextView msgContentView = holder.v(R.id.msg_content_view);
            msgContentView.setImageSpanTextColor(ContextCompat.getColor(mContext, R.color.main_text_color_dark));
            if (SessionTypeEnum.Team == bean.getSessionType()
                    && !TextUtils.isEmpty(fromId)
                    && !fromId.equals(UserCache.getUserAccount())
                    && !(bean.getAttachment() instanceof NotificationAttachment)) {
                String tid = bean.getContactId();
                String teamNick = getTeamUserDisplayName(tid, fromId);

                String content = teamNick + ": " + getShowContent(bean);

                L.i(TAG,content + ": " + AitHelper.hasAitExtention(bean));
                if (AitHelper.hasAitExtention(bean)) {
                    if (bean.getUnreadCount() == 0) {
                        AitHelper.clearRecentContactAited(bean);
                    } else {
                        content = AitHelper.getAitAlertString(content);
                    }
                }
//                MoonUtil.show(mContext, holder.tv(R.id.msg_content_view), content);
                // @ 提示字体变红
                MoonUtil.identifyRecentVHFaceExpressionAndTags(mContext, msgContentView
                        , content, ImageSpan.ALIGN_BOTTOM, 0.45f);

            } else {
                //最后一条消息内容
                MoonUtil.show(mContext, msgContentView, getShowContent(bean));
            }

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
                //itemLayout.setBackgroundResource(R.drawable.base_main_color_reverse_bg_selector);
                holder.itemView.setBackgroundResource(R.color.default_base_bg_dark2);
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                //itemLayout.setBackgroundResource(R.drawable.base_main_color_bg_selector);
            }
            itemLayout.setBackground(SkinHelper.getSkin().getThemeTranMaskBackgroundSelector());

            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UnreadMessageControl.removeMessageUnread(bean.getContactId());
                    NIMClient.getService(MsgService.class).clearUnreadCount(bean.getContactId(),
                            bean.getSessionType());
                    notifyItemChanged(position);

                    if (itemChatAction != null) {
                        itemChatAction.call(bean);
                    }
                }
            });
        }

        private String getTeamUserDisplayName(String tid, String account) {
            return TeamDataCache.getInstance().getTeamMemberDisplayName(tid, account);
        }

        private void showUnreadNum(RBaseViewHolder holder, RecentContact bean) {
            int unreadNum = bean.getUnreadCount() + UnreadMessageControl.getMessageUnreadCount(bean.getRecentMessageId());
            L.d(TAG,"showUnreadNum unreadcount : " + bean.getUnreadCount() + " msgcount : " + unreadNum);
            UnreadMsgUtils.showUnreadNum((MsgView) holder.v(R.id.msg_num_view), unreadNum);
        }

        /**
         * 头像, 昵称信息
         */
        private void updateUserInfo(RBaseViewHolder holder, RecentContact bean) {
            final RecentContactsInfo recentContactsInfo = getRecentContactsInfo(bean);
            SimpleDraweeView draweeView = holder.v(R.id.ico_view);
            holder.tv(R.id.recent_name_view).setText(recentContactsInfo.name);

            if (isAddContact(bean)) {
                draweeView.setImageResource(R.drawable.new_friend);
                holder.tv(R.id.recent_name_view).setText(mContext.getString(R.string.new_friend));
                return;
            } else if (isComment(bean)) {
                draweeView.setImageResource(R.drawable.dynamic_notification);
                holder.tv(R.id.recent_name_view).setText(mContext.getString(R.string.dynamic_notify));
                return;
            } else if (isKLJ(bean)) {
                holder.tv(R.id.recent_name_view).setText(R.string.text_klj);
                draweeView.setImageResource(R.drawable.konglongjun);
                return;
            } else if (isWalletJ(bean)) {
                holder.tv(R.id.recent_name_view).setText(R.string.text_klg_wallet);
            } else if (isHotNews(bean)) {
                holder.tv(R.id.recent_name_view).setText(R.string.text_hot_news);
            }
//            draweeView.setImageUrl(recentContactsInfo.icoUrl);
            DraweeViewUtil.setDraweeViewHttp(draweeView, recentContactsInfo.icoUrl);
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
                    // gif图文件方式获取
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
                    return "[音视频聊天]";
                case custom:
                    return bindCustomSession(recent, attachment);
                default:
                    return "[未知类型消息]";
            }
        }
    }
}
