package com.hn.d.valley.sub.other;

import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.hn.d.valley.bean.UserDiscussListBean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/14 18:10
 * 修改人员：Robi
 * 修改时间：2017/01/14 18:10
 * 修改备注：
 * Version: 1.0.0
 */
public class LikeUserRecyclerUIView extends SingleRecyclerUIView<UserDiscussListBean.DataListBean.UserInfoBean> {
    @Override
    protected RExBaseAdapter<String, UserDiscussListBean.DataListBean.UserInfoBean, String> initRExBaseAdapter() {
        return new RExBaseAdapter<String, UserDiscussListBean.DataListBean.UserInfoBean, String>(mActivity) {
            @Override
            protected int getItemLayoutId(int viewType) {
                return 0;
            }
        };
    }
}
