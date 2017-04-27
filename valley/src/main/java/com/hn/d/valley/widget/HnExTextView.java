package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.widget.RExTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.emoji.EmojiManager;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.skin.SkinUtils;

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

    String mImageUrl;

    public HnExTextView(Context context) {
        super(context);
    }

    public HnExTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnExTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImage(String image) {
        mImageUrl = image;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (haveImage()) {
            super.setText(text + "`", type);
        } else {
            super.setText(text, type);
        }
    }

    protected boolean haveImage() {
        return !TextUtils.isEmpty(mImageUrl);
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

        if (haveImage()) {
            int drawableRes;
            switch (SkinUtils.getSkin()) {
                case SkinManagerUIView.SKIN_BLUE:
                    drawableRes = R.drawable.icon_picture_blue;
                    break;
                case SkinManagerUIView.SKIN_GREEN:
                    drawableRes = R.drawable.icon_picture_green;
                    break;
                default:
                    drawableRes = R.drawable.icon_picture_grey;
                    break;
            }

            spanBuilder.setSpan(new ImageTextSpan(getContext(), ImageTextSpan.initDrawable(getContext(), drawableRes, getTextSize()),
                            getContext().getString(R.string.picture_comment),
                            mImageUrl)
                            .setOnImageSpanClick(mOnImageSpanClick)
                            .setTextColor(SkinHelper.getSkin().getThemeSubColor()),
                    text.length() - 1, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
