package com.hn.d.valley.widget;

import android.graphics.drawable.AnimationDrawable;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.resources.ResUtil;
import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/27 17:00
 * 修改人员：Robi
 * 修改时间：2016/12/27 17:00
 * 修改备注：
 * Version: 1.0.0
 */
public class HnAudioRedordDialog extends UIIDialogImpl {
    static boolean isShowing = false;
    static HnAudioRedordDialog mUILoading;
    private Chronometer chronometer;

    private boolean canCancel = true;

    public HnAudioRedordDialog(boolean canCancel) {
        this.canCancel = canCancel;
    }

    /**
     * 显示
     */
    public static void hide() {
        if (isShowing && mUILoading != null) {
//            mUILoading.canCancel = true;
            mUILoading.finishDialog();
            mUILoading = null;
            isShowing = false;
        }
    }

    /**
     * 显示
     */
    public static HnAudioRedordDialog show(ILayout layout) {
        return show(layout, true);
    }

    public static HnAudioRedordDialog show(ILayout layout, boolean canCancel) {
        if (isShowing) {

        } else {
            mUILoading = new HnAudioRedordDialog(canCancel);
            layout.startIView(mUILoading, new UIParam().setLaunchMode(UIParam.SINGLE_TOP));
            isShowing = true;
        }
        return mUILoading;
    }

    @Override
    public Animation loadStartAnimation() {
        return null;
    }

    @Override
    public Animation loadFinishAnimation() {
        return null;
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        FrameLayout frameLayout = new FrameLayout(mActivity);

        chronometer = new Chronometer(mActivity);
        chronometer.setText("00:00");
        chronometer.setBackgroundColor(0x44334422);
        int size = (int) ResUtil.dpToPx(mActivity, 88);

        frameLayout.addView(chronometer, new ViewGroup.LayoutParams(size, size));
        dialogRootLayout.addView(frameLayout, new ViewGroup.LayoutParams(-2, -2));
        return frameLayout;
    }

    public void startTime(){
        if (chronometer != null) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
        }
    }

    public void stopTime(){
        if (chronometer != null) {
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
        }

    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        isShowing = false;
    }

    @Override
    public boolean canCanceledOnOutside() {
        return false;
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public boolean onBackPressed() {
        return canCancel;
    }
}
