package com.hn.d.valley.service;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/09 10:49
 * 修改人员：Robi
 * 修改时间：2017/01/09 10:49
 * 修改备注：
 * Version: 1.0.0
 */
public interface OtherService {

    /**
     * 参数名	必选	类型	说明
     * uid	否	int	用户id【注册时不需要传】
     * phone	是	string	手机号
     * type	是	string	类型【register-注册，bind-绑定，forgot-找回密码,change-修改手机，auth-名人认证，login_protect -登录保护验证】
     * is_voice	否	int	是否语言 默认0【0-不是 1-是】
     */
    @POST("sms/sendPhoneVerifyCode")
    Observable<ResponseBody> sendPhoneVerifyCode(@QueryMap Map<String, String> map);


    /**
     * 文件上传前获取oss sts token
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     */
    @POST("upload/getToken")
    Observable<ResponseBody> getToken(@QueryMap Map<String, String> map);
}
