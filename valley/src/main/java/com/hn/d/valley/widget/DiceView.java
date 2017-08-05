package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.hn.d.valley.R;

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
public class DiceView extends android.support.v7.widget.AppCompatImageView {

    private int[] dice_res = {R.drawable.shaizi1_0000,R.drawable.shaizi1_0001
            ,R.drawable.shaizi1_0002,R.drawable.shaizi1_0003
            ,R.drawable.shaizi1_0004,R.drawable.shaizi1_0005
            ,R.drawable.shaizi1_0006,R.drawable.shaizi1_0007
            ,R.drawable.shaizi1_0008,R.drawable.shaizi1_0009
            ,R.drawable.shaizi1_0010,R.drawable.shaizi1_0011
            ,R.drawable.shaizi1_0012,R.drawable.shaizi1_0013};

    private Context context;
    private Paint mPaint;
    private Rect mRect;

    private int mWidth;
    private int mHeight;

    public DiceView setDiceValue(int diceValue) {
        this.diceValue = diceValue;
        init();
        return this;
    }

    private int diceValue;

    public DiceView(Context context) {
        this(context,null);
    }

    public DiceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DiceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mRect = new Rect();

    }

    private void init() {
        setBackgroundResource(R.drawable.dice_anim);

        final AnimationDrawable drawable = (AnimationDrawable)getBackground();
        drawable.start();

        int duration = 0;
        for(int i=0;i<drawable.getNumberOfFrames();i++){
            duration += drawable.getDuration(i);
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                drawable.stop();
            }
        },duration);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRect.set(getPaddingLeft(),getPaddingTop(),w - getPaddingRight(), h - getPaddingBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawBitmap(getBitmap(dice_res[0]),getPaddingLeft(),getPaddingTop(),mPaint);
//        canvas.drawBitmap(getBitmap(dice_res[1]),mRect,mPaint);

    }

    public Bitmap getBitmap(@DrawableRes int res) {
        return BitmapFactory.decodeResource(context.getResources(),res);
    }
}
