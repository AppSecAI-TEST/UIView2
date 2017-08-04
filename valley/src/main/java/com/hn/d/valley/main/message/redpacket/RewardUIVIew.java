package com.hn.d.valley.main.message.redpacket;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.hn.d.valley.main.wallet.MyWalletUIView;
import com.hn.d.valley.main.wallet.WalletAccount;
import com.hn.d.valley.main.wallet.WalletHelper;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.utils.MathUtils;
import com.hn.d.valley.widget.HnEmptyRefreshLayout;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.hn.d.valley.main.message.redpacket.GrabPacketHelper.parseResult;
import static com.hn.d.valley.main.message.redpacket.NewRedPacketUIView.showPinDialog;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：红包发放结果
 * 创建人员：hewking
 * 创建时间：2017/04/24 19:48
 * 修改人员：hewking
 * 修改时间：2017/04/24 19:48
 * 修改备注：
 * Version: 1.0.0
 */
public class RewardUIVIew extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private String uid;
    private String avatar;
    private String username;
    private String discussId;

    private Action0 action;

    public RewardUIVIew setAction(Action0 action) {
        this.action = action;
        return this;
    }

    public RewardUIVIew(String avatar ,String username, String uid, String disscussId) {
        this.uid = uid;
        this.username = username;
        this.avatar = avatar;
        this.discussId = disscussId;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleBarBGColor(ContextCompat.getColor(mActivity, R.color.base_red_d85940))
                .setTitleString(getString(R.string.text_redbag_reward));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_redpacket_reward;
    }

    @Override
    protected void inflateRecyclerRootLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        baseContentLayout.setBackgroundResource(R.color.base_red_d85940);
        FrameLayout frameLayout = new FrameLayout(mActivity);
        mRefreshLayout = new HnEmptyRefreshLayout(mActivity);
        mRecyclerView = new RRecyclerView(mActivity);
        mRecyclerView.setHasFixedSize(true);
        mRefreshLayout.addView(mRecyclerView, new ViewGroup.LayoutParams(-1, -1));
        frameLayout.addView(mRefreshLayout, new ViewGroup.LayoutParams(-1, -1));
        baseContentLayout.addView(frameLayout, new ViewGroup.LayoutParams(-1, -1));
        mRecyclerView.setBackgroundResource(R.color.default_base_white);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                HnGlideImageView iv_head = holder.v(R.id.iv_icon_head);
                iv_head.setImageUrl(avatar);

                TextView tv_username = holder.tv(R.id.tv_username);
                TextView tv_tip = holder.tv(R.id.tv_tip);
                final ExEditText et_money = holder.v(R.id.tv_money);
                ImageView tv_status = holder.imgV(R.id.tv_status);
                TextView tv_refresh = holder.tv(R.id.tv_refresh);

                et_money.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                et_money.setMaxNumber(10000);
//                et_money.setHint(R.string.text_not_over_10000);
                et_money.setDecimalCount(2);

                tv_refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_money.rollTo(MathUtils.nextFloat(0.1f,5.0f),0.1f,5.0f,2);
                    }
                });

                tv_refresh.performClick();

                tv_username.setText(username);
                tv_tip.setText("爱赞赏的人,运气不会太差哦~");

                tv_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (WalletHelper.getInstance().getWalletAccount() == null) {
                            WalletHelper.getInstance().fetchWallet(new RequestCallback<WalletAccount>() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onSuccess(WalletAccount o) {
                                    if (o.hasPin()) {
                                        perfromClick(et_money,o);
                                    } else {
                                        showPinDialog(mActivity,mILayout);
                                    }
                                }

                                @Override
                                public void onError(String msg) {

                                }
                            });
                            return;
                        }

                        if (!WalletHelper.getInstance().getWalletAccount().hasPin()) {
                            showPinDialog(mActivity,mILayout);
                        } else {
                            perfromClick(et_money,WalletHelper.getInstance().getWalletAccount());
                        }
                    }
                });

            }
        }));

    }

    private void perfromClick(ExEditText etMoney ,WalletAccount account) {
        if (TextUtils.isEmpty(etMoney.string())) {
            return;
        }

        float money = Float.valueOf(etMoney.string()) * 100;

        PayUIDialog.Params params = new PayUIDialog.Params();
        params.setBalance(account.getMoney())
                .setMoney(money)
                .setNum(1)
                .setContent("打赏")
                .setDiscussId(discussId)// 动态id
                .setTo_uid(uid)
                .setType(3);

        if (money > account.getMoney()) {
            T_.show(getString(R.string.text_balance_not_enough));

            params.enableBalance(false);
            startIView(new ThirdPayUIDialog(new Action1() {
                @Override
                public void call(Object o) {
                    finishIView();
                }
            },params, ThirdPayUIDialog.ALIPAY,3));

            return;
        }


        mParentILayout.startIView(new PayUIDialog(new Action1() {
            @Override
            public void call(Object o) {
                finishIView();
            }
        },params));
    }


}
