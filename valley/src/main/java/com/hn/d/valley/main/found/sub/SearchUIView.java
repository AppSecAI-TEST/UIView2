package com.hn.d.valley.main.found.sub;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.rsen.PlaceholderView;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.widget.RTextImageLayout;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.bean.HotInfoListBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.bean.UserRecommendBean;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.service.SearchService;
import com.hn.d.valley.service.NewsService;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：搜一搜界面
 * 创建人员：Robi
 * 创建时间：2017/01/17 09:19
 * 修改人员：Robi
 * 修改时间：2017/01/17 09:19
 * 修改备注：
 * Version: 1.0.0
 */
public class SearchUIView extends BaseRecyclerUIView<SearchUIView.TopBean,
        UserDiscussListBean.DataListBean, HotInfoListBean> {

    private RBaseItemDecoration mDecor;

    public static void updateMediaLayout(final UserDiscussListBean.DataListBean dataListBean, final ILayout iLayout, final RBaseViewHolder holder) {
        updateMediaLayout(dataListBean.getContent(), dataListBean.getDiscuss_id(), dataListBean.getMedia(), dataListBean.getMedia_type(),
                iLayout, holder);
    }

    public static void updateMediaLayout(String content, final String discussId, String media, String mediaType,

                                         final ILayout iLayout, final RBaseViewHolder holder) {
        final View videoPlayView = holder.v(R.id.video_play_view);
        final TextView videoTimeView = holder.v(R.id.video_time_view);
        RTextImageLayout textImageLayout = holder.v(R.id.text_image_layout);
        textImageLayout.setText(content);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //iLayout.startIView(new DynamicDetailUIView2(discussId));
                UserDiscussItemControl.jumpToDynamicDetailUIView(iLayout, discussId, false, false);
            }
        });

        final List<String> medias = RUtils.split(media);
        if (medias.isEmpty()) {
            return;
        } else {
            final String url = medias.get(0);

            if ("3".equalsIgnoreCase(mediaType)) {
                //图片类型
                videoTimeView.setVisibility(View.INVISIBLE);
                videoPlayView.setVisibility(View.INVISIBLE);

                textImageLayout.setConfigCallback(new RTextImageLayout.ConfigCallback() {
                    @Override
                    public int[] getImageSize(int position) {
                        return null;
                    }

                    @Override
                    public void onCreateImageView(ImageView imageView) {
                        imageView.setImageResource(R.drawable.zhanweitu_1);
                    }

                    @Override
                    public void onCreateTextView(TextView textView) {
                        textView.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.main_text_color));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                holder.getContext().getResources().getDimensionPixelOffset(R.dimen.default_text_little_size));
                    }

                    @Override
                    public void displayImage(ImageView imageView, String url) {
                        HotInfoListUIView.displayImage(imageView, url);
                    }
                });

                textImageLayout.setImages(medias);
            } else if ("2".equalsIgnoreCase(mediaType)) {
                //视频类型
                videoTimeView.setVisibility(View.VISIBLE);
                videoPlayView.setVisibility(View.VISIBLE);

                String[] split = url.split("\\?");
                final String thumbUrl = split[0];
                String videoUrl = "";
                try {
                    videoUrl = split[1];
                    videoTimeView.setText(UserDiscussItemControl.getVideoTime(videoUrl));
                    textImageLayout.setConfigCallback(new RTextImageLayout.ConfigCallback() {
                        @Override
                        public int[] getImageSize(int position) {
                            return OssHelper.getImageThumbSize2(thumbUrl);
                        }

                        @Override
                        public void onCreateImageView(ImageView imageView) {
                            imageView.setImageResource(R.drawable.zhanweitu_1);
                        }

                        @Override
                        public void onCreateTextView(TextView textView) {
                            textView.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.main_text_color));
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                    holder.getContext().getResources().getDimensionPixelOffset(R.dimen.default_text_little_size));
                        }

                        @Override
                        public void displayImage(ImageView imageView, String url) {
                            HotInfoListUIView.displayImage(imageView, url);
                        }
                    });
                    textImageLayout.setImage(thumbUrl);
                } catch (Exception e) {
                    videoTimeView.setTextColor(Color.RED);
                    videoTimeView.setText("video time format error");
                }
            } else {
                videoTimeView.setVisibility(View.INVISIBLE);
                videoPlayView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleHide(false)
                .setTitleString(mActivity.getString(R.string.search_title));
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }

    @Override
    protected boolean hasScrollListener() {
        return true;
    }

    @Override
    protected void initScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mUITitleBarContainer != null) {
                    mUITitleBarContainer.evaluateBackgroundColor(recyclerView.computeVerticalScrollOffset());
                }
            }
        });
    }

    @Override
    protected void initRefreshLayout() {
        super.initRefreshLayout();
        mRefreshLayout.setTopView(new PlaceholderView(mActivity));
        mRefreshLayout.setNotifyListener(false);
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        mRExBaseAdapter.appendHeaderData(new TopBean());
        mRecyclerView.addItemDecoration(new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
            @Override
            public void getItemOffsets(Rect outRect, int position) {
                if (position == mRExBaseAdapter.getHeaderCount() ||
                        position == mRExBaseAdapter.getHeaderCount() + mRExBaseAdapter.getDataCount()) {
                    outRect.top = getDimensionPixelOffset(R.dimen.base_xhdpi);
                }
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                paint.setColor(getColor(R.color.chat_bg_color));
                offsetRect.set(0, itemView.getTop() - offsetRect.top, mRecyclerView.getMeasuredWidth(), itemView.getTop());
                canvas.drawRect(offsetRect, paint);
            }
        }));
    }

    @Override
    protected RExBaseAdapter<TopBean, UserDiscussListBean.DataListBean, HotInfoListBean> initRExBaseAdapter() {
        return new RExBaseAdapter<TopBean, UserDiscussListBean.DataListBean, HotInfoListBean>(mActivity) {
            @Override
            protected int getHeaderItemType(int posInHeader) {
                return super.getHeaderItemType(posInHeader) + posInHeader;
            }

            @Override
            protected int getDataItemType(int posInData) {
                return super.getDataItemType(posInData);
            }

            @Override
            protected int getFooterItemType(int posInFooter) {
                return super.getFooterItemType(posInFooter);
            }

            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TYPE_HEADER) {
                    return R.layout.item_search_layout_top;
                }
                if (viewType == TYPE_HEADER + 1) {
                    return R.layout.item_search_layout_user_recommend;
                }
                return R.layout.item_article_layout;
            }

            /**最新资讯item*/
            @Override
            protected void onBindFooterView(RBaseViewHolder holder, int posInFooter, HotInfoListBean footerBean) {
                HotInfoListUIView.initItem(holder, footerBean);
                holder.v(R.id.delete_view).setVisibility(View.GONE);
                RTextView tv = holder.v(R.id.tip_text_view);
                if (posInFooter == 0) {
                    holder.v(R.id.tip_layout).setVisibility(View.VISIBLE);
                    tv.setDefaultSKin(R.string.new_hot_info_tip);
                } else {
                    holder.v(R.id.tip_layout).setVisibility(View.GONE);
                }
                holder.v(R.id.line).setVisibility(View.VISIBLE);

                holder.v(R.id.tip_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            /**名人推荐item*/
            @Override
            protected void onBindHeaderView(RBaseViewHolder holder, int posInHeader, TopBean headerBean) {
                if (posInHeader == 0) {
                    holder.v(R.id.search_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startIView(new SearchNextUIView());
                        }
                    });
                } else {
                    RTextView tv = holder.v(R.id.tip_view);
                    tv.setDefaultSKin(R.string.user_ecommend_tip);

                    RRecyclerView recyclerView = holder.reV(R.id.recycler_view);
                    if (mDecor == null) {
                        mDecor = new RBaseItemDecoration((int) (density() * 10), Color.TRANSPARENT);
                    } else {
                        recyclerView.removeItemDecoration(mDecor);
                    }
                    recyclerView.addItemDecoration(mDecor);
                    recyclerView.setAdapter(new UserRecommendAdapter(mActivity, headerBean.mUserRecommendBeen));
                }
            }

            /**最新动态item*/
            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, UserDiscussListBean.DataListBean dataBean) {
                RTextView tv = holder.v(R.id.tip_text_view);
                if (posInData == 0) {
                    holder.v(R.id.tip_layout).setVisibility(View.VISIBLE);
                    tv.setDefaultSKin(R.string.new_discuss_tip);
                } else {
                    holder.v(R.id.tip_layout).setVisibility(View.GONE);
                }

                //HotInfoListUIView.initTextImageLayout(holder, dataBean.getContent(), RUtils.split(dataBean.getMedia()));
                updateMediaLayout(dataBean, mILayout, holder);

                HnGlideImageView imageView = holder.v(R.id.image_view);
                imageView.setImageThumbUrl(dataBean.getUser_info().getAvatar());
                imageView.setAuth("1".equalsIgnoreCase(dataBean.getUser_info().getIs_auth()));

                holder.tv(R.id.author_view).setText(dataBean.getUser_info().getUsername());
                holder.tv(R.id.time_view).setText(dataBean.getShow_time());

                holder.v(R.id.line).setVisibility(View.VISIBLE);
                holder.tv(R.id.like_cnt_view).setVisibility(View.VISIBLE);
                holder.tv(R.id.like_cnt_view).setText(dataBean.getLike_cnt());
                holder.tv(R.id.reply_cnt_view).setText(dataBean.getComment_cnt());
                holder.v(R.id.delete_view).setVisibility(View.GONE);

                holder.v(R.id.tip_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        };
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        //((UILayoutImpl) mParentILayout).setEnableSwipeBack(false);
        loadUserRecommend();
    }

    private void loadUserRecommend() {

        /**
         * 加载名人推荐
         */
        Observable<List<UserRecommendBean>> userRecommend = RRetrofit.create(SearchService.class, RRetrofit.CacheType.MAX_STALE)
                .userRecommend(Param.buildMap()).compose(Rx.transformerList(UserRecommendBean.class));

        /**
         * 加载最新动态
         */
        Observable<UserDiscussListBean> discussRecommend = RRetrofit.create(SearchService.class, RRetrofit.CacheType.MAX_STALE)
                .discussRecommend(Param.buildMap()).compose(Rx.transformer(UserDiscussListBean.class));

        /**
         * 加载最新资讯
         */
        Observable<List<HotInfoListBean>> abstract_ = RRetrofit.create(NewsService.class)
                .abstract_(Param.buildInfoMap("random:0", "amount:10")).compose(Rx.transformerList(HotInfoListBean.class));

        add(Rx.concat(userRecommend, discussRecommend, abstract_)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object obj) {
                        try {
                            if (obj instanceof List) {
                                List list = (List) obj;
                                Object first = list.get(0);

                                if (first instanceof UserRecommendBean) {
                                    TopBean topBean = new TopBean(list);
                                    mRExBaseAdapter.appendHeaderData(topBean);
                                } else if (first instanceof HotInfoListBean) {
                                    mRExBaseAdapter.resetFooterData(list);
                                }
                            } else if (obj instanceof UserDiscussListBean) {
                                mRExBaseAdapter.resetAllData(((UserDiscussListBean) obj).getData_list());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e(throwable.getMessage());
                    }
                }));
    }

    public static class TopBean {
        public List<UserRecommendBean> mUserRecommendBeen;

        public TopBean() {
        }

        public TopBean(List<UserRecommendBean> userRecommendBeen) {
            mUserRecommendBeen = userRecommendBeen;
        }
    }

    /**
     * 名人推荐adapter
     */
    private class UserRecommendAdapter extends RBaseAdapter<UserRecommendBean> {

        public UserRecommendAdapter(Context context, List<UserRecommendBean> datas) {
            super(context, datas);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_search_layout_user_recommend_sub_item;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, final UserRecommendBean bean) {
            holder.tv(R.id.username_view).setText(bean.getUsername());
            holder.tv(R.id.fans_count_view).setText(bean.getFans_count() + "");

            HnGlideImageView imageView = holder.v(R.id.image_view);
            imageView.setImageUrl(bean.getAvatar());

            holder.v(R.id.item_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new UserDetailUIView2(bean.getUid()));
                }
            });
        }
    }

}
