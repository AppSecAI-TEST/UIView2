package com.hn.d.valley.bean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/03/24 17:05
 * 修改人员：Robi
 * 修改时间：2017/03/24 17:05
 * 修改备注：
 * Version: 1.0.0
 */
public class UserRecommendBean {

    /**
     * 是否已关注
     */
    public boolean isAttention = false;
    /**
     * uid : 50002
     * username : 和尚挑水
     * true_name : 范冰冰
     * avatar : http://static.bzsns.cn/pic/M00/00/B1/CixiMlfX0JiAJF9kAAAhgFkBirQ91.JPEG
     * sex : 0
     * is_auth : 1
     * auth_type : 2
     * job : 影视传媒
     * industry : 娱乐明星
     * company : 范冰冰工作室
     * fans_count : 100
     */

    private String uid;
    private String username;
    private String true_name;
    private String avatar;
    private String sex;
    private String is_auth;
    private String auth_type;
    private String job;
    private String industry;
    private String company;
    private int fans_count;

    public LikeUserInfoBean convert() {


        LikeUserInfoBean userInfo = new LikeUserInfoBean();

        userInfo.setAvatar(getAvatar());
        userInfo.setUid(getUid());
        userInfo.setUsername(getUsername());
        userInfo.setAuth_type(getAuth_type());
//        userInfo.setIs_auth(getIs_auth());
        userInfo.setSex(getSex());
        userInfo.setGrade("3");
        userInfo.setSignature(String.format("%s %s %s ", company, job, true_name));

        return userInfo;


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

    public String getTrue_name() {
        return true_name;
    }

    public void setTrue_name(String true_name) {
        this.true_name = true_name;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getFans_count() {
        return fans_count;
    }

    public void setFans_count(int fans_count) {
        this.fans_count = fans_count;
    }
}
