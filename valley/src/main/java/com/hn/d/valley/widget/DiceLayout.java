package com.hn.d.valley.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.angcyo.uiview.utils.ScreenUtil;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/8/4
 * 修改人员：cjh
 * 修改时间：2017/8/4
 * 修改备注：
 * Version: 1.0.0
 */
public class DiceLayout extends LinearLayout {

    private int diceCount;
    private int[] diceValues;

    private Context context;

    public DiceLayout(Context context) {
        this(context,null);
    }

    public DiceLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DiceLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void init(int diceCount , int[] values,boolean anim){
        this.diceCount = diceCount;
        this.diceValues = values;
        removeAllViews();
        LayoutParams params = new LayoutParams(ScreenUtil.dip2px(60),ScreenUtil.dip2px(60));
        params.setLayoutDirection(HORIZONTAL);
        for (int i = 0 ; i < diceCount; i ++) {
            addView(new DiceView(context),params);
        }

        for (int i = 0 ; i < diceCount ; i ++) {
            ((DiceView)getChildAt(i)).setAnim(anim).setDiceValue(values[i]);
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }



}
