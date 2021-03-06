package com.hn.d.valley.main.friend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.ArrayList;

import rx.functions.Action1;

/**
 * Created by hewking on 2017/3/7.
 */
public class FriendUIView extends BaseUIView {

    private FriendsControl mFriendsControl;

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        rightItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.tianjiahaoyou_haoyou).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentILayout.startIView(new NewFriend3UIView());
//                UIItemDialog.build()
//                        .addItem(getString(R.string.add_friend), new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mParentILayout.startIView(new SearchUserUIView());
//                            }
//                        })
//                        .addItem("添加群聊", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                ContactSelectUIVIew targetView = new ContactSelectUIVIew(new BaseContactSelectAdapter.Options());
//                                targetView.setSelectAction(new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
//                                    @Override
//                                    public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, RequestCallback requestCallback) {
//                                        TeamCreateHelper.createAndSavePhoto(uiBaseDataView, absContactItems, requestCallback);
//                                    }
//                                });
//                                mParentILayout.startIView(targetView);
//                            }
//                        }).showDialog(mParentILayout);
            }
        }));

        return super.getTitleBar().setShowBackImageView(true).setTitleString(mActivity.getString(R.string.friend)).setRightItems(rightItems);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mFriendsControl.init(mBaseContentLayout);
        mFriendsControl.setToUserDetailAction(new Action1<FriendBean>() {
            @Override
            public void call(FriendBean o) {
//                mParentILayout.startIView(new UserDetailUIView2(o.getUid()));
                SessionHelper.startP2PSession(mParentILayout,o.getUid(), SessionTypeEnum.P2P);
            }
        });

    }

    private void loadFriends() {
        mFriendsControl.loadData();
    }

    @Override
    public void onViewCreate(View rootView) {
        super.onViewCreate(rootView);
        mFriendsControl = new FriendsControl(mActivity, mSubscriptions, mParentILayout, new RequestCallback() {
            @Override
            public void onStart() {
                showLoadView();
            }


            @Override
            public void onSuccess(Object o) {
                hideLoadView();
                showContentLayout();
            }

            @Override
            public void onError(String msg) {
                hideLoadView();
                showContentLayout();
            }
        });

    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadFriends();
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_message_friend);
    }
}
