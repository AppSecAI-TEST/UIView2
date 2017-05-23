package com.hn.d.valley.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;

import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.umeng.UM;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseActivity;
import com.hn.d.valley.control.MainControl;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.start.LauncherUIView;
import com.hn.d.valley.start.WelcomeUIView;
import com.hn.d.valley.utils.RBus;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.ClientType;
import com.orhanobut.hawk.Hawk;

import rx.functions.Action0;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：启动页
 * 创建人员：Robi
 * 创建时间：2016/12/12 17:19
 * 修改人员：Robi
 * 修改时间：2016/12/12 17:19
 * 修改备注：
 * Version: 1.0.0
 */
public class HnSplashActivity extends BaseActivity {

    /**
     * 是否被踢
     */
    private static final String IS_KICKOUT = "is_kick_out";
    Intent mIntent;

    public static void launcher(Activity activity, boolean isKickOut) {
        Intent intent = new Intent(activity, HnSplashActivity.class);
        intent.putExtra(IS_KICKOUT, isKickOut);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.base_tran_to_left_enter,
                R.anim.base_tran_to_left_exit);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mIntent = intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RBus.register(this);
        mIntent = getIntent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RBus.unregister(this);
    }

    @Override
    protected void onLoadView(Intent intent) {
        final String versionName = RUtils.getAppVersionName(this);
        if (Hawk.get(versionName, true)) {
            startIView(new LauncherUIView(new Action0() {
                @Override
                public void call() {
                    Hawk.put(versionName, false);
                    mLayout.replaceIView(new WelcomeUIView());
                }
            }));
        } else if (RNim.isAutoLoginSucceed()) {
            //startIView(new MainUIView(), true);
            HnUIMainActivity.launcher(this, true);
            finish();
        } else {
            startIView(new WelcomeUIView().setEnableClipMode(UIBaseView.ClipMode.CLIP_START), false);
            MainControl.checkCrash(mLayout);
        }

        onParseIntent();

//        P2PChatUIView.start(mLayout, "50033");


//        Observable.timer(100, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Long>() {
//                    @Override
//                    public void call(Long aLong) {
//                        //HnUIMainActivity.launcher(HnSplashActivity.this);
//                        startIView(new LoginUIView(), false);
//                    }
//                });
    }

    @Override
    protected boolean enableWindowAnim() {
        return false;
    }

    private void onParseIntent() {
        boolean isKickout = mIntent.getBooleanExtra(IS_KICKOUT, false);
        if (isKickout) {
            //帐号被踢
            int type = NIMClient.getService(AuthService.class).getKickedClientType();
            String client;
            switch (type) {
                case ClientType.Web:
                    client = "网页端";
                    break;
                case ClientType.Windows:
                    client = "电脑端";
                    break;
                case ClientType.REST:
                    client = "服务端";
                    break;
                default:
                    client = "其他移动设备";
                    break;
            }
            startIView(UIDialog.build()
                    .setDialogContent(getString(R.string.kick_out_tip, client))
                    .setGravity(Gravity.CENTER));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UM.onActivityResult(requestCode, resultCode, data);
    }
}
