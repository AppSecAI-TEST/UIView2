package com.hn.d.valley.main.home.recommend;

import android.view.View;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.angcyo.uiview.utils.RUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.main.home.NoTitleBaseRecyclerUIView;
import com.hn.d.valley.main.home.UserDiscussAdapter;
import com.hn.d.valley.sub.user.service.UserInfoService;
import com.hn.d.valley.utils.PhotoPager;

import java.util.List;

import rx.functions.Action0;

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
public class RecommendUIView extends NoTitleBaseRecyclerUIView<UserDiscussListBean.DataListBean> {

    /**
     * 需要过滤的tag
     */
    private Tag filterTag;

    public void setFilterTag(Tag filterTag) {
        this.filterTag = filterTag;
    }

    @Override
    protected RExBaseAdapter<String, UserDiscussListBean.DataListBean, String> initRExBaseAdapter() {
        return new UserDiscussAdapter(mActivity) {
            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, UserDiscussListBean.DataListBean tBean) {
                //super.onBindDataView(holder, posInData, tBean);
                UserDiscussItemControl.initItem(mSubscriptions, holder, tBean, new Action0() {
                    @Override
                    public void call() {
                        loadData();
                    }
                });

                final SimpleDraweeView mediaImageType = holder.v(R.id.media_image_view);
                final List<String> medias = RUtils.split(tBean.getMedia());
                if ("3".equalsIgnoreCase(tBean.getMedia_type())) {
                    mediaImageType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImagePagerUIView.start(mOtherILayout, v, PhotoPager.getImageItems(medias), 0);
                        }
                    });
                }

            }
        };
    }

    @Override
    protected void onUILoadData(String page) {
        add(RRetrofit.create(UserInfoService.class)
                .discussList(Param.buildMap("uid:" + UserCache.getUserAccount(),
                        "type:" + 2, "page:" + page, "tag:" + getFilterTagId()))
                .compose(Rx.transformer(UserDiscussListBean.class))
                .subscribe(new BaseSingleSubscriber<UserDiscussListBean>() {

                    @Override
                    public void onNext(UserDiscussListBean userDiscussListBean) {
                        showContentLayout();
                        onUILoadDataEnd(userDiscussListBean.getData_list(), userDiscussListBean.getData_count());
                    }

                    @Override
                    protected void onEnd() {
                        onUILoadDataFinish();
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
