package com.hn.d.valley.bean;

import com.hn.d.valley.bean.realm.AdRealm;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/06/19 11:20
 * 修改人员：Robi
 * 修改时间：2017/06/19 11:20
 * 修改备注：
 * Version: 1.0.0
 */
public class AdModel {

    /**
     * data_list : [{"image":"http://avatorimg.klgwl.com/13/13163.png","content_type":"link","title":"广告","value":""},{"image":"http://circleimg.klgwl.com/62182/62182241_s_564x398.jpeg","content_type":"link","title":"20161216","value":"http://www.baidu.com"}]
     * data_count : 2
     */

    private int data_count;
    private List<AdRealm> data_list;

    public int getData_count() {
        return data_count;
    }

    public void setData_count(int data_count) {
        this.data_count = data_count;
    }

    public List<AdRealm> getData_list() {
        return data_list;
    }

    public void setData_list(List<AdRealm> data_list) {
        this.data_list = data_list;
    }

}
