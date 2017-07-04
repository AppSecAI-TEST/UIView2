package com.hn.d.valley.main.message;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.widget.ExEditText;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.SearchUserBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.mvp.Search;
import com.hn.d.valley.main.message.mvp.SearchPresenter;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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
@Deprecated
public class SearchUserUIView extends BaseUIView<Search.ISearchPresenter> implements Search.ISearchView {

    ExEditText mSearchInputView;
    TextView mSearchTipView;
    LinearLayout mSearchControlLayout;
    RRecyclerView mRecyclerView;
    TextView mEmptyTipView;
    private SearchUserAdapter mSearchUserAdapter;

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_search_layout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
//        return super.getTitleBar().setTitleString(mActivity.getString(R.string.add_friend)).setShowBackImageView(true);
        return null;
    }

    @Override
    public Animation loadLayoutAnimation() {
        return null;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mSearchInputView = v(R.id.edit_text_view);
        mSearchTipView = v(R.id.search_tip_view);
        mSearchControlLayout = v(R.id.search_control_layout);
        mRecyclerView = v(R.id.recycler_view);
        mEmptyTipView = v(R.id.empty_tip_view);

        click(R.id.search_tip_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchTipClick();
            }
        });

        bindPresenter(new SearchPresenter());
        mSearchUserAdapter = new SearchUserAdapter(mActivity);
        mRecyclerView.setAdapter(mSearchUserAdapter);

        mViewHolder.v(R.id.title_bar_layout).setBackgroundColor(SkinHelper.getSkin().getThemeColor());

        RxTextView.textChanges(mSearchInputView)
                .debounce(Constant.DEBOUNCE_TIME_700, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        if (TextUtils.isEmpty(charSequence)) {
                            mEmptyTipView.setText("");
                            mSearchUserAdapter.setItem(null);
                        } else {
                            mPresenter.onSearch(UserCache.getUserAccount(), mSearchInputView.getText().toString());
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

//    @OnTextChanged(R.id.search_input_view)
//    public void onSearchTextChanged(Editable editable) {
//        if (TextUtils.isEmpty(editable)) {
//            mSearchControlLayout.setVisibility(View.GONE);
//        } else {
//            mSearchControlLayout.setVisibility(View.VISIBLE);
//            SpannableStringBuilder stringBuilder = SpannableStringUtils.getBuilder(mActivity.getString(R.string.search_tip))
//                    .append(editable.toString())
//                    .setForegroundColor(mActivity.getResources().getColor(R.color.colorAccent))
//                    .create();
//            mSearchTipView.setText(stringBuilder);
//        }
//    }


    @Override
    public void onRequestStart() {
        super.onRequestStart();
        mEmptyTipView.setText("");
    }

    /**
     * 开始搜索用户
     */
    public void onSearchTipClick() {
        mPresenter.onSearch(UserCache.getUserAccount(), mSearchInputView.getText().toString());
    }

    @Override
    public void onSearchSucceed(List<SearchUserBean> bean) {
        if (bean == null) {
            mEmptyTipView.setText("该用户不存在");
        } else {
            mEmptyTipView.setText("");
        }
        mSearchUserAdapter.resetData(bean);
    }

    private class SearchUserAdapter extends RBaseAdapter<SearchUserBean> {

        public SearchUserAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_search_user_contacts;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, final SearchUserBean bean) {
            DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.ico_view), bean.getAvatar());
            holder.tv(R.id.recent_name_view).setText(bean.getUsername());

            holder.v(R.id.item_root_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new UserDetailUIView2(bean.getUid()));
                }
            });
        }

        public void setItem(SearchUserBean bean) {
            mAllDatas.clear();
            if (bean != null) {
                mAllDatas.add(bean);
            }
            notifyDataSetChanged();
        }
    }
}
