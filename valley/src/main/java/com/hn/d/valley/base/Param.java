package com.hn.d.valley.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.angcyo.uiview.net.rsa.RSA;
import com.angcyo.uiview.utils.RUtils;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.cache.UserCache;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/14 14:13
 * 修改人员：Robi
 * 修改时间：2016/12/14 14:13
 * 修改备注：
 * Version: 1.0.0
 */
public class Param {
    /**
     * en_US  英文
     * zh_CN  中文
     * zh_TW  繁体
     */
    /**
     * 获取系统语言
     */
    public static int getLang() {
        Resources resources = ValleyApp.getApp().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        String language = config.locale.getLanguage();
        if (language.contains("zh")) {
            return 1;
        } else if (language.contains("en")) {
            return 3;
        }
        return 1;
    }

    /**
     * 【目前支持三种：1-中文简体，2-中文繁体，3-英文】
     */
    public static void changeLang(int n) {
        Resources resources = ValleyApp.getApp().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        if (n == 1) {
            config.locale = Locale.CHINESE;
        } else {
            config.locale = Locale.ENGLISH;
        }
        Hawk.put(Constant.LANG, n);
        resources.updateConfiguration(config, dm);
    }

    /**
     * app_version	是	string	APP版本号
     * time_stamp	是	int	请求时间戳【精确到秒，10位】
     * client_type	是	String	终端类型【android/ios】
     * lang	否	int	接口使用的语言【目前支持三种：1-中文简体，2-中文繁体，3-英文】
     */
    public static Map<String, String> map(Map<String, String> map) {
        return map(map, false);
    }

    public static Map<String, String> map(Map<String, String> map, boolean isInfo) {
        Map<String, String> result = new HashMap<>();
        ArrayList<String> signList = new ArrayList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            if (!TextUtils.isEmpty(value)) {
                result.put(key, value);
                signList.add(key + "=" + value);
            }
        }

        long time = System.currentTimeMillis() / 1000;
        if (isInfo) {
            signList.add("timestamp=" + time);
            result.put("timestamp", String.valueOf(time));

            /*资讯API支持*/
            signList.add("e_type=RSA");
            result.put("e_type", "RSA");


        } else {
            /*版本信息*/
            String versionName = RUtils.getAppVersionName(ValleyApp.getApp());
            signList.add("app_version=" + versionName);
            result.put("app_version", versionName);

            /*时间戳*/
            signList.add("time_stamp=" + time);
            result.put("time_stamp", String.valueOf(time));

            /*终端类型*/
            signList.add("client_type=android");
            result.put("client_type", "android");

          /*语言*/
            signList.add("lang=" + getLang());
            result.put("lang", String.valueOf(getLang()));
        }

        Collections.sort(signList);

        StringBuilder builder = new StringBuilder();
        for (String s : signList) {
            builder.append(s);
            builder.append("&");
        }

        String signString;

        if (isInfo) {
            signString = RSA.encodeInfo(safe(builder)).replaceAll("/", "_a").replaceAll("\\+", "_b").replaceAll("=", "_c");
        } else {
            signString = RSA.encode(safe(builder));
        }

        result.put("sign", signString);

        if (isInfo) {
        } else {
            result.put("sign_type", "RSA");
        }
        return result;
    }

    /**
     * 安全的去掉字符串的最后一个字符
     */
    public static String safe(StringBuilder stringBuilder) {
        return stringBuilder.substring(0, Math.max(0, stringBuilder.length() - 1));
    }

    public static <T> String connect(List<T> list) {
        if (list == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (T bean : list) {
            builder.append(bean);
            builder.append(",");
        }

        return safe(builder);
    }

    /**
     * 组装参数之后, 并签名
     */
    public static Map<String, String> buildMap(String... args) {
        return map(build(args), false);
    }

    /**
     * 组装参数之后, 并签名 (资讯API)
     */
    public static Map<String, String> buildInfoMap(String... args) {
        return map(build(false, args), true);
    }

    /**
     * 组装参数
     */
    public static Map<String, String> build(String... args) {
        return build(true, args);
    }

    public static Map<String, String> build(boolean uid, String... args) {
        final Map<String, String> map = new HashMap<>();
        if (uid) {
            map.put("uid", UserCache.getUserAccount());//默认传输uid参数
            map.put("limit", Constant.DEFAULT_PAGE_DATA_COUNT + "");
        }
        foreach(new OnPutValue() {
            @Override
            public void onValue(String key, String value) {
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }
            }
        }, args);
        return map;
    }

    private static void foreach(OnPutValue onPutValue, String... args) {
        if (onPutValue == null || args == null) {
            return;
        }
        for (String str : args) {
            String[] split = str.split(":");
            if (split.length >= 2) {
                String first = split[0];
                onPutValue.onValue(first, str.substring(first.length() + 1));
            }
        }
    }

    interface OnPutValue {
        void onValue(String key, String value);
    }
}
