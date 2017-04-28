package com.hn.d.valley.main.message.groupchat;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RGroupItemDecoration;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.file.AttachmentStore;
import com.angcyo.uiview.utils.storage.StorageType;
import com.angcyo.uiview.utils.storage.StorageUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.GroupInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.friend.SearchUserUIView;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.utils.NetUtils;
import com.hn.d.valley.widget.HnIcoRecyclerView;
import com.hn.d.valley.widget.HnLoading;
import com.hn.d.valley.widget.HnRefreshLayout;
import com.hn.d.valley.widget.groupView.JoinBitmaps;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hewking on 2017/3/9.
 */
public class AddGroupChatUIView extends BaseUIView {

    public static final String SELECTED_UIDS = "SELECTED_UIDS";

    @BindView(R.id.friend_add_refreshlayout)
    HnRefreshLayout refreshLayout;
    @BindView(R.id.friend_add_recyclerview)
    RecyclerView recyclerView;
//    @BindView(R.id.rv_groupchat_icon)
//    HnIcoRecyclerView iconSelectedRv;

    private ContactSelectAdapter mGroupAdapter;

    private AddGroupDatatProvider datatProvider;

    private List<String> mSelectedUids;

    private Action2<Boolean,ContactItem> action = new Action2<Boolean, ContactItem>() {
        @Override
        public void call(Boolean aBoolean, ContactItem item) {
            HnIcoRecyclerView.IcoInfo icon ;
            FriendBean bean = item.getFriendBean();
//            if (aBoolean) {
//                icon = new HnIcoRecyclerView.IcoInfo(bean.getUid(),bean.getAvatar());
//                iconSelectedRv.getMaxAdapter().addLastItem(icon);
//            }else {
//                iconSelectedRv.remove(bean.getAvatar());
//            }

        }
    };

    public static void start(ILayout mLayout, List<String> uids) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_UIDS, (Serializable) uids);
        mLayout.startIView(new AddGroupChatUIView(), new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText("确定").setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T_.show("创建群聊");
                createGroupChat();
            }
        }));
        return super.getTitleBar().setTitleString("选择好友").setRightItems(rightItems);
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);

        if (param != null  && param.mBundle != null) {
            mSelectedUids = (List<String>) param.mBundle.getSerializable(SELECTED_UIDS);
        }

    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_friend_addgroupchat);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        init();
    }

    private void init() {

        refreshLayout.setRefreshDirection(HnRefreshLayout.TOP);
        refreshLayout.addRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(@RefreshLayout.Direction int direction) {
                if (direction == RefreshLayout.TOP) {
                    loadData();
                }
            }
        });

        mGroupAdapter = new ContactSelectAdapter(mActivity,new BaseContactSelectAdapter.Options(),recyclerView);
        datatProvider = new AddGroupDatatProvider();
//        mGroupAdapter.setAction(action);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.addItemDecoration(new RGroupItemDecoration(new FriendsControl.GroupItemCallBack(mActivity,mGroupAdapter)));

        recyclerView.setAdapter(mGroupAdapter);

        mGroupAdapter.setSelecteUids(mSelectedUids);

        loadData();
    }

    private void loadData() {
        datatProvider.provide(mSubscriptions, new RequestCallback<List<FriendBean>>() {
            @Override
            public void onStart() {
                showLoadView();
            }

            @Override
            public void onSuccess(List<FriendBean> beanList) {
                hideLoadView();
                refreshLayout.setRefreshEnd();

                List<AbsContactItem> datas = new ArrayList();
                datas.add(new FuncItem<>("搜索",new Action1<ILayout>() {
                    @Override
                    public void call(ILayout o) {
                        mOtherILayout.startIView(new SearchUserUIView());
                    }
                }));
                for (FriendBean bean : beanList) {
                    datas.add(new ContactItem(bean));
                }
                mGroupAdapter.resetData(datas);
            }

            @Override
            public void onError(String msg) {
                hideLoadView();
            }
        });
    }


    private void createGroupChat() {
        List<AbsContactItem> selectorData = mGroupAdapter.getSelectorData();
        if (selectorData == null || selectorData.size() < 2) {
            T_.show("成员不能小于两个人");
            return;
        }

        createAndSavePhoto(selectorData);
    }

    private void createAndSavePhoto(final List<AbsContactItem> selectorData) {
        HnLoading.show(mOtherILayout);
        final List<FriendBean> beans = new ArrayList<>();
        for (AbsContactItem item : selectorData) {
            beans.add(((ContactItem)item).getFriendBean());
        }
        add(Observable.just(beans)
                .flatMap(new Func1<List<FriendBean>, Observable<List<String>>>() {
                    @Override
                    public Observable<List<String>> call(List<FriendBean> beanList) {
                        List<String> urls = new ArrayList<>();
                        for(FriendBean bean : beanList){
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
                        for(String url : s ) {
                            Bitmap bitmap = NetUtils.createBitmapFromUrl(url);
                            if(bitmap == null) {
                                continue;
                            }
                            bitmaps.add(bitmap);
                        }
                        return Observable.just(bitmaps);
                    }
                }).map(new Func1<List<Bitmap>, String>() {
                    @Override
                    public String call(List<Bitmap> bitmaps) {
                        String filePath = StorageUtil.getDirectoryByDirType(StorageType.TYPE_IMAGE) +"/avatar2.png";
                        //ios 目前大小为 40
                        AttachmentStore.saveBitmap(JoinBitmaps.createGroupBitCircle(bitmaps,40,40,mActivity),filePath,true);
                        for (Bitmap bitmap : bitmaps) {
                            if (!bitmap.isRecycled()) {
                                bitmap.recycle();
                            }
                        }
                        File file = new File(filePath);
                        if(!file.exists()) {
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
                                        ,"avatar:" + circleUrl))
                                .compose(Rx.transformer(GroupInfoBean.class));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSingleSubscriber<GroupInfoBean>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        HnLoading.hide();
                    }

                    @Override
                    public void onSucceed(GroupInfoBean s) {
                        HnLoading.hide();
                        AddGroupChatUIView.this.finishIView();
                        T_.show(s.getGroupAvatar());
                    }
                }));

    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }
}
