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
 * 创建时间：2017/06/19 11:17
 * 修改人员：Robi
 * 修改时间：2017/06/19 11:17
 * 修改备注：
 * Version: 1.0.0
 */
public interface AdsService {
    /**
     * 获取广告列表
     * 参数名	必选	类型	说明
     * key	是	string	广告位置【下面有说明】
     */
    @POST("ads/list")
    Observable<ResponseBody> list(@QueryMap Map<String, String> map);
}
