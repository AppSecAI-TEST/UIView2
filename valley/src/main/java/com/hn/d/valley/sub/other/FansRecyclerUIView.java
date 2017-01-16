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
import com.hn.d.valley.main.message.ChatUIView;
import com.hn.d.valley.sub.user.service.ContactService;
import com.hn.d.valley.sub.user.service.UserInfoService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

/**
 * 我的粉丝
 * Created by angcyo on 2017-01-15.
 */

public class FansRecyclerUIView extends SingleRecyclerUIView<LikeUserInfoBean> {
    @Override
    protected RExBaseAdapter<String, LikeUserInfoBean, String> initRExBaseAdapter() {
        return new UserCardAdapter(mActivity, mILayout) {
            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, final LikeUserInfoBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                final ImageView commandView = holder.v(R.id.command_item_view);
                final ImageView chatView = holder.v(R.id.liaotian_item_view);
                chatView.setVisibility(View.GONE);

                if (dataBean.getIs_contact() != 1) {
                    commandView.setVisibility(View.VISIBLE);

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
//                                        public void onNext(String bean) {
//                                            T_.show(bean);
//                                        }
//                                    }));
//                        }
//                    });

                    commandView.setImageResource(R.drawable.attention_fans_n);
                    commandView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commandView.setVisibility(View.GONE);
                            chatView.setVisibility(View.VISIBLE);
                            chatView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ChatUIView.start(mILayout, dataBean.getUid(), SessionTypeEnum.P2P);
                                }
                            });

                            add(RRetrofit.create(UserInfoService.class)
                                    .attention(Param.buildMap("to_uid:" + dataBean.getUid()))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {
                                        @Override
                                        public void onError(int code, String msg) {
                                            super.onError(code, msg);
                                            T_.error(msg);
                                        }
                                    }));
                        }
                    });
                }
            }
        };
    }

    @Override
    protected String getTitleString() {
        return mActivity.getString(R.string.fans_title);
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(ContactService.class)
                .fans(Param.buildMap("page:" + page))
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
