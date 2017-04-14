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
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.skin.SkinUtils;
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

    public static void initUserItem(RBaseViewHolder holder, final LikeUserInfoBean dataBean, final ILayout ILayout) {
        holder.fillView(dataBean);

        //用户个人详情
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ILayout.startIView(new UserDetailUIView2(dataBean.getUid()));
            }
        });

        //头像
        HnGlideImageView userIcoView = holder.v(R.id.image_view);
        userIcoView.setImageThumbUrl(dataBean.getAvatar());
        userIcoView.setAuth("1".equals(dataBean.getIs_auth()));

        //等级性别
        HnGenderView hnGenderView = holder.v(R.id.grade);
        hnGenderView.setGender(dataBean.getSex(), dataBean.getGrade());


    }

    @Override
    protected int getItemLayoutId(int viewType) {
//        return R.layout.item_near_user_layout;
        return R.layout.item_user_info_new;
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, final int posInData, final LikeUserInfoBean dataBean) {
        super.onBindDataView(holder, posInData, dataBean);
        initUserItem(holder, dataBean, mILayout);

        if (dataBean.getIs_contact() == 1) {
        } else {
        }

        //认证
        TextView signatureView = holder.v(R.id.signature);
        if ("1".equalsIgnoreCase(dataBean.getIs_auth())) {
            //holder.v(R.id.auth).setVisibility(View.VISIBLE);
            signatureView.setText(dataBean.getAuth_desc());
        } else {
            //holder.v(R.id.auth).setVisibility(View.GONE);
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
                    switch (SkinUtils.getSkin()) {
                        case SkinManagerUIView.SKIN_BLUE:
                            followView.setImageResource(R.drawable.follow_blue);
                            break;
                        case SkinManagerUIView.SKIN_GREEN:
                            followView.setImageResource(R.drawable.follow);
                            break;
                        default:
                            followView.setImageResource(R.drawable.follow_black);
                            break;
                    }
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

    /**
     * @param value true 关注, false 未关注
     */
    protected void onSetDataBean(final LikeUserInfoBean dataBean, boolean value) {
        //请根据界面设置对应关系值
        if (!value) {
            dataBean.setIs_contact(0);
        }
        dataBean.setIs_attention(value ? 1 : 0);
    }

}
