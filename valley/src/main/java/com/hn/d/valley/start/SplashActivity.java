package com.hn.d.valley.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseActivity;
import com.hn.d.valley.main.MainUIView;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.utils.RBus;

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
public class SplashActivity extends BaseActivity {

    /**
     * 是否被踢
     */
    private static final String IS_KICKOUT = "is_kick_out";

    public static void launcher(Activity activity, boolean isKickOut) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.putExtra(IS_KICKOUT, isKickOut);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.default_window_tran_enter_anim,
                R.anim.default_window_tran_exit_anim);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RBus.unregister(this);
    }

    @Override
    protected void onLoadView() {
        if (RNim.isAutoLoginSucceed()) {
            startIView(new MainUIView(), true);
        } else {
            startIView(new LoginUIView(), false);
        }


//        Observable.timer(100, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Long>() {
//                    @Override
//                    public void call(Long aLong) {
//                        //MainActivity.launcher(SplashActivity.this);
//                        startIView(new LoginUIView(), false);
//                    }
//                });
    }

    @Override
    protected boolean enableWindowAnim() {
        return false;
    }
}
