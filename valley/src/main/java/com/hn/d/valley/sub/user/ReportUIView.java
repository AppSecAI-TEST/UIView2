package com.hn.d.valley.sub.user;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.service.SocialService;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/02/13 15:24
 * 修改人员：Robi
 * 修改时间：2017/02/13 15:24
 * 修改备注：
 * Version: 1.0.0
 */
public class ReportUIView extends BaseContentUIView {

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_report);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(mActivity.getString(R.string.report))
                .setShowBackImageView(true);
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        add(RRetrofit.create(SocialService.class)
                .getReportReason(Param.buildMap("type:discuss"))
                .compose(Rx.transformerList(Tag.class))
                .subscribe(new RSubscriber<List<Tag>>() {
                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                        showNonetLayout(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onViewLoad();
                            }
                        });
                    }

                    @Override
                    public void onSucceed(List<Tag> bean) {
                        super.onSucceed(bean);
//                        ViewGroupUtils.addViews();
                    }
                }));
    }
}
