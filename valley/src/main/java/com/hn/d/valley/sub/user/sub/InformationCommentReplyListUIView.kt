package com.hn.d.valley.sub.user.sub

import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.angcyo.uiview.dialog.UIDialog
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.RSubscriber
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.rsen.PlaceholderView
import com.angcyo.uiview.rsen.RefreshLayout
import com.angcyo.uiview.utils.T_
import com.hn.d.valley.R
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.constant.Constant
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.CommentListBean
import com.hn.d.valley.cache.UserCache
import com.hn.d.valley.service.NewsService
import com.hn.d.valley.widget.HnExTextView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：资讯评论的回复列表
 * 创建人员：Robi
 * 创建时间：2017/07/05 14:00
 * 修改人员：Robi
 * 修改时间：2017/07/05 14:00
 * 修改备注：
 * Version: 1.0.0
 */
class InformationCommentReplyListUIView(val commentBean: CommentListBean.DataListBean /*评论对应的数据Bean*/)
    : CommentListUIView(commentBean.id, ListType.INFO_COMMENT_REPLY_TYPE) {

    override fun getTitleBar(): TitleBarPattern {
        return createTitleBarPattern()
                .setShowBackImageView(true)
                .setTitleString(mActivity, R.string.detail)
    }

    override fun isLoadInViewPager() = false

    override fun getDefaultLayoutState() = LayoutState.CONTENT

    override fun initOnShowContentLayout() {
        super.initOnShowContentLayout()
        mRExBaseAdapter.appendData(commentBean)

        mViewHolder.tv(R.id.tip_view).text = getHintString()
        mViewHolder.tv(R.id.tip_view).setOnClickListener {
            startIView(CommentInputDialog(object : CommentInputDialog.InputConfig {
                override fun onInitDialogLayout(viewHolder: RBaseViewHolder) {
                    viewHolder.tv(R.id.input_view).hint = getHintString()
                    viewHolder.v<View>(R.id.control_layout).visibility = View.GONE
                    viewHolder.v<View>(R.id.line_view).visibility = View.GONE
                }

                override fun onSendClick(imagePath: String, content: String) {
                    reply(content)
                }
            }))
        }
    }

    /**
     * 文本框提示文本
     */
    private fun getHintString(): String {
        return getHintString(commentBean.username)
    }

    private fun getHintString(userName: String): String {
        return getString(R.string.reply) + ":" + userName
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mRefreshLayout.setRefreshDirection(RefreshLayout.TOP)//禁用刷新
        mRefreshLayout.setTopView(PlaceholderView(mActivity))
        mRefreshLayout.setNotifyListener(false)
    }

    override fun getDataItemType(posInData: Int): Int {
        if (posInData == 0) {
            return 1
        }
        return super.getDataItemType(posInData)
    }

    override fun getItemLayoutId(viewType: Int): Int {
        if (viewType == 1) {
            return R.layout.item_reply_top_layout
        }
        return super.getItemLayoutId(viewType)
    }

    override fun inflateRecyclerRootLayout(baseContentLayout: RelativeLayout, inflater: LayoutInflater) {
        inflate(R.layout.view_reply_list_layout)
    }

    override fun initInfoItemLayout(holder: RBaseViewHolder, dataBean: CommentListBean.DataListBean, posInData: Int) {
        super.initInfoItemLayout(holder, dataBean, posInData)
        val hnExTextView = holder.v<HnExTextView>(R.id.content_ex_view)
        holder.v<View>(R.id.reply_cnt_view).visibility = View.GONE

        holder.itemView.isClickable = false

        if (posInData > 0) {
            hnExTextView.setMaxShowLine(3)
            holder.click(R.id.delete_view) {
                UIDialog.build()
                        .setDialogContent(getString(R.string.delete_reply_tip))
                        .setOkListener {
                            mRExBaseAdapter.deleteItem(dataBean)

                            commentBean.reply_cnt = (commentBean.reply_cnt.toInt() - 1).toString()
                            if (mOnCommentListener != null) {
                                mOnCommentListener.onComment()
                            }

                            add(RRetrofit.create(NewsService::class.java)
                                    .delete(Param.buildInfoMap("type:reply", "id:" + dataBean.id))
                                    .compose(Rx.transformer(String::class.java))
                                    .subscribe(object : RSubscriber<String>() {
                                        override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                                            super.onEnd(isError, isNoNetwork, e)
                                            if (isError) {
                                                T_.error(e?.msg)
                                            }
                                        }
                                    }))
                        }
                        .showDialog(mParentILayout)
            }
        } else {
            hnExTextView.setMaxShowLine(Int.MAX_VALUE)

            holder.click(R.id.delete_view) {
                UIDialog.build()
                        .setDialogContent(getString(R.string.delete_commend_tip))
                        .setOkListener {
                            finishIView()
                            add(RRetrofit.create(NewsService::class.java)
                                    .delete(Param.buildInfoMap("type:comment", "id:" + commentBean.id))
                                    .compose(Rx.transformer(String::class.java))
                                    .subscribe(object : RSubscriber<String>() {
                                    }))
                            if (mOnCommentListener != null) {
                                mOnCommentListener.onDeleteComment()
                            }
                        }
                        .showDialog(mParentILayout)
            }
        }
    }

    override fun onUILoadData(page: String) {
        super.onUILoadData(page)
        if (mListType == ListType.INFO_COMMENT_REPLY_TYPE) run {
            add(RRetrofit.create(NewsService::class.java)
                    .replylist(Param.buildInfoMap("lastid:" + last_id, "id:" + discuss_id, "uid:" + UserCache.getUserAccount(),
                            "type:comment", "amount:" + Constant.DEFAULT_PAGE_DATA_COUNT))
                    .compose(Rx.transformerList(CommentListBean.DataListBean::class.java))
                    .subscribe(object : BaseSingleSubscriber<MutableList<CommentListBean.DataListBean>>() {
                        override fun onSucceed(beans: MutableList<CommentListBean.DataListBean>) {
                            super.onSucceed(beans)
                            if (page.toInt() <= 1) {
                                beans.add(0, commentBean)
                            }
                            onUILoadDataEnd(beans)
                            if (beans.isNotEmpty()) {
                                last_id = beans[beans.size - 1].id
                            }
                        }
                    }))
        }
    }

    /**
     * 发布回复
     */
    private fun reply(content: String) {
        add(RRetrofit.create(NewsService::class.java)
                .reply(Param.buildInfoMap("type:comment", "content:$content", "id:${commentBean.id}", "uid:${UserCache.getUserAccount()}"))
                .compose(Rx.transformer(String::class.java))
                .subscribe(object : BaseSingleSubscriber<String>() {

                    override fun onSucceed(bean: String?) {
                        T_.show(getString(R.string.replay_success))
                        loadData()
                        scrollToTop()

                        commentBean.reply_cnt = (commentBean.reply_cnt.toInt() + 1).toString()
                        if (mOnCommentListener != null) {
                            mOnCommentListener.onComment()
                        }
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                        super.onEnd(isError, isNoNetwork, e)
                        hideLoadView()
                    }
                }))
    }


}