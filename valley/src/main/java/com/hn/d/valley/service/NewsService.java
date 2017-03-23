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
 * 创建时间：2017/03/23 09:53
 * 修改人员：Robi
 * 修改时间：2017/03/23 09:53
 * 修改备注：
 * Version: 1.0.0
 */
public interface NewsService {
    /**
     * 获取资讯分类列表
     */
    @POST("news/classifylist")
    Observable<ResponseBody> classifylist(@QueryMap Map<String, String> map);

    /**
     * 获取资讯列表接口
     * 参数名	必选	类型	说明
     * classify	否	string	分类[政治、搞笑、娱乐...]为空表示随机分类
     * random	否	int	是否随机，1随机、0正常顺序，默认0
     * amount	否	int	数量，为空则默认10
     * lastid	否	int64	上一次列表中的最小ID，用于分页
     */
    @POST("news/abstract")
    Observable<ResponseBody> abstract_(@QueryMap Map<String, String> map);
}
