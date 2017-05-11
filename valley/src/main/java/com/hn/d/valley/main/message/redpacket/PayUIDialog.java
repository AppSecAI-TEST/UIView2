package com.hn.d.valley.main.message.redpacket;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.hn.d.valley.main.wallet.WalletHelper;
import com.hn.d.valley.main.wallet.WalletService;
import com.hn.d.valley.widget.PasscodeView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import rx.functions.Action1;

import static com.hn.d.valley.main.message.redpacket.GrabPacketHelper.balanceCheck;
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
public class PayUIDialog extends UIIDialogImpl {

    public static final String TAG = PayUIDialog.class.getSimpleName();

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.base_dialog_title_view)
    TextView baseDialogTitleView;
    @BindView(R.id.base_dialog_content_view)
    TextView baseDialogContentView;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.base_item_info_layout)
    ItemInfoLayout baseItemInfoLayout;
    //    @BindView(R.id.btn_send)
//    Button btn_send;
    @BindView(R.id.base_dialog_root_layout)
    LinearLayout baseDialogRootLayout;
    @BindView(R.id.passcode_view)
    PasscodeView passcodeView;

    private Params params;
    private Action1 action;

    public PayUIDialog(Action1 action, Params params) {
        this.params = params;
        this.action = action;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.pay_dialog_layout, dialogRootLayout);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        balanceCheck(new Action1<Integer>() {
            @Override
            public void call(Integer money) {
                baseItemInfoLayout.setItemDarkText("￥" + money / 100f);
            }
        });
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });

//        btn_send.setText(R.string.text_immediately_pay);

        baseDialogTitleView.setText(R.string.text_cashier_desk);
        baseDialogContentView.setText("￥ " + params.money / 100f);
        baseItemInfoLayout.setItemText(mActivity.getString(R.string.text_balance));
        baseItemInfoLayout.setLeftDrawableRes(R.drawable.icon_chai);
        baseItemInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOtherILayout.startIView(new ChoosePayWayUIDialog(action,params));
                finishDialog();
            }
        });

        passcodeView.setPasscodeEntryListener(new PasscodeView.PasscodeEntryListener() {
            @Override
            public void onPasscodeEntered(String passcode) {
                passwdConfirm(passcode);
            }
        });

//        btn_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendRedPacket();
//            }
//        });
    }

    private void passwdConfirm(final String passcode) {
        RRetrofit.create(WalletService.class)
                .passwordConfirm(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "password:" + passcode))
                .compose(getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        T_.show("支付密码校验失败！");
                    }

                    @Override
                    public void onSucceed(String beans) {
                        parseResult(mOtherILayout, beans, new Action1() {
                            @Override
                            public void call(Object o) {
                                sendRedPacket();
                            }
                        });
                    }
                });
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
                    .setDialogContent(String.format(getString(R.string.text_pwd_error_freeze), data))
                    .setOkText(mActivity.getString(R.string.ok))
                    .setCancelText(mActivity.getString(R.string.cancel))
                    .showDialog(mOtherILayout);
        }
    }



    private void sendRedPacket() {
        String type = checkType();
        RRetrofit.create(RedPacketService.class)
                .newbag(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "num:" + params.num, "money:" + (int)params.money, "content:" + params.content, type, "random:" + params.random))
                .compose(getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        finishDialog();
                        T_.show(mActivity.getString(R.string.text_send_fail));
                    }

                    @Override
                    public void onSucceed(String beans) {
//                        replaceIView(new P2PStatusRPUIView());
                        parseResult(beans);
                    }
                });

    }

    private void parseResult(String beans) {

        int code = -1;
        int data = 0;
        try {
            JSONObject jsonObject = new JSONObject(beans);
            code = jsonObject.optInt("code");

            if (Constants.SUCCESS == code) {
                data = jsonObject.optInt("data");
                T_.show(mActivity.getString(R.string.text_send_success));
                if (action != null) {
                    action.call(code);
                }
            } else if (Constants.FAIL == code){
//                T_.show(mActivity.getString(R.string.text_send_fail));
                T_.show(jsonObject.optString("data"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        finishDialog();

    }

    private String checkType() {
        if (params.to_uid != null) {
            return "to_uid:" + params.to_uid;
        } else if (params.to_gid != null) {
            return "to_gid:" + params.to_gid;
        }
        return "to_uid:" + params.to_uid;
    }

    public static class Params {

        int num;
        float money;
        int random = 0;
        int balance = -1;
        String to_gid;
        String content;
        String to_uid;

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public Params(int num, float money, String content, String to_uid, String to_gid, int random) {
            this.num = num;
            this.money = money;
            this.content = content;
            this.to_uid = to_uid;
            this.to_gid = to_gid;
            this.random = random;
        }
    }

    public static enum RedPacketType {
        PERSON("uid"), GROUP("gid"), SQURE("squre");

        String type;

        RedPacketType(String type) {
            this.type = type;
        }
    }


}
