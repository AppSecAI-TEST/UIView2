package com.hn.d.valley.main.wallet;

import android.app.DownloadManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.VerifyButton;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;
import java.util.Locale;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/02 10:24
 * 修改人员：hewking
 * 修改时间：2017/05/02 10:24
 * 修改备注：
 * Version: 1.0.0
 */
public class SetPayPwdUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    public static final String SETPAYPWD = "set_pay_password";
    public static final String FINDPAYPWD = "pay_password";

    private EditText et_phone;

    private String type;
    private String code;

    public SetPayPwdUIView(String type) {
        this.type = type;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        String title = "";
        if (SETPAYPWD.equals(type)) {
            title = mActivity.getString(R.string.text_set_pay_pwd);
        } else if (FINDPAYPWD.equals(type)) {
            title = mActivity.getString(R.string.text_forget_paypwd);
        }
        return super.getTitleBar().setTitleString(title);
    }

    @Override
    protected int getItemLayoutId(int viewType) {

        if (viewType == 0) {
            return R.layout.item_single_main_text_view;
        }

        if (mRExBaseAdapter.isLast(viewType)) {
            return R.layout.item_redpacket_button_view;
        }

        return R.layout.item_set_paypwd;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        int left = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
//        int top = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xxxhdpi);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView text = holder.tv(R.id.text_view);
                text.setTextColor(ContextCompat.getColor(mActivity,R.color.main_text_color_dark));
                text.setText(String.format(Locale.CHINA,mActivity.getString(R.string.text_aut_phonenum), UserCache.instance().getLoginBean().getPhone()));
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                final VerifyButton verifyBtn = holder.v(R.id.verify_view);
                et_phone = holder.v(R.id.edit_text_view);


                verifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendVerify(new RequestCallback<String>() {
                            @Override
                            public void onStart() {
                                verifyBtn.run();
                            }

                            @Override
                            public void onSuccess(String s) {
                            }

                            @Override
                            public void onError(String msg) {
                                verifyBtn.endCountDown();
                            }
                        });
                    }
                });
            }

        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                Button btn_next = holder.v(R.id.btn_send);
                btn_next.setText(mActivity.getString(R.string.text_next_step));
                btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.equals(code, et_phone.getText().toString())) {
                            Anim.band(et_phone);
                            T_.show(mActivity.getString(R.string.code_error_tip));
                            return;
                        }
                        if (SETPAYPWD.equals(type)) {
                            replaceIView(new ChangePayPwdUIview(ChangePayPwdUIview.SET_PAY_PWD,SetPayPwdUIView.this.code));
                        } else if (FINDPAYPWD.equals(type)) {
                            replaceIView(new ChangePayPwdUIview(ChangePayPwdUIview.FIND_PAY_PWD,SetPayPwdUIView.this.code));
                        }
                    }
                });
            }
        }));

    }

    private void sendVerify(final RequestCallback<String> callback) {
        add(RRetrofit.create(WalletService.class)
                .sendPhoneVerifyCode(Param.buildInfoMap("phone:" + UserCache.instance().getLoginBean().getPhone(),"type:" + this.type))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        callback.onStart();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        callback.onError(msg);
                    }

                    @Override
                    public void onSucceed(String code) {
                        super.onSucceed(code);
                        SetPayPwdUIView.this.code = code;
                    }
                }));
    }

}
