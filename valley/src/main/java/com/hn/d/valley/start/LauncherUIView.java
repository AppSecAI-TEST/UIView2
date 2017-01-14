package com.hn.d.valley.start;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.widget.viewpager.ImageAdapter;
import com.angcyo.uiview.widget.viewpager.RViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;

import rx.functions.Action0;

/**
 * 启动页
 * Created by angcyo on 2017-01-14.
 */

public class LauncherUIView extends BaseContentUIView {

    private RViewPager mViewPager;
    private Action0 mEndAction;

    public LauncherUIView(Action0 endAction) {
        mEndAction = endAction;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        mViewPager = new RViewPager(mActivity);
        mViewPager.setAdapter(new ImageAdapter() {

            @Override
            protected void initImageView(ImageView imageView, int position) {
                if (position == 0) {
                    imageView.setImageResource(R.drawable.guide_1);
                } else if (position == 1) {
                    imageView.setImageResource(R.drawable.guide_2);
                } else if (position == 2) {
                    imageView.setImageResource(R.drawable.guide_3);
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        baseContentLayout.addView(mViewPager, new ViewGroup.LayoutParams(-1, -1));

        mViewPager.setOnPagerEndListener(new RViewPager.OnPagerEndListener() {
            @Override
            public void onPagerEnd() {
                if (mEndAction != null) {
                    mEndAction.call();
                }
            }
        });
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }
}
