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
 * 创建时间：2017/01/06 8:25
 * 修改人员：Robi
 * 修改时间：2017/01/06 8:25
 * 修改备注：
 * Version: 1.0.0
 */
public interface ShowService {
    /**
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * lng	是	string	经度 【113.953856】
     * lat	是	string	纬度 【22.542617】
     * page	否	int	第几页 不传全部返回
     * limit	是	int	每页多少条数据【默认20】
     * distance	是	int	距离【单位千米】【默认5】
     * age_start	否	int	年龄起始
     * age_end	否	int	年龄结束
     * sex	否	int	1-男 2-女 0-全部【默认0】
     * c	否	int	星座【定义见下面】
     */
    @POST("show/list")
    Observable<ResponseBody> list(@QueryMap Map<String, String> map);

    /**
     * 秀场详情
     * <p>
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	是	int	查看谁的
     */
    @POST("show/detail")
    Observable<ResponseBody> detail(@QueryMap Map<String, String> map);

    @POST("show/like")
    Observable<ResponseBody> like(@QueryMap Map<String, String> map);

    @POST("show/dislike")
    Observable<ResponseBody> dislike(@QueryMap Map<String, String> map);


    /**
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * lng	是	string	当前经度
     * lat	是	string	当前纬度
     * page	否	int	第几页【默认1】
     * limit	否	int	每页显示的数量【默认20】
     */
    @POST("recommend/match")
    Observable<ResponseBody> match(@QueryMap Map<String, String> map);


}
