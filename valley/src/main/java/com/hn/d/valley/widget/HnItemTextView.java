package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：支持左图标的文本按钮
 * 创建人员：Robi
 * 创建时间：2017/01/06 18:58
 * 修改人员：Robi
 * 修改时间：2017/01/06 18:58
 * 修改备注：
 * Version: 1.0.0
 */
public class HnItemTextView extends AppCompatTextView {
    public HnItemTextView(Context context) {
        this(context, null);
    }

    public HnItemTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.base_mdpi));
        setClickable(true);
        setBackgroundResource(R.drawable.default_bg_selector);
        int padding = getResources().getDimensionPixelOffset(R.dimen.base_ldpi);
        setPadding(padding + getPaddingLeft(), getPaddingTop(), padding + getPaddingRight(), getPaddingBottom());
        setGravity(Gravity.CENTER_VERTICAL);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.default_text_size));
        setTextColor(getResources().getColor(R.color.main_text_color_dark));
        setHintTextColor(getResources().getColor(R.color.base_text_color_dark));
    }

    public void setLeftIco(@DrawableRes int leftIco) {
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(leftIco),
                compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
    }

}
