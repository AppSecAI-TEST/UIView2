package com.hn.d.valley.bean;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/11 11:24
 * 修改人员：Robi
 * 修改时间：2017/01/11 11:24
 * 修改备注：
 * Version: 1.0.0
 */
public class NearUserBean {

    /**
     * data_list : [{"uid":"50003","username":"憨豆先生","status":"1","avatar":"http://static.bzsns.cn/pic/M00/00/ED/CixiMlgf2JGANVS_AAAYeWy7a6g56.JPEG?w=200&h=200&s=1","sex":"0","signature":"","is_auth":"1","auth_type":"3","auth_desc":"国家体育队姚明","job":"运动员","company":"国家队","industry":"","is_contact":0,"is_blacklist":0,"is_attention":1,"lng":"39.990912172420714","lat":"116.32715863448607","distance":"0","show_distance":"附近","created":"1481694406","show_time":"2016-12-14 13:46"}]
     * data_count : 1
     */

    private int data_count;
    private List<LikeUserInfoBean> data_list;

    public int getData_count() {
        return data_count;
    }

    public void setData_count(int data_count) {
        this.data_count = data_count;
    }

    public List<LikeUserInfoBean> getData_list() {
        return data_list;
    }

    public void setData_list(List<LikeUserInfoBean> data_list) {
        this.data_list = data_list;
    }
}
