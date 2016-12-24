package com.hn.d.valley.start;

import com.hn.d.valley.base.BaseActivity;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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
    @Override
    protected void onLoadView() {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        //MainActivity.launcher(SplashActivity.this);
                        startIView(new LoginUIView(), false);
                    }
                });
    }

    @Override
    protected boolean enableWindowAnim() {
        return false;
    }
}
