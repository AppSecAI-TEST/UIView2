package com.hn.d.valley.main.home.nearby;

import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.NearUserInfo;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/02/27 10:22
 * 修改人员：Robi
 * 修改时间：2017/02/27 10:22
 * 修改备注：
 * Version: 1.0.0
 */
public class MapCardPagerAdapter extends PagerAdapter {

    List<NearUserInfo> mAllDatas;
    RBaseViewHolder mViewHolder;

    public MapCardPagerAdapter(List<NearUserInfo> allDatas) {
        mAllDatas = allDatas;
    }

    @Override
    public int getCount() {
        return mAllDatas.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View inflate = View.inflate(container.getContext(), R.layout.item_map_info_card_layout, null);
        mViewHolder = new RBaseViewHolder(inflate);
        bindView(position, mAllDatas.get(position));
        container.addView(inflate, new ViewGroup.LayoutParams(-1, -1));
        return inflate;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void resetDatas(List<NearUserInfo> allDatas) {
        mAllDatas = allDatas;
        notifyDataSetChanged();
    }

    /**
     * 绑定视图
     */
    private void bindView(int position, NearUserInfo userInfo) {
        //昵称
        TextView usernameView = mViewHolder.v(R.id.username);
        usernameView.setText(userInfo.getUsername());

        //头像
        HnGlideImageView userIcoView = mViewHolder.v(R.id.image_view);
        userIcoView.setImageThumbUrl(userInfo.getAvatar());

        //等级性别
        HnGenderView hnGenderView = mViewHolder.v(R.id.grade);
        hnGenderView.setGender(userInfo.getSex(), userInfo.getGrade());

        //认证
        TextView signatureView = mViewHolder.v(R.id.signature_view);
        if ("1".equalsIgnoreCase(userInfo.getIs_auth())) {
            mViewHolder.v(R.id.auth).setVisibility(View.VISIBLE);
            signatureView.setText(userInfo.getCompany() + userInfo.getJob());
        } else {
            mViewHolder.v(R.id.auth).setVisibility(View.GONE);
            String signature = userInfo.getSignature();
            if (TextUtils.isEmpty(signature)) {
                signatureView.setText(R.string.signature_empty_tip);
            } else {
                signatureView.setText(signature);
            }
        }

        View controlLayout = mViewHolder.v(R.id.time_control_layout);
        TextView timeView = mViewHolder.v(R.id.show_time);
        TextView distanceView = mViewHolder.v(R.id.show_distance);
        controlLayout.setVisibility(View.VISIBLE);
        timeView.setText(userInfo.getShow_time());
        distanceView.setText(userInfo.getShow_distance());

        //关注
        //bindFollowImageView(userInfo);
    }

//    private void bindFollowImageView(NearUserInfo userInfo) {
//        final HnFollowImageView followView = mViewHolder.v(R.id.follow_image_view);
//        if (userInfo.getIs_attention() == 1) {
//            followView.setImageResource(R.drawable.focus_on);
//        } else {
//            followView.setImageResource(R.drawable.follow);
//        }
//
//        followView.setLoadingModel(isSelector);
//    }

}
