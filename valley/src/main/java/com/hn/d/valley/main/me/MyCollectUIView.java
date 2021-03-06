package com.hn.d.valley.main.me;

import android.text.TextUtils;
import android.view.View;

import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.NewsService;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：我的收藏
 * 创建人员：Robi
 * 创建时间：2017/03/01 09:31
 * 修改人员：Robi
 * 修改时间：2017/03/01 09:31
 * 修改备注：
 * Version: 1.0.0
 */
public class MyCollectUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private ItemInfoLayout mStatusItemLayout;
    private ItemInfoLayout mHotItemLayout;

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_info_layout;
    }

    @Override
    protected int getTitleResource() {
        return R.string.my_collect;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(SocialService.class)
                .myCollect(Param.buildMap())
                .compose(Rx.transformerList(UserDiscussListBean.class))
                .subscribe(new BaseSingleSubscriber<List<UserDiscussListBean>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onSucceed(List<UserDiscussListBean> bean) {
                        super.onSucceed(bean);
                        if (mHotItemLayout != null) {
                            for (UserDiscussListBean model : bean) {
                                if (TextUtils.equals(model.getType(), "news")) {

                                } else if (TextUtils.equals(model.getType(), "discuss")) {
                                    mStatusItemLayout.setItemDarkText(model.getData_count() + "");
                                }
                            }
                        }
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        hideLoadView();
                    }
                }));

        add(RRetrofit.create(NewsService.class)
                .collectcount(Param.buildInfoMap("uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(String.class))
                .subscribe(new RSubscriber<String>() {
                    @Override
                    public void onSucceed(final String bean) {
                        super.onSucceed(bean);
                        if (mHotItemLayout != null) {
                            mHotItemLayout.setItemDarkText(bean);
                        }
                    }
                }));
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        int size = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
        int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);

        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                mStatusItemLayout = holder.v(R.id.item_info_layout);
                mStatusItemLayout.setItemText(getString(R.string.status));
                mStatusItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new MyCollectDynamicStateUIView());
                    }
                });
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(size, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                mHotItemLayout = holder.v(R.id.item_info_layout);
                mHotItemLayout.setItemText(getString(R.string.hot_information));

                mHotItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new MyCollectInformationUIView());
                    }
                });
            }
        }));
    }
}
