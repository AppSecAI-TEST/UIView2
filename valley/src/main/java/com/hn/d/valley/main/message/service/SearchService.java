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
}
