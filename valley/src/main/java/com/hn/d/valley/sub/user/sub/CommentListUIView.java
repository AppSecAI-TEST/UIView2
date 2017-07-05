package com.hn.d.valley.sub.user.sub;

import android.text.TextUtils;
import android.view.View;

import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.CommentListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.main.found.sub.InformationDetailUIView;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.service.NewsService;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.widget.HnExTextView;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnItemTextView;

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
        this(discuss_id, false);
    }

    /**
     * @param isInfo 是否是资讯评论列表
     */
    public CommentListUIView(String discuss_id, boolean isInfo) {
        super(isInfo ? ListType.INFO_COMMENT_TYPE : ListType.COMMENT_TYPE);
        this.discuss_id = discuss_id;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    protected String getEmptyTipString() {
        return getString(R.string.comment_view_empty_tip);
    }

    @Override
    protected void initInfoItemLayout(RBaseViewHolder holder, final CommentListBean.DataListBean dataBean, int posInData) {
        //内容
        HnExTextView hnExTextView = holder.v(R.id.content_ex_view);
        hnExTextView.setMaxShowLine(3);
        hnExTextView.setOnImageSpanClick(UserDiscussItemControl.createSpanClick(mParentILayout));
        String content = dataBean.getContent();
        hnExTextView.setText(content);
        hnExTextView.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);

        //头像
        HnGlideImageView glideImageView = holder.v(R.id.glide_image_view);
        glideImageView.setImageThumbUrl(dataBean.getAvatar());
        //glideImageView.setAuth(dataBean.getIs_auth());
        glideImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentILayout.startIView(new UserDetailUIView2(dataBean.getUid()));
            }
        });

        //名称, 性别
        holder.tv(R.id.user_name_view).setText(dataBean.getUsername());
        HnGenderView genderView = holder.v(R.id.gender_view);
        genderView.setGender(dataBean.getSex(), dataBean.getGrade());

        //热评
        holder.v(R.id.hot_view).setVisibility(dataBean.isHot() ? View.VISIBLE : View.GONE);

        //时间, 删除
        holder.tv(R.id.time_view).setText(TimeUtil.getTimeShowString(Long.valueOf(dataBean.getCreated()) * 1000l, true));
        View deleteView = holder.v(R.id.delete_view);
        deleteView.setVisibility(isMe(dataBean.getUid()) ? View.VISIBLE : View.GONE);

        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIDialog.build()
                        .setDialogContent(getString(R.string.delete_commend_tip))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mRExBaseAdapter.deleteItem(dataBean);
                                if (mOnCommentListener != null) {
                                    mOnCommentListener.onDeleteComment();
                                }
                                if (mRExBaseAdapter.isItemEmpty()) {
                                    onUILoadDataEnd(null);
                                }
                                add(RRetrofit.create(NewsService.class)
                                        .delete(Param.buildInfoMap("type:comment", "id:" + dataBean.getId()))
                                        .compose(Rx.transformer(String.class))
                                        .subscribe(new RSubscriber<String>() {
                                            @Override
                                            public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                                                super.onEnd(isError, isNoNetwork, e);
                                                if (isError) {
                                                    T_.error(e.getMsg());
                                                }
                                            }
                                        }));
                            }
                        })
                        .showDialog(mParentILayout);
            }
        });

        //点赞
        HnItemTextView itemTextView = holder.v(R.id.like_cnt);
        itemTextView.setBackgroundResource(R.drawable.base_bg2_selector);
        itemTextView.setText(dataBean.getLike_cnt());
        if (dataBean.getIs_like() == 1) {
            itemTextView.setLeftIco(R.drawable.love_icon_s);
        } else {
            itemTextView.setLeftIco(R.drawable.love_icon_n);
        }
        itemTextView.setVisibility(View.VISIBLE);

        InformationDetailUIView.Companion.initLikeView(mSubscriptions, holder, "comment", dataBean);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        if (mListType == ListType.INFO_COMMENT_TYPE) {
            add(RRetrofit.create(NewsService.class)
                    .replylist(Param.buildInfoMap("lastid:" + last_id, "id:" + discuss_id, "uid:" + UserCache.getUserAccount(),
                            "type:new", "amount:" + Constant.DEFAULT_PAGE_DATA_COUNT))
                    .compose(Rx.transformerList(CommentListBean.DataListBean.class))
                    .subscribe(new BaseSingleSubscriber<List<CommentListBean.DataListBean>>() {
                        @Override
                        public void onSucceed(List<CommentListBean.DataListBean> beans) {
                            super.onSucceed(beans);
                            onUILoadDataEnd(beans);
                            if (beans.size() > 0) {
                                last_id = beans.get(beans.size() - 1).getId();
                            }
                        }
                    }));
        } else if (mListType == ListType.COMMENT_TYPE) {
            add(RRetrofit.create(SocialService.class)
                    .commentList(Param.buildMap("page:" + page, "item_id:" + discuss_id, "type:discuss"))
                    .compose(Rx.transformer(CommentListBean.class))
                    .subscribe(new BaseSingleSubscriber<CommentListBean>() {
                        @Override
                        public void onSucceed(CommentListBean bean) {
                            super.onSucceed(bean);
                            if (bean == null) {
                                onUILoadDataEnd(null);
                            } else {
                                if (bean.getHot_list() != null && !bean.getHot_list().isEmpty()) {
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
                        }
                    }));
        }
    }
}
