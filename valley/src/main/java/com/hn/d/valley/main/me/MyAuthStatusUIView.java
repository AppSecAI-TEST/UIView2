package com.hn.d.valley.main.me;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.angcyo.uiview.base.Item;
import com.angcyo.uiview.base.SingleItem;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseItemUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.AuthDetailBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.AuthService;
import com.hn.d.valley.widget.HnAuthStatusView;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：认证状态界面/认证中/认证失败/等
 * 创建人员：Robi
 * 创建时间：2017/03/15 17:28
 * 修改人员：Robi
 * 修改时间：2017/03/15 17:28
 * 修改备注：
 * Version: 1.0.0
 */
public class MyAuthStatusUIView extends BaseItemUIView {

    AuthDetailBean mAuthDetailBean;
    int auth_type;

    public MyAuthStatusUIView(int auth_type) {
        this.auth_type = auth_type;
    }

    @Override
    protected int getTitleResource() {
        return R.string.my_auth_title;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }


    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        add(RRetrofit.create(AuthService.class)
                .detail(Param.buildMap())
                .compose(Rx.transformer(AuthDetailBean.class))
                .subscribe(new BaseSingleSubscriber<AuthDetailBean>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        showNonetLayout();
                    }

                    @Override
                    public void onSucceed(AuthDetailBean bean) {
                        super.onSucceed(bean);
                        mAuthDetailBean = bean;
                        showContentLayout();
                    }
                })
        );
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        getUITitleBarContainer().setTitleBarPattern(createTitleBar());
    }

    protected TitleBarPattern createTitleBar() {
        if (auth_type == 2) {
            return super.getTitleBar();
        }
        if (auth_type == 3) {
            return super.getTitleBar()
                    .addRightItem(TitleBarPattern.buildText("重新认证", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            replaceIView(new MyAuthUIView());
                        }
                    }));
        }
        return super.getTitleBar()
                .addRightItem(TitleBarPattern.buildText("修改认证", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replaceIView(new MyAuthUIView());
                    }
                }));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_auth_status_top;
        }
        return R.layout.item_single_text_view;
    }

    @Override
    protected void createItems(List<SingleItem> items) {
        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
        final String backUrl;
        List<String> split = RUtils.split(userInfoBean.getPhotos());
        if (split.isEmpty()) {
            backUrl = UserCache.getUserAvatar();
        } else {
            backUrl = split.get(0);
        }

        items.add(new SingleItem() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                HnGlideImageView icoView = holder.v(R.id.avatar_view);
                icoView.setImageThumbUrl(UserCache.getUserAvatar());

                HnAuthStatusView statusView = holder.v(R.id.status_view);
                HnGlideImageView backView = holder.v(R.id.image_view);
                backView.setImageUrl(backUrl);

                holder.tv(R.id.name_view).setText(mAuthDetailBean.getTrue_name());
                holder.tv(R.id.job_view).setText(mAuthDetailBean.getCompany() + mAuthDetailBean.getJob());

                statusView.setAuthType(auth_type);
            }
        });
        items.add(new SingleItem() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                RTextView textView = holder.v(R.id.text_view);
                textView.setText(R.string.introduction_tip2);
                textView.setCompoundDrawablePadding(mBaseOffsetSize);
                textView.setLeftIco(R.drawable.intro_icon);
            }
        });
        items.add(new SingleItem() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                RTextView textView = holder.v(R.id.text_view);
                textView.setText(mAuthDetailBean.getIntroduce());
                holder.itemView.setBackgroundColor(Color.WHITE);
            }
        });
    }
}
