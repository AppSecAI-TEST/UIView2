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
public interface UserInfoService {
    /**
     * 获取用户信息【包括自己】
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	是	int	要查看的用户id【uid=to_uid就是查看自己的信息】
     */
    @POST("user/info")
    Observable<ResponseBody> userInfo(@QueryMap Map<String, String> map);

    /**
     * 动态列表
     * <p>
     * 全部动态中 黑名单列表用户发布的动态不会出现
     * <p>
     * 对方把自己加入了黑名单 不会出现在列表中
     * <p>
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	否	int	具体获取哪个人的【可以为自己】
     * type	否	int	1-圈子 2-推荐
     * tag	否	int	标签id
     * page	是	int	第几页 默认1
     * limit	否	int	每页显示的数量 默认20
     */
    @POST("discuss/list")
    Observable<ResponseBody> discussList(@QueryMap Map<String, String> map);

    /**
     * 请求添加为联系人申请
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	是	int	对方id
     * tip	是	发给对方的提示	如: 我是张三
     */
    @POST("contact/addContact")
    Observable<ResponseBody> addContact(@QueryMap Map<String, String> map);

    /**
     * 关注
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	是	int	被关注的人
     */
    @POST("contact/attention")
    Observable<ResponseBody> attention(@QueryMap Map<String, String> map);

    /**
     * 取消关注
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	是	int	被关注的人
     */
    @POST("contact/unAttention")
    Observable<ResponseBody> unAttention(@QueryMap Map<String, String> map);


    /**
     * 上报位置
     * <p>
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * lat	是	string	精度 【如：116.32715863448607】
     * lng	是	string	纬度【如：39.990912172420714】
     * province	否	string	省 【如：北京】
     * city	否	string	市【如：北京】
     * town	否	string	区【如：东直门】
     * street	否	string	街道 【如：大川胡同】
     * street_number	否	string	街道号【如：20】
     * address	否	string	具体地址【如：北京市东直门大川胡同30号瑶瑶网吧】
     * type	是	string	定位方式【gps/ip】
     */
    @POST("user/location")
    Observable<ResponseBody> location(@QueryMap Map<String, String> map);

    /**
     * 附近的用户
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * lng	是	string	当前经度
     * lat	是	string	当前纬度
     * page	是	int	第几页【默认1】
     * limit	否	int	每页显示的数量【默认20】
     */
    @POST("user/nearUser")
    Observable<ResponseBody> nearUser(@QueryMap Map<String, String> map);
}