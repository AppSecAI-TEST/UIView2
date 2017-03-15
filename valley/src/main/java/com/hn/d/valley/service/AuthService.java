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
 * 创建时间：2017/03/13 15:21
 * 修改人员：Robi
 * 修改时间：2017/03/13 15:21
 * 修改备注：
 * Version: 1.0.0
 */
public interface AuthService {
    /**
     * 名人认证申请
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * type	是	int	认证类别【1-职场名人，2-娱乐明星，3-体育人物，4-政府人员】
     * true_name	是	string	真实姓名
     * id_card	是	string	身份证号
     * phone	是	string	手机号
     * code	是	string	验证码
     * introduce	是	string	人物介绍
     * website	否	string	个人链接
     * card_front	是	string	身份证正面图片地址
     * card_back	是	string	身份证反面图片地址
     * desc	否	string	认证说明
     * proof	否	string	认证材料【多张图片以，分割】
     * industry_id	否	int	职场名人认证时必填
     * company	是	string	公司/经纪公司/所在运动队/组织机构
     * job	是	string	职业/职位
     */
    @POST("auth/apply")
    Observable<ResponseBody> apply(@QueryMap Map<String, String> map);

    /**
     * 认证详情
     * <p>
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     */
    @POST("auth/detail")
    Observable<ResponseBody> detail(@QueryMap Map<String, String> map);
}
