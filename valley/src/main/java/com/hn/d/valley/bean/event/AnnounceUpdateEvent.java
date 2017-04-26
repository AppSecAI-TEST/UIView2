package com.hn.d.valley.bean.event;

import com.hn.d.valley.main.message.notify.GroupAnnounceNotification;

/**
 * Created by hewking on 2017/3/30.
 */
public class AnnounceUpdateEvent {

    public String sessionId;

    public GroupAnnounceNotification notification;

    public AnnounceUpdateEvent(String sessionId,GroupAnnounceNotification notification) {
        this.sessionId = sessionId;
        this.notification = notification;
    }


}
