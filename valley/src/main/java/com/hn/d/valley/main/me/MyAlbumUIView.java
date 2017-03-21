package com.hn.d.valley.main.me;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.github.swipe.recyclerview.touch.DefaultItemTouchHelper;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.TimeUtil;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.MyPhotoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;

import java.util.List;

/**
 * Created by hewking on 2017/3/20.
 */
public class MyAlbumUIView extends SingleRecyclerUIView<MyPhotoBean> {


    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString("我的相册");
    }

    @Override
    protected RExBaseAdapter<String, MyPhotoBean, String> initRExBaseAdapter() {
        return new AlbumAdapter(mActivity);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(UserInfoService.class)
                .myPhotos(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + UserCache.getUserAccount() , "page:" + page))
                .compose(Rx.transformerList(MyPhotoBean.class))
                .subscribe(new SingleRSubscriber<List<MyPhotoBean>>(this) {
                    @Override
                    protected void onResult(List<MyPhotoBean> bean) {
                        if (bean == null || bean.size() == 0) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(bean);
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }
                }));
    }

    public class AlbumAdapter extends RExBaseAdapter<String,MyPhotoBean,String> {

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

            String formatDateTime = TimeUtil.getDividerDateFormat(Long.parseLong(dataBean.getCreated()));
            tv_timeline.setText(getSlectedSpannable(formatDateTime));

            int itemHeight = (ScreenUtil.screenWidth - 2 * ScreenUtil.dip2px(5)) / 3;

            recyclerView.addItemDecoration(RExItemDecoration.build(new RExItemDecoration.ItemDecorationCallback() {
                @Override
                public Rect getItemOffsets(LinearLayoutManager layoutManager, int position) {
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
            recyclerView.setLayoutManager(new GridLayoutManager(mActivity,3));

            AlbumPhotoAdapter albumAdapter = new AlbumPhotoAdapter(mActivity,itemHeight);

            recyclerView.setAdapter(albumAdapter);
            albumAdapter.setData(dataBean.getMedia());
        }
    }

    public class AlbumPhotoAdapter extends RExBaseAdapter<String,String,String> {

        private int itemHeight;

        private ImageView imageView;

        public AlbumPhotoAdapter(Context context,int itemHeight) {
            super(context);
            this.itemHeight = itemHeight;
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_image_layout;
        }

        public void setData(String media) {
            List<String> medias = RUtils.split(media);
            resetAllData(medias);
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

    private SpannableString getSlectedSpannable(String str) {
        SpannableString ss = new SpannableString(str);
        int splitpos = str.indexOf("/");
        ss.setSpan(new AbsoluteSizeSpan(70), 0, splitpos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        return super.initItemDecoration();
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }
}
