package com.hn.d.valley.main.found;

import android.graphics.Rect;
import android.view.View;

import com.angcyo.uiview.base.Item;
import com.angcyo.uiview.base.SingleItem;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseItemUIView;
import com.hn.d.valley.main.found.sub.HotInformationUIView;
import com.hn.d.valley.main.found.sub.ScanUIView;
import com.hn.d.valley.main.found.sub.SearchUIView;
import com.hn.d.valley.main.home.nearby.NearbyUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：发现
 * 创建人员：Robi
 * 创建时间：2016/12/16 13:58
 * 修改人员：Robi
 * 修改时间：2016/12/16 13:58
 * 修改备注：
 * Version: 1.0.0
 */
public class FoundUIView extends BaseItemUIView {

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.nav_found_text)).setShowBackImageView(false);
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
//        mBaseOffsetSize = getDimensionPixelOffset(R.dimen.base_item_size);

    }

    @Override
    protected void createItems(List<SingleItem> items) {
        items.add(new SingleItem(SingleItem.Type.TOP) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                baseInitItem(holder, R.drawable.hot_news, getString(R.string.hot_information), mBaseOffsetSize, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new HotInformationUIView());
                    }
                });
            }
        });
        items.add(new SingleItem(SingleItem.Type.TOP) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                baseInitItem(holder, R.drawable.invite_friends, getString(R.string.nearby_perple), mBaseOffsetSize, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new NearbyUIView());
                    }
                });
            }
        });
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {

            @Override
            public void setItemOffsets(Rect rect) {
                super.setItemOffsets(rect);
                super.leftOffset = getDimensionPixelOffset(R.dimen.base_45dpi);
            }

            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                baseInitItem(holder, R.drawable.search, getString(R.string.scan_title), mBaseOffsetSize, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new ScanUIView());
                    }
                });
            }
        });

        items.add(new SingleItem(SingleItem.Type.TOP) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                baseInitItem(holder, R.drawable.scan, getString(R.string.search_title), mBaseOffsetSize, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new SearchUIView());
                    }
                });
            }
        });

        items.add(new SingleItem(SingleItem.Type.TOP) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                baseInitItem(holder, R.drawable.search, getString(R.string.game), mBaseOffsetSize, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });

    }
}
