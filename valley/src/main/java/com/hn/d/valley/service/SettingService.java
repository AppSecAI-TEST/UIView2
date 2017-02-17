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
 * 创建时间：2017/02/13 13:58
 * 修改人员：Robi
 * 修改时间：2017/02/13 13:58
 * 修改备注：
 * Version: 1.0.0
 */
public interface SettingService {
    /**
     * 设置他人权限【如允许查看我的动态，是否查看他/她的动态】
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * to_uid	是	int	被设置的用户id【被设置的人不能为自己】
     * key	是	string	1001-允许查看我的动态 1002-是否查看他/她的动态
     * val	是	string	0-不允许 1-允许
     */
    @POST("setting/personal")
    Observable<ResponseBody> personal(@QueryMap Map<String, String> map);

    /**
     * 设置/取消登录保护
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	int	1-开启登录保护 2-取消登录保护
     */
    @POST("setting/loginProtect")
    Observable<ResponseBody> loginProtect(@QueryMap Map<String, String> map);
}
