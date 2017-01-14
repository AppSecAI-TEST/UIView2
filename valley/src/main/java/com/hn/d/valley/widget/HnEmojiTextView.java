package com.hn.d.valley.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.hn.d.valley.emoji.MoonUtil;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/14 14:01
 * 修改人员：Robi
 * 修改时间：2017/01/14 14:01
 * 修改备注：
 * Version: 1.0.0
 */
public class HnEmojiTextView extends TextView {
    public HnEmojiTextView(Context context) {
        super(context);
    }

    public HnEmojiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnEmojiTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HnEmojiTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            SpannableString spannableString = MoonUtil.makeSpannableStringTags(getContext(),
                    String.valueOf(text), 0.45f, ImageSpan.ALIGN_BOTTOM, false);
            super.setText(spannableString, type);
            return;
        }
        super.setText(text, type);
    }
}
