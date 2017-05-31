package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/31 17:24
 * 修改人员：hewking
 * 修改时间：2017/05/31 17:24
 * 修改备注：
 * Version: 1.0.0
 */
public class DynamicDetailMsg extends BaseCustomMsg{


    /**
     * avatar : http://avatorimg.klgwl.com/50017/1495769395_s_414.00x414.00.png
     * media_type : 2
     * picture :
     * username : 乔宝丰
     * msg : 分享视频
     * cover : http://circleimg.klgwl.com/50017/491495981369_s_640x360.jpeg
     * vedioURL : http://video.klgwl.com/50017/701495981369_t_12.mp4
     * item_id : 4110
     * apnsText : 分享了乔宝丰的动态
     */

    private String avatar;
    private String media_type;
    private String picture;
    private String username;
    private String msg;
    private String cover;
    private String vedioURL;
    private String item_id;
    private String apnsText;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getVedioURL() {
        return vedioURL;
    }

    public void setVedioURL(String vedioURL) {
        this.vedioURL = vedioURL;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getApnsText() {
        return apnsText;
    }

    public void setApnsText(String apnsText) {
        this.apnsText = apnsText;
    }
}
