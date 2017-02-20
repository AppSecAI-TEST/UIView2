package com.hn.d.valley.main.me.setting;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;

import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RDragRecyclerView;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.adapter.HnAddImageAdapter;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserControl;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.utils.Image;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnLoading;
import com.lzy.imagepicker.ImagePickerHelper;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：编辑个人资料
 * 创建人员：Robi
 * 创建时间：2017/02/17 17:42
 * 修改人员：Robi
 * 修改时间：2017/02/17 17:42
 * 修改备注：
 * Version: 1.0.0
 */
public class EditInfoUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private HnAddImageAdapter mHnAddImageAdapter;

    @Override
    protected int getTitleResource() {
        return R.string.edit_into_title;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().addRightItem(TitleBarPattern.TitleBarItem
                .build(mActivity.getResources().getString(R.string.finish), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_drag_recycler_view;
        }
        return R.layout.item_info_layout;
    }

    @Override
    public void onViewCreate() {
        super.onViewCreate();
        ImagePickerHelper.clearAllSelectedImages();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选中的图片列表
        final ArrayList<String> images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data);
        final List<Luban.ImageItem> oldDatas = mHnAddImageAdapter == null ? null : mHnAddImageAdapter.getAllDatas();

        //请求压缩图片,排除掉已经压缩的图片
        Observable<ArrayList<Luban.ImageItem>> observable = Image.onActivityResult(mActivity, requestCode, resultCode, data, oldDatas);
        if (observable != null) {
            observable.subscribe(new BaseSingleSubscriber<ArrayList<Luban.ImageItem>>() {
                @Override
                public void onStart() {
                    super.onStart();
                    HnLoading.show(mILayout, false);
                }

                @Override
                public void onSucceed(ArrayList<Luban.ImageItem> strings) {
                    if (BuildConfig.DEBUG) {
                        Luban.logFileItems(mActivity, strings);
                    }
                    //新增的图片,和原有的图片匹配, 进行组合, 产生新的图片集合
                    List<Luban.ImageItem> imageItemList = new ArrayList<>();
                    for (int i = 0; i < images.size(); i++) {
                        String needPath = images.get(i);
                        Luban.ImageItem inListItem = Image.isInList(strings, needPath);
                        if (inListItem != null) {
                            imageItemList.add(inListItem);
                        } else {
                            inListItem = Image.isInList(oldDatas, needPath);
                            imageItemList.add(inListItem);
                        }
                    }
                    if (mHnAddImageAdapter != null) {
                        mHnAddImageAdapter.resetData(imageItemList);
                    }

                }

                @Override
                public void onEnd() {
                    super.onEnd();
                    HnLoading.hide();
                }
            });
        }
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        final int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
        final int left = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);

        final int size = 2 * line;

        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();

        //照片墙
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, final int posInData, ViewItemInfo dataBean) {
                final RDragRecyclerView dragRecyclerView = holder.v(R.id.drag_recycler_view);
                ViewGroup.LayoutParams layoutParams = dragRecyclerView.getLayoutParams();
                layoutParams.width = ScreenUtil.screenWidth;

                int itemHeight = layoutParams.width / 3;
                layoutParams.height = itemHeight * 2 + size;
                dragRecyclerView.setLayoutParams(layoutParams);

                //分割线
                dragRecyclerView.addItemDecoration(RExItemDecoration.build(new RExItemDecoration.ItemDecorationCallback() {
                    @Override
                    public Rect getItemOffsets(LinearLayoutManager layoutManager, int position) {
                        Rect rect = new Rect(0, 0, 0, 0);
                        if (position == 1) {
                            rect.set(size, 0, size, 0);
                        } else if (position > 2) {
                            rect.top = size;
                            if (position == 4) {
                                rect.left = size;
                                rect.right = size;
                            }
                        }
                        return rect;
                    }

                    @Override
                    public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {

                    }
                }));

                mHnAddImageAdapter = new HnAddImageAdapter(mActivity);
                mHnAddImageAdapter.setItemHeight(itemHeight);

                dragRecyclerView.setAdapter(mHnAddImageAdapter);

                //事件处理
                mHnAddImageAdapter.setAddListener(new HnAddImageAdapter.OnAddListener() {

                    @Override
                    public void onAddClick(View view, int position, Luban.ImageItem item) {
                        ImagePickerHelper.startImagePicker(mActivity, true, false, false, true, 6);
                    }

                    @Override
                    public void onItemClick(View view, int position, Luban.ImageItem item) {
                        ImagePagerUIView.start(mOtherILayout, view,
                                PhotoPager.getImageItems2(mHnAddImageAdapter.getAllDatas()), position);
                    }

                    @Override
                    public void onItemLongClick(View view, int position, Luban.ImageItem item) {
                        mHnAddImageAdapter.setDeleteModel(dragRecyclerView);
                    }

                    @Override
                    public void onDeleteClick(View view, int position, Luban.ImageItem item) {
                        mHnAddImageAdapter.getAllDatas().remove(position);
                        if (position == 5) {
                            mHnAddImageAdapter.notifyItemChanged(position);
                        } else {
                            mHnAddImageAdapter.notifyItemRemoved(position);
                        }
                    }
                });
            }
        }));


        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("昵称");
                infoLayout.setItemDarkText(userInfoBean.getUsername());
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("ID");
                infoLayout.setItemDarkText(UserCache.getUserAccount());
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("我的二维码");
                infoLayout.setDarkDrawableRes(R.drawable.qr_code);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("性别");
                infoLayout.setItemDarkText(UserControl.getSex(mActivity, userInfoBean.getSex()));
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("出生日期");
                infoLayout.setItemDarkText("1000-01-01");
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("地区");
                infoLayout.setItemDarkText(userInfoBean.getProvince_name() + " " + userInfoBean.getCity_name());
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("个性签名");
                infoLayout.setItemDarkText(userInfoBean.getIntroduce());
            }
        }));
    }
}
