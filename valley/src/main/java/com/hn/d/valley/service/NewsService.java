package com.hn.d.valley.service;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/03/23 09:53
 * 修改人员：Robi
 * 修改时间：2017/03/23 09:53
 * 修改备注：
 * Version: 1.0.0
 */
public interface NewsService {
    /**
     * 获取资讯分类列表
     */
    @GET("news/classifylist")
    Observable<ResponseBody> classifylist(@QueryMap Map<String, String> map);

    /**
     * 获取资讯列表接口
     * 参数名	必选	类型	说明
     * classify	否	string	分类[政治、搞笑、娱乐...]为空表示随机分类
     * random	否	int	是否随机，1随机、0正常顺序，默认0
     * amount	否	int	数量，为空则默认10
     * lastid	否	int64	上一次列表中的最小ID，用于分页
     */
    @GET("news/abstract")
    Observable<ResponseBody> abstract_(@QueryMap Map<String, String> map);

    /**
     * 获取资讯详情
     * 参数名	必选	类型	说明
     * id	是	int64	资讯id，必须唯一正确
     * uid	否	int	用户id，提供用户id则可以获取是否收藏等信息
     */
    @GET("news/detail")
    Observable<ResponseBody> detail(@QueryMap Map<String, String> map);

    /**
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * id	是	int64	资讯id
     */
    @GET("news/collect")
    Observable<ResponseBody> collect(@QueryMap Map<String, String> map);

    @GET("news/uncollect")
    Observable<ResponseBody> uncollect(@QueryMap Map<String, String> map);

    /**
     * 参数名	必选	类型	说明
     * type	是	string	操作类别【comment表示给评论点赞、reply表示给回复点赞】
     * id	是	int64	评论或者回复的ID
     * uid	是	int	参与点赞的用户ID
     */
    @GET("news/like")
    Observable<ResponseBody> like(@QueryMap Map<String, String> map);

    @GET("news/unlike")
    Observable<ResponseBody> unlike(@QueryMap Map<String, String> map);

    /**
     * 参数名	必选	类型	说明
     * type	是	string	操作类型【new表示评论资讯、comment表示回复评论】
     * id	是	int64	资讯或者评论的唯一id标识
     * uid	是	int	参与评论或者回复的用户id
     * content	是	string	评论或者回复的具体内容
     */
    @GET("news/reply")
    Observable<ResponseBody> reply(@QueryMap Map<String, String> map);

    /**
     * 参数名	必选	类型	说明
     * type	是	string	获取的列表类型【new表示拉取资讯的回复列表、comment表示拉取评论的回复列表】
     * uid	是	int	用户id
     * id	是	int64	资讯或者评论的唯一标识ID
     * amount	否	int	数量
     * lastid	否	int64	上一次列表中的最小id，没有则从最新开始
     */
    @GET("news/replylist")
    Observable<ResponseBody> replylist(@QueryMap Map<String, String> map);

    /**
     * 参数名	必选	类型	说明
     * type	是	string	操作类型【new表示删除资讯、comment表示删除评论、reply表示删除回复】
     * id	是	int64	需要删除的资讯、回复、评论的id
     */
    @GET("news/delete")
    Observable<ResponseBody> delete(@QueryMap Map<String, String> map);

    @GET("news/collectcount")
    Observable<ResponseBody> collectcount(@QueryMap Map<String, String> map);

    /**
     * 获取已经收藏的资讯列表，默认10条，按收藏时间排序
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * lastid	否	int64	上一次数据中的最小id，用于分页，0表示从最新开始，默认0
     * amount	否	int	个数，默认10
     */
    @GET("news/collectlist")
    Observable<ResponseBody> collectlist(@QueryMap Map<String, String> map);

    /**
     * 该接口接收用户不喜欢的反馈
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * id	是	int64	资讯、评论或者回复的id
     * content	是	string	反馈的内容，具体格式见下
     */
    @GET("news/feedback")
    Observable<ResponseBody> feedback(@QueryMap Map<String, String> map);
}
