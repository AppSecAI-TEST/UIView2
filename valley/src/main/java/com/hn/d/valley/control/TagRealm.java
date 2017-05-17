package com.hn.d.valley.control;

import android.text.TextUtils;

import io.realm.RealmObject;

public class TagRealm extends RealmObject {

    /**
     * id : 1
     * name : 搞笑
     */

    private String id;
    private String name;


    public TagRealm(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public TagRealm() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return TextUtils.equals(id, ((TagRealm) obj).id);
    }

    public String string() {
        return id + "->" + name;
    }
}