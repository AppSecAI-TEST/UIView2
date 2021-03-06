package com.hn.d.valley.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.angcyo.uiview.utils.TimeUtil;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/16 19:12
 * 修改人员：Robi
 * 修改时间：2017/01/16 19:12
 * 修改备注：
 * Version: 1.0.0
 */
public class HnTimeTextView extends AppCompatTextView {
    public HnTimeTextView(Context context) {
        super(context);
    }

    public HnTimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnTimeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            try {
                String timeString = TimeUtil.getTimeShowString(Long.valueOf(String.valueOf(text)) * 1000, false);
                super.setText(timeString, type);
                return;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        super.setText(text, type);
    }
}
