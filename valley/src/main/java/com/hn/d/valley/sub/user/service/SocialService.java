package com.hn.d.valley.sub.user.service;

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
 * 创建时间：2017/01/07 17:50
 * 修改人员：Robi
 * 修改时间：2017/01/07 17:50
 * 修改备注：
 * Version: 1.0.0
 */
public interface SocialService {

    /**
     * 点赞
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	string	点赞类型【discuss-动态 news-资讯 comment-评论 reply-回复】
     * item_id	是	string	数据id【动态id/资讯id/评论id/回复id】
     */
    @POST("social/like")
    Observable<ResponseBody> like(@QueryMap Map<String, String> map);

    /**
     * 取消赞
     */
    @POST("social/dislike")
    Observable<ResponseBody> dislike(@QueryMap Map<String, String> map);

    /**
     * 收藏
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	string	收藏类型【discuss-动态 news-资讯】
     * item_id	是	string	数据id【动态id/资讯id】
     */
    @POST("social/collect")
    Observable<ResponseBody> collect(@QueryMap Map<String, String> map);

    /**
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	string	取消收藏类型【discuss-动态 news-资讯】
     * item_id	是	string	数据id【动态id/资讯id】，支持多个。多个以，分割；如14,15
     */
    @POST("social/unCollect")
    Observable<ResponseBody> unCollect(@QueryMap Map<String, String> map);

    /**
     * 获取标签列表
     */
    @POST("discuss/getTags")
    Observable<ResponseBody> getTags(@QueryMap Map<String, String> map);

    /**
     * 发布动态
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * tags	否	string	标签id集合【多个以，分割,如1,2,3】
     * media_type	否	int	默认1【1-纯文字 ；2-视频/视频+文字； 3-图片/图片+文字】
     * media	否	string	图片或者视频地址集合;多张图片以，分割【如http://www.webosss.com/xx.jpg,http://www.webosss.com/xx2.jpg】
     * is_top	否	int	是否置顶；默认0【0-不置顶 1-置顶】
     * content	否	string	文字内容
     * open_location	否	int	默认0，是否公开位置【0-不公开 1-公开】
     * address	否	string	公开的地址【如深圳大冲国际】
     * lng	否	string	公开的地址经度【如113.961974】
     * lat	否	string	公开的地址纬度【如22.547832】
     */
    @POST("discuss/publish")
    Observable<ResponseBody> publish(@QueryMap Map<String, String> map);

}
