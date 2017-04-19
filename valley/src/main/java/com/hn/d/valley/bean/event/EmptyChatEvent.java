package com.hn.d.valley.bean.event;

/**
 * Created by hewking on 2017/3/30.
 */
public class EmptyChatEvent {
    public EmptyChatEvent(String sessionId) {
        this.sessionId = sessionId;
    }

    public String sessionId;

}
