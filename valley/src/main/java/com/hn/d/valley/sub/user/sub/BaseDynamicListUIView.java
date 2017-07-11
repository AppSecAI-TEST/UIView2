package com.hn.d.valley.sub.user.sub;

import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.utils.UI;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.CommentListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.widget.HnExTextView;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnItemTextView;

import java.util.List;

import static com.angcyo.uiview.recycler.adapter.RExBaseAdapter.TYPE_DATA;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/26 15:45
 * 修改人员：Robi
 * 修改时间：2017/04/26 15:45
 * 修改备注：
 * Version: 1.0.0
 */
public class BaseDynamicListUIView extends SingleRecyclerUIView<CommentListBean.DataListBean> {

    protected ListType mListType;

    OnCommentListener mOnCommentListener;


    public BaseDynamicListUIView(ListType listType) {
        mListType = listType;
    }

    @Override
    protected void initRefreshLayout() {
        super.initRefreshLayout();
        mRefreshLayout.setRefreshDirection(RefreshLayout.NONE);//禁用刷新
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        mRecyclerView.addItemDecoration(new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
            @Override
            public void getItemOffsets2(Rect outRect, int position, int edge) {
                super.getItemOffsets2(outRect, position, edge);
                if (position == 0) {
                } else {
                    outRect.top = getItemDecorationHeight();
                }
                if (!mRExBaseAdapter.isEnableLoadMore() && mRExBaseAdapter.isLast(position)) {
                    outRect.bottom = 10 * getItemDecorationHeight();
                }
            }
        }));
    }

    protected int getItemLayoutId(int viewType) {
        return R.layout.item_comment_layout2;
    }

    protected int getDataItemType(int posInData) {
        return TYPE_DATA;
    }

    protected void onBindDataView(RBaseViewHolder holder, int posInData, final CommentListBean.DataListBean dataBean) {
        if (mListType == ListType.INFO_COMMENT_TYPE || mListType == ListType.INFO_COMMENT_REPLY_TYPE) {
            //资讯评论列表
            initInfoItemLayout(holder, dataBean, posInData);
        } else {
            initItemLayout(holder, dataBean, mListType == ListType.COMMENT_TYPE ? "comment" : "", posInData);
        }
    }

    @Override
    protected RExBaseAdapter<String, CommentListBean.DataListBean, String> initRExBaseAdapter() {
        return new RExBaseAdapter<String, CommentListBean.DataListBean, String>(mActivity) {
            @Override
            protected int getItemLayoutId(int viewType) {
                return BaseDynamicListUIView.this.getItemLayoutId(viewType);
            }

            @Override
            protected int getDataItemType(int posInData) {
                return BaseDynamicListUIView.this.getDataItemType(posInData);
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, final CommentListBean.DataListBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                BaseDynamicListUIView.this.onBindDataView(holder, posInData, dataBean);
            }
        };
    }

    protected void initInfoItemLayout(RBaseViewHolder holder, final CommentListBean.DataListBean dataBean, int posInData) {

    }

    protected boolean isMe(String to_uid) {
        return TextUtils.equals(to_uid, UserCache.getUserAccount());
    }

    @Override
    protected void onLayoutStateChanged(LayoutState fromState, LayoutState toState) {
        super.onLayoutStateChanged(fromState, toState);
        if (toState == LayoutState.EMPTY) {
            //不显示图片
            mBaseEmptyLayout.findViewById(R.id.base_empty_image_view).setVisibility(View.GONE);
            ((TextView) mBaseEmptyLayout.findViewById(R.id.base_empty_tip_view)).setText(getEmptyTipString());
        }
    }

    @Override
    protected void inflateOverlayLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.layout_default_empty_pager);
    }

    @Override
    protected void onEmptyData(boolean isEmpty) {
        final View emptyRootLayout = mViewHolder.v(R.id.default_pager_root_layout);
        final TextView emptyTipView = mViewHolder.v(R.id.default_pager_tip_view);
        if (emptyRootLayout != null) {
            emptyRootLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            emptyTipView.setText(getEmptyTipString());
        }
    }

    protected void initItemLayout(RBaseViewHolder holder, final CommentListBean.DataListBean dataBean,
                                  String likeType, int position) {

        HnExTextView hnExTextView = holder.v(R.id.content_ex_view);

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

        View nameTimeControlLayout = holder.v(R.id.name_time_control_layout);
        int top;
        if (mListType == ListType.REPLY_TYPE && position == 0) {
            //回复列表的顶部头像放大处理
            int big = getDimensionPixelOffset(R.dimen.base_40dpi);
            UI.setView(glideImageView, big, big);
            top = getDimensionPixelOffset(R.dimen.base_xhdpi);
        } else {
            int big = getDimensionPixelOffset(R.dimen.base_xxxhdpi);
            UI.setView(glideImageView, big, big);
            top = 0;
        }
        nameTimeControlLayout.setPadding(nameTimeControlLayout.getPaddingLeft(), top,
                nameTimeControlLayout.getPaddingRight(), nameTimeControlLayout.getPaddingBottom());
        hnExTextView.setPadding(top, hnExTextView.getPaddingTop(),
                hnExTextView.getPaddingRight(), hnExTextView.getPaddingBottom());

        //名称, 性别
        holder.tv(R.id.user_name_view).setText(dataBean.getUsername());
        HnGenderView genderView = holder.v(R.id.gender_view);
        genderView.setGender(dataBean.getSex(), dataBean.getGrade());
        if (mListType == ListType.REPLY_TYPE) {
            holder.v(R.id.hot_view).setVisibility(View.GONE);
        } else {
            holder.v(R.id.hot_view).setVisibility(dataBean.isHot() ? View.VISIBLE : View.GONE);
        }

        //时间, 删除
        if (mListType == ListType.COMMENT_TYPE) {
            holder.tv(R.id.time_view).setText(dataBean.getShow_time());
        } else {
            holder.tv(R.id.time_view).setText(TimeUtil.getTimeShowString(Long.valueOf(dataBean.getCreated()) * 1000l, true));
        }
        View deleteView = holder.v(R.id.delete_view);
        deleteView.setVisibility(View.GONE);
        if (mListType == ListType.REPLY_TYPE) {
            if (position != 0 && isMe(dataBean.getUid())) {
                deleteView.setVisibility(View.VISIBLE);
            }
        } else if (mListType == ListType.COMMENT_TYPE) {
            if (isMe(dataBean.getUid())) {
                deleteView.setVisibility(View.VISIBLE);
            }
        }

        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIDialog.build()
                        .setDialogContent(mListType == ListType.REPLY_TYPE ?
                                getString(R.string.delete_reply_tip) :
                                getString(R.string.delete_commend_tip))
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
                                add(RRetrofit.create(SocialService.class)
                                        .removeComment(Param.buildMap("type:" + (mListType == ListType.REPLY_TYPE ? "reply" : "comment"),
                                                "item_id:" + (mListType == ListType.REPLY_TYPE ? dataBean.getReply_id() : dataBean.getComment_id())))
                                        .compose(Rx.transformer(String.class))
                                        .subscribe(new RSubscriber<String>() {
                                            @Override
                                            public void onError(int code, String msg) {
                                                super.onError(code, msg);
                                                T_.error(msg);
                                            }
                                        }));
                            }
                        })
                        .showDialog(mParentILayout);
            }
        });

        //内容
        if (mListType != ListType.REPLY_TYPE) {
            hnExTextView.setMaxShowLine(3);
        }
        hnExTextView.setOnImageSpanClick(UserDiscussItemControl.createSpanClick(mParentILayout));
        String content = dataBean.getContent();
        if (mListType == ListType.REPLY_TYPE && position > 0) {
            String reply = getString(R.string.reply);
            if ("1".equalsIgnoreCase(dataBean.getIs_first_level())) {
                content = reply +
                        UserDiscussItemControl.createMention(getUserId(), getUserName()) +
                        ":" + dataBean.getContent();
            } else {
                content = reply +
                        UserDiscussItemControl.createMention(dataBean.getTo_user_id(), dataBean.getTo_user_username()) +
                        ":" + dataBean.getContent();
            }
        }
        hnExTextView.setText(content);
        hnExTextView.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);

        //点赞
        HnItemTextView itemTextView = holder.v(R.id.like_cnt);
        itemTextView.setBackgroundResource(R.drawable.base_bg2_selector);
        itemTextView.setText(dataBean.getLike_cnt());
        if (dataBean.getIs_like() == 1) {
            itemTextView.setLeftIco(R.drawable.love_icon_s);
        } else {
            itemTextView.setLeftIco(R.drawable.love_icon_n);
        }
        itemTextView.setVisibility(mListType == ListType.FORWARD_TYPE ? View.GONE : View.VISIBLE);
        UserDiscussItemControl.bindLikeItemView(mSubscriptions, holder, dataBean, likeType, null, true);

        //评论的图片处理
        View mediaControlLayout = holder.v(R.id.media_control_layout);
        if (!TextUtils.isEmpty(dataBean.getImages())) {
            mediaControlLayout.setVisibility(View.VISIBLE);
            UserDiscussItemControl.initMediaLayout("3",
                    RUtils.split(dataBean.getImages()),
                    mediaControlLayout,
                    mParentILayout,
                    true, false, true
            );
        } else {
            mediaControlLayout.setVisibility(View.GONE);
        }

        //回复列表
        initReplyLayout(holder, dataBean);

        //
        if (mListType == ListType.COMMENT_TYPE) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mParentILayout.startIView(new ReplyListUIView(dataBean));
                }
            });
        }
    }

    /**
     * 回复列表布局
     */
    protected void initReplyLayout(RBaseViewHolder holder, final CommentListBean.DataListBean dataBean) {
        View replyControlLayout = holder.v(R.id.reply_control_layout);
        if (mListType == ListType.COMMENT_TYPE) {
            int reply_cnt = Integer.parseInt(dataBean.getReply_cnt());
            List<CommentListBean.DataListBean.ReplyListBean> reply_list = dataBean.getReply_list();
            if (reply_cnt < 1 || reply_list == null || reply_list.isEmpty()) {
                replyControlLayout.setVisibility(View.GONE);
            } else {
                replyControlLayout.setVisibility(View.VISIBLE);

                //第一条回复
                HnExTextView tv1 = holder.v(R.id.reply_text_view1);
                if (reply_cnt > 0) {
                    tv1.setVisibility(View.VISIBLE);
                    CommentListBean.DataListBean.ReplyListBean replyListBean = reply_list.get(0);
                    tv1.setImageSpanTextColor(SkinHelper.getSkin().getThemeSubColor());
                    tv1.setOnImageSpanClick(UserDiscussItemControl.createSpanClick(mParentILayout));
                    tv1.setImage(replyListBean.getImages());
                    tv1.setText(
                            UserDiscussItemControl.createMention(replyListBean.getUid(), replyListBean.getUsername()) +
                                    ":" + replyListBean.getContent()
                    );
                } else {
                    tv1.setVisibility(View.GONE);
                }

                //第二条回复
                HnExTextView tv2 = holder.v(R.id.reply_text_view2);
                if (reply_cnt > 1) {
                    tv2.setVisibility(View.VISIBLE);
                    CommentListBean.DataListBean.ReplyListBean replyListBean = reply_list.get(1);
                    tv2.setImageSpanTextColor(SkinHelper.getSkin().getThemeSubColor());
                    tv2.setOnImageSpanClick(UserDiscussItemControl.createSpanClick(mParentILayout));
                    tv2.setImage(replyListBean.getImages());
                    tv2.setText(UserDiscussItemControl.createMention(replyListBean.getUid(), replyListBean.getUsername()) +
                            ":" + replyListBean.getContent());
                } else {
                    tv2.setVisibility(View.GONE);
                }

                //第三条回复
                HnExTextView tv3 = holder.v(R.id.reply_text_view3);
                if (reply_cnt > 2) {
                    tv3.setVisibility(View.VISIBLE);
                    CommentListBean.DataListBean.ReplyListBean replyListBean = reply_list.get(2);
                    tv3.setImageSpanTextColor(SkinHelper.getSkin().getThemeSubColor());
                    tv3.setOnImageSpanClick(UserDiscussItemControl.createSpanClick(mParentILayout));
                    tv3.setImage(replyListBean.getImages());
                    tv3.setText(UserDiscussItemControl.createMention(replyListBean.getUid(), replyListBean.getUsername()) +
                            ":" + replyListBean.getContent());
                } else {
                    tv3.setVisibility(View.GONE);
                }

                holder.tv(R.id.reply_count_view).setTextColor(SkinHelper.getSkin().getThemeSubColor());
                holder.tv(R.id.reply_count_view).setText(reply_cnt + "");
                holder.tv(R.id.reply_count_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new ReplyListUIView(dataBean));
                    }
                });
                holder.tv(R.id.reply_count_view).setVisibility(reply_cnt > 3 ? View.VISIBLE : View.GONE);

            }
        } else {
            replyControlLayout.setVisibility(View.GONE);
        }
    }

    public void setOnCommentListener(OnCommentListener onCommentListener) {
        mOnCommentListener = onCommentListener;
    }

    /**
     * 发布成功调用
     */
    public void onComment() {
        if (mOnCommentListener != null) {
            mOnCommentListener.onComment();
        }
    }

    protected String getUserId() {
        return "";
    }

    protected String getUserName() {
        return "";
    }

    public enum ListType {
        FORWARD_TYPE,//转发列表
        COMMENT_TYPE,//评论列表
        INFO_COMMENT_TYPE,//资讯评论列表
        INFO_COMMENT_REPLY_TYPE,//资讯评论回复列表
        REPLY_TYPE//回复列表
    }


    public interface OnCommentListener {

        /**
         * 发布评论的回调
         */
        void onComment();

        /**
         * 删除评论的回调
         */
        void onDeleteComment();
    }
}
