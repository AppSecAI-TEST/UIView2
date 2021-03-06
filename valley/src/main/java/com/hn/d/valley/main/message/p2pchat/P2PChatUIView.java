package com.hn.d.valley.main.message.p2pchat;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.skin.SkinImpl;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.event.EmptyChatEvent;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.chat.ChatUIView2;
import com.hn.d.valley.main.message.session.SessionCustomization;
import com.hn.d.valley.main.message.uinfo.UserInfoHelper;
import com.hn.d.valley.main.message.uinfo.UserInfoObservable;
import com.hn.d.valley.main.other.KLJUIView;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.UserService;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：聊天界面
 * 创建人员：Robi
 * 创建时间：2016/12/27 17:46
 * 修改人员：Robi
 * 修改时间：2016/12/27 17:46
 * 修改备注：
 * Version: 1.0.0
 */
public class P2PChatUIView extends ChatUIView2 {

    private UserInfoObservable.UserInfoObserver uinfoObserver;

    private LinearLayout ll_focus;
    int getRelationship = 0;

    /**
     * @param sessionId   聊天对象账户
     * @param sessionType 聊天类型, 群聊, 单聊
     */
    public static void start(ILayout mLayout, String sessionId, SessionTypeEnum sessionType, IMMessage anchor, SessionCustomization customization) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SESSION_ID, sessionId);
        bundle.putInt(KEY_SESSION_TYPE, sessionType.getValue());
        bundle.putSerializable(KEY_ANCHOR, anchor);
        bundle.putSerializable(KEY_SESSION_CUSTOMIZATION, customization);
        mLayout.startIView(new P2PChatUIView(), new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }


    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        rightItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.top_friends).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startIView(new P2PInfoUIView());
                if (mSessionId.equals(Constant.klj)) {
                    startIView(new KLJUIView());
                    return;
                }
                P2PInfoUIView.start(mParentILayout, mSessionId, sessionType);

            }
        }));

        return super.getTitleBar().setRightItems(rightItems);
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        registerObservers(true);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        registerObservers(false);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        ll_focus = (LinearLayout) rootView.findViewById(R.id.ll_focus);

    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        initContactFocus();
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        NimUserInfoCache.getInstance().getUserInfoFromRemote(mSessionId, new com.netease.nimlib.sdk.RequestCallback<NimUserInfo>() {
            @Override
            public void onSuccess(NimUserInfo param) {
                if (param == null) {
                    return;
                }
                if (!TextUtils.isEmpty(param.getName())) {
                    setTitleString(param.getName());
                }
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
    }

    private void initContactFocus() {
        // 当前对象恐龙君不显示
        if (mSessionId.equals(Constant.klj)) {
            return;
        }
        add(RRetrofit.create(ContactService.class)
                .getRelationship(Param.buildMap("to_uid:" + mSessionId))
                .compose(Rx.transformer(Integer.class))
                .subscribe(new BaseSingleSubscriber<Integer>() {
                    @Override
                    public void onSucceed(Integer bean) {
                        super.onSucceed(bean);
                        getRelationship = bean;
                        if (bean == 0 || bean == 6) {
                            ll_focus.setVisibility(View.VISIBLE);
                            TextView tv_focus = (TextView) ll_focus.findViewById(R.id.btn_send);
                            tv_focus.setBackground(SkinHelper.getSkin().getThemeMaskBackgroundRoundSelector());
                            tv_focus.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                  collapseFocuslayout();
                                }
                            },5000);

                            tv_focus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    collapseFocuslayout();
                                    P2PChatUIView.this.add(RRetrofit.create(UserService.class)
                                            .attention(Param.buildMap("to_uid:" + mSessionId))
                                            .compose(Rx.transformer(String.class))
                                            .subscribe(new BaseSingleSubscriber<String>() {
                                                @Override
                                                public void onSucceed(String bean) {
                                                    T_.show(getResources().getString(R.string.attention_successed_tip));
                                                }

                                                @Override
                                                public void onError(int code, String msg) {
                                                    super.onError(code, msg);
                                                }
                                            }));
                                }
                            });
                        }
                    }
                }));
    }

    private void collapseFocuslayout() {
        float start = 0;
        float end = -ll_focus.getMeasuredHeight();
        ObjectAnimator animator = ObjectAnimator.ofFloat(ll_focus, "translationY", start, end);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    @Override
    public int getRelationType() {
        return getRelationship;
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

    private void registerObservers(boolean register) {
        if (register) {
            registerUserInfoObserver();
        } else {
            unregisterUserInfoObserver();
        }
    }

    private void registerUserInfoObserver() {
        if (uinfoObserver == null) {
            uinfoObserver = new UserInfoObservable.UserInfoObserver() {
                @Override
                public void onUserInfoChanged(List<String> accounts) {
                    if (accounts.contains(mSessionId)) {
                        requestBuddyInfo();
                    }
                }
            };
        }

        UserInfoHelper.registerObserver(uinfoObserver);
    }

    private void requestBuddyInfo() {
        setTitleString(UserInfoHelper.getUserTitleName(mSessionId, SessionTypeEnum.P2P));
    }

    private void unregisterUserInfoObserver() {
        if (uinfoObserver != null) {
            UserInfoHelper.unregisterObserver(uinfoObserver);
        }
    }

}
