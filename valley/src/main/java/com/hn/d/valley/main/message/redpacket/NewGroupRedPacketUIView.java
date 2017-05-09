package com.hn.d.valley.main.message.redpacket;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.UI;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.x5.X5WebUIView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

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
public class NewGroupRedPacketUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private String to_gid;
    private int groupNum;

    private int rp_type = 1; // 默认拼手气

    public NewGroupRedPacketUIView(String to_gid,int groupNum) {
       this.to_gid = to_gid;
        this.groupNum = groupNum;
    }


    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.text_rp_rule)).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new X5WebUIView(Constants.REDPACKET_PROTOCOL));
            }
        }));
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_send_redpacket)).setRightItems(rightItems);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_new_group_redpacket;
    }

    public static SpannableString buildClickSpan(String prestr, String targetStr, final int color, int start, int end, final Action1 action) {
        String str = prestr;
        String txt = str + targetStr;
        SpannableString spannableString = new SpannableString(txt);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //Do something.
                if (action != null) {
                    action.call(widget);
                }
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(color);
                ds.setUnderlineText(false);
                ds.clearShadowLayer();
            }
        };
        spannableString.setSpan(clickableSpan,start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {

                final EditText etMoney = holder.v(R.id.et_money);
                final EditText etContent = holder.v(R.id.et_content);
                final Button btn_send = holder.v(R.id.btn_send);
                final EditText et_count = holder.v(R.id.et_count);
                final TextView tv_cursor = holder.v(R.id.tv_cursor);
                final TextView tv_image = holder.v(R.id.text_view);
                final CheckBox cb_switch = holder.v(R.id.cb_switch);
                TextView tv_groupNum = holder.v(R.id.tv_group_member_num);
                RelativeLayout item_input_note = holder.v(R.id.item_input_note);
                LinearLayout layout_switch = holder.v(R.id.item_switch);
                final LinearLayout layout_input = holder.v(R.id.item_input);
                TextView tv_notice = holder.v(R.id.item_notice);

                String preStr = "继续即表示同意";
                String targetStr = "《恐龙谷红包用户协议》 \n 24小时未领取的红包，将于2天内退款至你的恐龙谷钱包";
                // \n 24小时未领取的红包，将于2天内退款至你的恐龙谷钱包
                SpannableString spannableStr = buildClickSpan(preStr,targetStr,RApplication.getApp().getResources().getColor(R.color.main_text_color)
                        ,preStr.length(),preStr.length() + 11 , new Action1() {
                    @Override
                    public void call(Object o) {
                        startIView(new X5WebUIView(Constants.WALLET_PROTOCOL));
                    }
                });
                tv_notice.setText(spannableStr);
                tv_notice.setMovementMethod(LinkMovementMethod.getInstance());

                wrapSpan(cb_switch,"当前为拼手气群红包,", "改为普通红包",R.color.base_red, 10, 16,null);
                cb_switch.setChecked(true);

                cb_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            tv_image.setText(R.string.text_amout_money);
                            tv_image.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ping_hongbao, 0, 0, 0);
//                            cb_switch.setText(R.string.text_redpacket_switch_desc);
                            wrapSpan(cb_switch,"当前为拼手气群红包,", "改为普通红包",R.color.base_red, 10, 16,null);
                            rp_type = 1;
                        } else {
                            tv_image.setText(R.string.text_single_money);
                            tv_image.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
//                            cb_switch.setText(R.string.text_random_redpacket);
                            wrapSpan(cb_switch,"当前为普通红包,", "改为拼手气红包",R.color.base_red, 8, 15,null);
                            rp_type = 0;
                        }
                    }
                });

//                layout_switch.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.base_tran_to_left_enter);
//                        layout_input.startAnimation(animation);
//                    }
//                });


                tv_groupNum.setText(String.format(mActivity.getString(R.string.text_group_num),groupNum));

                UI.setViewHeight(item_input_note, mActivity.getResources().getDimensionPixelOffset(R.dimen.base_100dpi));

                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String value = etMoney.getText().toString();
                        boolean enable = value.length() > 0;
                        tv_cursor.setVisibility(!enable ? View.VISIBLE : View.GONE);
                        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(et_count.getText().toString())) {
                            return;
                        }
                        float money = Float.valueOf(value);
                        if (money > 200) {
                            enable = false;
                            T_.show(mActivity.getString(R.string.text_hongbao_lower_200));
                        }

                        btn_send.setEnabled(enable && et_count.getText().toString().length() > 0);
                    }
                };

                etMoney.addTextChangedListener(textWatcher);
                et_count.addTextChangedListener(textWatcher);

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = etContent.getText().toString();
                        if ("".equals(content)) {
                            content = etContent.getHint().toString();
                        }
                        Integer count = Integer.valueOf(et_count.getText().toString());
                        Float money = Float.valueOf(etMoney.getText().toString()) * 100;
                        if (rp_type == 0) {
                            money = count * money;

                        }

                        if (money < 0.01 * count) {
                            T_.show(mActivity.getString(R.string.text_grabed_redpacket_cant_lower));
                            return;
                        }

                        final PayUIDialog.Params params = new PayUIDialog.Params(count
                                ,money,content,null,to_gid,rp_type);

                        // 红包发送成功回调
                        final Action1 action = new Action1() {
                            @Override
                            public void call(Object o) {
                                finishIView();
                            }
                        };

                        //检查余额是否足够
                        GrabPacketHelper.balanceCheck(new Action1<Integer>() {
                            @Override
                            public void call(Integer money) {
                                //参数设置余额
                                params.setBalance(money);
                                if (money >= params.money) {
                                    mOtherILayout.startIView(new PayUIDialog(action,params));
                                } else {
                                    mOtherILayout.startIView(new ChoosePayWayUIDialog(action,params));
                                }
                            }
                        });
                    }
                });
            }
        }));
    }

    public static void wrapSpan(TextView cb_switch,String prestr, String targetStr,int color, int start,int end, final Action1 action) {
        SpannableString clickSpan = buildClickSpan(prestr,targetStr, RApplication.getApp().getResources().getColor(color),start,end,action);
        cb_switch.setText(clickSpan);
        cb_switch.setMovementMethod(LinkMovementMethod.getInstance());
    }


}
