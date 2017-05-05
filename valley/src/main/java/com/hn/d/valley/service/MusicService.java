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
 * 创建时间：2017/05/04 18:27
 * 修改人员：Robi
 * 修改时间：2017/05/04 18:27
 * 修改备注：
 * Version: 1.0.0
 */
public interface MusicService {

    /**
     * 音乐搜索
     * <p>
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * key	否	string	关键字【不填则返回推荐列表】
     * limit	否	int	每页显示的数量 默认20
     * page	否	int	第几页 【默认1】
     */
    @POST("music/search")
    Observable<ResponseBody> search(@QueryMap Map<String, String> map);
}
