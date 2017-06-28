package com.hn.d.valley.main.teamavchat.test.loadmore;


import com.hn.d.valley.R;

public final class SimpleLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.base_item_load_more_layout;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_view;
    }
}
