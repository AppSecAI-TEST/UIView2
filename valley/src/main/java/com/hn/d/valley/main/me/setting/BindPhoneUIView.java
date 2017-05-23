package com.hn.d.valley.main.me.setting;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.VerifyButton;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.OtherService;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.widget.HnLoading;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：绑定手机号码
 * 创建人员：Robi
 * 创建时间：2017/02/16 16:58
 * 修改人员：Robi
 * 修改时间：2017/02/16 16:58
 * 修改备注：
 * Version: 1.0.0
 */
public class BindPhoneUIView extends ItemRecyclerUIView<String> {

    String code = "";
    ExEditText mPhoneEdit, mCodeEdit;

    @Override
    protected String getTitleString() {
        return mActivity.getString(R.string.bind_phone);
    }

    @Override
    public int getDefaultBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, int posInData, String dataBean) {
        if (posInData == 0) {
            holder.tv(R.id.tip_text_view).setText(R.string.new_phone_number_tip);
            mPhoneEdit = holder.v(R.id.edit_text_view);
            mPhoneEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            mPhoneEdit.setMaxLength(mActivity.getResources().getInteger(R.integer.phone_number_count));
            final VerifyButton verifyButton = holder.v(R.id.verify_view);
            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    code = "";
                    if (!mPhoneEdit.isPhone()) {
                        Anim.band(mPhoneEdit);
                        return;
                    }
                    verifyButton.run();
                    add(RRetrofit.create(OtherService.class)
                            .sendPhoneVerifyCode(Param.buildMap("phone:" + mPhoneEdit.string(), "type:bind"))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {
                                @Override
                                public void onSucceed(String s) {
                                    code = s;
                                    T_.show(mActivity.getString(R.string.code_send_tip));
                                }

                                @Override
                                public void onError(int code, String msg) {
                                    super.onError(code, msg);
                                    verifyButton.endCountDown();
                                }
                            })
                    );
                }
            });
        } else if (posInData == 1) {
            holder.tv(R.id.tip_text_view).setText(R.string.code_text_tip);
            mCodeEdit = holder.v(R.id.edit_text_view);
            mCodeEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            mCodeEdit.setMaxLength(mActivity.getResources().getInteger(R.integer.code_count));
        } else if (posInData == 2) {
            TextView textView = holder.tv(R.id.text_view);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.topMargin = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xxhdpi);
            textView.setLayoutParams(layoutParams);

            textView.setText(R.string.ok);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPhoneEdit == null || mCodeEdit == null) {
                        return;
                    }
                    if (!mPhoneEdit.isPhone()) {
                        Anim.band(mPhoneEdit);
                        return;
                    }
                    if (mCodeEdit.isEmpty()) {
                        Anim.band(mCodeEdit);
                        return;
                    }
                    if (!TextUtils.isEmpty(code)) {
                        if (TextUtils.equals(code, mCodeEdit.string())) {
                            //调用绑定手机号码
                            add(RRetrofit.create(UserInfoService.class)
                                    .bindPhone(Param.buildMap("phone:" + mPhoneEdit.string(), "code:" + mCodeEdit.string(), "is_bind:1"))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {

                                        @Override
                                        public void onStart() {
                                            super.onStart();
                                            HnLoading.show(mParentILayout);
                                        }

                                        @Override
                                        public void onSucceed(String s) {
                                            T_.show(s);
                                            finishIView();
                                            UserCache.instance().updateUserInfo();
                                        }

                                        @Override
                                        public void onEnd() {
                                            super.onEnd();
                                            HnLoading.hide();
                                        }
                                    })
                            );
                        } else {
                            T_.error(mActivity.getResources().getString(R.string.code_error_tip));
                        }
                    }
                }
            });
        } else {
            holder.tv(R.id.text_tip_view1).setText(R.string.voice_code_tip);
            holder.tv(R.id.text_tip_view2).setText(R.string.voice_code);
            holder.tv(R.id.text_tip_view2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //调用语音验证码接口
                    if (!mPhoneEdit.isPhone()) {
                        Anim.band(mPhoneEdit);
                        return;
                    }
                    add(RRetrofit.create(OtherService.class)
                            .sendPhoneVerifyCode(Param.buildMap("phone:" + mPhoneEdit.string(), "type:bind", "is_voice:1"))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {
                                @Override
                                public void onSucceed(String s) {
                                    code = s;
                                    T_.show(mActivity.getString(R.string.voice_code_send_tip));
                                }

                                @Override
                                public void onError(int code, String msg) {
                                    super.onError(code, msg);
                                }
                            })
                    );
                }
            });
        }
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_edit_and_code;
        }
        if (viewType == 1) {
            return R.layout.item_edit_view;
        }
        if (viewType == 2) {
            return R.layout.item_button_view;
        }
        return R.layout.item_text_view;
    }

    @Override
    protected void createItems(List<String> items) {
        items.add("");
        items.add("");
        items.add("");
        items.add("");
    }
}
