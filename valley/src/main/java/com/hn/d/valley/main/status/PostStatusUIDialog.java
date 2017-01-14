package com.hn.d.valley.main.status;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.hn.d.valley.R;
import com.angcyo.uiview.utils.T_;
import com.lzy.imagepicker.ImagePickerHelper;

import butterknife.OnClick;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/16 8:36
 * 修改人员：Robi
 * 修改时间：2016/12/16 8:36
 * 修改备注：
 * Version: 1.0.0
 */
public class PostStatusUIDialog extends UIIDialogImpl {
    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        return inflate(R.layout.view_post_status_dialog);
    }

    @Override
    public int getDimColor() {
        return Color.parseColor("#80000000");
    }

    @OnClick(R.id.cancel_view)
    public void onCancelClick() {
        finishDialog();
    }

    @OnClick(R.id.image_view)
    public void onImageClick() {
        finishDialog();
        ImagePickerHelper.startImagePicker(mActivity, false, true, false, true, 9);
    }

    @OnClick(R.id.video_view)
    public void onVideoClick() {
        T_.show("莫慌, 马上就能发布视频了...");
        finishDialog();
    }
}
