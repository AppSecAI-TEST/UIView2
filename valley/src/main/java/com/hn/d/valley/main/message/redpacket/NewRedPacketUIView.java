package com.hn.d.valley.main.message.redpacket;

import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.UI;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

import static com.hn.d.valley.main.message.redpacket.NewGroupRedPacketUIView.buildClickSpan;
import static com.hn.d.valley.main.message.redpacket.NewGroupRedPacketUIView.wrapSpan;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/24 16:45
 * 修改人员：hewking
 * 修改时间：2017/04/24 16:45
 * 修改备注：
 * Version: 1.0.0
 */
public class NewRedPacketUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private String to_uid;

    public NewRedPacketUIView(String to_uid) {
       this.to_uid = to_uid;
    }


    @Override
    protected TitleBarPattern getTitleBar() {

        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.text_rp_rule)).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));

        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_send_redpacket)).setRightItems(rightItems);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_new_redpacket;

    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

//        final int left = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi_5);
//        int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {

                final EditText etMoney = holder.v(R.id.et_money);
                final EditText etContent = holder.v(R.id.et_content);
                final Button btn_send = holder.v(R.id.btn_send);
                final TextView tv_cursor = holder.v(R.id.tv_cursor);

                TextView tv_notice = holder.v(R.id.item_notice);

                String preStr = "继续即表示同意";
                String targetStr = "《恐龙谷红包用户协议》 \n 24小时未领取的红包，将于2天内退款至你的恐龙谷钱包";
                // \n 24小时未领取的红包，将于2天内退款至你的恐龙谷钱包
                wrapSpan(tv_notice,preStr,targetStr,R.color.main_text_color,preStr.length(),preStr.length() + 11 , new Action1() {
                    @Override
                    public void call(Object o) {
                        T_.show("呵呵");
                    }
                });


                UI.setViewHeight(etContent, mActivity.getResources().getDimensionPixelOffset(R.dimen.base_100dpi));

                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int money = Integer.valueOf(etMoney.getText().toString());
                        if (money > 200) {
                            T_.show(mActivity.getString(R.string.text_hongbao_lower_200));
                            return;
                        }
                        boolean enable = etMoney.getText().toString().length() > 0;
                        btn_send.setEnabled(enable);
                        tv_cursor.setVisibility(!enable ? View.VISIBLE : View.GONE);
                    }
                };

                etMoney.addTextChangedListener(textWatcher);

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = etContent.getText().toString();
                        if ("".equals(content)) {
                            content = etContent.getHint().toString();
                        }
                        PayUIDialog.Params params = new PayUIDialog.Params(1,Integer.valueOf(etMoney.getText().toString()) * 100,content,to_uid,null,0);
                        mOtherILayout.startIView(new PayUIDialog(new Action1() {
                            @Override
                            public void call(Object o) {
                                finishIView();
                            }
                        },params));
                    }
                });
            }
        }));
    }


}
