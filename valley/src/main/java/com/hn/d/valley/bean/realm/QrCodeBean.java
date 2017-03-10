package com.hn.d.valley.bean.realm;

import io.realm.RealmObject;

/**
 * 保存用户id和对应的二维码路径
 */
public class QrCodeBean extends RealmObject {
    private String uid;
    private String path;
    private String avatar;

    public QrCodeBean() {
    }

    public QrCodeBean(String uid, String path, String avatar) {
        this.uid = uid;
        this.path = path;
        this.avatar = avatar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return uid + ":" + path;
    }
}