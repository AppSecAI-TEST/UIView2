package com.hn.d.valley.main.message.redpacket;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.net.base.Network;
import com.angcyo.uiview.utils.Json;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.UIBaseUIDialog;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.wallet.WalletService;
import com.hn.d.valley.pay_library.PayAPI;
import com.hn.d.valley.pay_library.WechatParam;
import com.hn.d.valley.pay_library.WechatPayReq;
import com.hn.d.valley.pay_library.alipay.PayConstants;
import com.hn.d.valley.pay_library.alipay.OrderInfoUtil2_0;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.hn.d.valley.pay_library.alipay.PayConstants.AliPay_APPID;
import static com.hn.d.valley.pay_library.alipay.OrderInfoUtil2_0.biz_content_Json;
import static com.hn.d.valley.pay_library.alipay.PayConstants.WECHATPAY_ACTION;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/02 17:39
 * 修改人员：hewking
 * 修改时间：2017/05/02 17:39
 * 修改备注：
 * Version: 1.0.0
 */
public class ThirdPayUIDialog extends UIBaseUIDialog {

    public static final String TAG = ThirdPayUIDialog.class.getSimpleName();

    public static final String ALIPAY = "alipay";
    public static final String WECHAT = "wechat";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    ImageView ivCancel;
    TextView baseDialogTitleView;
    TextView baseDialogContentView;
    ItemInfoLayout baseItemInfoLayout;
    Button btnSend;

    private PayUIDialog.Params params;
    private Action1 action;

    private String type;
    private int missionType;
    private OrderInfoUtil2_0.Builder builder;


    /**
     *
     * @param action
     * @param params 传进来的 money 单位统一为 分
     * @param thirdType
     * @param missionType
     */
    public ThirdPayUIDialog(Action1 action, PayUIDialog.Params params, String thirdType,int missionType) {
        this.params = params;
        this.type = thirdType;
        this.action = action;
        this.missionType = missionType;
    }

