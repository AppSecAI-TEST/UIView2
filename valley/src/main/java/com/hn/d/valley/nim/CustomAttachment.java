package com.hn.d.valley.nim;

import android.text.TextUtils;

import com.angcyo.uiview.utils.Json;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

/**
 * Created by zhoujianghua on 2015/4/9.
 */
public class CustomAttachment implements MsgAttachment {

    protected String mData;
    protected CustomBean mBean;

    public CustomAttachment(String data) {
        this.mData = data;
        if (!TextUtils.isEmpty(data)) {
            mBean = Json.from(data, CustomBean.class);
        }
    }

    public CustomAttachment(CustomBean bean) {
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
