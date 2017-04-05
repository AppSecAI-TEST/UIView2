package com.hn.d.valley.main.message.search;

import android.os.Bundle;
import android.view.View;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.main.message.query.TextQuery;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/01 19:29
 * 修改人员：hewking
 * 修改时间：2017/04/01 19:29
 * 修改备注：
 * Version: 1.0.0
 */
public class GlobalSingleSearchUIView extends GlobalSearchUIView2 {

    public static final String TEXT_QUERY = "textquery";

    private String textQuery ;

    public static void start(ILayout mLayout,String textQuery, Options options, int[] itemTypes) {
        Bundle bundle = new Bundle();
        bundle.putIntArray(ITEMTYPES,itemTypes);
        bundle.putString(TEXT_QUERY,textQuery);

        GlobalSearchUIView2 targetView = new GlobalSearchUIView2(options);
        mLayout.startIView(targetView, new UIParam().setBundle(bundle).setLaunchMode(UIParam.NORMAL));
    }

    public GlobalSingleSearchUIView(Options options) {
        super(options);
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        if (param != null) {
            textQuery = param.mBundle.getString(TEXT_QUERY);
        }
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mPresenter.search(new TextQuery(textQuery), itemTypes);

    }
}
