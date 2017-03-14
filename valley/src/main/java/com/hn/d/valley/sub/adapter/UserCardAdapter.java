package com.hn.d.valley.sub.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.ChatUIView;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.widget.HnGenderView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

/**
 * Created by angcyo on 2017-01-15.
 */

public class UserCardAdapter extends RExBaseAdapter<String, LikeUserInfoBean, String> {

    private ILayout mILayout;

    public UserCardAdapter(Context context, ILayout ILayout) {
        super(context);
        mILayout = ILayout;
    }

    private static void setAttentionView(final ImageView view, final LikeUserInfoBean dataBean, final String to_uid,
                                         final ImageView chatView, final View.OnClickListener chatListener) {
        if (dataBean.getIs_attention() == 1) {
            //已经是关注
            view.setImageResource(R.drawable.attention_fans_s);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    RRetrofit.create(UserInfoService.class)
                            .unAttention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {

                                @Override
                                public void onSucceed(String bean) {
                                    dataBean.setIs_attention(0);
                                    if (view != null) {
                                        setAttentionView(view, dataBean, to_uid, chatView, chatListener);
                                    }
                                }
                            });
                }
            });
        } else {
            view.setImageResource(R.drawable.attention_fans_n);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RRetrofit.create(UserInfoService.class)
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
                            });
                }
            });
        }
    }

    private static void setChatView(final ImageView view, final View.OnClickListener clickListener) {
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(v);
            }
        });
    }


    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_near_user_layout;
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, int posInData, final LikeUserInfoBean dataBean) {
        super.onBindDataView(holder, posInData, dataBean);
        holder.fillView(dataBean, true);

        holder.v(R.id.introduce).setVisibility(View.GONE);
        holder.v(R.id.star).setVisibility(View.GONE);

        HnGenderView hnGenderView = holder.v(R.id.grade);
        hnGenderView.setGender(dataBean.getSex(), dataBean.getGrade());

        ImageView commandView = holder.v(R.id.command_item_view);
        commandView.setVisibility(View.GONE);

        ImageView chatView = holder.v(R.id.liaotian_item_view);
        chatView.setVisibility(View.GONE);

        final View.OnClickListener onChatListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatUIView.start(mILayout, dataBean.getUid(), SessionTypeEnum.P2P);
            }
        };

        if (dataBean.getIs_contact() == 1) {
            setChatView(chatView, onChatListener);
        } else {
            commandView.setVisibility(View.VISIBLE);
            setAttentionView(commandView, dataBean, dataBean.getUid(), chatView, onChatListener);
        }

        holder.v(R.id.auth).setVisibility("1".equalsIgnoreCase(dataBean.getIs_auth()) ? View.VISIBLE : View.GONE);
    }
}
