package com.hn.d.valley.main.message.groupchat;

import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.FriendListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.friend.IDataResource;
import com.hn.d.valley.service.ContactService;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by hewking on 2017/3/9.
 */
public class AddGroupDatatProvider implements IDataResource.IDataActionProvider<List<FriendBean>> {

    @Override
    public void provide(CompositeSubscription subscription, final RequestCallback<List<FriendBean>> requestCallback) {
        subscription.add(RRetrofit.create(ContactService.class)
                .friends(Param.buildMap("uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(FriendListModel.class))
                .subscribe(new BaseSingleSubscriber<FriendListModel>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        requestCallback.onStart();
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if (isError) {

                            requestCallback.onError(e.getMsg());
                        }
                    }

                    @Override
                    public void onSucceed(FriendListModel bean) {
                        super.onSucceed(bean);
                        if (bean == null || bean.getData_list().size() == 0) {
                            requestCallback.onSuccess(null);
                            return;
                        }
                        List<FriendBean> data_list = bean.getData_list();
                        requestCallback.onSuccess(data_list);
                    }

                }));
    }
}
