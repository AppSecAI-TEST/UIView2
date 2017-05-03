package com.hn.d.valley.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;

import java.io.File;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/03 14:45
 * 修改人员：Robi
 * 修改时间：2017/05/03 14:45
 * 修改备注：
 * Version: 1.0.0
 */
public class HnGlide {
    public static void displayFile(ImageView imageView, String filePath) {
        Glide.with(ValleyApp.getApp())
                .load(new File(filePath))
                .placeholder(R.drawable.zhanweitu_1)
                .into(imageView);
    }

    public static void displayUrl(ImageView imageView, String url) {
        Glide.with(ValleyApp.getApp())
                .load(url)
                .placeholder(R.drawable.zhanweitu_1)
                .into(imageView);
    }
}
