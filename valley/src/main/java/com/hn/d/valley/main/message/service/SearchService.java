package com.hn.d.valley.main.message.service;

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
 * 创建时间：2016/12/26 9:18
 * 修改人员：Robi
 * 修改时间：2016/12/26 9:18
 * 修改备注：
 * Version: 1.0.0
 */
public interface SearchService {
    /**
     * 添加好友【搜索用户】
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * key	是	string	手机或用户id
     */
    @POST("contact/searchUser")
    Observable<ResponseBody> searchUser(@QueryMap Map<String, String> map);

    /**
     * 名人推荐
     */
    @POST("search/userRecommend")
    Observable<ResponseBody> userRecommend(@QueryMap Map<String, String> map);


    /**
     * 推荐动态
     */
    @POST("search/discussRecommend")
    Observable<ResponseBody> discussRecommend(@QueryMap Map<String, String> map);

    /**
     * 热搜关键词列表，默认10个，10分钟更新一次
     * 参数名	必选	类型	说明
     * limit	否	int	请求个数，默认10个
     * timestamp	是	int64	当前时间戳，10位长度
     * e_type	否	string	API签名的加密方式【rsa、md5】，默认md5
     * sign	是	string	API签名
     */
    @POST("klgsearch/hotwords")
    Observable<ResponseBody> hotwords(@QueryMap Map<String, String> map);
}
