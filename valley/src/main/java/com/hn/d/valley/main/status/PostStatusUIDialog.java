package com.hn.d.valley.main.status;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.lzy.imagepicker.ImagePickerHelper;

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
    protected void initDialogContentView() {
        super.initDialogContentView();
        click(R.id.cancel_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClick();
            }
        });
        click(R.id.image_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick();
            }
        });
        click(R.id.video_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVideoClick();
            }
        });
    }

    @Override
    public int getDimColor() {
        return Color.parseColor("#80000000");
    }

    public void onCancelClick() {
        finishDialog();
    }

    public void onImageClick() {
        finishDialog();
        ImagePickerHelper.startImagePicker(mActivity, false, true, false, true, 9);
    }

    public void onVideoClick() {
        T_.show("莫慌, 马上就能发布视频了...");
        finishDialog();
    }
}
