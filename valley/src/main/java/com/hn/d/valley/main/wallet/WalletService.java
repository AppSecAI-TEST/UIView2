package com.hn.d.valley.main.wallet;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/27 11:15
 * 修改人员：hewking
 * 修改时间：2017/04/27 11:15
 * 修改备注：
 * Version: 1.0.0
 */
public interface WalletService {


    /**钱包功能开户
     参数名	必选	类型	说明
     uid	是	int	用户id
     timestamp	是	int64	当前10位时间戳
     e_type	否	string	API签名编码方式【md5、rsa，默认md5】
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/account/open")
    Observable<ResponseBody> open(@QueryMap Map<String, String> map);


    /**
     *钱包功能停用 (信息并不会变动)
     参数名	必选	类型	说明
     uid	是	int	用户id
     timestamp	是	int64	当前10位时间戳
     e_type	否	string	API签名编码方式【md5、rsa，默认md5】
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/account/close")
    Observable<ResponseBody> close(@QueryMap Map<String, String> map);

    /**
     *查看账户详情
     参数名	必选	类型	说明
     uid	是	int	用户id
     device	是	string	用户当前设备号
     timestamp	是	int64	当前10位时间戳
     e_type	否	string	API签名编码方式【md5、rsa，默认md5】
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/account/check")
    Observable<ResponseBody> account(@QueryMap Map<String, String> map);


    /**设置支付密码
     * uid	是	int	用户id
     password	是	string	支付密码【md5加密】
     oldpassword	是	string	旧支付密码【md5加密，没有设置支付密码则忽略】
     phone	否	string	手机号
     verification_code	否	string	短信验证码，修改密码不需要验证码
     timestamp	是	int64	当前10位时间戳
     e_type	否	string	API签名编码方式【md5、rsa，默认md5】
     sign	是	string	API签名
     成功返回示例
     * @param map
     * @return
     */
    @POST("wallet/account/password/set")
    Observable<ResponseBody> passwordSet(@QueryMap Map<String, String> map);


    /**
     *确认支付密码
     参数名	必选	类型	说明
     uid	是	int	用户id
     password	是	string	支付密码【md5加密】
     timestamp	是	int64	当前10位时间戳
     e_type	否	string	API签名编码方式【md5、rsa，默认md5】
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/account/password/confirm")
    Observable<ResponseBody> passwordConfirm(@QueryMap Map<String, String> map);


    /**设置提现账户
     * uid	是	int	用户id
     type	是	int	账户类型【0支付宝、1微信，没有默认值，必填】
     account	是	string	对应type的账户
     realname	是	string	对应账号的实名姓名，错误填写将无法提现
     e_type	否	string	API签名方式【MD5、RSA，默认MD5】
     timestamp	是	int64	当前10位时间戳
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/account/cashaccount/set")
    Observable<ResponseBody> cashaccountSet(@QueryMap Map<String, String> map);


    /**
     *设置提现账户
     参数名	必选	类型	说明
     uid	是	int	用户id
     type	是	int	账户类型【0支付宝、1微信，没有默认值，必填】
     e_type	否	string	API签名方式【MD5、RSA，默认MD5】
     timestamp	是	int64	当前10位时间戳
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/account/cashaccount/remove")
    Observable<ResponseBody> cashaccountRemove(@QueryMap Map<String, String> map);


    /**本接口用于设置用户的指纹支付开关
     * uid	是	int	用户id
     value	是	int	要设置的指纹支付值【1表示打开、0表示关闭】
     e_type	否	string	API签名编码方式【md5、rsa，默认md5】
     timestamp	是	string	当前10位时间戳
     sign	是	string	API签
     * @param map
     * @return
     */
    @POST("wallet/account/fingerprint/set")
    Observable<ResponseBody> figerprientSet(@QueryMap Map<String, String> map);


    /**********************流水记录相关************************************/


