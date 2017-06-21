package com.hn.d.valley.main.me.setting;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.ListModel;
import com.hn.d.valley.bean.UserRecommendBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.hn.d.valley.main.message.service.SearchService;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.sub.adapter.UserInfoClickAdapter;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/06/01 14:30
 * 修改人员：hewking
 * 修改时间：2017/06/1 14:30
 * 修改备注：
 * Version: 1.0.0
 */
public class UserRecommendUIView extends SingleRecyclerUIView<LikeUserInfoBean> {

    public UserRecommendUIView() {

    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(getString(R.string.text_user_recommend));
    }

    @Override
    protected RExBaseAdapter<String, LikeUserInfoBean, String> initRExBaseAdapter() {
        return new UserRecommAdapter(mActivity,mILayout,mSubscriptions);
    }

    @Override
    protected boolean hasDecoration() {
        return true;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);

        RRetrofit.create(SearchService.class, RRetrofit.CacheType.MAX_STALE)
                .userRecommend(Param.buildMap()).compose(Rx.transformerList(UserRecommendBean.class))
                .subscribe(new BaseSingleSubscriber<List<UserRecommendBean>>() {
                    @Override
                    public void onSucceed(List<UserRecommendBean> bean) {
                        super.onSucceed(bean);
                        if (bean == null || bean.size() == 0) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(UserRecomm2LikeUserInfo(bean));
                        }

                    }
                });
    }

    private List<LikeUserInfoBean> UserRecomm2LikeUserInfo(List<UserRecommendBean> datas) {
        List<LikeUserInfoBean> list = new ArrayList<>();
        for(UserRecommendBean bean : datas) {
            list.add(bean.convert());
        }
        return list;

    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }


    private class UserRecommAdapter extends UserInfoClickAdapter {

        public UserRecommAdapter(Context context, ILayout ILayout, CompositeSubscription subscription) {
            super(context, ILayout, subscription);

        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, LikeUserInfoBean dataBean) {
            holder.v(R.id.right_control_layout).setVisibility(View.GONE);
            super.onBindDataView(holder, posInData, dataBean);
        }

        @Override
        protected void onBindNormalView(RBaseViewHolder holder, int position, LikeUserInfoBean bean) {
            super.onBindNormalView(holder, position, bean);
            holder.v(R.id.right_control_layout).setVisibility(View.GONE);
        }

        @Override
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, LikeUserInfoBean bean) {
            super.onBindModelView(model, isSelector, holder, position, bean);
            holder.v(R.id.right_control_layout).setVisibility(View.GONE);
        }
    }
}
