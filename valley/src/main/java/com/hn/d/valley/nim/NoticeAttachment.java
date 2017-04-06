package com.hn.d.valley.nim;

import android.text.TextUtils;

import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.CustomAttachmentType;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

/**
 * Created by zhoujianghua on 2015/4/9.
 */
public class NoticeAttachment extends CustomAttachment {

    protected String mData;
    protected CustomBean mBean;

    NoticeAttachment(String data) {
        super(CustomAttachmentType.Notice);
        this.mData = data;
        if (!TextUtils.isEmpty(data)) {
            mBean = Json.from(data, CustomBean.class);
        }
    }

    public NoticeAttachment(CustomBean bean) {
        super(CustomAttachmentType.Notice);
        mBean = bean;
        if (mBean != null) {
            mData = Json.to(mBean);
        } else {
            mData = "";
        }
    }

    @Override
    public String toJson(boolean send) {
        return mData;
    }

    public CustomBean getBean() {
        return mBean;
    }

    public String getData() {
        return mData;
    }
}
