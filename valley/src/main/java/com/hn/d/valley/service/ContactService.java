package com.hn.d.valley.service;

import com.hn.d.valley.bean.EntityResponse;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.ListModel;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.POST;
import retrofit2.http.Query;
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
     * 关注好友
     * @param map
     * @return
     */
    @POST("contact/attention")
    Observable<ResponseBody> attention(@QueryMap Map<String,String> map);

    @POST("contact/attention")
    Observable<EntityResponse<Integer>> attention(@Query("uid") int uid, @Query("to_uid") int to_uid,@Query("source") int source);

}
