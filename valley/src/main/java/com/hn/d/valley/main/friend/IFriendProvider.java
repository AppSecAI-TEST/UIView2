package com.hn.d.valley.main.friend;

import java.util.List;

/**
 * Created by hewking on 2017/3/8.
 */
public interface IFriendProvider {
    List<AbsFriendItem> provide(int itemType , AbsFriendItem item);
}
