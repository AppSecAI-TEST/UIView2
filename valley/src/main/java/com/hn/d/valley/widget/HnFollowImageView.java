package com.hn.d.valley.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/02/24 11:27
 * 修改人员：Robi
 * 修改时间：2017/02/24 11:27
 * 修改备注：
 * Version: 1.0.0
 */
public class HnFollowImageView extends HnGlideImageView {

    boolean isLoadingModel = false;

    public HnFollowImageView(Context context) {
        super(context);
    }

    public HnFollowImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isLoadingModel) {
            showLoading();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
    }

    public void setLoadingModel(boolean loadingModel) {
        isLoadingModel = loadingModel;
        if (isLoadingModel) {
            showLoading();
        } else {
            clearAnimation();
        }
    }

    void showLoading() {
        clearAnimation();
        setImageResource(R.drawable.loading_guanzhu);

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        startAnimation(rotateAnimation);
    }


}
