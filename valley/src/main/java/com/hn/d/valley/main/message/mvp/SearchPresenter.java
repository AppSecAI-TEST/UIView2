package com.hn.d.valley.main.message.mvp;

import com.angcyo.uiview.mvp.presenter.BasePresenter;
import com.angcyo.uiview.net.RRetrofit;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.Transform;
import com.hn.d.valley.base.rx.UIStringSubscriber;
import com.hn.d.valley.bean.SearchUserBean;
import com.hn.d.valley.main.message.service.SearchService;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/26 9:16
 * 修改人员：Robi
 * 修改时间：2016/12/26 9:16
 * 修改备注：
 * Version: 1.0.0
 */
public class SearchPresenter extends BasePresenter<Search.ISearchView> implements Search.ISearchPresenter {
    @Override
    public void onSearch(String uid, String key) {
        Map<String, String> map = new HashMap<>();
        map.put("uid", uid);
        map.put("key", key);

        add(RRetrofit.create(SearchService.class)
                .searchUser(Param.map(map))
                .compose(Transform.defaultStringSchedulers(mBaseView, SearchUserBean.class))
                .subscribe(new UIStringSubscriber<SearchUserBean, Search.ISearchView>(mBaseView) {
                    @Override
                    public void onSuccess(SearchUserBean bean) {
                        mBaseView.onSearchSucceed(bean);
                    }
                })
        );
    }
}
