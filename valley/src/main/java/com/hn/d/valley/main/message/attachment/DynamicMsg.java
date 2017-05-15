package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/12 16:54
 * 修改人员：hewking
 * 修改时间：2017/05/12 16:54
 * 修改备注：
 * Version: 1.0.0
 */
public class DynamicMsg extends LikeMsg {


    /**
     * comment_id : 1
     * reply_content : 确实不错
     */

    private String comment_id;
    private String reply_content;

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getReply_content() {
        return reply_content;
    }

    public void setReply_content(String reply_content) {
        this.reply_content = reply_content;
    }
}
