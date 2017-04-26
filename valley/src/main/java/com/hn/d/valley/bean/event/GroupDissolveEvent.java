package com.hn.d.valley.bean.event;

import com.hn.d.valley.main.message.notify.GroupAnnounceNotification;
import com.hn.d.valley.main.message.notify.GroupDissolve;

/**
 * Created by hewking on 2017/3/30.
 */
public class GroupDissolveEvent {

    public String sessionId;

    public GroupDissolve notification;

    public GroupDissolveEvent(String sessionId, GroupDissolve notification) {
        this.sessionId = sessionId;
        this.notification = notification;
    }


}
