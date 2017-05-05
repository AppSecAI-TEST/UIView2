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
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.github.WaveSideBarView;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RGroupItemDecoration;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.bean.event.SelectedUserNumEvent;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.IDataResource;
import com.hn.d.valley.widget.HnLoading;
import com.hn.d.valley.widget.HnRefreshLayout;
import com.hwangjr.rxbus.annotation.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import rx.functions.Action3;

/**
 * Created by hewking on 2017/3/9.
 */
public class BaseContactSelectUIVIew extends BaseUIView {

    public static final String SELECTED_UIDS = "SELECTED_UIDS";

    @BindView(R.id.friend_add_refreshlayout)
    HnRefreshLayout refreshLayout;
    @BindView(R.id.friend_add_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.sidebar_friend_index)
    WaveSideBarView sideBarView;

    protected BaseContactSelectAdapter.Options options;

    protected BaseContactSelectAdapter mGroupAdapter;

    protected IDataResource.IDataActionProvider datatProvider;

    private List<String> mSelectedUids;


    protected Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback> selectAction;

    public BaseContactSelectUIVIew(BaseContactSelectAdapter.Options options) {
        super();
        this.options = options;
    }


    public void setSelectAction(Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback> selectAction) {
        this.selectAction = selectAction;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.ok)).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelected();
            }
        }));
        return super.getTitleBar().setShowBackImageView(true).setTitleString(mActivity.getString(R.string.text_select_friend)).setRightItems(rightItems);
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);

        if (param != null && param.mBundle != null) {
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
        refreshLayout.addOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(@RefreshLayout.Direction int direction) {
                if (direction == RefreshLayout.TOP) {
                    loadData();
                }
            }
        });

        buildAdapter();

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

    protected void buildAdapter() {

    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadData();
    }

    protected void loadData() {

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
                    BaseContactSelectUIVIew.this.finishIView();
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
            selectNum.setText(String.format(Locale.CHINA,mActivity.getString(R.string.text_contact_ok),event.getNum()));
        } else {
            selectNum.setText(mActivity.getString(R.string.ok));
        }
    }


}
