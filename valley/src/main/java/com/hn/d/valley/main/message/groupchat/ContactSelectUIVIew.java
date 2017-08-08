package com.hn.d.valley.main.message.groupchat;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.event.SelectedUserNumEvent;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.friend.GroupBean;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.main.friend.SearchUserUIView;
import com.hn.d.valley.main.friend.SystemPushItem;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.main.other.ContactSearchAdapter;
import com.hn.d.valley.main.other.ContactSearchUIView;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Action3;

/**
 * Created by hewking on 2017/3/9.
 */
public class ContactSelectUIVIew extends BaseContactSelectUIVIew {

    private boolean onShowGroup;

    public ContactSelectUIVIew(BaseContactSelectAdapter.Options options) {
        super(options);
    }

    public static ContactSelectUIVIew start(ILayout mLayout, BaseContactSelectAdapter.Options options/**配置*/,
                                            List<String> uids/**默认选中*/,
                                            Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback> selectAction/**回调*/) {
        return start(mLayout, options, uids, false, selectAction);
    }


    public static ContactSelectUIVIew start(ILayout mLayout, BaseContactSelectAdapter.Options options/**配置*/,
                                            List<String> uids/**默认选中*/,boolean onShowGroup,/*是否显示群聊funcitem*/
                                            Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback> selectAction/**回调*/) {
        return start(mLayout,options,uids,new ArrayList<String>(),onShowGroup,selectAction);
    }


    public static ContactSelectUIVIew start(ILayout mLayout, BaseContactSelectAdapter.Options options/**配置*/,
                                            List<String> uids/**默认选中*/,List<String> finanUids,boolean onShowGroup,/*是否显示群聊funcitem*/
                                            Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback> selectAction/**回调*/) {
        return start(mLayout,options,uids,finanUids,null,onShowGroup,selectAction);
    }

    public static ContactSelectUIVIew start(ILayout mLayout, BaseContactSelectAdapter.Options options/**配置*/,
                                            List<String> uids/**默认选中*/,List<String> finanUids,List<String> unselectUids,boolean onShowGroup,/*是否显示群聊funcitem*/
                                            Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback> selectAction/**回调*/) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_UIDS, (Serializable) uids);
        bundle.putSerializable(SELECTED_FINAL_UIDS, (Serializable) finanUids);
        bundle.putSerializable(UNSELECTED_FINAL_UIDS, (Serializable) unselectUids);
        ContactSelectUIVIew targetView = new ContactSelectUIVIew(options);
        targetView.setSelectAction(selectAction);
        targetView.onShowGroup = onShowGroup;
        mLayout.startIView(targetView, new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
        return targetView;
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
        // TODO: 2017/4/16 创建群聊 ,无法访问 realm 只能在创建的线程中访问
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

    private void processResult(final List<FriendBean> beanList) {
        showContentLayout();
        // 清除已选中
        mGroupAdapter.getSelectedUsers().clear();
        refreshLayout.setRefreshEnd();

        final List<AbsContactItem> datas = new ArrayList<>();

        //转发等需要转发到群聊 需要添加群聊funcItem
        if (onShowGroup) {
            datas.add(new FuncItem<>("群聊", new Action1<ILayout>() {
                @Override
                public void call(ILayout layout) {
                    //进入群聊
                    MyGroupUIView.start(mParentILayout, new BaseContactSelectAdapter.Options(RModelAdapter.MODEL_SINGLE, 1, true)
                            , new Action2<GroupBean, RequestCallback>() {
                                @Override
                                public void call(GroupBean groupBean, com.hn.d.valley.main.message.groupchat.RequestCallback requestCallback) {
                                    FriendBean briend = FriendBean.create(groupBean);
                                    ContactItem item = new ContactItem(briend);
                                    List<AbsContactItem> list = new ArrayList<>();
                                    list.add(item);
                                    // 注释暂时 size > 1 判断 team
//                                    list.add(item);
                                    getILayout().finishIView(ContactSelectUIVIew.class);
                                    selectAction.call(null, list, requestCallback);
                                }
                            });
                }
            }, R.drawable.group_chat_n));
        }

        // 发送名片增加本人显示再第一列
        if (options.showDialog) {
            FriendBean bean = new FriendBean();
            bean.setDefaultMark(UserCache.instance().getUserInfoBean().getUsername());
            bean.setUid(UserCache.getUserAccount());
            bean.setAvatar(UserCache.getUserAvatar());
            datas.add(new ContactItem(bean,false));
        }

        datas.add(new FuncItem<>(mActivity.getString(R.string.search), ItemTypes.SEARCH, new Action1<ILayout>() {
            @Override
            public void call(ILayout o) {
//                mParentILayout.startIView(new SearchUserUIView());
                mParentILayout.startIView(new ContactSearchUIView(beanList, new Action1<FriendBean>() {
                    @Override
                    public void call(FriendBean friendBean) {
                        List<String> selectedUsers = mGroupAdapter.getSelectedUsers();
                        selectedUsers.add(friendBean.getUid());
                        ((ContactSelectAdapter) mGroupAdapter).showSelectUsers();
                        mGroupAdapter.notifyDataSetChanged();
//                        onSelected();
                    }
                }));
            }
        }));

        for (FriendBean bean : beanList) {
            boolean flag = false;
            if (options.isUnSelectUids) {
                if (unselectedFinalUids == null) {
                    datas.add(new ContactItem(bean));
                    continue;
                }

                for (String uid : unselectedFinalUids) {
                    if (bean.getUid().equals(uid)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) {
                continue;
            }
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
            selectNum.setText(String.format(mActivity.getString(R.string.text_sure_d), +event.getNum()));
        } else {
            selectNum.setText(mActivity.getString(R.string.ok));
        }
    }

}
