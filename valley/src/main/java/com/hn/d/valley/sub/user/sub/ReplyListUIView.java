package com.hn.d.valley.sub.user.sub;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RMaxAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.CommentListBean;
import com.hn.d.valley.bean.realm.IcoInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.sub.other.LikeUserRecyclerUIView;
import com.hn.d.valley.widget.HnIcoRecyclerView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action0;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：回复列表界面
 * 创建人员：Robi
 * 创建时间：2017/04/28 09:02
 * 修改人员：Robi
 * 修改时间：2017/04/28 09:02
 * 修改备注：
 * Version: 1.0.0
 */
public class ReplyListUIView extends BaseDynamicListUIView {

    List<CommentListBean.DataListBean> like_users = new ArrayList<>();
    /**
     * 评论信息bean
     */
    private CommentListBean.DataListBean commendBean;

    public ReplyListUIView(CommentListBean.DataListBean commendBean) {
        super(ListType.REPLY_TYPE);
        this.commendBean = commendBean;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setShowBackImageView(true)
                .setTitleString(mActivity, R.string.detail);
    }

    @Override
    protected String getEmptyTipString() {
        return getString(R.string.reply_view_empty_tip);
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected void inflateRecyclerRootLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_reply_list_layout);
    }

    @Override
    protected void initRefreshLayout() {
        super.initRefreshLayout();
        mRefreshLayout.setRefreshDirection(RefreshLayout.TOP);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mViewHolder.tv(R.id.tip_view).setText(getHintString());
        mViewHolder.tv(R.id.tip_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new CommentInputDialog(new CommentInputDialog.InputConfig() {
                    @Override
                    public void onInitDialogLayout(RBaseViewHolder viewHolder) {
                        viewHolder.tv(R.id.input_view).setHint(getHintString());
                    }

                    @Override
                    public void onSendClick(String imagePath, String content) {
                        reply(imagePath, content, "");
                    }
                }));
            }
        });
    }

    /**
     * 文本框提示文本
     */
    private String getHintString() {
        return getHintString(commendBean.getUsername());
    }

    private String getHintString(String userName) {
        return getString(R.string.reply) + ":" + userName;
    }

    @Override
    protected RExBaseAdapter<String, CommentListBean.DataListBean, String> initRExBaseAdapter() {
        return new RExBaseAdapter<String, CommentListBean.DataListBean, String>(mActivity) {

            @Override
            protected int getDataItemType(int posInData) {
                if (posInData == 0) {
                    return TYPE_DATA + 1;
                }
                return super.getDataItemType(posInData);
            }

            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TYPE_DATA + 1) {
                    return R.layout.item_reply_top_layout;
                }
                return R.layout.item_comment_layout2;
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, final CommentListBean.DataListBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                if (posInData == 0) {
                    initReplyTopLayout(holder);
                } else {
                    initItemLayout(holder, dataBean, "reply", posInData);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mParentILayout.startIView(new CommentInputDialog(new CommentInputDialog.InputConfig() {
                                @Override
                                public void onInitDialogLayout(RBaseViewHolder viewHolder) {
                                    viewHolder.tv(R.id.input_view).setHint(getHintString(dataBean.getUsername()));
                                }

                                @Override
                                public void onSendClick(String imagePath, String content) {
                                    reply(imagePath, content, dataBean.getReply_id());
                                }
                            }));
                        }
                    });
                }
            }
        };
    }

    /**
     * 回复列表头部处理
     */
    private void initReplyTopLayout(RBaseViewHolder holder) {
        initItemLayout(holder, commendBean, "comment", 0);
        //点赞列表
        final View likeUserControlLayout = holder.v(R.id.like_users_layout);
        final TextView userCountView = holder.tv(R.id.like_user_count_view);
        final HnIcoRecyclerView mIcoRecyclerView = holder.v(R.id.like_user_recycler_view);

        userCountView.setText(commendBean.getLike_cnt());
        if (like_users != null && !like_users.isEmpty()) {
            likeUserControlLayout.setVisibility(View.VISIBLE);

            List<IcoInfoBean> infos = new ArrayList<>();
            for (CommentListBean.DataListBean infoBean : like_users) {
                infos.add(new IcoInfoBean(infoBean.getUid(), infoBean.getAvatar()));
            }
            mIcoRecyclerView.getMaxAdapter().resetData(infos);
        }

        UserDiscussItemControl.bindLikeItemView(mSubscriptions, holder, commendBean, "comment", new UserDiscussItemControl.InitLikeViewCallback() {
            @Override
            public void onLikeCall(boolean isLike) {
                if (mIcoRecyclerView != null) {
                    RMaxAdapter<IcoInfoBean> maxAdapter = mIcoRecyclerView.getMaxAdapter();
                    IcoInfoBean IcoInfoBean = new IcoInfoBean(UserCache.getUserAccount(),
                            UserCache.getUserAvatar());
                    int itemRawCount = Integer.parseInt(commendBean.getLike_cnt());
                    if (isLike) {
                        maxAdapter.addLastItem(IcoInfoBean);
                        itemRawCount++;
                        userCountView.setText(itemRawCount + "");
                    } else {
                        maxAdapter.deleteItem(IcoInfoBean);
                        itemRawCount--;
                        userCountView.setText(itemRawCount + "");
                    }

                    likeUserControlLayout.setVisibility(itemRawCount > 0 ? View.VISIBLE : View.GONE);
                }
            }
        }, true);

        //点赞人数列表
        holder.v(R.id.click_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new LikeUserRecyclerUIView(commendBean.getComment_id(), LikeUserRecyclerUIView.LikeType.TYPE_COMMENT));
            }
        });
    }

    @Override
    protected void onUILoadData(final String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(SocialService.class)
                .replyList(Param.buildMap("page:" + page, "comment_id:" + commendBean.getComment_id()))
                .compose(Rx.transformer(CommentListBean.class))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoadView();
                    }
                })
                .subscribe(new BaseSingleSubscriber<CommentListBean>() {
                    @Override
                    public void onSucceed(CommentListBean bean) {
                        super.onSucceed(bean);
                        if (bean != null && bean.getLike_users() != null) {
                            like_users.clear();
                            like_users.addAll(bean.getLike_users());
                        }

                        if ("1".equalsIgnoreCase(page)) {
                            bean.getData_list().add(0, new CommentListBean.DataListBean());
                        }

                        onUILoadDataEnd(bean.getData_list());
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                    }
                }));
    }

    @Override
    protected String getUserId() {
        return commendBean.getUid();
    }

    @Override
    protected String getUserName() {
        return commendBean.getUsername();
    }

    /**
     * 发布回复
     */
    private void reply(String imagePath, String content, String reply_id) {
        add(RRetrofit.create(SocialService.class)
                .reply(Param.buildMap("type:discuss", "comment_id:" + commendBean.getComment_id(),
                        "reply_id:" + reply_id,
                        "content:" + content, "images:" + imagePath))
                .compose(Rx.transformer(String.class))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoadView();
                    }
                })
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onSucceed(String bean) {
                        T_.show(bean);
                        loadData();
                        scrollToTop();
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                    }
                }));
    }

}

