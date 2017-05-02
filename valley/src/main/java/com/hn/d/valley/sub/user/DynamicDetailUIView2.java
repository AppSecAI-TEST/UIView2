package com.hn.d.valley.sub.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.design.StickLayout2;
import com.angcyo.uiview.github.tablayout.SlidingTabLayout;
import com.angcyo.uiview.github.tablayout.TabLayoutUtil;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RMaxAdapter;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.view.IView;
import com.angcyo.uiview.widget.viewpager.FadeInOutPageTransformer;
import com.angcyo.uiview.widget.viewpager.UIPagerAdapter;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.service.DiscussService;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.sub.other.LikeUserRecyclerUIView;
import com.hn.d.valley.sub.user.dialog.DynamicShareDialog;
import com.hn.d.valley.sub.user.sub.CommentInputDialog;
import com.hn.d.valley.sub.user.sub.CommentListUIView;
import com.hn.d.valley.sub.user.sub.ForwardListUIView;
import com.hn.d.valley.widget.HnIcoRecyclerView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：动态详情2
 * 创建人员：Robi
 * 创建时间：2017/01/13 19:27
 * 修改人员：Robi
 * 修改时间：2017/01/13 19:27
 * 修改备注：
 * Version: 1.0.0
 */
public class DynamicDetailUIView2 extends BaseContentUIView {

    UserDiscussListBean.DataListBean mDataListBean;
    /**
     * 动态id
     */
    private String discuss_id;
    /**
     * 点赞头像列表
     */
    private HnIcoRecyclerView mIcoRecyclerView;
    private UIViewPager mUiViewPager;
    private SlidingTabLayout mTabLayout;
    private TextView userCountView;
    private View likeUserControlLayout;
    private CommentListUIView mCommentListUIView;
    private ForwardListUIView mForwardListUIView;

