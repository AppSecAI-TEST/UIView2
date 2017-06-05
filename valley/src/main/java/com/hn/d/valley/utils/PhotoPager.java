package com.hn.d.valley.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.widget.RImageView;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.library.fresco.PhotoPagerControl;
import com.lzy.imagepicker.bean.ImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/06 11:35
 * 修改人员：Robi
 * 修改时间：2017/01/06 11:35
 * 修改备注：
 * Version: 1.0.0
 */
public class PhotoPager {
    public static void init(final ILayout iLayout, TextIndicator textIndicatorView, ViewPager viewPager,
                            ArrayList<String> photos) {
        final ArrayList<ImageItem> imageItems = getImageItems(photos);
        new PhotoPagerControl(textIndicatorView, viewPager, photos)
                .setItemClickListener(new PhotoPagerControl.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, String url) {
                        ImagePagerUIView.start(iLayout, view, imageItems, position);
                    }
                });
    }

    @NonNull
    public static ArrayList<ImageItem> getImageItems(List<String> photos) {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        for (String s : photos) {
            if (TextUtils.isEmpty(s)) {
                continue;
            }
            final ImageItem imageItem = new ImageItem();
            imageItem.url = s;
            imageItems.add(imageItem);
        }
        return imageItems;
    }

    @NonNull
    public static ArrayList<ImageItem> getImageItems(List<String> photos, List<RImageView> imageList) {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        for (int i = 0; i < photos.size(); i++) {
            String s = photos.get(i);
            if (TextUtils.isEmpty(s)) {
                continue;
            }

            final ImageItem imageItem = new ImageItem();
            imageItem.url = s;

            if (imageList.size() > i) {
                imageItem.placeholderDrawable = imageList.get(i).copyDrawable();
            }
            imageItems.add(imageItem);
        }

        return imageItems;
    }

    @NonNull
    public static ArrayList<ImageItem> getImageItems(List<String> photos, Drawable curDrawable, int curDrawablePosition) {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        for (int i = 0; i < photos.size(); i++) {
            String s = photos.get(i);
            if (TextUtils.isEmpty(s)) {
                continue;
            }

            final ImageItem imageItem = new ImageItem();
            imageItem.url = s;

            if (curDrawablePosition == i) {
                imageItem.placeholderDrawable = curDrawable;
            }
            imageItems.add(imageItem);
        }

        return imageItems;
    }

    @NonNull
    public static ArrayList<ImageItem> getImageItems(String s) {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        final ImageItem imageItem = new ImageItem();
        imageItem.url = s;
        imageItems.add(imageItem);
        return imageItems;
    }

    @NonNull
    public static ArrayList<ImageItem> getImageItems2(List<Luban.ImageItem> photos) {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        for (Luban.ImageItem item : photos) {
            final ImageItem imageItem = new ImageItem();
            imageItem.path = item.path;
            imageItem.thumbPath = item.thumbPath;
            imageItems.add(imageItem);
        }
        return imageItems;
    }

}
