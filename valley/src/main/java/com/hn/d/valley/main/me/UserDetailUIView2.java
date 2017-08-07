package com.hn.d.valley.main.me;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.design.StickLayout;
import com.angcyo.uiview.design.StickLayout2;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.github.tablayout.CommonTabLayout;
import com.angcyo.uiview.github.tablayout.TabEntity;
import com.angcyo.uiview.github.tablayout.TabLayoutUtil;
import com.angcyo.uiview.github.tablayout.listener.CustomTabEntity;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.ThreadExecutor;
import com.angcyo.uiview.view.IView;
import com.angcyo.uiview.view.RClickListener;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RImageView;
import com.angcyo.uiview.widget.RTextView;
import com.angcyo.uiview.widget.viewpager.UIPagerAdapter;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Action;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.oss.OssControl;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LastAuthInfoBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.helper.AudioPlayHelper;
import com.hn.d.valley.main.home.circle.CircleUIView;
import com.hn.d.valley.main.me.setting.DynamicPermissionUIView;
import com.hn.d.valley.main.me.setting.EditInfoUIView;
import com.hn.d.valley.main.me.sub.ShowDetailUIView;
import com.hn.d.valley.main.me.sub.UserDetailMoreUIView;
import com.hn.d.valley.main.me.sub.UserInfoSubUIView;
import com.hn.d.valley.main.message.audio.BaseAudioControl;
import com.hn.d.valley.main.message.audio.Playable;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.main.wallet.WalletHelper;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.AuthService;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.sub.other.FansRecyclerUIView;
import com.hn.d.valley.sub.other.FollowersRecyclerUIView;
import com.hn.d.valley.sub.other.InputUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.user.ReportUIView;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnLoading;
import com.lzy.imagepicker.ImagePickerHelper;
import com.lzy.imagepicker.bean.ImageItem;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/10 11:11
 * 修改人员：Robi
 * 修改时间：2017/04/10 11:11
 * 修改备注：
 * Version: 1.0.0
 */
public class UserDetailUIView2 extends BaseContentUIView {

    public static final int TYPE_CHANGE_ICO = 1;
    public static final int TYPE_CHANGE_BG_PHOTO = 2;
    boolean isFollower = false;
    LastAuthInfoBean mLastAuthInfoBean;
    int getRelationship = 0;
    boolean isJumpToShowDetail = false;
    private TextView mCommandItemView;
    private TextView tv_chat;
    private String to_uid;
    private CommonTabLayout mCommonTabLayout;
    private UIViewPager mViewPager;
    private UserInfoBean mUserInfoBean;
    private AudioPlayHelper mAudioPlayHelper;
    private Action0 mOnFinishAction;
    /**
     * 0	int	普通陌生人【没有拉黑情况】
     * 1	int	双方拉黑
     * 2	int	我拉对方黑
     * 3	int	对方拉我黑
     * 4	int	互为联系人【互相关注就为联系人】
     * 5	int	我关注了对方
     * 6	int	对方关注了我
     */
    private Integer relation = 0;//与用户之间的关系
    private String mUserSetIco = "";//需要更换的用户头像
    private HnGlideImageView mHnAvatarGlideImageView;
    private CircleUIView mCircleUIView;
    /**
     * 改变头像, 还是改变背景
     */
    private int changeType = TYPE_CHANGE_ICO;

    public UserDetailUIView2(String to_uid) {
        this.to_uid = to_uid;
    }

    public UserDetailUIView2() {
        this(UserCache.getUserAccount());
    }

    /**
     * 命令按钮
     */
    public static void initCommandView(final TextView commandView, final TextView tv_chat, final UserInfoBean userInfoBean,
                                       final ILayout iLayout, final CompositeSubscription subscription,
                                       final Action0 onRequestEnd) {
        final String to_uid = userInfoBean.getUid();
        final String uid = UserCache.getUserAccount();
        if (isMe(to_uid)) {
            commandView.setVisibility(View.GONE);
            tv_chat.setVisibility(View.GONE);
        } else {
            commandView.setVisibility(View.VISIBLE);
            tv_chat.setVisibility(View.VISIBLE);
            tv_chat.setText(R.string.send_message);
            tv_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SessionHelper.startP2PSession(iLayout, to_uid, SessionTypeEnum.P2P);
                }
            });
            if (userInfoBean.getIs_attention() == 1) {
                //已关注
//                if (userInfoBean.getIs_contact() == 1) {
                //是联系人
                tv_chat.setVisibility(View.GONE);
                commandView.setText(R.string.send_message);
                //commandView.setImageResource(R.drawable.send_message_selector);
                commandView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SessionHelper.startP2PSession(iLayout, to_uid, SessionTypeEnum.P2P);
                    }
                });
