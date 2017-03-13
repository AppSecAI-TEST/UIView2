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
 * 创建时间：2017/03/13 15:21
 * 修改人员：Robi
 * 修改时间：2017/03/13 15:21
 * 修改备注：
 * Version: 1.0.0
 */
public interface SiteService {
    /**
     * 获取行业列表
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * page	否	int	第几页 默认0,返回所有数据
     * limit	否	int	每页显示的数量【默认20】
     */
    @POST("site/getIndustries")
    Observable<ResponseBody> getIndustries(@QueryMap Map<String, String> map);
}
