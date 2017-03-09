package com.hn.d.valley.main.home.circle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.PublishControl;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.main.home.NoTitleBaseRecyclerUIView;
import com.hn.d.valley.main.home.UserDiscussAdapter;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.user.DynamicDetailUIView;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：首页 下面的 圈子
 * 创建人员：Robi
 * 创建时间：2016/12/16 11:18
 * 修改人员：Robi
 * 修改时间：2016/12/16 11:18
 * 修改备注：
 * Version: 1.0.0
 */
public class CircleUIView extends NoTitleBaseRecyclerUIView<UserDiscussListBean.DataListBean> {

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
                        mOtherILayout.startIView(new DynamicDetailUIView(dataBean.getDiscuss_id()));
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
    protected int getEmptyTipStringId() {
        return R.string.default_empty_circle_tip;
    }

    @Override
    protected void onUILoadData(String page) {
        add(RRetrofit.create(UserInfoService.class)
                .discussList(Param.buildMap("uid:" + UserCache.getUserAccount(),
                        "type:" + 1, "page:" + page, "first_id:" + first_id, "last_id:" + last_id))
                .compose(Rx.transformer(UserDiscussListBean.class))
                .subscribe(new BaseSingleSubscriber<UserDiscussListBean>() {

                    @Override
                    public void onSucceed(UserDiscussListBean userDiscussListBean) {
                        if (userDiscussListBean == null) {
                            onUILoadDataEnd(null, 0);
                        } else {
                            List<UserDiscussListBean.DataListBean> data_list = userDiscussListBean.getData_list();
                            initConfigId(userDiscussListBean);
                            List<UserDiscussListBean.DataListBean> newDatas = insertPublishTask();
                            newDatas.addAll(data_list);
                            onUILoadDataEnd(newDatas, userDiscussListBean.getData_count());
                        }
                    }

                    @Override
                    public void onEnd() {
                        onUILoadDataFinish();
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

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        onShowInPager(null);
    }

    /**
     * 需要更新数据
     */
    @Subscribe(tags = {@Tag(Constant.TAG_UPDATE_CIRCLE)})
    public void onEvent(UpdateDataEvent event) {
        if (mIViewStatus == STATE_VIEW_SHOW) {
            if (getRecyclerView() != null) {
                getRecyclerView().smoothScrollToPosition(0);
            }
            loadData();
        }
    }

    /**
     * 发布了动态之后, 插入到第一条
     */
    public void onPublishStart() {
        if (mRExBaseAdapter == null) {
            return;
        }

        List<UserDiscussListBean.DataListBean> allDatas = mRExBaseAdapter.getAllDatas();

        List<UserDiscussListBean.DataListBean> newDatas = insertPublishTask();

        for (UserDiscussListBean.DataListBean bean : allDatas) {
            if (TextUtils.isEmpty(bean.uuid)) {
                newDatas.add(bean);
            }
        }

        mRExBaseAdapter.resetAllData(newDatas);
    }

    List<UserDiscussListBean.DataListBean> insertPublishTask() {
        List<UserDiscussListBean.DataListBean> newDatas = new ArrayList<>();
        List<UserDiscussListBean.DataListBean> list = PublishControl.instance().getDataListBeen();
        for (int i = list.size() - 1; i >= 0; i--) {
            newDatas.add(list.get(i));
        }
        return newDatas;
    }

}
