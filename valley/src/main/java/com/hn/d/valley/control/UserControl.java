package com.hn.d.valley.control;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/17 10:41
 * 修改人员：Robi
 * 修改时间：2016/12/17 10:41
 * 修改备注：
 * Version: 1.0.0
 */
public class UserControl {

    public static void setUserId(@NonNull TextView textView, @NonNull String id) {
        textView.setText("ID:" + id);
    }

    /**
     * auth_type	int	是否已认证【0-未认证，1-职场名人，2-娱乐明星，3-体育任务，4-政府人员】
     */
    public static String getAuthString(String type) {
        String auth = "";
        if ("0".equalsIgnoreCase(type)) {
            auth = ValleyApp.getApp().getResources().getString(R.string.not_auth);
        } else if ("1".equalsIgnoreCase(type)) {
            auth = ValleyApp.getApp().getResources().getString(R.string.auth_zcmr);
        } else if ("2".equalsIgnoreCase(type)) {
            auth = ValleyApp.getApp().getResources().getString(R.string.auth_ylmx);
        } else if ("3".equalsIgnoreCase(type)) {
            auth = ValleyApp.getApp().getResources().getString(R.string.auth_tyrw);
        } else if ("4".equalsIgnoreCase(type)) {
            auth = ValleyApp.getApp().getResources().getString(R.string.auth_zfry);
        }
        return auth;
    }
}
