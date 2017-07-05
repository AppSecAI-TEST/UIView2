package com.hn.d.valley.main.me;

import android.os.Bundle;
import android.view.View;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.CollectInformationListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.found.sub.HotInfoListUIView;
import com.hn.d.valley.service.NewsService;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：我收藏的资讯
 * 创建人员：Robi
 * 创建时间：2017/03/01 10:24
 * 修改人员：Robi
 * 修改时间：2017/03/01 10:24
 * 修改备注：
 * Version: 1.0.0
 */
public class MyCollectInformationUIView extends HotInfoListUIView {

    public MyCollectInformationUIView() {
        super("");
    }

    @Override
    protected int getTitleResource() {
        return R.string.information;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return createTitleBarPattern()
                .setShowBackImageView(true);
    }

    @Override
    protected boolean isLoadInViewPager() {
        return true;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadData();
    }

    @Override
    protected void onUILoadData(String page) {
//        super.onUILoadData(page);
        add(RRetrofit.create(NewsService.class)
                .collectlist(Param.buildInfoMap("uid:" + UserCache.getUserAccount(),
                        "lastid:" + last_id, "amount:" + Constant.DEFAULT_PAGE_DATA_COUNT))
                .compose(Rx.transformer(CollectInformationListBean.class))
                .subscribe(new BaseSingleSubscriber<CollectInformationListBean>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onSucceed(CollectInformationListBean userDiscussListBean) {
                        super.onSucceed(userDiscussListBean);
                        if (userDiscussListBean == null) {
                            onUILoadDataEnd();
                        } else {
                            last_id = String.valueOf(userDiscussListBean.getLastid());
                            onUILoadDataEnd(userDiscussListBean.getList());
                        }
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        hideLoadView();
                        if (isError) {
                            showNonetLayout(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadData();
                                }
                            });
                        }
                    }
                }));
    }
}
