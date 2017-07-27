package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/26
 * 修改人员：cjh
 * 修改时间：2017/7/26
 * 修改备注：
 * Version: 1.0.0
 */
public class OnlineVideoForwardMsg extends BaseCustomMsg {

    /**
     *
     * {"videoURL":"http:\/\/video.klgwl.com\/62849\/831501052764_t_4.mp4","extend_type":"video","msg":"[视频]","cover":"http:\/\/circleimg.klgwl.com\/62849\/271501052764_s_720x842.jpeg"}
     *
     * videoURL : http://video.klgwl.com/62849/831501052764_t_4.mp4
     * msg : [视频]
     * cover : http://circleimg.klgwl.com/62849/271501052764_s_720x842.jpeg
     */

    private String videoURL;
    private String msg;
    private String cover;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private int width;
    private int height;

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
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
}
