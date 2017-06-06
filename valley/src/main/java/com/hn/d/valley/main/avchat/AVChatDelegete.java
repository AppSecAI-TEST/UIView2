package com.hn.d.valley.main.avchat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.avchat.activity.AVChatActivity;
import com.hn.d.valley.main.avchat.constant.CallStateEnum;
import com.hn.d.valley.utils.RBus;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/31 15:07
 * 修改人员：hewking
 * 修改时间：2017/05/31 15:07
 * 修改备注：
 * Version: 1.0.0
 */
public class AVChatDelegete {

    private AVFloatViewService mFloatViewService;

    private Activity mActivity;

    private AVChatDelegete(){}

    private static class Holder {
        private static AVChatDelegete sinstance = new AVChatDelegete();
    }

    public static AVChatDelegete getInstance() {
        return Holder.sinstance;
    }

    public void bind(Activity activity) {
        this.mActivity = activity;
        bindService();
    }

    /**
     * 连接到Service
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mFloatViewService = ((AVFloatViewService.FloatServiceBinder) iBinder).getService();
            L.d("AVChatDelegete", "onServiceConnected " + mFloatViewService);
//            showFloatingView();
            setFloatActionListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    T_.show("返回视频聊天");
                    RBus.post(new AVChatFloatEvent(false));
                    AVChatActivity.launch(mActivity, CallStateEnum.VIDEO);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            hideFloatingView();
            mFloatViewService = null;
        }
    };

    /**
     * 显示悬浮图标
     */
    public void showFloatingView() {
        L.d("event","showFloatingView  " + "boo : " + mFloatViewService);
        if (mFloatViewService != null) {
            mFloatViewService.showFloat(UserCache.getUserAccount(),"");
        }
    }

    /**
     * 隐藏悬浮图标
     */
    public void hideFloatingView() {
        if (mFloatViewService != null) {
            mFloatViewService.hideFloat();
        }
    }

    public void setFloatActionListener(View.OnClickListener listener) {
        if (mFloatViewService != null) {
            mFloatViewService.setActionListener(listener);
        }
    }

    public void bindService() {
        try {
            Intent intent = new Intent(mActivity, AVFloatViewService.class);
            mActivity.startService(intent);
            mActivity.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
        }
    }

    /**
     * 销毁
     */
    public void destroy() {
        try {
            mActivity.stopService(new Intent(mActivity, AVFloatViewService.class));
            mActivity.unbindService(mServiceConnection);
        } catch (Exception e) {
        }
    }

}
