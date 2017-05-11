package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/10 15:09
 * 修改人员：hewking
 * 修改时间：2017/05/10 15:09
 * 修改备注：
 * Version: 1.0.0
 */
public class CustomExpressionMsg extends BaseCustomMsg {

    public CustomExpressionMsg(String msg) {
        this.msg = msg;
        this.extend_type = CustomAttachmentType.KLGGIF_MSG;
    }

    /**
     * msg : f_l_021

     */

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
