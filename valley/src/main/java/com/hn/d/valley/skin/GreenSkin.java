package com.hn.d.valley.skin;

import android.content.Context;
import android.graphics.Color;

import com.angcyo.uiview.skin.BaseSkin;
import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/01 15:49
 * 修改人员：Robi
 * 修改时间：2017/04/01 15:49
 * 修改备注：
 * Version: 1.0.0
 */
public class GreenSkin extends BaseSkin {
    public GreenSkin(Context context) {
        super(context);
    }

    @Override
    public String skinName() {
        return mContext.getString(R.string.green);
    }

    @Override
    public int getThemeColor() {
        return Color.parseColor("#0BD1A0");
    }

    @Override
    public int getThemeSubColor() {
        return Color.parseColor("#0BD1A0");
    }

    @Override
    public int getThemeDarkColor() {
        return Color.parseColor("#800BD1A0");
    }
}
