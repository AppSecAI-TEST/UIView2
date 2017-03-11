package com.hn.d.valley.control;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UnreadMessage extends RealmObject {
    @PrimaryKey
    String messageUid;//未读消息的id

    String sessionId;//会话id

    public UnreadMessage() {
    }

    public UnreadMessage(String messageUid, String sessionId) {
        this.messageUid = messageUid;
        this.sessionId = sessionId;
    }
}