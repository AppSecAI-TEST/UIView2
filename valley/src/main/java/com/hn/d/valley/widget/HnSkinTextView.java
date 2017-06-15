package com.hn.d.valley.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.angcyo.uiview.skin.SkinHelper;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：主题文本颜色的文本View
 * 创建人员：Robi
 * 创建时间：2017/04/06 10:49
 * 修改人员：Robi
 * 修改时间：2017/04/06 10:49
 * 修改备注：
 * Version: 1.0.0
 */
public class HnSkinTextView extends AppCompatTextView {
    public HnSkinTextView(Context context) {
        super(context);
        initView();
    }

    public HnSkinTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        if (!isInEditMode()) {
            setTextColor(SkinHelper.getSkin().getThemeSubColor());
        }
    }
}
