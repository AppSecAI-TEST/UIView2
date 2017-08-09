package com.hn.d.valley.main.message.redpacket;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.utils.string.MD5;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.UIBaseUIDialog;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.hn.d.valley.main.wallet.WalletAccountUpdateEvent;
import com.hn.d.valley.main.wallet.WalletHelper;
import com.hn.d.valley.main.wallet.WalletService;
import com.hn.d.valley.utils.RBus;
import com.hn.d.valley.widget.HnLoading;
import com.hn.d.valley.widget.PasscodeView;

import org.json.JSONException;
import org.json.JSONObject;

import rx.functions.Action1;

import static com.hn.d.valley.main.wallet.WalletHelper.getTransformer;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/24 19:47
 * 修改人员：hewking
 * 修改时间：2017/04/24 19:47
 * 修改备注：
 * Version: 1.0.0
 */
public class RechargeAndRefundUIDialog extends UIBaseUIDialog {

    public static final String TAG = RechargeAndRefundUIDialog.class.getSimpleName();

    ImageView ivCancel;
    TextView baseDialogTitleView;
    TextView baseDialogContentView;
    View lineLayout;
    LinearLayout baseDialogRootLayout;
    PasscodeView passcodeView;

    private PayUIDialog.Params params;
    private Action1 action;

    public RechargeAndRefundUIDialog(Action1 action, PayUIDialog.Params params) {
        this.params = params;
        this.action = action;
    }

    @Override
    protected View inflateDialogView(FrameLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.recharge_dialog_layout, dialogRootLayout);
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        showSoftInput(passcodeView.getEditText());

    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        ivCancel = v(R.id.iv_cancel);
        baseDialogTitleView = v(R.id.base_dialog_title_view);
        baseDialogContentView = v(R.id.base_dialog_content_view);
        lineLayout = v(R.id.line_layout);
        baseDialogRootLayout = v(R.id.base_dialog_root_layout);
        passcodeView = v(R.id.passcode_view);

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });
        baseDialogTitleView.setText(R.string.text_inputpwd);
        baseDialogContentView.setText("￥ " + params.money);

        passcodeView.setPasscodeEntryListener(new PasscodeView.PasscodeEntryListener() {
            @Override
            public void onPasscodeEntered(String passcode) {
                passwdConfirm(passcode);
            }
        });

    }

    private void passwdConfirm(final String passcode) {
        String encryptPw = MD5.getStringMD5(passcode);
        add(RRetrofit.create(WalletService.class)
                .passwordConfirm(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "password:" + encryptPw))
                .compose(getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if (isError) {
                            T_.show("支付密码校验失败！");
                        }
                    }

                    @Override
                    public void onSucceed(String beans) {
                        parseResult(mParentILayout, beans, new Action1() {
                            @Override
                            public void call(Object o) {
                                if (params.missionType == 3) {
                                    recharge();
                                } else if(params.missionType == 4) {
                                    refund();
                                }
                            }
                        });
                    }
                }));
    }


    /**
     * {
     "uid":60001,          // 购买者id
     "money":200,          // 金额，单位为分
     "coin":20000,         // 购买的龙币个数
     "way":4               // 1-苹果商店 2-支付宝 3-微信 4-余额
     }
     */
    private void refund() {
        int type = WalletHelper.getInstance().getWalletAccount().bindType();
        String account = "";
        if (type == 0) {
            account =  WalletHelper.getInstance().getWalletAccount().getAlipay().split(";;;")[0];
        } else if (type == 1) {
            account = WalletHelper.getInstance().getWalletAccount().getAlipay_userid().split(";;;")[0];
        }
        add(RRetrofit.create(WalletService.class)
                .cashoutRequest(Param.buildInfoMap(
                        "uid:" + UserCache.getUserAccount(),
                        "type:" + type,
                        "account:" + account,
                        "money:" + (int) (params.money * 100)))
                .compose(WalletHelper.getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        HnLoading.show(getILayout());
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if (isError) {
                            HnLoading.hide();
                            finishIView();
                        }
                    }

                    @Override
                    public void onSucceed(String code) {
                        super.onSucceed(code);
                        HnLoading.hide();
                        parseResult(code);
                    }
                }));
    }

    private void parseResult(ILayout mOtherILayout, String beans, Action1 action) {
        int code = -1;
        int data = 0;
        try {
            JSONObject jsonObject = new JSONObject(beans);
            code = jsonObject.optInt("code");
            data = jsonObject.optInt("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (code == 200) {
            action.call(code);
        } else if (code == 401) {
            UIDialog.build()
                    .setDialogContent(String.format(getString(R.string.text_pwd_error_reaming), data))
                    .setOkText(mActivity.getString(R.string.ok))
                    .setCancelText(mActivity.getString(R.string.cancel))
                    .showDialog(mOtherILayout);
        } else if (code == 403) {
            UIDialog.build()
                    .setDialogContent(String.format(getString(R.string.text_pwd_error_freeze), TimeUtil.getElapseTimeForShow(data * 1000)))
                    .setOkText(mActivity.getString(R.string.ok))
                    .setCancelText(mActivity.getString(R.string.cancel))
                    .showDialog(mOtherILayout);
        } else {
//            T_.show(getString(R.string.send_error));
        }
    }


    private void recharge() {


    }

    private void parseResult(String beans) {
        int code = -1;
        int data = 0;
        try {
            JSONObject jsonObject = new JSONObject(beans);
            code = jsonObject.optInt("code");
            data = jsonObject.optInt("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (200 == code) {
            action.call(code);
            T_.show(getString(R.string.text_refund_success));
            RBus.post(new WalletAccountUpdateEvent());
        } else if (400 == code) {
            T_.show(getString(R.string.text_params_lose));
        } else if (401 == code) {
            T_.show(getString(R.string.text_pwd_error));
        } else if (402 == code) {
            T_.show(getString(R.string.text_account_not_set));
        } else if (403 == code) {
            T_.show(getString(R.string.text_account_incorrect));
        } else if (500 == code) {
            T_.show(getString(R.string.tex_server_error));
        }
        finishIView();

    }

}
