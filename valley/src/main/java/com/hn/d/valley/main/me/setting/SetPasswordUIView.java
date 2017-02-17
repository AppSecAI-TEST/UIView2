package com.hn.d.valley.main.me.setting;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.net.rsa.RSA;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.widget.HnLoading;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：设置密码/修改密码
 * 创建人员：Robi
 * 创建时间：2017/02/16 16:58
 * 修改人员：Robi
 * 修改时间：2017/02/16 16:58
 * 修改备注：
 * Version: 1.0.0
 */
public class SetPasswordUIView extends ItemRecyclerUIView<String> {

    ExEditText mEditText1, mEditText2, mEditText3;

    boolean isModifyPassword = false;

    public SetPasswordUIView(boolean isModifyPassword) {
        this.isModifyPassword = isModifyPassword;
    }

    @Override
    protected String getTitleString() {
        if (isModifyPassword) {
            return mActivity.getString(R.string.modify_password_title);
        }
        return mActivity.getString(R.string.set_password_title);
    }

    @Override
    public int getDefaultBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, int posInData, String dataBean) {
        if (isLastPosition(posInData)) {
            TextView textView = holder.tv(R.id.text_view);
            if (isModifyPassword) {
                textView.setText(R.string.modify);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mEditText1.isPassword()) {
                            Anim.band(mEditText1);
                            return;
                        }

                        if (!mEditText2.isPassword()) {
                            Anim.band(mEditText2);
                            return;
                        }

                        if (!mEditText3.isPassword()) {
                            Anim.band(mEditText3);
                            return;
                        }

                        if (!TextUtils.equals(mEditText2.string(), mEditText3.string())) {
                            T_.error(mActivity.getString(R.string.modify_password_error_tip));
                            return;
                        }
                        add(RRetrofit.create(UserInfoService.class)
                                .resetPassword(Param.buildMap("old_pwd:" + RSA.encode(mEditText1.string()), "pwd:" + RSA.encode(mEditText2.string())))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {
                                    @Override
                                    public void onStart() {
                                        super.onStart();
                                        HnLoading.show(mOtherILayout);
                                    }

                                    @Override
                                    public void onSucceed(String bean) {
                                        super.onSucceed(bean);
                                        T_.show(bean);
                                        finishIView();
                                    }

                                    @Override
                                    public void onEnd() {
                                        super.onEnd();
                                        HnLoading.hide();
                                    }
                                })
                        );
                    }
                });
            } else {
                textView.setText(R.string.ok);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mEditText1.isPassword()) {
                            Anim.band(mEditText1);
                            return;
                        }

                        if (!mEditText2.isPassword()) {
                            Anim.band(mEditText2);
                            return;
                        }

                        if (!TextUtils.equals(mEditText1.string(), mEditText2.string())) {
                            T_.error(mActivity.getString(R.string.modify_password_error_tip));
                            return;
                        }
                        add(RRetrofit.create(UserInfoService.class)
                                .resetPassword(Param.buildMap("pwd:" + RSA.encode(mEditText2.string())))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {
                                    @Override
                                    public void onStart() {
                                        super.onStart();
                                        HnLoading.show(mOtherILayout);
                                    }

                                    @Override
                                    public void onSucceed(String bean) {
                                        super.onSucceed(bean);
                                        T_.show(bean);
                                        finishIView();
                                    }

                                    @Override
                                    public void onEnd() {
                                        super.onEnd();
                                        HnLoading.hide();
                                    }
                                })
                        );
                    }
                });
            }

        } else {
            TextView tipTextView = holder.v(R.id.tip_text_view);
            final ExEditText editText = holder.v(R.id.edit_text_view);
            CheckBox showPassWord = holder.v(R.id.show_password_checkbox);
            showPassWord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        editText.showPassword();
                    } else {
                        editText.hidePassword();
                    }
                }
            });

            if (isModifyPassword) {
                if (posInData == 0) {
                    mEditText1 = editText;
                    mEditText1.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    tipTextView.setText(R.string.old_password_tip);
                    editText.setHint(R.string.old_password_hint);
                } else if (posInData == 1) {
                    mEditText2 = editText;
                    mEditText2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    tipTextView.setText(R.string.new_password_tip);
                    editText.setHint(R.string.new_password_hint);
                } else if (posInData == 2) {
                    mEditText3 = editText;
                    mEditText3.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    tipTextView.setText(R.string.confirm_password_tip);
                    editText.setHint(R.string.confirm_password_hint);
                }
            } else {
                if (posInData == 0) {
                    mEditText1 = editText;
                    mEditText1.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    tipTextView.setText(R.string.password_tip);
                    editText.setHint(R.string.password_hint2);
                } else if (posInData == 1) {
                    mEditText2 = editText;
                    mEditText2.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    tipTextView.setText(R.string.confirm_password_tip2);
                    editText.setHint(R.string.confirm_password_hint2);
                }
            }
        }
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        mRecyclerView.addItemDecoration(new RExItemDecoration(new RExItemDecoration.ItemDecorationCallback() {
            @Override
            public Rect getItemOffsets(LinearLayoutManager layoutManager, int position) {
                Rect rect = new Rect();
                if (!isLastPosition(position)) {
                    int offset = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
                    rect.set(offset, offset, offset, offset);
                }
                return rect;
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {

            }
        }));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (isLastPosition(viewType)) {
            return R.layout.item_button_view;
        } else {
            return R.layout.item_password_view;
        }
    }

    private boolean isLastPosition(int viewType) {
        return viewType == mRExBaseAdapter.getItemCount() - 1;
    }


    @Override
    protected void createItems(List<String> items) {
        items.add("");
        items.add("");
        items.add("");
        if (isModifyPassword) {
            items.add("");
        }
    }


}
