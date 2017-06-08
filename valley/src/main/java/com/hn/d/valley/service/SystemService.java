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
 * 创建时间：2017/02/28 10:07
 * 修改人员：Robi
 * 修改时间：2017/02/28 10:07
 * 修改备注：
 * Version: 1.0.0
 */
public interface SystemService {
    /**
     * 获取客服列表
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     */
    @POST("system/customerService")
    Observable<ResponseBody> customerService(@QueryMap Map<String, String> map);

    /**
     *
     参数名	必选	类型	说明
     uid	是	int	用户id
     content	是	string	详情
     images	否	string	多张图片以英文，分割
     contact	否	string	联系方式(手机号/QQ/微信)
     * @param map
     * @return
     */
    @POST("system/feedBack")
    Observable<ResponseBody> feedBack(@QueryMap Map<String, String> map);
}
