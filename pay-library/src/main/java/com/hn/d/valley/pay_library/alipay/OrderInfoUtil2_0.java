package com.hn.d.valley.pay_library.alipay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class OrderInfoUtil2_0 {

    public  Builder builder;

    public OrderInfoUtil2_0(Builder builder){
        this.builder = builder;
    }

    /**
     * 构造授权参数列表
     *
     * @param pid
     * @param app_id
     * @param target_id
     * @return
     */
    public static Map<String, String> buildAuthInfoMap(String pid, String app_id, String target_id, boolean rsa2) {
        Map<String, String> keyValues = new HashMap<String, String>();

        // 商户签约拿到的app_id，如：2013081700024223
        keyValues.put("app_id", app_id);

        // 商户签约拿到的pid，如：2088102123816631
        keyValues.put("pid", pid);

        // 服务接口名称， 固定值
        keyValues.put("apiname", "com.alipay.account.auth");

        // 商户类型标识， 固定值
        keyValues.put("app_name", "mc");

        // 业务类型， 固定值
        keyValues.put("biz_type", "openservice");

        // 产品码， 固定值
        keyValues.put("product_id", "APP_FAST_LOGIN");

        // 授权范围， 固定值
        keyValues.put("scope", "kuaijie");

        // 商户唯一标识，如：kkkkk091125
        keyValues.put("target_id", target_id);

        // 授权类型， 固定值
        keyValues.put("auth_type", "AUTHACCOUNT");

        // 签名类型
        keyValues.put("sign_type", rsa2 ? "RSA2" : "RSA");

        keyValues.put("method","alipay.open.auth.sdk.code.get");

        //接口名称，常量值为alipay.open.auth.sdk.code.get
        return keyValues;
    }

    public static String biz_content = "{\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"0.01\",\"subject\":\"1\",\"body\":\"我是测试数据\",\"out_trade_no\":\"" + getOutTradeNo() + "\"}";

    public static String biz_content_Json(Builder builder) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("timeout_express", builder.timeout_express);
            jsonObject.put("product_code", "QUICK_MSECURITY_PAY");
            jsonObject.put("total_amount", builder.total_amount);
            jsonObject.put("subject", builder.subject);
            jsonObject.put("body", builder.body);
            jsonObject.put("out_trade_no", builder.out_trade_no);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
    public static class Builder {
        private String timeout_express;
        private String product_code;
        private String total_amount;
        private String subject;
        private String body;
        private String out_trade_no;
        private String app_id;
        private String timestamp;
        private boolean isRSA2;

        public Builder setTimeout(String timeout) {
            this.timeout_express = timeout;
            return this;
        }

        public Builder setProductCode(String product_code) {
            this.product_code = product_code;
            return this;
        }

        public Builder setTotalAmount(String total_amount) {
            this.total_amount = total_amount;
            return this;
        }

        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setOutTradeNo(String out_trade_no) {
            this.out_trade_no = out_trade_no;
            return this;
        }

        public Builder setAppId(String app_id) {
            this.app_id = app_id;
            return this;
        }

        public Builder setTimestamp(String timestamp) {
            this.timestamp = timestamp;
            return  this;
        }

        public Builder setRSA2(boolean isRSA2) {
            this.isRSA2 = isRSA2;
            return this;
        }

        public OrderInfoUtil2_0 create(Builder builder) {
            return new OrderInfoUtil2_0(builder);
        }
    }


    /**
     * 构造支付订单参数列表
     *
     * @return
     */
    public static Map<String, String> buildOrderParamMap(Builder builder) {
        Map<String, String> keyValues = new HashMap<String, String>();

        keyValues.put("app_id", builder.app_id);

        keyValues.put("biz_content", biz_content_Json(builder));

        keyValues.put("charset", "utf-8");

        keyValues.put("method", "alipay.trade.app.pay");

        keyValues.put("sign_type", builder.isRSA2 ? "RSA2" : "RSA");

        keyValues.put("timestamp", builder.timestamp);

        keyValues.put("notify_url", PayConstants.CALLBACKURL);

        keyValues.put("version", "1.0");

        return keyValues;
    }

    /**
     * 构造支付订单参数信息
     *
     * @param map 支付订单参数
     * @return
     */
    public static String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<String>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, true));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, true));

        return sb.toString();
    }

    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param isEncode
     * @return
     */
    private static String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }


    /**
     * 要求外部订单号必须唯一。
     *
     * @return
     */
    private static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

}
