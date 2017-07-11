package com.hn.d.valley.main.message.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RGroupItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.message.query.TextQuery;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/27 12:00
 * 修改人员：hewking
 * 修改时间：2017/03/27 12:00
 * 修改备注：
 * Version: 1.0.0
 */
public class GlobalSearchUIView extends BaseUIView<GlobalSearch.ISearchPresenter> implements GlobalSearch.ISearchView {

    public static final String ITEMTYPES = "itemTypes";

    ExEditText mSearchInputView;
    RRecyclerView mRecyclerView;
    TextView mEmptyTipView;
//    @BindView(R.id.refresh_layout)
//    RefreshLayout refreshLayout;

    private GlobalSearchAdapter mSearchUserAdapter;

    protected int[] itemTypes;

    public static void start(ILayout mLayout,int[] itemTypes) {
        Bundle bundle = new Bundle();
        bundle.putIntArray(ITEMTYPES,itemTypes);

        GlobalSearchUIView targetView = new GlobalSearchUIView();
        mLayout.startIView(targetView, new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_message_globalsearch_layout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString("搜索").setShowBackImageView(true);
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        if (param != null ) {
            itemTypes = param.mBundle.getIntArray(ITEMTYPES);
        }
    }

    @Override
    public Animation loadLayoutAnimation() {
        return null;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mSearchInputView = v(R.id.search_input_view);
        mRecyclerView = v(R.id.recycler_view);
        mEmptyTipView = v(R.id.empty_tip_view);

        bindPresenter(new GlobalSearchPresenter());

//        refreshLayout.setRefreshDirection(RefreshLayout.TOP);
//        refreshLayout.setNotifyListener(false);

        mSearchUserAdapter = new GlobalSearchAdapter(mActivity, mParentILayout);
        mRecyclerView.addItemDecoration(new RGroupItemDecoration(new FriendsControl.GroupItemCallBack(mActivity,mSearchUserAdapter)));
        mRecyclerView.setAdapter(mSearchUserAdapter);

        buildSearchView();
    }

    protected void buildSearchView() {
        RxTextView.textChanges(mSearchInputView)
                .debounce(Constant.DEBOUNCE_TIME_700, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        if (TextUtils.isEmpty(charSequence)) {
                            mEmptyTipView.setText("");
                        } else {
                            mPresenter.search(new TextQuery(charSequence.toString()), itemTypes);
                        }
                    }
                });
    }

    @Override
    public int getDefaultBackgroundColor() {
        return mActivity.getResources().getColor(R.color.default_base_bg_dark2);
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }


    @Override
    public void onRequestStart() {
        super.onRequestStart();
        mEmptyTipView.setText("");
    }

//    /**
//     * 开始搜索用户
//     */
//    @OnClick(R.id.search_tip_view)
//    public void onSearchTipClick() {
//        mPresenter.search();
//    }

    @Override
    public void onSearchSuccess(List<AbsContactItem> items) {
        if (items == null || items.size() == 0) {
            mEmptyTipView.setText("未搜索到数据");
        } else {
            mEmptyTipView.setText("");
        }
        mSearchUserAdapter.resetData(items);
    }



}
