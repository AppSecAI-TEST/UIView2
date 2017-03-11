package com.hn.d.valley.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.angcyo.uiview.utils.RUtils;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/06 11:07
 * 修改人员：Robi
 * 修改时间：2017/01/06 11:07
 * 修改备注：
 * Version: 1.0.0
 */
public class HnTagsNameTextView extends TextView {
    public HnTagsNameTextView(Context context) {
        super(context);
    }

    public HnTagsNameTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnTagsNameTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HnTagsNameTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        final List<String> split = RUtils.split(String.valueOf(text));
        StringBuilder stringBuilder = new StringBuilder("#");
        for (String s : split) {
            stringBuilder.append("    ");
            stringBuilder.append(s);
        }
        super.setText(stringBuilder.toString(), type);
    }
}
