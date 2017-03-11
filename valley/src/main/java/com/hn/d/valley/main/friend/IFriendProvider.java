package com.hn.d.valley.main.friend;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by hewking on 2017/3/8.
 */
public interface IFriendProvider {
    List<AbsFriendItem> provide(Action1 action);
}
