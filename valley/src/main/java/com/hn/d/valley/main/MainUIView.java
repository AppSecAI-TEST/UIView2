package com.hn.d.valley.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.UILayoutImpl;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.github.tablayout.CommonTabLayout;
import com.angcyo.uiview.github.tablayout.TabEntity;
import com.angcyo.uiview.github.tablayout.listener.CustomTabEntity;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.model.ViewPattern;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.rsen.RGestureDetector;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.activity.HnSplashActivity;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.cache.MsgCache;
import com.hn.d.valley.control.MainControl;
import com.hn.d.valley.control.PublishControl;
import com.hn.d.valley.main.found.FoundUIView;
import com.hn.d.valley.main.friend.FriendUIView;
import com.hn.d.valley.main.home.HomeUIView;
import com.hn.d.valley.main.me.MeUIView2;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.message.MessageUIView;
import com.hn.d.valley.skin.SkinUtils;
import com.hn.d.valley.sub.user.PublishDynamicUIView;
import com.hn.d.valley.utils.Image;
import com.hn.d.valley.widget.HnLoading;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.netease.nimlib.sdk.StatusCode;

import java.util.ArrayList;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：主页面
 * 创建人员：Robi
 * 创建时间：2016/12/15 15:51
 * 修改人员：Robi
 * 修改时间：2016/12/15 15:51
 * 修改备注：
 * Version: 1.0.0
 */
public class MainUIView extends BaseUIView {

    protected long startAnimTime = DEFAULT_ANIM_TIME;
    @BindView(R.id.bottom_nav_layout)
    CommonTabLayout mBottomNavLayout;
    @BindView(R.id.main_layout)
    UILayoutImpl mMainUILayout;
    /*首页*/
    HomeUIView mHomeUIView;
    /*发现*/
    FoundUIView mFoundUIView;
    /*消息*/
    MessageUIView mMessageUIView;
    /*我的*/
    MeUIView2 mMeUIView;
    /*好友*/
    FriendUIView mFriend2UIView;

    long onBackTime = 0;

    private int lastPosition = 0;
    private Subscription mSubscribe;

    public MainUIView() {
    }

