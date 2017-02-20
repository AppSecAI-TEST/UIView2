package com.hn.d.valley.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.angcyo.uiview.github.luban.Luban;
import com.lzy.imagepicker.ImagePickerHelper;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/09 11:18
 * 修改人员：Robi
 * 修改时间：2017/01/09 11:18
 * 修改备注：
 * Version: 1.0.0
 */
public class Image {
    public static Observable<ArrayList<Luban.ImageItem>> onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        return onActivityResult(activity, requestCode, resultCode, data, null);
    }

    public static Observable<ArrayList<Luban.ImageItem>> onActivityResult(Activity activity, int requestCode, int resultCode,
                                                                          Intent data, List<Luban.ImageItem> oldItems) {
        final ArrayList<String> images = ImagePickerHelper.getImages(activity, requestCode, resultCode, data);
        final boolean origin = ImagePickerHelper.isOrigin(requestCode, resultCode, data);
        if (images.isEmpty()) {
            return null;
        }

        if (oldItems != null && !oldItems.isEmpty()) {
            for (int i = images.size() - 1; i >= 0; i--) {
                String path = images.get(i);
                for (Luban.ImageItem item : oldItems) {
                    if (TextUtils.equals(item.path, path)) {
                        images.remove(i);
                    }
                }
            }
        }

        return Luban.luban2(activity, images);
    }

    public static Luban.ImageItem isInList(List<Luban.ImageItem> itemList, String path) {
        if (itemList != null && !itemList.isEmpty()) {
            for (Luban.ImageItem item : itemList) {
                if (TextUtils.equals(item.path, path)) {
                    return item;
                }
            }
        }
        return null;
    }
}
