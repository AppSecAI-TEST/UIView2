package com.hn.d.valley.bean;


/**
 * Created by hewking on 2017/3/1.
 */
@Deprecated
public class RecommendUserBean{

    /**
     * uid : 50507
     * username : 奔跑的西瓜仔
     * true_name :
     * avatar : http://klg-useravator.oss-cn-shenzhen.aliyuncs.com/13811111111_1484649274.262574
     * sex : 2
     * is_auth : 0
     * auth_type : 0
     * job :
     * industry :
     * company :
     * grade : 1
     * signature :
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
    private String grade;

    public int getIs_contact() {
        return is_contact;
    }

    public void setIs_contact(int is_contact) {
        this.is_contact = is_contact;
    }

    public int getIs_attention() {
        return is_attention;
    }

    public void setIs_attention(int is_attention) {
        this.is_attention = is_attention;
    }

    private String signature;

    private int is_contact;
    private int is_attention;

    /**
     * RxUtil connect 调用tostring 返回uid
     * @return
     */
    @Override
    public String toString() {
        return uid;
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
