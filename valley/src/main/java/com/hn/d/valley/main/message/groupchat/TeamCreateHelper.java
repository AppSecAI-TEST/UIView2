package com.hn.d.valley.main.message.groupchat;

import android.graphics.Bitmap;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.file.AttachmentStore;
import com.angcyo.uiview.utils.storage.StorageType;
import com.angcyo.uiview.utils.storage.StorageUtil;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.GroupInfoBean;
import com.hn.d.valley.bean.GroupMemberBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.utils.NetUtils;
import com.hn.d.valley.widget.groupView.JoinBitmaps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hewking on 2017/3/16.
 */
public class TeamCreateHelper {

    public static void createAndSavePhoto(UIBaseRxView iLayout, final List<AbsContactItem> selectorData, final RequestCallback<GroupInfoBean> callback) {

        if (selectorData == null || selectorData.size() < 2) {
            T_.show("成员不能小于两个人");
            return;
        }

        callback.onStart();

        final List<FriendBean> beans = new ArrayList<>();
        for (AbsContactItem item : selectorData) {
            beans.add(((ContactItem) item).getFriendBean());
        }
        iLayout.add(Observable.just(beans)
                .flatMap(new Func1<List<FriendBean>, Observable<List<String>>>() {
                    @Override
                    public Observable<List<String>> call(List<FriendBean> beanList) {
                        List<String> urls = new ArrayList<>();
                        for (FriendBean bean : beanList) {
                            urls.add(bean.getAvatar());
                        }
                        return Observable.just(urls);
                    }
                })
                .flatMap(new Func1<List<String>, Observable<List<Bitmap>>>() {
                    @Override
                    public Observable<List<Bitmap>> call(List<String> s) {
                        List<Bitmap> bitmaps = new ArrayList<>();
                        s.add(UserCache.getUserAvatar());
                        for (String url : s) {
                            Bitmap bitmap = NetUtils.createBitmapFromUrl(url);
                            if (bitmap == null) {
                                continue;
                            }
                            bitmaps.add(bitmap);
                        }
                        return Observable.just(bitmaps);
                    }
                }).map(new Func1<List<Bitmap>, String>() {
                    @Override
                    public String call(List<Bitmap> bitmaps) {
                        String filePath = StorageUtil.getDirectoryByDirType(StorageType.TYPE_IMAGE) + UUID.randomUUID().toString() + ".png";
                        //ios 目前大小为 40
                        AttachmentStore.saveBitmap(JoinBitmaps.createGroupBitCircle(bitmaps, 100, 100, ValleyApp.getApp().getApplicationContext()), filePath, true);
                        for (Bitmap bitmap : bitmaps) {
                            if (!bitmap.isRecycled()) {
                                bitmap.recycle();
                            }
                        }
                        File file = new File(filePath);
                        if (!file.exists()) {
                            return null;
                        }
                        return filePath;
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return OssHelper.uploadAvatorImg(s);
                    }
                })
                .flatMap(new Func1<String, Observable<GroupInfoBean>>() {
                    @Override
                    public Observable<GroupInfoBean> call(String s) {
                        String circleUrl = OssHelper.getAvatorUrl(s);
                        return RRetrofit.create(GroupChatService.class)
                                .add(Param.buildMap("uid:" + UserCache.getUserAccount()
                                        , "to_uid:" + RUtils.connect(beans)
                                        , "avatar:" + circleUrl))
                                .compose(Rx.transformer(GroupInfoBean.class));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSingleSubscriber<GroupInfoBean>() {

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if (isError) {
                            callback.onError(e.getMsg());
                        }
                    }

                    @Override
                    public void onSucceed(GroupInfoBean s) {
                        callback.onSuccess(s);
                    }
                }));

    }


    public static void invite(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, final RequestCallback requestCallback, final String gid, final Action1<Boolean> successAction) {
        requestCallback.onStart();

        uiBaseDataView.add(Observable.just(absContactItems)
                .flatMap(new Func1<List<AbsContactItem>, Observable<List<FriendBean>>>() {
                    @Override
                    public Observable<List<FriendBean>> call(List<AbsContactItem> absContactItem) {
                        List<FriendBean> beanlist = new ArrayList();
                        for (AbsContactItem bean : absContactItem) {
                            beanlist.add(((ContactItem) bean).getFriendBean());
                        }
                        return Observable.just(beanlist);
                    }
                }).flatMap(new Func1<List<FriendBean>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<FriendBean> friendBeen) {
                        return RRetrofit.create(GroupChatService.class)
                                .invite(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + RUtils.connect(friendBeen), "gid:" + gid))
                                .compose(Rx.transformer(String.class));
                    }
                }).subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        requestCallback.onSuccess(bean);
                        if (successAction != null) {
                            successAction.call(true);
                        }
                    }


                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if (isError) {
                            requestCallback.onError(e.getMsg());
                        }
                    }
                }));

//        uiBaseDataView.add(RRetrofit.create(GroupChatService.class)
//                .add(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + RUtils.connect(absContactItems)))
//                .compose(Rx.transformer(String.class))
//                .subscribe(new BaseSingleSubscriber<String>() {
//                    @Override
//                    public void onSucceed(String bean) {
//                        requestCallback.onSuccess(bean);
//                    }
//                }));
    }

    public static void changeOwner(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, final String gid, final RequestCallback callback, final Action1<Boolean> action) {
        callback.onStart();

        uiBaseDataView.add(Observable.just(absContactItems)
                .flatMap(new Func1<List<AbsContactItem>, Observable<List<GroupMemberBean>>>() {
                    @Override
                    public Observable<List<GroupMemberBean>> call(List<AbsContactItem> absContactItem) {
                        List<GroupMemberBean> beanlist = new ArrayList();
                        for (AbsContactItem bean : absContactItem) {
                            beanlist.add(((GroupMemberItem) bean).getMemberBean());
                        }
                        return Observable.just(beanlist);
                    }
                }).flatMap(new Func1<List<GroupMemberBean>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<GroupMemberBean> friendBeen) {
                        return RRetrofit.create(GroupChatService.class)
                                .changeOwner(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + RUtils.connect(friendBeen), "gid:" + gid))
                                .compose(Rx.transformer(String.class));
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        callback.onSuccess(bean);
                        action.call(true);
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if (isError) {
                            callback.onError(e.getMsg());
                        }
                    }
                }));
    }


}
