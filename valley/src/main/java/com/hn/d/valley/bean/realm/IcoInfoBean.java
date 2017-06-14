package com.hn.d.valley.bean.realm;

import android.text.TextUtils;

import io.realm.RealmObject;

public class IcoInfoBean extends RealmObject {
    //用户id
    public String uid;
    //用户头像
    public String avatar;

    public IcoInfoBean() {
    }

    public IcoInfoBean(String uid, String avatar) {
        this.uid = uid;
        this.avatar = avatar;
    }

    @Override
    public boolean equals(Object obj) {
        return TextUtils.equals(uid, ((IcoInfoBean) obj).uid);
    }
}