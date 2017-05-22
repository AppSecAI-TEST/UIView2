package com.hn.d.valley.sub.other;

import android.support.v7.widget.RecyclerView;

import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.sub.adapter.UserInfoAdapter;
import com.hn.d.valley.sub.adapter.UserInfoClickAdapter;

/**
 * 用户信息列表界面
 * Created by angcyo on 2017-01-15.
 */

public abstract class UserInfoRecyclerUIView extends SingleRecyclerUIView<LikeUserInfoBean> {

    protected UserInfoAdapter mUserInfoAdapter;

    @Override
    protected boolean hasDecoration() {
        return true;
    }

    @Override
    protected RExBaseAdapter<String, LikeUserInfoBean, String> initRExBaseAdapter() {
        mUserInfoAdapter = new UserInfoClickAdapter(mActivity, mOtherILayout, mSubscriptions) {

            @Override
            protected boolean isContact(LikeUserInfoBean dataBean) {
                return UserInfoRecyclerUIView.this.isContact(dataBean);
            }

            @Override
            protected boolean isAttention(LikeUserInfoBean dataBean) {
                return UserInfoRecyclerUIView.this.isAttention(dataBean);
            }

            @Override
            protected boolean onSetDataBean(LikeUserInfoBean dataBean, boolean value) {
                return UserInfoRecyclerUIView.this.onSetDataBean(dataBean, value);
            }

        };
        return mUserInfoAdapter;
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
     * 改变数据, 返回false , 采用默认处理方式
     */
    protected boolean onSetDataBean(final LikeUserInfoBean dataBean, boolean value) {
        return false;
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected RecyclerView.ItemDecoration initItemDecoration() {
        return super.createBaseItemDecoration()
                .setDividerSize(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_line))
                .setMarginStart(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi))
                .setDrawLastLine(true);
    }
}
