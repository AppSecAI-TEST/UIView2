package com.hn.d.valley.main.message.notify;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/25 10:58
 * 修改人员：hewking
 * 修改时间：2017/04/25 10:58
 * 修改备注：
 * Version: 1.0.0
 */
public class BaseNotification {

    private int created;

    private String extend_type;

    public String getExtend_type() {
        return extend_type;
    }

    public void setExtend_type(String extend_type) {
        this.extend_type = extend_type;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }



}
