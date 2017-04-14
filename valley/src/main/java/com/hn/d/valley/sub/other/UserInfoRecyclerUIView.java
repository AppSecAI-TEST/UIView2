package com.hn.d.valley.sub.other;

import com.angcyo.uiview.recycler.RBaseItemDecoration;
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

    @Override
    protected boolean hasDecoration() {
        return true;
    }

    @Override
    protected RExBaseAdapter<String, LikeUserInfoBean, String> initRExBaseAdapter() {
        UserInfoAdapter userInfoAdapter = new UserInfoClickAdapter(mActivity, mOtherILayout, mSubscriptions) {

            @Override
            protected boolean isContact(LikeUserInfoBean dataBean) {
                return UserInfoRecyclerUIView.this.isContact(dataBean);
            }

            @Override
            protected boolean isAttention(LikeUserInfoBean dataBean) {
                return UserInfoRecyclerUIView.this.isAttention(dataBean);
            }

            @Override
            protected void onSetDataBean(LikeUserInfoBean dataBean, boolean value) {
                UserInfoRecyclerUIView.this.onSetDataBean(dataBean, value);
            }

        };
        return userInfoAdapter;
    }

    //是否是联系人
    protected boolean isContact(final LikeUserInfoBean dataBean) {
        return false;
    }

    //是否已关注
    protected boolean isAttention(final LikeUserInfoBean dataBean) {
        return false;
    }

    //改变数据
    protected void onSetDataBean(final LikeUserInfoBean dataBean, boolean value) {

    }

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        return super.initItemDecoration()
                .setDividerSize(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_line))
                .setMarginStart(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi))
                .setDrawLastLine(true);
    }
}
