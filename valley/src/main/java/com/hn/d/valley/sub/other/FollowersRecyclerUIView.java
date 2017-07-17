package com.hn.d.valley.sub.other;

import android.view.View;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.ContactService;

/**
 * Created by hewking on 2017/3/16.
 */

public class FollowersRecyclerUIView extends UserInfoRecyclerUIView {

    /**
     * 默认是自己
     */
    String uid;

    public FollowersRecyclerUIView() {
        this.uid = UserCache.getUserAccount();
    }

    public FollowersRecyclerUIView(String uid) {
        this.uid = uid;
    }

    @Override
    protected String getTitleString() {
        if (isMe()) {
            return mActivity.getString(R.string.followers_title);
        } else {
            return mActivity.getString(R.string.ta_followers_title);
        }
    }

    private boolean isMe() {
        return uid.equalsIgnoreCase(UserCache.getUserAccount());
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(ContactService.class)
                .followers(Param.buildMap("uid:" + uid, "page:" + page))
                .compose(Rx.transformer(UserListModel.class))
                .subscribe(new SingleRSubscriber<UserListModel>(this) {
                    @Override
                    protected void onResult(UserListModel bean) {
                        if (bean == null || bean.getData_list() == null || bean.getData_list().isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            for (LikeUserInfoBean b : bean.getData_list()) {
                                b.setIs_attention(1);
                            }
                            onUILoadDataEnd(bean.getData_list());
                        }
                    }
                }));
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, int posInData, LikeUserInfoBean dataBean) {
        super.onBindDataView(holder, posInData, dataBean);
        holder.v(R.id.follow_image_view).setVisibility(isMe() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected boolean isContact(LikeUserInfoBean dataBean) {
        return dataBean.getIs_contact() == 1;
    }

    @Override
    protected boolean isAttention(LikeUserInfoBean dataBean) {
        return dataBean.getIs_attention() == 1;
    }

    @Override
    protected boolean onSetDataBean(LikeUserInfoBean dataBean, boolean value) {
        if (!value) {
            dataBean.setIs_contact(0);
        }
        dataBean.setIs_attention(value ? 1 : 0);
        return true;
    }
}
