package com.hn.d.valley.main.message;

import android.widget.LinearLayout;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.view.IView;
import com.angcyo.uiview.widget.viewpager.UIPagerAdapter;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.emoji.EmojiManager;

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
    LinearLayout mTabLayout;

    public EmojiLayoutControl(RBaseViewHolder viewHolder) {
        mViewHolder = viewHolder;
        mUIViewPager = mViewHolder.v(R.id.view_pager);
        mTabLayout = mViewHolder.v(R.id.emoji_tab_layout);
    }

    public void init() {
        mUIViewPager.setAdapter();
    }

    class EmojiAdapter extends UIPagerAdapter {

        @Override
        protected IView getIView(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return EmojiManager.getDisplayCount();
        }
    }
}
