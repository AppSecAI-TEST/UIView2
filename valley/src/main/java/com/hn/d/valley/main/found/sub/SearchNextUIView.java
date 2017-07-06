package com.hn.d.valley.main.found.sub;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.angcyo.uiview.recycler.widget.IShowState;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.string.SingleTextWatcher;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.HotInfoListBean;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.SearchResultBean;
import com.hn.d.valley.bean.realm.SearchHistoryRealm;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.service.SearchService;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.sub.adapter.UserInfoAdapter;

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
    public static final int LAST_ITEM_TYPE = 999;
    RRecyclerView mRecyclerView;
    /**
     * 搜索历史
     */
    List<SearchHistoryRealm> mHistoryRealmList;
    /**
     * 热词
     */
    List<String> mHotwords;

    boolean isNormalModel = true;

    private RExBaseAdapter<SearchHistoryRealm, String, String> mNormalAdapter;
    private ExEditText mEditText;
    private RExItemDecoration mNormalDecoration;
    private GridLayoutManager mNormalLayoutManager;
    private GridLayoutManager mSearchLayoutManager;
    private RExBaseAdapter<LikeUserInfoBean,
            LikeUserInfoBean,
            HotInfoListBean> mSearchAdapter;
    private RExItemDecoration mSearchItemDecoration;
    private String curSearchText = "";

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
                RealmResults<SearchHistoryRealm> results = realm.where(SearchHistoryRealm.class)
                        .equalTo("uid", UserCache.getUserAccount()).findAll();

                int size = results.size();
                if (size > 0) {
                    mHistoryRealmList.add(new SearchHistoryRealm());//空的占位数据bean
                }

                for (int i = size - 1; i >= 0; i--) {
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

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadHotwords();
    }

    /**
     * 输入框文本变化
     */
    public void onInputTextChanged(Editable editable) {
        if (TextUtils.isEmpty(editable)) {
            switchToNormal();
        }
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

                        mNormalAdapter.resetAllData(mHotwords);
                    }
                })
        );
    }

    private void searchText(String text, boolean add) {
        curSearchText = text;

        if (add) {
            SearchHistoryRealm searchHistoryRealm = new SearchHistoryRealm(text, System.currentTimeMillis());

            if (!mHistoryRealmList.contains(searchHistoryRealm)) {
                RRealm.save(searchHistoryRealm);

                if (IsMax()) {
                    mHistoryRealmList.remove(mHistoryRealmList.size() - 1);
                }

                if (mHistoryRealmList.size() == 0) {
                    mHistoryRealmList.add(0, new SearchHistoryRealm());
                }
                mHistoryRealmList.add(1, searchHistoryRealm);

                mNormalAdapter.resetHeaderData(mHistoryRealmList);
                mNormalAdapter.notifyDataSetChanged();
            }

        }
        mEditText.setText(text);
        mEditText.setSelection(text.length());

        onCancel();
        add(RRetrofit.create(SearchService.class)
                .search(Param.buildInfoMap("type:abstract", "content:" + text))
                .compose(Rx.transformer(SearchResultBean.class))
                .subscribe(new BaseSingleSubscriber<SearchResultBean>() {
                    @Override
                    public void onSucceed(SearchResultBean bean) {
                        super.onSucceed(bean);
                        mSearchAdapter.setShowState(IShowState.NORMAL);

                        List<LikeUserInfoBean> users = bean.getUsers();
                        List<LikeUserInfoBean> newUsers = new ArrayList<>();
                        for (LikeUserInfoBean b : users) {
                            if (UserCache.getUserAccount().equalsIgnoreCase(b.getUid())) {
                            } else {
                                newUsers.add(b);
                            }
                        }
                        mSearchAdapter.resetHeaderData(newUsers);

                        mSearchAdapter.resetAllData(bean.getDynamics().getDynamics());
                        mSearchAdapter.resetFooterData(bean.getNews().getNews());
                    }
                })
        );
        switchToSearch();
    }

    /**
     * RecyclerView显示搜索结果
     */
    private void switchToSearch() {
        if (!isNormalModel) {
            return;
        }
        mRecyclerView.removeItemDecoration(mNormalDecoration);
        mRecyclerView.addItemDecoration(mSearchItemDecoration);
        mSearchAdapter.setShowState(IShowState.LOADING);
        mRecyclerView.setLayoutManager(mSearchLayoutManager);
        mRecyclerView.setAdapter(mSearchAdapter);
        isNormalModel = false;
    }

    /**
     * RecyclerView显示搜索历史
     */
    private void switchToNormal() {
        if (isNormalModel) {
            return;
        }
        mRecyclerView.setLayoutManager(mNormalLayoutManager);
        mRecyclerView.setAdapter(mNormalAdapter);
        mRecyclerView.addItemDecoration(mNormalDecoration);
        mRecyclerView.removeItemDecoration(mSearchItemDecoration);
        isNormalModel = true;
    }

    protected void initRecyclerView() {
        mRecyclerView = mViewHolder.v(R.id.recycler_view);
        /**搜索历史*/
        mNormalLayoutManager = new GridLayoutManager(mActivity, 2);
        mNormalLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position <= mHistoryRealmList.size() ? 2 : 1;
            }
        });
        mRecyclerView.setLayoutManager(mNormalLayoutManager);
        //空的占位数据bean
        mNormalAdapter = new RExBaseAdapter<SearchHistoryRealm, String, String>(mActivity) {
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
                                    realm.where(SearchHistoryRealm.class)
                                            .equalTo("uid", UserCache.getUserAccount())
                                            .findAll().deleteAllFromRealm();
                                    mHistoryRealmList = new ArrayList<>();
                                    mHistoryRealmList.add(new SearchHistoryRealm());//空的占位数据bean
                                    mNormalAdapter.resetHeaderData(mHistoryRealmList);
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
                            searchText(dataBean, true);
                        }
                    });
                }
            }
        };
        mNormalAdapter.resetHeaderData(mHistoryRealmList);
        mRecyclerView.setAdapter(mNormalAdapter);

        mNormalDecoration = new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
            @Override
            public void getItemOffsets(Rect outRect, int position) {
                int offset = getDimensionPixelOffset(R.dimen.base_line);

                if (position != 0 && position < mHistoryRealmList.size()) {
                    outRect.top = offset;
                } else if (position == mHistoryRealmList.size()) {
                    outRect.top = getDimensionPixelOffset(R.dimen.base_xhdpi);
                } else if (position == mHistoryRealmList.size() + 1 || position == mHistoryRealmList.size() + 2) {
                    outRect.top = offset;
                }

                if (position > mHistoryRealmList.size() && (position - 1 - mHistoryRealmList.size()) % 2 == 1) {
                    outRect.left = offset;
                }
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                int left = offsetRect.left;

                if (position == mHistoryRealmList.size()) {
                    paint.setColor(getColor(R.color.chat_bg_color));
                } else {
                    paint.setColor(getColor(R.color.line_color));
                }

                offsetRect.set(0, itemView.getTop() - offsetRect.top, itemView.getRight(), itemView.getTop());
                canvas.drawRect(offsetRect, paint);

                if (position > mHistoryRealmList.size() && (position - 1 - mHistoryRealmList.size()) % 2 == 1) {
                    int offset = getDimensionPixelOffset(R.dimen.base_xhdpi);
                    paint.setColor(getColor(R.color.line_color));
                    offsetRect.set(itemView.getLeft() - left, itemView.getTop() + offset,
                            itemView.getLeft(), itemView.getBottom() - offset);
                    canvas.drawRect(offsetRect, paint);
                }
            }
        });
        mRecyclerView.addItemDecoration(mNormalDecoration);


        /**搜索结果配置*/
        mSearchLayoutManager = new GridLayoutManager(mActivity, 1);
        mSearchAdapter = new RExBaseAdapter<LikeUserInfoBean, LikeUserInfoBean, HotInfoListBean>(mActivity) {
            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TYPE_HEADER) {
                    return R.layout.item_search_result_user;
                }
                if (viewType == LAST_ITEM_TYPE) {
                    return R.layout.item_search_result_search_last;
                }
                if (viewType == TYPE_DATA) {
                    return R.layout.item_search_result_user;
                }
                return R.layout.item_search_result_article;
