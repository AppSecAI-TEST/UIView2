package com.hn.d.valley.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/06 14:42
 * 修改人员：Robi
 * 修改时间：2017/01/06 14:42
 * 修改备注：
 * Version: 1.0.0
 */
public class HnDefaultPagerView extends TextView {
    public HnDefaultPagerView(Context context) {
        super(context);
    }

    public HnDefaultPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnDefaultPagerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HnDefaultPagerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setTextColor(getResources().getColor(R.color.main_text_color_dark));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.default_text_size));
        setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.default_page, 0, 0);
        setGravity(Gravity.CENTER);
    }
}
