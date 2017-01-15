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
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	string	取消点赞类型【discuss-动态 news-资讯 comment-评论 reply-回复】
     * item_id	是	string	数据id【动态id/资讯id/评论id/回复id】
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
     * 评论列表
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	string	点赞类型【discuss-动态 news-资讯】
     * item_id	是	string	数据id【动态id/资讯id】
     * page	否	int	第几页【不传就是所有数据全部返回】
     * limit	否	int	页面显示的数量 默认20
     */
    @POST("social/commentList")
    Observable<ResponseBody> commentList(@QueryMap Map<String, String> map);

    /**
     * 发表评论
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	string	点赞类型【discuss-动态 news-资讯】
     * item_id	是	string	数据id【动态id/资讯id】
     * content	是	string	评论的内容
     */
    @POST("social/comment")
    Observable<ResponseBody> comment(@QueryMap Map<String, String> map);

    /**
     * 回复
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	string	删除类型【comment-评论 reply-回复】
     * item_id	是	int	评论id/回复id
     */
    @POST("social/removeComment")
    Observable<ResponseBody> removeComment(@QueryMap Map<String, String> map);

    /**
     * 点赞用户列表
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	string	点赞类型【discuss-动态 news-资讯 comment-评论 reply-回复】
     * item_id	是	string	数据id【动态id/资讯id/评论id/回复id】
     * page	否	int	第几页【不传就是所有数据全部返回】
     * limit	否	int	页面显示的数量 默认100
     */
    @POST("social/likeList")
    Observable<ResponseBody> likeList(@QueryMap Map<String, String> map);

}
