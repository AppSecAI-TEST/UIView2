package com.hn.d.valley.main.me;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.main.home.UserDiscussAdapter;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.sub.user.DynamicDetailUIView2;

import java.util.List;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：我收藏的动态
 * 创建人员：Robi
 * 创建时间：2017/03/01 10:24
 * 修改人员：Robi
 * 修改时间：2017/03/01 10:24
 * 修改备注：
 * Version: 1.0.0
 */
public class MyCollectDynamicStateUIView extends BaseRecyclerUIView<String, UserDiscussListBean.DataListBean, String> {
    @Override
    protected RExBaseAdapter<String, UserDiscussListBean.DataListBean, String> initRExBaseAdapter() {
        return new UserDiscussAdapter(mActivity) {
            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, final UserDiscussListBean.DataListBean dataBean) {
                //super.onBindDataView(holder, posInData, tBean);
                UserDiscussItemControl.initItem(mSubscriptions, holder, dataBean, new Action1<UserDiscussListBean.DataListBean>() {
                    @Override
                    public void call(UserDiscussListBean.DataListBean dataListBean) {
                        loadData();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        mOtherILayout.startIView(new DynamicDetailUIView2(dataBean.getDiscuss_id()));
                    }
                }, getILayout());
            }

            @Override
            protected ILayout getILayout() {
                return mOtherILayout;
            }
        };
    }

    @Override
    protected int getTitleResource() {
        return R.string.status;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setFloating(false)
                .setTitleHide(false)
                .setTitleBarBGColor(getTitleBarBGColor());
    }

    @Override
    protected boolean hasScrollListener() {
        return false;
    }

    @Override
    public int getDefaultBackgroundColor() {
        return getColor(R.color.base_main_color_bg_color);
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadData();
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(SocialService.class)
                .myCollect(Param.buildMap("type:discuss", "page:" + page))
                .compose(Rx.transformer(UserDiscussListBean.class))
                .subscribe(new BaseSingleSubscriber<UserDiscussListBean>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onSucceed(UserDiscussListBean userDiscussListBean) {
                        super.onSucceed(userDiscussListBean);

                        if (userDiscussListBean == null) {
                            onUILoadDataEnd();
                        } else {
                            List<UserDiscussListBean.DataListBean> data_list = userDiscussListBean.getData_list();
                            initConfigId(userDiscussListBean, false);
                            onUILoadDataEnd(data_list);
                        }
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                    }
                }));
    }
}
