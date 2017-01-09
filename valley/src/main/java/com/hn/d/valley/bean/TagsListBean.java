package com.hn.d.valley.bean;

import com.hn.d.valley.control.TagsControl;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/09 16:16
 * 修改人员：Robi
 * 修改时间：2017/01/09 16:16
 * 修改备注：
 * Version: 1.0.0
 */

public class TagsListBean {
    /**
     * result : 1
     * data : [{"id":"1","name":"搞笑"},{"id":"2","name":"励志"},{"id":"3","name":"感动"},{"id":"4","name":"情感"},{"id":"5","name":"生活"}]
     */

    private int result;
    private List<TagsControl.Tag> data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<TagsControl.Tag> getData() {
        return data;
    }

    public void setData(List<TagsControl.Tag> data) {
        this.data = data;
    }

}
