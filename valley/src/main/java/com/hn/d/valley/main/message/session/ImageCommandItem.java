package com.hn.d.valley.main.message.session;

import android.content.Intent;
import android.view.View;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/18 15:10
 * 修改人员：hewking
 * 修改时间：2017/05/18 15:10
 * 修改备注：
 * Version: 1.0.0
 */
public class ImageCommandItem extends CommandItemInfo{

    public ImageCommandItem(int icoResId, String text, View.OnClickListener clickListener) {
        super(icoResId, text, clickListener);
    }

    @Override
    protected void onClick() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
