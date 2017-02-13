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
}
