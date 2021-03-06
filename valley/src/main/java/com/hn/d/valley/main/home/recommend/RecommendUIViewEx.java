package com.hn.d.valley.main.home.recommend;

import android.os.Bundle;
import android.view.View;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.TagsControl;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.main.home.HomeBaseRecyclerUIView;
import com.hn.d.valley.main.home.UserDiscussAdapter;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.widget.groupView.AutoPlayVideoControl;

import java.util.List;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：首页 下面的 推荐
 * 创建人员：Robi
 * 创建时间：2016/12/16 11:18
 * 修改人员：Robi
 * 修改时间：2016/12/16 11:18
 * 修改备注：
 * Version: 1.0.0
 */
public class RecommendUIViewEx extends HomeBaseRecyclerUIView {

    LoadStatusCallback mLoadStatusCallback;
    /**
     * 需要过滤的tag
     */
    private Tag filterTag;

    public RecommendUIViewEx(TagLoadStatusCallback loadStatusCallback) {
        mLoadStatusCallback = loadStatusCallback;
        filterTag = TagsControl.recommendTag;
    }

    public Tag getFilterTag() {
        return filterTag;
    }

    public void setFilterTag(Tag filterTag) {
        setFilterTag(filterTag, false);
    }

    public void setFilterTag(Tag filterTag, boolean refresh) {
        this.filterTag = filterTag;
        if (refresh) {
            onCancel();//取消之前的请求
            if (mRecyclerView == null) {
                loadData();
            } else {
                mRecyclerView.scrollTo(0, false);
                mRefreshLayout.setRefreshState(RefreshLayout.TOP);
            }
        }
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    public void loadData() {
        super.loadData();
    }

    @Override
    protected void onShowContentLayout() {
        super.onShowContentLayout();
        AutoPlayVideoControl.INSTANCE.init(getRecyclerView());
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        AutoPlayVideoControl.INSTANCE.stopPlay();
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        AutoPlayVideoControl.INSTANCE.init(getRecyclerView());
        post(new Runnable() {
            @Override
            public void run() {
                AutoPlayVideoControl.INSTANCE.checkPlay();
            }
        });
    }

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
                }, new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        //mParentILayout.startIView(new DynamicDetailUIView2(dataBean.getDiscuss_id()));
                        UserDiscussItemControl.jumpToDynamicDetailUIView(mParentILayout, dataBean.getDiscuss_id(),
                                false, false, false, aBoolean);
                    }
                }, getILayout());

            }

            @Override
            protected ILayout getILayout() {
                return mParentILayout;
            }
        };
    }

    @Override
    protected void onUILoadData(final String page) {
        mLoadStatusCallback.onLoadStart();
        add(RRetrofit.create(UserService.class)
                .discussList(Param.buildMap("uid:" + UserCache.getUserAccount(),
                        "type:" + 2, "page:" + page, "tag:" + getFilterTagId(), "first_id:" + first_id, "last_id:" + last_id))
                .compose(Rx.transformer(UserDiscussListBean.class))
                .subscribe(new BaseSingleSubscriber<UserDiscussListBean>() {

                    @Override
                    public void onSucceed(UserDiscussListBean userDiscussListBean) {
                        if (userDiscussListBean == null) {
                            onUILoadDataEnd();
                        } else {
                            List<UserDiscussListBean.DataListBean> data_list = userDiscussListBean.getData_list();
                            initConfigId(userDiscussListBean);

                            onUILoadDataEnd(data_list);

                            if (filterTag == TagsControl.recommendTag) {
                                last_id = String.valueOf(userDiscussListBean.getLast_id());
                                mRExBaseAdapter.setEnableLoadMore(true);
                                onUILoadDataFinish();
                            }
                        }
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        onUILoadDataFinish();
                        mLoadStatusCallback.onLoadEnd();
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                        showNonetLayout(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadData();
                            }
                        });
                    }

                }));
    }

    public String getFilterTagId() {
        if (filterTag == null) {
            return "";
        }
        return filterTag.getId();
    }
}
