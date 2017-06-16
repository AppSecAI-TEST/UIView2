package com.hn.d.valley.main.me.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;

/**
    扫描二维码加群
 */
public class QrCodeAddGroupUIView extends BaseContentUIView {


    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_qrcode_add_group);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setShowBackImageView(true);
    }

    @Override
    public int getDefaultBackgroundColor() {
        return mActivity.getResources().getColor(R.color.chat_bg_color);
    }

    @Override
    protected int getTitleResource() {
        return R.string.my_qr_code;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();



    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
    }

}
