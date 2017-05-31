package com.hn.d.valley.sub.user;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.CustomMessageBean;
import com.hn.d.valley.bean.ListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.service.MessageService;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.widget.HnFollowImageView;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;

/**
 * Created by hewking on 2017/3/17.
 */
public class FriendsNewUIView extends SingleRecyclerUIView<CustomMessageBean>{

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    protected RExBaseAdapter<String, CustomMessageBean, String> initRExBaseAdapter() {
        return new FriendsNewAdapter(mActivity);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);

        add(RRetrofit.create(MessageService.class)
                .list(Param.buildMap("uid:" + UserCache.getUserAccount(), "type:" + 5 , "page:" + page))
                .compose(Rx.transformer(CustomMessageModel.class))
                .subscribe(new SingleRSubscriber<CustomMessageModel>(this) {
                    @Override
                    protected void onResult(CustomMessageModel bean) {
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

    public static class CustomMessageModel extends ListModel<CustomMessageBean> {

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
                    mParentILayout.startIView(new UserDetailUIView2(dataBean.getBodyBean().getUid()));
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
                    add(RRetrofit.create(UserService.class)
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
                                        add(RRetrofit.create(UserService.class)
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
                                })).showDialog(mParentILayout);
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
    }

    protected void onSetDataBean(CustomMessageBean dataBean, boolean value) {
        if (!value) {
            dataBean.setIs_contact(0);
        }
        dataBean.setIs_attention(value ? 1 : 0);
    }

    @Override
    protected RecyclerView.ItemDecoration initItemDecoration() {
        return super.createBaseItemDecoration()
                .setDividerSize(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_line))
                .setMarginStart(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi))
                .setDrawLastLine(true);
    }

}