    public DynamicDetailUIView2(String discuss_id) {
        this.discuss_id = discuss_id;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(mActivity, R.string.dynamic_detail)
                .setShowBackImageView(true)
                .addRightItem(TitleBarPattern.buildImage(R.drawable.more,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showShareDialog();
                            }
                        }).setVisibility(View.GONE))
                ;
    }

    private void showShareDialog() {
        startIView(new DynamicShareDialog(mDataListBean, mSubscriptions));
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_dynamic_detail2);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mUiViewPager = mViewHolder.v(R.id.view_pager);
        mTabLayout = mViewHolder.v(R.id.tab_layout);
        userCountView = mViewHolder.tv(R.id.like_user_count_view);
        mIcoRecyclerView = mViewHolder.v(R.id.like_user_recycler_view);
        likeUserControlLayout = mViewHolder.v(R.id.like_users_layout);

        StickLayout2 stickLayout2 = mViewHolder.v(R.id.stick_layout);
        stickLayout2.setEdgeScroll(true);

        updateSkin();
    }

    @Override
    protected void OnShowContentLayout() {
        super.OnShowContentLayout();
        if (mDataListBean != null) {
            UserDiscussItemControl.initItem(mSubscriptions, mViewHolder, mDataListBean, null, null, mILayout);
            //转发, 点赞, 评论按钮布局隐藏
            mViewHolder.v(R.id.function_control_layout).setVisibility(View.GONE);

            //点赞列表
            List<LikeUserInfoBean> like_users = mDataListBean.getLike_users();

            userCountView.setText(mDataListBean.getLike_cnt());
            if (like_users != null && !like_users.isEmpty()) {
                likeUserControlLayout.setVisibility(View.VISIBLE);

                List<HnIcoRecyclerView.IcoInfo> infos = new ArrayList<>();
                for (LikeUserInfoBean infoBean : like_users) {
                    infos.add(new HnIcoRecyclerView.IcoInfo(infoBean.getUid(), infoBean.getAvatar()));
                }
                mIcoRecyclerView.getMaxAdapter().resetData(infos);
            }

            UserDiscussItemControl.bindLikeItemView(mSubscriptions, mViewHolder, mDataListBean, new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    if (mIcoRecyclerView != null) {
                        RMaxAdapter<HnIcoRecyclerView.IcoInfo> maxAdapter = mIcoRecyclerView.getMaxAdapter();
                        HnIcoRecyclerView.IcoInfo icoInfo = new HnIcoRecyclerView.IcoInfo(UserCache.getUserAccount(),
                                UserCache.getUserAvatar());
                        int itemRawCount = Integer.parseInt(mDataListBean.getLike_cnt());
                        if (aBoolean) {
                            maxAdapter.addLastItem(icoInfo);
                            itemRawCount++;
                            userCountView.setText(itemRawCount + "");
                        } else {
                            maxAdapter.deleteItem(icoInfo);
                            itemRawCount--;
                            userCountView.setText(itemRawCount + "");
                        }

                        likeUserControlLayout.setVisibility(itemRawCount > 0 ? View.VISIBLE : View.GONE);
                    }
                }
            });

            //点赞人数列表
            mViewHolder.v(R.id.click_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new LikeUserRecyclerUIView(mDataListBean.getDiscuss_id()));
                }
            });

            initViewPager();
            //Tab
            initTabLayout();

            //点击打开评论对话框
            mViewHolder.v(R.id.input_tip_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new CommentInputDialog(new CommentInputDialog.InputConfig() {
                        @Override
                        public void onInitDialogLayout(RBaseViewHolder viewHolder) {

                        }

                        @Override
                        public void onSendClick(String imagePath, String content) {
                            comment(imagePath, content);
                        }
                    }));
                }
            });

            //转发按钮
            mViewHolder.v(R.id.bottom_forward_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    private void initTabLayout() {
        TabLayoutUtil.initSlidingTab(mTabLayout, mUiViewPager, new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {

            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void initViewPager() {
        mUiViewPager.setPageTransformer(true, new FadeInOutPageTransformer());
        mUiViewPager.setAdapter(new UIPagerAdapter() {
            @Override
            protected IView getIView(int position) {
                if (position == 0) {
                    if (mCommentListUIView == null) {
                        mCommentListUIView = new CommentListUIView(mDataListBean.getDiscuss_id());
                        mCommentListUIView.bindOtherILayout(mOtherILayout);
                    }
                    return mCommentListUIView;
                }
                if (mForwardListUIView == null) {
                    mForwardListUIView = new ForwardListUIView(mDataListBean.getDiscuss_id());
                    mForwardListUIView.bindOtherILayout(mOtherILayout);
                }
                return mForwardListUIView;
            }


            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return getString(R.string.comment) + " " + mDataListBean.getComment_cnt();
                }
                return getString(R.string.forward) + " " + mDataListBean.getForward_cnt();
            }
        });
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadData();
    }

    /**
     * 拉取动态详情
     */
    private void loadData() {
        add(RRetrofit.create(DiscussService.class)
                .detail(Param.buildMap("discuss_id:" + discuss_id))
                .compose(Rx.transformer(UserDiscussListBean.DataListBean.class))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoadView();
                    }
                })
                .subscribe(new BaseSingleSubscriber<UserDiscussListBean.DataListBean>() {
                    @Override
                    public void onSucceed(UserDiscussListBean.DataListBean bean) {
                        super.onSucceed(bean);
                        if (bean == null) {
                            showEmptyLayout();
                        } else {
                            mDataListBean = bean;
                            getUITitleBarContainer().showRightItem(0);
                            showContentLayout();
                        }
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        showNonetLayout(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadData();
                            }
                        });
                    }
                })
        );
    }

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);
        if (mTabLayout != null) {
            mTabLayout.setIndicatorColor(SkinHelper.getSkin().getThemeSubColor());
        }
    }

    /**
     * 发布评论
     */
    private void comment(String imagePath, String content) {
        add(RRetrofit.create(SocialService.class)
                .comment(Param.buildMap("type:discuss", "item_id:" + discuss_id, "content:" + content, "images:" + imagePath))
                .compose(Rx.transformer(String.class))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoadView();
                    }
                })
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onSucceed(String bean) {
                        T_.show(bean);
                        if (mCommentListUIView != null) {
                            mCommentListUIView.loadData();
                            mCommentListUIView.scrollToTop();
                        }
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                    }
                }));
    }
}
