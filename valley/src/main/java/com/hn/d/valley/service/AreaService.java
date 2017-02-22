package com.hn.d.valley.service;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：地区信息服务接口
 * 创建人员：Robi
 * 创建时间：2017/02/22 10:56
 * 修改人员：Robi
 * 修改时间：2017/02/22 10:56
 * 修改备注：
 * Version: 1.0.0
 */
public interface AreaService {

    /**
     * 参数名	必选	类型	说明
     * province_id	否	int	省id【如果为空 则返回所有市，否则返回某个省下的市】
     * 获取省列表
     */
    @POST("area/getCities")
    Observable<ResponseBody> getCities(@QueryMap Map<String, String> map);

    /**
     * 获取省列表
     */
    @POST("area/getProvinces")
    Observable<ResponseBody> getProvinces(@QueryMap Map<String, String> map);
}
