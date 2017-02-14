package com.hn.d.valley.main.found.sub;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.github.utilcode.utils.VibrationUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.RImageCheckView;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.lzy.imagepicker.ImagePickerHelper;

import java.util.ArrayList;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：扫一扫界面
 * 创建人员：Robi
 * 创建时间：2017/01/17 09:19
 * 修改人员：Robi
 * 修改时间：2017/01/17 09:19
 * 修改备注：
 * Version: 1.0.0
 */
public class ScanUIView extends BaseContentUIView implements QRCodeView.Delegate {
    @BindView(R.id.zxing_view)
    ZXingView mZxingView;
    @BindView(R.id.light_switch_view)
    RImageCheckView mLightSwitchView;
    @BindView(R.id.photo_selector_view)
    RImageCheckView mPhotoSelectorView;

    @Override
    public boolean canTryCaptureView() {
        return true;//不支持滑动删除
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_scan_layout);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mLightSwitchView.setOnCheckedChangeListener(new RImageCheckView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RImageCheckView buttonView, boolean isChecked) {
                if (isChecked) {
                    mZxingView.openFlashlight();
                } else {
                    mZxingView.closeFlashlight();
                }
            }
        });
        mPhotoSelectorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePickerHelper.startImagePicker(mActivity, false, true, false, false, 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ArrayList<String> images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data);
        if (images.size() > 0) {
            T_.show(QRCodeDecoder.syncDecodeQRCode(images.get(0)));
        }
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(mActivity.getString(R.string.scan_title))
                .setShowBackImageView(true)
                .setTitleBarBGColor(Color.TRANSPARENT)
                .setFloating(true);
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        mZxingView.startCamera();
        mZxingView.showScanRect();
        mZxingView.startSpotDelay(160);
//        mZxingView.startSpotAndShowRect();
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        mZxingView.stopCamera();
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        mZxingView.setDelegate(this);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        mZxingView.onDestroy();
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        T_.show(result);
        VibrationUtils.vibrate(mActivity, 200);//震动
        mZxingView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        if (BuildConfig.DEBUG) {
            T_.error("Scan QRCode Error...");
        }
    }
}
