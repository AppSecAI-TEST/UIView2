package com.hn.d.valley.base;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/14 13:47
 * 修改人员：Robi
 * 修改时间：2016/12/14 13:47
 * 修改备注：
 * Version: 1.0.0
 */
public class Bean<T extends Object> {

    /**
     * error : {"code":1039,"msg":"验证码未发送或者已过期,请重新发送","more":""}
     * result : 0
     */

    public ErrorBean error;
    public int result;
    public T data;

    public boolean isSuccess() {
        return 1 == result;
    }

    public static class ErrorBean {
        /**
         * code : 1039
         * msg : 验证码未发送或者已过期,请重新发送
         * more :
         */

        public int code;
        public String msg;
        public String more;
    }
}
