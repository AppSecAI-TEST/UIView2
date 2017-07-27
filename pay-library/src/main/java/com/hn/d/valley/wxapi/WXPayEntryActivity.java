package com.hn.d.valley.wxapi;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.hn.d.R;
import com.hn.d.valley.pay_library.alipay.PayConstants;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import static com.hn.d.valley.pay_library.alipay.PayConstants.WECHATPAY_ACTION;


public class WXPayEntryActivity extends WXCallbackActivity {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, PayConstants.WechatPay_APPID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Toast.makeText(this, "BaseReq : " + req.openId, Toast.LENGTH_SHORT).show();
        super.onReq(req);
    }

    @Override
    public void onResp(BaseResp resp) {
//        Toast.makeText(this, "onresp : " + resp.errStr, Toast.LENGTH_SHORT).show();
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Intent intent = new Intent(WECHATPAY_ACTION);
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) { //        0 成功	展示成功页面
                intent.putExtra(PayConstants.WECHATPAY_CODE,BaseResp.ErrCode.ERR_OK);
                manager.sendBroadcast(intent);
            } else {//  -1	错误       -2	用户取消
                intent.putExtra(PayConstants.WECHATPAY_CODE,resp.errCode);
                manager.sendBroadcast(intent);
            }
        }
        super.onResp(resp);
    }

}