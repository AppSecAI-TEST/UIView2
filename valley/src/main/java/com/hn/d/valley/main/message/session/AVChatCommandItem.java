package com.hn.d.valley.main.message.session;

import android.Manifest;
import android.content.Intent;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.main.avchat.AVChatDelegete;
import com.hn.d.valley.main.avchat.activity.AVChatActivity;
//import com.hn.d.valley.main.message.avchat.AVChatControl;
//import com.hn.d.valley.main.message.avchat.ui.AVChatUIView;
import com.hn.d.valley.main.message.avchat.ui.AVChatUIVIew2;
import com.hn.d.valley.service.ContactService;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;

import rx.functions.Action1;

import static com.hn.d.valley.main.avchat.activity.AVChatActivity.FROM_INTERNAL;
import static com.hn.d.valley.main.avchat.activity.AVChatActivity.PREVIEW_REQUESTCODE;

//import static com.hn.d.valley.main.message.avchat.ui.AVChatUIView.FROM_INTERNAL;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/22 16:55
 * 修改人员：hewking
 * 修改时间：2017/05/22 16:55
 * 修改备注：
 * Version: 1.0.0
 */
public class AVChatCommandItem extends CommandItemInfo {

    private AVChatType chatType;

    public AVChatCommandItem(AVChatType type) {
        this(type == AVChatType.AUDIO ? R.drawable.yuying_xiaoxi_n : R.drawable.shipingliaotian_xiaoxi,
                type == AVChatType.AUDIO ? "语音聊天" : "视频聊天");
        this.chatType = type;
    }

    public AVChatCommandItem(int icoResId, String text) {
        super(icoResId, text);
    }

    @Override
    protected void onClick() {
//        AVChatUIVIew2.start(getContainer().mLayout,getContainer().account, AVChatType.VIDEO.getValue(),FROM_INTERNAL);
//        AVChatDelegete.getInstance().bind(getContainer().activity);
        // 好友才可以视频聊天
        if (getContainer().proxy.getRelationType() != 4) {
            T_.show("非好友不能语音聊天/视频聊天");
            return;
        }

        getContainer().activity.checkPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO},
                new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            AVChatActivity.launch(getContainer().activity, getContainer().account, chatType.getValue(), FROM_INTERNAL);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PREVIEW_REQUESTCODE) {

        }
    }
}
