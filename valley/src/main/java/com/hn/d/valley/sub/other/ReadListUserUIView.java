package com.hn.d.valley.sub.other;

import android.view.View;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.UserListModel;
import com.hn.d.valley.service.SocialService;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/19 18:12
 * 修改人员：Robi
 * 修改时间：2017/04/19 18:12
 * 修改备注：
 * Version: 1.0.0
 */
public class ReadListUserUIView extends UserInfoTimeRecyclerUIView {

    private String discuss_id;

    public ReadListUserUIView(String discuss_id) {
        this.discuss_id = discuss_id;
    }

    @Override
    protected int getTitleResource() {
        return R.string.read_list_title;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(SocialService.class)
                .readList(Param.buildMap("discuss_id:" + discuss_id, "page:" + page))
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
