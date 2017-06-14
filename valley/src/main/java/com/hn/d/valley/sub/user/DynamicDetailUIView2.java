package com.hn.d.valley.sub.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.design.StickLayout2;
import com.angcyo.uiview.github.tablayout.SlidingTabLayout;
import com.angcyo.uiview.github.tablayout.TabLayoutUtil;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.github.utilcode.utils.NetworkUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.receiver.NetworkStateReceiver;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RMaxAdapter;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.view.IView;
import com.angcyo.uiview.widget.RNineImageLayout;
import com.angcyo.uiview.widget.viewpager.FadeInOutPageTransformer;
import com.angcyo.uiview.widget.viewpager.UIPagerAdapter;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.example.m3b.Audio;
import com.example.m3b.audiocachedemo.Player;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.bean.realm.IcoInfoBean;
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
import com.hn.d.valley.widget.HnPlayTimeView;
import com.hn.d.valley.widget.HnVideoPlayView;
import com.hn.d.valley.widget.HnVoiceTipView;

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
    private Player.OnPlayListener mOnPlayListener;

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
    public void onViewUnload() {
        super.onViewUnload();
        Audio.instance().stop();
        Audio.instance().removeOnPlayListener(mOnPlayListener);
    }

    @Override
    protected void OnShowContentLayout() {
        super.OnShowContentLayout();
        if (mDataListBean != null) {
            //媒体内容
            initMediaLayout();

            //转发, 点赞, 评论按钮布局隐藏
            mViewHolder.v(R.id.function_control_layout).setVisibility(View.GONE);

            //点赞列表
            List<LikeUserInfoBean> like_users = mDataListBean.getLike_users();

            userCountView.setText(mDataListBean.getLike_cnt());
            if (like_users != null && !like_users.isEmpty()) {
                likeUserControlLayout.setVisibility(View.VISIBLE);

                List<IcoInfoBean> infos = new ArrayList<>();
                for (LikeUserInfoBean infoBean : like_users) {
                    infos.add(new IcoInfoBean(infoBean.getUid(), infoBean.getAvatar()));
                }
                mIcoRecyclerView.getMaxAdapter().resetData(infos);
            }

            //点赞按钮
            UserDiscussItemControl.bindLikeItemView(mSubscriptions, mViewHolder, mDataListBean, new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    if (mIcoRecyclerView != null) {
                        RMaxAdapter<IcoInfoBean> maxAdapter = mIcoRecyclerView.getMaxAdapter();
                        IcoInfoBean IcoInfoBean = new IcoInfoBean(UserCache.getUserAccount(),
                                UserCache.getUserAvatar());
                        int itemRawCount = Integer.parseInt(mDataListBean.getLike_cnt());
                        if (aBoolean) {
                            maxAdapter.addLastItem(IcoInfoBean);
                            itemRawCount++;
                            userCountView.setText(itemRawCount + "");
                        } else {
                            maxAdapter.deleteItem(IcoInfoBean);
                            itemRawCount--;
                            userCountView.setText(itemRawCount + "");
                        }

                        likeUserControlLayout.setVisibility(itemRawCount > 0 ? View.VISIBLE : View.GONE);
                    }
                }
            }, true);

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

            //转发按钮,语音不允许转发
            if (mDataListBean.isVoiceMediaType()) {
                mViewHolder.v(R.id.bottom_forward_item).setVisibility(View.GONE);
            }

            mViewHolder.v(R.id.bottom_forward_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new PublishDynamicUIView2(mDataListBean));
                }
            });

            //收藏按钮
            initCollectView();
        }
    }

    private void initCollectView() {
        ImageView imageView = mViewHolder.v(R.id.collect_view);
        if (mDataListBean.getIs_collect() == 1) {
            imageView.setImageResource(R.drawable.share_shouchang_n);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add(RRetrofit.create(SocialService.class)
                            .unCollect(Param.buildMap("type:discuss", "item_id:" + mDataListBean.getDiscuss_id()))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {
                                @Override
                                public void onSucceed(String bean) {
                                    super.onSucceed(bean);
                                    T_.show(bean);
                                    mDataListBean.setIs_collect(0);
                                    initCollectView();
                                }
                            }));
                }
            });
        } else {
            imageView.setImageResource(R.drawable.share_shouchang_n);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add(RRetrofit.create(SocialService.class)
                            .collect(Param.buildMap("type:discuss", "item_id:" + mDataListBean.getDiscuss_id()))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {
                                @Override
                                public void onSucceed(String bean) {
                                    super.onSucceed(bean);
                                    T_.show(bean);
                                    mDataListBean.setIs_collect(1);
                                    initCollectView();
                                }
                            }));
                }
            });
        }
    }

    private void initMediaLayout() {
        if (isAudioType()) {
            View mediaControlLayout = mViewHolder.v(R.id.media_control_layout);

            RNineImageLayout mediaImageTypeView = (RNineImageLayout) mediaControlLayout.findViewById(R.id.media_image_view);
            HnPlayTimeView videoTimeView = (HnPlayTimeView) mediaControlLayout.findViewById(R.id.video_time_view);
            final HnVoiceTipView voiceTipView = (HnVoiceTipView) mediaControlLayout.findViewById(R.id.voice_tip_view);
            final HnVideoPlayView videoPlayView = (HnVideoPlayView) mediaControlLayout.findViewById(R.id.video_play_view);

            videoTimeView.setTag(mDataListBean.getMediaValue()[1]);
            voiceTipView.setPlayTimeView(videoTimeView);
            mOnPlayListener = new Player.OnPlayListener() {
                @Override
                public void onPlay(String url, boolean isPause) {
                    if (isPause) {
                        voiceTipView.startPlaying(false);
                        videoPlayView.setPlayType(HnVideoPlayView.PlayType.VOICE);
                    } else {
                        voiceTipView.startPlaying(true);
                        videoPlayView.setPlayType(HnVideoPlayView.PlayType.VOICE_PAUSE);
                    }
                }

                @Override
                public void onPlayEnd(String url) {
                    videoPlayView.setPlayType(HnVideoPlayView.PlayType.VOICE);
                    voiceTipView.startPlaying(false);
                }
            };
            Audio.instance().addOnPlayListener(mOnPlayListener);
        }

        UserDiscussItemControl.initItem(mSubscriptions, mViewHolder, mDataListBean,
                null, null, mILayout, true);

        //WIFI 下自动播放语音
        if (isAudioType() && NetworkStateReceiver.getNetType() == NetworkUtils.NetworkType.NETWORK_WIFI) {
            Audio.instance().play(mDataListBean.getMediaValue()[1]);
        }
    }

    private boolean isAudioType() {
        return "4".equalsIgnoreCase(mDataListBean.getMedia_type());
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
                        mCommentListUIView.bindParentILayout(mParentILayout);
                    }
                    return mCommentListUIView;
                }
                if (mForwardListUIView == null) {
                    mForwardListUIView = new ForwardListUIView(mDataListBean.getDiscuss_id());
                    mForwardListUIView.bindParentILayout(mParentILayout);
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
