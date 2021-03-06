package com.hn.d.valley.main.message.groupchat;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RTitleCenterLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupAnnouncementBean;
import com.hn.d.valley.bean.GroupDescBean;
import com.hn.d.valley.bean.GroupMemberBean;
import com.hn.d.valley.bean.event.AnnounceUpdateEvent;
import com.hn.d.valley.bean.event.EmptyChatEvent;
import com.hn.d.valley.bean.event.GroupDissolveEvent;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.message.chat.ChatUIView2;
import com.hn.d.valley.main.message.notify.BaseNotification;
import com.hn.d.valley.main.message.notify.GroupAnnounceNotification;
import com.hn.d.valley.main.message.notify.SystemNotifyManager;
import com.hn.d.valley.main.message.redpacket.NewGroupRedPacketUIView;
import com.hn.d.valley.main.message.session.AitHelper;
import com.hn.d.valley.main.message.session.CommandItemInfo;
import com.hn.d.valley.main.message.session.RedPacketCommandItem;
import com.hn.d.valley.main.message.session.SessionCustomization;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.main.message.session.TeamAVChatCommandItem;
import com.hn.d.valley.main.message.uinfo.DynamicFuncManager2;
import com.hn.d.valley.service.GroupChatService;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MemberPushOption;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.functions.Action0;
import rx.functions.Action3;

/**
 * Created by hewking on 2017/3/10.
 */
public class GroupChatUIView extends ChatUIView2 implements GroupInfoUpdatelistener ,SessionHelper.GroupHeadAitListener {

    public static final String TAG = GroupChatUIView.class.getSimpleName();

    //view
    private View layout;
    private TextView tv_announce;
    private LinearLayout ait_control_layout;
    private ImageView iv_show_disturbing;
    private CheckBox cb_show;
    private TextView tv_title;
    private TextView tv_group_member_count;
    private LinearLayout ll_switch;

    private Map<String, GroupMemberBean> selectedMembers;
    private GroupDescBean mGroupDesc;
    private Set<IMMessage> mAitMessages;

