package com.hn.d.valley.service;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by hewking on 2017/3/9.
 */
public interface GroupChatService {

    /**
     *
     参数名	必选	类型	说明
     uid	是	int	用户id
     to_uid	是	string	添加的用户【不要传自己的uid】【多个用户已,分割；例如50001,50002,50003】
     avatar	是	string	群头像
     */
    @POST("group/add")
    Observable<ResponseBody> add(@QueryMap Map<String, String> map);

    /**
     * uid	是	int	用户id
     invitor	是	int	邀请人【发送二维码的人】
     gid	是	int	群id
     avatar	否	string	群头像
     * @param map
     * @return
     */
    @POST("group/join")
    Observable<ResponseBody> join(@QueryMap Map<String, String> map);

    /**
     * 解散群聊
     * @param map
     * @return
     */
    @POST("group/dissolve")
    Observable<ResponseBody> dissolve(@QueryMap Map<String, String> map);

    /**
     * 退出群聊
     * @param map
     * @return
     */
    @POST("group/leave")
    Observable<ResponseBody> leave(@QueryMap Map<String, String> map);

    /**
     * 邀请好友加入群聊
     * @param map
     * @return
     */
    @POST("group/invite")
    Observable<ResponseBody> invite(@QueryMap Map<String, String> map);

    /**
     * 群主转让
     * @param map
     * @return
     */
    @POST("group/changeOwner")
    Observable<ResponseBody> changeOwner(@QueryMap Map<String, String> map);

    /**
     * 踢人出群
     * @param map
     * @return
     */
    @POST("group/kick")
    Observable<ResponseBody> kick(@QueryMap Map<String, String> map);

    /**
     * 修改/设置群名片
     * @param map
     * @return
     */
    @POST("group/updateNick")
    Observable<ResponseBody> updateNick(@QueryMap Map<String, String> map);

    /**
     * 群公告列表
     * @param map
     * @return
     */
    @POST("group/announcementList")
    Observable<ResponseBody> announcementList(@QueryMap Map<String, String> map);

    /**
     *编辑/设置群公告
     * @param map
     * @return
     */
    @POST("group/setAnnouncement")
    Observable<ResponseBody> setAnnouncement(@QueryMap Map<String, String> map);

    /**
     * 群公告详情
     * @param map
     * @return
     */
    @POST("group/announcementDetail")
    Observable<ResponseBody> announcementDetail(@QueryMap Map<String, String> map);

    /**
     *我的群聊列表
     * @param map
     * @return
     */
    @POST("group/myGroup")
    Observable<ResponseBody> myGroup(@QueryMap Map<String, String> map);

    /**
     *删除群公告
     * @param map
     * @return
     */
    @POST("group/removeAnnouncement")
    Observable<ResponseBody> removeAnnouncement(@QueryMap Map<String, String> map);

    /**
     * 获取群成员列表
     * @param map
     * @return
     */
    @POST("group/groupMember")
    Observable<ResponseBody> groupMember(@QueryMap Map<String, String> map);

    /**
     * 获取群详情
     * @param map
     * @return
     */
    @POST("group/groupInfo")
    Observable<ResponseBody> groupInfo(@QueryMap Map<String, String> map);

    /**
     * 编辑群名称
     * @param map
     * @return
     */
    @POST("group/editGroupName")
    Observable<ResponseBody> editGroupName(@QueryMap Map<String, String> map);

    /**
     * 举报群/群成员
     * @param map
     * @return
     */
    @POST("group/report")
    Observable<ResponseBody> report(@QueryMap Map<String, String> map);

}
