package com.hn.d.valley.main.message.provider;

import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.friend.IDataResource;
import com.hn.d.valley.main.friend.IDataResource.IDataProvider;
import com.hn.d.valley.main.message.query.ContactSearch;
import com.hn.d.valley.main.message.query.RecordHitInfo;
import com.hn.d.valley.main.message.query.TextQuery;
import com.hn.d.valley.main.message.search.RecordContactItem;
import com.hn.d.valley.realm.RRealm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.RealmResults;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/27 14:43
 * 修改人员：hewking
 * 修改时间：2017/03/27 14:43
 * 修改备注：
 * Version: 1.0.0
 */
public class ContactDataProvider implements IDataProvider{

    private ContactDataProvider(){}

    private static class Holder {
        private static ContactDataProvider sInsatace = new ContactDataProvider();
    }

    public static ContactDataProvider getInstance() {
        return Holder.sInsatace;
    }

    @Override
    public List<AbsContactItem> provide() {
        return null;
    }

    @Override
    public List<AbsContactItem> provide(TextQuery query) {
        // mainthread 线程访问 RRealm.realm()
        RealmResults<FriendBean> results = RRealm.realm().where(FriendBean.class).findAll();
        List<AbsContactItem> contactItems = new ArrayList<>();
        for (Iterator<FriendBean> it = results.iterator(); it.hasNext() ;) {
            FriendBean bean = it.next();
            RecordHitInfo hitInfo = new RecordHitInfo();
            boolean hit = ContactSearch.hitFriend(bean, query,hitInfo);
            if (!hit) {
                continue;
            }
            contactItems.add(new RecordContactItem(bean,hitInfo));
        }
        return contactItems;
    }



}
