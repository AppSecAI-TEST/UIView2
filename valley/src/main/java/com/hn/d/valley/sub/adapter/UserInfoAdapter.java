package com.hn.d.valley.sub.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.main.me.UserDetailUIView;
import com.hn.d.valley.widget.HnFollowImageView;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;

/**
 * Created by angcyo on 2017-01-15.
 */

public abstract class UserInfoAdapter extends RExBaseAdapter<String, LikeUserInfoBean, String> {

    protected ILayout mILayout;

    public UserInfoAdapter(Context context, ILayout ILayout) {
        super(context);
        mILayout = ILayout;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
//        return R.layout.item_near_user_layout;
        return R.layout.item_user_info_new;
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, final int posInData, final LikeUserInfoBean dataBean) {
        super.onBindDataView(holder, posInData, dataBean);
        holder.fillView(dataBean);

        //用户个人详情
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mILayout.startIView(new UserDetailUIView(dataBean.getUid()));
            }
        });

        //头像
        HnGlideImageView userIcoView = holder.v(R.id.image_view);
        userIcoView.setImageThumbUrl(dataBean.getAvatar());

        //等级性别
        HnGenderView hnGenderView = holder.v(R.id.grade);
        hnGenderView.setGender(dataBean.getSex(), dataBean.getGrade());

        if (dataBean.getIs_contact() == 1) {
        } else {
        }

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
    }

    @Override
    protected void onBindNormalView(RBaseViewHolder holder, int position, LikeUserInfoBean bean) {
        super.onBindNormalView(holder, position, bean);
    }

    @Override
    protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, final int position, LikeUserInfoBean bean) {
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

    //是否是联系人
    protected boolean isContact(final LikeUserInfoBean dataBean) {
        return dataBean.getIs_contact() == 1;
    }

    //是否已关注
    protected boolean isAttention(final LikeUserInfoBean dataBean) {
        return dataBean.getIs_attention() == 1;
    }

    protected void onSetDataBean(final LikeUserInfoBean dataBean, boolean value) {

    }

}