//                } else {
//                    //不是联系人
//                    //commandView.setImageResource(R.drawable.add_contact2_selector);
//                    commandView.setText(R.string.add_friend);
//                    commandView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            onAddFriend(iLayout, userInfoBean, subscription);
//                        }
//                    });
//                }
            } else {
                //未关注
                // commandView.setImageResource(R.drawable.attention_selector);
                commandView.setText(R.string.add_follow);
                commandView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int relationship = userInfoBean.getGetRelationship();

                        if (relationship == 3) {
                            //对方拉黑了我
                            T_.error(commandView.getResources().getString(R.string.send_request_faild));
                        } else if (relationship == 2) {
                            //我拉黑了对方
                            UIDialog.build()
                                    .setDialogContent(commandView.getResources().getString(R.string.in_blacklist_tip, userInfoBean.getUsername()))
                                    .setOkText(commandView.getResources().getString(R.string.cancel_blackList_tip))
                                    .setOkListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            subscription.add(RRetrofit.create(ContactService.class)
                                                    .cancelBlackList(Param.buildMap("to_uid:" + to_uid))
                                                    .compose(Rx.transformer(String.class))
                                                    .subscribe(new BaseSingleSubscriber<String>() {

                                                        @Override
                                                        public void onSucceed(String bean) {
                                                            T_.show(bean);
                                                            userInfoBean.setGetRelationship(0);
                                                        }
                                                    }));
                                        }
                                    })
                                    .showDialog(iLayout)
                            ;
                        } else {
                            subscription.add(RRetrofit.create(UserService.class)
                                    .attention(Param.buildMap("to_uid:" + to_uid))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {
                                        @Override
                                        public void onSucceed(String bean) {
                                            T_.show(commandView.getResources().getString(R.string.attention_successed_tip));
                                            userInfoBean.setIs_attention(1);
                                            initCommandView(commandView, tv_chat, userInfoBean, iLayout, subscription, onRequestEnd);
                                            if (onRequestEnd != null) {
                                                onRequestEnd.call();
                                            }
                                        }
                                    }));
                        }
                    }
                });
            }
        }
    }

    /**
     * 添加好友事件
     */
    public static void onAddFriend(final ILayout iLayout, final UserInfoBean userInfoBean, final CompositeSubscription subscription) {
        iLayout.startIView(InputUIView.build(new InputUIView.InputConfigCallback() {
            @Override
            public TitleBarPattern initTitleBar(TitleBarPattern titleBarPattern) {
                return super.initTitleBar(titleBarPattern)
                        .setTitleString(iLayout.getLayout().getContext(), R.string.add_friend)
                        .addRightItem(TitleBarPattern.TitleBarItem.build(iLayout.getLayout().getContext().getString(R.string.send), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                iLayout.finishIView(mIView);
                                subscription.add(RRetrofit.create(UserService.class)
                                        .addContact(Param.buildMap("to_uid:" + userInfoBean.getUid(),
                                                "tip:" + mExEditText.string()))
                                        .compose(Rx.transformer(String.class))
                                        .subscribe(new BaseSingleSubscriber<String>() {
                                            @Override
                                            public void onSucceed(String bean) {
                                                T_.show(bean);
                                            }
                                        }));
                            }
                        }));
            }

            @Override
            public void initInputView(RBaseViewHolder holder, ExEditText editText, ItemRecyclerUIView.ViewItemInfo bean) {
                super.initInputView(holder, editText, bean);
                TextView tipView = holder.v(R.id.input_tip_view);
                tipView.setVisibility(View.VISIBLE);
                tipView.setText(R.string.add_friend_tip);

                String username = UserCache.instance().getUserInfoBean().getUsername();
                if (!TextUtils.isEmpty(username)) {
                    setInputText(iLayout.getLayout().getContext().getString(R.string.add_friend_format, username));
                    mExEditText.setSelection(2, username.length() + 2);
                    mExEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((InputUIView) mIView).showSoftInput(mExEditText);
                        }
                    }, DEFAULT_ANIM_TIME);
                }
            }
        }));
    }

    public static boolean isMe(String uid) {
        return TextUtils.equals(uid, UserCache.getUserAccount());
    }

    public boolean isMe() {
        return TextUtils.equals(to_uid, UserCache.getUserAccount());
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        mOnFinishAction = new Action0() {
            @Override
            public void call() {
                setTitleString(UserCache.instance().getUserInfoBean().getUsername());
            }
        };

        TitleBarPattern titleBarPattern = super.getTitleBar()
                .setShowBackImageView(true)
                .setTitleString("")
                .setFloating(true)
                .setTitleBarBGColor(Color.TRANSPARENT);

        titleBarPattern.addRightItem(TitleBarPattern.buildText(getString(R.string.more), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showBottomDialog();
                showMoreUIView();
            }
        }).setVisibility(View.GONE));

        titleBarPattern.addRightItem(TitleBarPattern.buildImage(R.drawable.editor, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startIView(new EditInfoUIView(MeUIView2.initPhotos(mUserInfoBean), null));
                startIView(new EditInfoUIView(MeUIView2.initPhotos(mUserInfoBean), mOnFinishAction));
            }
        }).setVisibility(View.GONE));

        return titleBarPattern;
    }

    @Override
    public void onSkinChanged(ISkin skin) {
        if (mCommonTabLayout != null) {
            ViewGroup group = (ViewGroup) mCommonTabLayout.getChildAt(0);
            for (int i = 0; i < (group).getChildCount(); i++) {
                ResUtil.setBgDrawable(group.getChildAt(i), skin.getThemeTranBackgroundSelector());
            }
            mCommonTabLayout.setTextSelectColor(SkinHelper.getSkin().getThemeSubColor());
        }
        if (mCommandItemView != null) {
            ResUtil.setBgDrawable(mCommandItemView, skin.getThemeMaskBackgroundSelector());
            ResUtil.setBgDrawable(tv_chat, skin.getThemeMaskBackgroundSelector());
        }
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_user_detail_layout2);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        mAudioPlayHelper = new AudioPlayHelper(mActivity);

        StickLayout2 stickLayout = mViewHolder.v(R.id.stick_layout);
        stickLayout.setFloatTopOffset((int) getTitleBarHeight());
        mCommandItemView = mViewHolder.v(R.id.command_item_view);
        tv_chat = mViewHolder.v(R.id.tv_chat);
        initTabLayout();
        mViewPager = mViewHolder.v(R.id.view_pager);
        mViewPager.setBackgroundColor(Color.WHITE);

        stickLayout.setOnScrollListener(new StickLayout.OnScrollListener() {
            @Override
            public void onScrollTo(float scrollY) {
                mUITitleBarContainer.evaluateBackgroundColor((int) scrollY);
            }
        });
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        mAudioPlayHelper.setEarPhoneModeEnable(false);
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        mAudioPlayHelper.stopAudio();
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        if (!isMe()) {
            //获取用户关系
            add(RRetrofit.create(ContactService.class)
                    .getRelationship(Param.buildMap("to_uid:" + to_uid))
                    .compose(Rx.transformer(Integer.class))
                    .subscribe(new BaseSingleSubscriber<Integer>() {
                        @Override
                        public void onSucceed(Integer bean) {
                            super.onSucceed(bean);
                            getRelationship = bean;
                        }
                    }));
        }
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        add(RRetrofit.create(UserService.class)
                .userInfo(Param.buildMap("to_uid:" + to_uid))
                .compose(Rx.transformer(UserInfoBean.class))
                .subscribe(new RSubscriber<UserInfoBean>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        hideLoadView();
                        if (isError) {
                            T_.error(e.getMsg());
                        }
                    }

                    @Override
                    public void onSucceed(UserInfoBean bean) {
                        if (bean == null) {
                            showEmptyLayout();
                        } else {
                            showContentLayout();
                            initView(bean);
                            initViewPager();
                        }
                    }
                })
        );

        updateRelationship();

        if (!isMe()) {
            add(RRetrofit.create(UserService.class)
                    .visit(Param.buildMap("to_uid:" + to_uid))
                    .compose(Rx.transformer(String.class))
                    .subscribe(new BaseSingleSubscriber<String>() {

                    }));
        } else {
            //获取最后一次认证的信息
            getLastAuthInfo();
        }
    }

    protected void updateRelationship() {
        if (!isMe()) {
            add(RRetrofit.create(ContactService.class)
                    .getRelationship(Param.buildMap("to_uid:" + to_uid))
                    .compose(Rx.transformer(Integer.class))
                    .subscribe(new BaseSingleSubscriber<Integer>() {
                        @Override
                        public void onSucceed(Integer bean) {
                            super.onSucceed(bean);
                            relation = bean;
                            isFollower = bean == 6;
                        }
                    }));
        }
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCommonTabLayout.setCurrentTab(position, false);
            }
        });
        mViewPager.setAdapter(new UIPagerAdapter() {
            @Override
            protected IView getIView(int position) {
                if (position == 0) {
                    UserInfoSubUIView userInfoSubUIView = new UserInfoSubUIView(mUserInfoBean, mOnFinishAction);
                    userInfoSubUIView.bindParentILayout(mParentILayout);
                    return userInfoSubUIView;
                } else if (position == 1) {
                    mCircleUIView = new CircleUIView(mUserInfoBean.getUid());
                    mCircleUIView.bindParentILayout(mParentILayout);
                    mCircleUIView.setInSubUIView(true);
                    mCircleUIView.setNeedRefresh(false);
                    return mCircleUIView;
                } else if (position == 2) {
                    MyAlbumUIView myAlbumUIView = new MyAlbumUIView(mUserInfoBean.getUid());
                    myAlbumUIView.bindParentILayout(mParentILayout);
                    return myAlbumUIView;
                } else {
                    ShowDetailUIView showDetailUIView = new ShowDetailUIView(mUserInfoBean.getUid());
                    showDetailUIView.bindParentILayout(mParentILayout);
                    return showDetailUIView;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        });
        mViewPager.setCurrentItem(isJumpToShowDetail ? 3 : 1);
    }

    public UserDetailUIView2 setJumpToShowDetail(boolean jumpToShowDetail) {
        isJumpToShowDetail = jumpToShowDetail;
        return this;
    }

    private void initTabLayout() {
        mCommonTabLayout = mViewHolder.v(R.id.tab_layout);
        List<CustomTabEntity> tabs = new ArrayList<>();
        tabs.add(new TabEntity(getString(R.string.info)));
        tabs.add(new TabEntity(getString(R.string.status)));
        tabs.add(new TabEntity(getString(R.string.photo)));
        tabs.add(new TabEntity("秀场"));
        TabLayoutUtil.initCommonTab(mCommonTabLayout, tabs, new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(position);
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        mCommonTabLayout.setTextUnselectColor(getColor(R.color.main_text_color));
        onSkinChanged(SkinHelper.getSkin());

        mCommonTabLayout.setCurrentTab(isJumpToShowDetail ? 3 : 1);
        TabLayoutUtil.setCommonTabDivider(mCommonTabLayout,
                getColor(R.color.line_color),
                LinearLayout.SHOW_DIVIDER_MIDDLE, getDimensionPixelOffset(R.dimen.base_hdpi));

        mCommonTabLayout.setBackgroundResource(R.drawable.base_dark_border);
    }

    private void initView(final UserInfoBean bean) {
        mUserInfoBean = bean;
        mUserInfoBean.setGetRelationship(getRelationship);

        final String is_auth = bean.getIs_auth();

        //背景墙
        initBgView();

        //头像的更改
        mHnAvatarGlideImageView = mViewHolder.v(R.id.user_ico_view);
        mHnAvatarGlideImageView.setImageThumbUrl(bean.getAvatar());
        mHnAvatarGlideImageView.setAuth(is_auth);
        mHnAvatarGlideImageView.setShowBorder(true);
        mHnAvatarGlideImageView.setOnClickListener(createAvatarClickListener(mHnAvatarGlideImageView));

        setTitleString(mUserInfoBean.getUsername());
        //getUITitleBarContainer().setBackgroundColor(Color.TRANSPARENT);
        //mViewHolder.fillView(mUserInfoBean);

        //关注, 粉丝
        mViewHolder.tv(R.id.attention_count).setText(RUtils.getShortString(mUserInfoBean.getAttention_count()));
        mViewHolder.tv(R.id.fans_count).setText(RUtils.getShortString(mUserInfoBean.getFans_count()));
        mViewHolder.tv(R.id.charm_count).setText(mUserInfoBean.getCharm());

        mViewHolder.click(R.id.attention_count, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMe() || mUserInfoBean.getLook_fans() == 1) {
                    startIView(new FollowersRecyclerUIView(mUserInfoBean.getUid()));
                } else {
                    T_.error("对方设置不允许查看关注/粉丝列表");
                }
            }
        });
        mViewHolder.click(R.id.fans_count, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMe() || mUserInfoBean.getLook_fans() == 1) {
                    startIView(new FansRecyclerUIView(mUserInfoBean.getUid()));
                } else {
                    T_.error("对方设置不允许查看关注/粉丝列表");
                }
            }
        });

        //可见性图标控制
        if (!isMe()) {
            View startView = mViewHolder.v(R.id.visible_start_view);
            View noseeHeView = mViewHolder.v(R.id.visible_nosee_he_view);
            View noseeMeView = mViewHolder.v(R.id.visible_nosee_me_view);

            startView.setVisibility(mUserInfoBean.getIs_star() == 1 ? View.VISIBLE : View.GONE);
            noseeHeView.setVisibility(mUserInfoBean.getLook_his_discuss() == 0 ? View.VISIBLE : View.GONE);
            noseeMeView.setVisibility(mUserInfoBean.getLook_my_discuss() == 0 ? View.VISIBLE : View.GONE);
        }

        RTextView authTextView = mViewHolder.v(R.id.auth_desc_tview);
        //是否已认证【0-未认证，1-已认证，2-认证中-查看自己信息才会有，3-认证失败-查看自己信息才会有，以前没有认证成功过才会有该值】
        if ("1".equalsIgnoreCase(is_auth)) {
            //已认证
            authTextView.setText(bean.getAuth_desc());
            authTextView.setBackgroundColor(Color.TRANSPARENT);
            authTextView.setTextColor(getColor(R.color.base_text_color_dark));
        } else {
            //未认证,认证中,认证失败
            int offset = getDimensionPixelOffset(R.dimen.base_hdpi);
            authTextView.setPadding(offset, 0, offset, 0);
            authTextView.setText(R.string.not_auth);
            if (isMe()) {
                authTextView.setBackground(ResUtil.createDrawable(getColor(R.color.orange), density()));
                if ("2".equalsIgnoreCase(is_auth)) {
                    authTextView.setText(R.string.auth_ing);
                } else if ("3".equalsIgnoreCase(is_auth)) {
                    authTextView.setText(R.string.auth_error);
                }
            } else {
                authTextView.setBackground(ResUtil.createDrawable(getColor(R.color.base_gray), density()));
            }
            authTextView.setTextColor(Color.WHITE);
        }

        if (isMe()) {
            authTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 先判断是否绑定手机
                    if (!UserCache.instance().isBindPhone()) {
                        WalletHelper.showBindPhoneDialog(mParentILayout, getString(R.string.text_not_bind_phone)
                                , getString(R.string.ok), getString(R.string.cancel));
                        return;
                    }
                    Integer integer = Integer.valueOf(is_auth);
                    switch (integer) {
                        case 1:
                        case 2:
                        case 3:
                            mParentILayout.startIView(new MyAuthStatusUIView(integer).setLastAuthInfoBean(mLastAuthInfoBean));
                            break;
                        default:
                            Action.authAction();
                            mParentILayout.startIView(new MyAuthUIView());
                            break;
                    }
                }
            });
        } else {
            authTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer integer = Integer.valueOf(is_auth);
                    switch (integer) {
                        case 1:
                        case 2:
                        case 3:
                            mParentILayout.startIView(new AuthDetailUIView(mUserInfoBean));
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        initCommandView(mCommandItemView, tv_chat, mUserInfoBean, mILayout, mSubscriptions, new Action0() {
            @Override
            public void call() {
                updateRelationship();
            }
        });

        //语音介绍
        initVoiceView();

        if (isMe()) {
            getUITitleBarContainer().showRightItem(1);
        } else {
            getUITitleBarContainer().showRightItem(0);
        }
    }

    /**
     * 最后一次认证失败的资料信息信息
     */
    private void getLastAuthInfo() {
        add(RRetrofit.create(AuthService.class)
                .lastInfo(Param.buildMap())
                .compose(Rx.transformer(LastAuthInfoBean.class))
                .subscribe(new RSubscriber<LastAuthInfoBean>() {

                    @Override
                    public void onSucceed(LastAuthInfoBean bean) {
                        mLastAuthInfoBean = bean;
                    }
                })
        );
    }


    /**
     * 背景墙
     */
    private void initBgView() {
        ImageView bgImageView = mViewHolder.v(R.id.bg_view);
        String cover = mUserInfoBean.getCover();
        if (TextUtils.isEmpty(cover)) {
            bgImageView.setImageResource(R.drawable.yonghuxiangqing_3);
        } else {
            Glide.with(mActivity)
                    .load(RUtils.split(cover).get(0))
                    .placeholder(RImageView.copyDrawable(bgImageView))
                    .into(bgImageView);
        }
        if (isMe()) {
            bgImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIItemDialog.build()
                            .setShowCancelButton(false)
                            .addItem(getString(R.string.change_bg_photos_title), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    changeType = TYPE_CHANGE_BG_PHOTO;
                                    ImagePickerHelper.startImagePicker(mActivity, true, true, false, false, 1);
                                }
                            })
                            .setItemConfig(new UIItemDialog.ItemConfig() {
                                @Override
                                public void onCreateItem(TextView itemView) {
                                    itemView.setTextColor(getColor(R.color.base_text_color));
                                }

                                @Override
                                public void onLoadContent(UIItemDialog dialog, RBaseViewHolder viewHolder) {
                                    viewHolder.v(R.id.item_content_layout).setBackgroundResource(R.drawable.base_white_round_little_shape);
                                }
                            })
                            .setGravity(Gravity.CENTER)
                            .showDialog(UserDetailUIView2.this);
                }
            });
        }
    }

    /**
     * 点击头像之后的事件处理,查看大图, 是自己可以修改头像
     */
    private View.OnClickListener createAvatarClickListener(final HnGlideImageView hnGlideImageView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMe()) {
                    ImagePagerUIView.start(mILayout, v,
                            PhotoPager.getImageItems(mUserInfoBean.getAvatar(), hnGlideImageView.copyDrawable()), 0)
                            .setIViewConfigCallback(new ImagePagerUIView.IViewConfigCallback() {
                                @Override
                                public void onInflateBaseView(RelativeLayout containRootLayout, LayoutInflater inflater) {
                                    TextView changeAvatarView = new TextView(mActivity);
                                    changeAvatarView.setText(R.string.change_avatar_text);
                                    changeAvatarView.setTextColor(Color.WHITE);
                                    ResUtil.setBgDrawable(changeAvatarView, SkinHelper.getRoundBorderSelector(Color.WHITE));
                                    changeAvatarView.setGravity(Gravity.CENTER);

                                    int offset = getDimensionPixelOffset(R.dimen.base_xxxhdpi);
                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1,
                                            getDimensionPixelOffset(R.dimen.base_item_size));
                                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    params.setMargins(offset, 0, offset, 2 * offset);

                                    containRootLayout.addView(changeAvatarView, params);

                                    changeAvatarView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            changeType = TYPE_CHANGE_ICO;
                                            ImagePickerHelper.startImagePicker(mActivity, true, true, true, false, 1);
                                        }
                                    });
                                }

                                @Override
                                public void onActivityResult(int requestCode, int resultCode, Intent data) {
                                    super.onActivityResult(requestCode, resultCode, data);
                                    final ArrayList<String> images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data);
                                    if (!images.isEmpty()) {
                                        mUserSetIco = images.get(0);

                                        ArrayList<ImageItem> items = new ArrayList<>();
                                        ImageItem imageItem = new ImageItem();
                                        imageItem.placeholderDrawable = hnGlideImageView.copyDrawable();
                                        imageItem.path = mUserSetIco;
                                        items.add(imageItem);
                                        mImagePagerUIView.getImagePageAdapter().resetData(items);

                                        changeAvatar();
                                    }
                                }
                            })
                    ;
                } else {
                    ImagePagerUIView.start(mILayout, v,
                            PhotoPager.getImageItems(mUserInfoBean.getAvatar(), hnGlideImageView.copyDrawable()), 0)
                    ;
                }
            }
        };
    }

    /**
     * 更改头像
     */
    private void changeAvatar() {
        new OssControl(new OssControl.OnUploadListener() {
            @Override
            public void onUploadStart() {
                HnLoading.show(mILayout);
            }

            @Override
            public void onUploadSucceed(List<String> list) {
                final String avatarUrl = list.get(0);

                add(RRetrofit.create(UserService.class)
                        .editInfo(Param.buildMap((changeType == TYPE_CHANGE_BG_PHOTO ? "cover:" : "avatar:") + avatarUrl))
                        .compose(Rx.transformer(UserInfoBean.class))
                        .subscribe(new BaseSingleSubscriber<UserInfoBean>() {
                            @Override
                            public void onSucceed(UserInfoBean bean) {
                                super.onSucceed(bean);
                                mUserInfoBean = bean;
                                UserCache.instance().setUserInfoBean(bean);

                                if (changeType == TYPE_CHANGE_ICO) {
                                    T_.ok(getString(R.string.avatar_change_success));
                                }

                                RRealm.exe(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        if (changeType == TYPE_CHANGE_ICO) {
                                            UserCache.setUserAvatar(avatarUrl);
                                            UserCache.instance().getUserInfoBean().setAvatar(avatarUrl);
                                        } else if (changeType == TYPE_CHANGE_BG_PHOTO) {
                                            UserCache.instance().getUserInfoBean().setCover(avatarUrl);
                                            initBgView();
                                        }
                                    }
                                });

                                try {
                                    if (changeType == TYPE_CHANGE_ICO) {
                                        mHnAvatarGlideImageView.setImageThumbUrl(avatarUrl);
                                        mCircleUIView.loadData();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                                super.onEnd(isError, isNoNetwork, e);
                                HnLoading.hide();
                            }
                        }));
            }

            @Override
            public void onUploadFailed(int code, String msg) {
                T_.show(msg);
                HnLoading.hide();
            }
        }).uploadCircleImg(mUserSetIco, true);
    }

    private void initVoiceView() {
        final LinearLayout controlLayout = mViewHolder.v(R.id.voice_control_layout);
        final ImageView voicePlayView = mViewHolder.v(R.id.voice_play_view);
        final TextView voiceTimeView = mViewHolder.v(R.id.voice_time_view);
        voiceTimeView.setTextColor(SkinHelper.getSkin().getThemeSubColor());

        if (TextUtils.isEmpty(mUserInfoBean.getVoice_introduce())) {
            controlLayout.setVisibility(View.GONE);
        } else {
            if (controlLayout.getVisibility() == View.GONE) {
                controlLayout.setVisibility(View.INVISIBLE);
                controlLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewCompat.setTranslationX(controlLayout, controlLayout.getMeasuredWidth());
                        controlLayout.setVisibility(View.VISIBLE);
                        ViewCompat.animate(controlLayout).translationX(0).setDuration(300).start();
                    }
                });
            }
            mAudioPlayHelper.initPlayImageView(voicePlayView);

            voiceTimeView.setText(Integer.valueOf(mUserInfoBean.getVoiceTime()) < 1 ? "1" : mUserInfoBean.getVoiceTime());

