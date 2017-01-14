package com.hn.d.valley.base.inner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/28 16:36
 * 修改人员：Robi
 * 修改时间：2016/12/28 16:36
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class RSupportActivity extends SupportActivity {
    protected RxPermissions mRxPermissions;
    protected RBaseViewHolder mViewHolder;
    protected RRootLayout mRRootLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        initWindow();

        setContentView(R.layout.activity_root_layout);

        mRRootLayout = (RRootLayout) findViewById(R.id.activity_root_layout);
        mViewHolder = new RBaseViewHolder(mRRootLayout);

        checkPermissions();

        if (savedInstanceState == null) {
            loadRootFragment(R.id.activity_root_layout, getRootFragment());
        }

        onActivityCreate();
    }

    private void checkPermissions() {
        mRxPermissions = new RxPermissions(this);
        mRxPermissions.requestEach(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA)
                .map(new Func1<Permission, String>() {
                    @Override
                    public String call(Permission permission) {
                        if (permission.granted) {
                            return "1";
                        }
                        return "0";
                    }
                })
                .scan(new Func2<String, String, String>() {
                    @Override
                    public String call(String s, String s2) {
                        return s + s2;
                    }
                })
                .last()
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        L.e(s);
                        if (s.contains("0")) {
                            finish();
                            notifyAppDetailView();
                            T_.show("必要的权限被拒绝!");
                        } else {

                        }
                    }
                });
    }

    protected abstract SupportFragment getRootFragment();

    protected void onActivityCreate() {
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        getWindow().getDecorView().setBackgroundDrawable(null);
        mRRootLayout.setOnSwipeListener(new RRootLayout.OnSwipeChangeListener() {
            @Override
            public void onSwipeClose() {
//                getTopFragment().popForSwipeBack();
                //onBackPressed();

                SupportFragment topFragment = getTopFragment();
                SupportFragment mPreFragment = null;
                List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                if (fragmentList != null && fragmentList.size() > 1) {
                    int index = fragmentList.indexOf(topFragment);
                    for (int i = index - 1; i >= 0; i--) {
                        Fragment fragment = fragmentList.get(i);
                        if (fragment != null && fragment.getView() != null) {
                            mPreFragment = (SupportFragment) fragment;
                            break;
                        }
                    }
                }

                if (mPreFragment == null) {
                    if (!topFragment.isDetached()) {
                        topFragment.popForSwipeBack();
                    }
                    finish();
                    overridePendingTransition(0, 0);
                } else {
                    mPreFragment.mLocking = true;
                    if (!topFragment.isDetached()) {
                        topFragment.popForSwipeBack();
                    }
                    mPreFragment.mLocking = false;
                }
            }

            @Override
            public void onSwipeStart() {

            }

            @Override
            public void onSwipeStartDragging() {

            }

            @Override
            public void onSwipeTo(float percent) {

            }
        });
    }

    @Override
    public void onExceptionAfterOnSaveInstanceState(Exception e) {
        // TODO: 16/12/7 在此可以监听到警告： Can not perform this action after onSaveInstanceState!
        // 建议在线上包中，此处上传到异常检测服务器（eg. 自家异常检测系统或Bugtags等崩溃检测第三方），来监控该异常
        e.printStackTrace();
    }

    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

    public void notifyAppDetailView() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * 窗口样式
     */
    protected void initWindow() {
        enableLayoutFullScreen();
    }

    /**
     * 激活布局全屏, View 可以布局在 StatusBar 下面
     */
    protected void enableLayoutFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

}
