package com.hn.d.valley.start.service;

import com.hn.d.valley.base.Bean;
import com.hn.d.valley.bean.realm.LoginBean;

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
 * 创建时间：2016/12/14 13:43
 * 修改人员：Robi
 * 修改时间：2016/12/14 13:43
 * 修改备注：
 * Version: 1.0.0
 */
public interface StartService {

    /**
     * username	是	string	用户名/昵称【2-20位】
     * pwd	是	string	密码【rsa加密】
     * phone	是	string	手机号【11位】
     * avatar	是	string	头像
     * sex	是	int	性别【1-男 2-女】
     * code	是	string	验证码【6位数字】
     */
    @POST("user/register")
    Observable<Bean<String>> userRegister(@QueryMap Map<String, String> map);


    /**
     * phone	否	string	手机号【第三方登录想此字段为空】
     * pwd	是	string	密码【rsa加密】
     * open_id	否	string	第三方唯一id【第三方登录必填】
     * open_type	否	string	第三方类型【1-qq,2-微信】【第三方登录必填】
     * open_nick	否	string	昵称【第三方登录必填】
     * open_avatar	否	string	头像【第三方登录必填】
     * open_sex	否	int	性别【1-男，2-女，0-保密】【第三方登录必填】
     * push_device_id	是	string	jpush对应的registration_id
     * os_version	是	string	操作系统版本号【如ISO10.1.1】
     */
    @POST("user/login")
    Observable<Bean<LoginBean>> userLogin(@QueryMap Map<String, String> map);

    /**
     * 注册时-感兴趣的人推荐
     *
     uid	用户id	--
     avatar	用户头像	--
     sex	性别	0-保密 1-男 2-女
     username	用户名【昵称】	--
     true_name	真实姓名	--
     is_auth	是否已认证	0-未认证 1-已认证
     auth_type	认证类型	1-职场名人 2-娱乐明星 3-体育人员 4-政府人员
     job	职位	--
     industry	行业	--
     company	公司/经纪公司/所在运动队/组织机构	--
     grade	用户等级	--
     signature	个性签名
     */
    @POST("recommend/interestUser")
    Observable<ResponseBody> interestUser(@QueryMap Map<String,String> map);
}
