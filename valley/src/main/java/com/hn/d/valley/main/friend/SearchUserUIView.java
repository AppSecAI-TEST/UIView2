package com.hn.d.valley.main.friend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.viewgroup.RRelativeLayout;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.SearchUserBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.mvp.Search;
import com.hn.d.valley.main.message.mvp.SearchPresenter;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;
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
public class SearchUserUIView extends BaseUIView<Search.ISearchPresenter> implements Search.ISearchView {

    ExEditText mSearchInputView;
    TextView mSearchTipView;
    LinearLayout mSearchControlLayout;
    RRecyclerView mRecyclerView;
    TextView mEmptyTipView;
    private SearchUserAdapter mSearchUserAdapter;

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
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
        mViewHolder.v(R.id.cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishIView();
            }
        });

        mSearchInputView.setHint(R.string.search_hint);

        post(new Runnable() {
            @Override
            public void run() {
                showSoftInput(mSearchInputView);
            }
        });

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

        mRecyclerView.addItemDecoration(new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
            @Override
            public void getItemOffsets2(Rect outRect, int position, int edge) {
                super.getItemOffsets2(outRect, position, edge);
                if (!mSearchUserAdapter.isLast(position)) {
                    outRect.bottom = getDimensionPixelOffset(R.dimen.base_line);
                }
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                drawLeftBottomLine(canvas, paint, itemView, offsetRect, itemCount, position);
            }
        }));
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
        if (bean == null || bean.isEmpty()) {
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
            return R.layout.item_user_info_new_friend;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, final SearchUserBean bean) {
//            DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.ico_view), bean.getAvatar());
            holder.tv(R.id.username).setText(bean.getUsername());

            String signature = bean.getSignature();
            if (TextUtils.isEmpty(signature)) {
                holder.tv(R.id.signature).setText(R.string.signature_empty_tip);
            } else {
                holder.tv(R.id.signature).setText(signature);
            }

            HnGenderView genderView = holder.v(R.id.grade);
            genderView.setGender(bean.getSex(), bean.getGrade());

            HnGlideImageView icoImageView = holder.v(R.id.image_view);
            icoImageView.setImageThumbUrl(bean.getAvatar());

            holder.v(R.id.right_control_layout).setVisibility(View.GONE);

            holder.v(R.id.item_root_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new UserDetailUIView2(bean.getUid()));
                }
            });

            RRelativeLayout layout = holder.v(R.id.item_root_layout);
            layout.setRBackgroundDrawable(Color.WHITE);
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
