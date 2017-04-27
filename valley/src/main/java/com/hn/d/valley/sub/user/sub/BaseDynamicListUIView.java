package com.hn.d.valley.sub.user.sub;

import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

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
import com.angcyo.uiview.widget.RNineImageLayout;
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

    ListType mListType;

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
                if (mRExBaseAdapter.isLast(position)) {
                    outRect.bottom = 10 * getItemDecorationHeight();
                }
            }
        }));
    }

    @Override
    protected RExBaseAdapter<String, CommentListBean.DataListBean, String> initRExBaseAdapter() {
        return new RExBaseAdapter<String, CommentListBean.DataListBean, String>(mActivity) {
            @Override
            protected int getItemLayoutId(int viewType) {
                return R.layout.item_comment_layout2;
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, final CommentListBean.DataListBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                //头像
                HnGlideImageView glideImageView = holder.v(R.id.glide_image_view);
                glideImageView.setImageThumbUrl(dataBean.getAvatar());
                //glideImageView.setAuth(dataBean.getIs_auth());
                glideImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new UserDetailUIView2(dataBean.getUid()));
                    }
                });

                //名称, 性别
                holder.tv(R.id.user_name_view).setText(dataBean.getUsername());
                HnGenderView genderView = holder.v(R.id.gender_view);
                genderView.setGender(dataBean.getSex(), dataBean.getGrade());
                holder.v(R.id.hot_view).setVisibility(dataBean.isHot() ? View.VISIBLE : View.GONE);

                //时间, 删除
                if (mListType == ListType.COMMENT_TYPE) {
                    holder.tv(R.id.time_view).setText(dataBean.getShow_time());
                } else {
                    holder.tv(R.id.time_view).setText(TimeUtil.getTimeShowString(Long.valueOf(dataBean.getCreated()) * 1000l, true));
                }
                holder.v(R.id.delete_view).setVisibility(mListType == ListType.COMMENT_TYPE && isMe(dataBean.getUid()) ? View.VISIBLE : View.GONE);
                holder.v(R.id.delete_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItem(dataBean);
                        add(RRetrofit.create(SocialService.class)
                                .removeComment(Param.buildMap("type:comment",
                                        "item_id:" + dataBean.getComment_id()))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new RSubscriber<String>() {
                                    @Override
                                    public void onError(int code, String msg) {
                                        super.onError(code, msg);
                                        T_.error(msg);
                                    }
                                }));
                    }
                });

                //内容
                HnExTextView hnExTextView = holder.v(R.id.content_ex_view);
                hnExTextView.setMaxShowLine(3);
                hnExTextView.setOnImageSpanClick(UserDiscussItemControl.createSpanClick(mOtherILayout));
                hnExTextView.setText(dataBean.getContent());
                hnExTextView.setVisibility(TextUtils.isEmpty(dataBean.getContent()) ? View.GONE : View.VISIBLE);

                //点赞
                HnItemTextView itemTextView = holder.v(R.id.like_cnt);
                itemTextView.setBackgroundResource(R.drawable.base_bg2_selector);
                itemTextView.setText(dataBean.getLike_cnt());
                if (dataBean.getIs_like() == 1) {
                    itemTextView.setLeftIco(R.drawable.love_icon_2_s);
                } else {
                    itemTextView.setLeftIco(R.drawable.love_icon_2_n);
                }
                itemTextView.setVisibility(mListType == ListType.COMMENT_TYPE ? View.VISIBLE : View.GONE);
                UserDiscussItemControl.bindLikeItemView(mSubscriptions, holder, dataBean, "comment", null);

                //评论的图片处理
                View mediaControlLayout = holder.v(R.id.media_control_layout);
                if (!TextUtils.isEmpty(dataBean.getImages())) {
                    mediaControlLayout.setVisibility(View.VISIBLE);
                    UserDiscussItemControl.initMediaLayout("3",
                            RUtils.split(dataBean.getImages()),
                            mediaControlLayout,
                            ((RNineImageLayout) holder.v(R.id.media_image_view)),
                            (TextView) holder.v(R.id.video_time_view),
                            holder.v(R.id.video_play_view),
                            mOtherILayout
                    );
                } else {
                    mediaControlLayout.setVisibility(View.GONE);
                }

                //回复列表
                initReplyLayout(holder, dataBean);
            }

            /**回复列表布局*/
            private void initReplyLayout(RBaseViewHolder holder, CommentListBean.DataListBean dataBean) {
                View replyControlLayout = holder.v(R.id.reply_control_layout);
                if (mListType == ListType.FORWARD_TYPE) {
                    replyControlLayout.setVisibility(View.GONE);
                } else {
                    int reply_cnt = Integer.parseInt(dataBean.getReply_cnt());
                    List<CommentListBean.DataListBean.ReplyListBean> reply_list = dataBean.getReply_list();
                    if (reply_cnt < 1 || reply_list == null || reply_list.isEmpty()) {
                        replyControlLayout.setVisibility(View.GONE);
                    } else {
                        replyControlLayout.setVisibility(View.VISIBLE);

                        HnExTextView tv1 = holder.v(R.id.reply_text_view1);
                        if (reply_cnt > 0) {
                            tv1.setVisibility(View.VISIBLE);
                            CommentListBean.DataListBean.ReplyListBean replyListBean = reply_list.get(0);
                            tv1.setImageSpanTextColor(SkinHelper.getSkin().getThemeSubColor());
                            tv1.setOnImageSpanClick(UserDiscussItemControl.createSpanClick(mOtherILayout));
                            tv1.setImage(replyListBean.getImages());
                            tv1.setText(
                                    UserDiscussItemControl.createMention(replyListBean.getUid(), replyListBean.getUsername()) +
                                            ":" + replyListBean.getContent()
                            );
                        } else {
                            tv1.setVisibility(View.GONE);
                        }

                        HnExTextView tv2 = holder.v(R.id.reply_text_view2);
                        if (reply_cnt > 1) {
                            tv2.setVisibility(View.VISIBLE);
                            CommentListBean.DataListBean.ReplyListBean replyListBean = reply_list.get(1);
                            tv2.setImageSpanTextColor(SkinHelper.getSkin().getThemeSubColor());
                            tv2.setOnImageSpanClick(UserDiscussItemControl.createSpanClick(mOtherILayout));
                            tv2.setImage(replyListBean.getImages());
                            tv2.setText(UserDiscussItemControl.createMention(replyListBean.getUid(), replyListBean.getUsername()) +
                                    ":" + replyListBean.getContent());
                        } else {
                            tv2.setVisibility(View.GONE);
                        }

                        holder.tv(R.id.reply_count_view).setTextColor(SkinHelper.getSkin().getThemeSubColor());
                        holder.tv(R.id.reply_count_view).setText(reply_cnt + "");
                        holder.tv(R.id.reply_count_view).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                        replyControlLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                }
            }
        };
    }

    private boolean isMe(String to_uid) {
        return TextUtils.equals(to_uid, UserCache.getUserAccount());
    }

    public enum ListType {
        FORWARD_TYPE,//转发列表
        COMMENT_TYPE//评论列表
    }
}
