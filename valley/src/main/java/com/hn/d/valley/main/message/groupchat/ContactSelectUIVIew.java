package com.hn.d.valley.main.message.groupchat;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.event.SelectedUserNumEvent;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.friend.SearchUserUIView;
import com.hwangjr.rxbus.annotation.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Action3;

/**
 * Created by hewking on 2017/3/9.
 */
public class ContactSelectUIVIew extends BaseContactSelectUIVIew {


    public ContactSelectUIVIew(BaseContactSelectAdapter.Options options) {
        super(options);
    }

    public static void start(ILayout mLayout, BaseContactSelectAdapter.Options options/**配置*/,
                             List<String> uids/**默认选中*/,
                             Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback> selectAction/**回调*/) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_UIDS, (Serializable) uids);

        ContactSelectUIVIew targetView = new ContactSelectUIVIew(options);
        targetView.setSelectAction(selectAction);

        mLayout.startIView(targetView, new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }


    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setShowBackImageView(true).setTitleString(mActivity.getString(R.string.text_select_friend));
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        super.datatProvider = new AddGroupDatatProvider();
    }

    /**
     * 允许在对话框上显示
     */
    @Override
    public boolean showOnDialog() {
        return true;
    }

    @Override
    protected void buildAdapter() {
        mGroupAdapter = new ContactSelectAdapter(mActivity, options, recyclerView);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadData();
    }

    @Override
    protected void loadData() {
        // TODO: 2017/4/16 创建群聊 无法访问 realm 只能在创建的线程中访问
//        RealmResults<FriendBean> results = RRealm.realm().where(FriendBean.class).findAll();
//        if (!results.isEmpty()) {
//
//            processResult(results);
//
//        } else {

        datatProvider.provide(mSubscriptions, new RequestCallback<List<FriendBean>>() {
            @Override
            public void onStart() {
                showLoadView();
            }

            @Override
            public void onSuccess(List<FriendBean> beanList) {
                hideLoadView();
                processResult(beanList);

            }

            @Override
            public void onError(String msg) {
                hideLoadView();
            }
        });

//        }
    }

    private void processResult(List<FriendBean> beanList) {
        showContentLayout();

        refreshLayout.setRefreshEnd();

        List<AbsContactItem> datas = new ArrayList();
        datas.add(new FuncItem<>(mActivity.getString(R.string.search), new Action1<ILayout>() {
            @Override
            public void call(ILayout o) {
                mParentILayout.startIView(new SearchUserUIView());
            }
        }));
        for (FriendBean bean : beanList) {
            datas.add(new ContactItem(bean));
        }

        FriendsControl.sort(datas);

        mGroupAdapter.resetData(datas);

        sideBarView.setLetters(FriendsControl.generateIndexLetter(datas));
    }

    @Subscribe
    public void onEvent(SelectedUserNumEvent event) {
        TextView selectNum = (TextView) getUITitleBarContainer().getRightControlLayout().getChildAt(0);
        if (event.getNum() != 0) {
            selectNum.setText(String.format(mActivity.getString(R.string.text_sure_d),+ event.getNum()));
        } else {
            selectNum.setText(mActivity.getString(R.string.ok));
        }
    }

}
