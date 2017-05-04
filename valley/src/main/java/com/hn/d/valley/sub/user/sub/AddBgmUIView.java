package com.hn.d.valley.sub.user.sub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.rsen.PlaceholderView;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.MusicRealm;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;

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
        mRExBaseAdapter.appendData(new MusicRealm());
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
        return new RExBaseAdapter<String, MusicRealm, String>(mActivity) {
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
            protected void onBindDataView(RBaseViewHolder holder, int posInData, MusicRealm dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                if (posInData == 0) {
                    holder.v(R.id.empty_tip_view).setVisibility(isMusicEmpty ? View.VISIBLE : View.GONE);
                    holder.v(R.id.search_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startIView(new SearchBgmUIView());
                        }
                    });
                }
            }
        };
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
    }
}