//            controlLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mAudioPlayHelper.playAudio(mUserInfoBean.getVoiceUrl(),
//                            mUserInfoBean.getVoiceDuration(),
//                            new BaseAudioControl.AudioControlListener() {
//                                @Override
//                                public void onAudioControllerReady(Playable playable) {
//
//                                }
//
//                                @Override
//                                public void onEndPlay(Playable playable) {
//                                    initVoiceView();
//                                }
//
//                                @Override
//                                public void updatePlayingProgress(Playable playable, long curPosition) {
//                                    voiceTimeView.setText(String.valueOf(curPosition / 1000));
//                                }
//                            });
//                }
//            });
            controlLayout.setOnClickListener(new RClickListener(1000, true) {

                @Override
                public void onRClick(View view) {
                    mAudioPlayHelper.playAudio(mUserInfoBean.getVoiceUrl(),
                            mUserInfoBean.getVoiceDuration(),
                            new BaseAudioControl.AudioControlListener() {
                                @Override
                                public void onAudioControllerReady(Playable playable) {

                                }

                                @Override
                                public void onEndPlay(Playable playable) {
                                    ThreadExecutor.instance().onMain(new Runnable() {
                                        @Override
                                        public void run() {
                                            initVoiceView();
                                        }
                                    });
                                }

                                @Override
                                public void updatePlayingProgress(Playable playable, long curPosition) {
                                    voiceTimeView.setText(String.valueOf(curPosition / 1000));
                                }
                            });
                }
            });
        }
    }

    /**
     * 更多弹窗
     */
    @Deprecated
    private void showBottomDialog() {
        if (mUserInfoBean.getIs_contact() == 1) {
            //联系人, 也就是好友
            showFriendDialog();
        } else {
            if (mUserInfoBean.getIs_attention() == 1) {
                //关注的人
                showAttentionDialog();
            } else {
                if (isFollower) {
                    //粉丝
                    showFollowerDialog();
                } else {
                    //陌生人
                    showOtherDialog();
                }
            }
        }
    }

    private void showMoreUIView() {
        startIView(new UserDetailMoreUIView(mUserInfoBean, relation));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ArrayList<String> images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data);
        if (!images.isEmpty()) {
            if (changeType == TYPE_CHANGE_BG_PHOTO) {
                ImageView bgImageView = mViewHolder.v(R.id.bg_view);
                mUserSetIco = images.get(0);
                Glide.with(mActivity)
                        .load(new File(mUserSetIco))
                        .into(bgImageView);
                changeAvatar();
            }
        }
    }

    @Override
    public void onViewShow(long viewShowCount) {
        super.onViewShow(viewShowCount);
        if (viewShowCount > 1) {
            add(RRetrofit.create(UserService.class)
                    .userInfo(Param.buildMap("to_uid:" + to_uid))
                    .compose(Rx.transformer(UserInfoBean.class))
                    .subscribe(new RSubscriber<UserInfoBean>() {

                        @Override
                        public void onSucceed(UserInfoBean bean) {
                            if (bean != null) {
                                initView(bean);
                            }
                        }
                    })
            );

            if (mUserInfoBean != null) {
                initView(mUserInfoBean);
            }
            updateRelationship();
        }
    }

    /**
     * 陌生人
     */
    private void showOtherDialog() {
        UIBottomItemDialog.build().setUseWxStyle(true)
//                .addItem(getString(R.string.set_dynamic_permission), R.drawable.delete_search, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showDynamicPermission();
//                    }
//                })
                .addItem(getString(R.string.report), R.drawable.delete_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showReport();
                    }
                })
                .addItem(getBlackListItem())
                .showDialog(mParentILayout);
    }

    /**
     * 粉丝
     */
    private void showFollowerDialog() {
        UIBottomItemDialog.build().setUseWxStyle(true)
//                .addItem(getString(R.string.set_dynamic_permission), R.drawable.delete_search, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showDynamicPermission();
//                    }
//                })
                .addItem(getString(R.string.del_fans), R.drawable.delete_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delFans();
                    }
                })
                .addItem(getString(R.string.report), R.drawable.delete_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showReport();
                    }
                })
                .addItem(getBlackListItem())
                .showDialog(mParentILayout);
    }

    /**
     * 关注的人
     */
    private void showAttentionDialog() {
        UIBottomItemDialog.build().setUseWxStyle(true)
//                .addItem(getString(R.string.set_dynamic_permission), R.drawable.delete_search, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showDynamicPermission();
//                    }
//                })
                .addItem(getString(R.string.un_attention), R.drawable.delete_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unAttention();
                    }
                })
                .addItem(getString(R.string.report), R.drawable.delete_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showReport();
                    }
                })
                .addItem(getBlackListItem())
                .showDialog(mParentILayout);
    }

    /**
     * 是好友..
     */
    private void showFriendDialog() {

        UIBottomItemDialog.build().setUseWxStyle(true)
                .addItem(getString(R.string.set_mark_title), R.drawable.delete_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setMark();
                    }
                })
                .addItem("把TA推荐给朋友", R.drawable.delete_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        T_.error("正在开发中...");
                    }
                })
                .addItem(isSetStar() ? getString(R.string.set_not_star_tip) : getString(R.string.set_star_tip),
                        R.drawable.delete_search,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setStar();
                            }
                        })
                .addItem(getString(R.string.set_dynamic_permission), R.drawable.delete_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDynamicPermission();
                    }
                })
                .addItem(getString(R.string.report), R.drawable.delete_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showReport();
                    }
                })
                .addItem(getString(R.string.del_friend), R.drawable.delete_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delFriend();
                    }
                })
                .addItem(getBlackListItem())
                .showDialog(mParentILayout);

    }

    private void showDynamicPermission() {
        startIView(new DynamicPermissionUIView(mUserInfoBean));
    }

    private void showReport() {
        startIView(new ReportUIView(mUserInfoBean));
    }

    /**
     * 取消关注
     */
    private void unAttention() {
        add(RRetrofit.create(UserService.class)
                .unAttention(Param.buildMap("to_uid:" + to_uid))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onSucceed(String bean) {
                        mUserInfoBean.setIs_attention(0);
                        initCommandView(mCommandItemView, tv_chat, mUserInfoBean, mILayout, mSubscriptions, new Action0() {
                            @Override
                            public void call() {
                                updateRelationship();
                            }
                        });
                        updateRelationship();
                    }
                }));
    }

    /**
     * 移除粉丝
     */
    private void delFans() {
        add(RRetrofit.create(ContactService.class)
                .delFans(Param.buildMap("to_uid:" + to_uid))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onSucceed(String bean) {
                        isFollower = false;
                        updateRelationship();
                    }
                }));
    }

    /**
     * 设置星标好友
     */
    private void setStar() {
        if (isSetStar()) {
            add(RRetrofit.create(ContactService.class)
                    .cancelStar(Param.buildMap("to_uid:" + to_uid))
                    .compose(Rx.transformer(String.class))
                    .subscribe(new BaseSingleSubscriber<String>() {

                        @Override
                        public void onSucceed(String bean) {
                            mUserInfoBean.setIs_star(0);
                            updateRelationship();
                        }
                    }));
        } else {
            add(RRetrofit.create(ContactService.class)
                    .setStar(Param.buildMap("to_uid:" + to_uid))
                    .compose(Rx.transformer(String.class))
                    .subscribe(new BaseSingleSubscriber<String>() {

                        @Override
                        public void onSucceed(String bean) {
                            mUserInfoBean.setIs_star(1);
                            updateRelationship();
                        }
                    }));
        }

    }

    private boolean isSetStar() {
        return mUserInfoBean.getIs_star() == 1;
    }

    /**
     * 解除好友关系
     */
    private void delFriend() {
        add(RRetrofit.create(ContactService.class)
                .delFriend(Param.buildMap("to_uid:" + to_uid))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onSucceed(String bean) {
                        isFollower = false;
                        mUserInfoBean.setIs_attention(0);
                        mUserInfoBean.setIs_contact(0);
                        initCommandView(mCommandItemView, tv_chat, mUserInfoBean, mILayout, mSubscriptions, new Action0() {
                            @Override
                            public void call() {
                                updateRelationship();
                            }
                        });
                        updateRelationship();
                    }
                }));
    }

    boolean isInBlackList() {
        return mUserInfoBean.getIs_blacklist() == 1;
    }

    /**
     * 加入黑名单
     */
    private void addBlackList() {
        if (isInBlackList()) {
            add(RRetrofit.create(ContactService.class)
                    .cancelBlackList(Param.buildMap("to_uid:" + to_uid))
                    .compose(Rx.transformer(String.class))
                    .subscribe(new BaseSingleSubscriber<String>() {

                        @Override
                        public void onSucceed(String bean) {
                            T_.show(bean);
                            mUserInfoBean.setIs_blacklist(0);
                            updateRelationship();
                        }
                    }));
        } else {
            add(RRetrofit.create(ContactService.class)
                    .addBlackList(Param.buildMap("to_uid:" + to_uid))
                    .compose(Rx.transformer(String.class))
                    .subscribe(new BaseSingleSubscriber<String>() {

                        @Override
                        public void onSucceed(String bean) {
                            T_.show(bean);
                            mUserInfoBean.setIs_blacklist(1);
                            updateRelationship();
                        }
                    }));
        }
    }

    public UIItemDialog.ItemInfo getBlackListItem() {
        return new UIItemDialog.ItemInfo(isInBlackList() ? getString(R.string.cancel_blackList_tip) : getString(R.string.add_blackList_tip),
                R.drawable.delete_search,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addBlackList();
                    }
                });
    }

    /**
     * 设置备注
     */
    private void setMark() {
        startIView(InputUIView.build(new InputUIView.InputConfigCallback() {
            @Override
            public TitleBarPattern initTitleBar(TitleBarPattern titleBarPattern) {
                return super.initTitleBar(titleBarPattern)
                        .setTitleString(mActivity, R.string.set_mark_title)
                        .addRightItem(TitleBarPattern.TitleBarItem.build(getString(R.string.finish), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String string = mExEditText.string();
                                if (TextUtils.isEmpty(string)) {
                                    Anim.band(mExEditText);
                                    return;
                                }
                                finishIView(mIView);
                                add(RRetrofit.create(ContactService.class)
                                        .setMark(Param.buildMap("to_uid:" + to_uid,
                                                "mark:" + string))
                                        .compose(Rx.transformer(String.class))
                                        .subscribe(new BaseSingleSubscriber<String>() {

                                            @Override
                                            public void onSucceed(String bean) {
                                                mUserInfoBean.setContact_remark(string);
                                                T_.show(bean);
                                            }
                                        }));
                            }
                        }));
            }

            @Override
            public void initInputView(RBaseViewHolder holder, ExEditText editText, ItemRecyclerUIView.ViewItemInfo bean) {
                super.initInputView(holder, editText, bean);
                TextView tipView = holder.v(R.id.input_tip_view);
                tipView.setVisibility(View.VISIBLE);
                tipView.setText(R.string.set_mark_tip);

                String mark = mUserInfoBean.getContact_remark();
                if (!TextUtils.isEmpty(mark)) {
                    setInputText(mark);
//                    mExEditText.setSelection(2, mark.length() + 2);
//                    mExEditText.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            showSoftInput(mExEditText);
//                        }
//                    }, DEFAULT_ANIM_TIME);
                }
            }
        }));
    }
}
