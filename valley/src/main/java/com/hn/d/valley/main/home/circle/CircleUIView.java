package com.hn.d.valley.main.home.circle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.PublishControl;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.main.home.HomeBaseRecyclerUIView;
import com.hn.d.valley.main.home.UserDiscussAdapter;
import com.hn.d.valley.main.home.recommend.LoadStatusCallback;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.sub.MyStatusUIView;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：首页 下面的 圈子, 朋友圈
 * 创建人员：Robi
 * 创建时间：2016/12/16 11:18
 * 修改人员：Robi
 * 修改时间：2016/12/16 11:18
 * 修改备注：
 * Version: 1.0.0
 */
public class CircleUIView extends HomeBaseRecyclerUIView {

    LoadStatusCallback mLoadStatusCallback;
    /**
     * 只在查看个人动态的时候使用
     */
    MyStatusUIView.StatusType mStatusType = MyStatusUIView.StatusType.ALL;
    private String to_uid;
    /**
     * 只在查看个人动态的时候使用
     */
    private boolean isInSubUIView = false;
    private boolean needRefresh = true;

    public CircleUIView(String to_uid) {
        this.to_uid = to_uid;
    }

    public CircleUIView(LoadStatusCallback loadStatusCallback) {
        mLoadStatusCallback = loadStatusCallback;
    }

    public void setInSubUIView(boolean inSubUIView) {
        isInSubUIView = inSubUIView;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public void setStatusType(MyStatusUIView.StatusType statusType) {
        mStatusType = statusType;
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
                }, new Action0() {
                    @Override
                    public void call() {
                        //mParentILayout.startIView(new DynamicDetailUIView2(dataBean.getDiscuss_id()));
                        UserDiscussItemControl.jumpToDynamicDetailUIView(mParentILayout, dataBean.getDiscuss_id(), false, false, false);
                    }
                }, getILayout());

                if (isInSubUIView) {
                    UserDiscussItemControl.showTopView(holder, dataBean);
                }
            }

            @Override
            protected ILayout getILayout() {
                return mParentILayout;
            }
        };
    }

    @Override
    protected void initRefreshLayout() {
        super.initRefreshLayout();
        if (isInSubUIView && !needRefresh) {
            mRefreshLayout.setRefreshDirection(RefreshLayout.NONE);
        }
    }

    @Override
    protected void inflateOverlayLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        if (isInSubUIView) {
            inflate(R.layout.layout_default_empty_pager);
        } else {
            super.inflateOverlayLayout(baseContentLayout, inflater);
        }
    }

    @Override
    protected void onEmptyData(boolean isEmpty) {
        super.onEmptyData(isEmpty);
    }

    @Override
    protected int getEmptyTipStringId() {
        if (isInSubUIView) {
            return R.string.status_empty__tip;
        }
        return R.string.default_empty_circle_tip;
    }

    private boolean isMe() {
        return TextUtils.equals(to_uid, UserCache.getUserAccount());
    }

    @Override
    public void onShowInPager(UIViewPager viewPager) {
        super.onShowInPager(viewPager);
    }

    @Override
    public void onHideInPager(UIViewPager viewPager) {
        if (isInSubUIView) {
            return;
        }
        super.onHideInPager(viewPager);
    }

    @Override
    protected void onUILoadData(String page) {
        if (mLoadStatusCallback != null) {
            mLoadStatusCallback.onLoadStart();
        }

        if (isInSubUIView) {
            add(RRetrofit.create(UserService.class)
                    .myDiscuss(Param.buildMap("to_uid:" + to_uid, "type:" + mStatusType.getId(), "page:" + page))
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
                        public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                            super.onEnd(isError, isNoNetwork, e);
                            onUILoadDataFinish();
                            if (mLoadStatusCallback != null) {
                                mLoadStatusCallback.onLoadEnd();
                            }

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
        } else {
            add(RRetrofit.create(UserService.class)
                    .discussList(buildParam(page))
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
                        public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                            super.onEnd(isError, isNoNetwork, e);
                            onUILoadDataFinish();
                            if (mLoadStatusCallback != null) {
                                mLoadStatusCallback.onLoadEnd();
                            }
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
    }

    private Map<String, String> buildParam(String page) {
        if (TextUtils.isEmpty(to_uid)) {
            return Param.buildMap("uid:" + UserCache.getUserAccount(),
                    "type:" + 1, "page:" + page, "first_id:" + first_id, "last_id:" + last_id);
        } else {
            return Param.buildMap("to_uid:" + to_uid, "page:" + page, "first_id:" + first_id, "last_id:" + last_id);
        }
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        if (!isInSubUIView) {
            onShowInPager(null);
        }
    }

    /**
     * 需要更新数据
     */
    @Subscribe(tags = {@Tag(Constant.TAG_UPDATE_CIRCLE)})
    public void onEvent(UpdateDataEvent event) {
        if (mIViewStatus == IViewShowState.STATE_VIEW_SHOW) {
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

    public void setLoadStatusCallback(LoadStatusCallback loadStatusCallback) {
        mLoadStatusCallback = loadStatusCallback;
    }

    public void onPublishError() {
        if (mRExBaseAdapter == null) {
            return;
        }

        List<UserDiscussListBean.DataListBean> list = PublishControl.instance().getDataListBeen();
        mRExBaseAdapter.notifyItemRangeChanged(0, list.size());
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
