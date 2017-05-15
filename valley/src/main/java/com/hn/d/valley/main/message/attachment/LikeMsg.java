package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/12 16:58
 * 修改人员：hewking
 * 修改时间：2017/05/12 16:58
 * 修改备注：
 * Version: 1.0.0
 */
public class LikeMsg extends BaseCustomMsg {

    /**
     * msg : 幽灵赞了你的动态
     * username : 幽灵
     * item_id : 9
     * type : discuss
     * uid : 50001
     * avatar : http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG
     * created : 1481956334
     * media :
     * media_type : 1
     * content : nihao
     * share_original_item_id : 0
     * share_original_type :
     */

    private String msg;
    private String username;
    private String item_id;
    private String type;
    private String uid;
    private String avatar;
    private int created;
    private String media;
    private String media_type;
    private String content;
    private String share_original_item_id;
    private String share_original_type;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getShare_original_item_id() {
        return share_original_item_id;
    }

    public void setShare_original_item_id(String share_original_item_id) {
        this.share_original_item_id = share_original_item_id;
    }

    public String getShare_original_type() {
        return share_original_type;
    }

    public void setShare_original_type(String share_original_type) {
        this.share_original_type = share_original_type;
    }
}
