package com.hn.d.valley.main.avchat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;

import com.angcyo.library.utils.L;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.avchat.widget.FloatView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/25 15:46
 * 修改人员：hewking
 * 修改时间：2017/05/25 15:46
 * 修改备注：
 * Version: 1.0.0
 */
public class AVFloatViewService extends Service {


    FloatView mFloatView;

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatView = new FloatView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyFloat();
    }


    public void destroyFloat() {
        if ( mFloatView != null ) {
            mFloatView.destroy();
        }
        mFloatView = null;
    }

    public void showFloat(String account) {
        L.d("AVFloatViewService","showFloat  " + account + "boo : " + mFloatView);
        if ( mFloatView != null ) {
            mFloatView.show();
            mFloatView.initSmallSurfaceView(account);
        }
    }

    public void setActionListener (View.OnClickListener listener) {

        if (mFloatView != null) {
            mFloatView.setActionListener(listener);
        }

    }

    public boolean stopSmallSurfacePreview() {
        if (mFloatView != null) {
            return mFloatView.stopSmallSurfacePreview();
        }else {
            return true;
        }
    }

    public void hideFloat() {
        if ( mFloatView != null ) {
            mFloatView.hide();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new FloatServiceBinder();
    }

    public class FloatServiceBinder extends Binder{
        public AVFloatViewService getService() {
            return AVFloatViewService.this;
        }
    }
}