    private boolean showAnnounce;
    private boolean isVisible;
    private boolean isSelfAdmin;


    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.top_chat_messages_icon).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Team team = TeamDataCache.getInstance().getTeamById(mSessionId);
                if (team != null && team.isMyTeam()) {
                    GroupInfoUIVIew.start(mParentILayout, mSessionId, sessionType, GroupChatUIView.this);
                } else {
                    T_.info(mActivity.getString(R.string.team_invalid_tip));
                }
            }
        }));

        return super.getTitleBar()
                .setShowBackImageView(true)
                .setTitleString("")
                .setRightItems(rightItems)
                .setOnInitTitleLayout(new TitleBarPattern.SingleTitleInit() {
                    @Override
                    public void onInitLayout(RTitleCenterLayout parent) {
                        L.e("GroupChatUIView", "getTitlebar");
                        LinearLayout layout = (LinearLayout) LayoutInflater.from(mActivity)
                                .inflate(R.layout.item_switch_annoncement, parent)
                                .findViewById(R.id.ll_titlebar);
                        parent.setTitleView(layout);
                        ll_switch = layout;
                        cb_show = (CheckBox) parent.findViewById(R.id.cb_show);
                        tv_title = (TextView) parent.findViewById(R.id.tv_title);
                        tv_group_member_count = (TextView) parent.findViewById(R.id.tv_group_member_num);
                        iv_show_disturbing = (ImageView) parent.findViewById(R.id.iv_show_disturbing);
                    }
                });
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        isVisible = true;
        if (!checkInGroup()) {
            showNotice();
        }

        // 是否有群消息更新通知
        BaseNotification notification = SystemNotifyManager.getInstance().checkForKey(mSessionId);
        if (notification != null) {
            final GroupAnnounceNotification announce = (GroupAnnounceNotification) notification;
            if (isVisible) {
//                mParentILayout.startIView(new MiddleUIDialog(mActivity.getString(R.string.text_groupannounce_update),announce.getContent()));
                mParentILayout.startIView(new GroupAnnnounceUpdateDialog(mSessionId, announce));
                tv_announce.setText(announce.getContent());
                tv_announce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new EditGroupAnnounceUIView(mGroupDesc.getGid(), announce.getAn_id(), isSelfAdmin));
                    }
                });
            }
        }
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        isVisible = false;
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        layout = v(R.id.rl_group_announcement);
        tv_announce = v(R.id.tv_announcement);
        ait_control_layout = v(R.id.ait_control_layout);
    }

    TeamAVChatCommandItem avChatAction;

    @Override
    protected List<CommandItemInfo> createCommandItems() {
        List<CommandItemInfo> list = super.createCommandItems();
        if (DynamicFuncManager2.instance().dynamicFuncResult.isShowWallet()) {
            list.add(new RedPacketCommandItem() {
                @Override
                protected void onClick() {
                    getContainer().mLayout.startIView(new NewGroupRedPacketUIView(mSessionId, mGroupDesc == null ? 0 : mGroupDesc.getMemberCount()));
                }
            });
        }
        // 定制加号点开后可以包含的操作， 默认已经有图片，视频等消息了
        avChatAction = new TeamAVChatCommandItem(AVChatType.VIDEO);
        list.add(avChatAction);
        return list;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
//        setTitleString(TeamDataCache.getInstance().getTeamName(mSessionId));

        if (mGroupDesc != null) {
            tv_title.setText(TeamDataCache.getInstance().getTeamName(mSessionId));
        } else {
            tv_title.setText(TeamDataCache.getInstance().getTeamName(mSessionId));
        }

        final Team team = TeamDataCache.getInstance().getTeamById(mSessionId);
        if (team != null && team.isMyTeam()) {
            iv_show_disturbing.setVisibility(team.mute() ? View.VISIBLE : View.GONE);
        }

        if (team != null && team.getCreator().equals(UserCache.getUserAccount())) {
            isSelfAdmin = true;
        }

        ll_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnnounce = !showAnnounce;
                animAnnounce(showAnnounce);
                cb_show.setChecked(showAnnounce);
            }
        });

        if (mAitMessages != null && mAitMessages.size() != 0) {
            ait_control_layout.setVisibility(View.VISIBLE);
            ait_control_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animAit();
                    List<IMMessage> aitList = new ArrayList<>(mAitMessages);
                    Collections.sort(aitList, new Comparator<IMMessage>() {
                        @Override
                        public int compare(IMMessage o1, IMMessage o2) {
                            return (int) (o2.getTime() - o1.getTime());
                        }
                    });
                    IMMessage target = aitList.get(0);
                    int index = mChatControl.containTarget(target);
                    if (index != -1) {
                        mChatControl.scrollToTarget(index);
                    } else {
                        fetchAnchorAndScrollTo(target);
                    }
                }
            });
        }
    }

    private void animAnnounce(boolean show) {
        if (layout.getVisibility() == View.GONE) {
            layout.setVisibility(View.VISIBLE);
        }

        float start = show ? ScreenUtil.dip2px(-40) : 0;
        float end = show ? 0 : ScreenUtil.dip2px(-40);
        ObjectAnimator animator = ObjectAnimator.ofFloat(layout, "translationY", start, end);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    private void animAit() {
        float start = 0;
        float end = ait_control_layout.getMeasuredWidth();
        ObjectAnimator animator = ObjectAnimator.ofFloat(ait_control_layout, "translationX", start, end);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();

//        ait_control_layout.post(new Runnable() {
//            @Override
//            public void run() {
//                ViewCompat.setTranslationX(ait_control_layout, ait_control_layout.getMeasuredWidth());
//                ait_control_layout.setVisibility(View.VISIBLE);
//                ViewCompat.animate(ait_control_layout).translationX(0).setDuration(300).start();
//            }
//        });
    }

    private boolean checkInGroup() {
        Team team = TeamDataCache.getInstance().getTeamById(mSessionId);
        if (team == null || !team.isMyTeam()) {
            return false;
        }
        return true;
    }

    private void loadAnnounce(final GroupDescBean bean) {
        add(RRetrofit.create(GroupChatService.class)
                .announcementList(Param.buildMap("gid:" + bean.getGid()))
                .compose(Rx.transformerList(GroupAnnouncementBean.class))
                .subscribe(new BaseSingleSubscriber<List<GroupAnnouncementBean>>() {
                    @Override
                    public void onSucceed(final List<GroupAnnouncementBean> beans) {
                        if (beans == null || beans.size() == 0) {
                            tv_announce.setText(R.string.text_no_announce);
                        } else {
                            tv_announce.setText(beans.get(0).getContent());
                            tv_announce.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startIView(new EditGroupAnnounceUIView(mGroupDesc.getGid(), beans.get(0).getAn_id(), isSelfAdmin));
                                }
                            });
                        }
                    }
                }));

    }

    private void showNotice() {
//        final View layout = mViewHolder.v(R.id.recent_contact_layout);
//        final TextView content = mViewHolder.v(R.id.recent_recent_content_view);
//        content.setText(R.string.team_invalid_tip);
//        if (layout.getVisibility() == View.GONE) {
//            layout.setVisibility(View.VISIBLE);
//            post(new Runnable() {
//                @Override
//                public void run() {
//                    layout.setTranslationY(-layout.getMeasuredHeight());
//                    ViewCompat.animate(layout).translationY(0).setDuration(UIBaseView.DEFAULT_ANIM_TIME).start();
//                }
//            });
//        }
    }

    /**
     * @param sessionId   聊天对象账户
     * @param sessionType 聊天类型, 群聊, 单聊
     */
    public static void start(ILayout mLayout, String sessionId, SessionTypeEnum sessionType, IMMessage anchor, Set<IMMessage> aitMessages, SessionCustomization customization) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SESSION_ID, sessionId);
        bundle.putInt(KEY_SESSION_TYPE, sessionType.getValue());
        bundle.putSerializable(KEY_ANCHOR, anchor);
        bundle.putSerializable(KEY_AITMESSAGES, (Serializable) aitMessages);
        bundle.putSerializable(KEY_SESSION_CUSTOMIZATION, customization);
        mLayout.startIView(new GroupChatUIView(), new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mChatControl.mChatAdapter.setHeadAitListener(this);
        if (checkInGroup()) {
            add(RRetrofit.create(GroupChatService.class)
                    .groupInfo(Param.buildMap("uid:" + UserCache.getUserAccount(), "yx_gid:" + mSessionId))
                    .compose(Rx.transformer(GroupDescBean.class))
                    .subscribe(new BaseSingleSubscriber<GroupDescBean>() {

                        @Override
                        public void onSucceed(GroupDescBean bean) {
                            if (bean != null) {
                                mGroupDesc = bean;
                                avChatAction.setGid(mGroupDesc.getGid());
                                tv_group_member_count.setText(String.format("(%d)", mGroupDesc.getMemberCount()));
                                loadAnnounce(bean);
                                initMentionListener(bean);
                            }
                        }
                    }));
        }
    }

    @Override
    protected void parseBundle(Bundle bundle) {
        super.parseBundle(bundle);
        mAitMessages = (Set<IMMessage>) bundle.getSerializable(KEY_AITMESSAGES);
    }

    @Override
    public void onSendClick() {
        final String text = mInputView.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        IMMessage imMessage = createTextMessage(text);

//        imMessage.getRemoteExtension();

//        if (text.contains("@")) {
//            imMessage.setPushContent("有人@我");
//            Map<String, Object> data = new HashMap<>();
//            data.put("@", text);
//            imMessage.setRemoteExtension(data);
//        }


        if (selectedMembers != null && !selectedMembers.isEmpty()) {
            // 从消息中构造 option 字段
            MemberPushOption option = createMemPushOption(selectedMembers, imMessage);
            imMessage.setMemberPushOption(option);
            selectedMembers = null;
        }

        sendMessage(imMessage);
        mInputView.setText("");
    }

    private void initMentionListener(final GroupDescBean bean) {
        mInputView.setOnMentionInputListener(new ExEditText.OnMentionInputListener() {
            @Override
            public void onMentionCharacterInput() {
                GroupMemberSelectUIVIew.start(mParentILayout, new BaseContactSelectAdapter.Options(RModelAdapter.MODEL_SINGLE), null, bean.getGid(), new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                    @Override
                    public void call(UIBaseRxView uiBaseRxView, List<AbsContactItem> items, RequestCallback callback) {
                        if (items.size() == 0) {
                            T_.show("不能为空!");
                            return;
                        }
                        callback.onSuccess("");

                        GroupMemberItem item = (GroupMemberItem) items.get(0);

                        if (selectedMembers == null) {
                            selectedMembers = new HashMap<>();
                        }
//                        selectedMembers.put(item.getMemberBean().getUserId(), item.getMemberBean());
                        selectedMembers.put(item.getMemberBean().getDefaultNick(), item.getMemberBean());
                        mInputView.addMention(item.getMemberBean().getDefaultNick());
                    }
                });
            }

            @Override
            public void onMentionTextChanged(List<String> allMention) {

            }
        });
    }

    private MemberPushOption createMemPushOption(Map<String, GroupMemberBean> selectedMembers, IMMessage message) {
        if (message == null || selectedMembers == null) {
            return null;
        }

        List<String> pushList = new ArrayList<>();

        String text = message.getContent();

        // remove invalid account
        Iterator<String> keys = selectedMembers.keySet().iterator();
        while (keys.hasNext()) {
            String account = keys.next();
            Pattern p = Pattern.compile("(@" + account + " )");
            Matcher matcher = p.matcher(text);
            if (matcher.find()) {
                continue;
            }
            keys.remove();
        }

        // replace
        keys = selectedMembers.keySet().iterator();
        while (keys.hasNext()) {
            String account = keys.next();
            String aitName = AitHelper.getAitName(selectedMembers.get(account));
            text = text.replaceAll("(@" + account + " )", "@" + aitName + " ");
            pushList.add(account);
        }
        message.setContent(text);

        if (pushList.isEmpty()) {
            return null;
        }

        MemberPushOption memberPushOption = new MemberPushOption();
        memberPushOption.setForcePush(true);
        memberPushOption.setForcePushContent(message.getContent());
        memberPushOption.setForcePushList(pushList);
        return memberPushOption;
    }

    @Subscribe
    public void onAnnounceUpdate(AnnounceUpdateEvent event) {
        if (!mSessionId.equals(event.sessionId)) {
            return;
        }

        if (isVisible) {
//            mParentILayout.startIView(new MiddleUIDialog(mActivity.getString(R.string.text_groupannounce_update),event.notification.getContent()));
            mParentILayout.startIView(new GroupAnnnounceUpdateDialog(mSessionId, event.notification));
        }

        L.i(TAG, event.notification.getContent());

    }

    @Subscribe
    public void onGroupDissolve(GroupDissolveEvent event) {
        if (!mSessionId.equals(event.sessionId)) {
            return;
        }

        mParentILayout.startIView(new MiddleUIDialog(mActivity.getString(R.string.text_group_dissolove), event.notification.getMsg(), new Action0() {
            @Override
            public void call() {
                finishIView();
                msgService().deleteRecentContact2(mSessionId, sessionType);
            }
        }));

        L.i(TAG, event.notification.getMsg());
    }


    @Subscribe
    public void onEvent(EmptyChatEvent event) {
        if (!mSessionId.equals(event.sessionId)) {
            return;
        }
        msgService().queryMessageListEx(
                getEmptyMessage(),
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
    public void onGroupMemebeerChanged() {

    }

    @Override
    public void onGroupNameChanged(String name) {
        tv_title.setText(String.format(Locale.CANADA, "%s", name));
        tv_group_member_count.setText(String.format(Locale.CANADA, "(%d)", mGroupDesc.getMemberCount()));
    }

    @Override
    public void onGroupTop() {

    }

    @Override
    public void onGroupHeadAit(IMMessage message){
        // 点击头像ait
        mInputView.unableCallback();
        mInputView.insert("@");
        mInputView.addMention(message.getFromNick());
        if (selectedMembers == null) {
            selectedMembers = new HashMap<>();
        }
        GroupMemberBean memberBean = new GroupMemberBean();
        memberBean.setDefaultNick(message.getFromNick());
        memberBean.setUserId(message.getFromAccount());
        mInputView.enableCallback();
        selectedMembers.put(message.getFromNick(),memberBean);
    }

    @Override
    public String getGid() {
        return mGroupDesc.getGid();
    }

    @Override
    public void onGroupNotifySetting() {
        final Team team = TeamDataCache.getInstance().getTeamById(mSessionId);
        if (team != null && team.isMyTeam()) {
            iv_show_disturbing.setVisibility(team.mute() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onGropuDissolve() {
        msgService().deleteRecentContact2(mSessionId, sessionType);
        finishIView();
    }


}
