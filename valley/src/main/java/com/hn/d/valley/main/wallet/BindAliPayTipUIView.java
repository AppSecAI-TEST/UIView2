package com.hn.d.valley.main.wallet;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.Json;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.utils.string.StringTextWatcher;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.pay_library.alipay.AuthResult;
import com.hn.d.valley.pay_library.alipay.OrderInfoUtil2_0;
import com.hn.d.valley.pay_library.alipay.PayConstants;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.utils.RBus;
import com.hn.d.valley.widget.HnLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/02 11:55
 * 修改人员：hewking
 * 修改时间：2017/05/02 11:55
 * 修改备注：
 * Version: 1.0.0
 */
public class BindAliPayTipUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private static final int SDK_AUTH_FLAG = 2;

    private boolean isBind;

    public BindAliPayTipUIView(boolean isBind) {
        this.isBind = isBind;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_bind_alipay));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_bind_alipay_tip;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                Button btn_bind = holder.v(R.id.btn_send);
                TextView tv_tip = holder.v(R.id.tv_isbind);
                TextView tv_note = holder.v(R.id.tv_username);

                if (isBind) {
                    tv_tip.setVisibility(View.VISIBLE);
                    tv_tip.setText(R.string.text_have_bind);
                    int type = WalletHelper.getInstance().getWalletAccount().bindType();
                    String account ;
                    if (type == 0) {
                        account = WalletHelper.getInstance().getWalletAccount().getAlipay().split(";;;")[0];
                    } else {
                        account = WalletHelper.getInstance().getWalletAccount().getAlipay_userid().split(";;;")[1];
                    }
                    tv_note.setText(String.format("支付宝账号: %s",account));
                    btn_bind.setText(R.string.text_not_immedately_bind);
                    btn_bind.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (WalletHelper.getInstance().getWalletAccount().getMoney() > 0) {
                                mParentILayout.startIView(new UnableUnBindUIView());
                            } else {
                                unBindAlipay();                            }
                        }
                    });
                } else {
                    btn_bind.setText(R.string.text_immediately_bind);
                    btn_bind.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            replaceIView(new VerifyAlipayUIView(true));
                            // 如果安装了支付宝授权
                            Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PayConstants.AliPay_PID
                                    , PayConstants.AliPay_APPID, UserCache.getUserAccount(), true);
                            long time = System.currentTimeMillis();
                            final String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
                            authInfoMap.put("timestamp",time / 1000 + "");
                            authInfoMap.put("flitets","1");
                            add(RRetrofit.create(WalletService.class)
                                    .getsign(Param.mapPay(authInfoMap))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {
                                        @Override
                                        public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                                            super.onEnd(isError, isNoNetwork, e);
                                            HnLoading.hide();
                                        }

                                        @Override
                                        public void onStart() {
                                            super.onStart();
                                            HnLoading.show(mILayout);
                                        }
                                        @Override
                                        public void onSucceed(String bean) {
                                            super.onSucceed(bean);

                                            auth2(bean,info);
                                        }
                                    }));
                        }
                    });
                }

            }
        }));
    }

    private void unBindAlipay() {
        RRetrofit.create(WalletService.class)
                .cashaccountRemove(Param.buildInfoMap("uid:" + UserCache.getUserAccount()
                        , "type:" + WalletHelper.getInstance().getWalletAccount().bindType()))
                .compose(WalletHelper.getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onSucceed(String beans) {
                        finishIView();
                        T_.show(mActivity.getString(R.string.text_unbind_success));
                    }
                });
    }


    private void auth2(String sign,String info) {
        String encodedSign = "";
        try {
            encodedSign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String authInfo = info + "&sign=" + encodedSign;
        Runnable authRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(mActivity);
                // 调用授权接口，获取授权结果
                L.d("auth2 " + authInfo);
                Map<String, String> result = authTask.authV2(authInfo, true);

                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread authThread = new Thread(authRunnable);
        authThread.start();

    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则授权账户
                        bindAlipay(authResult.getAuthCode());
                    } else {
                        // 其他状态值则为授权失败
                        T_.show("授权失败");
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void bindAlipay(String authCode) {
        add(RRetrofit.create(WalletService.class)
                .bindAlipay(Param.buildInfoMap("uid:" + UserCache.getUserAccount(),"auth_code:" + authCode))
                .compose(WalletHelper.getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        parseResult(bean);
                        T_.show("授权绑定成功");
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if(isError) {
                            T_.show("授权绑定失败");
                        }
                    }
                }));

    }

    private void parseResult(String beans) {
        int code = -1;
        String data = "";
        try {
            JSONObject jsonObject = new JSONObject(beans);
            code = jsonObject.optInt("code");
            data = jsonObject.optString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (200 == code) {
            AlipayAccount alipayAccount = Json.from(data, AlipayAccount.class);
            StringBuilder sb = new StringBuilder();
            sb.append(alipayAccount.getUserid()).append(";;;")
                    .append(alipayAccount.getNick()).append(";;;")
                    .append(alipayAccount.getAvatar());
            WalletHelper.getInstance().getWalletAccount().setAlipay_userid(sb.toString());
            RBus.post(new WalletAccountUpdateEvent());
        } else if (code == 405) {
            T_.show(data);
        }else {
            T_.show(getString(R.string.text_access_error));
        }
        finishIView();

    }


}
