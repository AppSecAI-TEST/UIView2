package com.hn.d.valley.main.friend;

import com.hn.d.valley.main.message.query.TextQuery;

import java.util.List;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by hewking on 2017/3/23.
 */
public interface IDataResource {

    interface IDataRepository {

        List<? extends AbsContactItem> provide(TextQuery query,int... types);

        List<? extends AbsContactItem> provide(int type, TextQuery query);
    }

    interface IDataProvider {
        List<? extends AbsContactItem> provide();

        List<? extends AbsContactItem> provide(TextQuery query);
    }

    interface IDataActionProvider<T> {
        void provide(CompositeSubscription subscription,Action1<T> action);
    }
}
