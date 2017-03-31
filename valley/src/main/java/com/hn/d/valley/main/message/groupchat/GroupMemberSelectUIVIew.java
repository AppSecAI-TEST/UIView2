package com.hn.d.valley.main.message.groupchat;

import android.os.Bundle;
import android.view.View;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.GroupMemberBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.message.SearchUserUIView;
import com.hn.d.valley.service.GroupChatService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Action3;

/**
 * Created by hewking on 2017/3/9.
 */
public class GroupMemberSelectUIVIew extends BaseContactSelectUIVIew {

    public static final String GID = "gid";
    private String gid;

    public GroupMemberSelectUIVIew(Options options) {
        super(options);
    }

    public static void start(ILayout mLayout, Options options, List<String> uids,String gid, Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback> selectAction) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_UIDS, (Serializable) uids);
        bundle.putString(GID,gid);
        GroupMemberSelectUIVIew targetView = new GroupMemberSelectUIVIew(options);
        targetView.setSelectAction(selectAction);

        mLayout.startIView(targetView, new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }


    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setShowBackImageView(true).setTitleString("选择群成员");
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        if (param != null) {
            gid = param.mBundle.getString(GID);
        }
    }


    @Override
    protected void buildAdapter() {
        mGroupAdapter = new GroupMemberSelectAdapter(mActivity, options);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadData();
    }

    @Override
    protected void loadData() {
        add(RRetrofit.create(GroupChatService.class)
                .groupMember(Param.buildMap("uid:" + UserCache.getUserAccount(),"gid:" + gid))
                .compose(Rx.transformer(GroupMemberModel.GroupMemberList.class))
                .subscribe(new BaseSingleSubscriber<GroupMemberModel.GroupMemberList>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }

                    @Override
                    public void onSucceed(GroupMemberModel.GroupMemberList beans) {
                        if (beans == null || beans.getData_list().size() == 0) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd();

                            List<AbsContactItem> datas = new ArrayList();
                            datas.add(new FuncItem<>("搜索", new Action1<ILayout>() {
                                @Override
                                public void call(ILayout o) {
                                    mOtherILayout.startIView(new SearchUserUIView());
                                }
                            }));
                            for (GroupMemberBean bean : beans.getData_list()) {
                                datas.add(new GroupMemberItem(bean));
                            }

                            FriendsControl.sort(datas);

                            mGroupAdapter.resetData(datas);

                            sideBarView.setLetters(FriendsControl.generateIndexLetter(datas));
                        }
                    }
                }));

    }

    private void onUILoadDataEnd() {
        showContentLayout();
        refreshLayout.setRefreshEnd();
    }

}