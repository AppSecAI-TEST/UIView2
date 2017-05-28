package com.hn.d.valley.main.me;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RGroupAdapter;
import com.angcyo.uiview.recycler.adapter.RGroupData;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.RImageView;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.iview.VideoPlayUIView;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.bean.MyPhotoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.utils.PhotoPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 我的相册
 * Created by hewking on 2017/3/20.
 */
public class MyAlbumUIView extends SingleRecyclerUIView<MyAlbumUIView.AlbumGroup> {

    String to_uid;
    private AlbumAdapter2 mAlbumAdapter;

    public MyAlbumUIView(String to_uid) {
        this.to_uid = to_uid;
    }

    public MyAlbumUIView() {
        to_uid = UserCache.getUserAccount();
    }

    private static SpannableString getSlectedSpannable(String str) {
        SpannableString ss = new SpannableString(str);
        int splitpos = str.indexOf("/");
        ss.setSpan(new AbsoluteSizeSpan(28, true), 0, splitpos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        if (isInSubUIView()) {
            return null;
        }
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_my_album));
    }

    private boolean isInSubUIView() {
        return mParentILayout != mILayout;
    }

    @Override
    protected RExBaseAdapter<String, AlbumGroup, String> initRExBaseAdapter() {
        mAlbumAdapter = new AlbumAdapter2(mActivity);
        return mAlbumAdapter;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        if (!isInSubUIView()) {
            super.onViewShowFirst(bundle);
        }
    }

    @Override
    protected void onUILoadData(final String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(UserInfoService.class)
                .myPhotos(Param.buildMap("to_uid:" + to_uid, "page:" + page))
                .compose(Rx.transformerList(MyPhotoBean.class))
                .subscribe(new SingleRSubscriber<List<MyPhotoBean>>(this) {
                    @Override
                    protected void onResult(List<MyPhotoBean> bean) {
                        if (bean == null || bean.size() == 0) {
                            onUILoadDataEnd();
                        } else {
                            //onUILoadDataEnd(bean);

                            AlbumGroup lastGroup = null;
                            if (!"1".equalsIgnoreCase(page)) {
                                List<AlbumGroup> allDatas = mAlbumAdapter.getAllDatas();
                                if (allDatas.size() > 0) {
                                    lastGroup = allDatas.get(allDatas.size() - 1);
                                }
                            }


                            List<AlbumGroup> albumGroups = getAlbumGroups(bean);

                            if (lastGroup == null) {
                                mAlbumAdapter.resetAllData(albumGroups);
                            } else {
                                if (albumGroups.size() > 0) {
                                    AlbumGroup firstGroup = albumGroups.get(0);
                                    if (lastGroup.year == firstGroup.year && lastGroup.month == firstGroup.month) {
                                        lastGroup.appendDatas(mAlbumAdapter, firstGroup.getAllDatas());
                                        albumGroups.remove(0);
                                    }
                                    mAlbumAdapter.appendData(albumGroups);
                                }
                            }
                            if (mAlbumAdapter.getItemCount() > 0) {
                                showContentLayout();
                            } else {
                                showEmptyLayout();
                            }

                            if ("1".equalsIgnoreCase(page) && bean.size() >= Constant.DEFAULT_PAGE_DATA_COUNT) {
                                mAlbumAdapter.setEnableLoadMore(true);
                            }

                            onUILoadDataFinish();
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }
                }));
    }

    private List<AlbumGroup> getAlbumGroups(List<MyPhotoBean> bean) {
        int lastYear = -1, lastMonth = -1;

//                            if (lastGroup != null) {
//                                lastYear = Integer.valueOf(lastGroup.year);
//                                lastMonth = Integer.valueOf(lastGroup.month);
//                            }

        List<AlbumGroup> groups = new ArrayList<>();

        AlbumGroup group = null;
        for (int i = 0; i < bean.size(); i++) {
            MyPhotoBean myPhotoBean = bean.get(i);
            String formatDateTime = TimeUtil.getDividerDateFormat(Long.parseLong(myPhotoBean.getCreated()) * 1000);
            String[] split = formatDateTime.split("/");
            if (split.length == 2) {
                if (i == 0) {
                    lastYear = Integer.valueOf(split[1]);
                    lastMonth = Integer.valueOf(split[0]);

                    group = new AlbumGroup(getAlbumBeans(myPhotoBean), lastYear, lastMonth);
                } else {
                    int year = Integer.valueOf(split[1]);
                    int month = Integer.valueOf(split[0]);

                    if (lastYear == year && lastMonth == month) {
                        group.appendDatas(getAlbumBeans(myPhotoBean));
                    } else {
                        groups.add(group);
                        lastYear = year;
                        lastMonth = month;
                        group = new AlbumGroup(getAlbumBeans(myPhotoBean), lastYear, lastMonth);
                    }
                }
            }
        }
        if (group != null) {
            groups.add(group);
        }
        return groups;
    }

    @NonNull
    private List<AlbumBean> getAlbumBeans(MyPhotoBean myPhotoBean) {
        List<AlbumBean> beans = new ArrayList<>();
        List<String> strings = RUtils.split(myPhotoBean.getMedia());
        for (String str : strings) {
            beans.add(new AlbumBean(myPhotoBean.getMedia_type(), str));
        }
        return beans;
    }

    @Override
    protected void initRefreshLayout() {
        super.initRefreshLayout();
        if (isInSubUIView()) {
            getRefreshLayout().setRefreshDirection(RefreshLayout.NONE);
        }
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3, LinearLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (mAlbumAdapter.isInGroup(position) ||
                        (mAlbumAdapter.isEnableLoadMore() && mAlbumAdapter.isLast(position))) ?
                        layoutManager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    protected RecyclerView.ItemDecoration initItemDecoration() {
        return super.initItemDecoration();
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }

    public static class AlbumBean {
        public String media_type;
        public String url;

        public AlbumBean(String media_type, String url) {
            this.media_type = media_type;
            this.url = url;
        }
    }

    public class AlbumGroup extends RGroupData<AlbumBean> {

        int year;//年
        int month;//月

        public AlbumGroup(List<AlbumBean> allDatas, int year, int month) {
            super(allDatas);
            this.year = year;
            this.month = month;
        }

        @Override
        protected void onBindGroupView(RBaseViewHolder holder, int position, int indexInGroup) {
            holder.tv(R.id.text_view).setText(getSlectedSpannable(
                    String.format(Locale.CHINA, "%02d", month) + "/" + year));
        }

        private List<String> getAllPhotos() {
            List<String> photos = new ArrayList<>();
            for (int i = 0; i < mAllDatas.size(); i++) {
                AlbumBean albumBean = mAllDatas.get(i);

                if ("2".equalsIgnoreCase(albumBean.media_type)) {
                    String[] split = albumBean.url.split("\\?");
                    final String thumbUrl = split[0];
                } else if ("3".equalsIgnoreCase(albumBean.media_type)) {
                    photos.add(albumBean.url);
                } else {
                    //photos.add(albumBean.url);
                }
            }
            return photos;
        }

        private int getPhotoStartIndex(int position) {
            int index = 0;
            for (int i = 0; i < mAllDatas.size(); i++) {
                AlbumBean albumBean = mAllDatas.get(i);

                if (i == position) {
                    break;
                }

                if ("3".equalsIgnoreCase(albumBean.media_type)) {
                    index++;
                }

            }
            return index;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int position, final int indexInData) {
            final RImageView imageView = holder.v(R.id.image_view);
            final AlbumBean albumBean = mAllDatas.get(indexInData);

            int size = mRecyclerView.getMeasuredWidth() / 3;
            UI.setView(imageView, size, size);
            int offset = getDimensionPixelOffset(R.dimen.base_mdpi);
            holder.itemView.setPadding(offset, offset, offset, offset);

            if ("3".equalsIgnoreCase(albumBean.media_type)) {
                //图片
                Glide.with(holder.getContext())
                        .load(OssHelper.getImageThumb(albumBean.url, size, size))
                        .placeholder(R.drawable.zhanweitu_1)
                        .into(imageView);

                imageView.setPlayDrawable(null);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePagerUIView.start(mParentILayout, imageView,
                                PhotoPager.getImageItems(getAllPhotos()), getPhotoStartIndex(indexInData));
                    }
                });
            } else if ("2".equalsIgnoreCase(albumBean.media_type)) {
                //视频
                String[] split = albumBean.url.split("\\?");

                if (split.length == 2) {
                    final String thumbUrl = split[0];
                    final String videoUrl = split[1];

                    Glide.with(holder.getContext())
                            .load(OssHelper.getImageThumb(thumbUrl, size, size))
                            .placeholder(R.drawable.zhanweitu_1)
                            .into(imageView);
                    imageView.setPlayDrawable(R.drawable.play);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mParentILayout.startIView(new VideoPlayUIView(videoUrl,
                                    imageView.getDrawable().getConstantState().newDrawable(),
                                    OssHelper.getWidthHeightWithUrl(videoUrl)));

                        }
                    });
                }
            } else {
                imageView.setPlayDrawable(null);
            }
        }
    }

    @Deprecated
    public class AlbumAdapter extends RExBaseAdapter<String, MyPhotoBean, String> {

        private TextView tv_timeline;
        private RRecyclerView recyclerView;

        public AlbumAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_user_album;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, final int posInData, MyPhotoBean dataBean) {
            super.onBindDataView(holder, posInData, dataBean);
            tv_timeline = holder.tv(R.id.tv_timeline);
            recyclerView = holder.v(R.id.rv_album_photo);

            String formatDateTime = TimeUtil.getDividerDateFormat(Long.parseLong(dataBean.getCreated()) * 1000);
            tv_timeline.setText(getSlectedSpannable(formatDateTime));

            int itemHeight = (ScreenUtil.screenWidth - 2 * ScreenUtil.dip2px(5)) / 3;

            recyclerView.addItemDecoration(RExItemDecoration.build(new RExItemDecoration.ItemDecorationCallback() {
                @Override
                public Rect getItemOffsets(RecyclerView.LayoutManager layoutManager, int position, int edge) {
                    int size = ScreenUtil.dip2px(5);
                    Rect rect = new Rect(0, 0, 0, 0);

                    if (position % 3 == 1) {
                        rect.left = size;
                        rect.right = size;
                    }

                    if (position > 2) {
                        rect.top = size;
                    }
                    return rect;
                }

                @Override
                public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {

                }
            }));
            recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));

            AlbumPhotoAdapter albumAdapter = new AlbumPhotoAdapter(mActivity, itemHeight);

            recyclerView.setAdapter(albumAdapter);
            albumAdapter.setDataData(dataBean.getMedia());
        }
    }

    @Deprecated
    public class AlbumPhotoAdapter extends RExBaseAdapter<String, String, String> {

        private int itemHeight;

        private ImageView imageView;

        public AlbumPhotoAdapter(Context context, int itemHeight) {
            super(context);
            this.itemHeight = itemHeight;
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_image_layout;
        }

        @Override
        public RExBaseAdapter<String, String, String> setDataData(String media) {
            List<String> medias = RUtils.split(media);
            resetAllData(medias);
            return this;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, String dataBean) {
            super.onBindDataView(holder, posInData, dataBean);

            if (itemHeight != 0) {
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = itemHeight;
            }

            imageView = holder.v(R.id.image_view);

            Glide.with(mActivity).load(dataBean).into(imageView);

        }
    }

    private class AlbumAdapter2 extends RGroupAdapter<String, AlbumGroup, String> {

        public AlbumAdapter2(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            if (viewType == TYPE_GROUP_HEAD) {
                return R.layout.item_album_time_layout;
            } else if (viewType == TYPE_GROUP_DATA) {
                return R.layout.item_single_image_view;
            }
            return super.getItemLayoutId(viewType);
        }
    }
}
