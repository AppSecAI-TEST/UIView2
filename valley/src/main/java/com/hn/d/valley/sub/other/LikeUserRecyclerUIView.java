package com.hn.d.valley.sub.other;

import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.LikeUserModel;
import com.hn.d.valley.service.SocialService;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：点赞用户列表界面, 动态点赞人数/评论点赞人数
 * 创建人员：Robi
 * 创建时间：2017/01/14 18:10
 * 修改人员：Robi
 * 修改时间：2017/01/14 18:10
 * 修改备注：
 * Version: 1.0.0
 */
public class LikeUserRecyclerUIView extends UserInfoRecyclerUIView {

    LikeType mLikeType = LikeType.TYPE_DISCUSS;
    private String id;

    public LikeUserRecyclerUIView(String id) {
        this.id = id;
    }

    public LikeUserRecyclerUIView(String id, LikeType likeType) {
        this.id = id;
        mLikeType = likeType;
    }

    @Override
    protected String getTitleString() {
        return mActivity.getString(R.string.like_title);
    }

//    @Override
//    protected void onEmptyData(boolean isEmpty) {
//        super.onEmptyData(isEmpty);
//        if (isEmpty) {
//            setTitleString(getTitleString());
//        } else {
//            setTitleString(String.format(Locale.CHINA,
//                    mActivity.getString(R.string.like_title_formater),
//                    mRExBaseAdapter.getRawItemCount()));
//        }
//    }

    @Override
    protected RExBaseAdapter<String, LikeUserInfoBean, String> initRExBaseAdapter() {
        super.initRExBaseAdapter();
        return mUserInfoAdapter.setShowFollowView(false);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(SocialService.class)
                .likeList(Param.buildMap("type:" + (mLikeType == LikeType.TYPE_DISCUSS ? "discuss" : "comment"),
                        "item_id:" + id, "page:" + page))
                .compose(Rx.transformer(LikeUserModel.class))
                .subscribe(new BaseSingleSubscriber<LikeUserModel>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onSucceed(LikeUserModel bean) {
                        super.onSucceed(bean);
                        if (bean == null || bean.getData_list() == null || bean.getData_list().isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(bean.getData_list());
                        }
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        hideLoadView();
                    }
                }));

//        add(RRetrofit.create(SocialService.class)
//                .likeList(Param.buildMap("type:discuss", "item_id:" + id, "page:" + page))
//                .compose(Rx.transformer(LikeUserModel.class))
//                .subscribe(new SingleRSubscriber<LikeUserModel>(this) {
//                    @Override
//                    protected void onResult(LikeUserModel bean) {
//                        if (bean == null || bean.getData_list() == null || bean.getData_list().isEmpty()) {
//                            onUILoadDataEnd();
//                        } else {
//                            onUILoadDataEnd(bean.getData_list(), bean.getData_count());
//                        }
//                    }
//                }));
    }

    public enum LikeType {
        TYPE_DISCUSS,//动态点赞
        TYPE_COMMENT//评论点赞
    }
}
