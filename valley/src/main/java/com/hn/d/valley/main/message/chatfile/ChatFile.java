package com.hn.d.valley.main.message.chatfile;

import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/16 16:50
 * 修改人员：hewkingM
 * 修改时间：2017/04/16 16:50
 * 修改备注：
 * Version: 1.0.0
 */
public class ChatFile {

    /**
     * 文件路径
     */
    public String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    /**
     * 文件大小
     */
    public long size;

    public ChatFile(String path, long size, String displayName, String thumbPath,long time) {
        this.path = path;
        this.size = size;
        this.displayName = displayName;
        this.thumbPath = thumbPath;
        this.time = time;
    }

    /**

     * 文件显示名
     */
    public String displayName;


    public String thumbPath;

    public String media_type;

    private long time;


    public static ChatFile create(FileAttachment attachment, long time) {

        return new ChatFile(attachment.getPath(),attachment.getSize(),attachment.getDisplayName(),attachment.getThumbPath(),time);

    }

    public static ChatFile create(IMMessage message) {
        FileAttachment attachment = (FileAttachment) message.getAttachment();
        if (attachment instanceof VideoAttachment) {
            return VideoFile.createVideo((VideoAttachment) attachment,message.getTime());
        } else if (attachment instanceof ImageAttachment) {
            return PictureFile.createImage((ImageAttachment)attachment,message.getTime());
        }else {
            return create(attachment,message.getTime());
        }
    }

    protected ChatFile createEx(IMMessage message) {
        return null;
    }

}
