package com.hn.d.valley.main.message.groupchat;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupDescBean;
import com.hn.d.valley.bean.event.EmptyChatEvent;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.message.chat.ChatUIView2;
import com.hn.d.valley.service.GroupChatService;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action3;

/**
 * Created by hewking on 2017/3/10.
 */
public class GroupChatUIView extends ChatUIView2 {

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        rightItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.add_friends_s).setListener(new View.OnClickListener() {
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

        return super.getTitleBar().setRightItems(rightItems);
    }


    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);

        if(!checkInGroup()){
            showNotice();
        }

    }

    private boolean checkInGroup() {
        Team team = TeamDataCache.getInstance().getTeamById(mSessionId);
        if (team == null || !team.isMyTeam()) {
            return false;
        }
        return true;
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
                                initMentionListener(bean);
                            }
                        }
                    }));
        }
    }

    @Override
    public void onSendClick() {
        super.onSendClick();
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
                        mInputView.addMention(item.getMemberBean().getDefaultNick());
                    }
                });
            }
        });
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
