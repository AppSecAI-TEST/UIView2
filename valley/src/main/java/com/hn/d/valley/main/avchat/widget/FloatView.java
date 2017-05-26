package com.hn.d.valley.main.avchat.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.UserCache;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/25 14:09
 * 修改人员：hewking
 * 修改时间：2017/05/25 14:09
 * 修改备注：
 * Version: 1.0.0
 */
public class FloatView extends FrameLayout implements View.OnTouchListener{

    private Context mContext;
    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;

    // private view
    private FrameLayout small_size_preview_layout;
    private LinearLayout small_size_preview;
    private AVChatVideoRender small_preview_render;
    private ImageView smallSizePreviewCoverImg;

    private float mTouchStartX;
    private float mTouchStartY;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean mDraging;

    private boolean mIsRight;


    public FloatView(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        this.mWmParams = new WindowManager.LayoutParams();
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        // 设置图片格式，效果为背景透明
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置为左侧置�?
        mWmParams.gravity = Gravity.RIGHT | Gravity.TOP;

        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        mWmParams.x = 0;
        mWmParams.y = mScreenHeight / 2;

        // 设置悬浮窗口长宽数据
        mWmParams.width = LayoutParams.WRAP_CONTENT;
        mWmParams.height = LayoutParams.WRAP_CONTENT;
        addView(createView(mContext));
        mWindowManager.addView(this, mWmParams);
    }

    private View createView(Context mContext) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_avchat_float, null);

        small_size_preview_layout = (FrameLayout) view.findViewById(R.id.small_size_preview_layout);
        small_size_preview = (LinearLayout) view.findViewById(R.id.small_size_preview);
        small_preview_render = (AVChatVideoRender) view.findViewById(R.id.small_preview_render);
        smallSizePreviewCoverImg = (ImageView) view.findViewById(R.id.smallSizePreviewCoverImg);

        view.setOnTouchListener(this);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                T_.show("被点击了");
            }
        });

        view.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        return view;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        int oldX = mWmParams.x;
        int oldY = mWmParams.y;
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE://横屏
                if ( mIsRight ) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
            case Configuration.ORIENTATION_PORTRAIT://竖屏
                if ( mIsRight ) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
        }
        mWindowManager.updateViewLayout(this, mWmParams);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);
                mDraging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 3
                        && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    mDraging = true;
                    // 更新浮动窗口位置参数
                    mWmParams.x = (int) (mScreenWidth - (x - mTouchStartX));
                    mWmParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(this, mWmParams);
                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (mWmParams.x >= mScreenWidth / 2) {
                    mWmParams.x = mScreenWidth;
                    mIsRight = true;
                } else if (mWmParams.x < mScreenWidth / 2) {
                    mIsRight = false;
                    mWmParams.x = 0;
                }
                mWindowManager.updateViewLayout(this, mWmParams);
                // 初始化
                mTouchStartX = mTouchStartY = 0;
                break;
        }
        return false;
    }

    public void initSmallSurfaceView(String account){
        /**
         * 设置画布，加入到自己的布局中，用于呈现视频图像
         * account 要显示视频的用户帐号
         */
        smallSizePreviewCoverImg.setVisibility(GONE);
        small_size_preview.setVisibility(VISIBLE);
        if (UserCache.getUserAccount().equals(account)) {
            AVChatManager.getInstance().setupLocalVideoRender(small_preview_render, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        } else {
            AVChatManager.getInstance().setupRemoteVideoRender(account, small_preview_render, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        }
    }

    public void hide(){
        small_size_preview_layout.setVisibility(GONE);
    }

    public void show() {
        small_size_preview_layout.setVisibility(VISIBLE);
    }

    private void removeFloatView() {
        try {
            mWindowManager.removeView(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void destroy() {
        removeFloatView();
    }
}
