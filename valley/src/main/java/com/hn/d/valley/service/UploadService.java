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
 * 创建时间：2017/06/01 11:58
 * 修改人员：Robi
 * 修改时间：2017/06/01 11:58
 * 修改备注：
 * Version: 1.0.0
 */
public interface UploadService {
    /**
     * 文件上传前检测
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * md5	是	string	json字符串【例如["1e97982e29666f0b6302e64cc58df571","1e97982e29666f0b6302e64cc58df57b"]】
     */
    @POST("upload/checkMd5")
    Observable<ResponseBody> checkMd5(@QueryMap Map<String, String> map);

    /**
     * 文件上传成功
     * <p>
     * 参数名	必选	类型	说明
     * uid	是	int	用户id
     * md5	是	string	json字符串【例如【{"e97982e29666f0b6302e64cc58df57b":"http://circleimg.klgwl.com/50000/1488685209142.jpg_s_1560x2080.jpg"}】
     */
    @POST("upload/success")
    Observable<ResponseBody> success(@QueryMap Map<String, String> map);
}
