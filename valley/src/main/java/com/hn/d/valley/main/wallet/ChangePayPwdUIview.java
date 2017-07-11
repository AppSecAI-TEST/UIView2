package com.hn.d.valley.main.wallet;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.utils.RBus;
import com.hn.d.valley.widget.PasscodeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import rx.functions.Action1;

import static com.hn.d.valley.main.wallet.WalletHelper.getTransformer;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/02 10:23
 * 修改人员：hewking
 * 修改时间：2017/05/02 10:23
 * 修改备注：
 * Version: 1.0.0
 */
public class ChangePayPwdUIview extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    public static final int FIND_PAY_PWD = 0;
    public static final int CHANGE_PAY_PWD = 1;
    public static final int SET_PAY_PWD = 2;
    TextView tv_change_pwd_tip;
    PasscodeView passcodeView;
    private RelativeLayout ll_container;
    private boolean confirmPwd = false;
    private boolean inputNewPwd = false;

    private String newPwd;
    private String oldPwd;

    private String verifyCode;
    private boolean findPayPwd = false;
    private int pay_pwd_type = CHANGE_PAY_PWD;

    public ChangePayPwdUIview() {

    }

    public ChangePayPwdUIview(int pay_pwd_type, String verifyCode) {
//        this.findPayPwd = findPayPwd;
        this.verifyCode = verifyCode;
        this.pay_pwd_type = pay_pwd_type;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        if (pay_pwd_type == CHANGE_PAY_PWD) {
            return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_change_pay_pwd));
        } else if (pay_pwd_type == FIND_PAY_PWD) {
            return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_find_paypwd));
        } else if (pay_pwd_type == SET_PAY_PWD) {
            return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_set_pay_pwd));
        }
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_change_pay_pwd));

    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.view_change_paypwd;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                passcodeView = holder.v(R.id.passcode_view);
                tv_change_pwd_tip = holder.v(R.id.tv_change_pwd_tip);

                ll_container = holder.v(R.id.ll_container);
                passcodeView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        passcodeView.requestToShowKeyboard();
                    }
                }, 500);

                if (pay_pwd_type == FIND_PAY_PWD || pay_pwd_type == SET_PAY_PWD) {
                    tv_change_pwd_tip.setText(R.string.text_please_input_pwd);
                }

                passcodeView.setPasscodeEntryListener(new PasscodeView.PasscodeEntryListener() {
                    @Override
                    public void onPasscodeEntered(String passcode) {

                        if (pay_pwd_type == FIND_PAY_PWD || pay_pwd_type == SET_PAY_PWD) {
                            newPwd = passcode;
                            passwordSet();
                            return;
                        }

                        if (!confirmPwd) {
                            oldPwd = passcode;
                            passwdConfirm(passcode);
                        } else if (!inputNewPwd) {
                            inputNewPwd = true;
                            newPwd = passcode;
                            ll_container.startAnimation(translateAnim());
                            tv_change_pwd_tip.setText(R.string.text_repeat_new_pwd);
                            passcodeView.clearText();
                        } else {
                            if (newPwd != null && newPwd.equals(passcode)) {
                                passwordSet();
                            } else {
                                T_.show(mActivity.getString(R.string.text_new_old_pwd_not_same));
                            }
                        }
                    }
                });
            }
        }));
    }

    private void passwordSet() {
        Map<String, String> buildInfoMap = null;
        if (pay_pwd_type == FIND_PAY_PWD || pay_pwd_type == SET_PAY_PWD) {
            buildInfoMap = Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "password:" + newPwd
                    , "verification_code:" + verifyCode, "phone:" + UserCache.instance().getLoginBean().getPhone());
        } else {
            buildInfoMap = Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "password:" + newPwd, "oldpassword:" + oldPwd
                    , "phone:" + UserCache.instance().getLoginBean().getPhone());
        }

        RRetrofit.create(WalletService.class)
                .passwordSet(buildInfoMap)
                .compose(getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onSucceed(String beans) {
                        parseResult(mParentILayout, beans, new Action1() {
                            @Override
                            public void call(Object o) {
                                finishIView();
                                RBus.post(new WalletAccountUpdateEvent());
                                T_.show("修改成功！");
                            }
                        });
                    }
                });
    }

    private void passwdConfirm(String passcode) {
        RRetrofit.create(WalletService.class)
                .passwordConfirm(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "password:" + passcode))
                .compose(getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if (isError) {
                            T_.show(mActivity.getString(R.string.text_pay_pwd_failed));
                        }
                    }

                    @Override
                    public void onSucceed(String beans) {
//                        replaceIView(new ChangePayPwdUIview());
                        parseResult(mParentILayout, beans, new Action1<Integer>() {
                            @Override
                            public void call(Integer o) {
                                confirmPwd = true;
                                ll_container.startAnimation(translateAnim());
                                tv_change_pwd_tip.setText(R.string.text_please_input_new_pwd);
                                passcodeView.clearText();
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
                    .setDialogContent(String.format("密码错误，剩余可尝试次数 %d", data))
                    .setOkText(mActivity.getString(R.string.text_forget_paypwd))
                    .setCancelText(mActivity.getString(R.string.cancel))
                    .setOkClick(new UIDialog.OnDialogClick() {
                        @Override
                        public void onDialogClick(UIDialog dialog, View clickView) {
                            startIView(new SetPayPwdUIView(SetPayPwdUIView.FINDPAYPWD));
                        }
                    })
                    .showDialog(mOtherILayout);
        } else if (code == 403) {
            UIDialog.build()
                    .setDialogContent(String.format("支付密码输入错误超过3次,账户已被锁定,请点击忘记密码找回或%s后重试", TimeUtil.getElapseTimeForShow(data * 1000)))
                    .setOkText(mActivity.getString(R.string.text_forget_paypwd))
                    .setOkClick(new UIDialog.OnDialogClick() {
                        @Override
                        public void onDialogClick(UIDialog dialog, View clickView) {
                            startIView(new SetPayPwdUIView(SetPayPwdUIView.FINDPAYPWD));
                        }
                    })
                    .setCancelText(mActivity.getString(R.string.cancel))
                    .showDialog(mOtherILayout);
        } else {
            T_.show(getString(R.string.text_access_error));
        }
    }


    public Animation translateAnim() {
        Animation translateAniation = AnimationUtils.loadAnimation(mActivity, R.anim.base_tran_to_left_enter);
        return translateAniation;

//        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1f,
//                Animation.RELATIVE_TO_PARENT, 0f,
//                Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f);
//        setDefaultConfig(translateAnimation, false);
//        return translateAnimation;
    }

}
