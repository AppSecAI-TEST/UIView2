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
 * 我的粉丝
 * Created by angcyo on 2017-01-15.
 */

public class FansRecyclerUIView extends UserInfoRecyclerUIView {

    /**
     * 默认是自己
     */
    String uid;

    public FansRecyclerUIView() {
        this.uid = UserCache.getUserAccount();
    }

    public FansRecyclerUIView(String uid) {
        this.uid = uid;
    }

//    @Override
//    protected RExBaseAdapter<String, LikeUserInfoBean, String> initRExBaseAdapter() {
//        return new UserCardAdapter(mActivity, mILayout) {
//            @Override
//            protected void onBindDataView(RBaseViewHolder holder, int posInData, final LikeUserInfoBean dataBean) {
//                super.onBindDataView(holder, posInData, dataBean);
//                final ImageView commandView = holder.v(R.id.command_item_view);
//                final ImageView chatView = holder.v(R.id.liaotian_item_view);
//                chatView.setVisibility(View.GONE);
//
//                if (dataBean.getIs_contact() != 1) {
//                    commandView.setVisibility(View.VISIBLE);
//
////                    commandView.setImageResource(R.drawable.add_contacts_n);
////                    commandView.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(final View v) {
////                            add(RRetrofit.create(UserService.class)
////                                    .addContact(Param.buildMap("to_uid:" + dataBean.getUid(),
////                                            "tip:" + mActivity.getResources().getString(R.string.add_contact_tip,
////                                                    UserCache.instance().getUserInfoBean().getUsername())))
////                                    .compose(Rx.transformer(String.class))
////                                    .subscribe(new BaseSingleSubscriber<String>() {
////
////                                        @Override
////                                        public void onNext(String bean) {
////                                            T_.show(bean);
////                                        }
////                                    }));
////                        }
////                    });
//
//                    commandView.setImageResource(R.drawable.attention_fans_n);
//                    commandView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            commandView.setVisibility(View.GONE);
//                            chatView.setVisibility(View.VISIBLE);
//                            chatView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    P2PChatUIView.start(mILayout, dataBean.getUid(), SessionTypeEnum.P2P);
//                                }
//                            });
//
//                            add(RRetrofit.create(UserService.class)
//                                    .attention(Param.buildMap("to_uid:" + dataBean.getUid()))
//                                    .compose(Rx.transformer(String.class))
//                                    .subscribe(new BaseSingleSubscriber<String>() {
//                                        @Override
//                                        public void onError(int code, String msg) {
//                                            T_.error(msg);
//                                        }
//                                    }));
//                        }
//                    });
//                }
//
//                holder.v(R.id.card_root_layout).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startIView(new UserDetailUIView2(dataBean.getUid()));
//                    }
//                });
//            }
//        };
//    }


    @Override
    protected void onBindDataView(RBaseViewHolder holder, int posInData, LikeUserInfoBean dataBean) {
        super.onBindDataView(holder, posInData, dataBean);
        holder.v(R.id.follow_image_view).setVisibility(isMe() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected String getTitleString() {
        if (isMe()) {
            return mActivity.getString(R.string.fans_title);
        } else {
            return mActivity.getString(R.string.ta_fans_title);
        }
    }

    private boolean isMe() {
        return uid.equalsIgnoreCase(UserCache.getUserAccount());
    }

    @Override
    protected void onEmptyData(boolean isEmpty) {
        super.onEmptyData(isEmpty);
        initOverEmptyLayout("你还没有粉丝哦~\n去发布精彩动态, 让更多的人关注你", R.drawable.image_wufensi);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(ContactService.class)
                .fans(Param.buildMap("uid:" + uid, "page:" + page))
                .compose(Rx.transformer(UserListModel.class))
                .subscribe(new SingleRSubscriber<UserListModel>(this) {
                    @Override
                    protected void onResult(UserListModel bean) {
                        if (bean == null || bean.getData_list() == null || bean.getData_list().isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(bean.getData_list());
                        }
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                        showNonetLayout(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadData();
                            }
                        });
                    }
                }));
    }

    @Override
    protected boolean isContact(LikeUserInfoBean dataBean) {
        return dataBean.getIs_contact() == 1;
    }

    @Override
    protected boolean isAttention(LikeUserInfoBean dataBean) {
        return isContact(dataBean);
    }

    @Override
    protected boolean onSetDataBean(LikeUserInfoBean dataBean, boolean value) {
        dataBean.setIs_contact(value ? 1 : 0);
        return true;
    }
}
