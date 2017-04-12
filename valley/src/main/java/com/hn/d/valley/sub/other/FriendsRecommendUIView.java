package com.hn.d.valley.sub.other;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.RecommendUserBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.widget.HnFollowImageView;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.List;


public class FriendsRecommendUIView extends SingleRecyclerUIView<RecommendUserBean> {

    boolean isInSubUIView = true;
    private RecommendFriendAdapter mAdapter;

    public FriendsRecommendUIView(boolean isInSubUIView) {
        this.isInSubUIView = isInSubUIView;
    }

    public FriendsRecommendUIView(String uid) {
        this(true);
    }


    @Override
    protected TitleBarPattern getTitleBar() {
        if (isInSubUIView) {
            return null;
        } else {
            return super.getTitleBar().setTitleString(mActivity, R.string.frient_tip);
        }
    }

    @Override
    protected RExBaseAdapter<String, RecommendUserBean, String> initRExBaseAdapter() {
        mAdapter = new RecommendFriendAdapter(mActivity, this, mOtherILayout);
        mAdapter.setModel(RModelAdapter.MODEL_MULTI);
        return mAdapter;
    }


    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(ContactService.class)
                .recommendUser(Param.buildMap("page:" + page))
                .compose(Rx.transformerList(RecommendUserBean.class))
                .subscribe(new SingleRSubscriber<List<RecommendUserBean>>(this) {
                    @Override
                    protected void onResult(List<RecommendUserBean> bean) {
                        if (bean == null || bean.isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            for (RecommendUserBean b : bean) {
//                                b.setIs_attention(1);
                            }
                            onUILoadDataEnd(bean);
                        }
                    }
                }));
    }

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        return super.initItemDecoration()
                .setDividerSize(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_line))
                .setMarginStart(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi))
                .setDrawLastLine(true);
    }

    public static class RecommendFriendAdapter extends RExBaseAdapter<String, RecommendUserBean, String> {

        private UIBaseRxView mSubscriptions;

        private ILayout mLayout;

        public RecommendFriendAdapter(Context context, UIBaseRxView mSubscriptions, ILayout layout) {
            super(context);
            this.mSubscriptions = mSubscriptions;
            this.mLayout = layout;
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_user_info_new;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, final int posInData, final RecommendUserBean dataBean) {
            super.onBindDataView(holder, posInData, dataBean);
            holder.fillView(dataBean);


            //用户个人详情
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLayout.startIView(new UserDetailUIView2(dataBean.getUid()));
                }
            });

            //头像
            HnGlideImageView userIcoView = holder.v(R.id.image_view);
            userIcoView.setImageThumbUrl(dataBean.getAvatar());
            userIcoView.setAuth(dataBean.getIs_auth());

            //等级性别
            HnGenderView hnGenderView = holder.v(R.id.grade);
            hnGenderView.setGender(dataBean.getSex(), dataBean.getGrade());

            //认证
            TextView signatureView = holder.v(R.id.signature);
            if ("1".equalsIgnoreCase(dataBean.getIs_auth())) {
                holder.v(R.id.auth).setVisibility(View.VISIBLE);
                signatureView.setText(dataBean.getCompany() + dataBean.getJob());
            } else {
                holder.v(R.id.auth).setVisibility(View.GONE);
                String signature = dataBean.getSignature();
                if (TextUtils.isEmpty(signature)) {
                    signatureView.setText(R.string.signature_empty_tip);
                } else {
                    signatureView.setText(signature);
                }
            }

            //关注
            final ImageView followView = holder.v(R.id.follow_image_view);
            final String to_uid = dataBean.getUid();

            View.OnClickListener clickListener = null;
            String titleString = "";
            if (isContact(dataBean)) {
                titleString = mContext.getString(R.string.unattention_tip2);
            } else {
                if (isAttention(dataBean)) {
                    titleString = mContext.getString(R.string.unattention_tip1);
                } else {
                    //未关注
                    clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setSelectorPosition(posInData);
                            mSubscriptions.add(RRetrofit.create(UserInfoService.class)
                                    .attention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {

                                        @Override
                                        public void onSucceed(String bean) {
                                            dataBean.setIs_attention(1);
                                            onSetDataBean(dataBean, true);
                                            setSelectorPosition(posInData);
                                        }

                                        @Override
                                        public void onError(int code, String msg) {
                                            super.onError(code, msg);
                                            setSelectorPosition(posInData);
                                        }
                                    }));
                        }
                    };
                }
            }

            if (isContact(dataBean) || isAttention(dataBean)) {
                final String finalTitleString = titleString;
                clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消关注
                        UIBottomItemDialog.build()
                                .setTitleString(finalTitleString)
                                .addItem(new UIItemDialog.ItemInfo(mContext.getResources().getString(R.string.base_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setSelectorPosition(posInData);
                                        mSubscriptions.add(RRetrofit.create(UserInfoService.class)
                                                .unAttention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                                                .compose(Rx.transformer(String.class))
                                                .subscribe(new BaseSingleSubscriber<String>() {

                                                    @Override
                                                    public void onSucceed(String bean) {
                                                        dataBean.setIs_attention(0);
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
                                })).showDialog(mLayout);
                    }
                };
            }

            followView.setOnClickListener(clickListener);
        }

        @Override
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, RecommendUserBean bean) {
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


        protected boolean isContact(RecommendUserBean dataBean) {
            return dataBean.getIs_contact() == 1;
        }

        protected boolean isAttention(RecommendUserBean dataBean) {
            return dataBean.getIs_attention() == 1;
        }

        protected void onSetDataBean(RecommendUserBean dataBean, boolean value) {
            if (!value) {
                dataBean.setIs_contact(0);
            }
            dataBean.setIs_attention(value ? 1 : 0);
        }
    }
}
