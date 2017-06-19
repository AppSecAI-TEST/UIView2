package com.hn.d.valley.service;

import android.os.Build;

import com.angcyo.uiview.net.rsa.RSA;
import com.angcyo.uiview.utils.RUtils;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.cache.UserCache;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.angcyo.uiview.utils.RUtils.safe;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/06/19 18:20
 * 修改人员：Robi
 * 修改时间：2017/06/19 18:20
 * 修改备注：
 * Version: 1.0.0
 */
public class PhonesBody {

    String sign;//签名字段
    String sign_type;
    String app_version;
    String time_stamp;
    String client_type;
    String lang;

    //

    String uid;
    String phones;
    String phone_model;
    String device_id;

    public PhonesBody(String phones) {
        app_version = RUtils.getAppVersionName(ValleyApp.getApp());
        time_stamp = String.valueOf(System.currentTimeMillis() / 1000);
        client_type = "android";
        lang = String.valueOf(Param.getLang());
        sign_type = "RSA";

        //

        uid = UserCache.getUserAccount();
        phone_model = Build.MODEL;
        device_id = ValleyApp.getIMEI();

        this.phones = phones;

        //需要签名的字段
        List<String> signList = new ArrayList<>();

        //拿到所有字段
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            try {
                String fieldName = field.getName();
                if ("sign".equalsIgnoreCase(fieldName) || "sign_type".equalsIgnoreCase(fieldName)) {
                    //去除不需要签名的字段
                    continue;
                }
                field.setAccessible(true);/*设置访问权限*/
                signList.add(fieldName + "=" + field.get(this) /*拿到字段的值*/);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(signList);

        StringBuilder builder = new StringBuilder();
        for (String s : signList) {
            builder.append(s);
            builder.append("&");
        }

        //赋值签名字段
        sign = RSA.encode(safe(builder));
    }
}
