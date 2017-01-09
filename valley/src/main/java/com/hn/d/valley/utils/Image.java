package com.hn.d.valley.utils;

import android.app.Activity;
import android.content.Intent;

import com.angcyo.uiview.github.luban.Luban;
import com.lzy.imagepicker.ImagePickerHelper;

import java.util.ArrayList;

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
        final ArrayList<String> images = ImagePickerHelper.getImages(activity, requestCode, resultCode, data);
        final boolean origin = ImagePickerHelper.isOrigin(requestCode, resultCode, data);
        if (images.isEmpty()) {
            return null;
        }
        return Luban.luban2(activity, images);
    }

}
