package com.hn.d.valley.skin;

import android.content.Context;

import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.hn.d.valley.R;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.orhanobut.hawk.Hawk;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/01 17:50
 * 修改人员：Robi
 * 修改时间：2017/04/01 17:50
 * 修改备注：
 * Version: 1.0.0
 */
public class SkinUtils {
    public static ISkin getSkin(Context context) {
        int skin = Hawk.get(SkinManagerUIView.SKIN_KEY, SkinManagerUIView.SKIN_BLACK);
        ISkin iSkin = SkinHelper.getSkin();
        switch (skin) {
            case SkinManagerUIView.SKIN_BLACK:
                iSkin = new BlackSkin(context);
                break;
            case SkinManagerUIView.SKIN_BLUE:
                iSkin = new BlueSkin(context);
                break;
            case SkinManagerUIView.SKIN_GREEN:
                iSkin = new GreenSkin(context);
                break;
        }
        return iSkin;
    }

    public static int getSkinStyle() {
        int skin = Hawk.get(SkinManagerUIView.SKIN_KEY, SkinManagerUIView.SKIN_BLACK);
        int style = R.style.AppTheme;
        switch (skin) {
            case SkinManagerUIView.SKIN_BLACK:
                style = R.style.BlackTheme;
                break;
            case SkinManagerUIView.SKIN_BLUE:
                style = R.style.BlueTheme;
                break;
            case SkinManagerUIView.SKIN_GREEN:
                style = R.style.GreenTheme;
                break;
        }
        return style;
    }
}
