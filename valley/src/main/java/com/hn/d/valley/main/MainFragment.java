package com.hn.d.valley.main;

import android.os.Bundle;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.github.tablayout.CommonTabLayout;
import com.angcyo.uiview.github.tablayout.TabEntity;
import com.angcyo.uiview.github.tablayout.listener.CustomTabEntity;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.hn.d.valley.R;
import com.hn.d.valley.base.inner.RSupportFragment;
import com.hn.d.valley.cache.DataCacheManager;
import com.hn.d.valley.main.message.MessageFragment;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/28 17:14
 * 修改人员：Robi
 * 修改时间：2016/12/28 17:14
 * 修改备注：
 * Version: 1.0.0
 */
public class MainFragment extends RSupportFragment {

    @BindView(R.id.bottom_nav_layout)
    CommonTabLayout mBottomNavLayout;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void onCreateView() {
        initTabLayout();
        loadMultipleRootFragment(R.id.content_root_layout, 0, MessageFragment.newInstance());
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        DataCacheManager.buildDataCacheAsync();
    }

    private void initTabLayout() {
        ArrayList<CustomTabEntity> tabs = new ArrayList<>();
        tabs.add(new TabEntity(true, getString(R.string.nav_home_text), R.drawable.home_s, R.drawable.home_n));
        tabs.add(new TabEntity(true, getString(R.string.nav_found_text), R.drawable.found_s, R.drawable.found_n));
        tabs.add(new TabEntity(true, "", -1, -1));
        tabs.add(new TabEntity(true, getString(R.string.nav_message_text), R.drawable.message_s, R.drawable.message_n));
        tabs.add(new TabEntity(true, getString(R.string.nav_me_text), R.drawable.me_s, R.drawable.me_n));

        mBottomNavLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                L.e(position + "");
            }

            @Override
            public void onTabReselect(int position) {
                L.e(position + "");
            }
        });

        mBottomNavLayout.setTabData(tabs);
    }
}
