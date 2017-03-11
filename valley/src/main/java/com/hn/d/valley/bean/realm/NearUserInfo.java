package com.hn.d.valley.bean.realm;

import io.realm.RealmObject;


@Deprecated
/**请使用 {@link com.hn.d.valley.bean.LikeUserInfoBean}*/
public class NearUserInfo extends RealmObject {
    /**
     * uid : 50003
     * username : 憨豆先生
     * status : 1
     * avatar : http://static.bzsns.cn/pic/M00/00/ED/CixiMlgf2JGANVS_AAAYeWy7a6g56.JPEG?w=200&h=200&s=1
     * sex : 0
     * signature :
     * is_auth : 1
     * auth_type : 3
     * auth_desc : 国家体育队姚明
     * job : 运动员
     * company : 国家队
     * industry :
     * is_contact : 0
     * is_blacklist : 0
     * is_attention : 1
     * lng : 39.990912172420714
     * lat : 116.32715863448607
     * distance : 0
     * show_distance : 附近
     * created : 1481694406
     * show_time : 2016-12-14 13:46
     */

    private String uid;
    private String username;
    private String status;
    private String avatar;
    private String sex;
    private String signature;
    private String is_auth;
    private String auth_type;
    private String auth_desc;
    private String job;
    private String company;
    private String industry;
    private int is_contact;
    private int is_blacklist;
    private int is_attention;
    private String lng;
    private String lat;
    private String distance;
    private String show_distance;
    private String created;
    private String show_time;
    private String grade;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getIs_auth() {
        return is_auth;
    }

    public void setIs_auth(String is_auth) {
        this.is_auth = is_auth;
    }

    public String getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(String auth_type) {
        this.auth_type = auth_type;
    }

    public String getAuth_desc() {
        return auth_desc;
    }

    public void setAuth_desc(String auth_desc) {
        this.auth_desc = auth_desc;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public int getIs_contact() {
        return is_contact;
    }

    public void setIs_contact(int is_contact) {
        this.is_contact = is_contact;
    }

    public int getIs_blacklist() {
        return is_blacklist;
    }

    public void setIs_blacklist(int is_blacklist) {
        this.is_blacklist = is_blacklist;
    }

    public int getIs_attention() {
        return is_attention;
    }

    public void setIs_attention(int is_attention) {
        this.is_attention = is_attention;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getShow_distance() {
        return show_distance;
    }

    public void setShow_distance(String show_distance) {
        this.show_distance = show_distance;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getShow_time() {
        return show_time;
    }

    public void setShow_time(String show_time) {
        this.show_time = show_time;
    }
}