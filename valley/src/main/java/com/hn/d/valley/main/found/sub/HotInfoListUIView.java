package com.hn.d.valley.main.found.sub;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIWindow;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.widget.GlideImageView;
import com.angcyo.uiview.widget.RFlowLayout;
import com.angcyo.uiview.widget.RTextImageLayout;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.HotInfoListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.service.NewsService;
import com.hn.d.valley.widget.HnExTextView;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.List;

import rx.functions.Action0;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：资讯列表
 * 创建人员：Robi
 * 创建时间：2017/03/23 11:06
 * 修改人员：Robi
 * 修改时间：2017/03/23 11:06
 * 修改备注：
 * Version: 1.0.0
 */
public class HotInfoListUIView extends BaseRecyclerUIView<String, HotInfoListBean, String> {

    /**
     * 分类, 名称
     */
    String classify;

    public HotInfoListUIView(String classify) {
        this.classify = classify;
    }

    public static void initTextImageLayout(final RBaseViewHolder holder, final String text, final List<String> images, final boolean isVideo) {
        RTextImageLayout textImageLayout = holder.v(R.id.text_image_layout);
        textImageLayout.setConfigTextView(new RTextImageLayout.ConfigTextView() {
            @Override
            public TextView onCreateTextView(TextView textView) {
                if (textView == null) {
                    return new HnExTextView(holder.getContext());
                }
                textView.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.main_text_color));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        holder.getContext().getResources().getDimensionPixelOffset(R.dimen.default_text_size16));
                return textView;
            }

        });
        textImageLayout.setText(text);
        textImageLayout.setConfigCallback(new RTextImageLayout.ConfigCallback() {
            @Override
            public int[] getImageSize(int position) {
                if (isVideo) {
                    return new int[]{-1, (int) (ScreenUtil.density * 100)};
                }
                return null;
            }

            @Override
            public void onCreateImageView(GlideImageView imageView) {
                imageView.setImageResource(R.drawable.zhanweitu_1);
                imageView.setPlayDrawable(isVideo ? R.drawable.image_picker_play : -1);
            }

            @Override
            public void displayImage(GlideImageView imageView, String url) {
                imageView.setShowGifTip(false);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setCheckGif(true);
                imageView.setShowGifImage(false);
                imageView.setPlaceholderRes(R.drawable.zhanweitu_1);
                imageView.setUrl(url);
//                HotInfoListUIView.displayImage(imageView, url);
                L.i("RTextImageLayout: displayImage([imageView, url])-> " + url);
            }

            @Override
            public boolean isVideoType() {
                return isVideo;
            }
        });
        textImageLayout.setImages(images);
    }

    public static void initItem(final ILayout iLayout, final RBaseViewHolder holder, final HotInfoListBean dataBean, String highlightWord) {
        //holder.fillView(dataBean);
        String author = dataBean.getAuthor();
        if (TextUtils.isEmpty(author)) {
            author = ValleyApp.getApp().getString(R.string.information_klg);
        }
        holder.tv(R.id.author_view).setText(author);

        int reply_cnt = dataBean.getReply_cnt();
        holder.tv(R.id.reply_cnt_view).setText(reply_cnt + "");
        holder.tv(R.id.reply_cnt_view).setVisibility(reply_cnt <= 0 ? View.GONE : View.VISIBLE);

        holder.tv(R.id.time_view).setText(TimeUtil.getTimeShowString(dataBean.getDate() * 1000, false));

        HnGlideImageView imageView = holder.v(R.id.image_view);
        imageView.setImageUrl(dataBean.getLogo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iLayout.startIView(new InformationDetailUIView(dataBean));
            }
        });

        initTextImageLayout(holder, dataBean.getTitle(), dataBean.getImgsList(), "video".equalsIgnoreCase(dataBean.getType()));

        if (!TextUtils.isEmpty(highlightWord)) {
            RTextImageLayout textImageLayout = holder.v(R.id.text_image_layout);
            TextView textView = textImageLayout.getTextView();
            if (textView instanceof RTextView) {
                ((RTextView) textView).setHighlightWord(highlightWord);
            }
        }
    }

    public static void displayImage(ImageView imageView, String url) {
//        Glide.with(imageView.getContext())
//                .load(url)
//                .placeholder(R.drawable.zhanweitu_1)
//                .into(imageView);

        UserDiscussItemControl.displayImage(imageView, url);

//        UserDiscussItemControl.displayImage(imageView, url,
//                imageView.getMeasuredWidth(), imageView.getMeasuredHeight(),
//                true, Integer.MAX_VALUE);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    protected boolean hasScrollListener() {
        return false;
    }

    @Override
    protected int getItemDecorationHeight() {
        return mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
    }

    @Override
    protected boolean isLoadInViewPager() {
        return true;
    }

    @Override
    public void loadData() {
        loadTime = System.currentTimeMillis();
        page = 1;
        //last_id = "";
        hasNext = true;
        onCancel();//刷新数据,取消之前的加载更多请求
        onUILoadData(getPage());
    }

    @Override
    protected RExBaseAdapter<String, HotInfoListBean, String> initRExBaseAdapter() {
        RExBaseAdapter<String, HotInfoListBean, String> baseAdapter = new RExBaseAdapter<String, HotInfoListBean, String>(mActivity) {

            @Override
            protected int getDataItemType(int posInData) {
                return super.getDataItemType(posInData);
            }

            @Override
            protected int getItemLayoutId(int viewType) {
                return R.layout.item_article_layout;
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, final int posInData, final HotInfoListBean dataBean) {
                initItem(mParentILayout, holder, dataBean, "");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InformationDetailUIView informationDetailUIView = new InformationDetailUIView(dataBean);
                        informationDetailUIView.setNoLikeAction(new Action0() {
                            @Override
                            public void call() {
                                deleteItem(posInData);
                            }
                        });
                        mParentILayout.startIView(informationDetailUIView);
                    }
                });


                holder.v(R.id.delete_view).setVisibility(View.VISIBLE);
                holder.click(R.id.delete_view, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //RPopupWindow.build(mActivity).showAsDropDown(v);
                        UIWindow.build(v)
                                .layout(R.layout.information_no_like_layout)
                                .onInitWindow(new UIWindow.OnInitWindow() {
                                    @Override
                                    public void onInitWindow(final UIWindow window, final RBaseViewHolder viewHolder) {
                                        final RFlowLayout flowLayout = viewHolder.v(R.id.flow_layout);
                                        View.OnClickListener checkListener = new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                List<String> checkedTextList = flowLayout.getCheckedTextList();

                                                RTextView tipView = viewHolder.v(R.id.tip_view);
                                                if (checkedTextList.isEmpty()) {
                                                    viewHolder.tv(R.id.not_like).setText(getString(R.string.no_like));
                                                    tipView.setText(getString(R.string.no_like_tip));
                                                } else {
                                                    viewHolder.tv(R.id.not_like).setText(getString(R.string.ok));
                                                    tipView.setText(getString(R.string.no_like_tip_format, checkedTextList.size()));
                                                    tipView.setHighlightWord(String.valueOf(checkedTextList.size()));
                                                }
                                            }
                                        };

                                        flowLayout.addCheckTextView(getString(R.string.no_like_1)).setOnClickListener(checkListener);
                                        ;
                                        flowLayout.addCheckTextView(getString(R.string.no_like_2)).setOnClickListener(checkListener);
                                        ;

                                        for (String text : dataBean.getTagList()) {
                                            flowLayout.addCheckTextView(getString(R.string.no_like_format, text)).setOnClickListener(checkListener);
                                        }

                                        viewHolder.click(R.id.not_like, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                deleteItem(posInData);

                                                List<String> checkedTextList = flowLayout.getCheckedTextList();

                                                StringBuilder content = new StringBuilder();
                                                if (checkedTextList.isEmpty()) {
                                                    content.append(dataBean.getId());
                                                    content.append(";");
                                                } else {
                                                    for (String tag : checkedTextList) {
                                                        content.append(tag.replaceAll(getString(R.string.no_like_), "").trim());
                                                        content.append(";");
                                                    }
                                                }

                                                window.finishIView();

                                                add(RRetrofit
                                                        .create(NewsService.class)
                                                        .feedback(Param.buildInfoMap("uid:" + UserCache.getUserAccount(),
                                                                "id:" + dataBean.getId(), "content:" + RUtils.safe(content)))
                                                        .compose(Rx.transformer(String.class))
                                                        .subscribe(new BaseSingleSubscriber<String>() {
                                                        })
                                                );
                                            }
                                        });
                                    }
                                })
                                .show(mParentILayout, (int) (1 * density()));
                    }
                });
            }
        };
        baseAdapter.setEnableLoadMore(true);
        return baseAdapter;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        L.e("call: onUILoadData([page])-> " + classify);
        add(RRetrofit
                .create(NewsService.class)
                .abstract_(Param.buildInfoMap("classify:" + classify, "random:0", "uid:" + UserCache.getUserAccount(),
                        "amount:" + Constant.DEFAULT_PAGE_DATA_COUNT, "lastid:" + last_id))
                .compose(Rx.transformerList(HotInfoListBean.class))
                .subscribe(new BaseSingleSubscriber<List<HotInfoListBean>>() {

                    @Override
                    public void onSucceed(List<HotInfoListBean> bean) {
                        if (bean == null || bean.isEmpty()) {

                        } else {
                            last_id = String.valueOf(bean.get(bean.size() - 1).getId());
                            onUILoadDataEnd(bean);
                        }
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        onUILoadDataFinish();
                        if (isError) {
                            showNonetLayout(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadData();
                                }
                            });
                        }
                    }
                })
        );
    }

}
