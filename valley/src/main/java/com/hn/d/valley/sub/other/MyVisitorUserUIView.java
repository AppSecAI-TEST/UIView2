package com.hn.d.valley.sub.other;

import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserListModel;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.adapter.UserInfoAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：我的访客
 * 创建人员：Robi
 * 创建时间：2017/02/24 18:07
 * 修改人员：Robi
 * 修改时间：2017/02/24 18:07
 * 修改备注：
 * Version: 1.0.0
 */
@Deprecated
public class MyVisitorUserUIView extends UserInfoRecyclerUIView {

    public static DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    @Override
    protected RExBaseAdapter<String, LikeUserInfoBean, String> initRExBaseAdapter() {
        return new UserInfoAdapter(mActivity, mParentILayout) {
            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, LikeUserInfoBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                holder.v(R.id.follow_image_view).setVisibility(View.GONE);
                TextView timeView = holder.v(R.id.time_text_view);
                timeView.setText(TIME_FORMAT.format(new Date()));
            }
        };
    }

    @Override
    protected int getTitleResource() {
        return R.string.my_visitor_title;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(UserInfoService.class)
                .visitorList(Param.buildMap("page:" + page))
                .compose(Rx.transformer(UserListModel.class))
                .subscribe(new SingleRSubscriber<UserListModel>(this) {
                    @Override
                    protected void onResult(UserListModel bean) {
                        if (bean == null || bean.getData_list() == null || bean.getData_list().isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(bean.getData_list());
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