//                }
//                return super.getItemLayoutId(viewType);
            }

            @Override
            public int getItemType(int position) {
                if (position == getItemCount() - 1) {
                    return LAST_ITEM_TYPE;
                }
                return super.getItemType(position);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount() + 1;
            }

            @Override
            public int getDataCount() {
                return Math.min(3, super.getDataCount());
            }

            @Override
            public int getFooterCount() {
                return Math.min(3, super.getFooterCount());
            }

            @Override
            public int getHeaderCount() {
                return Math.min(3, super.getHeaderCount());
            }

            private void resetLayout(RBaseViewHolder holder) {
                holder.v(R.id.tip_view).setVisibility(View.GONE);
                holder.v(R.id.search_line1).setVisibility(View.GONE);
                holder.v(R.id.search_line2).setVisibility(View.GONE);
                holder.v(R.id.tip_more_view).setVisibility(View.GONE);
            }

            /**用户item*/
            @Override
            protected void onBindHeaderView(RBaseViewHolder holder, int posInHeader, LikeUserInfoBean headerBean) {
                UserInfoAdapter.initUserItem(holder, headerBean, mILayout);
                UserInfoAdapter.initSignatureItem(holder.tv(R.id.signature), headerBean);

                RTextView nameView = holder.v(R.id.username);
                RTextView signView = holder.v(R.id.signature);
                nameView.setHighlightWord(curSearchText);
                signView.setHighlightWord(curSearchText);

                holder.v(R.id.follow_image_view).setVisibility(View.GONE);

                resetLayout(holder);
                if (posInHeader == 0) {
                    holder.tv(R.id.tip_view).setVisibility(View.VISIBLE);
                    holder.v(R.id.search_line1).setVisibility(View.VISIBLE);
                } else if (posInHeader == getHeaderCount() - 1 && mAllHeaderDatas.size() > 3) {
                    holder.tv(R.id.tip_more_view).setVisibility(View.VISIBLE);
                    holder.v(R.id.search_line2).setVisibility(View.VISIBLE);
                    holder.tv(R.id.tip_more_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mILayout.startIView(new MoreUserUIView(curSearchText));
                        }
                    });
                }
            }

            /**动态*/
            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, final LikeUserInfoBean dataBean) {
                MoreDynamicsUIView.Companion.initDynamicsItemLayout(holder, dataBean, mILayout, curSearchText);

//                SearchUIView.updateMediaLayout(dataBean.getContent(), dataBean.getId(), dataBean.getMedia(), dataBean.getMedia_type(),
//                        mILayout, holder);
//
//                HnGlideImageView imageView = holder.v(R.id.image_view);
//                imageView.setImageThumbUrl(dataBean.getAvatar());
//                imageView.setAuth("1".equalsIgnoreCase(dataBean.getIs_auth()));
//
//                holder.tv(R.id.author_view).setText(dataBean.getUsername());
//                holder.tv(R.id.time_view).setText(TimeUtil.getTimeShowString(dataBean.getCreated() * 1000, true));
//
//                holder.v(R.id.cnt_control_layout).setVisibility(View.GONE);
//                holder.v(R.id.delete_view).setVisibility(View.GONE);
//
                resetLayout(holder);
                if (posInData == 0) {
                    holder.tv(R.id.tip_view).setText(R.string.status);
                    holder.tv(R.id.tip_view).setVisibility(View.VISIBLE);
                    holder.v(R.id.search_line1).setVisibility(View.VISIBLE);
                } else if (posInData == getDataCount() - 1 && mAllDatas.size() > 3) {
                    holder.tv(R.id.tip_more_view).setText(R.string.more_status);
                    holder.tv(R.id.tip_more_view).setVisibility(View.VISIBLE);
                    holder.v(R.id.search_line2).setVisibility(View.VISIBLE);
                    holder.tv(R.id.tip_more_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mILayout.startIView(new MoreDynamicsUIView(curSearchText));
                        }
                    });
                }
            }

            /**资讯item*/
            @Override
            protected void onBindFooterView(RBaseViewHolder holder, int posInFooter, HotInfoListBean footerBean) {
                HotInfoListUIView.initItem(mParentILayout, holder, footerBean, curSearchText);
                holder.v(R.id.delete_view).setVisibility(View.GONE);
//                if (posInFooter == 0) {
//                    holder.v(R.id.tip_layout).setVisibility(View.VISIBLE);
//                    holder.tv(R.id.tip_text_view).setText(R.string.new_hot_info_tip);
//                } else {
//                    holder.v(R.id.tip_layout).setVisibility(View.GONE);
//                }
//                holder.v(R.id.line).setVisibility(View.VISIBLE);
//
//                holder.v(R.id.tip_layout).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });

                resetLayout(holder);
                if (posInFooter == 0) {
                    holder.tv(R.id.tip_view).setText(R.string.information);
                    holder.tv(R.id.tip_view).setVisibility(View.VISIBLE);
                    holder.v(R.id.search_line1).setVisibility(View.VISIBLE);
                } else if (posInFooter == getFooterCount() - 1 && mAllFooterDatas.size() > 3) {
                    holder.tv(R.id.tip_more_view).setText(R.string.more_information);
                    holder.tv(R.id.tip_more_view).setVisibility(View.VISIBLE);
                    holder.v(R.id.search_line2).setVisibility(View.VISIBLE);
                    holder.tv(R.id.tip_more_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mILayout.startIView(new MoreInformationUIView(curSearchText));
                        }
                    });
                }
            }

            @Override
            protected void onBindCommonView(RBaseViewHolder holder, int position, LikeUserInfoBean bean) {
                if (holder.getItemViewType() == LAST_ITEM_TYPE) {
                    holder.tv(R.id.search_word_view).setText(mEditText.getText());
                    holder.tv(R.id.search_word_view).setTextColor(SkinHelper.getSkin().getThemeColor());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mILayout.startIView(new MoreSearchUIView(curSearchText));
                        }
                    });
                } else {
                    super.onBindCommonView(holder, position, bean);
                }
            }
        };
        mSearchItemDecoration = new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
            @Override
            public void getItemOffsets(Rect outRect, int position) {
                if (position != 0) {
                    outRect.top = getDimensionPixelOffset(R.dimen.base_line);
                    if (position == mSearchAdapter.getHeaderCount() ||
                            position == mSearchAdapter.getHeaderCount() + mSearchAdapter.getDataCount() ||
                            position == mSearchAdapter.getHeaderCount() + mSearchAdapter.getDataCount() + mSearchAdapter.getFooterCount()) {
                        outRect.top = getDimensionPixelOffset(R.dimen.base_xhdpi);
                    }
                }
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                int left = 0;
                int itemViewTop = itemView.getTop();
                int top = itemViewTop - offsetRect.top;
                if (position == mSearchAdapter.getHeaderCount() ||
                        position == mSearchAdapter.getHeaderCount() + mSearchAdapter.getDataCount() ||
                        position == mSearchAdapter.getHeaderCount() + mSearchAdapter.getDataCount() + mSearchAdapter.getFooterCount()) {

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
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        final EditText editText = v(R.id.edit_text_view);
        editText.addTextChangedListener(new SingleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                onInputTextChanged(editText.getText());
            }
        });

        mViewHolder.v(R.id.cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishIView();
            }
        });
    }
}
