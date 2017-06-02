package com.hn.d.valley.main.message.attachment;

import java.io.Serializable;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/06 14:19
 * 修改人员：hewking
 * 修改时间：2017/04/06 14:19
 * 修改备注：
 * Version: 1.0.0
 */
public class BaseCustomMsg implements Serializable{

    private static final long serialVersionUID = 1L;

    protected String extend_type;

    public String getExtend_type() {
        return extend_type;
    }

    public void setExtend_type(String extend_type) {
        this.extend_type = extend_type;
    }
}
