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

    /**
     * 转发
     * 参数：
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	string	转发类型【discuss-动态 news-资讯 user-名片 group-群】
     * item_id	是	int	数据id【动态id/资讯id/用户id/群id】
     * content	否	string	转发的内容
     */
    @POST("social/forward")
    Observable<ResponseBody> forward(@QueryMap Map<String, String> map);

    /**
     * 举报原因列表
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	string	discuss-动态 group-群聊
     */
    @POST("social/getReportReason")
    Observable<ResponseBody> getReportReason(@QueryMap Map<String, String> map);

    /**
     * 举报
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	string	举报类型【discuss-动态 comment-评论 reply-回复】
     * item_id	是	int	数据id【动态id/评论id/回复id】
     * reason_id	是	int	举报原因id
     */
    @POST("social/report")
    Observable<ResponseBody> report(@QueryMap Map<String, String> map);

    /**
     * 我的收藏
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	否	string	类型【discuss-动态 news-资讯】；如果为空则返回数量列表
     * page	否	int	第几页【不传就是所有数据全部返回】
     * limit	否	int	页面显示的数量 默认20
     */
    @POST("social/myCollect")
    Observable<ResponseBody> myCollect(@QueryMap Map<String, String> map);

    /**
     * 动态浏览列表
     * <p>
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * discuss_id	是	int	动态id
     * page	否	int	默认第一页
     * limit	否	int	页面显示的数量 默认20
     */
    @POST("social/readList")
    Observable<ResponseBody> readList(@QueryMap Map<String, String> map);

}
