package com.hn.d.valley.sub.user;

import android.text.TextUtils;

/**
 * 发布动态的类型
 */
public enum DynamicType {
    TEXT(1)/*纯文本*/, VIDEO(2)/*视频*/, IMAGE(3)/*图文*/, VOICE(4)/*语音*/, PACKET(5)/*红包*/,
    FORWARD_TEXT(6)/*转发纯文本*/, FORWARD_VIDEO(7)/*转发视频*/, FORWARD_IMAGE(8)/*转发图文*/, FORWARD_VOICE(9)/*转发语音*/, FORWARD_PACKET(10)/*转发红包*/,
    FORWARD_INFORMATION(11)/*转发资讯*/;

    int type;

    DynamicType(int type) {
        this.type = type;
    }

    public static boolean isText(String type) {
        return TextUtils.equals(TEXT.getValueString(), type);
    }

    public static boolean isVideo(String type) {
        return TextUtils.equals(VIDEO.getValueString(), type);
    }

    public static boolean isVoice(String type) {
        return TextUtils.equals(VOICE.getValueString(), type);
    }

    public static boolean isImage(String type) {
        return TextUtils.equals(IMAGE.getValueString(), type);
    }

    public int getValue() {
        return type;
    }

    public String getValueString() {
        return String.valueOf(type);
    }
}