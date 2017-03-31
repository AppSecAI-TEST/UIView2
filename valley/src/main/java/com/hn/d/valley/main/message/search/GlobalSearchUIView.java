package com.hn.d.valley.main.message.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RGroupItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.widget.ExEditText;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.SearchUserBean;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.main.message.query.TextQuery;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.sub.user.UserInfoUIView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
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

    @BindView(R.id.search_input_view)
    ExEditText mSearchInputView;
    @BindView(R.id.search_tip_view)
    TextView mSearchTipView;
    @BindView(R.id.search_control_layout)
    LinearLayout mSearchControlLayout;
    @BindView(R.id.recycler_view)
    RRecyclerView mRecyclerView;
    @BindView(R.id.empty_tip_view)
    TextView mEmptyTipView;

    private GlobalSearchAdapter mSearchUserAdapter;

    protected int[] itemTypes;

    public static void start(ILayout mLayout,int[] itemTypes) {
        Bundle bundle = new Bundle();
        bundle.putIntArray(ITEMTYPES,itemTypes);

        GlobalSearchUIView targetView = new GlobalSearchUIView();
        mLayout.startIView(targetView, new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
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
        bindPresenter(new GlobalSearchPresenter());
        mSearchUserAdapter = new GlobalSearchAdapter(mActivity,mOtherILayout);
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

    /**
     * 开始搜索用户
     */
    @OnClick(R.id.search_tip_view)
    public void onSearchTipClick() {
//        mPresenter.search();
    }

    @Override
    public void onSearchSuccess(List<AbsContactItem> items) {
        if (items == null) {
            mEmptyTipView.setText("该用户不存在");
        } else {
            mEmptyTipView.setText("");
        }
        mSearchUserAdapter.resetData(items);
    }



}
