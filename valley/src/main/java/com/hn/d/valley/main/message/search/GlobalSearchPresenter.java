package com.hn.d.valley.main.message.search;

import com.angcyo.uiview.mvp.presenter.BasePresenter;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.DataResourceRepository;
import com.hn.d.valley.main.message.query.TextQuery;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/27 15:49
 * 修改人员：hewking
 * 修改时间：2017/03/27 15:49
 * 修改备注：
 * Version: 1.0.0
 */
public class GlobalSearchPresenter extends BasePresenter<GlobalSearch.ISearchView> implements GlobalSearch.ISearchPresenter {


    @Override
    public void search(final TextQuery textQuery, final int... itemtypes) {

        Observable.unsafeCreate(new Observable.OnSubscribe<List<AbsContactItem>>() {
            @Override
            public void call(Subscriber<? super List<AbsContactItem>> subscriber) {
                subscriber.onStart();
                List<AbsContactItem> contactItems = DataResourceRepository.getInstance().provide(textQuery, itemtypes);
                subscriber.onNext(contactItems);
                subscriber.onCompleted();
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<AbsContactItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<AbsContactItem> o) {
                        mBaseView.onSearchSuccess(o);
                    }
                });
    }
}
