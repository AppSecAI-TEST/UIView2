package com.hn.d.valley.main.friend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.Json;
import com.angcyo.uiview.utils.ScreenUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.CustomMessageBean;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.MessageService;
import com.hn.d.valley.sub.adapter.UserInfoClickAdapter;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.sub.user.FriendsNewUIView;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by hewking on 2017/3/22.
 */
public class FriendNewUIView2 extends SingleRecyclerUIView<LikeUserInfoBean> {

    public FriendNewUIView2() {

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

    private boolean isEmpty;

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(MessageService.class)
                .list(Param.buildMap("uid:" + UserCache.getUserAccount(), "type:" + 5, "page:" + page))
                .compose(Rx.transformer(FriendsNewUIView.CustomMessageModel.class))
                .subscribe(new SingleRSubscriber<FriendsNewUIView.CustomMessageModel>(this) {
                    @Override
                    protected void onResult(FriendsNewUIView.CustomMessageModel bean) {
                        if (bean == null || bean.getData_list().size() == 0) {
                            onUILoadDataEnd();
                        } else {
                            for (CustomMessageBean message : bean.getData_list()) {
                                message.setBodyBean(Json.from(message.getBody(), CustomMessageBean.BodyBean.class));
                            }
                            onUILoadDataEnd(CustomMessage2LikeUserInfo(bean.getData_list()));
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

    private List<LikeUserInfoBean> CustomMessage2LikeUserInfo(List<CustomMessageBean> datas) {
        List<LikeUserInfoBean> list = new ArrayList<>();
        for (CustomMessageBean bean : datas) {
            LikeUserInfoBean convert = bean.convert();
            CustomMessageBean.BodyBean bodyBean = bean.getBodyBean();
            bodyBean.setMessage_id(bean.getMessage_id());
            convert.setBodyBean(bodyBean);
            list.add(convert);
        }
        return list;

    }


    @Override
    protected RExBaseAdapter initRExBaseAdapter() {
        return new AddFriendAdpater(mActivity, mILayout, mSubscriptions);
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
        static final int EMPTY = 10003;

        public AddFriendAdpater(Context context, ILayout ILayout, CompositeSubscription subscription) {
            super(context, ILayout, subscription);
        }


        @Override
        protected int getItemLayoutId(int viewType) {
            switch (viewType) {
                case FUNC:
                    return R.layout.item_recent_search;
                case ADDRESSBOOK:
                    return R.layout.item_contact_addressbook;
                case EMPTY:
                    return R.layout.empty_layout;
            }
            return R.layout.item_contact_info_new;
        }

        @Override
        protected int getDataItemType(int posInData) {
            if (posInData == 0) {
                return FUNC;
            }
            if (posInData == 1) {
                return ADDRESSBOOK;
            }

            if (posInData == 2 && isEmpty) {
                return EMPTY;
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

            } else if (ADDRESSBOOK == getDataItemType(posInData)) {
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

            } else if (EMPTY == getDataItemType((posInData))){
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.v(R.id.base_empty_root_layout).getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.topMargin = ScreenUtil.dip2px(20);
                holder.v(R.id.base_empty_root_layout).setLayoutParams(params);
                holder.imgV(R.id.base_empty_image_view).setImageResource(R.drawable.image_wufensi);
                holder.tv(R.id.base_empty_tip_view).setText("当前没有新的好友请求");
            }else {
                super.onBindDataView(holder, posInData, dataBean);
//                holder.itemView.setBackgroundResource(R.color.white);
                CustomMessageBean.BodyBean bodyBean = dataBean.getBodyBean();
                holder.tv(R.id.signature).setVisibility(View.VISIBLE);
                holder.tv(R.id.signature).setText(bodyBean.getMsg());

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        UIBottomItemDialog.build()
                                .addItem(getString(R.string.delete_text), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String message_id = dataBean.getBodyBean().getMessage_id();
                                        deleteItem(posInData);

                                        add(RRetrofit.create(MessageService.class)
                                                .remove(Param.buildMap("type:5", "message_id:" + message_id))
                                                .compose(Rx.transformer(String.class))
                                                .subscribe(new BaseSingleSubscriber<String>() {
                                                }));
                                    }
                                })
                                .showDialog(mParentILayout);
                        return true;
                    }
                });
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
            if (datas == null || datas.size() == 0) {
//                this.mAllDatas = new ArrayList<>();
                isEmpty = true;
                mAllDatas.add(new LikeUserInfoBean());
            } else {
                this.mAllDatas.addAll(datas);
            }
            notifyItemRangeChanged(2, datas.size());
        }
    }
}
