package com.hn.d.valley.main.message.provider;

import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.GroupBean;
import com.hn.d.valley.main.friend.IDataResource;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.main.message.query.ContactSearch;
import com.hn.d.valley.main.message.query.RecordHitInfo;
import com.hn.d.valley.main.message.query.TextQuery;
import com.hn.d.valley.main.message.search.RecordGroupItem;
import com.hn.d.valley.realm.RRealm;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/28 15:06
 * 修改人员：hewking
 * 修改时间：2017/03/28 15:06
 * 修改备注：
 * Version: 1.0.0
 */
public class GroupListProvider implements IDataResource.IDataProvider {

    private GroupListProvider(){}

    private static class Holder {
        private static GroupListProvider sInstance = new GroupListProvider();
    }

    public static GroupListProvider getInstance() {
        return Holder.sInstance;
    }

    @Override
    public List<AbsContactItem> provide() {
        return null;
    }

    @Override
    public List<AbsContactItem> provide(TextQuery query) {

        List<AbsContactItem> itemList = new ArrayList<>();
        RealmResults<GroupBean> results = RRealm.realm().where(GroupBean.class).findAll();
        for(Iterator<GroupBean> it = results.iterator(); it.hasNext() ;) {
            GroupBean bean = it.next();
            RecordHitInfo hitInfo = new RecordHitInfo();
            boolean hit = ContactSearch.hitGroup(bean, query,hitInfo);
            if (!hit) {
                continue;
            }
            itemList.add(new RecordGroupItem(bean,hitInfo));
        }
        return itemList;
    }

}
