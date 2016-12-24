package com.hn.d.valley.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.angcyo.uiview.net.rsa.RSA;
import com.angcyo.uiview.utils.Utils;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.constant.Constant;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
        Map<String, String> result = new HashMap<>();
        StringBuilder builder = new StringBuilder();

        ArrayList<String> signList = new ArrayList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            if (!TextUtils.isEmpty(value)) {
                result.put(key, value);
                signList.add(key + "=" + value);
            }
        }

        /*版本信息*/
        String versionName = Utils.getAppVersionName(ValleyApp.getApp());
        signList.add("app_version=" + versionName);
        result.put("app_version", versionName);

        /*时间戳*/
        long time = System.currentTimeMillis() / 1000;
        signList.add("time_stamp=" + time);
        result.put("time_stamp", String.valueOf(time));

        /*终端类型*/
        signList.add("client_type=android");
        result.put("client_type", "android");

        /*语言*/
        signList.add("lang=" + getLang());
        result.put("lang", String.valueOf(getLang()));

        Collections.sort(signList);
        for (String s : signList) {
            builder.append(s);
            builder.append("&");
        }

        result.put("sign", RSA.encode(safe(builder)));
        result.put("sign_type", "RSA");
        return result;
    }

    /**
     * 安全的去掉字符串的最后一个字符
     */
    public static String safe(StringBuilder stringBuilder) {
        return stringBuilder.substring(0, Math.max(0, stringBuilder.length() - 1));
    }
}
