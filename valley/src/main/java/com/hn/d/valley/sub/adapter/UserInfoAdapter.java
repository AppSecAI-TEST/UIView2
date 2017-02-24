package com.hn.d.valley.sub.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;

/**
 * Created by angcyo on 2017-01-15.
 */

public class UserInfoAdapter extends RExBaseAdapter<String, LikeUserInfoBean, String> {

    private ILayout mILayout;

    public UserInfoAdapter(Context context) {
        super(context);
//        mILayout = ILayout;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
//        return R.layout.item_near_user_layout;
        return R.layout.item_user_info_new;
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, int posInData, final LikeUserInfoBean dataBean) {
        super.onBindDataView(holder, posInData, dataBean);
        holder.fillView(dataBean);

        //头像
        HnGlideImageView userIcoView = holder.v(R.id.image_view);
        userIcoView.setImageThumbUrl(dataBean.getAvatar());

        //等级性别
        HnGenderView hnGenderView = holder.v(R.id.grade);
        hnGenderView.setGender(dataBean.getSex(), dataBean.getGrade());

        if (dataBean.getIs_contact() == 1) {
        } else {
        }

        //关注
        ImageView followView = holder.v(R.id.follow_image_view);
        if (dataBean.getIs_attention() == 1) {
            followView.setImageResource(R.drawable.focus_on);
        } else {
            followView.setImageResource(R.drawable.follow);
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
}
