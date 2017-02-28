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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.github.pickerview.DateDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RMaxAdapter;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.NewestDiscussPicBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.ChatUIView;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.other.FansRecyclerUIView;
import com.hn.d.valley.sub.other.FollowersRecyclerUIView;
import com.hn.d.valley.sub.other.InputUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnIcoRecyclerView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
public class UserDetailUIView extends BaseContentUIView {

    String to_uid;
    @BindView(R.id.scroll_root_layout)
    NestedScrollView mScrollRootLayout;
    @BindView(R.id.ico_recycler_view)
    HnIcoRecyclerView mIcoRecyclerView;
    @BindView(R.id.view_pager_placeholder_view)
    ImageView mViewPagerPlaceholderView;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.single_text_indicator_view)
    TextIndicator mSingleTextIndicatorView;
    @BindView(R.id.command_item_view)
    TextView mCommandItemView;
    @BindView(R.id.auth_desc)
    TextView mAuthDesc;
    private UserInfoBean mUserInfoBean;

    public UserDetailUIView(String to_uid) {
        this.to_uid = to_uid;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_user_detail_layout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleHide(true)
                .setFloating(true)
                .setShowBackImageView(true)
                .addRightItem(TitleBarPattern.TitleBarItem.build(R.drawable.more, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        T_.show("更多");
                    }
                }).setVisibility(View.GONE));
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
                        }
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                    }
                })
        );
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
        final RMaxAdapter<HnIcoRecyclerView.IcoInfo> maxAdapter = mIcoRecyclerView.getMaxAdapter();
        maxAdapter.setMaxcount(3);
        List<HnIcoRecyclerView.IcoInfo> infos = new ArrayList<>();
        for (NewestDiscussPicBean picBean : mUserInfoBean.getNewest_discuss_pic()) {
            if (3 == (picBean.getMedia_type())) {
                infos.add(new HnIcoRecyclerView.IcoInfo(to_uid, picBean.getUrl()));
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

    @OnClick({R.id.qr_code_view, R.id.follow_item_layout, R.id.follower_item_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qr_code_view://二维码
                break;
            case R.id.follow_item_layout://关注
                if (UserCache.getUserAccount().equalsIgnoreCase(mUserInfoBean.getUid())) {
                    mOtherILayout.startIView(new FollowersRecyclerUIView(mUserInfoBean.getUid()));
                } else {
                    T_.error("不支持查看");
                }
                break;
            case R.id.follower_item_layout://粉丝
                if (UserCache.getUserAccount().equalsIgnoreCase(mUserInfoBean.getUid())) {
                    mOtherILayout.startIView(new FansRecyclerUIView(mUserInfoBean.getUid()));
                } else {
                    T_.error("不支持查看");
                }
                break;
        }
    }

    private void initCommandView() {
        final String to_uid = mUserInfoBean.getUid();
        final String uid = UserCache.getUserAccount();
        if (TextUtils.equals(uid, to_uid)) {
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
                            ChatUIView.start(getILayout(), to_uid, SessionTypeEnum.P2P);
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
                        add(RRetrofit.create(UserInfoService.class)
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
                                add(RRetrofit.create(UserInfoService.class)
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
}
