package com.hn.d.valley.main.wallet;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.widget.PasscodeView;

import java.util.List;

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
public class ChangePayPwdUIview extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo>{

    @Override
    protected TitleBarPattern getTitleBar() {
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
                final PasscodeView passcodeView = holder.v(R.id.passcode_view);

                passcodeView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        passcodeView.requestToShowKeyboard();
                    }
                },500);

                passcodeView.setPasscodeEntryListener(new PasscodeView.PasscodeEntryListener() {
                    @Override
                    public void onPasscodeEntered(String passcode) {

                    }
                });
            }
        }));
    }
}
