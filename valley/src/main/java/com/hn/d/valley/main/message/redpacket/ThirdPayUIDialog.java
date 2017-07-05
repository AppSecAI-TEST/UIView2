package com.hn.d.valley.main.message.redpacket;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.wallet.WalletService;
import com.hn.pay_library.alipay.AlipayConstants;
import com.hn.pay_library.alipay.OrderInfoUtil2_0;

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

import static com.hn.pay_library.alipay.AlipayConstants.APPID;
import static com.hn.pay_library.alipay.OrderInfoUtil2_0.biz_content_Json;

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
public class ThirdPayUIDialog extends UIIDialogImpl {

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


    public ThirdPayUIDialog(Action1 action, PayUIDialog.Params params, String thirdType,int missionType) {
        this.params = params;
        this.type = thirdType;
        this.action = action;
        this.missionType = missionType;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
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
        baseDialogContentView.setText("￥ " + params.money / 100f);

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
            case WECHAT:
                baseItemInfoLayout.setLeftDrawableRes(R.drawable.icon_wechat_wallet);
                baseItemInfoLayout.setItemText(mActivity.getString(R.string.text_wechat));
                break;
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

        if (missionType == 0) {
            //充值
        } else if(missionType == 1) {
            //发红包
        }

         builder = new OrderInfoUtil2_0.Builder()
         .setAppId(APPID)
         .setTimeout("30m")
         .setProductCode("QUICK_MSECURITY_PAY")
         .setTotalAmount("0.01")
         .setSubject("1").setRSA2(true)
         .setBody("我是测试数据");

        RRetrofit.create(WalletService.class)
                .alipayPrepar(Param.buildInfoMap("missiontype:" + missionType + "", "missionparam:" + missionParam))
                .compose(Rx.transformer(String.class))
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        long time = System.currentTimeMillis();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" +
                                "", Locale.getDefault());
                        String format = formatter.format(new Date(time));
                        long time_stamp = time / 1000;
                        builder.setOutTradeNo(s);
                        builder.setTimestamp(format);
                        return RRetrofit.create(WalletService.class)
                                .rechargeAlipay(Param.buildPayMap("app_id:" + APPID, "biz_content:" + biz_content_Json(builder)
                                        , "charset:" + "utf-8", "method:" + "alipay.trade.app.pay", "sign_type:" + "RSA2"
                                        , "version:" + "1.0", "notify_url:" + AlipayConstants.CALLBACKURL,"timestamp:" + time_stamp))
                                .compose(Rx.transformer(String.class));
                    }
                })
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String code) {
                        L.i(TAG, code);
                        alipay(code);

                    }

                });
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
            object.put("money", 1);
            if (type == 0) {
                //充值

            } else if (type == 1) {
                // 发红包
                object.put("num", params.num);
                object.put("random", params.random);
                object.put("to_uid", TextUtils.isEmpty(params.to_uid) ? 0 : Integer.valueOf(params.to_uid));
                object.put("to_gid", TextUtils.isEmpty(params.to_gid) ? 0 : Integer.valueOf(params.to_gid));
                object.put("content", params.content);
            } else if (type == 2) {
                // 转账
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
