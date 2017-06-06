package com.hn.d.valley.sub.user.sub;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.CommentListBean;
import com.hn.d.valley.service.SocialService;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：转发列表界面
 * 创建人员：Robi
 * 创建时间：2017/04/26 14:40
 * 修改人员：Robi
 * 修改时间：2017/04/26 14:40
 * 修改备注：
 * Version: 1.0.0
 */
public class ForwardListUIView extends BaseDynamicListUIView {

    String discuss_id;

    public ForwardListUIView(String discuss_id) {
        super(ListType.FORWARD_TYPE);
        this.discuss_id = discuss_id;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    protected String getEmptyTipString() {
        return getString(R.string.forward_view_empty_tip);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(SocialService.class)
                .forwardList(Param.buildMap("page:" + page, "discuss_id:" + discuss_id, "type:discuss"))
                .compose(Rx.transformer(CommentListBean.class))
                .subscribe(new BaseSingleSubscriber<CommentListBean>() {
                    @Override
                    public void onSucceed(CommentListBean bean) {
                        super.onSucceed(bean);
                        onUILoadDataEnd(bean.getData_list());
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                    }
                }));
    }
}
