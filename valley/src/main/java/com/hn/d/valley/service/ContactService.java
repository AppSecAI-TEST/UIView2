package com.hn.d.valley.service;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by angcyo on 2017-01-15.
 */

public interface ContactService {

    /**
     * 我的粉丝列表
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * page	否	int	第几页【不传就是所有数据全部返回】
     * limit	否	int	页面显示的数量
     */
    @POST("contact/fans")
    Observable<ResponseBody> fans(@QueryMap Map<String, String> map);

    /**
     * 我的关注列表
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * page	否	int	第几页【不传就是所有数据全部返回】
     * limit	否	int	页面显示的数量
     */
    @POST("contact/followers")
    Observable<ResponseBody> followers(@QueryMap Map<String, String> map);

    /**
     * -解除好友关联
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	是	string	被解除的人
     */
    @POST("contact/delFriend")
    Observable<ResponseBody> delFriend(@QueryMap Map<String, String> map);

//    @POST("contact/followers")
//    Observable<EntityResponse<ListModel<LikeUserInfoBean>>> getFollowers(@QueryMap Map<String,String> map);


    /**
     * 获取和指定用户关系
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	是	int	要查看的用户id
     * <p>
     * 字段名	备注	说明
     * 0	int	普通陌生人【没有拉黑情况】
     * 1	int	双方拉黑
     * 2	int	我拉对方黑
     * 3	int	对方拉我黑
     * 4	int	互为联系人【互相关注就为联系人】
     * 5	int	我关注了对方
     * 6	int	对方关注了我
     */
    @POST("contact/getRelationship")
    Observable<ResponseBody> getRelationship(@QueryMap Map<String, String> map);

    /**
     * 移除粉丝
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	是	string	被移除的人【多个以，分割】
     */
    @POST("contact/delFans")
    Observable<ResponseBody> delFans(@QueryMap Map<String, String> map);

    /**
     * 加入黑名单
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	是	int	被加黑用户id
     */
    @POST("contact/addBlackList")
    Observable<ResponseBody> addBlackList(@QueryMap Map<String, String> map);

    @POST("contact/cancelBlackList")
    Observable<ResponseBody> cancelBlackList(@QueryMap Map<String, String> map);

    /**
     * 设置为星标好友
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	是	int	被设为星标的好友
     */
    @POST("contact/setStar")
    Observable<ResponseBody> setStar(@QueryMap Map<String, String> map);

    @POST("contact/cancelStar")
    Observable<ResponseBody> cancelStar(@QueryMap Map<String, String> map);

    /**
     * 编辑联系人备注
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	是	int	联系人
     * mark	是	int	备注
     */
    @POST("contact/setMark")
    Observable<ResponseBody> setMark(@QueryMap Map<String, String> map);

    /**
     *好友列表
     * uid	是	int	用户id
     page	否	int	第几页【不传就是所有数据全部返回】
     limit	否	int	页面显示的数量
     */
    @POST("contact/friends")
    Observable<ResponseBody> friends(@QueryMap Map<String,String> map);

    @POST("recommend/user")
    Observable<ResponseBody> recommendUser(@QueryMap Map<String, String> map);
}
