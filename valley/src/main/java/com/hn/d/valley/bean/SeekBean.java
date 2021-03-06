package com.hn.d.valley.bean;

import android.text.TextUtils;

import com.hn.d.valley.control.UserDiscussItemControl;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/08/03 12:02
 * 修改人员：Robi
 * 修改时间：2017/08/03 12:02
 * 修改备注：
 * Version: 1.0.0
 */
public class SeekBean {

    /**
     * uid : 50038
     * video : http://circleimg.klgwl.com/50017/711499846878_s_360x640.jpeg?http://video.klgwl.com/50017/191499846878_t_3.mp4
     * images : http://circleimg.klgwl.com/60047/1499843704111_s_440x419.jpeg,http://circleimg.klgwl.com/60047/1499843704791_s_440x416.jpeg,http://circleimg.klgwl.com/60047/1499843704808_s_440x420.jpeg,http://circleimg.klgwl.com/60047/1499843704844_s_440x409.jpeg,http://circleimg.klgwl.com/60047/149984370446_s_440x419.jpeg,http://circleimg.klgwl.com/60047/1499843704400_s_440x413.jpeg,http://circleimg.klgwl.com/60047/1499843704236_s_440x418.jpeg
     * username : 易燃易爆炸
     * avatar : http://avatorimg.klgwl.com/50038/1497429896220_s_414.00x414.00.png
     * sex : 1
     * score : 100
     * grade : 1
     * is_auth : 0
     * birthday : 1994-08-10
     * constellation : 狮子座
     * distance : 41.0609
     * charm : 5
     * rank : 2
     * fans_count : 1179
     */

    private String uid;
    private String video;
    private String images;
    private String username;
    private String avatar;
    private String sex;
    private String score;
    private String grade;
    private String is_auth;
    private String birthday;
    private String constellation;
    private String distance;
    private String charm;
    private int rank;
    private int fans_count;
    /**
     * like_cnt : 182
     * dislike_cnt : 1
     * enable : 1
     * is_like : 0
     * is_dislike : 1
     */

    private String like_cnt;
    private String dislike_cnt;
    private String enable;
    private int is_like;
    private int is_dislike;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCharm() {
        return charm;
    }

    public void setCharm(String charm) {
        this.charm = charm;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getFans_count() {
        return fans_count;
    }

    public void setFans_count(int fans_count) {
        this.fans_count = fans_count;
    }

    public String getLike_cnt() {
        return like_cnt;
    }

    public void setLike_cnt(String like_cnt) {
        this.like_cnt = like_cnt;
    }

    public String getDislike_cnt() {
        return dislike_cnt;
    }

    public void setDislike_cnt(String dislike_cnt) {
        this.dislike_cnt = dislike_cnt;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public int getIs_like() {
        return is_like;
    }

    public void setIs_like(int is_like) {
        this.is_like = is_like;
    }

    public int getIs_dislike() {
        return is_dislike;
    }

    public void setIs_dislike(int is_dislike) {
        this.is_dislike = is_dislike;
    }

    public boolean isVideoEmpty() {
        return TextUtils.isEmpty(video) || "null".equalsIgnoreCase(video);
    }

    public boolean isImagesEmpty() {
        return TextUtils.isEmpty(images) || "null".equalsIgnoreCase(images);
    }

    public boolean isEmpty() {
        return isVideoEmpty() && isImagesEmpty();
    }

    public String getVideoThumbUrl() {
        return UserDiscussItemControl.getVideoParams(video)[0];
    }

    public String getVideoUrl() {
        return UserDiscussItemControl.getVideoParams(video)[1];
    }
}
