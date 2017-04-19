package com.hn.d.valley.sub.other;

import android.view.View;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.UserListModel;
import com.hn.d.valley.service.UserInfoService;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：新的 我的访客
 * 创建人员：Robi
 * 创建时间：2017/02/24 18:07
 * 修改人员：Robi
 * 修改时间：2017-4-19
 * 修改备注：
 * Version: 1.0.0
 */
public class MyVisitorUserUIView2 extends UserInfoTimeRecyclerUIView {

    @Override
    protected int getTitleResource() {
        return R.string.my_visitor_title;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(UserInfoService.class)
                .visitorList(Param.buildMap("page:" + page))
                .compose(Rx.transformer(UserListModel.class))
                .subscribe(new SingleRSubscriber<UserListModel>(this) {
                    @Override
                    protected void onResult(UserListModel bean) {
                        if (bean == null || bean.getData_list() == null || bean.getData_list().isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            setUserInfos(bean.getData_list());
                        }
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                        showNonetLayout(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadData();
                            }
                        });
                    }
                }));
    }
}
