package com.hn.d.valley.main.friend;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.sub.adapter.UserInfoClickAdapter;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.sub.other.UserInfoRecyclerUIView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewking on 2017/3/22.
 */
public class AddFriendUIView extends UserInfoRecyclerUIView {

    public AddFriendUIView() {

    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity, R.string.new_friend);
    }

    @Override
    protected void initOnShowContentLayout() {
        getRecyclerView().setBackgroundResource(R.color.default_base_bg_dark2);
        super.initOnShowContentLayout();
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(ContactService.class)
                .recommendUser(Param.buildMap("page:" + page))
                .compose(Rx.transformerList(LikeUserInfoBean.class))
                .subscribe(new SingleRSubscriber<List<LikeUserInfoBean>>(this) {
                    @Override
                    protected void onResult(List<LikeUserInfoBean> bean) {
                        if (bean == null || bean.isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            for (LikeUserInfoBean b : bean) {
                                b.setIs_attention(1);
                            }
                            onUILoadDataEnd(bean);
                        }
                    }
                }));
    }

    public List<LikeUserInfoBean> onPreProvider() {
        List<LikeUserInfoBean> preBeans = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            preBeans.add(new LikeUserInfoBean());
        }
        return preBeans;
    }


    @Override
    protected RExBaseAdapter initRExBaseAdapter() {
        return new AddFriendAdpater();
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
                            if (i > 2) {
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
                    if (viewLayoutPosition == 1) {
                        //这里可以决定,第2个item的分割线
                        outRect.set(0, 0, 0, mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi));
                    } else {
                        outRect.set(0, 0, 0, (int) mDividerSize);
                    }
                }
            }
        };
        return itemDecoration.setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi));

    }

    class AddFriendAdpater extends UserInfoClickAdapter {

        static final int FUNC = 10001;
        static final int ADDRESSBOOK = 10002;
        static final int QRCODE = 10003;

        public AddFriendAdpater() {
            super(mActivity, mParentILayout, mSubscriptions);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            switch (viewType) {
                case FUNC:
                    return R.layout.item_recent_search;
                case ADDRESSBOOK:
                    return R.layout.item_contact_addressbook;
//                case QRCODE:
//                    return R.layout.item_contact_qrcode;
            }
            return R.layout.item_contact_info_new;
        }

        @Override
        protected int getDataItemType(int posInData) {
            if (posInData == 0) {
                return FUNC;
            }

//            if (posInData == 1) {
//                return QRCODE;
//            }

            if (posInData == 1) {
                return ADDRESSBOOK;
            }
            return super.getDataItemType(posInData);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, final int posInData, final LikeUserInfoBean dataBean) {

            if (FUNC == getDataItemType(posInData)) {
                TextView searchview = holder.tv(R.id.search_view);
                RelativeLayout layout_search_view = holder.v(R.id.layout_search_view);
                layout_search_view.setBackgroundResource(R.drawable.base_dark_round_selector_white);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new SearchUserUIView());
                    }
                });

            }
//            else if (QRCODE == getDataItemType(posInData)) {
//
//                TextView tv_uid = holder.tv(R.id.tv_qrcode_desc);
//                tv_uid.setText(String.format("我的id : %s", UserCache.getUserAccount()));
//
//               holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mParentILayout.startIView(new MyQrCodeUIView());
//                    }
//                });
//
//            }
            else if (ADDRESSBOOK == getDataItemType(posInData)) {

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new AddressBookUI2View());
                    }
                });

                TextView tv_username = holder.tv(R.id.tv_username);
                ImageView imgv = holder.imgV(R.id.image_view);

                tv_username.setText(R.string.text_context);
                imgv.setImageResource(R.drawable.address_book_2);

            } else {
                super.onBindDataView(holder, posInData, dataBean);
//                holder.itemView.setBackgroundResource(R.color.white);
            }

        }


        @Override
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, LikeUserInfoBean bean) {
            if (getItemType(position) == super.getDataItemType(position)) {
                super.onBindModelView(model, isSelector, holder, position, bean);
            }
        }

        @Override
        public void resetData(List<LikeUserInfoBean> datas) {
            mAllDatas.clear();
            mAllDatas.addAll(onPreProvider());
            if (datas == null) {
//                this.mAllDatas = new ArrayList<>();
            } else {
                this.mAllDatas.addAll(datas);
            }
            notifyItemRangeChanged(2, datas.size());
        }
    }
}
