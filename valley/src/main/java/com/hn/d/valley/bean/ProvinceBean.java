package com.hn.d.valley.bean;

import com.angcyo.uiview.github.pickerview.model.IPickerViewData;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：省份/城市
 * 创建人员：Robi
 * 创建时间：2017/02/22 10:59
 * 修改人员：Robi
 * 修改时间：2017/02/22 10:59
 * 修改备注：
 * Version: 1.0.0
 */
public class ProvinceBean implements IPickerViewData {

    /**
     * id : 1
     * name : 北京市
     * short_name : 北京
     */

    private String id;
    private String name;
    private String short_name;
    /**
     * province_id : 6
     * abbr : SYS
     */

    private String province_id;
    private String abbr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getProvince_id() {
        return province_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    @Override
    public String getPickerViewText() {
        return short_name;
    }
}
