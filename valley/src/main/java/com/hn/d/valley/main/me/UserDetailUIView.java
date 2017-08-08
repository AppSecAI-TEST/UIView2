package com.hn.d.valley.main.me;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.github.pickerview.DateDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RMaxAdapter;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.IcoInfoBean;
import com.hn.d.valley.bean.realm.NewestDiscussPicBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.setting.DynamicPermissionUIView;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.sub.other.FansRecyclerUIView;
import com.hn.d.valley.sub.other.FollowersRecyclerUIView;
import com.hn.d.valley.sub.other.InputUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.user.ReportUIView;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnIcoRecyclerView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：用户详情界面
 * 创建人员：Robi
 * 创建时间：2017/01/17 10:30
 * 修改人员：Robi
 * 修改时间：2017/01/17 10:30
 * 修改备注：
 * Version: 1.0.0
 */
@Deprecated
public class UserDetailUIView extends BaseContentUIView {

    String to_uid;
    //    @BindView(R.id.scroll_root_layout)
    NestedScrollView mScrollRootLayout;
    //    @BindView(R.id.ico_recycler_view)
    HnIcoRecyclerView mIcoRecyclerView;
    //    @BindView(R.id.view_pager_placeholder_view)
    ImageView mViewPagerPlaceholderView;
    //    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    //    @BindView(R.id.single_text_indicator_view)
    TextIndicator mSingleTextIndicatorView;
    //    @BindView(R.id.command_item_view)
    TextView mCommandItemView;
    //    @BindView(R.id.auth_desc)
    TextView mAuthDesc;
    /**
     * 是否是粉丝
     */
    boolean isFollower = false;
    private UserInfoBean mUserInfoBean;

