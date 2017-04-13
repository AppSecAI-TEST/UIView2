package com.hn.d.valley.main.message.groupchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.event.SelectedUserNumEvent;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.message.SearchUserUIView;
import com.hn.d.valley.realm.RRealm;
import com.hwangjr.rxbus.annotation.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import rx.functions.Action1;
import rx.functions.Action3;

/**
 * Created by hewking on 2017/3/9.
 */
public class ContactSelectUIVIew extends BaseContactSelectUIVIew {


    public ContactSelectUIVIew(Options options) {
        super(options);
    }

    public static void start(ILayout mLayout, Options options, List<String> uids, Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback> selectAction) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_UIDS, (Serializable) uids);

        ContactSelectUIVIew targetView = new ContactSelectUIVIew(options);
        targetView.setSelectAction(selectAction);

        mLayout.startIView(targetView, new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }


    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setShowBackImageView(true).setTitleString("选择好友");
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        super.datatProvider = new AddGroupDatatProvider();
    }


    @Override
    protected void buildAdapter() {
        mGroupAdapter = new AddGroupAdapter(mActivity, options);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadData();
    }

    @Override
    protected void loadData() {

//        RealmResults<FriendBean> results = RRealm.realm().where(FriendBean.class).findAll();
//        if (!results.isEmpty()) {
//
//            showContentLayout();
//
//            refreshLayout.setRefreshEnd();
//
//            List<AbsContactItem> datas = new ArrayList();
//            datas.add(new FuncItem<>("搜索", new Action1<ILayout>() {
//                @Override
//                public void call(ILayout o) {
//                    mOtherILayout.startIView(new SearchUserUIView());
//                }
//            }));
//            for (FriendBean bean : results) {
//                datas.add(new ContactItem(bean));
//            }
//
//            FriendsControl.sort(datas);
//
//            mGroupAdapter.resetData(datas);
//
//            sideBarView.setLetters(FriendsControl.generateIndexLetter(datas));
//        }

        datatProvider.provide(mSubscriptions, new RequestCallback<List<FriendBean>>() {
            @Override
            public void onStart() {
                showLoadView();
            }

            @Override
            public void onSuccess(List<FriendBean> beanList) {
                hideLoadView();
                showContentLayout();

                refreshLayout.setRefreshEnd();

                List<AbsContactItem> datas = new ArrayList();
                datas.add(new FuncItem<>("搜索", new Action1<ILayout>() {
                    @Override
                    public void call(ILayout o) {
                        mOtherILayout.startIView(new SearchUserUIView());
                    }
                }));
                for (FriendBean bean : beanList) {
                    datas.add(new ContactItem(bean));
                }

                FriendsControl.sort(datas);

                mGroupAdapter.resetData(datas);

                sideBarView.setLetters(FriendsControl.generateIndexLetter(datas));

            }

            @Override
            public void onError(String msg) {
                hideLoadView();
            }
        });

    }

    @Subscribe
    public void onEvent(SelectedUserNumEvent event) {
        TextView selectNum = (TextView) getUITitleBarContainer().getRightControlLayout().getChildAt(0);
        if (event.getNum() != 0) {
            selectNum.setText("确定(" + event.getNum() + ")");
        } else {
            selectNum.setText("确定");
        }
    }

}
