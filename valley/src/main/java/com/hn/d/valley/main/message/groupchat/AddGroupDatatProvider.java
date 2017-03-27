package com.hn.d.valley.main.message.groupchat;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.FriendListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.ContactService;

import java.util.List;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by hewking on 2017/3/9.
 */
public class AddGroupDatatProvider {
    public void provide(CompositeSubscription subscription , final Action1<List<FriendBean>> action) {
        subscription.add(RRetrofit.create(ContactService.class)
                .friends(Param.buildMap("uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(FriendListModel.class))
                .subscribe(new BaseSingleSubscriber<FriendListModel>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        action.call(null);
                    }

                    @Override
                    public void onSucceed(FriendListModel bean) {
                        super.onSucceed(bean);
                        if(bean == null || bean.getData_list().size() == 0 ) {
                            action.call(null);
                            return;
                        }
                        List<FriendBean> data_list = bean.getData_list();
                        action.call(data_list);
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                    }
                }));
    }
}
