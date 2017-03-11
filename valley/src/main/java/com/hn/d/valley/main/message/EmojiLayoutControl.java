package com.hn.d.valley.main.message;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.EmojiTabLayout;
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
    RBaseViewHolder mViewHolder;
    UIViewPager mUIViewPager;
    EmojiTabLayout mTabLayout;
    OnEmojiSelectListener mOnEmojiSelectListener;

    public EmojiLayoutControl(RBaseViewHolder viewHolder, final OnEmojiSelectListener listener) {
        mViewHolder = viewHolder;
        mUIViewPager = mViewHolder.v(R.id.view_pager);
        mTabLayout = mViewHolder.v(R.id.emoji_tab_layout);
        mOnEmojiSelectListener = listener;

        mTabLayout.addItem(R.drawable.nim_emoji_icon);
        mTabLayout.addItem(R.drawable.nim_emoji_icon);
        mTabLayout.addItem(R.drawable.nim_emoji_icon);
        mTabLayout.addItem(R.drawable.nim_emoji_icon);
        mTabLayout.addItem(R.drawable.nim_emoji_icon);

        mTabLayout.setOnTabSelectorListener(new EmojiTabLayout.OnTabSelectorListener() {
            @Override
            public void onTabSelect(int index) {
                L.i("" + index);
                if (index > mUIViewPager.getAdapter().getCount()) {
                    return;
                }
                mUIViewPager.setCurrentItem(index);
            }

            @Override
            public void onTabReselect(int index) {
                L.i("" + index);
            }
        });

        mUIViewPager.setAdapter(new RPagerAdapter() {
            @Override
            protected View getView(LayoutInflater from, ViewGroup container, int position) {
//                RRecyclerView recyclerView = new RRecyclerView(container.getContext());
//                recyclerView.setItemAnim(false);
//                recyclerView.setAdapter(new EmojiAdapter(container.getContext()));
//                recyclerView.setTag("GV8");
                EmojiRecyclerView recyclerView = new EmojiRecyclerView(container.getContext());
                recyclerView.setOnEmojiSelectListener(mOnEmojiSelectListener);
                container.addView(recyclerView);
                return recyclerView;
            }

            @Override
            public int getCount() {
                return 5;
            }
        });

        mUIViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTabLayout.setCheckedIndex(position, false);
            }
        });

    }

    public void init() {
//        mUIViewPager.setAdapter();
    }

    public interface OnEmojiSelectListener {
        void onEmojiText(String emoji);
    }

    @Deprecated
    class EmojiAdapter extends RBaseAdapter<String> {

        public EmojiAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return 0;
        }

        @Override
        protected View createContentView(ViewGroup parent, int viewType) {
            RelativeLayout layout = new RelativeLayout(mContext);
            layout.setBackgroundResource(R.drawable.base_bg_selector);

            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            final float density = mContext.getResources().getDisplayMetrics().density;
            int width = (int) (density * 28);
            int height = (int) (density * 40);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(imageView, params);
            return layout;
        }

        @Override
        public int getItemCount() {
            return EmojiManager.getDisplayCount();
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, final int position, String bean) {
            ((ImageView) ((ViewGroup) holder.itemView).getChildAt(0)).setImageDrawable(EmojiManager.getDisplayDrawable(mContext, position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnEmojiSelectListener != null) {
                        mOnEmojiSelectListener.onEmojiText(EmojiManager.getDisplayText(position));
                    }
                }
            });
        }
    }
}
