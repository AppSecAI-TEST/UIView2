package com.hn.d.valley.main.message.session;

import com.hn.d.valley.R;
import com.hn.d.valley.main.message.avchat.AVChatControl;
import com.hn.d.valley.main.message.avchat.ui.AVChatUIView;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;

import static com.hn.d.valley.main.message.avchat.ui.AVChatUIView.FROM_INTERNAL;

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

    public AVChatCommandItem(){
        this(R.drawable.nim_message_plus_photo_normal, "发送语音");
    }

    public AVChatCommandItem(int icoResId, String text) {
        super(icoResId, text);
    }

    @Override
    protected void onClick() {
        AVChatUIView.start(getContainer().mLayout,getContainer().account, AVChatType.VIDEO.getValue(),FROM_INTERNAL);
    }
}
