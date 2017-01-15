package com.hn.d.valley.bean;

/**
 * Created by angcyo on 2017-01-15.
 */

public class LikeUserInfoBean {

    /**
     * avatar : http://static.bzsns.cn/pic/M00/00/69/CixiMlctybOAWCO9AAAbZx76WXw10.JPEG?w=200&h=200&s=1
     * username : 张伟
     * sex : 0
     * uid : 50004
     * grade : 1
     * is_auth : 1
     * signature :
     * company : 中国共产党
     * job : 国家主席
     * industry : 国务院
     */
    private String avatar;
    private String username;
    private String sex;
    private String uid;
    private String grade;
    private String is_auth;
    private String signature;
    private String company;
    private String job;
    private String industry;
    /**
     * is_contact : 1
     * contact_mark : 张伟
     * is_attention : 1
     */

    private int is_contact;
    private String contact_mark;
    private int is_attention;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getIs_auth() {
        return is_auth;
    }

    public void setIs_auth(String is_auth) {
        this.is_auth = is_auth;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
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

    public String getContact_mark() {
        return contact_mark;
    }

    public void setContact_mark(String contact_mark) {
        this.contact_mark = contact_mark;
    }

    public int getIs_attention() {
        return is_attention;
    }

    public void setIs_attention(int is_attention) {
        this.is_attention = is_attention;
    }
}
