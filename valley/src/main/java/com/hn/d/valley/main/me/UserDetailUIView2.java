package com.hn.d.valley.main.me;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.design.StickLayout;
import com.angcyo.uiview.design.StickLayout2;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.github.tablayout.CommonTabLayout;
import com.angcyo.uiview.github.tablayout.TabEntity;
import com.angcyo.uiview.github.tablayout.TabLayoutUtil;
import com.angcyo.uiview.github.tablayout.listener.CustomTabEntity;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.view.IView;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RTextView;
import com.angcyo.uiview.widget.viewpager.UIPagerAdapter;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.helper.AudioPlayHelper;
import com.hn.d.valley.main.home.circle.CircleUIView;
import com.hn.d.valley.main.me.setting.DynamicPermissionUIView;
import com.hn.d.valley.main.me.setting.EditInfoUIView;
import com.hn.d.valley.main.me.sub.UserDetailMoreUIView;
import com.hn.d.valley.main.me.sub.UserInfoSubUIView;
import com.hn.d.valley.main.message.audio.BaseAudioControl;
import com.hn.d.valley.main.message.audio.Playable;
import com.hn.d.valley.main.message.service.SessionHelper;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.other.InputUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.user.ReportUIView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.ArrayList;
import java.util.List;

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

    boolean isFollower = false;
    TextView mCommandItemView;
    private String to_uid;
    private CommonTabLayout mCommonTabLayout;
    private UIViewPager mViewPager;
    private UserInfoBean mUserInfoBean;

    private AudioPlayHelper mAudioPlayHelper;

    public UserDetailUIView2(String to_uid) {
        this.to_uid = to_uid;
    }

    public UserDetailUIView2() {
        this(UserCache.getUserAccount());
    }

    /**
     * 命令按钮
     */
    public static void initCommandView(final TextView commandView, final UserInfoBean userInfoBean,
                                       final ILayout iLayout, final CompositeSubscription subscription) {
        final String to_uid = userInfoBean.getUid();
        final String uid = UserCache.getUserAccount();
        if (isMe(to_uid)) {
            commandView.setVisibility(View.GONE);
        } else {
            commandView.setVisibility(View.VISIBLE);
            if (userInfoBean.getIs_attention() == 1) {
                //已关注
                if (userInfoBean.getIs_contact() == 1) {
                    //是联系人
                    commandView.setText(R.string.send_message);
                    //commandView.setImageResource(R.drawable.send_message_selector);
                    commandView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SessionHelper.startP2PSession(iLayout, to_uid, SessionTypeEnum.P2P);
                        }
                    });
                } else {
                    //不是联系人
                    //commandView.setImageResource(R.drawable.add_contact2_selector);
                    commandView.setText(R.string.add_friend);
                    commandView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onAddFriend(iLayout, userInfoBean, subscription);
                        }
                    });
                }
            } else {
                //未关注
                // commandView.setImageResource(R.drawable.attention_selector);
                commandView.setText(R.string.add_follow);
                commandView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscription.add(RRetrofit.create(UserInfoService.class)
                                .attention(Param.buildMap("to_uid:" + uid, "to_uid:" + to_uid))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onSucceed(String bean) {
                                        T_.show(commandView.getResources().getString(R.string.attention_successed_tip));
                                        userInfoBean.setIs_attention(1);
                                        initCommandView(commandView, userInfoBean, iLayout, subscription);
                                    }
                                }));
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
                                subscription.add(RRetrofit.create(UserInfoService.class)
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

    @Override
    protected TitleBarPattern getTitleBar() {
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
                startIView(new EditInfoUIView(MeUIView2.initPhotos(mUserInfoBean), new Action0() {
                    @Override
                    public void call() {
                        setTitleString(UserCache.instance().getUserInfoBean().getUsername());
                    }
                }));
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
        }
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_user_detail_layout2);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        mAudioPlayHelper = new AudioPlayHelper(mActivity);

        StickLayout2 stickLayout = mViewHolder.v(R.id.stick_layout);
        stickLayout.setFloatTopOffset((int) getTitleBarHeight());
        mCommandItemView = mViewHolder.v(R.id.command_item_view);
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
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        add(RRetrofit.create(UserInfoService.class)
                .userInfo(Param.buildMap("to_uid:" + to_uid))
                .compose(Rx.transformer(UserInfoBean.class))
                .subscribe(new RSubscriber<UserInfoBean>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        T_.error(msg);
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

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                    }
                })
        );

        if (!isMe(to_uid)) {
            add(RRetrofit.create(ContactService.class)
                    .getRelationship(Param.buildMap("to_uid:" + to_uid))
                    .compose(Rx.transformer(Integer.class))
                    .subscribe(new BaseSingleSubscriber<Integer>() {
                        @Override
                        public void onSucceed(Integer bean) {
                            super.onSucceed(bean);
                            isFollower = bean == 6;
                        }
                    }));
        }
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(2);
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
                    UserInfoSubUIView userInfoSubUIView = new UserInfoSubUIView(mUserInfoBean);
                    userInfoSubUIView.bindOtherILayout(mOtherILayout);
                    return userInfoSubUIView;
                } else if (position == 1) {
                    CircleUIView circleUIView = new CircleUIView(mUserInfoBean.getUid());
                    circleUIView.bindOtherILayout(mOtherILayout);
                    circleUIView.setInSubUIView(true);
                    return circleUIView;
                } else {
                    MyAlbumUIView myAlbumUIView = new MyAlbumUIView(mUserInfoBean.getUid());
                    myAlbumUIView.bindOtherILayout(mOtherILayout);
                    return myAlbumUIView;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        mViewPager.setCurrentItem(1);
    }

    private void initTabLayout() {
        mCommonTabLayout = mViewHolder.v(R.id.tab_layout);
        List<CustomTabEntity> tabs = new ArrayList<>();
        tabs.add(new TabEntity(getString(R.string.info)));
        tabs.add(new TabEntity(getString(R.string.status)));
        tabs.add(new TabEntity(getString(R.string.photo)));
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

        mCommonTabLayout.setCurrentTab(1);
        TabLayoutUtil.setCommonTabDivider(mCommonTabLayout,
                getColor(R.color.line_color),
                LinearLayout.SHOW_DIVIDER_MIDDLE, getDimensionPixelOffset(R.dimen.base_hdpi));

        mCommonTabLayout.setBackgroundResource(R.drawable.base_dark_border);
    }

    private void initView(UserInfoBean bean) {
        mUserInfoBean = bean;
        String is_auth = bean.getIs_auth();

        HnGlideImageView hnGlideImageView = mViewHolder.v(R.id.user_ico_view);
        hnGlideImageView.setImageThumbUrl(bean.getAvatar());
        hnGlideImageView.setAuth(is_auth);
        hnGlideImageView.setShowBorder(true);

        setTitleString(mUserInfoBean.getUsername());
        //getUITitleBarContainer().setBackgroundColor(Color.TRANSPARENT);
        mViewHolder.fillView(mUserInfoBean);

        RTextView authTextView = mViewHolder.v(R.id.auth_desc_tview);
        //是否已认证【0-未认证，1-已认证，2-认证中-查看自己信息才会有，3-认证失败-查看自己信息才会有，以前没有认证成功过才会有该值】
        if ("1".equalsIgnoreCase(is_auth)) {
            authTextView.setText(bean.getAuth_desc());
            authTextView.setBackgroundColor(Color.TRANSPARENT);
            authTextView.setTextColor(getColor(R.color.base_text_color_dark));
        } else {
            authTextView.setText(R.string.not_auth);
            authTextView.setBackground(SkinHelper.getSkin().getThemeMaskBackgroundRoundSelector());
            authTextView.setTextColor(Color.WHITE);
        }

        initCommandView(mCommandItemView, mUserInfoBean, mILayout, mSubscriptions);
        //语音介绍
        initVoiceView();

        if (isMe(to_uid)) {
            getUITitleBarContainer().showRightItem(1);
        } else {
            getUITitleBarContainer().showRightItem(0);
        }
    }

    private void initVoiceView() {
        LinearLayout controlLayout = mViewHolder.v(R.id.voice_control_layout);
        final ImageView voicePlayView = mViewHolder.v(R.id.voice_play_view);
        final TextView voiceTimeView = mViewHolder.v(R.id.voice_time_view);
        voiceTimeView.setTextColor(SkinHelper.getSkin().getThemeSubColor());

        if (TextUtils.isEmpty(mUserInfoBean.getVoice_introduce())) {
            controlLayout.setVisibility(View.GONE);
        } else {
            controlLayout.setVisibility(View.VISIBLE);
            mAudioPlayHelper.initPlayImageView(voicePlayView);

            voiceTimeView.setText(mUserInfoBean.getVoiceTime());

            controlLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAudioPlayHelper.playAudio(mUserInfoBean.getVoiceUrl(), mUserInfoBean.getVoiceDuration(), new BaseAudioControl.AudioControlListener() {
                        @Override
                        public void onAudioControllerReady(Playable playable) {

                        }

                        @Override
                        public void onEndPlay(Playable playable) {
                            initVoiceView();
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
        startIView(new UserDetailMoreUIView(mUserInfoBean));
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
                .showDialog(mOtherILayout);
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
                .showDialog(mOtherILayout);
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
                .showDialog(mOtherILayout);
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
                .showDialog(mOtherILayout);

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
        add(RRetrofit.create(UserInfoService.class)
                .unAttention(Param.buildMap("to_uid:" + to_uid))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onSucceed(String bean) {
                        mUserInfoBean.setIs_attention(0);
                        initCommandView(mCommandItemView, mUserInfoBean, mILayout, mSubscriptions);
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
                        initCommandView(mCommandItemView, mUserInfoBean, mILayout, mSubscriptions);
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
