package com.hn.d.valley.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：性别和等级一体
 * 创建人员：Robi
 * 创建时间：2017/01/05 17:02
 * 修改人员：Robi
 * 修改时间：2017/01/05 17:02
 * 修改备注：
 * Version: 1.0.0
 */
public class HnGenderView extends TextView {
    public HnGenderView(Context context) {
        super(context);
    }

    public HnGenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnGenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HnGenderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.default_text_size10));
        setTextColor(Color.WHITE);
        setGravity(Gravity.CENTER_VERTICAL);
        final float density = getResources().getDisplayMetrics().density;
        setPadding((int) (density * 4), 0, 0, 0);
        setMinWidth((int) (density * 20));
        if (getTag() == null) {
            setTag("1");
        }
    }

    @Override
    public void setTag(Object tag) {
        super.setTag(tag);
        if (tag.equals("1")) {
            setBackgroundResource(R.drawable.gender_boy);
        } else if (tag.equals("2")) {
            setBackgroundResource(R.drawable.gender_gril);
        } else {
            setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    /**
     * 设置行性别和等级
     */
    public void setGender(String sex, String level) {
        setTag(sex);
        setText(level);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText("V" + text, type);
    }
}
