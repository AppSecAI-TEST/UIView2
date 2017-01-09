package com.hn.d.valley.sub.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.utils.ScreenUtil;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.utils.Image;
import com.hn.d.valley.widget.HnLoading;
import com.lzy.imagepicker.ImagePickerHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：发布动态
 * 创建人员：Robi
 * 创建时间：2017/01/09 9:19
 * 修改人员：Robi
 * 修改时间：2017/01/09 9:19
 * 修改备注：
 * Version: 1.0.0
 */
public class PublishDynamicUIView extends BaseContentUIView {
    private static int MAX_COUNT = 3;//最多多少列
    @BindView(R.id.recycler_view)
    RRecyclerView mRecyclerView;
    private ResizeAdapter mResizeAdapter;
    private ArrayList<String> photos;

    public PublishDynamicUIView(ArrayList<String> photos) {
        this.photos = photos;
    }

    private static void setHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * 根据数量, 返回多少列
     */
    private static int getColumnCount(int size) {
        int count;
        if (size >= MAX_COUNT) {
            count = MAX_COUNT;
        } else {
            count = size % MAX_COUNT;
        }
        return count;
    }

    /**
     * 通过数量, 计算出对应的高度
     */
    private static int getHeight(int count) {
        int height;
        int screenWidth = ScreenUtil.screenWidth;
        height = screenWidth / getColumnCount(count);
        return height;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_publish_dynamic);
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();
        mResizeAdapter = new ResizeAdapter(mActivity);
        mRecyclerView.setItemAnim(false);
        mRecyclerView.setAdapter(mResizeAdapter);
        mRecyclerView.addItemDecoration(new RBaseItemDecoration((int) ResUtil.dpToPx(mActivity.getResources(), 6),
                Color.TRANSPARENT));
        mResizeAdapter.resetData(photos);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString("发布动态")
                .setShowBackImageView(true)
                .addRightItem(TitleBarPattern.TitleBarItem.build(R.drawable.send_forward_dynamic_n, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }));
    }

    @Override
    public void onViewCreate() {
        super.onViewCreate();
        //ImagePickerHelper.startImagePicker(mActivity, false, true, false, true, 9);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Observable<ArrayList<String>> observable = Image.onActivityResult(mActivity, requestCode, resultCode, data);
        if (observable != null) {
            observable.subscribe(new BaseSingleSubscriber<ArrayList<String>>() {
                @Override
                public void onStart() {
                    super.onStart();
                    HnLoading.show(mILayout, false);
                }

                @Override
                public void onNext(ArrayList<String> strings) {
                    mResizeAdapter.resetData(strings);
                }

                @Override
                protected void onEnd() {
                    super.onEnd();
                    HnLoading.hide();
                }
            });
        }
    }

    public class ResizeAdapter extends RBaseAdapter<String> {

        private boolean isDeleteModel = false;

        public ResizeAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return 0;
        }

        @Override
        protected View createContentView(ViewGroup parent, int viewType) {
            int size = getHeight(mAllDatas.size());
            RelativeLayout relativeLayout = new RelativeLayout(mContext);
            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, size));

            ImageView imageView = new ImageView(mContext);
            imageView.setId(R.id.image_view);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            ImageView deleteImageView = new ImageView(mContext);
            deleteImageView.setImageResource(R.drawable.base_delete);
            deleteImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            deleteImageView.setId(R.id.delete_image_vie);
            deleteImageView.setBackgroundResource(R.drawable.base_main_color_bg_selector2);
            int padding = (int) ResUtil.dpToPx(mContext, 6);
            deleteImageView.setPadding(padding, padding, padding, padding);
            deleteImageView.setVisibility(View.GONE);

            relativeLayout.addView(imageView, new ViewGroup.LayoutParams(-1, -1));
            RelativeLayout.LayoutParams deleteParams = new RelativeLayout.LayoutParams(-2, -2);
            deleteParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            relativeLayout.addView(deleteImageView, deleteParams);

            return relativeLayout;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, final String bean) {
            int size = getHeight(mAllDatas.size());
            setHeight(holder.itemView, size);
            Glide.with(mContext).load(bean).override(size, size).placeholder(R.drawable.zhanweitu_1)
                    .into(holder.imgV(R.id.image_view));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImagePickerHelper.startImagePicker(mActivity, false, false, false, true, 9);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (isDeleteModel) {
                        return true;
                    }
                    isDeleteModel = true;
                    for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(i);
                        if (viewHolder != null) {
                            ((RBaseViewHolder) viewHolder).v(R.id.delete_image_vie).setVisibility(View.VISIBLE);
                        }
                    }
                    return true;
                }
            });

            holder.v(R.id.delete_image_vie).setVisibility(isDeleteModel ? View.VISIBLE : View.GONE);
            holder.v(R.id.delete_image_vie).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItemCount() == 1) {
                        T_.show(mContext.getString(R.string.publish_image_empty_tip));
                        return;
                    }
                    ImagePickerHelper.deleteItemFromSelected(bean);
                    deleteItem(bean);
                    resetData(mAllDatas);
                }
            });
        }

        @Override
        public void resetData(List<String> datas) {
            super.resetData(datas);
            int size = datas.size();
            double line = Math.ceil(size * 1.f / MAX_COUNT);
            setHeight(mRecyclerView, (int) ((int) (line * getHeight(size)) +
                    Math.max(0, line - 1) * ResUtil.dpToPx(mActivity.getResources(), 6)));
            ((GridLayoutManager) mRecyclerView.getLayoutManager()).setSpanCount(getColumnCount(size));
        }
    }
}
