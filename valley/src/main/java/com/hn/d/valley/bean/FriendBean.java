package com.hn.d.valley.bean;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by hewking on 2017/3/7.
 */
public class FriendBean extends RealmObject{

    /**
     * uid : 50004
     * is_star : 0
     * default_mark : 大鹏
     * mark :
     * status : 1
     * sex : 0
     * avatar : http://static.bzsns.cn/pic/M00/00/69/CixiMlctybOAWCO9AAAbZx76WXw10.JPEG?w=200&h=200&s=1
     * is_auth : 0
     * introduce : null
     * signature : 个性签名
     * grade : 1
     */

    private String uid;
    @SerializedName("is_star")
    private String isStar;
    @SerializedName("default_mark")
    private String defaultMark;
    private String mark;
    private String status;
    private String sex;
    private String avatar;
    @SerializedName("is_auth")
    private String isAuth;
    private String introduce;
    private String signature;
    private String grade;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIsStar() {
        return isStar;
    }

    public void setIsStar(String isStar) {
        this.isStar = isStar;
    }

    public String getDefaultMark() {
        return defaultMark;
    }

    public void setDefaultMark(String defaultMark) {
        this.defaultMark = defaultMark;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(String isAuth) {
        this.isAuth = isAuth;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return getUid();
    }
}
