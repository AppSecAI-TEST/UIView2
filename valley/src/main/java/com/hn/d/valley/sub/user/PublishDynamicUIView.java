package com.hn.d.valley.sub.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.bumptech.glide.Glide;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.base.oss.OssControl;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.AmapBean;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.TagsControl;
import com.hn.d.valley.sub.user.service.SocialService;
import com.hn.d.valley.utils.Image;
import com.hn.d.valley.utils.RAmap;
import com.hn.d.valley.widget.HnLoading;
import com.lzy.imagepicker.ImagePickerHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

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
public class PublishDynamicUIView extends BaseContentUIView implements OssControl.OnUploadListener, UIIDialogImpl.OnDismissListener {
    private static int MAX_COUNT = 3;//最多多少列
    @BindView(R.id.recycler_view)
    RRecyclerView mRecyclerView;
    @BindView(R.id.tag_layout)
    ItemInfoLayout mTagLayout;
    @BindView(R.id.top_box)
    SwitchCompat mTopBox;
    @BindView(R.id.share_box)
    SwitchCompat mShareBox;
    boolean isFirst = true;
    @BindView(R.id.input_view)
    ExEditText mInputView;
    private ResizeAdapter mResizeAdapter;
    /**
     * 选择的图片
     */
    private ArrayList<Luban.ImageItem> photos;
    private List<Tag> mSelectorTags = new ArrayList<>();
    /**
     * 上传之后的媒体文件路径
     */
    private List<String> mUploadMedias = new ArrayList<>();
    /**
     * 选择的标签
     */
    private Action1<List<Tag>> mListAction1;
    private OssControl mOssControl;

    public PublishDynamicUIView(ArrayList<Luban.ImageItem> photos) {
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
        if (size == 4) {
            count = 2;
        } else if (size >= MAX_COUNT) {
            count = MAX_COUNT;
        } else {
            count = size % MAX_COUNT;
        }
        return count;
    }

