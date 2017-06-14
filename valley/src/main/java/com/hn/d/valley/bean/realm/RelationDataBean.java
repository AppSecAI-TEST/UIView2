package com.hn.d.valley.bean.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RelationDataBean extends RealmObject {
    /**
     * count : 2
     * list : [{"avatar":"http://circleimg.klgwl.com/77500371484917281.776834","uid":"50037"},{"avatar":"http://avatorimg.klgwl.com/15019298316_1485330266.033782","uid":"60021"}]
     */

    private int count;
    private RealmList<IcoInfoBean> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public RealmList<IcoInfoBean> getList() {
        return list;
    }

    public void setList(RealmList<IcoInfoBean> list) {
        this.list = list;
    }
}