package com.hn.d.valley.main.found.sub;

import android.support.annotation.NonNull;
import android.view.View;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.HotInfoListBean;
import com.hn.d.valley.service.NewsService;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/03/23 11:06
 * 修改人员：Robi
 * 修改时间：2017/03/23 11:06
 * 修改备注：
 * Version: 1.0.0
 */
public class HotInfoListUIView extends BaseRecyclerUIView<String, HotInfoListBean, String> {

    /**
     * 分类, 名称
     */
    String classify;

    /**
     * 最小id, 用来分页
     */
    String lastId = "";

    public HotInfoListUIView(String classify) {
        this.classify = classify;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    protected boolean hasScrollListener() {
        return false;
    }

    @Override
    protected int getItemDecorationHeight() {
        return mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
    }

    @Override
    protected boolean isLoadInViewPager() {
        return true;
    }

    @Override
    protected RExBaseAdapter<String, HotInfoListBean, String> initRExBaseAdapter() {
        RExBaseAdapter<String, HotInfoListBean, String> baseAdapter = new RExBaseAdapter<String, HotInfoListBean, String>(mActivity) {

            @Override
            protected int getDataItemType(int posInData) {

                return super.getDataItemType(posInData);
            }

            @Override
            protected int getItemLayoutId(int viewType) {
                return R.layout.item_article_layout;
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, HotInfoListBean dataBean) {
                holder.fillView(dataBean);
                holder.tv(R.id.time_view).setText(TimeUtil.getTimeShowString(dataBean.getDate() * 1000, true));

                HnGlideImageView imageView = holder.v(R.id.image_view);
                imageView.setImageUrl(dataBean.getLogo());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        baseAdapter.setEnableLoadMore(true);
        return baseAdapter;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit
                .create(NewsService.class)
                .abstract_(Param.buildInfoMap("classify:" + classify, "random:0", "amount:" + Constant.DEFAULT_PAGE_DATA_COUNT, "lastid:" + lastId))
                .compose(Rx.transformerList(HotInfoListBean.class))
                .subscribe(new BaseSingleSubscriber<List<HotInfoListBean>>() {

                    @Override
                    public void onSucceed(List<HotInfoListBean> bean) {
                        if (bean == null || bean.isEmpty()) {

                        } else {
                            lastId = String.valueOf(bean.get(bean.size() - 1).getId());
                            onUILoadDataEnd(bean);
                        }
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        onUILoadDataFinish();
                    }
                })
        );
    }

}
