package com.hn.d.valley.sub.user;

import android.text.TextUtils;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：我的动态/个人动态界面
 * 创建人员：Robi
 * 创建时间：2017/02/28 10:28
 * 修改人员：Robi
 * 修改时间：2017/02/28 10:28
 * 修改备注：
 * Version: 1.0.0
 */
@Deprecated
public class MyStatusDetailUIView extends SingleRecyclerUIView<UserDiscussListBean.DataListBean> {

    String targetUid;

    public MyStatusDetailUIView() {
        this(UserCache.getUserAccount());
    }

    public MyStatusDetailUIView(String targetUid) {
        this.targetUid = targetUid;
    }

    boolean isMe() {
        return TextUtils.equals(targetUid, UserCache.getUserAccount());
    }

    @Override
    protected int getTitleResource() {
        return isMe() ? R.string.my_status : R.string.ta_status;
    }

    @Override
    protected RExBaseAdapter<String, UserDiscussListBean.DataListBean, String> initRExBaseAdapter() {
        return new RExBaseAdapter<String, UserDiscussListBean.DataListBean, String>(mActivity) {

            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TYPE_HEADER) {
                    return R.layout.item_status_header_layout;
                }
                return R.layout.item_status_view_layout;
            }

            @Override
            protected void onBindHeaderView(RBaseViewHolder holder, int posInHeader, String headerBean) {
                super.onBindHeaderView(holder, posInHeader, headerBean);
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, UserDiscussListBean.DataListBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
            }
        };
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(UserInfoService.class)
                .discussList(Param.buildMap("page:" + page, "to_uid:" + targetUid))
                .compose(Rx.transformer(UserDiscussListBean.class))
                .subscribe(new BaseSingleSubscriber<UserDiscussListBean>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onSucceed(UserDiscussListBean userDiscussListBean) {
                        if (userDiscussListBean == null) {
                            onUILoadDataEnd();
                            return;
                        }
                        List<UserDiscussListBean.DataListBean> data_list = userDiscussListBean.getData_list();
                        if (data_list != null && data_list.size() > 0) {
                            UserDiscussListBean.DataListBean lastBean = data_list.get(data_list.size() - 1);
                        }
                        onUILoadDataEnd(data_list);
                    }

                    @Override
                    public void onEnd() {
                        hideLoadView();
                        onUILoadDataFinish();
                    }
                }));
    }
}
