package com.hn.d.valley.main.message;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.viewpager.RPagerAdapter;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.emoji.EmojiManager;
import com.hn.d.valley.emoji.EmojiRecyclerView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/30 16:02
 * 修改人员：Robi
 * 修改时间：2016/12/30 16:02
 * 修改备注：
 * Version: 1.0.0
 */
public class EmojiLayoutControl {

    public static final int EMOJI_PER_PAGE = 27; // 最后一个是删除键

    RBaseViewHolder mViewHolder;
    UIViewPager mUIViewPager;
    OnEmojiSelectListener mOnEmojiSelectListener;
    LinearLayout pageNumberLayout;

    Context context;

    /**
     * 总页数.
     */
    private int pageCount;

    public EmojiLayoutControl(RBaseViewHolder viewHolder, final OnEmojiSelectListener listener) {
        mViewHolder = viewHolder;
        mUIViewPager = mViewHolder.v(R.id.view_pager);
        pageNumberLayout = mViewHolder.v(R.id.layout_scr_bottom);
        mOnEmojiSelectListener = listener;
        context = viewHolder.getContext();

        mUIViewPager.setAdapter(pagerAdapter);

        mUIViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                mTabLayout.setCheckedIndex(position, false);
                setCurEmotionPage(position);
            }
        });

        showEmojiGridView();


    }

    RPagerAdapter pagerAdapter = new RPagerAdapter() {
        @Override
        protected View getView(LayoutInflater from, ViewGroup container, int position) {

            int pos = position;

            EmojiRecyclerView recyclerView = new EmojiRecyclerView(context, pos * EMOJI_PER_PAGE);
            recyclerView.setOnEmojiSelectListener(mOnEmojiSelectListener);
            container.addView(recyclerView);
            return recyclerView;
        }

        @Override
        public int getCount() {
            return pageCount == 0 ? 1 : pageCount;
        }
    };

    private void setCurEmotionPage(int position) {
        setCurPage(position, pageCount);
    }

    private void showEmojiGridView() {
        pageCount = (int) Math.ceil(EmojiManager.getDisplayCount() / (float) EMOJI_PER_PAGE);
        pagerAdapter.notifyDataSetChanged();
        resetEmotionPager();
    }

    private void resetEmotionPager() {
        setCurEmotionPage(0);
        mUIViewPager.setCurrentItem(0, false);
    }

    private void setCurPage(int page, int pageCount) {
        int hasCount = pageNumberLayout.getChildCount();
        int forMax = Math.max(hasCount, pageCount);

        ImageView imgCur = null;
        for (int i = 0; i < forMax; i++) {
            if (pageCount <= hasCount) {
                if (i >= pageCount) {
                    pageNumberLayout.getChildAt(i).setVisibility(View.GONE);
                    continue;
                } else {
                    imgCur = (ImageView) pageNumberLayout.getChildAt(i);
                }
            } else {
                if (i < hasCount) {
                    imgCur = (ImageView) pageNumberLayout.getChildAt(i);
                } else {
                    imgCur = new ImageView(context);
                    imgCur.setBackgroundResource(R.drawable.nim_view_pager_indicator_selector);
                    pageNumberLayout.addView(imgCur);
                }
            }

            imgCur.setId(i);
            imgCur.setSelected(i == page); // 判断当前页码来更新
            imgCur.setVisibility(View.VISIBLE);
        }
    }


    public interface OnEmojiSelectListener {
        void onEmojiText(String emoji);
    }

}
