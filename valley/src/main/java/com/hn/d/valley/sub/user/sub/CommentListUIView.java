package com.hn.d.valley.sub.user.sub;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.CommentListBean;
import com.hn.d.valley.service.SocialService;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：评论列表界面
 * 创建人员：Robi
 * 创建时间：2017/04/26 14:40
 * 修改人员：Robi
 * 修改时间：2017/04/26 14:40
 * 修改备注：
 * Version: 1.0.0
 */
public class CommentListUIView extends BaseDynamicListUIView {

    String discuss_id;

    public CommentListUIView(String discuss_id) {
        super(ListType.COMMENT_TYPE);
        this.discuss_id = discuss_id;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(SocialService.class)
                .commentList(Param.buildMap("page:" + page, "item_id:" + discuss_id, "type:discuss"))
                .compose(Rx.transformer(CommentListBean.class))
                .subscribe(new BaseSingleSubscriber<CommentListBean>() {
                    @Override
                    public void onSucceed(CommentListBean bean) {
                        super.onSucceed(bean);
                        if (bean != null && bean.getHot_list() != null && !bean.getHot_list().isEmpty()) {
                            List<CommentListBean.DataListBean> datas = new ArrayList<>();
                            for (CommentListBean.DataListBean b : bean.getHot_list()) {
                                b.setDiscuss_id(discuss_id);
                                b.setHot(true);
                            }
                            datas.addAll(bean.getHot_list());

                            if (bean.getData_list() != null) {
                                for (CommentListBean.DataListBean b : bean.getData_list()) {
                                    b.setDiscuss_id(discuss_id);
                                    if (!bean.getHot_list().contains(b)) {
                                        datas.add(b);
                                    }
                                }
                            }
                            onUILoadDataEnd(datas);
                        } else {
                            onUILoadDataEnd(bean.getData_list());
                        }
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                    }
                }));
    }
}
