package com.hn.d.valley.main.message.redpacket;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.hn.d.valley.main.wallet.MyWalletUIView;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.widget.HnEmptyRefreshLayout;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：抢红包结果
 * 创建人员：hewking
 * 创建时间：2017/04/24 16:45
 * 修改人员：hewking
 * 修改时间：2017/04/24 16:45
 * 修改备注：
 * Version: 1.0.0
 */
public class GrabedRDResultUIView extends SingleRecyclerUIView<GrabedRDDetail.ResultBean> {

    private GrabedRDDetail grabedRDDetail;

    private long red_id;

    private TextView tv_money;
    private TextView tv_red_to_wallet;

    public GrabedRDResultUIView(long redId) {
        this.red_id = redId;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleBarBGColor(ContextCompat.getColor(mActivity,R.color.base_red_d85940))
                .setTitleString(mActivity.getString(R.string.text_klg_redpacket));    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
    }

    protected void inflateRecyclerRootLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        baseContentLayout.setBackgroundResource(R.color.base_red_d85940);
        mRootSoftInputLayout = new RSoftInputLayout(mActivity);
        mRefreshLayout = new HnEmptyRefreshLayout(mActivity);
        mRecyclerView = new RRecyclerView(mActivity);
        mRecyclerView.setBackgroundResource(R.color.default_base_white);
        mRecyclerView.setHasFixedSize(true);
        mRefreshLayout.addView(mRecyclerView, new ViewGroup.LayoutParams(-1, -1));
        mRootSoftInputLayout.addView(mRefreshLayout, new ViewGroup.LayoutParams(-1, -1));
        baseContentLayout.addView(mRootSoftInputLayout, new ViewGroup.LayoutParams(-1, -1));

    }


        @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);

        add(RRetrofit.create(RedPacketService.class)
                .detail(Param.buildInfoMap("redid:" + red_id))
                .compose(Rx.transformRedPacket(GrabedRDDetail.class))
                .subscribe(new SingleRSubscriber<GrabedRDDetail>(this) {
                    @Override
                    protected void onResult(GrabedRDDetail bean) {
                        if (bean == null || bean.getResult().size() == 0) {
                            grabedRDDetail = bean;
                            onUILoadDataFinish();
                            mRExBaseAdapter.resetData(null);
                            mRExBaseAdapter.notifyDataSetChanged();
                        } else {
                            grabedRDDetail = bean;
                            onUILoadDataEnd(bean.getResult());
                        }
                    }

                }));
    }

    public List<GrabedRDDetail.ResultBean> onPreProvider() {
        List<GrabedRDDetail.ResultBean> preBeans = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            preBeans.add(new GrabedRDDetail.ResultBean());
        }
        return preBeans;
    }


    @Override
    protected RExBaseAdapter initRExBaseAdapter() {
        return new GrabedUserAdpater(mActivity);
    }



    class GrabedUserAdpater extends RExBaseAdapter<String,GrabedRDDetail.ResultBean,String> {

        static final int RPDETAIL = 10001;
        static final int ITEM_SECTION = 10002;

        public GrabedUserAdpater(Context context) {
            super(context);
        }


        @Override
        protected int getItemLayoutId(int viewType) {
            switch (viewType) {
                case RPDETAIL:
                    return R.layout.item_redpacket_grabed_result;
                case ITEM_SECTION:
                    return R.layout.item_single_text_view;
            }
            return R.layout.item_message_chat_search;
        }

        @Override
        protected int getDataItemType(int posInData) {
            if (posInData == 0) {
                return RPDETAIL;
            }
            if (posInData == 1) {
                return ITEM_SECTION;
            }
            return super.getDataItemType(posInData);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, final int posInData, final GrabedRDDetail.ResultBean dataBean) {
            if (grabedRDDetail == null) {
                return;
            }
            if (RPDETAIL == getDataItemType(posInData)) {
                HnGlideImageView iv_icon_head = holder.v(R.id.iv_icon_head);
                TextView tv_username = holder.v(R.id.tv_username);
                TextView tv_tip = holder.v(R.id.tv_tip);
                tv_money = holder.v(R.id.tv_money);
                tv_red_to_wallet = holder.tv(R.id.tv_red_to_wallet);

                if (grabedRDDetail == null) {
                    return;
                }

                iv_icon_head.setImageUrl(grabedRDDetail.getAvatar());
                tv_username.setText(grabedRDDetail.getUsername());
                tv_tip.setText(grabedRDDetail.getContent());

            }
            else if (ITEM_SECTION == getDataItemType(posInData)) {
                holder.itemView.setBackgroundResource(R.color.chat_bg_color);
                RTextView tv_left = holder.v(R.id.text_view);
                RTextView tv_right = holder.v(R.id.right_text_view);

                tv_left.setText(String.format(mContext.getString(R.string.text_grabed_count_per_all),grabedRDDetail.getResult().size(),grabedRDDetail.getNum()));
                tv_right.setText(String.format(mContext.getString(R.string.text_all_amout),grabedRDDetail.getMoney() / 100f));

            } else {
                holder.itemView.setBackgroundResource(R.color.default_base_white);
                HnGlideImageView iv_icon = holder.v(R.id.ico_view);
                TextView recent_name_view = holder.tv(R.id.recent_name_view);
                TextView msg_content_view = holder.tv(R.id.msg_content_view);
                TextView msg_time_view = holder.tv(R.id.msg_time_view);
                TextView tv_rp_desc = holder.tv(R.id.tv_rp_desc);

                iv_icon.setImageUrl(dataBean.getAvatar());
                recent_name_view.setText(dataBean.getUsername());
                msg_content_view.setText(TimeUtil.getTimeShowString(dataBean.getCreated() * 1000l,true));
                msg_time_view.setText(dataBean.getMoney() / 100f + "元");

                if (dataBean.getUid() == Integer.valueOf(UserCache.getUserAccount())) {
                    tv_red_to_wallet.setVisibility(View.VISIBLE);
                    tv_red_to_wallet.setMovementMethod(LinkMovementMethod.getInstance());
                    tv_red_to_wallet.setText(SpannableStringUtils.getBuilder(getString(R.string.text_already_save))
                            .append(getString(R.string.text_klg_wallet)).setClickSpan(new ClickableSpan() {
                                @Override
                                public void onClick(View widget) {
                                    startIView(new MyWalletUIView());
                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    super.updateDrawState(ds);
                                    ds.setColor(mActivity.getResources().getColor(R.color.blue_4777af));
                                    ds.clearShadowLayer();
                                }
                            })
                            .create());
                    tv_money.setText(SpannableStringUtils.getBuilder(dataBean.getMoney() / 100f + " ")
                            .append("元").setProportion(0.5f)
                            .create());
                }

                if (dataBean.getBest() == 1) {
                    tv_rp_desc.setText(R.string.text_lucky_best);
                    tv_rp_desc.setVisibility(View.VISIBLE);
                    tv_rp_desc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shouqizuijia_hongbao,0,0,0);
                }
            }
        }


        @Override
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, GrabedRDDetail.ResultBean bean) {
            if (getItemType(position) == super.getDataItemType(position)) {
                super.onBindModelView(model, isSelector, holder, position, bean);
            }
        }

        @Override
        public void resetData(List<GrabedRDDetail.ResultBean> datas) {
            mAllDatas.clear();
            mAllDatas.addAll(onPreProvider());
            if (datas == null) {
//                this.mAllDatas = new ArrayList<>();
                notifyItemRangeChanged(2, 0);
                return;
            } else {
                this.mAllDatas.addAll(datas);
            }
            notifyItemRangeChanged(2, datas.size());
        }
    }

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        RBaseItemDecoration itemDecoration = new RBaseItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                final RecyclerView.LayoutManager manager = parent.getLayoutManager();
                //线性布局
                final LinearLayoutManager layoutManager = (LinearLayoutManager) manager;
                final int firstItem = layoutManager.findFirstVisibleItemPosition();
                for (int i = 0; i < layoutManager.getChildCount(); i++) {
                    final View view = layoutManager.findViewByPosition(firstItem + i);
                    if (view != null) {
                        if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                            //水平
                            if (i > 1) {
                                drawDrawableH(c, view);
                            }
                        }
                    }
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();//布局管理器
                final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
                final int viewLayoutPosition = layoutParams.getViewLayoutPosition();//布局时当前View的位置
                //线性布局 就简单了
                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) layoutManager);
                if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                    //垂直方向
                    outRect.set(0, 0, 0, (int) mDividerSize);
                }
            }
        };
        return itemDecoration.setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi));
    }
}
