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
     * first_id	否	int	默认0 第一页第一条数据的discuss_id【推荐列表下拉更新显示有多少条数据更新用到】
     * last_id	否	int	默认0 目前加载的数据最后一条数据的discuss_id【避免上拉加载数据 数据重复，当该值不为0时 page不用传】
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
     * 批量关注
       注册引导可以用到
     */
    @POST("contact/attentionBatch")
    Observable<ResponseBody> attentionBatch(@QueryMap Map<String,String> map);

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

    /**
     * 绑定/修改手机号
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * phone	是	string	手机号
     * code	是	string	手机验证码
     * is_bind	是	int	是否绑定手机【0/1】，如果不是则为修改手机
     */
    @POST("user/bindPhone")
    Observable<ResponseBody> bindPhone(@QueryMap Map<String, String> map);

    /**
     * 修改密码
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * old_pwd	否	string	旧密码【rsa加密】【修改密码需要传】
     * pwd	是	string	新密码【rsa加密】
     */
    @POST("user/resetPassword")
    Observable<ResponseBody> resetPassword(@QueryMap Map<String, String> map);

    /**
     * 找回密码
     * 参数名	必选	类型	说明
     * phone	是	string	手机号
     * code	是	string	手机验证码
     * pwd	是	string	新密码【rsa加密】
     */
    @POST("user/forgot")
    Observable<ResponseBody> forgot(@QueryMap Map<String, String> map);

    /**
     * 编辑个人信息
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * username	否	string	用户名、昵称
     * sex	否	int	性别 【1-男 2-女】
     * signature	否	string	个性签名
     * province_id	否	int	省份id
     * city_id	否	int	市id
     * avatar	否	string	头像
     * photos	否	string	照片墙【不包含头像，多张图片以，分割】【当删除图片时，删除所有照片传empty】
     * voice	否	string	语音介绍【传empty代表删除语音介绍】
     * birthday	否	string	生日【2017-02-21】
     */
    @POST("user/editInfo")
    Observable<ResponseBody> editInfo(@QueryMap Map<String, String> map);

    /**
     * 访客列表
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * page	是	int	第几页,默认第一页
     * limit	否	int	页面显示的数量
     */
    @POST("user/visitorList")
    Observable<ResponseBody> visitorList(@QueryMap Map<String, String> map);


}