    /**
     * 根据数量, 返回多少行
     */
    private static int getLineCount(int size) {
        int count;
        if (size == 4) {
            count = 2;
        } else {
            count = (int) Math.ceil(size * 1.f / MAX_COUNT);
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

        mListAction1 = new Action1<List<Tag>>() {
            @Override
            public void call(List<Tag> tags) {
                if (tags.isEmpty()) {
                } else {
                    Tag tag = tags.get(0);
                    mSelectorTags.clear();
                    if (isFirst) {
                        mSelectorTags.add(tag);
                        mTagLayout.setItemDarkText(tag.getName());
                    } else {
                        StringBuilder builder = new StringBuilder();
                        for (Tag t : tags) {
                            builder.append(t.getName());
                            builder.append(" ");
                        }
                        mSelectorTags.addAll(tags);
                        mTagLayout.setItemDarkText(builder.toString());
                    }
                }
                isFirst = false;
            }
        };
        TagsControl.getTags(mListAction1);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString("发布动态")
                .setShowBackImageView(true)
                .addRightItem(TitleBarPattern.TitleBarItem.build(R.drawable.send_forward_dynamic_n, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //
                        mOssControl = new OssControl(PublishDynamicUIView.this);
                        List<String> files = new ArrayList<>();
                        for (Luban.ImageItem item : photos) {
                            files.add(item.thumbPath);
                        }
                        mOssControl.uploadCircleImg(files);
                    }
                }));
    }

    /**
     * 发布动态
     */
    private void publish() {
        add(RRetrofit.create(SocialService.class)
                .publish(Param.buildMap("uid:" + UserCache.getUserAccount(),
                        "tags:" + RUtils.connect(mSelectorTags),
                        "media_type:3",
                        "media:" + RUtils.connect(mUploadMedias),
                        "is_top:" + (mTopBox.isChecked() ? 1 : 0),
                        "open_location:" + (mShareBox.isChecked() ? 1 : 0),
                        "content:" + mInputView.string(),
                        "address:" + getAddress(),
                        "lng:" + getLongitude(),
                        "lat:" + getLatitude()))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        T_.show(s);
                        finishIView();
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        HnLoading.hide();
                    }
                })
        );
    }

    private String getAddress() {
        AmapBean lastLocation = RAmap.getLastLocation();
        if (lastLocation == null) {
            return "";
        }
        return lastLocation.address;
    }

    private String getLatitude() {
        AmapBean lastLocation = RAmap.getLastLocation();
        if (lastLocation == null) {
            return "";
        }
        return String.valueOf(lastLocation.latitude);
    }

    private String getLongitude() {
        AmapBean lastLocation = RAmap.getLastLocation();
        if (lastLocation == null) {
            return "";
        }
        return String.valueOf(lastLocation.longitude);
    }

    @Override
    public void onViewCreate() {
        super.onViewCreate();
        //ImagePickerHelper.startImagePicker(mActivity, false, true, false, true, 9);
        TagsControl.getTags(null);//拉取一下标签
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Observable<ArrayList<Luban.ImageItem>> observable = Image.onActivityResult(mActivity, requestCode, resultCode, data);
        if (observable != null) {
            observable.subscribe(new BaseSingleSubscriber<ArrayList<Luban.ImageItem>>() {
                @Override
                public void onStart() {
                    super.onStart();
                    HnLoading.show(mILayout, false);
                }

                @Override
                public void onNext(ArrayList<Luban.ImageItem> strings) {
                    if (BuildConfig.DEBUG) {
                        Luban.logFileItems(mActivity, strings);
                    }
                    photos = strings;
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

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        RAmap.startLocation();
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        RAmap.stopLocation();
    }

    @OnClick({R.id.top_layout, R.id.share_layout})
    public void onLayoutClick(View view) {
        switch (view.getId()) {
            case R.id.top_layout:
                mTopBox.setChecked(!mTopBox.isChecked());
                break;
            case R.id.share_layout:
                mShareBox.setChecked(!mShareBox.isChecked());
                break;
        }
    }

    /**
     * 选择标签
     */
    @OnClick(R.id.tag_layout)
    public void onTagClick() {
        isFirst = false;
        startIView(new TagsUIView(mListAction1, mSelectorTags));
    }

    @Override
    public void onUploadStart() {
        HnLoading.show(mILayout).addDismissListener(this);
    }

    @Override
    public void onUploadSucceed(List<String> list) {
        mUploadMedias = list;
        publish();
    }

    @Override
    public void onUploadFailed(int code, String msg) {
        T_.show(msg);
        HnLoading.hide();
    }

    @Override
    public void onDismiss() {
        if (mOssControl != null) {
            mOssControl.setCancel(true);
        }
    }

    /**
     * 适配器
     */
    public class ResizeAdapter extends RBaseAdapter<Luban.ImageItem> {

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
//            deleteImageView.setBackgroundColor(Color.RED);
            int padding = (int) ResUtil.dpToPx(mContext, 6);
            deleteImageView.setPadding(padding, padding, padding, padding);
            deleteImageView.setVisibility(View.GONE);

            relativeLayout.addView(imageView, new ViewGroup.LayoutParams(-1, -1));
            RelativeLayout.LayoutParams deleteParams = new RelativeLayout.LayoutParams(-2, -2);
            deleteParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            View clickView = new View(mContext);
            clickView.setId(R.id.click_view);
            clickView.setBackgroundResource(R.drawable.base_bg_selector);
            relativeLayout.addView(clickView, new ViewGroup.LayoutParams(-1, -1));
            relativeLayout.addView(deleteImageView, deleteParams);

            return relativeLayout;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, final Luban.ImageItem bean) {
            int size = getHeight(mAllDatas.size());
            setHeight(holder.itemView, size);
            Glide.with(mContext).load(bean.thumbPath).override(size, size).placeholder(R.drawable.zhanweitu_1)
                    .into(holder.imgV(R.id.image_view));
            holder.v(R.id.click_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImagePickerHelper.startImagePicker(mActivity, false, false, false, true, 9);
                }
            });
            holder.v(R.id.click_view).setOnLongClickListener(new View.OnLongClickListener() {
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
                    ImagePickerHelper.deleteItemFromSelected(bean.path);
                    deleteItem(bean);
                    resetData(mAllDatas);
                }
            });
        }

        @Override
        public void resetData(List<Luban.ImageItem> datas) {
            super.resetData(datas);
            int size = datas.size();
            double line = Math.ceil(size * 1.f / MAX_COUNT);
            setHeight(mRecyclerView, (int) ((int) (line * getHeight(size)) +
                    Math.max(0, line - 1) * ResUtil.dpToPx(mActivity.getResources(), 6)));
            ((GridLayoutManager) mRecyclerView.getLayoutManager())
                    .setSpanCount(getColumnCount(size));
        }
    }
}
