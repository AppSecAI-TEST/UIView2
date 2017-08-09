package com.hn.d.valley.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;

import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UILayoutImpl;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.github.tablayout.CommonTabLayout;
import com.angcyo.uiview.github.tablayout.TabEntity;
import com.angcyo.uiview.github.tablayout.listener.CustomTabEntity;
import com.angcyo.uiview.github.tablayout.listener.SimpleTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.rsen.RGestureDetector;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.activity.HnSplashActivity;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.constant.Action;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.cache.MsgCache;
import com.hn.d.valley.control.LoginControl;
import com.hn.d.valley.control.MainControl;
import com.hn.d.valley.control.PublishControl;
import com.hn.d.valley.main.found.FoundUIView;
import com.hn.d.valley.main.found.sub.SearchUIView;
import com.hn.d.valley.main.friend.FriendUIView;
import com.hn.d.valley.main.home.HomeUIView;
import com.hn.d.valley.main.me.MeUIView2;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.message.MessageUIView;
import com.hn.d.valley.main.message.uinfo.DynamicFuncManager2;
import com.hn.d.valley.main.seek.SeekUIView;
import com.hn.d.valley.skin.SkinUtils;
import com.hn.d.valley.start.RecommendUser2UIView;
import com.hn.d.valley.sub.user.PublishDynamicUIView;
import com.hn.d.valley.utils.Image;
import com.hn.d.valley.widget.HnLoading;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.netease.nimlib.sdk.StatusCode;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;

import static com.hn.d.valley.base.constant.Constant.POS_HOME;
import static com.hn.d.valley.base.constant.Constant.POS_ME;
import static com.hn.d.valley.base.constant.Constant.POS_MESSAGE;
import static com.hn.d.valley.base.constant.Constant.POS_SEEK;

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
public class MainUIView extends BaseUIView implements SearchUIView.OnJumpToDynamicListAction {

    protected long startAnimTime = DEFAULT_ANIM_TIME;
    CommonTabLayout mBottomNavLayout;
    UILayoutImpl mMainUILayout;

    /*首页*/
    HomeUIView mHomeUIView;
    /*发现*/
    FoundUIView mFoundUIView;
    /*消息*/
    MessageUIView mMessageUIView;
    /*我的*/
    MeUIView2 mMeUIView;
    /*寻觅*/
    SeekUIView mSeekUIView;
    /*好友*/
    FriendUIView mFriend2UIView;

    long onBackTime = 0;

    private int lastPosition = 0;
    private Subscription mSubscribe;
    private boolean isFirst = true;//第一次显示页面, 不执行动画

    public MainUIView() {
    }

    public MainUIView(long startAnimTime) {
        this.startAnimTime = startAnimTime;
    }

    @Override
    public boolean canTryCaptureView() {
        return false;
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
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

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        setChildILayout((ILayout) mViewHolder.v(R.id.main_layout));
    }

    @NonNull
    @Override
    protected UIBaseView.LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        mBottomNavLayout = mViewHolder.v(R.id.bottom_nav_layout);
        mMainUILayout = mViewHolder.v(R.id.main_layout);

