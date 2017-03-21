package com.hn.d.valley.service;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by hewking on 2017/3/17.
 */
public interface MessageService {

    /**
     * 自定义普通消息列表

     新的朋友、系统消息，动态通知

     参数名	必选	类型	说明
     uid	是	int	用户id
     type	是	int	类型【3-系统消息 4-动态通知 5-新的朋友】
     page	否	int	第几页【默认为1】
     limit	否	int	每页显示的数量【默认20】
     */
    @POST("message/list")
    Observable<ResponseBody> list(@QueryMap Map<String, String> map);

    /**
     * 删除自定义普通消息

     参数名	必选	类型	说明
     uid	是	int	用户id
     message_id	否	string	消息id【删除多条消息时，每个消息id以英文，分割】【不传，则清空这一类消息】
     type	是	int	类型【5-新的朋友】
     */
    @POST("message/remove")
    Observable<ResponseBody> remove(@QueryMap Map<String, String> map);


}
