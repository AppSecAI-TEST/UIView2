package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.angcyo.uiview.skin.SkinHelper;
import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：置顶ImageView
 * 创建人员：Robi
 * 创建时间：2017/05/03 09:05
 * 修改人员：Robi
 * 修改时间：2017/05/03 09:05
 * 修改备注：
 * Version: 1.0.0
 */
public class HnTopImageView extends AppCompatImageView {

    boolean isTop;

    public HnTopImageView(Context context) {
        super(context);
    }

    public HnTopImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnTopImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setTop(false);
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setTop(!isTop);
//            }
//        });
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.zhiding_fabudongtai_n);
        if (isTop) {
            drawable.mutate().setColorFilter(SkinHelper.getSkin().getThemeSubColor(), PorterDuff.Mode.MULTIPLY);
        }
        setImageDrawable(drawable);
    }
}
