package com.hn.d.valley.bean;

import android.text.TextUtils;

import com.angcyo.uiview.utils.RUtils;
import com.hn.d.valley.sub.user.DynamicType;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/03/23 11:07
 * 修改人员：Robi
 * 修改时间：2017/03/23 11:07
 * 修改备注：
 * Version: 1.0.0
 */
public class HotInfoListBean {

    /**
     * id : 521
     * title : 台女星隐退结婚继子女拒喊妈只喊姐
     * date : 1486405080
     * author :
     * from : 网易娱乐
     * classify : 娱乐
     * tags : 娱乐
     * type : article
     * imgs : http://112.74.15.30/group1/M00/00/06/cEoPHliZqaqAbZvRAACqEBpJirg585.png
     * reply_cnt : 0
     * logo :
     */

    private int id;
    private String title;
    private long date;
    private String author;
    private String from;
    private String classify;
    private String tags;
    private String type;
    private String imgs;
    private int reply_cnt;
    private String logo;
    /**
     * date : 1486405080
     * media :
     */

    private String media;

    public static HotInfoListBean from(UserDiscussListBean.DataListBean.OriginalInfo originalInfo) {
        HotInfoListBean bean;
        String media = originalInfo.getMedia();
        if (originalInfo.isInformationVideoType()) {
            bean = HotInfoListBean.from(Integer.parseInt(originalInfo.getNews_id()), originalInfo.getAuthor(),
                    originalInfo.getMedia_type(), originalInfo.getLogo(), originalInfo.getTitle(),
                    originalInfo.getInformationVideoThumbUrl(), originalInfo.getInformationVideoUrl());
        } else {
            bean = HotInfoListBean.from(Integer.parseInt(originalInfo.getNews_id()), originalInfo.getAuthor(),
                    originalInfo.getMedia_type(), originalInfo.getLogo(), originalInfo.getTitle(),
                    media, "");
        }
        return bean;
    }

    public static HotInfoListBean from(UserDiscussListBean.DataListBean dataListBean) {
        HotInfoListBean bean;
//        String media = dataListBean.getMedia();
//        if (originalInfo.isInformationVideoType()) {
//            bean = HotInfoListBean.from(Integer.parseInt(originalInfo.getNews_id()), originalInfo.getAuthor(),
//                    originalInfo.getMedia_type(), originalInfo.getLogo(), originalInfo.getTitle(),
//                    originalInfo.getInformationVideoThumbUrl(), originalInfo.getInformationVideoUrl());
//        } else {
//            bean = HotInfoListBean.from(Integer.parseInt(originalInfo.getNews_id()), originalInfo.getAuthor(),
//                    originalInfo.getMedia_type(), originalInfo.getLogo(), originalInfo.getTitle(),
//                    media, "");
//        }
        bean = HotInfoListBean.from(Integer.parseInt(dataListBean.getNews_id()), dataListBean.getAuthor(),
                dataListBean.getMedia_type(), dataListBean.getUser_info().getAvatar(), dataListBean.getContent(),
                dataListBean.getMedia(), "");
        return bean;
    }

    public static HotInfoListBean from(int id, String author, String type, String logo, String title, String imgs, String videoUrl) {
        HotInfoListBean bean = new HotInfoListBean();
        bean.setId(id);
        bean.setAuthor(author);
        bean.setTitle(title);
        bean.setType(type);
        bean.setLogo(logo);
        bean.setImgs(imgs);
        bean.setMedia(videoUrl);
        return bean;
    }

    public boolean isPicture() {
        return "picture".equalsIgnoreCase(type) || DynamicType.isImage(type);
    }

    public boolean isText() {
        return "article".equalsIgnoreCase(type) || DynamicType.isText(type);
    }

    public boolean isVideo() {
        return "video".equalsIgnoreCase(type) || DynamicType.isVideo(type);
    }

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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgs() {
        if (TextUtils.isEmpty(imgs)) {
            return "";
        }
        return imgs.replaceAll(";", ",");
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public List<String> getImgsList() {
        return RUtils.split(getImgs());
    }

    public String getInformationVideoUrl() {
        try {
            String media = getImgs();
            return media.substring(media.lastIndexOf('?') + 1, media.length());
        } catch (Exception e) {
            return media;
        }
    }

    public String getInformationVideoThumbUrl() {
        try {
            String media = getImgs();
            return media.substring(0, media.lastIndexOf('?'));
        } catch (Exception e) {
            return media;
        }
    }

    public int getReply_cnt() {
        return reply_cnt;
    }

    public void setReply_cnt(int reply_cnt) {
        this.reply_cnt = reply_cnt;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }
}
