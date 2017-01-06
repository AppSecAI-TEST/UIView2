package com.hn.d.valley.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：阅读数
 * 创建人员：Robi
 * 创建时间：2017/01/06 10:33
 * 修改人员：Robi
 * 修改时间：2017/01/06 10:33
 * 修改备注：
 * Version: 1.0.0
 */
public class HnViewCountTextView extends TextView {
    public HnViewCountTextView(Context context) {
        super(context);
    }

    public HnViewCountTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnViewCountTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HnViewCountTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text + "阅", type);
    }
}
