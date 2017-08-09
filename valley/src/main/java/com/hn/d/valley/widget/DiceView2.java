package com.hn.d.valley.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.hn.d.valley.R;
import com.nineoldandroids.animation.AnimatorInflater;

import java.util.Random;


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
public class DiceView2 extends View {

    public static
    @DrawableRes
    int[] dice_res = {R.drawable.shaizi1_0000, R.drawable.shaizi1_0001
            , R.drawable.shaizi1_0002, R.drawable.shaizi1_0003
            , R.drawable.shaizi1_0004, R.drawable.shaizi1_0005
            , R.drawable.shaizi1_0006, R.drawable.shaizi1_0007
            , R.drawable.shaizi1_0008, R.drawable.shaizi1_0009
            , R.drawable.shaizi1_0010, R.drawable.shaizi1_0011
            , R.drawable.shaizi1_0012, R.drawable.shaizi1_0013};

    public static @DrawableRes int[]  cover_res = {R.drawable.dice_1,R.drawable.dice_2,R.drawable.dice_3,R.drawable.dice_4,R.drawable.dice_5,R.drawable.dice_6};

    private Context context;
    private Paint mPaint;
    private Rect mRect;

    // 执行时长
    private int time;
    // 执行次数
    private int frequency = 2;

    // 当前图片
    private int current;

    // 随机种子
    private Random random;

    // 是否结束动画
    private boolean flag;

    private int mWidth;
    private int mHeight;
    private boolean anim;

    public DiceView2 setDiceValue(int diceValue) {
        this.diceValue = diceValue;
        frequency = 2;
        init();
        return this;
    }

    private int diceValue;

    public DiceView2(Context context) {
        this(context, null);
    }

    public DiceView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiceView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mRect = new Rect();
//        init();
    }

    private void init() {

        random = new Random(System.nanoTime());
//        startAnim();
        play();
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
        mRect.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
    }

    public void startAnim() {
        if (!anim) {
            invalidate();
            return;
        }
        flag = false;
        int rv = random.nextInt(dice_res.length);
        ValueAnimator animator = ValueAnimator.ofInt(rv, dice_res.length - 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                current = (int) animation.getAnimatedValue();
                Log.v("onAnimationU value1", current + "");
                postInvalidateOnAnimation();
            }
        });

//        animator.setDuration()

        // rv 注意边界
        final ValueAnimator animator2 = ValueAnimator.ofInt(0, rv);

        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                current = (int) animation.getAnimatedValue();
                Log.v("onAnimationU value2", current + "");
                postInvalidateOnAnimation();
            }
        });

        final AnimatorSet set = new AnimatorSet();
//        set.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//                set.start();
//            }
//        });
        set.setDuration(500);
        set.play(animator2).after(animator);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                flag = true;
                Log.v("onAnimationEnd value2", current + "");
                postInvalidate();
            }
        });
        set.start();
    }

    public void play() {
        Log.v("onAnimvalue1 anim", anim + "");

        if (!anim) {
            flag = true;
            invalidate();
            return;
        }
//        int r = random.nextInt(dice_res.length);
        startThread();


    }

    public void startThread() {
        new GameThread().start();
    }

    public DiceView2 setAnim(boolean anim) {
        this.anim = anim;
        return this;
    }


    //线程来提醒UI线程不断重绘
    public class GameThread extends Thread {
        public void run() {
            flag = false;
            int r = random.nextInt(dice_res.length);
            // r - 13  0 - r
            long frameTime = 500 / 14;

            for (int k = 0; k < frequency; k++) {
                current = r;
                for (int i = r; i < dice_res.length; i++) {
                    try {
                        Thread.sleep(frameTime);
                        current++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    postInvalidate();
                }

                current = 0;
                for (int i = 0; i < r; i++) {
                    try {
                        Thread.sleep(frameTime);
                        current++;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    postInvalidate();
                }

            }

            flag = true;
            postInvalidate();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (flag) {
            Log.v("onAni diceValue", diceValue + "");
            canvas.drawBitmap(getBitmap(cover_res[diceValue - 1]), null, mRect, mPaint);
        } else {
            canvas.drawBitmap(getBitmap(dice_res[current]), null, mRect, mPaint);
        }
    }

    public Bitmap getBitmap(@DrawableRes int res) {
        return BitmapFactory.decodeResource(context.getResources(), res);
    }

    public Drawable getDrawable(@DrawableRes int res) {
        return context.getResources().getDrawable(res);
    }

}
