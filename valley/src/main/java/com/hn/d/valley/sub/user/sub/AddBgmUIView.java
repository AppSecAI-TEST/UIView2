package com.hn.d.valley.sub.user.sub;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RSwipeRecycleView;
import com.angcyo.uiview.recycler.adapter.RBaseSwipeAdapter;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.widget.MenuBuilder;
import com.angcyo.uiview.rsen.PlaceholderView;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.angcyo.uiview.widget.RecordTimeView;
import com.example.m3b.Audio;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.MusicRealm;
import com.hn.d.valley.control.MusicControl;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.widget.HnPlayTextView;
import com.hn.d.valley.widget.HnRefreshLayout;

import java.util.List;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：添加配乐
 * 创建人员：Robi
 * 创建时间：2017/05/03 17:03
 * 修改人员：Robi
 * 修改时间：2017/05/03 17:03
 * 修改备注：
 * Version: 1.0.0
 */
public class AddBgmUIView extends SingleRecyclerUIView<MusicRealm> {

    boolean isMusicEmpty = true;
    Action1<MusicRealm> selectorAction;
    private HnPlayTextView mLastPlayTextView;

    /**
     * @param selectorAction 选中音乐之后的回调
     */
    public AddBgmUIView(Action1<MusicRealm> selectorAction) {
        this.selectorAction = selectorAction;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity, R.string.add_bgm_tip);
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    public int getDefaultBackgroundColor() {
        return getColor(R.color.chat_bg_color);
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        loadMusicData();

        mRecyclerView.addItemDecoration(new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
            @Override
            public void getItemOffsets2(Rect outRect, int position, int edge) {
                if (position > 1) {
                    outRect.top = getDimensionPixelOffset(R.dimen.base_line);
                }
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                drawLeftTopLine(canvas, paint, itemView, offsetRect, itemCount, position);
            }

            @Override
            protected int getLeftOffset(Context context) {
                return context.getResources().getDimensionPixelOffset(R.dimen.base_60dpi);
            }
        }));
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        Audio.instance().stop();
    }

    @Override
    protected void inflateRecyclerRootLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        mRootSoftInputLayout = new RSoftInputLayout(mActivity);
        mRefreshLayout = new HnRefreshLayout(mActivity);
        mRecyclerView = new RSwipeRecycleView(mActivity);
        mRefreshLayout.addView(mRecyclerView, new ViewGroup.LayoutParams(-1, -1));

        baseContentLayout.addView(mRefreshLayout, new ViewGroup.LayoutParams(-1, -1));
    }

    @Override
    protected void initRefreshLayout() {
        super.initRefreshLayout();
        mRefreshLayout.setTopView(new PlaceholderView(mActivity));
        mRefreshLayout.setBottomView(new PlaceholderView(mActivity));
        mRefreshLayout.setNotifyListener(false);
    }

    @Override
    protected RExBaseAdapter<String, MusicRealm, String> initRExBaseAdapter() {
        return new RBaseSwipeAdapter<String, MusicRealm, String>(mActivity) {
            @Override
            protected int getDataItemType(int posInData) {
                if (posInData == 0) {
                    return TYPE_DATA - 1;
                }
                return super.getDataItemType(posInData);
            }

            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TYPE_DATA - 1) {
                    return R.layout.item_bgm_top;
                }
                return R.layout.item_bgm_data;
            }

            @Override
            protected void onBindMenuView(MenuBuilder menuBuilder, int viewType, final int position) {
                if (viewType == TYPE_DATA - 1) {
                    return;
                }
                final MusicRealm dataBean = getAllDatas().get(position);
                menuBuilder.addMenu(getString(R.string.delete_text),
                        getColor(R.color.base_dark_red),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dataBean.deleteFile();
                                deleteItem(position);

                                if (getItemCount() <= 1) {
                                    isMusicEmpty = true;
                                    notifyItemChanged(0);
                                }

                                MusicControl.loadMusic(null);

//                                UIBottomItemDialog
//                                        .build()
//                                        .setTitleString(getString(R.string.delete_music_format, dataBean.getName()))
//                                        .addItem(getString(R.string.delete_text), new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                            }
//                                        })
//                                        .showDialog(AddBgmUIView.this);
                            }
                        });
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, final int posInData, final MusicRealm dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                if (posInData == 0) {
                    holder.v(R.id.empty_tip_view).setVisibility(isMusicEmpty ? View.VISIBLE : View.GONE);
                    holder.v(R.id.search_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startIView(new SearchBgmUIView());
                        }
                    });
                } else {
                    holder.tv(R.id.name_view).setText(dataBean.getName());
                    holder.tv(R.id.time_view).setText(RecordTimeView.formatMMSS(Long.parseLong(dataBean.getTime())));

                    /**添加*/
                    holder.v(R.id.add_music_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishIView();
                            if (selectorAction != null) {
                                selectorAction.call(dataBean);
                            }
                        }
                    });

                    /**试听*/
                    final HnPlayTextView playTextView = holder.v(R.id.play_text_view);
                    playTextView.setPlaying(MusicControl.isPlaying(dataBean.getPlayPath()));
                    if (MusicControl.isPlaying(dataBean.getPlayPath())) {
                        mLastPlayTextView = playTextView;
                    }
                    playTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (playTextView.isPlaying()) {
                                MusicControl.pausePlay(dataBean.getPlayPath());
                                playTextView.setPlaying(false);
                            } else {
                                if (mLastPlayTextView != null) {
                                    mLastPlayTextView.setPlaying(false);
                                }
                                mLastPlayTextView = playTextView;
                                MusicControl.play(dataBean.getPlayPath());
                                playTextView.setPlaying(true);
                            }
                        }
                    });
                }
            }
        };
    }

    @Override
    public void onViewShow(long viewShowCount) {
        super.onViewShow(viewShowCount);
        if (viewShowCount > 1) {
            mRExBaseAdapter.resetAllData(null);
            loadMusicData();
        }
    }

    private void loadMusicData() {
        mRExBaseAdapter.appendData(new MusicRealm());
        MusicControl.loadMusic(new Action1<List<MusicRealm>>() {
            @Override
            public void call(List<MusicRealm> musicRealms) {
                isMusicEmpty = musicRealms.isEmpty();
                if (!isMusicEmpty) {
                    mRExBaseAdapter.appendData(musicRealms);
                }
            }
        });
    }

}
