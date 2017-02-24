package com.hn.d.valley.sub.other;

import android.view.View;
import android.widget.ImageView;

import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.angcyo.uiview.recycler.RModelAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.adapter.UserInfoAdapter;

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
//        return new UserCardAdapter(mActivity, mILayout) {
//            @Override
//            protected void onBindDataView(RBaseViewHolder holder, int posInData, final LikeUserInfoBean dataBean) {
//                super.onBindDataView(holder, posInData, dataBean);
//                ImageView commandView = holder.v(R.id.command_item_view);
//                if (dataBean.getIs_contact() != 1) {
//                    commandView.setVisibility(View.VISIBLE);
//
//                    commandView.setImageResource(R.drawable.add_contacts_n);
//                    commandView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(final View v) {
//                            add(RRetrofit.create(UserInfoService.class)
//                                    .addContact(Param.buildMap("to_uid:" + dataBean.getUid(),
//                                            "tip:" + mActivity.getResources().getString(R.string.add_contact_tip,
//                                                    UserCache.instance().getUserInfoBean().getUsername())))
//                                    .compose(Rx.transformer(String.class))
//                                    .subscribe(new BaseSingleSubscriber<String>() {
//
//                                        @Override
//                                        public void onSucceed(String bean) {
//                                            T_.show(bean);
//                                        }
//                                    }));
//                        }
//                    });
//                }
//
//                holder.v(R.id.card_root_layout).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startIView(new UserDetailUIView(dataBean.getUid()));
//                    }
//                });
//            }
//        };
        UserInfoAdapter userInfoAdapter = new UserInfoAdapter(mActivity, mOtherILayout) {
            @Override
            protected void onBindDataView(RBaseViewHolder holder, final int posInData, final LikeUserInfoBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                //关注
                final ImageView followView = holder.v(R.id.follow_image_view);
                final String to_uid = dataBean.getUid();

                View.OnClickListener clickListener = null;
                if (dataBean.getIs_attention() == 1) {
                    //已关注
                    if (dataBean.getIs_contact() == 1) {
                        //是联系人
                        clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UIBottomItemDialog.build()
                                        .setTitleString("你们是好友,取消关注后将会解除好友,你们将从各自的联系人列表中消失,对方不会收到任何提示")
                                        .addItem(new UIItemDialog.ItemInfo(mActivity.getResources().getString(R.string.base_ok), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        })).showDialog(mOtherILayout);
                            }
                        };
                    } else {
                        //不是联系人
                        clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UIBottomItemDialog.build()
                                        .setTitleString("确定不再关注此人?")
                                        .addItem(new UIItemDialog.ItemInfo(mActivity.getResources().getString(R.string.base_ok), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        })).showDialog(mOtherILayout);
                            }
                        };
                    }
                } else {
                    //未关注
                    clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setSelectorPosition(posInData);
                            add(RRetrofit.create(UserInfoService.class)
                                    .attention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {

                                        @Override
                                        public void onSucceed(String bean) {
                                            dataBean.setIs_attention(1);
                                            if (view != null) {
                                                setChatView(chatView, chatListener);
                                                //setAttentionView(view, dataBean, to_uid, chatView, chatListener);
                                            }
                                        }
                                    }));
                        }
                    };
                }
                followView.setOnClickListener(clickListener);
            }
        };
        userInfoAdapter.setModel(RModelAdapter.MODEL_MULTI);
        return userInfoAdapter;
    }

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        return super.initItemDecoration()
                .setDividerSize(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_line))
                .setMarginStart(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi))
                .setDrawLastLine(true)
                ;
    }

    @Override
    protected String getTitleString() {
        if (uid.equalsIgnoreCase(UserCache.getUserAccount())) {
            return mActivity.getString(R.string.followers_title);
        } else {
            return mActivity.getString(R.string.ta_followers_title);
        }
    }

    @Override
    protected boolean hasDecoration() {
        return true;
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
