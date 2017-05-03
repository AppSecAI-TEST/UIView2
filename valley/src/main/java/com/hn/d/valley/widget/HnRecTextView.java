package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/03 15:42
 * 修改人员：Robi
 * 修改时间：2017/05/03 15:42
 * 修改备注：
 * Version: 1.0.0
 */
public class HnRecTextView extends RTextView {
    boolean isRec = false;

    public HnRecTextView(Context context) {
        super(context);
    }

    public HnRecTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnRecTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setText("REC");
        setRec(false);
        setCompoundDrawablePadding((int) (getResources().getDisplayMetrics().density * 3));
    }

    public void setRec(boolean rec) {
        isRec = rec;

        final Drawable[] compoundDrawables = getCompoundDrawables();
        Drawable drawable;
        if (isRec) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_record_s);
            setTextColor(ContextCompat.getColor(getContext(), R.color.base_dark_red));
        } else {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_record_n);
            setTextColor(ContextCompat.getColor(getContext(), R.color.base_text_color_dark));
        }

        setCompoundDrawablesWithIntrinsicBounds(drawable,
                compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
    }
}
