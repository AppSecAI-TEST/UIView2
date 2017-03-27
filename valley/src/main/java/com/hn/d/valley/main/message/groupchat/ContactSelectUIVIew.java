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
import com.angcyo.uiview.github.WaveSideBarView;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RGroupItemDecoration;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.GroupInfoBean;
import com.hn.d.valley.bean.event.SelectedUserNumEvent;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.message.SearchUserUIView;
import com.hn.d.valley.widget.HnIcoRecyclerView;
import com.hn.d.valley.widget.HnLoading;
import com.hn.d.valley.widget.HnRefreshLayout;
import com.hwangjr.rxbus.annotation.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Action3;

/**
 * Created by hewking on 2017/3/9.
 */
public class ContactSelectUIVIew extends BaseUIView {

    public static final String SELECTED_UIDS = "SELECTED_UIDS";

    @BindView(R.id.friend_add_refreshlayout)
    HnRefreshLayout refreshLayout;
    @BindView(R.id.friend_add_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.sidebar_friend_index)
    WaveSideBarView sideBarView;

//    @BindView(R.id.rv_groupchat_icon)
//    HnIcoRecyclerView iconSelectedRv;

    private Options options;

    private AddGroupAdapter mGroupAdapter;

    private AddGroupDatatProvider datatProvider;

    private List<String> mSelectedUids;

    private Action2<Boolean, ContactItem> action = new Action2<Boolean, ContactItem>() {
        @Override
        public void call(Boolean aBoolean, ContactItem item) {
            HnIcoRecyclerView.IcoInfo icon;
            FriendBean bean = item.getFriendBean();
//            if (aBoolean) {
//                icon = new HnIcoRecyclerView.IcoInfo(bean.getUid(),bean.getAvatar());
//                iconSelectedRv.getMaxAdapter().addLastItem(icon);
//            }else {
//                iconSelectedRv.remove(bean.getAvatar());
//            }

        }
    };

    private Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback> selectAction;

    public ContactSelectUIVIew(Options options) {
        super();
        this.options = options;
    }

    public static void start(ILayout mLayout, Options options, List<String> uids, Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback> selectAction) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_UIDS, (Serializable) uids);

        ContactSelectUIVIew targetView = new ContactSelectUIVIew(options);
        targetView.setSelectAction(selectAction);

        mLayout.startIView(targetView, new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    public void setSelectAction(Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback> selectAction) {
        this.selectAction = selectAction;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText("确定").setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelected();
            }
        }));
        return super.getTitleBar().setShowBackImageView(true).setTitleString("选择好友").setRightItems(rightItems);
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);

        if (param != null && param.mBundle != null) {
            mSelectedUids = (List<String>) param.mBundle.getSerializable(SELECTED_UIDS);
        }

        datatProvider = new AddGroupDatatProvider();


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

        mGroupAdapter = new AddGroupAdapter(mActivity, options);
//        mGroupAdapter.setAction(action);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.addItemDecoration(new RGroupItemDecoration(new FriendsControl.GroupItemCallBack(mActivity, mGroupAdapter)));

        recyclerView.setAdapter(mGroupAdapter);

        mGroupAdapter.setSelecteUids(mSelectedUids);


        sideBarView.setOnTouchLetterChangeListener(new WaveSideBarView.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                FriendsControl.scrollToLetter(letter, recyclerView, mGroupAdapter.getAllDatas());
            }
        });

    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadData();
    }

    private void loadData() {
        datatProvider.provide(mSubscriptions, new Action1<List<FriendBean>>() {
            @Override
            public void call(List<FriendBean> beanList) {

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
        });
    }


    private void onSelected() {
        if (selectAction != null) {
            selectAction.call(this, mGroupAdapter.getSelectorData(), new RequestCallback() {
                @Override
                public void onStart() {
                    HnLoading.show(mOtherILayout);
                }

                @Override
                public void onSuccess(Object groupInfoBean) {
                    HnLoading.hide();
                    ContactSelectUIVIew.this.finishIView();
                }

                @Override
                public void onError(String msg) {
                    HnLoading.hide();
                }
            });
        }

    }


    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    /**************************RxBus 订阅事件  ********************************/

    @Subscribe
    public void onEvent(SelectedUserNumEvent event) {
        TextView selectNum = (TextView) getUITitleBarContainer().getRightControlLayout().getChildAt(0);
        if (event.getNum() != 0) {
            selectNum.setText("确定(" + event.getNum() + ")");
        } else {
            selectNum.setText("确定");
        }
    }

    public static class Options {

        public static final int DEFALUT_LIMIT = 5;

        public int mode;
        public int selectCountLimit = DEFALUT_LIMIT;

        public Options() {
            this(RModelAdapter.MODEL_MULTI);
        }

        public Options(int mode) {
            this(mode, DEFALUT_LIMIT);
        }

        public Options(int mode, int limit) {
            this.mode = mode;
            this.selectCountLimit = limit;
        }

    }
}