        mMainUILayout.lock();
        initILayout();
        initTabLayout();
    }

    private void initILayout() {

    }

    private void resetTabLayoutIco(ArrayList<CustomTabEntity> tabs) {
        switch (SkinUtils.getSkin()) {
            case SkinManagerUIView.SKIN_BLUE:
                tabs.get(POS_MESSAGE).setTabSelectedIcon(R.drawable.message_blue_s).setTabUnselectedIcon(R.drawable.message_black_n);
                tabs.get(POS_HOME).setTabSelectedIcon(R.drawable.home_blue_s).setTabUnselectedIcon(R.drawable.home_black_n);
                tabs.get(POS_SEEK).setTabSelectedIcon(R.drawable.xunmi_blue_s).setTabUnselectedIcon(R.drawable.xunmi_black_n);
                tabs.get(POS_ME).setTabSelectedIcon(R.drawable.me_blue_s).setTabUnselectedIcon(R.drawable.me_black_n);

                break;
            case SkinManagerUIView.SKIN_GREEN:
                tabs.get(POS_MESSAGE).setTabSelectedIcon(R.drawable.message_green_s).setTabUnselectedIcon(R.drawable.message_black_n);
                tabs.get(POS_HOME).setTabSelectedIcon(R.drawable.home_green_s).setTabUnselectedIcon(R.drawable.home_black_n);
                tabs.get(POS_SEEK).setTabSelectedIcon(R.drawable.xunmi_green_s).setTabUnselectedIcon(R.drawable.xunmi_black_n);
                tabs.get(POS_ME).setTabSelectedIcon(R.drawable.me_green_s).setTabUnselectedIcon(R.drawable.me_black_n);

                break;
            default:
                tabs.get(POS_MESSAGE).setTabSelectedIcon(R.drawable.message_black_s).setTabUnselectedIcon(R.drawable.message_black_n);
                tabs.get(POS_HOME).setTabSelectedIcon(R.drawable.home_black_s).setTabUnselectedIcon(R.drawable.home_black_n);
                tabs.get(POS_SEEK).setTabSelectedIcon(R.drawable.xunmi_black_s).setTabUnselectedIcon(R.drawable.xunmi_black_n);
                tabs.get(POS_ME).setTabSelectedIcon(R.drawable.me_black_s).setTabUnselectedIcon(R.drawable.me_black_n);
                break;
        }
    }

    private void initTabLayout() {
        ArrayList<CustomTabEntity> tabs = new ArrayList<>();
        tabs.add(new TabEntity(true, mActivity.getString(R.string.nav_home_text), R.drawable.konglonggu_s, R.drawable.konglonggu_n));
        tabs.add(new TabEntity(true, "寻觅", R.drawable.konglonggu_s, R.drawable.konglonggu_n));
        tabs.add(new TabEntity(true, mActivity.getString(R.string.nav_message_text), R.drawable.message_s, R.drawable.message_n));
//        tabs.add(new TabEntity(true, mActivity.getString(R.string.friend), R.drawable.haoyou_s, R.drawable.haoyou_n));
//        tabs.add(new TabEntity(true, mActivity.getString(R.string.nav_found_text), R.drawable.found_s, R.drawable.found_n));
        tabs.add(new TabEntity(true, mActivity.getString(R.string.nav_me_text), R.drawable.me_s, R.drawable.me_n));
        resetTabLayoutIco(tabs);

//        mBottomNavLayout.setOnTabSelectListener(new OnTabSelectListener() {
//            @Override
//            public void onTabSelect(int position) {
//                changePage(position);
//            }
//
//            @Override
//            public void onTabReselect(int position) {
//                //L.e("");
//            }
//        });
        mBottomNavLayout.setOnTabSelectListener(new SimpleTabSelectListener() {
            @Override
            public void onTabSelect(int position, View tabView) {
                changePage(position);
                if (RApplication.isHighDevice) {
                    scaleView(tabView.findViewById(R.id.iv_tab_icon));
                }
            }

            @Override
            public void onTabReselect(int position, View tabView) {
                if (RApplication.isHighDevice) {
                    scaleView(tabView.findViewById(R.id.iv_tab_icon));
                }
            }
        });

        mBottomNavLayout.setTabData(tabs);
        updateSkin();

        try {
            ViewGroup group = (ViewGroup) mBottomNavLayout.getChildAt(0);
            RGestureDetector.onDoubleTap(group.getChildAt(POS_MESSAGE), new RGestureDetector.OnDoubleTapListener() {
                @Override
                public void onDoubleTap() {
                    //消息tab双击
                    if (mMessageUIView != null) {
                        mMessageUIView.scrollToTop();
                    }
                }
            });
            RGestureDetector.onDoubleTap(group.getChildAt(POS_HOME), new RGestureDetector.OnDoubleTapListener() {
                @Override
                public void onDoubleTap() {
                    //首页tab双击
                    if (mHomeUIView != null) {
                        mHomeUIView.scrollToTop();
                    }
                }
            });

            RGestureDetector.onDoubleTap(group.getChildAt(POS_SEEK), new RGestureDetector.OnDoubleTapListener() {
                @Override
                public void onDoubleTap() {
                    //首页tab双击
                    if (mSeekUIView != null) {
                        mSeekUIView.scrollToTop();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scaleView(View view) {
        ViewCompat.setScaleX(view, 0.5f);
        ViewCompat.setScaleY(view, 0.5f);
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new BounceInterpolator())
                .setDuration(300)
                .start();
    }

    /**
     * 页面切换
     */
    protected void changePage(int position) {
        boolean isRightToLeft = position < lastPosition;

        if (position == POS_HOME) {
            //首页 恐龙谷界面
            Action.tap_klg();
            if (mHomeUIView == null) {
                mHomeUIView = new HomeUIView();
                mHomeUIView.bindParentILayout(mILayout);
                mHomeUIView.setIsRightJumpLeft(isRightToLeft);
                mMainUILayout.startIView(mHomeUIView, new UIParam(!isFirst));
            } else {
                mHomeUIView.setIsRightJumpLeft(isRightToLeft);
                mMainUILayout.showIView(mHomeUIView);
            }
        } else if (position == Constant.POS_FOUND) {
            //发现
            if (mFoundUIView == null) {
                mFoundUIView = new FoundUIView();
                mFoundUIView.bindParentILayout(mILayout);
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
                mFriend2UIView.bindParentILayout(mILayout);
                mFriend2UIView.setIsRightJumpLeft(isRightToLeft);
                mMainUILayout.startIView(mFriend2UIView);
            } else {
                mFriend2UIView.setIsRightJumpLeft(isRightToLeft);
                mMainUILayout.showIView(mFriend2UIView);
            }

        } else if (position == POS_MESSAGE) {
//                    HnChatActivity.launcher(mActivity, "50033");
            //消息
            if (mMessageUIView == null) {
                mMessageUIView = new MessageUIView();
                mMessageUIView.bindParentILayout(mILayout);
                mMessageUIView.setIsRightJumpLeft(isRightToLeft);
                mMainUILayout.startIView(mMessageUIView, new UIParam(!isFirst));
            } else {
                mMessageUIView.setIsRightJumpLeft(isRightToLeft);
                mMainUILayout.showIView(mMessageUIView);
            }
        } else if (position == Constant.POS_ME) {
            //我的
            if (mMeUIView == null) {
                mMeUIView = new MeUIView2();
                mMeUIView.bindParentILayout(mILayout);
                mMeUIView.setIsRightJumpLeft(isRightToLeft);
                mMainUILayout.startIView(mMeUIView);
            } else {
                mMeUIView.setIsRightJumpLeft(isRightToLeft);
                mMainUILayout.showIView(mMeUIView);
            }
        } else if (position == Constant.POS_SEEK) {
            //寻觅
            if (mSeekUIView == null) {
                mSeekUIView = new SeekUIView();
                mSeekUIView.bindParentILayout(mILayout);
                mSeekUIView.setIsRightJumpLeft(isRightToLeft);
                mMainUILayout.startIView(mSeekUIView);
            } else {
                mSeekUIView.setIsRightJumpLeft(isRightToLeft);
                mMainUILayout.showIView(mSeekUIView);
            }
        }
        lastPosition = position;
        isFirst = false;
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

//    @Override
//    public boolean onBackPressed() {
//        long timeMillis = System.currentTimeMillis();
//        if (timeMillis - onBackTime < 1000) {
//            return true;
//        }
//        onBackTime = timeMillis;
//        T_.show(mActivity.getString(R.string.back_pressed_tip));
//        return false;
//    }


    @Override
    public void onViewHide() {
        super.onViewHide();
    }

    @Override
    public void onViewReShow(Bundle bundle) {
        super.onViewReShow(bundle);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        MainControl.checkVersion(mILayout);

        SkinUtils.init(mActivity, mParentILayout);

        DynamicFuncManager2.instance().load();

    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);

//        ViewPattern lastViewPattern = mMainUILayout.getLastViewPattern();
//        if (lastViewPattern != null) {
//            lastViewPattern.mIView.onViewShow(bundle);
//        }
        MsgCache.notifyNoreadNum();

        if (LoginControl.instance().isFirstRegister()) {
            //// TODO: 2017/5/19
            mParentILayout.startIView(new RecommendUser2UIView());
            LoginControl.instance().setFirstRegister(false);
        }
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        MainControl.onMainUnload();
    }

    @Subscribe
    public void onEvent(StatusCode status) {
        if (status == StatusCode.KICKOUT
                || status == StatusCode.KICK_BY_OTHER_CLIENT
                ) {
            //帐号被踢
            HnSplashActivity.launcher(mActivity, true, status.getValue());
            mActivity.finish();
        } else if (status == StatusCode.FORBIDDEN) {
            HnSplashActivity.launcher(mActivity, false, status.getValue());
            mActivity.finish();
        }
    }

    @Subscribe(tags = {@Tag(Constant.TAG_NO_READ_NUM)})
    public void onEvent(UpdateDataEvent event) {
        try {
            if (mHomeUIView != null) {
                mHomeUIView.onEvent(event);
            }

            if (event.num == 0) {
                mBottomNavLayout.hideMsg(event.position);
            } else if (event.position == POS_HOME) {
                mBottomNavLayout.showDot(event.position);
            } else {
                mBottomNavLayout.showMsg(event.position, event.num);
            }
        } catch (Exception e) {

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

                                    @Override
                                    public void onPublishError(String msg) {

                                    }
                                });
                            }
                        }));
                    }
                }

                @Override
                public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                    super.onEnd(isError, isNoNetwork, e);
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
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    ResUtil.setBgDrawable(group.getChildAt(i), null);
                } else {
                    ResUtil.setBgDrawable(group.getChildAt(i), skin.getThemeTranBackgroundSelector());
                }
            }

            mBottomNavLayout.setTextSelectColor(SkinHelper.getSkin().getThemeSubColor());

            resetTabLayoutIco(mBottomNavLayout.getTabEntitys());
            mBottomNavLayout.updateTabStyles();
        }
    }

    @Override
    public void onJumpToDynamicListAction() {
        mBottomNavLayout.setCurrentTab(Constant.POS_HOME);
        if (mHomeUIView != null) {
            mHomeUIView.onJumpToDynamicListAction();
        }
    }
}
