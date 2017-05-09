package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.angcyo.uiview.skin.SkinHelper;
import com.hn.d.valley.R;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.skin.SkinUtils;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/09 14:47
 * 修改人员：Robi
 * 修改时间：2017/05/09 14:47
 * 修改备注：
 * Version: 1.0.0
 */
public class HnBigPlayView extends AppCompatImageView {

    Paint mPaint;
    RectF oval;

    boolean isPlaying;
    private int curProgress = 0;
    private float mStrokeWidth;

    public HnBigPlayView(Context context) {
        super(context);
        initView();
    }

    public HnBigPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HnBigPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(SkinHelper.getSkin().getThemeSubColor());
        mStrokeWidth = 4 * getResources().getDisplayMetrics().density;
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        oval = new RectF();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setPlaying(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
    }

    private void drawProgress(Canvas canvas) {
        if (curProgress <= 0) {
            return;
        }

        int width2 = getMeasuredWidth() / 2;
        int height2 = getMeasuredHeight() / 2;

        int r = Math.max(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                getMeasuredHeight() - getPaddingBottom() - getPaddingTop()) / 2;

        canvas.save();
        canvas.translate(width2, height2);
        //绘制大圈
        //mPaint.setColor(ContextCompat.getColor(getContext(), R.color.base_text_color_dark));
        //canvas.drawCircle(0, 0, r, mPaint);

        float v = mStrokeWidth / 2;//去掉笔触的宽度,否则在边缘的时候, 只能绘制出一半的笔触大小
        //绘制进度圈
        oval.set(-r + v, -r + v, r - v, r - v);

        float angel = calcAngel();
        canvas.drawArc(oval, -90, angel, false, mPaint);
        if (angel >= 360) {
            //setDownloadState(RDownloadView.DownloadState.FINISH);
        }


        canvas.restore();
    }

    /**
     * 设置当前的进度
     */
    public void setCurProgress(int curProgress) {
        this.curProgress = curProgress;
        postInvalidate();
    }

    private float calcAngel() {
        return curProgress * 360f / 100;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean play) {
        isPlaying = play;
        if (isPlaying) {
            switch (SkinUtils.getSkin()) {
                case SkinManagerUIView.SKIN_BLUE:
                    setImageResource(R.drawable.icon_pause_big_blue);
                    break;
                case SkinManagerUIView.SKIN_GREEN:
                    setImageResource(R.drawable.icon_pause_big_green);
                    break;
                default:
                    setImageResource(R.drawable.icon_pause_big_grey);
                    break;
            }
        } else {
            switch (SkinUtils.getSkin()) {
                case SkinManagerUIView.SKIN_BLUE:
                    setImageResource(R.drawable.icon_play_big_s_blue);
                    break;
                case SkinManagerUIView.SKIN_GREEN:
                    setImageResource(R.drawable.icon_play_big_s_green);
                    break;
                default:
                    setImageResource(R.drawable.icon_play_big_s_grey);
                    break;
            }
        }
    }
}
