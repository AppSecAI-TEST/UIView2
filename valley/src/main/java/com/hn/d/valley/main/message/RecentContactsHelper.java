package com.hn.d.valley.main.message;

import android.view.View;

import com.angcyo.uiview.github.swipe.recyclerview.SwipeMenuRecyclerView;
import com.angcyo.uiview.recycler.RBaseViewHolder;

/**
 * Created by angcyo on 2016-12-25.
 */

public class RecentContactsHelper {
    RBaseViewHolder mViewHolder;

    SwipeMenuRecyclerView mSwipeMenuRecyclerView;

    public RecentContactsHelper() {
    }

    public void init(final View rootView) {
        mViewHolder = new RBaseViewHolder(rootView);
    }
}
