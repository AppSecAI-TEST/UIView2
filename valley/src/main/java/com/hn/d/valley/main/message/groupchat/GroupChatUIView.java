package com.hn.d.valley.main.message.groupchat;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.github.tablayout.SegmentTabLayout;
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
import com.hn.d.valley.main.message.session.AitHelper;
import com.hn.d.valley.service.GroupChatService;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MemberPushOption;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import rx.functions.Action3;

/**
 * Created by hewking on 2017/3/10.
 */
public class GroupChatUIView extends ChatUIView2 {

    public static final String TAG = GroupChatUIView.class.getSimpleName();


    @BindView(R.id.rl_group_announcement)
    View layout;
    @BindView(R.id.tv_announcement)
    TextView tv_announce;

    private Map<String, GroupMemberBean> selectedMembers;

    private GroupDescBean mGroupDesc;

    private CheckBox cb_show;
    private TextView tv_title;
    private LinearLayout ll_switch;

    private boolean showAnnounce;

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        rightItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.top_chat_messages_icon).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Team team = TeamDataCache.getInstance().getTeamById(mSessionId);
                if (team != null && team.isMyTeam()) {
                    GroupInfoUIVIew.start(mOtherILayout,mSessionId,sessionType);
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
                        L.e("GroupChatUIView","getTitlebar");
                        LinearLayout layout = (LinearLayout) LayoutInflater.from(mActivity)
                                .inflate(R.layout.item_switch_annoncement, parent)
                                .findViewById(R.id.ll_titlebar);
                        parent.setTitleView(layout);
                        ll_switch = layout;
                        cb_show = (CheckBox) parent.findViewById(R.id.cb_show);
                        tv_title = (TextView) parent.findViewById(R.id.tv_title);
                    }
                });
    }


    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);

        if(!checkInGroup()){
            showNotice();
        }

    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
//        setTitleString(TeamDataCache.getInstance().getTeamName(mSessionId));

        if (mGroupDesc != null) {
            tv_title.setText(TeamDataCache.getInstance().getTeamName(mSessionId)
                    + "(" + mGroupDesc.getMemberCount() + ")");
        } else {
            tv_title.setText(TeamDataCache.getInstance().getTeamName(mSessionId));
        }

        ll_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnnounce = !showAnnounce;
                animAnnounce(showAnnounce);
                cb_show.setChecked(showAnnounce);
            }
        });
    }

    private void animAnnounce(boolean show) {

        if (layout.getVisibility() == View.GONE) {
            layout.setVisibility(View.VISIBLE);
        }

        float start = show? ScreenUtil.dip2px(- 40):0;
        float end = show?0:ScreenUtil.dip2px(- 40);
        ObjectAnimator animator = new ObjectAnimator().ofFloat(layout,"translationY",start,end);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();

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
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }

                    @Override
                    public void onSucceed(List<GroupAnnouncementBean> beans) {
                        if (beans == null || beans.size() == 0) {

                        } else {
                            tv_announce.setText(beans.get(0).getContent());
                        }
                    }
                }));

    }

    private void showNotice() {

        final View layout = mViewHolder.v(R.id.recent_contact_layout);
        final TextView content = mViewHolder.v(R.id.recent_recent_content_view);
        content.setText(R.string.team_invalid_tip);
        if (layout.getVisibility() == View.GONE) {
            layout.setVisibility(View.VISIBLE);
            post(new Runnable() {
                @Override
                public void run() {
                    layout.setTranslationY(-layout.getMeasuredHeight());
                    ViewCompat.animate(layout).translationY(0).setDuration(UIBaseView.DEFAULT_ANIM_TIME).start();
                }
            });
        }
    }

    /**
     * @param sessionId   聊天对象账户
     * @param sessionType 聊天类型, 群聊, 单聊
     */
    public static void start(ILayout mLayout, String sessionId, SessionTypeEnum sessionType , IMMessage anchor) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SESSION_ID, sessionId);
        bundle.putInt(KEY_SESSION_TYPE, sessionType.getValue());
        bundle.putSerializable(KEY_ANCHOR,anchor);
        mLayout.startIView(new GroupChatUIView(), new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        if(checkInGroup()) {
            add(RRetrofit.create(GroupChatService.class)
                    .groupInfo(Param.buildMap("uid:" + UserCache.getUserAccount(), "yx_gid:" + mSessionId))
                    .compose(Rx.transformer(GroupDescBean.class))
                    .subscribe(new BaseSingleSubscriber<GroupDescBean>() {
                        @Override
                        public void onError(int code, String msg) {
                            super.onError(code, msg);
                        }

                        @Override
                        public void onSucceed(GroupDescBean bean) {
                            if (bean != null) {
                                mGroupDesc = bean;
                                loadAnnounce(bean);
                                initMentionListener(bean);
                            }
                        }
                    }));
        }
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
                GroupMemberSelectUIVIew.start(mOtherILayout, new BaseContactSelectAdapter.Options(RModelAdapter.MODEL_SINGLE), null,bean.getGid(), new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                    @Override
                    public void call(UIBaseRxView uiBaseRxView, List<AbsContactItem> items, RequestCallback callback) {
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

        mOtherILayout.startIView(new MiddleUIDialog(mActivity.getString(R.string.text_groupannounce_update),event.notification.getContent()));

        L.i(TAG,event.notification.getContent());

    }

    @Subscribe
    public void onGroupDissolve(GroupDissolveEvent event) {
        if (!mSessionId.equals(event.sessionId)) {
            return;
        }

        mOtherILayout.startIView(new MiddleUIDialog(mActivity.getString(R.string.text_group_dissolove),event.notification.getMsg()));

        L.i(TAG,event.notification.getMsg());
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
}
