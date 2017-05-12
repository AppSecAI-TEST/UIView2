package com.hn.d.valley.sub.user.sub;

import android.graphics.Canvas;
import android.graphics.Rect;
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
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.widget.IShowState;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RDownloadView;
import com.angcyo.uiview.widget.RTextView;
import com.example.m3b.Audio;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.MusicBean;
import com.hn.d.valley.bean.realm.MusicRealm;
import com.hn.d.valley.bean.realm.SearchMusicHistoryRealm;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.MusicControl;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.MusicService;
import com.hn.d.valley.widget.HnPlayView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

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

    List<MusicRealm> mMusicRealms = new ArrayList<>();
    int page = 1;
    private ExEditText mEditText;
    private RRecyclerView mRecyclerView;
    private GridLayoutManager mSearchLayoutManager;
    private RExBaseAdapter<String, MusicRealm, String> mSearchAdapter;
    private RExItemDecoration mSearchItemDecoration;
    private String mSearchWord = "";
    private RExBaseAdapter<String, SearchMusicHistoryRealm, String> mHistoryAdapter;
    private ArrayList<SearchMusicHistoryRealm> mHistoryRealmList = new ArrayList<>();
    private HnPlayView mLastPlayView;

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_search_next_layout);
    }

    @Override
    public int getDefaultBackgroundColor() {
        return getColor(R.color.chat_bg_color);
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
                RealmResults<SearchMusicHistoryRealm> results = realm.where(SearchMusicHistoryRealm.class)
                        .equalTo("uid", UserCache.getUserAccount()).findAll();
                mHistoryRealmList.addAll(results);
                Collections.reverse(mHistoryRealmList);//逆序
            }
        });
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
                    //mEditText.requestFocus();
                } else if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    page = 1;
                    mSearchWord = text.toString();
                    mSearchAdapter.setEnableLoadMore(false);
                    searchText(mSearchWord);
                }
                return true;
            }
        });
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        Audio.instance().stop();
    }

    /**
     * Adapter数据是否为空
     */
    private boolean isDataEmpty() {
        return mMusicRealms.isEmpty() && mHistoryRealmList.isEmpty();
    }

    protected void initRecyclerView() {
        mRecyclerView = mViewHolder.v(R.id.recycler_view);

        /**搜索历史*/
        mHistoryAdapter = new RExBaseAdapter<String, SearchMusicHistoryRealm, String>(mActivity, mHistoryRealmList) {

            @Override
            public int getItemType(int position) {
                if (position == 0) {
                    return TYPE_DATA + 1;
                }
                return TYPE_DATA + 2;
            }

            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TYPE_DATA + 1) {
                    return R.layout.item_search_next_layout_top;
                } else if (viewType == TYPE_DATA + 2) {
                    return R.layout.item_single_delete_text_view;
                }

                return super.getItemLayoutId(viewType);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount() + 1;
            }

            @Override
            protected void onBindCommonView(RBaseViewHolder holder, final int position, final SearchMusicHistoryRealm bean) {
                if (position == 0) {
                    holder.v(R.id.trash_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mHistoryRealmList.clear();
                            RRealm.exe(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.where(SearchMusicHistoryRealm.class)
                                            .equalTo("uid", UserCache.getUserAccount())
                                            .findAll()
                                            .deleteAllFromRealm();
                                    mRecyclerView.setAdapter(mSearchAdapter);
                                }
                            });
                        }
                    });
                } else {
                    final SearchMusicHistoryRealm dataBean = mHistoryRealmList.get(position - 1);
                    holder.tv(R.id.text_view).setText(dataBean.getText());

                    holder.v(R.id.delete_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mAllDatas.remove(dataBean);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount() - position);

                            RRealm.exe(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    dataBean.deleteFromRealm();
                                }
                            });
                        }
                    });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSearchWord = dataBean.getText();
                            mEditText.setExText(mSearchWord);
                            searchText(mSearchWord);
                        }
                    });
                }
            }
        };

        /**搜索结果配置*/
        mSearchLayoutManager = new GridLayoutManager(mActivity, 1);
        mSearchAdapter = new RExBaseAdapter<String, MusicRealm, String>(mActivity) {
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
            protected void onBindDataView(RBaseViewHolder holder, int posInData, final MusicRealm dataBean) {
                if (isDataEmpty()) {

                } else {
                    RTextView nameView = holder.v(R.id.name_view);
                    RTextView albumView = holder.v(R.id.album_view);

                    nameView.setText(dataBean.getName());
                    albumView.setText(dataBean.getSigner() + " - " + dataBean.getAlbum());

                    nameView.setHighlightWord(mSearchWord);
                    albumView.setHighlightWord(mSearchWord);

                    /**下载*/
                    RDownloadView downloadView = holder.v(R.id.download_view);
                    downloadView.setTag(dataBean.getMp3());
                    MusicControl.initDownView(dataBean, new WeakReference<>(downloadView));
                    /**试听*/
                    final HnPlayView playView = holder.v(R.id.play_view);
                    playView.setPlaying(MusicControl.isPlaying(dataBean.getPlayPath()));
                    if (MusicControl.isPlaying(dataBean.getPlayPath())) {
                        mLastPlayView = playView;
                    }
                    playView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (playView.isPlaying()) {
                                MusicControl.pausePlay(dataBean.getPlayPath());
                                playView.setPlaying(false);
                            } else {
                                if (mLastPlayView != null) {
                                    mLastPlayView.setPlaying(false);
                                }
                                mLastPlayView = playView;
                                MusicControl.play(dataBean.getPlayPath());
                                playView.setPlaying(true);
                            }
                        }
                    });
                }
            }

        };
        mSearchAdapter.setOnLoadMoreListener(new RBaseAdapter.OnAdapterLoadMoreListener() {
            @Override
            public void onAdapterLodeMore(RBaseAdapter baseAdapter) {
                page++;
                searchText(mSearchWord);
            }
        });
        mSearchItemDecoration = new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
            @Override
            public void getItemOffsets2(Rect outRect, int position, int edge) {
                if (isDataEmpty()) {
                    return;
                }

                outRect.top = getDimensionPixelOffset(R.dimen.base_line);
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                drawLeftTopLine(canvas, paint, itemView, offsetRect, itemCount, position);
            }
        });

        mRecyclerView.setLayoutManager(mSearchLayoutManager);

        if (mHistoryRealmList.isEmpty()) {
            mRecyclerView.setAdapter(mSearchAdapter);
        } else {
            mRecyclerView.setAdapter(mHistoryAdapter);
        }
        mRecyclerView.addItemDecoration(mSearchItemDecoration);
    }

    private void searchText(String text) {
        if (mRecyclerView.getAdapter() != mSearchAdapter) {
            mRecyclerView.setAdapter(mSearchAdapter);
            mSearchAdapter.setShowState(IShowState.LOADING);
        }

        SearchMusicHistoryRealm historyRealm = new SearchMusicHistoryRealm(text, System.currentTimeMillis());
        if (!mHistoryRealmList.contains(historyRealm)) {
            RRealm.save(historyRealm);
            mHistoryRealmList.add(0, historyRealm);
        }

        onCancel();
        add(RRetrofit
                .create(MusicService.class)
                .search(Param.buildMap("key:" + text, "page:" + page))
                .compose(Rx.transformer(MusicBean.class))
                .subscribe(new BaseSingleSubscriber<MusicBean>() {
                    @Override
                    public void onSucceed(MusicBean bean) {
                        super.onSucceed(bean);
                        if (bean.getData_list() == null || bean.getData_list().isEmpty()) {
                            mSearchAdapter.setShowState(IShowState.EMPTY);
                        } else {
                            if (bean.getData_list().size() >= Constant.DEFAULT_PAGE_DATA_COUNT) {
                                mSearchAdapter.setEnableLoadMore(true);
                            } else {
                                mSearchAdapter.setEnableLoadMore(false);
                            }
                            mSearchAdapter.setShowState(IShowState.NORMAL);
                            if (page == 1) {
                                mMusicRealms.clear();
                                mMusicRealms.addAll(bean.getData_list());
                                mSearchAdapter.resetAllData(bean.getData_list());
                            } else {
                                mMusicRealms.addAll(bean.getData_list());
                                mSearchAdapter.appendData(bean.getData_list());
                                mSearchAdapter.setLoadMoreEnd();
                            }
                        }
                    }
                })
        );
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
