package com.hn.d.valley.main.avchat.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.utils.permissionCompat.SettingsCompat;
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

    private static final String TAG = FloatView.class.getSimpleName();

    private Context mContext;
    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;

    // private view
    private FrameLayout small_size_preview_layout;
    private LinearLayout small_size_preview;
    private LinearLayout ll_micro_preview;
    private AVChatVideoRender small_preview_render;
    private AVChatVideoRender micro_preview_render;
    private ImageView smallSizePreviewCoverImg;
    private ImageView iv_max_sclae;
    private ImageView iv_audio_flag;

    private float mTouchStartX;
    private float mTouchStartY;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean mDraging;

    private boolean mIsRight;
    private boolean init ;

    private OnClickListener mActionListener;

    public FloatView(@NonNull Context context) {
        super(context);
        mContext = context;

        init();
    }

    private void init() {

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
        mWmParams.y = 0;

        // 设置悬浮窗口长宽数据
        mWmParams.width = LayoutParams.WRAP_CONTENT;
        mWmParams.height = LayoutParams.WRAP_CONTENT;
        addView(createView(mContext));
        mWindowManager.addView(this, mWmParams);

        init = true;
        hide();
    }

    private View createView(Context mContext) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_avchat_float, null);

        small_size_preview_layout = (FrameLayout) view.findViewById(R.id.small_size_preview_layout);
        small_size_preview = (LinearLayout) view.findViewById(R.id.small_size_preview);
        small_preview_render = (AVChatVideoRender) view.findViewById(R.id.small_preview_render);
        micro_preview_render = (AVChatVideoRender) view.findViewById(R.id.micro_preview_render);
        smallSizePreviewCoverImg = (ImageView) view.findViewById(R.id.smallSizePreviewCoverImg);
        ll_micro_preview = (LinearLayout) view.findViewById(R.id.ll_micro_preview);
        iv_max_sclae = (ImageView) view.findViewById(R.id.iv_max_sclae);
        iv_audio_flag = (ImageView) view.findViewById(R.id.iv_audio_flag);

        int width = mScreenWidth / 4;
        int height = (int) (width * (mScreenHeight * 1.0f / mScreenWidth));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width,height);
        small_size_preview.setLayoutParams(params);

        view.setOnTouchListener(this);
//        view.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                T_.show("被点击了");
//            }
//        });

        view.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        return view;
    }

    public void checkPermission() {
        if (SettingsCompat.canDrawOverlays(mContext)) {
            attach();
        } else {
            detach();
        }
    }

    public void attach() {
        if (getParent() == null) {
            init();
        }
    }

    public void detach() {
//        try {
//            mWm.removeViewImmediate(this);
//        } catch (Exception e) {
//
//        }
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

    public void setActionListener (OnClickListener listener) {
        this.mActionListener = listener;
        if (getChildCount() == 1) {
            View view = getChildAt(0);
            view.setOnClickListener(listener);
        }
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


    public void relocate() {
        mWmParams.x = 0;
        mWmParams.y = 0;
        mWindowManager.updateViewLayout(this,mWmParams);
    }

    public void initSmallSurfaceView(String account,String largeAccount){
        L.d(TAG,"initSmallSurfaceView  " + account + "laregaccount : " + largeAccount  +  "boo : " + UserCache.getUserAccount().equals(account));

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(largeAccount)) {
            return;
        }
        /**
         * 设置画布，加入到自己的布局中，用于呈现视频图像
         * account 要显示视频的用户帐号
         */
        smallSizePreviewCoverImg.setVisibility(GONE);
        small_size_preview.setVisibility(VISIBLE);
        micro_preview_render.setVisibility(VISIBLE);
        if (UserCache.getUserAccount().equals(account)) {
            AVChatManager.getInstance().setupLocalVideoRender(micro_preview_render, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
            AVChatManager.getInstance().setupRemoteVideoRender(largeAccount, small_preview_render, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        } else {

        }
    }

    public boolean stopSmallSurfacePreview() {
        return AVChatManager.getInstance().stopVideoPreview();
    }

    public void hide(){
//        T_.show("悬浮窗隐藏");
        if (!init) {
            return;
        }
        if (getVisibility() != GONE) {
            setVisibility(GONE);
//            stopSmallSurfacePreview();
        }

    }

    public void show() {
        if (!init){
            //先检测悬浮窗权限
            if (SettingsCompat.canDrawOverlays(mContext)) {
                init();
            } else {
                T_.show("没有获取悬浮窗权限!");
                return;
            }
        }

        if (getVisibility() != VISIBLE) {
            relocate();
            showVideoUI();
            iv_max_sclae.setVisibility(VISIBLE);
            micro_preview_render.setVisibility(VISIBLE);
            setVisibility(VISIBLE);

            // 5秒后隐藏 micro 窗口
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    iv_max_sclae.setVisibility(GONE);
                    micro_preview_render.setVisibility(GONE);
                }
            },5000);
        }
    }

    public void showAudio() {
        if (!init){
            //先检测悬浮窗权限
            if (SettingsCompat.canDrawOverlays(mContext)) {
                init();
            } else {
                T_.show("没有获取悬浮窗权限!");
                return;
            }
        }

        if (getVisibility() != VISIBLE) {
            relocate();
            showAudioUI();
            setVisibility(VISIBLE);
        }
    }

    private void showVideoUI() {
        small_size_preview.setVisibility(VISIBLE);
        ll_micro_preview.setVisibility(VISIBLE);
        iv_max_sclae.setVisibility(VISIBLE);
        smallSizePreviewCoverImg.setVisibility(VISIBLE);
        iv_audio_flag.setVisibility(GONE);

    }

    private void showAudioUI() {
        iv_audio_flag.setVisibility(VISIBLE);
        small_size_preview.setVisibility(GONE);
        ll_micro_preview.setVisibility(GONE);
        iv_max_sclae.setVisibility(GONE);
        smallSizePreviewCoverImg.setVisibility(GONE);
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
