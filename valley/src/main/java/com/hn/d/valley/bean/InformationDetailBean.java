package com.hn.d.valley.bean;

import com.angcyo.uiview.utils.RUtils;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/06/20 14:43
 * 修改人员：Robi
 * 修改时间：2017/06/20 14:43
 * 修改备注：
 * Version: 1.0.0
 */
public class InformationDetailBean implements ILikeData {

    /**
     * id : 1
     * title : 郑州南曹火灾记者被打 官方：遭围观者阻拦起冲突
     * content : <p>2月6日，河南省郑州市管城区南曹乡一沙发厂发生火灾，有河南当地媒体记者在现场报道救援进展时，遭到现场不明身份人员的阻挠和殴打</p>...
     * type : article
     * classify : 社会
     * tags : 社会
     * form : 澎湃新闻
     * original : http://news.sohu.com/20170207/n480126248.shtml
     * author :
     * date : 1486465260
     * status : 1
     * reply : 0
     * forward : 0
     * view : 0
     * like : 918
     * tread : 10
     * recomend : 0
     * modify : 0
     * images :
     * report : 0
     * logo :
     * collect : 0
     * like_or_tread : 1
     */

    private int id;
    private String title;
    private String content;
    private String type;
    private String classify;
    private String tags;
    private String form;
    private String original;
    private String author;
    private int date;
    private int status;
    private int reply;
    private int forward;
    private int view;
    private int like;
    private int tread;
    private int recomend;
    private int modify;
    private String images;
    private int report;
    private String logo;
    private int collect;
    private int like_or_tread;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public int getForward() {
        return forward;
    }

    public void setForward(int forward) {
        this.forward = forward;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public List<String> getTagList() {
        return RUtils.split(getTags(), ";");
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getTread() {
        return tread;
    }

    public void setTread(int tread) {
        this.tread = tread;
    }

    public int getRecomend() {
        return recomend;
    }

    public void setRecomend(int recomend) {
        this.recomend = recomend;
    }

    public int getModify() {
        return modify;
    }

    public void setModify(int modify) {
        this.modify = modify;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getLike_or_tread() {
        return like_or_tread;
    }

    public void setLike_or_tread(int like_or_tread) {
        this.like_or_tread = like_or_tread;
    }

    @Override
    public String getLikeCount() {
        return "0";
    }

    @Override
    public void setLikeCount(String like_cnt) {

    }

    @Override
    public int getIsLike() {
        return getLike_or_tread();
    }

    @Override
    public void setIsLike(int is_like) {
        setLike_or_tread(is_like);
    }

    @Override
    public String getDiscussId(String type) {
        return String.valueOf(getId());
    }
}