    @Override
    protected View inflateDialogView(FrameLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.pay_third_dialog_layout, dialogRootLayout);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        ivCancel = v(R.id.iv_cancel);
        baseDialogTitleView = v(R.id.base_dialog_title_view);
        baseDialogContentView = v(R.id.base_dialog_content_view);
        baseItemInfoLayout = v(R.id.base_item_info_layout);
        btnSend = v(R.id.btn_send);

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });

        baseDialogTitleView.setText(R.string.text_cashier_desk);
        baseDialogContentView.setText("￥ " + params.money / 100);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendRedPacket();
                pay();
            }
        });

        btnSend.setText(R.string.text_immediately_pay);

        baseItemInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentILayout.startIView(new ChoosePayWayUIDialog(action,params));
                finishDialog();
            }
        });

        switch (type) {
            case ALIPAY:
                baseItemInfoLayout.setLeftDrawableRes(R.drawable.icon_alipay_wallet);
                baseItemInfoLayout.setItemText(mActivity.getString(R.string.text_alipay));
                break;
//            case WECHAT:
//                baseItemInfoLayout.setLeftDrawableRes(R.drawable.icon_wechat_wallet);
//                baseItemInfoLayout.setItemText(mActivity.getString(R.string.text_wechat));
//                break;
        }


    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        L.d(TAG,"onViewShow : wechat pay");
        if (type.equals(WECHAT)) {
            // 微信支付成功或失败 WxPayEntryActivity
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();

                    L.i("msp", resultInfo + ":::" + resultStatus);

                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        T_.show(mActivity.getString(R.string.text_pay_success));
                        action.call(resultInfo);
                        finishDialog();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        T_.show(mActivity.getString(R.string.text_pay_fail));
                        finishDialog();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void pay() {
        String missionParam = generateParam(missionType);

        builder = new OrderInfoUtil2_0.Builder()
                .setAppId(AliPay_APPID)
                .setTimeout("30m")
                .setProductCode("QUICK_MSECURITY_PAY")
                .setTotalAmount(String.valueOf(params.money / 100))
                .setRSA2(true);

        if (missionType == 0) {
            //充值
            builder.setSubject("余额充值")
                    .setBody("余额充值");
        } else if(missionType == 1) {
            //发红包
            builder.setSubject("发红包")
            .setBody("发红包");
        } else if (missionType == 2) {
            // 充龙币
            builder.setSubject("充龙币")
            .setBody("充龙币");
        }  else if (missionType == 3) {
            // 打赏
            builder.setSubject("打赏")
                    .setBody("打赏");
        }

        switch (type) {
            case ALIPAY:
                alipayPrepare(missionParam);
                break;
            case WECHAT:
                wechatPrepare(missionParam);
                break;
        }


    }

    private void wechatPrepare(String missionParam) {
        add(RRetrofit.create(WalletService.class)
                .prepare(Param.buildInfoMap("missiontype:" + missionType + "", "missionparam:" + missionParam))
                .compose(Rx.transformer(String.class))
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return RRetrofit.create(WalletService.class)
                                .wechatPay(Param.buildInfoMap("money:" + (int)params.money,"ip:" + Network.getIPAddress(true),"payid:" + s))
                                .compose(Rx.transformer(String.class));
                    }
                })
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String param) {
                        L.i(TAG, "wechat" + param);
                        wechatPay(param);
                    }
                }));

    }

    private void wechatPay(String param) {

        observerWechatPay();

        WechatParam wechat = Json.from(param,WechatParam.class);

        WechatPayReq wechatPayReq = new WechatPayReq.Builder()
                .with(mActivity) //activity实例
                .setAppId(PayConstants.WechatPay_APPID) //微信支付AppID
                .setPartnerId(wechat.getPartnerid())//微信支付商户号
                .setPrepayId(wechat.getPrepayid())//预支付码
                .setNonceStr(wechat.getNoncestr())
                .setTimeStamp(wechat.getTimestamp() + "")//时间戳
                .setSign(wechat.getSign())//签名
                .create();

        wechatPayReq.setOnWechatPayListener(new WechatPayReq.OnWechatPayListener() {
            @Override
            public void onPaySuccess(int errorCode) {
                T_.show(mActivity.getString(R.string.text_pay_success));
                action.call("");
                finishDialog();
            }

            @Override
            public void onPayFailure(int errorCode) {
                T_.show(mActivity.getString(R.string.text_pay_fail));
                finishDialog();
            }
        });

        PayAPI.getInstance().sendPayRequest(wechatPayReq);

    }

    private void observerWechatPay() {
        // 监听微信支付成功失败 广播
        final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(mActivity);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WECHATPAY_ACTION);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(WECHATPAY_ACTION)){
                    int resultCode = intent.getIntExtra(PayConstants.WECHATPAY_CODE,-1);
                    if (resultCode == 0) {
                        // 成功
                        T_.show(mActivity.getString(R.string.text_pay_success));
                        action.call(0);
                        finishDialog();
                    } else {
                        // -1  -2 用户取消
                        T_.show(mActivity.getString(R.string.text_pay_fail));
                        finishDialog();
                    }
                }
                manager.unregisterReceiver(this);
            }
        };
        manager.registerReceiver(receiver,filter);
    }

    private void alipayPrepare(String missionParam) {
        add(RRetrofit.create(WalletService.class)
                .prepare(Param.buildInfoMap("missiontype:" + missionType + "", "missionparam:" + missionParam))
                .compose(Rx.transformer(String.class))
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        long time = System.currentTimeMillis();
                        String format = TimeUtil.getDatetime(time);
                        long time_stamp = time / 1000;
                        builder.setOutTradeNo(s);
                        builder.setTimestamp(format);
                        return RRetrofit.create(WalletService.class)
                                .rechargeAlipay(Param.buildPayMap("app_id:" + AliPay_APPID, "biz_content:" + biz_content_Json(builder)
                                        , "charset:" + "utf-8", "method:" + "alipay.trade.app.pay", "sign_type:" + "RSA2"
                                        , "version:" + "1.0", "notify_url:" + PayConstants.CALLBACKURL,"timestamp:" + time_stamp))
                                .compose(Rx.transformer(String.class));
                    }
                })
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String code) {
                        L.i(TAG, code);
                        alipay(code);
                    }
                }));
    }

    /**
     * {
     * "uid":60006,          // 红包发起者id
     * "num":1,              // 红包个数，个人红包固定为1
     * "random":0,           // 是否随机【1随机、0平均】
     * "money":20000,        // 金额，单位为分
     * "to_uid":60001,       // 红包接收方
     * "to_gid":0,           // 红包接收群，to_uid和to_gid必有一个为0
     * "content":"恭喜发财"   // 红包祝福语
     * }
     *
     * @param type
     * @return
     */
    private String generateParam(int type) {
        JSONObject object = new JSONObject();
        try {
            object.put("uid", Integer.valueOf(UserCache.getUserAccount()));
            object.put("money", (int) params.money);
            if (type == 0) {
                //充值
            } else if (type == 1) {
                // 发红包
                object.put("num", params.num);
                object.put("random", params.random);
                object.put("to_uid", TextUtils.isEmpty(params.to_uid) ? 0 : Integer.valueOf(params.to_uid));
                object.put("to_gid", TextUtils.isEmpty(params.to_gid) ? 0 : Integer.valueOf(params.to_gid));
                object.put("content", params.content);
                if (!TextUtils.isEmpty(params.getExtend())) {
                    // 如果是视频红包
                    object.put("extend",params.getExtend());
                    object.put("to_square",Integer.valueOf(params.to_square));
                }
            } else if (type == 2) {
                // 购买龙币
                object.put("coin",params.coin);
                object.put("way",params.way);
            } else if (type == 3) {
                // 打赏
                object.put("to_uid", TextUtils.isEmpty(params.to_uid) ? 0 : Integer.valueOf(params.to_uid));
                object.put("discussid",Integer.valueOf(params.discussid));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();

    }

    private void alipay(String code) {
        String encodedSign = code;

        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(builder);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        try {
            encodedSign = URLEncoder.encode(encodedSign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String sign = "sign=" + encodedSign;

        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(mActivity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                L.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

}
