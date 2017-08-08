package com.m3b.rblibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/08/08 14:17
 * 修改人员：Robi
 * 修改时间：2017/08/08 14:17
 * 修改备注：
 * Version: 1.0.0
 */
public class ShapeImageView extends AppCompatImageView {

    Drawable shapeDrawable;
    Rect shapeRect;
    RectF srcRectF = new RectF();
    RectF desRectF = new RectF();

    public ShapeImageView(Context context) {
        super(context);
    }

    public ShapeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 一个矩形(from), 在另一个矩形(to),居中显示时的宽高度
     */
    static int[] getCenterRectWidthHeight(RectF from, RectF to) {
        int[] result = new int[2];

        Matrix matrix = new Matrix();
        matrix.setRectToRect(from, to, Matrix.ScaleToFit.CENTER);
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);

        result[0] = (int) (matrixValues[Matrix.MSCALE_X] * from.width());//缩放之后的宽度
        result[1] = (int) (matrixValues[Matrix.MSCALE_Y] * from.height());//缩放之后的高度

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (shapeDrawable != null && shapeDrawable instanceof GradientDrawable) {
            srcRectF.set(0, 0, shapeRect.width(), shapeRect.height());
            desRectF.set(0, 0, getMeasuredWidth(), getMeasuredHeight());

            int[] widthHeight = getCenterRectWidthHeight(srcRectF, desRectF);
            shapeDrawable.setBounds(0, getMeasuredHeight() / 2 - widthHeight[1] / 2,
                    getMeasuredWidth(), getMeasuredHeight() / 2 + widthHeight[1] / 2);

            shapeDrawable.draw(canvas);
        }
    }

    public void setShapeDrawable(Drawable shapeDrawable) {
        this.shapeDrawable = shapeDrawable;
        shapeRect = shapeDrawable.getBounds();
    }
}
