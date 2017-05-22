package com.hn.d.valley.main.message.session;

import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.view.View;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/18 14:14
 * 修改人员：hewking
 * 修改时间：2017/05/18 14:14
 * 修改备注：
 * Version: 1.0.0
 */
public class CommandItemInfo {
    @DrawableRes
    public int icoResId;
    public String text;
    private Container mContainer;

    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public CommandItemInfo(int icoResId, String text) {
        this.icoResId = icoResId;
        this.text = text;
    }

    public Container getContainer() {
        return mContainer;
    }

    public void setContainer(Container mContainer) {
        this.mContainer = mContainer;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

    }

    protected void onClick() {

    }
}
