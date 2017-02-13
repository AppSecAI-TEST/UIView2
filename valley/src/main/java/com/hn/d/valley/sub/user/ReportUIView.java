package com.hn.d.valley.sub.user;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.github.all.base.adapter.ViewGroupUtils;
import com.angcyo.uiview.github.all.base.adapter.adapter.cache.BaseCacheAdapter;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.RCheckGroup;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.widget.HnLoading;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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

    @BindView(R.id.content_layout)
    RCheckGroup mContentLayout;

    UserDiscussListBean.DataListBean mDataBean;

    public ReportUIView(UserDiscussListBean.DataListBean dataBean) {
        mDataBean = dataBean;
    }

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
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                    }

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
                        showContentLayout();
                        ViewGroupUtils.addViews(mContentLayout, new BaseCacheAdapter<Tag>(mActivity, bean) {
                            @Override
                            public View getView(ViewGroup parent, int pos, Tag data) {
                                final View inflate = mInflater.inflate(R.layout.item_report, parent, false);
                                ((TextView) inflate.findViewById(R.id.text_view)).setText(data.getContent());
                                inflate.setTag(data);
                                return inflate;
                            }
                        });
                    }
                }));
    }

    /**
     * 举报
     */
    @OnClick(R.id.submit_view)
    public void onSubmitClick() {
        final View checkView = mContentLayout.getCheckView();
        if (checkView == null) {
            T_.show(mActivity.getString(R.string.no_selector_report_tip));
        } else {
            Tag tag = (Tag) checkView.getTag();
            add(RRetrofit.create(SocialService.class)
                    .report(Param.buildMap("type:discuss", "item_id:" + mDataBean.getDiscuss_id(), "reason_id:" + tag.getId()))
                    .compose(Rx.transformer(String.class)).subscribe(new RSubscriber<String>() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            HnLoading.show(mOtherILayout, false);
                        }

                        @Override
                        public void onSucceed(String bean) {
                            super.onSucceed(bean);
                            T_.show(bean);
                            finishIView();
                        }

                        @Override
                        public void onEnd() {
                            super.onEnd();
                            HnLoading.hide();
                        }
                    }));
        }
    }
}
