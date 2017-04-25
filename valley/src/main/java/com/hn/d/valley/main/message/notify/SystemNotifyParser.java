package com.hn.d.valley.main.message.notify;

import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.PersonalCardAttachment;
import com.hn.d.valley.realm.RRealm;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

import org.json.JSONObject;

import io.realm.Realm;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：系统通知解析类
 * 创建人员：hewking
 * 创建时间：2017/04/24 11:24
 * 修改人员：hewking
 * 修改时间：2017/04/24 11:24
 * 修改备注：
 * Version: 1.0.0
 */
public class SystemNotifyParser implements MsgAttachmentParser {

    public static final String EXTEND_TYPE = "extend_type";


    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            JSONObject object = new JSONObject(json);
            String type = object.getString(EXTEND_TYPE);
            switch (type) {
                case SystemNotifyType.NEW_DISCUSS:
                    attachment = null;
                    break;
                case SystemNotifyType.NEW_VISITOR:

                    final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
                    RRealm.exe(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            userInfoBean.setNew_visitor(true);
                        }
                    });

                    break;
                default:
//                    attachment = new NoticeAttachment(json);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attachment;
    }

}
