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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RMaxAdapter;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.RTextView;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.NewestDiscussPicBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.ChatUIView;
import com.hn.d.valley.sub.other.FansRecyclerUIView;
import com.hn.d.valley.sub.other.FollowersRecyclerUIView;
import com.hn.d.valley.sub.user.service.UserInfoService;
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
 * 类的描述：
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
    @BindView(R.id.sex_text_view)
    RTextView mSexTextView;
    @BindView(R.id.address_text_view)
    RTextView mAddressTextView;
    @BindView(R.id.auth_desc)
    TextView mAuthDesc;
    @BindView(R.id.view_pager_placeholder_view)
    ImageView mViewPagerPlaceholderView;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.single_text_indicator_view)
    TextIndicator mSingleTextIndicatorView;
    @BindView(R.id.command_item_view)
    ImageView mCommandItemView;
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
                }));
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
                    public void onNext(UserInfoBean bean) {
                        super.onNext(bean);
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
        if ("1".equalsIgnoreCase(mUserInfoBean.getSex())) {
            mSexTextView.setText(R.string.man);
        } else if ("2".equalsIgnoreCase(mUserInfoBean.getSex())) {
            mSexTextView.setText(R.string.women);
        } else {
            mSexTextView.setText(R.string.secret);
        }
        mAddressTextView.setText(mUserInfoBean.getProvince_name(), mUserInfoBean.getCity_name(), mUserInfoBean.getCounty_name());
        if ("1".equalsIgnoreCase(mUserInfoBean.getIs_auth())) {
            mAuthDesc.setVisibility(View.VISIBLE);
        } else {
            mAuthDesc.setVisibility(View.GONE);
        }

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
    }

    /**
     * 动态详情
     */
    @OnClick(R.id.status_layout)
    public void onStatusClick() {

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
                    mCommandItemView.setImageResource(R.drawable.send_message_selector);
                    mCommandItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ChatUIView.start(getILayout(), to_uid, SessionTypeEnum.P2P);
                        }
                    });
                } else {
                    //不是联系人
                    mCommandItemView.setImageResource(R.drawable.add_contact2_selector);
                    mCommandItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            add(RRetrofit.create(UserInfoService.class)
                                    .addContact(Param.buildMap("uid:" + uid, "to_uid:" + to_uid,
                                            "tip:" + mActivity.getResources().getString(R.string.add_contact_tip,
                                                    UserCache.instance().getUserInfoBean().getUsername())))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {

                                        @Override
                                        public void onNext(String bean) {
                                            T_.show(bean);
                                        }
                                    }));
                        }
                    });
                }
            } else {
                //未关注
                mCommandItemView.setImageResource(R.drawable.attention_selector);
                mCommandItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add(RRetrofit.create(UserInfoService.class)
                                .attention(Param.buildMap("uid:" + uid, "to_uid:" + to_uid))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onNext(String bean) {
                                        T_.show(bean);
                                        mUserInfoBean.setIs_attention(1);
                                        initCommandView();
                                    }
                                }));
                    }
                });
            }
        }
    }
}
