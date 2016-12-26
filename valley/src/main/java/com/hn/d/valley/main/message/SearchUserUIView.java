package com.hn.d.valley.main.message;

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

import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.Bean;
import com.hn.d.valley.bean.SearchUserBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.mvp.Search;
import com.hn.d.valley.main.message.mvp.SearchPresenter;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：搜索添加好友
 * 创建人员：Robi
 * 创建时间：2016/12/26 9:04
 * 修改人员：Robi
 * 修改时间：2016/12/26 9:04
 * 修改备注：
 * Version: 1.0.0
 */
public class SearchUserUIView extends BaseUIView<Search.ISearchPresenter> implements Search.ISearchView<SearchUserBean, Bean<SearchUserBean>> {

    @BindView(R.id.search_input_view)
    ExEditText mSearchInputView;
    @BindView(R.id.search_tip_view)
    TextView mSearchTipView;
    @BindView(R.id.search_control_layout)
    LinearLayout mSearchControlLayout;

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_search_layout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.add_friend)).setShowBackImageView(true);
    }

    @Override
    public Animation loadLayoutAnimation() {
        return null;
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();
        bindPresenter(new SearchPresenter());
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

    @OnTextChanged(R.id.search_input_view)
    public void onSearchTextChanged(Editable editable) {
        if (TextUtils.isEmpty(editable)) {
            mSearchControlLayout.setVisibility(View.GONE);
        } else {
            mSearchControlLayout.setVisibility(View.VISIBLE);
            SpannableStringBuilder stringBuilder = SpannableStringUtils.getBuilder(mActivity.getString(R.string.search_tip))
                    .append(editable.toString())
                    .setForegroundColor(mActivity.getResources().getColor(R.color.colorAccent))
                    .create();
            mSearchTipView.setText(stringBuilder);
        }
    }

    /**
     * 开始搜索用户
     */
    @OnClick(R.id.search_tip_view)
    public void onSearchTipClick() {
        mPresenter.onSearch(UserCache.getUserAccount(), mSearchInputView.getText().toString());
    }

    @Override
    public void onSearchSucceed(Bean<SearchUserBean> bean) {

    }
}
