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
 * 创建人员：hewking
 * 创建时间：2017/04/24 16:36
 * 修改人员：hewking
 * 修改时间：2017/04/24 16:36
 * 修改备注：
 * Version: 1.0.0
 */
public interface RedPacketService {

    /**
     * 该接口用于发起一个红包
     * uid	是	int	用户名，红包发起者
     num	是	int	红包可抢个数，私包等于1
     money	是	int	红包金额，以分为单位，比如1元，money等于100
     content	是	string	红包祝福语
     timestamp	是	int64	当前时间戳，10位
     random	否	int	是否随机红包【1随机、0平均，默认0】,私包可忽略
     to_uid	否	int	红包接收方用户id，群红包和广场红包可忽略
     to_gid	否	int	红包接收群id，私包和广场红包可忽略
     to_square	否	int	是否广场红包【1是、0不是】，私包和群红包可忽略
     e_type	否	string	API签名加密方式【rsa、md5】，默认md5
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("redbag/newbag")
    Observable<ResponseBody> newbag(@QueryMap Map<String, String> map);

}
