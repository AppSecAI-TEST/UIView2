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
 * 创建时间：2017/07/24 10:37
 * 修改人员：Robi
 * 修改时间：2017/07/24 10:37
 * 修改备注：
 * Version: 1.0.0
 */
public interface GameService {

    /**
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * page	是	int	第几页【默认1】
     * limit	是	int	页面显示的条数【默认20】
     */
    @POST("game/list")
    Observable<ResponseBody> list(@QueryMap Map<String, String> map);

    /**
     * 获取登录token
     * <p>
     * uid	是	int	用户id
     * app_id	是	string	游戏应用id
     */
    @POST("game/getToken")
    Observable<ResponseBody> getToken(@QueryMap Map<String, String> map);
}
