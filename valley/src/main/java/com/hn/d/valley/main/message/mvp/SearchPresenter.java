package com.hn.d.valley.main.message.mvp;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.mvp.presenter.BasePresenter;
import com.angcyo.uiview.net.RRetrofit;
import com.hn.d.valley.base.Bean;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.Transform;
import com.hn.d.valley.base.rx.UISubscriber;
import com.hn.d.valley.bean.SearchUserBean;
import com.hn.d.valley.main.message.service.SearchService;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;

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

        UISubscriber<SearchUserBean, Bean<SearchUserBean>, Search.ISearchView> subscriber =
                new UISubscriber<SearchUserBean, Bean<SearchUserBean>, Search.ISearchView>(mBaseView) {
                    @Override
                    public void onSuccess(Bean<SearchUserBean> searchUserBeanBean) {
                        super.onSuccess(searchUserBeanBean);
                        mBaseView.onSearchSucceed(searchUserBeanBean);
                    }
                };

//        add(RRetrofit.create(SearchService.class)
//                .search(Param.map(map))
//                .compose(Transform.<Response<String>, Search.ISearchView>defaultSchedulers(mBaseView))
//                .map(new Func1<Response<String>, Object>() {
//                    @Override
//                    public Object call(Response<String> stringResponse) {
//
//
//                    }
//                }))
////                .subscribe(subscriber));
//                ;
        add(RRetrofit.create(SearchService.class)
                .search(Param.map(map))
                .compose(Transform.defaultStringSchedulers(mBaseView, SearchUserBean.class))
                .subscribe(new Subscriber<SearchUserBean>() {
                    @Override
                    public void onCompleted() {
                        L.e("");
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.e("");
                    }

                    @Override
                    public void onNext(SearchUserBean searchUserBean) {
                        L.e("");
                    }
                })
        );
    }
}
