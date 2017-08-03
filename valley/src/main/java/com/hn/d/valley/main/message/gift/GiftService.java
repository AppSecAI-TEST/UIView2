package com.hn.d.valley.main.message.gift;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/5
 * 修改人员：cjh
 * 修改时间：2017/7/5
 * 修改备注：
 * Version: 1.0.0
 */
public interface GiftService {

    /**
     * 简要描述：
     礼物列表
     */
    @POST("gift/list")
    Observable<ResponseBody> giftList(@QueryMap Map<String, String> map);

    /**
     * 赠送礼物
     * @param map
     * @return
     */
    @POST("gift/giving")
    Observable<ResponseBody> giving(@QueryMap Map<String, String> map);

    /**
     * 收到的礼物
     * @param map
     * @return
     */
    @POST("gift/received")
    Observable<ResponseBody> giftReceived(@QueryMap Map<String, String> map);

    /**
     * 礼物往来记录
     * @param map
     * @return
     */
    @POST("gift/history")
    Observable<ResponseBody> giftHistory(@QueryMap Map<String, String> map);



}
