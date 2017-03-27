package com.hn.d.valley.main.friend;

import com.hn.d.valley.main.message.query.TextQuery;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by hewking on 2017/3/8.
 */
public class FriendDataProvider implements IDataResource.IDataProvider {
    public List<AbsContactItem> provide(Action1 action) {
        return null;
    }

    @Override
    public List<? extends AbsContactItem> provide() {
        return null;
    }

    @Override
    public List<? extends AbsContactItem> provide(TextQuery query) {
        return null;
    }
//    @Override
//    public List<AbsContactItem> provide(int itemType, AbsContactItem item) {
//        List<AbsContactItem> friendItems = new ArrayList<>();
//
//        //retrofit 加载
//        //成功回调 往frienditems 填充数据
//
//        return friendItems;
//    }
}
