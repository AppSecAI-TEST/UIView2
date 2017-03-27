package com.hn.d.valley.bean;

/**
 * Created by hewking on 2017/3/23.
 */
public class PhoneUser {


    /**
     * phone : 18770090773
     * uid : 50000
     * username : nihao
     * status : 1
     * avatar : http://static.bzsns.cn/pic/M00/00/38/CixiMlbj0DOATFzlAABGkQXpArU717.png
     * sex : 0
     * signature : 个性签名
     * is_contact : 0
     * is_blacklist : 0
     * is_attention : 0
     */

    private String phone;
    private String uid;
    private String username;
    private String status;
    private String avatar;
    private String sex;
    private String signature;
    private int is_contact;
    private int is_blacklist;
    private int is_attention;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
}
