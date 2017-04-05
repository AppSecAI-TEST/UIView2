package com.hn.d.valley.main.message.search;

import com.angcyo.uiview.mvp.presenter.BasePresenter;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.DataResourceRepository;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.main.message.query.TextQuery;
import com.hn.d.valley.sub.other.SingleRSubscriber;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by hewking on 2017/3/30.
 */
public class GlobalSearchDetailPresenter extends BasePresenter<GlobalSearch.ISearchDetailView> implements GlobalSearch.ISearchDetailPresenter  {


    @Override
    public void search(final TextQuery textQuery) {

        Observable.unsafeCreate(new Observable.OnSubscribe<List<AbsContactItem>>() {
            @Override
            public void call(Subscriber<? super List<AbsContactItem>> subscriber) {
                subscriber.onStart();
                List<AbsContactItem> contactItems = DataResourceRepository.getInstance().provide(textQuery, ItemTypes.MSG);
                subscriber.onNext(contactItems);
                subscriber.onCompleted();
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSingleSubscriber<List<AbsContactItem>>() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mBaseView.onRequestFinish();
                    }

                    @Override
                    public void onSucceed(List<AbsContactItem> bean) {
                        super.onSucceed(bean);
                        mBaseView.onSearchSuccess(bean);
                    }
                });

    }
}