    /**
     *查询资金动向记录
     参数名	必选	类型	说明
     uid	是	int	用户id
     type	否	int	查询类型【0全部、1收入、2支出，默认0】
     lastid	否	int64	上次收到结果中的最小id，用于分页
     limit	否	int	查询个数，默认10
     timestamp	是	int64	当前10位时间戳
     e_type	否	string	API签名编码方式【md5、rsa，默认md5】
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/record/check")
    Observable<ResponseBody> recordCheck(@QueryMap Map<String, String> map);


    /**
     *钱包余额查询
     参数名	必选	类型	说明
     uid	是	int	用户id
     timestamp	是	int64	当前10位时间戳
     e_type	否	string	API签名编码方式【md5、rsa，默认md5】
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/balance/check")
    Observable<ResponseBody> balanceCheck(@QueryMap Map<String, String> map);

    /**
     *余额转账，分为2中模式：一种不需要对方确认，直接到账；另一种需要对方确认，不确认一定时间后将退还金额
     参数名	必选	类型	说明
     uid	是	int	转账用户id
     to_uid	是	int	收款用户id
     money	是	int	转账金额，单位为分
     desc	否	string	操作描述
     confirm	否	int	是否需要对方确认【0不需要、1需要，默认0】
     timestamp	是	int64	当前10位时间戳
     e_type	否	string	API签名编码方式【md5、rsa，默认md5】
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/balance/transfer")
    Observable<ResponseBody> balanceTransfer(@QueryMap Map<String, String> map);


    /**
     *本接口用于查询转账请求的详情
     参数名	必选	类型	说明
     uid	是	int	汇款方用户id
     id	是	string	转账请求的id
     e_type	否	string	API签名加密方式【RSA、MD5，默认MD5】
     timestamp	是	int64	当前10位时间戳
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/balance/transfer/check")
    Observable<ResponseBody> transferCheck(@QueryMap Map<String, String> map);

    /**
     *本接口用于接收汇款方的转账请求
     参数名	必选	类型	说明
     uid	是	int	汇款方用户id
     id	是	string	转账请求的id
     e_type	否	string	API签名加密方式【RSA、MD5，默认MD5】
     timestamp	是	int64	当前10位时间戳
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/balance/collection")
    Observable<ResponseBody> balanceCollection(@QueryMap Map<String, String> map);


    /**
     * 发起余额提现申请，运营人员审核通过后，即可提现
     * uid	是	string	用户id
     money	是	int	提现金额，单位为分
     type	是	int	提现账户的类型【0支付宝、1微信...】
     account	是	string	提现的账户
     e_type	否	string	API编码方式【RSA、MD5】，默认MD5
     timestamp	是	int64	当前10位时间戳
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/balance/cashout_request")
    Observable<ResponseBody> cashoutRequest(@QueryMap Map<String, String> map);


    /**
     *查询自己的提现申请记录
     参数名	必选	类型	说明
     uid	是	int	用户id
     type	是	int	查询的类型【0所有、1成功的、2未成功的，默认0】
     lastid	否	int64	上一次收到的最小id，用于分页
     limit	否	int	个数，默认10
     e_type	否	string	API签名的编码方式
     timestamp	是	int64	当前10位时间戳
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/cashout/check")
    Observable<ResponseBody> cashoutCheck(@QueryMap Map<String, String> map);

    /**
     *运营人员调用，确认用户的提现申请
     参数名	必选	类型	说明
     uid	是	int	用户id
     id	是	int64	提现申请记录id
     timestamp	是	int64	当前10位时间戳
     e_type	否	string	API签名编码方式【md5、rsa，默认md5】
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/cashout/confirm")
    Observable<ResponseBody> cashoutConfirm(@QueryMap Map<String, String> map);

    /**
     * 本接口适用于后台运营人员查看未处理的提现申请
     * limit	否	个数	默认为10
     e_type	否	string	API编码方式【RSA、MD5，默认MD5】
     timestamp	是	int64	当前10位时间戳
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/cashout/unsloved")
    Observable<ResponseBody> cashoutUnsloved(@QueryMap Map<String, String> map);


    /**
     *余额冻结
     参数名	必选	类型	说明
     uid	是	int	用户id
     money	是	int	冻结金额
     desc	否	string	操作描述
     timestamp	是	int64	当前10位时间戳
     e_type	否	string	API签名编码方式【md5、rsa，默认md5】
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/balance/freeze")
    Observable<ResponseBody> balanceFreeze(@QueryMap Map<String, String> map);

    /**
     *余额冻结
     参数名	必选	类型	说明
     uid	是	int	用户id
     money	是	int	冻结金额
     desc	否	string	操作描述
     timestamp	是	int64	当前10位时间戳
     e_type	否	string	API签名编码方式【md5、rsa，默认md5】
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/balance/unfreeze")
    Observable<ResponseBody> balanceUnfreeze(@QueryMap Map<String, String> map);


    /**
     *该接口适用于支付宝充值时，获取我方交易号，并预先设置支付任务
     参数名	必选	类型	说明
     missiontype	是	int	任务类型【0存入余额、1发红包、2转账】
     missionparam	是	json	任务参数，详见下文
     e_type	否	string	API签名编码方式【RSA、MD5，默认MD5】
     timestamp	是	int64	当前10位时间戳
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/balance/recharge/alipay/prepare")
    Observable<ResponseBody> alipayPrepar(@QueryMap Map<String, String> map);

    /**
     *客户端通过该接口获取支付宝接口签名
     参数名	必选	类型	说明
     e_type	否	string	API签名编码方式【RSA、MD5，默认MD5】
     timestamp	是	int64	当前10位时间戳
     sign	是	string	API签名
     以及所有支付宝接口中需要用到的参数	是	string	这些参数将用作生成支付宝接口签名
     * @param map
     * @return
     */
    @POST("wallet/balance/recharge/alipay")
    Observable<ResponseBody> rechargeAlipay(@QueryMap Map<String, String> map);


    /**客户端将第三方充值结果和服务器进行确认
     * username	是	string	用户名
     password	是	string	密码
     name	否	string	昵称
     * @param map
     * @return
     */
    @POST("wallet/balance/recharge/confirm")
    Observable<ResponseBody> rechargeConfirm(@QueryMap Map<String, String> map);

    /**
     *获取验证码
     参数名	必选	类型	说明
     phone	是	string	手机号
     type	是	string	pay_password-找回支付密码、set_pay_password-设置支付密码
     e_type	否	string	API签名编码方式【MD5、RSA，默认MD5】
     timestamp	是	int64	当前10位时间戳
     sign	是	string	API签名
     * @param map
     * @return
     */
    @POST("wallet/sms/sendPhoneVerifyCode")
    Observable<ResponseBody> sendPhoneVerifyCode(@QueryMap Map<String, String> map);


    @POST("coin/records")
    Observable<ResponseBody> records(@QueryMap Map<String, String> map);

    /**
     * 余额消费接口
     * @param map
     * @return
     */
    @POST("wallet/balance/consume")
    Observable<ResponseBody> rechargeKlgcoin(@QueryMap Map<String, String> map);

}
