package com.hn.d.valley.main.friend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.CustomMessageBean;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.MessageService;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.adapter.UserInfoClickAdapter;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.sub.other.UserInfoRecyclerUIView;
import com.hn.d.valley.sub.user.FriendsNewUIView;
import com.hn.d.valley.widget.HnFollowImageView;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewking on 2017/3/22.
 */
public class FriendNewUIView2 extends SingleRecyclerUIView<CustomMessageBean> {

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

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(MessageService.class)
                .list(Param.buildMap("uid:" + UserCache.getUserAccount(), "type:" + 5 , "page:" + page))
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
                            onUILoadDataEnd(bean.getData_list());
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }
                }));
    }

    public List<CustomMessageBean> onPreProvider() {
        List<CustomMessageBean> preBeans = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            preBeans.add(new CustomMessageBean());
        }
        return preBeans;
    }


    @Override
    protected RExBaseAdapter initRExBaseAdapter() {
        return new AddFriendAdpater(mActivity);
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

    private class FriendsNewAdapter extends RExBaseAdapter<String,CustomMessageBean,String> {

        private HnGlideImageView imageView;

        public FriendsNewAdapter(Context context) {
            super(context);
            setModel(MODEL_MULTI);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_user_info_new;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, final int posInData, final CustomMessageBean dataBean) {
            super.onBindDataView(holder, posInData, dataBean);

            TextView userName = holder.tv(R.id.username);
            userName.setText(dataBean.getBodyBean().getUsername());

            //用户个人详情
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOtherILayout.startIView(new UserDetailUIView2(dataBean.getBodyBean().getUid()));
                }
            });

            //头像
            HnGlideImageView userIcoView = holder.v(R.id.image_view);
            userIcoView.setImageThumbUrl(dataBean.getBodyBean().getAvatar());

            //等级性别
            HnGenderView hnGenderView = holder.v(R.id.grade);
            hnGenderView.setGender(dataBean.getBodyBean().getSex(), dataBean.getBodyBean().getGrade());

            if (dataBean.getIs_contact() == 1) {

            } else {

            }

            //认证
            TextView signatureView = holder.v(R.id.signature);
            if ("1".equalsIgnoreCase(dataBean.getBodyBean().getIs_auth())) {
                holder.v(R.id.auth_iview).setVisibility(View.VISIBLE);
                signatureView.setText(dataBean.getBodyBean().getCompany() + dataBean.getBodyBean().getJob());
            } else {
                holder.v(R.id.auth_iview).setVisibility(View.GONE);
                String signature = dataBean.getBodyBean().getSignature();
                if (TextUtils.isEmpty(signature)) {
                    signatureView.setText(R.string.signature_empty_tip);
                } else {
                    signatureView.setText(signature);
                }
            }

            //关注
            final ImageView followView = holder.v(R.id.follow_image_view);
            final String to_uid = dataBean.getBodyBean().getUid();

            followView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectorPosition(posInData);
                    add(RRetrofit.create(UserInfoService.class)
                            .attention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {

                                @Override
                                public void onSucceed(String bean) {
                                    dataBean.setIs_attention(1);
                                    setSelectorPosition(posInData);
                                }

                                @Override
                                public void onError(int code, String msg) {
                                    super.onError(code, msg);
                                    setSelectorPosition(posInData);
                                }
                            }));
                }
            });

            if (isContact(dataBean) || isAttention(dataBean)) {
                final String finalTitleString = getTitleString();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消关注
                        UIBottomItemDialog.build()
                                .setTitleString(finalTitleString)
                                .addItem(new UIItemDialog.ItemInfo(mActivity.getResources().getString(R.string.base_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setSelectorPosition(posInData);
                                        add(RRetrofit.create(UserInfoService.class)
                                                .unAttention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                                                .compose(Rx.transformer(String.class))
                                                .subscribe(new BaseSingleSubscriber<String>() {

                                                    @Override
                                                    public void onSucceed(String bean) {
                                                        //dataBean.setIs_attention(0);
                                                        onSetDataBean(dataBean, false);
                                                        setSelectorPosition(posInData);
                                                    }

                                                    @Override
                                                    public void onError(int code, String msg) {
                                                        super.onError(code, msg);
                                                        setSelectorPosition(posInData);
                                                    }
                                                }));
                                    }
                                })).showDialog(mOtherILayout);
                    }
                };
                followView.setOnClickListener(clickListener);
            }


        }

        @Override
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, CustomMessageBean bean) {
            super.onBindModelView(model, isSelector, holder, position, bean);
            final HnFollowImageView followView = holder.v(R.id.follow_image_view);
            followView.setLoadingModel(isSelector);
            if (isSelector) {
                followView.setOnClickListener(null);
            } else {
                //关注
                if (isContact(bean)) {
                    followView.setImageResource(R.drawable.huxiangguanzhu);
                } else {
                    if (isAttention(bean)) {
                        followView.setImageResource(R.drawable.focus_on);
                    } else {
                        followView.setImageResource(R.drawable.follow);
                    }
                }
            }
        }


        protected boolean isContact(CustomMessageBean dataBean) {
            return dataBean.getIs_contact() == 1;
        }

        protected boolean isAttention(CustomMessageBean dataBean) {
            return dataBean.getIs_attention() == 1;
        }

        protected void onSetDataBean(CustomMessageBean dataBean, boolean value) {
            if (!value) {
                dataBean.setIs_contact(0);
            }
            dataBean.setIs_attention(value ? 1 : 0);
        }
    }



    class AddFriendAdpater extends FriendsNewAdapter {

        static final int FUNC = 10001;
        static final int ADDRESSBOOK = 10002;

        public AddFriendAdpater(Context context) {
            super(context);
        }


        @Override
        protected int getItemLayoutId(int viewType) {
            switch (viewType) {
                case FUNC:
                    return R.layout.item_recent_search;
                case ADDRESSBOOK:
                    return R.layout.item_contact_addressbook;
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
            return super.getDataItemType(posInData);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, final int posInData, final CustomMessageBean dataBean) {

            if (FUNC == getDataItemType(posInData)) {
                TextView searchview = holder.tv(R.id.search_view);
                RelativeLayout layout_search_view = holder.v(R.id.layout_search_view);
                layout_search_view.setBackgroundResource(R.drawable.base_dark_round_selector_white);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new SearchUserUIView());
                    }
                });

            }
            else if (ADDRESSBOOK == getDataItemType(posInData)) {

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new AddressBookUI2View());
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
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, CustomMessageBean bean) {
            if (getItemType(position) == super.getDataItemType(position)) {
                super.onBindModelView(model, isSelector, holder, position, bean);
            }
        }

        @Override
        public void resetData(List<CustomMessageBean> datas) {
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
