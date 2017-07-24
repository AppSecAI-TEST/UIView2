package com.hn.d.valley.bean;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/07/24 10:40
 * 修改人员：Robi
 * 修改时间：2017/07/24 10:40
 * 修改备注：
 * Version: 1.0.0
 */
public class GameModel {

    private List<GameBean> data_list;

    public List<GameBean> getData_list() {
        return data_list;
    }

    public void setData_list(List<GameBean> data_list) {
        this.data_list = data_list;
    }

    public static class DataListBean {
    }
}
