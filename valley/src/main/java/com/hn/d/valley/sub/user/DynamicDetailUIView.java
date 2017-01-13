package com.hn.d.valley.sub.user;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.CommentListBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.sub.user.service.SocialService;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func2;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/13 19:27
 * 修改人员：Robi
 * 修改时间：2017/01/13 19:27
 * 修改备注：
 * Version: 1.0.0
 */
public class DynamicDetailUIView extends BaseRecyclerUIView<UserDiscussListBean.DataListBean, CommentListBean.DataListBean, String> {

    /**
     * 动态id
     */
    private String discuss_id;

    public DynamicDetailUIView(String discuss_id) {
        this.discuss_id = discuss_id;
    }

    @Override
    protected RExBaseAdapter<UserDiscussListBean.DataListBean, CommentListBean.DataListBean, String> initRExBaseAdapter() {
        return new RExBaseAdapter<UserDiscussListBean.DataListBean, CommentListBean.DataListBean, String>(mActivity) {
            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TYPE_HEADER) {
                    return R.layout.item_search_user_item_layout;
                }
                return R.layout.item_comment_layout;
            }

            @Override
            protected void onBindHeaderView(RBaseViewHolder holder, int posInHeader, UserDiscussListBean.DataListBean headerBean) {
                super.onBindHeaderView(holder, posInHeader, headerBean);
                UserDiscussItemControl.initItem(mSubscriptions, holder, headerBean, null, null);
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, CommentListBean.DataListBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                holder.fillView(dataBean, true);
            }
        };
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString("动态详情")
                .setFloating(false)
                .setTitleHide(false)
                .setTitleBarBGColor(mActivity.getResources().getColor(com.angcyo.uiview.R.color.theme_color_primary));
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }

    @Override
    protected boolean hasScrollListener() {
        return false;
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
        Observable.zip(
                RRetrofit.create(SocialService.class)
                        .detail(Param.buildMap("discuss_id:" + discuss_id, "uid:" + UserCache.getUserAccount()))
                        .compose(Rx.transformer(UserDiscussListBean.DataListBean.class))
                        .asObservable(),
                RRetrofit.create(SocialService.class)
                        .commentList(Param.buildMap("page:" + page, "item_id:" + discuss_id, "type:discuss", "uid:" + UserCache.getUserAccount()))
                        .compose(Rx.transformer(CommentListBean.class))
                        .asObservable(),
                new Func2<UserDiscussListBean.DataListBean, CommentListBean, String>() {
                    @Override
                    public String call(UserDiscussListBean.DataListBean dataListBean, CommentListBean dataListBean2) {
                        showContentLayout();
                        if (dataListBean != null) {
                            List<UserDiscussListBean.DataListBean> headList = new ArrayList<>();
                            headList.add(dataListBean);
                            mRExBaseAdapter.resetHeaderData(headList);
                        }
                        if (dataListBean2 != null) {
                            mRExBaseAdapter.resetAllData(dataListBean2.getData_list());
                        }
                        return null;
                    }
                }
        )
                .compose(Rx.<String>transformer())
                .subscribe(new RSubscriber<String>() {
                    @Override
                    public void onEnd() {
                        super.onEnd();
                    }
                });
    }
}
