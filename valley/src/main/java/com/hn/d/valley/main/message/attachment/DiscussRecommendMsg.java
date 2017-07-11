package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/7
 * 修改人员：cjh
 * 修改时间：2017/7/7
 * 修改备注：
 * Version: 1.0.0
 */
public class DiscussRecommendMsg extends BaseCustomMsg {

    /**
     * msg : 尊敬的C999999，您的动态已被系统设为推荐
     * discuss_id : 3214
     * created : 1499418929
     */

    private String msg;
    private String discuss_id;
    private int created;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDiscuss_id() {
        return discuss_id;
    }

    public void setDiscuss_id(String discuss_id) {
        this.discuss_id = discuss_id;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }
}