    public UserDetailUIView(String to_uid) {
        this.to_uid = to_uid;
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_user_detail_layout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        TitleBarPattern titleBarPattern = super.getTitleBar()
                .setTitleHide(true)
                .setFloating(true)
                .setShowBackImageView(true)
                .setBackImageRes(R.drawable.back_2);

        if (!isMe()) {
            titleBarPattern.addRightItem(TitleBarPattern.TitleBarItem.build(R.drawable.more, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //T_.show("更多");
                    showBottomDialog();
                }
            }).setVisibility(View.GONE));
        }

        return titleBarPattern;
    }

    private boolean isMe() {
        return TextUtils.equals(to_uid, UserCache.getUserAccount());
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        mScrollRootLayout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                getUITitleBarContainer().evaluateBackgroundColorSelf(scrollY);
            }
        });
        mViewPagerPlaceholderView.setVisibility(View.VISIBLE);

        getUITitleBarContainer().showRightItem(0);
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
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
                        }
                    }
                })
        );

        if (!isMe()) {
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

    private void initView(UserInfoBean bean) {
        mUserInfoBean = bean;
        mViewPagerPlaceholderView.setVisibility(View.GONE);

        setTitleString(mUserInfoBean.getUsername());
        getUITitleBarContainer().setBackgroundColor(Color.TRANSPARENT);
        mViewHolder.fillView(mUserInfoBean);
        //性别
        ItemInfoLayout sexItem = mViewHolder.v(R.id.sex_item);
        if ("1".equalsIgnoreCase(mUserInfoBean.getSex())) {
            sexItem.setItemDarkText(getString(R.string.man));
        } else if ("2".equalsIgnoreCase(mUserInfoBean.getSex())) {
            sexItem.setItemDarkText(getString(R.string.women));
        } else {
            sexItem.setItemDarkText(getString(R.string.secret));
        }

        //区域
        ItemInfoLayout addressItem = mViewHolder.v(R.id.address_item);
        addressItem.setItemDarkText(mUserInfoBean.getProvince_name() + " " + mUserInfoBean.getCity_name() + " " + mUserInfoBean.getCounty_name());

        //认证
        if ("1".equalsIgnoreCase(mUserInfoBean.getIs_auth())) {
            mAuthDesc.setVisibility(View.VISIBLE);
            mAuthDesc.setText(mUserInfoBean.getAuth_desc());
        } else {
            mAuthDesc.setVisibility(View.GONE);
        }

        //动态
        final RMaxAdapter<IcoInfoBean> maxAdapter = mIcoRecyclerView.getMaxAdapter();
        maxAdapter.setMaxCount(3);
        List<IcoInfoBean> infos = new ArrayList<>();
        for (NewestDiscussPicBean picBean : mUserInfoBean.getNewest_discuss_pic()) {
            if (3 == (picBean.getMedia_type())) {
                infos.add(new IcoInfoBean(to_uid, picBean.getUrl()));
            }
        }

        /*动态最后3个媒体*/
        mIcoRecyclerView.getMaxAdapter().resetData(infos);

        /*照片墙*/
        ArrayList<String> photos = new ArrayList<>();
        photos.add(mUserInfoBean.getAvatar());
        photos.addAll(RUtils.split(mUserInfoBean.getPhotos()));
        PhotoPager.init(mILayout, mSingleTextIndicatorView, mViewPager, photos);

        initCommandView();

        //等级
        ItemInfoLayout gradeItem = mViewHolder.v(R.id.grade_item);
        gradeItem.setItemDarkText(bean.getGrade());

        //昵称
        ItemInfoLayout item = mViewHolder.v(R.id.username_item);
        item.setItemDarkText(bean.getUsername());

        //电话
        item = mViewHolder.v(R.id.phone_item);
        String phone = bean.getPhone();
        if (!TextUtils.isEmpty(phone) && phone.length() > 4) {
            item.setItemDarkText(phone.substring(0, phone.length() - 4) + "****");
        }

        //个性签名
        item = mViewHolder.v(R.id.signature_item);
        item.setItemDarkText(bean.getSignature());

        //id
        item = mViewHolder.v(R.id.id_item);
        item.setItemDarkText(bean.getUid());

        item = mViewHolder.v(R.id.birthday_item);
        String birthday = bean.getBirthday();
        if (TextUtils.isEmpty(birthday)) {
            item.setItemDarkText(getString(R.string.secret));
        } else {
            item.setItemDarkText(getString(R.string.birthday_format, DateDialog.getBirthday(birthday)));
        }

        //语音介绍
        ItemInfoLayout infoLayout = mViewHolder.v(R.id.voice_layout);
        LinearLayout controlLayout = mViewHolder.v(R.id.voice_control_layout);
        if (TextUtils.isEmpty(bean.getVoice_introduce())) {
            infoLayout.setItemDarkText(mActivity.getString(R.string.nothing));
        } else {
            controlLayout.setVisibility(View.VISIBLE);
        }
    }

    //    @OnClick({R.id.qr_code_view, R.id.follow_item_layout, R.id.follower_item_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qr_code_view://二维码
                break;
            case R.id.follow_item_layout://关注
                if (UserCache.getUserAccount().equalsIgnoreCase(mUserInfoBean.getUid())) {
                    mParentILayout.startIView(new FollowersRecyclerUIView(mUserInfoBean.getUid()));
                } else {
                    if (mUserInfoBean.getLook_fans() == 1) {
                        mParentILayout.startIView(new FollowersRecyclerUIView(mUserInfoBean.getUid()));
                    } else {
                        T_.error("对方不允许您查看.");
                    }
                }
                break;
            case R.id.follower_item_layout://粉丝
                if (UserCache.getUserAccount().equalsIgnoreCase(mUserInfoBean.getUid())) {
                    mParentILayout.startIView(new FansRecyclerUIView(mUserInfoBean.getUid()));
                } else {
                    if (mUserInfoBean.getLook_fans() == 1) {
                        mParentILayout.startIView(new FansRecyclerUIView(mUserInfoBean.getUid()));
                    } else {
                        T_.error("对方不允许您查看.");
                    }
                }
                break;
        }
    }

    /**
     * 命令按钮
     */
    private void initCommandView() {
        final String to_uid = mUserInfoBean.getUid();
        final String uid = UserCache.getUserAccount();
        if (isMe()) {
            mCommandItemView.setVisibility(View.GONE);
        } else {
            mCommandItemView.setVisibility(View.VISIBLE);
            if (mUserInfoBean.getIs_attention() == 1) {
                //已关注
                if (mUserInfoBean.getIs_contact() == 1) {
                    //是联系人
                    mCommandItemView.setText(R.string.send_message);
                    //mCommandItemView.setImageResource(R.drawable.send_message_selector);
                    mCommandItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SessionHelper.startP2PSession(getILayout(), to_uid, SessionTypeEnum.P2P);
                        }
                    });
                } else {
                    //不是联系人
                    //mCommandItemView.setImageResource(R.drawable.add_contact2_selector);
                    mCommandItemView.setText(R.string.add_friend);
                    mCommandItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onAddFriend();
                        }
                    });
                }
            } else {
                //未关注
                // mCommandItemView.setImageResource(R.drawable.attention_selector);
                mCommandItemView.setText(R.string.add_follow);
                mCommandItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add(RRetrofit.create(UserService.class)
                                .attention(Param.buildMap("uid:" + uid, "to_uid:" + to_uid))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onSucceed(String bean) {
                                        T_.show(getString(R.string.attention_successed_tip));
                                        mUserInfoBean.setIs_attention(1);
                                        initCommandView();
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
    private void onAddFriend() {
        startIView(InputUIView.build(new InputUIView.InputConfigCallback() {
            @Override
            public TitleBarPattern initTitleBar(TitleBarPattern titleBarPattern) {
                return super.initTitleBar(titleBarPattern)
                        .setTitleString(mActivity, R.string.add_friend)
                        .addRightItem(TitleBarPattern.TitleBarItem.build(getString(R.string.send), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finishIView(mIView);
                                add(RRetrofit.create(UserService.class)
                                        .addContact(Param.buildMap("to_uid:" + to_uid,
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
                    setInputText(getString(R.string.add_friend_format, username));
                    mExEditText.setSelection(2, username.length() + 2);
                    mExEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showSoftInput(mExEditText);
                        }
                    }, DEFAULT_ANIM_TIME);
                }
            }
        }));
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

    /**
     * 更多弹窗
     */
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
                        initCommandView();
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
                        initCommandView();
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
}