    public MainUIView(long startAnimTime) {
        this.startAnimTime = startAnimTime;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main);
    }

    @Override
    public int getDefaultBackgroundColor() {
        return Color.TRANSPARENT;
    }

    @Override
    public void onViewCreate(View rootView) {
        super.onViewCreate(rootView);
        MainControl.onMainCreate();
    }

    @NonNull
    @Override
    protected UIBaseView.LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        mMainUILayout.lock();
        initILayout();
        initTabLayout();
    }

    private void initILayout() {

    }

    private void resetTabLayoutIco(ArrayList<CustomTabEntity> tabs) {
        switch (SkinUtils.getSkin()) {
            case SkinManagerUIView.SKIN_BLUE:
                tabs.get(0).setTabSelectedIcon(R.drawable.message_s_blue).setTabUnselectedIcon(R.drawable.message_n);
                tabs.get(1).setTabSelectedIcon(R.drawable.konglonggu_s_blue).setTabUnselectedIcon(R.drawable.konglonggu_n);
                tabs.get(2).setTabSelectedIcon(R.drawable.found_s_blue).setTabUnselectedIcon(R.drawable.found_n);
                tabs.get(3).setTabSelectedIcon(R.drawable.me_s_blue).setTabUnselectedIcon(R.drawable.me_n);

                break;
            case SkinManagerUIView.SKIN_GREEN:
                tabs.get(0).setTabSelectedIcon(R.drawable.message_s_green).setTabUnselectedIcon(R.drawable.message_n);
                tabs.get(1).setTabSelectedIcon(R.drawable.konglonggu_s_green).setTabUnselectedIcon(R.drawable.konglonggu_n);
                tabs.get(2).setTabSelectedIcon(R.drawable.found_s_green).setTabUnselectedIcon(R.drawable.found_n);
                tabs.get(3).setTabSelectedIcon(R.drawable.me_s_green).setTabUnselectedIcon(R.drawable.me_n);

                break;
            default:
                tabs.get(0).setTabSelectedIcon(R.drawable.message_s_black).setTabUnselectedIcon(R.drawable.message_n);
                tabs.get(1).setTabSelectedIcon(R.drawable.konglonggu_s_black).setTabUnselectedIcon(R.drawable.konglonggu_n);
                tabs.get(2).setTabSelectedIcon(R.drawable.found_s_black).setTabUnselectedIcon(R.drawable.found_n);
                tabs.get(3).setTabSelectedIcon(R.drawable.me_s_black).setTabUnselectedIcon(R.drawable.me_n);
                break;
        }
    }

    private void initTabLayout() {
        ArrayList<CustomTabEntity> tabs = new ArrayList<>();
        tabs.add(new TabEntity(true, mActivity.getString(R.string.nav_message_text), R.drawable.message_s, R.drawable.message_n));
        tabs.add(new TabEntity(true, mActivity.getString(R.string.nav_home_text), R.drawable.konglonggu_s, R.drawable.konglonggu_n));
//        tabs.add(new TabEntity(true, mActivity.getString(R.string.friend), R.drawable.haoyou_s, R.drawable.haoyou_n));
        tabs.add(new TabEntity(true, mActivity.getString(R.string.nav_found_text), R.drawable.found_s, R.drawable.found_n));
        tabs.add(new TabEntity(true, mActivity.getString(R.string.nav_me_text), R.drawable.me_s, R.drawable.me_n));
        resetTabLayoutIco(tabs);

        mBottomNavLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                boolean isRightToLeft = position < lastPosition;

                if (position == Constant.POS_HOME) {
                    //首页
                    if (mHomeUIView == null) {
                        mHomeUIView = new HomeUIView();
                        mHomeUIView.bindOtherILayout(mILayout);
                        mHomeUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.startIView(mHomeUIView);
                    } else {
                        mHomeUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.showIView(mHomeUIView);
                    }
                } else if (position == Constant.POS_FOUND) {
                    //发现
                    if (mFoundUIView == null) {
                        mFoundUIView = new FoundUIView();
                        mFoundUIView.bindOtherILayout(mILayout);
                        mFoundUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.startIView(mFoundUIView);
                    } else {
                        mFoundUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.showIView(mFoundUIView);
                    }
                } else if (position == Constant.POS_CONNECT) {
                    //联系人, 好友
                    if (mFriend2UIView == null) {
                        mFriend2UIView = new FriendUIView();
                        mFriend2UIView.bindOtherILayout(mILayout);
                        mFriend2UIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.startIView(mFriend2UIView);
                    } else {
                        mFriend2UIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.showIView(mFriend2UIView);
                    }

                } else if (position == Constant.POS_MESSAGE) {
//                    HnChatActivity.launcher(mActivity, "50033");
                    //消息
                    if (mMessageUIView == null) {
                        mMessageUIView = new MessageUIView();
                        mMessageUIView.bindOtherILayout(mILayout);
                        mMessageUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.startIView(mMessageUIView);
                    } else {
                        mMessageUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.showIView(mMessageUIView);
                    }
                } else if (position == Constant.POS_ME) {
                    //我的
                    if (mMeUIView == null) {
                        mMeUIView = new MeUIView2();
                        mMeUIView.bindOtherILayout(mILayout);
                        mMeUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.startIView(mMeUIView);
                    } else {
                        mMeUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.showIView(mMeUIView);
                    }
                }
                lastPosition = position;
            }

            @Override
            public void onTabReselect(int position) {
                L.e("");
            }
        });

        mBottomNavLayout.setTabData(tabs);
        onSkinChanged(SkinHelper.getSkin());

        try {
            ViewGroup group = (ViewGroup) mBottomNavLayout.getChildAt(0);
            RGestureDetector.onDoubleTap(group.getChildAt(1), new RGestureDetector.OnDoubleTapListener() {
                @Override
                public void onDoubleTap() {
                    //首页tab双击
                    if (mHomeUIView != null) {
                        mHomeUIView.scrollToTop();
                    }
                }
            });
            RGestureDetector.onDoubleTap(group.getChildAt(0), new RGestureDetector.OnDoubleTapListener() {
                @Override
                public void onDoubleTap() {
                    //消息tab双击
                    if (mMessageUIView != null) {
                        mMessageUIView.scrollToTop();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    public Animation loadLayoutAnimation() {
        return null;
    }

    @Override
    public Animation loadStartAnimation() {
        Animation animation = super.loadStartAnimation();
        animation.setDuration(startAnimTime);
        return animation;
    }

//    @OnClick(R.id.nav_center_view)
//    public void onPostStatusClick() {
//        startIView(new PostStatusUIDialog());
//    }

    @Override
    public boolean onBackPressed() {
        long timeMillis = System.currentTimeMillis();
        if (timeMillis - onBackTime < 1000) {
            return true;
        }
        onBackTime = timeMillis;
        T_.show(mActivity.getString(R.string.back_pressed_tip));
        return false;
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        ViewPattern lastViewPattern = mMainUILayout.getLastViewPattern();
        if (lastViewPattern != null) {
            lastViewPattern.mIView.onViewShow(bundle);
        }
        MsgCache.notifyNoreadNum();
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        MainControl.onMainUnload();
    }

    @Subscribe
    public void onEvent(StatusCode status) {
        if (status == StatusCode.KICKOUT || status == StatusCode.KICK_BY_OTHER_CLIENT) {
            //帐号被踢
            HnSplashActivity.launcher(mActivity, true);
            mActivity.finish();

//            int type = NIMClient.getService(AuthService.class).getKickedClientType();
//            String client;
//            switch (type) {
//                case ClientType.Web:
//                    client = "网页端";
//                    break;
//                case ClientType.Windows:
//                    client = "电脑端";
//                    break;
//                case ClientType.REST:
//                    client = "服务端";
//                    break;
//                default:
//                    client = "其他移动设备";
//                    break;
//            }
//            replaceIView(new LoginUIView(), new UIParam(true, true));
//            startIView(UIDialog.build()
//                    .setDialogContent("您的账户在 " + client + " 登录.")
//                    .setGravity(Gravity.CENTER));
        }
    }

    @Subscribe(tags = {@Tag(Constant.TAG_NO_READ_NUM)})
    public void onEvent(UpdateDataEvent event) {
        if (event.num == 0) {
            mBottomNavLayout.hideMsg(event.position);
        } else {
            mBottomNavLayout.showMsg(event.position, event.num);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Observable<ArrayList<Luban.ImageItem>> observable = Image.onActivityResult(mActivity, requestCode, resultCode, data);
        if (observable != null) {
            //开始发布任务.
            //mViewHolder.v(R.id.publish_control_layout).setVisibility(View.VISIBLE);
            mSubscribe = observable.subscribe(new BaseSingleSubscriber<ArrayList<Luban.ImageItem>>() {
                @Override
                public void onStart() {
                    super.onStart();
                    HnLoading.show(mILayout, true).addDismissListener(new UIIDialogImpl.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            if (mSubscribe != null) {
                                mSubscribe.unsubscribe();
                            }
                        }
                    });
                }

                @Override
                public void onSucceed(ArrayList<Luban.ImageItem> strings) {
                    if (BuildConfig.DEBUG) {
                        Luban.logFileItems(mActivity, strings);
                    }
                    if (!strings.isEmpty()) {
                        HnLoading.hide();
                        startIView(new PublishDynamicUIView(strings, new Action0() {
                            @Override
                            public void call() {
                                //开始发布任务.
                                PublishControl.instance().startPublish(new PublishControl.OnPublishListener() {
                                    @Override
                                    public void onPublishStart() {
                                        //mViewHolder.v(R.id.publish_control_layout).setVisibility(View.VISIBLE);
                                        if (mHomeUIView != null) {
                                            mHomeUIView.onPublishStart();
                                        }
                                    }

                                    @Override
                                    public void onPublishEnd() {
                                        mViewHolder.v(R.id.publish_control_layout).setVisibility(View.GONE);
                                    }
                                });
                            }
                        }));
                    }
                }

                @Override
                public void onEnd() {
                    super.onEnd();
                    HnLoading.hide();
                }
            });
        }
    }

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);
        if (mBottomNavLayout != null) {
            ViewGroup group = (ViewGroup) mBottomNavLayout.getChildAt(0);
            for (int i = 0; i < (group).getChildCount(); i++) {
                ResUtil.setBgDrawable(group.getChildAt(i), skin.getThemeTranBackgroundSelector());
            }
            mBottomNavLayout.setTextSelectColor(SkinHelper.getSkin().getThemeSubColor());

            resetTabLayoutIco(mBottomNavLayout.getTabEntitys());
            mBottomNavLayout.updateTabStyles();
        }
    }
}
