package com.angcyo.uiview.dialog;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.angcyo.uiview.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/02/24 12:11
 * 修改人员：Robi
 * 修改时间：2017/02/24 12:11
 * 修改备注：
 * Version: 1.0.0
 */
public class UIBottomItemDialog extends UIItemDialog {

    //是否显示取消按钮
    boolean showCancelButton = true;
    /*是否显示分割线*/
    boolean showDivider = true;
    /*标题文本, 没有则隐藏标题*/
    String titleString;

    public UIBottomItemDialog() {
    }

    public static UIBottomItemDialog build() {
        return new UIBottomItemDialog();
    }

    public UIBottomItemDialog setShowCancelButton(boolean showCancelButton) {
        this.showCancelButton = showCancelButton;
        return this;
    }

    public UIBottomItemDialog setShowDivider(boolean showDivider) {
        this.showDivider = showDivider;
        return this;
    }

    public UIBottomItemDialog setTitleString(String titleString) {
        this.titleString = titleString;
        return this;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        return inflate(R.layout.base_dialog_bottom_layout);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        if (!showCancelButton) {
            mViewHolder.v(R.id.cancel_control_layout).setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(titleString)) {
            mViewHolder.v(R.id.base_title_view).setVisibility(View.GONE);
        } else {
            mViewHolder.tv(R.id.base_title_view).setText(titleString);
        }

        if (showDivider) {
            mItemContentLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_BEGINNING);
        } else {
            mItemContentLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        }
    }
}
