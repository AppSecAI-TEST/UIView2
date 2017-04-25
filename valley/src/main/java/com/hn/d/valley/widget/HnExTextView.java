package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import com.angcyo.uiview.widget.RExTextView;
import com.hn.d.valley.emoji.EmojiManager;
import com.hn.d.valley.emoji.MoonUtil;

import java.util.regex.Matcher;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：支持显示@, 支持显示 网页链接, 支持显示表情
 * 创建人员：Robi
 * 创建时间：2017/04/24 15:14
 * 修改人员：Robi
 * 修改时间：2017/04/24 15:14
 * 修改备注：
 * Version: 1.0.0
 */
public class HnExTextView extends RExTextView {
    public HnExTextView(Context context) {
        super(context);
    }

    public HnExTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnExTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void afterPattern(SpannableStringBuilder spanBuilder, CharSequence text) {
        Matcher matcher = EmojiManager.getPattern().matcher(text);
        int start = 0;
        int end = 0;

        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            String emot = matcher.group();
            Drawable d = MoonUtil.getEmotDrawable(getContext(), emot, MoonUtil.SMALL_SCALE);
            if (d != null) {
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                spanBuilder.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}
