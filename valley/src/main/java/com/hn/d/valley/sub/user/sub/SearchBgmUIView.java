package com.hn.d.valley.sub.user.sub;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.bean.HotInfoListBean;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.SearchResultBean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：搜索音乐界面
 * 创建人员：Robi
 * 创建时间：2017/03/28 10:18
 * 修改人员：Robi
 * 修改时间：2017/03/28 10:18
 * 修改备注：
 * Version: 1.0.0
 */
public class SearchBgmUIView extends BaseContentUIView {

    private ExEditText mEditText;
    private RRecyclerView mRecyclerView;
    private GridLayoutManager mSearchLayoutManager;
    private RExBaseAdapter<LikeUserInfoBean, SearchResultBean.DynamicsBeanX.DynamicsBean, HotInfoListBean> mSearchAdapter;
    private RExItemDecoration mSearchItemDecoration;

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_search_next_layout);
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        loadSearchHistory();
    }

    private void loadSearchHistory() {
//        RRealm.exe(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                mHistoryRealmList = new ArrayList<>();
//                mHistoryRealmList.add(new SearchHistoryRealm());//空的占位数据bean
//                RealmResults<SearchHistoryRealm> results = realm.where(SearchHistoryRealm.class).findAll();
//
//                for (int i = results.size() - 1; i >= 0; i--) {
//                    if (IsMax()) {
//                        break;
//                    }
//                    mHistoryRealmList.add(results.get(i));
//                }
//            }
//        });
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        mViewHolder.v(R.id.title_bar_layout).setBackgroundColor(SkinHelper.getSkin().getThemeColor());

        RefreshLayout refreshLayout = mViewHolder.v(R.id.refresh_layout);
        refreshLayout.setNotifyListener(false);
        refreshLayout.setRefreshDirection(RefreshLayout.TOP);

        initRecyclerView();
        mEditText = mViewHolder.v(R.id.edit_text_view);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                CharSequence text = v.getText();
                if (TextUtils.isEmpty(text)) {
                    return false;
                }
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchText(text.toString(), true);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Adapter数据是否为空
     */
    private boolean isDataEmpty() {
        return true;
    }

    protected void initRecyclerView() {
        mRecyclerView = mViewHolder.v(R.id.recycler_view);

        /**搜索结果配置*/
        mSearchLayoutManager = new GridLayoutManager(mActivity, 1);
        mSearchAdapter = new RExBaseAdapter<LikeUserInfoBean,
                SearchResultBean.DynamicsBeanX.DynamicsBean,
                HotInfoListBean>(mActivity) {
            @Override
            protected int getItemLayoutId(int viewType) {
                if (isDataEmpty()) {
                    return R.layout.item_bgm_search_empty;
                }
                return R.layout.item_bgm_search_data;
            }

            @Override
            public int getItemCount() {
                if (isDataEmpty()) {
                    return 1;
                }
                return super.getItemCount();
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, SearchResultBean.DynamicsBeanX.DynamicsBean dataBean) {

            }

        };
        mSearchItemDecoration = new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
            @Override
            public void getItemOffsets2(Rect outRect, int position, int edge) {
                super.getItemOffsets2(outRect, position, edge);
            }
        });

        mRecyclerView.setLayoutManager(mSearchLayoutManager);
        mRecyclerView.setAdapter(mSearchAdapter);
        mRecyclerView.addItemDecoration(mSearchItemDecoration);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadHotwords();
    }

    /**
     * 加载热词
     */
    private void loadHotwords() {

    }

    private void searchText(String text, boolean add) {

    }


    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mViewHolder.v(R.id.cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishIView();
            }
        });
    }
}
