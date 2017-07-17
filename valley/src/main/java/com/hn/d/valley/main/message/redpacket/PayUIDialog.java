package com.hn.d.valley.main.message.redpacket;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.hn.d.valley.main.wallet.WalletService;
import com.hn.d.valley.widget.PasscodeView;

import org.json.JSONException;
import org.json.JSONObject;

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

    ImageView ivCancel;
    TextView baseDialogTitleView;
    TextView baseDialogContentView;
    View lineLayout;
    ItemInfoLayout baseItemInfoLayout;
    //    @BindView(R.id.btn_send)
//    Button btn_send;
    LinearLayout baseDialogRootLayout;
    PasscodeView passcodeView;

    private Params params;
    private Action1 action;

    public PayUIDialog(Action1 action, Params params) {
        this.params = params;
        this.action = action;
    }

    @Override
    protected View inflateDialogView(FrameLayout dialogRootLayout, LayoutInflater inflater) {
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
        ivCancel = v(R.id.iv_cancel);
        baseDialogTitleView = v(R.id.base_dialog_title_view);
        baseDialogContentView = v(R.id.base_dialog_content_view);
        lineLayout = v(R.id.line_layout);
        baseItemInfoLayout = v(R.id.base_item_info_layout);
        baseDialogRootLayout = v(R.id.base_dialog_root_layout);
        passcodeView = v(R.id.passcode_view);


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
                mParentILayout.startIView(new ChoosePayWayUIDialog(action, params));
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
                                if (params.missionType == 1) {
                                    sendRedPacket();
                                } else if(params.missionType == 2) {
                                    buyKlgCoin();
                                }
                            }
                        });
                    }
                });
    }


    /**
     * {
     "uid":60001,          // 购买者id
     "money":200,          // 金额，单位为分
     "coin":20000,         // 购买的龙币个数
     "way":4               // 1-苹果商店 2-支付宝 3-微信 4-余额
     }
     */
    private void buyKlgCoin() {
        //goods 0 为龙币
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid",Integer.valueOf(UserCache.getUserAccount()));
            jsonObject.put("money",(int)params.money);
            jsonObject.put("coin",params.coin);
            jsonObject.put("way",4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RRetrofit.create(WalletService.class)
                .rechargeKlgcoin(Param.buildInfoMap("goods:0", "data:" + jsonObject.toString()))
                .compose(getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if (isError) {
                            finishDialog();
                            T_.show(mActivity.getString(R.string.text_send_fail));
                        }
                    }

                    @Override
                    public void onSucceed(String beans) {
                        parseResult(beans);
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
                    .setDialogContent(String.format(getString(R.string.text_pwd_error_freeze), TimeUtil.getElapseTimeForShow(data * 1000)))
                    .setOkText(mActivity.getString(R.string.ok))
                    .setCancelText(mActivity.getString(R.string.cancel))
                    .showDialog(mOtherILayout);
        } else {
            T_.show(getString(R.string.text_access_error));
        }
    }


    private void sendRedPacket() {
        String type = checkType();
        RRetrofit.create(RedPacketService.class)
                .newbag(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "num:" + params.num,
                        "money:" + (int) params.money, "content:" + params.content, type, "random:" + params.random))
                .compose(getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if (isError) {
                            finishDialog();
                            T_.show(mActivity.getString(R.string.text_send_fail));
                        }
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

            if (200 == code) {
                data = jsonObject.optInt("data");
                T_.show(mActivity.getString(R.string.text_send_success));
                if (action != null) {
                    action.call(code);
                }
            } else  {
                T_.show(mActivity.getString(R.string.text_send_fail));
//                T_.show(jsonObject.optString("data"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            T_.show(getString(R.string.text_access_error));
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

    public static enum RedPacketType {
        PERSON("uid"), GROUP("gid"), SQURE("squre");

        String type;

        RedPacketType(String type) {
            this.type = type;
        }
    }

    public static class Params {

        int num; // 群红包个数
        public float money;//金额
        int random = 0;//红包类型 拼手气或普通
        int balance = -1;//钱包余额
        String to_gid;// 群id
        String content;// 红包附带信息
        String to_uid;//uid
        int coin;//龙币数
        int way;// 充值龙币途径 余额 或支付宝
        //默认1 发红包 2 充值龙币 3 充值 4 提现
        int missionType = 1;
        boolean enableBalance = true; // 是否显示余额选项

        public void enableBalance(boolean enableBalance) {
            this.enableBalance = enableBalance;
        }

        public Params setType(int missionType) {
            this.missionType = missionType;
            return this;
        }

        public int getBalance() {
            return balance;
        }

        public Params setBalance(int balance) {
            this.balance = balance;
            return this;
        }

        public Params setNum(int num) {
            this.num = num;
            return this;
        }

        public Params setMoney(float money) {
            this.money = money;
            return this;
        }

        public Params setRandom(int random) {
            this.random = random;
            return this;
        }

        public Params setTo_gid(String to_gid) {
            this.to_gid = to_gid;
            return this;
        }

        public Params setContent(String content) {
            this.content = content;
            return this;
        }

        public Params setTo_uid(String to_uid) {
            this.to_uid = to_uid;
            return this;
        }

        public Params setCoin(int coin) {
            this.coin = coin;
            return this;
        }

        public Params setWay(int way) {
            this.way = way;
            return this;
        }

        public Params(int num, float money, String content, String to_uid, String to_gid, int random) {
            this.num = num;
            this.money = money;
            this.content = content;
            this.to_uid = to_uid;
            this.to_gid = to_gid;
            this.random = random;
        }

        public Params() {
        }
    }

}
