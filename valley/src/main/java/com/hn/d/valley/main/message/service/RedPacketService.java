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

    /**
     *发起抢红包请求
     参数名	必选	类型	说明
     uid	是	int	抢红包的用户id
     redid	是	in64	红包id
     timestamp	是	int64	当前时间戳，10位长度
     e_type	否	string	API签名加密方式【rsa、md5】，默认md5
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("redbag/grabbag")
    Observable<ResponseBody> grabbag(@QueryMap Map<String, String> map);

    /**
     *查询抢红包请求的结果
     参数名	必选	类型	说明
     uid	是	int	抢红包的用户id
     redid	是	int64	红包id
     timestamp	是	int64	当前时间戳，10位长度
     e_type	否	string	API签名加密方式【rsa、md5】，默认md5
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("redbag/result")
    Observable<ResponseBody> result(@QueryMap Map<String, String> map);


    /**
     *查询该红包状态，看看是不是能抢啥的
     参数名	必选	类型	说明
     uid	是	int	查询红包状态的用户id
     redid	是	int64	红包id
     timestamp	是	int64	当前时间戳，10位长度
     e_type	否	string	API签名加密方式【rsa、md5】，默认md5
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("redbag/status")
    Observable<ResponseBody> status(@QueryMap Map<String, String> map);


    /**
     *查看该红包的详情，比如是谁发的、金额、是否随机红包、是否群红包、有哪些人抢了等等等等(默认先返回20个)
     参数名	必选	类型	说明
     redid	是	int64	红包id
     timestamp	是	int64	当前时间戳,10位长度
     e_type	否	string	API签名的加密方式【rsa、md5】，默认md5
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("redbag/detail")
    Observable<ResponseBody> detail(@QueryMap Map<String, String> map);

    /**
     *获取红包结果列表,数据对应红包详情下面的列表数据
     参数名	必选	类型	说明
     redid	是	int64	红包id
     limit	否	int	请求个数限制，不填默认20个
     lastid	否	int64	上次收到列表中的最小id,用于分页，不填默认从最新开始
     timestamp	是	int64	当前时间戳
     e_type	否	string	API签名的加密方式【rsa、md5】，默认md5
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("redbag/resultlist")
    Observable<ResponseBody> resultlist(@QueryMap Map<String, String> map);


    /**
     *获取收到/发出的红包列表，数据对应红包概要下方的列表数据
     参数名	必选	类型	说明
     uid	是	int	查询的用户id
     type	是	int	查询的类型【1表示发出的、2表示收到的】
     limit	否	int	查询个数
     lastid	否	int64	上次列表中收到的最小id，用于分页
     timestamp	是	int64	当前时间戳，10位长度
     e_type	否	string	API签名的加密方式【rsa、md5】，默认md5
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("redbag/redbaglist")
    Observable<ResponseBody> redbaglist(@QueryMap Map<String, String> map);


    /**获取自己发出的/收到的红包概要
     * uid	是	int	用户名
     type	是	int	请求的列表类型【1：发出的列表、2：收到的列表】
     timestamp	是	int64	当前时间戳，10位
     e_type	否	string	API签名的加密方式【rsa、md5】，默认md5
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("redbag/myredbag")
    Observable<ResponseBody> myredbag(@QueryMap Map<String, String> map);

}
