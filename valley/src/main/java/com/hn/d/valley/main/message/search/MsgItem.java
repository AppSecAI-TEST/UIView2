package com.hn.d.valley.main.message.search;

import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ItemTypes;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.search.model.MsgIndexRecord;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/27 15:23
 * 修改人员：hewking
 * 修改时间：2017/03/27 15:23
 * 修改备注：
 * Version: 1.0.0
 */
public class MsgItem extends AbsContactItem {

    private MsgIndexRecord indexRecord;

    private boolean querySession;

    public boolean isQuerySession() {
        return querySession;
    }

    public MsgItem(MsgIndexRecord indexRecord,boolean querySession) {
        this.indexRecord = indexRecord;
        itemType = ItemTypes.MSG;
        groupText = "聊天信息";
        this.querySession = querySession;
    }

    public MsgIndexRecord getIndexRecord() {
        return indexRecord;
    }

    public void setIndexRecord(MsgIndexRecord indexRecord) {
        this.indexRecord = indexRecord;
    }

    public String getDisplayName() {
        String sessionId = indexRecord.getSessionId();
        SessionTypeEnum sessionType = indexRecord.getSessionType();

        if (sessionType == SessionTypeEnum.P2P) {
            return NimUserInfoCache.getInstance().getUserDisplayName(sessionId);
        } else if (sessionType == SessionTypeEnum.Team) {
            return TeamDataCache.getInstance().getTeamName(sessionId);
        }

        return "";
    }

    public String getContactId() {
        return indexRecord.getSessionId();
    }

}
