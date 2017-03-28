package com.hn.d.valley.main.found.sub;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.SearchHistoryRealm;
import com.hn.d.valley.main.message.service.SearchService;
import com.hn.d.valley.realm.RRealm;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：搜索界面
 * 创建人员：Robi
 * 创建时间：2017/03/28 10:18
 * 修改人员：Robi
 * 修改时间：2017/03/28 10:18
 * 修改备注：
 * Version: 1.0.0
 */
public class SearchNextUIView extends BaseContentUIView {

    public static final int MAX_HISTORY_COUNT = 3;
    RRecyclerView mRecyclerView;
    /**
     * 搜索历史
     */
    List<SearchHistoryRealm> mHistoryRealmList;
    /**
     * 热词
     */
    List<String> mHotwords;
    private RExBaseAdapter<SearchHistoryRealm, String, String> mBaseAdapter;
    private ExEditText mEditText;

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
        RRealm.exe(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mHistoryRealmList = new ArrayList<>();
                mHistoryRealmList.add(new SearchHistoryRealm());//空的占位数据bean
                RealmResults<SearchHistoryRealm> results = realm.where(SearchHistoryRealm.class).findAll();

                for (int i = results.size() - 1; i >= 0; i--) {
                    if (IsMax()) {
                        break;
                    }
                    mHistoryRealmList.add(results.get(i));
                }
            }
        });
    }

    protected boolean IsMax() {
        return mHistoryRealmList.size() >= MAX_HISTORY_COUNT + 1;
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
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

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadHotwords();
    }

    /**
     * 加载热词
     */
    private void loadHotwords() {
        add(RRetrofit.create(SearchService.class)
                .hotwords(Param.buildInfoMap())
                .compose(Rx.transformerList(String.class))
                .subscribe(new BaseSingleSubscriber<List<String>>() {
                    @Override
                    public void onSucceed(List<String> bean) {
                        mHotwords = new ArrayList<>();
                        mHotwords.add("");//分组占位数据
                        mHotwords.addAll(bean);

                        mBaseAdapter.resetAllData(mHotwords);
                    }
                })
        );
    }

    private void searchText(String text, boolean add) {
        if (add) {
            SearchHistoryRealm searchHistoryRealm = new SearchHistoryRealm(text, System.currentTimeMillis());
            RRealm.save(searchHistoryRealm);
            if (IsMax()) {
                mHistoryRealmList.remove(mHistoryRealmList.size() - 1);
            }
            mHistoryRealmList.add(1, searchHistoryRealm);
            mBaseAdapter.resetHeaderData(mHistoryRealmList);
            mBaseAdapter.notifyDataSetChanged();
        } else {

        }
    }

    protected void initRecyclerView() {
        mRecyclerView = mViewHolder.v(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position <= mHistoryRealmList.size() ? 2 : 1;
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        //空的占位数据bean
        mBaseAdapter = new RExBaseAdapter<SearchHistoryRealm, String, String>(mActivity) {
            @Override
            protected int getHeaderItemType(int posInHeader) {
                if (posInHeader == 0) {
                    return TYPE_HEADER + 1;
                }
                return TYPE_HEADER + 2;
            }

            @Override
            protected int getDataItemType(int posInData) {
                if (posInData == 0) {
                    return TYPE_DATA + 1;
                }
                return TYPE_DATA + 2;
            }

            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TYPE_HEADER + 1 || viewType == TYPE_DATA + 1) {
                    return R.layout.item_search_next_layout_top;
                } else if (viewType == TYPE_HEADER + 2 || viewType == TYPE_DATA + 2) {
                    return R.layout.item_single_main_text_view;
                }

                return super.getItemLayoutId(viewType);
            }

            @Override
            protected void onBindHeaderView(RBaseViewHolder holder, int posInHeader, final SearchHistoryRealm headerBean) {
                if (posInHeader == 0) {
                    holder.tv(R.id.tip_view).setText(getString(R.string.search_history_tip));
                    holder.v(R.id.trash_view).setVisibility(View.VISIBLE);
                    holder.v(R.id.trash_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RRealm.exe(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.where(SearchHistoryRealm.class).findAll().deleteAllFromRealm();
                                    mHistoryRealmList = new ArrayList<>();
                                    mHistoryRealmList.add(new SearchHistoryRealm());//空的占位数据bean
                                    mBaseAdapter.resetHeaderData(mHistoryRealmList);
                                }
                            });
                        }
                    });
                } else {
                    holder.tv(R.id.text_view).setText(headerBean.getText());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchText(headerBean.getText(), false);
                        }
                    });
                }
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, final String dataBean) {
                if (posInData == 0) {
                    holder.tv(R.id.tip_view).setText(getString(R.string.search_hotword_tip));
                    holder.v(R.id.trash_view).setVisibility(View.GONE);
                } else {
                    holder.tv(R.id.text_view).setText(dataBean);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchText(dataBean, false);
                        }
                    });
                }
            }
        };
        mBaseAdapter.resetHeaderData(mHistoryRealmList);
        mRecyclerView.setAdapter(mBaseAdapter);

        mRecyclerView.addItemDecoration(new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
            @Override
            public void getItemOffsets(Rect outRect, int position) {
                if (position != 0 && position < mHistoryRealmList.size()) {
                    outRect.top = getDimensionPixelOffset(R.dimen.base_line);
                } else if (position == mHistoryRealmList.size()) {
                    outRect.top = getDimensionPixelOffset(R.dimen.base_xhdpi);
                } else if (position == mHistoryRealmList.size() + 1 || position == mHistoryRealmList.size() + 2) {
                    outRect.top = getDimensionPixelOffset(R.dimen.base_line);
                }
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                if (position == mHistoryRealmList.size()) {
                    paint.setColor(getColor(R.color.chat_bg_color));
                } else {
                    paint.setColor(getColor(R.color.line_color));
                }
                offsetRect.set(0, itemView.getTop() - offsetRect.top, itemView.getRight(), itemView.getTop());
                canvas.drawRect(offsetRect, paint);
            }
        }));
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
