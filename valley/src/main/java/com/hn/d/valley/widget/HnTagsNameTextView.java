package com.hn.d.valley.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.widget.RFlowLayout;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;

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
public class HnTagsNameTextView extends RFlowLayout {
    public HnTagsNameTextView(Context context) {
        super(context);
    }

    public HnTagsNameTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTags(String tags) {
        final List<String> split = RUtils.split(String.valueOf(tags));
        int childCount = getChildCount();
        int size = split.size();
        if (childCount > size) {
            for (int i = 0; i < childCount - size; i++) {
                removeViewAt(getChildCount() - 1);
            }
        }

        for (int i = getChildCount(); i < size; i++) {
            addView(createTagView(), i);
        }

        for (int i = 0; i < size; i++) {
            ((RTextView) getChildAt(i)).setText(split.get(i));
        }
    }

    private View createTagView() {
        RTextView textView = new RTextView(getContext());
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.line_color));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.default_text_size10));
        textView.setBackgroundResource(R.drawable.base_line_border_selector);
        int offset = getResources().getDimensionPixelOffset(R.dimen.base_ldpi);
        textView.setPadding(2 * offset, offset, 2 * offset, offset);
        return textView;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = super.generateDefaultLayoutParams();
        params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
        return params;
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        LayoutParams params = super.generateLayoutParams(lp);
        params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
        return params;
    }
}
