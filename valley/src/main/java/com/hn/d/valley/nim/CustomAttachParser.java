package com.hn.d.valley.nim;

import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.CustomAttachmentType;
import com.hn.d.valley.main.message.attachment.PersonalCard;
import com.hn.d.valley.main.message.attachment.PersonalCardAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

import org.json.JSONObject;

public class CustomAttachParser implements MsgAttachmentParser {

//    private static final String KEY_TYPE = "type";
//    private static final String KEY_DATA = "data";

    public static final String EXTEND_TYPE = "extend_type";


    // 根据解析到的消息类型，确定附件对象类型
    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;

//        try {
//            JSONObject object = new JSONObject(json);
//            int type = object.getInt(KEY_TYPE);
//            String data = object.getString(KEY_DATA);
//            switch (type) {
//                case CustomAttachmentType.PersonalCard:
//                    attachment = new PersonalCardAttachment(Json.from(data, FriendBean.class));
//                    break;
//                default:
//                    attachment = new NoticeAttachment(json);
//                    break;
//            }
//
////            if (attachment != null) {
////                attachment.fromJson(data);
////            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            JSONObject object = new JSONObject(json);
            String type = object.getString(EXTEND_TYPE);
            switch (type) {
                case CustomAttachmentType.PersonalCard_:
                    attachment = new PersonalCardAttachment(Json.from(json, PersonalCard.class));
                    break;
                default:
                    attachment = new NoticeAttachment(json);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attachment;
    }

}