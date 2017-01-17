package com.hn.d.valley.sub.other;

import android.view.View;
import android.widget.ImageView;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView;
import com.hn.d.valley.sub.user.service.ContactService;
import com.hn.d.valley.sub.user.service.UserInfoService;

/**
 * 我的关注
 * Created by angcyo on 2017-01-15.
 */

public class FollowersRecyclerUIView extends SingleRecyclerUIView<LikeUserInfoBean> {

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
    protected RExBaseAdapter<String, LikeUserInfoBean, String> initRExBaseAdapter() {
        return new UserCardAdapter(mActivity, mILayout) {
            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, final LikeUserInfoBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                ImageView commandView = holder.v(R.id.command_item_view);
                if (dataBean.getIs_contact() != 1) {
                    commandView.setVisibility(View.VISIBLE);

                    commandView.setImageResource(R.drawable.add_contacts_n);
                    commandView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            add(RRetrofit.create(UserInfoService.class)
                                    .addContact(Param.buildMap("to_uid:" + dataBean.getUid(),
                                            "tip:" + mActivity.getResources().getString(R.string.add_contact_tip,
                                                    UserCache.instance().getUserInfoBean().getUsername())))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {

                                        @Override
                                        public void onNext(String bean) {
                                            T_.show(bean);
                                        }
                                    }));
                        }
                    });
                }

                holder.v(R.id.card_root_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new UserDetailUIView(dataBean.getUid()));
                    }
                });
            }
        };
    }

    @Override
    protected String getTitleString() {
        if (uid.equalsIgnoreCase(UserCache.getUserAccount())) {
            return mActivity.getString(R.string.followers_titile);
        } else {
            return mActivity.getString(R.string.ta_followers_titile);
        }
    }

    @Override
    protected boolean hasDecoration() {
        return false;
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
                            onUILoadDataEnd(bean.getData_list(), bean.getData_count());
                        }
                    }
                }));
    }
}
