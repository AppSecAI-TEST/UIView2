package com.hn.d.valley.main.message.search;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.constant.Constant;
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
public class GlobalSearchUIView2 extends BaseUIView<GlobalSearch.ISearchPresenter> implements GlobalSearch.ISearchView ,GlobalSearchAdapter2.OnTextChangeListener{

    public static final String ITEMTYPES = "itemTypes";

    ExEditText mSearchInputView;
    RRecyclerView mRecyclerView;
    TextView mEmptyTipView;
//    @BindView(R.id.refresh_layout)
//    RefreshLayout refreshLayout;

    protected GlobalSearchAdapter2 mSearchUserAdapter;

    protected int[] itemTypes;

    private Options option;

    public static void start(ILayout mLayout,Options options, int[] itemTypes) {
        Bundle bundle = new Bundle();
        bundle.putIntArray(ITEMTYPES,itemTypes);

        GlobalSearchUIView2 targetView = new GlobalSearchUIView2(options);
        mLayout.startIView(targetView, new UIParam().setBundle(bundle).setLaunchMode(UIParam.NORMAL));
    }

    public GlobalSearchUIView2(Options options) {
        this.option = new Options(options.isSearchMuti());
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_message_globalsearch_layout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
//        return super.getTitleBar().setTitleString("搜索").setShowBackImageView(true);
        return null;
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
        mSearchInputView = v(R.id.edit_text_view);
        mRecyclerView = v(R.id.recycler_view);
        mEmptyTipView = v(R.id.empty_tip_view);

        bindPresenter(new GlobalSearchPresenter());

//        refreshLayout.setRefreshDirection(RefreshLayout.TOP);
//        refreshLayout.setNotifyListener(false);

        mViewHolder.v(R.id.title_bar_layout).setBackgroundColor(SkinHelper.getSkin().getThemeColor());
        mViewHolder.v(R.id.cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishIView();
            }
        });

        mSearchUserAdapter = new GlobalSearchAdapter2(mActivity, mParentILayout,option);
//        mRecyclerView.addItemDecoration(new RGroupItemDecoration(new FriendsControl.GroupItemCallBack(mActivity,mSearchUserAdapter)));
        mRecyclerView.addItemDecoration(mSearchItemDecoration);
        mRecyclerView.setAdapter(mSearchUserAdapter);
        // 搜索变化回调
        mSearchUserAdapter.setTextChangeListener(this);
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

//    @Override
//    public int getDefaultBackgroundColor() {
//        return mActivity.getResources().getColor(R.color.default_base_bg_dark2);
//    }

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


    @Override
    public void onSearchSuccess(List<AbsContactItem> items) {
        if (items == null || items.size() == 0) {
            mEmptyTipView.setText("未搜索到数据");
        } else {
            mSearchUserAdapter.getOption().setSearchFlag(true);
            mEmptyTipView.setText("");
        }
        mSearchUserAdapter.resetData(items);
    }

    private RExItemDecoration mSearchItemDecoration  = new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
        @Override
        public void getItemOffsets(Rect outRect, int position) {
            if (position != 0) {
                outRect.top = getDimensionPixelOffset(R.dimen.base_line);

                AbsContactItem item = mSearchUserAdapter.getItem(position);
                if (item instanceof SectionItem) {
                    outRect.top = getDimensionPixelOffset(R.dimen.base_xhdpi);
                    return;
                }

                if (mSearchUserAdapter.getOption().isSearchMuti()) {
                    if (position == mSearchUserAdapter.getItemCount() - 1) {
                        outRect.top = getDimensionPixelOffset(R.dimen.base_xhdpi);
                    }
                }
            }
        }

        @Override
        public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
            int left = 0;
            int itemViewTop = itemView.getTop();
            int top = itemViewTop - offsetRect.top;


            if (position == 0) {
                return;
            }

            AbsContactItem item = mSearchUserAdapter.getItem(position);

            if (item instanceof SectionItem || position == mSearchUserAdapter.getItemCount() - 1) {

                left = 0;
                paint.setColor(Color.WHITE);
                offsetRect.set(0, top, left, itemViewTop);
                canvas.drawRect(offsetRect, paint);

                paint.setColor(getColor(R.color.chat_bg_color));
            } else {
                left = getDimensionPixelOffset(R.dimen.base_xhdpi);

                paint.setColor(Color.WHITE);
                offsetRect.set(0, top, left, itemViewTop);
                canvas.drawRect(offsetRect, paint);

                paint.setColor(getColor(R.color.line_color));
            }
            offsetRect.set(left, top, itemView.getRight(), itemViewTop);
            canvas.drawRect(offsetRect, paint);
        }
    });

    @Override
    public String textChange() {
        return mSearchInputView.getText().toString();
    }

    public static class Options {

        public static Options sOptions = new Options(false);

        protected boolean searchFlag  = true;

        public boolean isSearchMuti() {
            return searchFlag;
        }

        public void setSearchFlag(boolean searchFlag) {
            this.searchFlag = searchFlag;
        }

        public Options(boolean searchFlag) {
            this.searchFlag = searchFlag;
        }
    }



}
