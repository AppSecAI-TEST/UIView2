package com.hn.d.valley.nim;

import java.io.Serializable;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/13 11:00
 * 修改人员：Robi
 * 修改时间：2017/01/13 11:00
 * 修改备注：
 * Version: 1.0.0
 */
public class CustomBean implements Serializable {

    private static final long serialVersionUID = 8165224494189449271L;
    /**
     * msg : 请求添加你为联系人
     * extend_type : add_contact
     * tip : 我是 宋大哥
     * uid : 50053
     * created : 1484215578
     * username : 宋大哥
     * true_name :
     * avatar : http://avatorimg.klgwl.com/c6607ea0-a1da-484b-9f8f-dd3dee046b14
     * sex : 1
     * is_auth : 0
     * auth_type : 0
     * job :
     * industry :
     * company :
     * signature :
     * grade : 1
     */

    private String msg;
    private String extend_type;
    private String tip;
    private String uid;
    private int created;
    private String username;
    private String true_name;
    private String avatar;
    private String sex;
    private String is_auth;
    private String auth_type;
    private String job;
    private String industry;
    private String company;
    private String signature;
    private String grade;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getExtend_type() {
        return extend_type;
    }

    public void setExtend_type(String extend_type) {
        this.extend_type = extend_type;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
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
}
