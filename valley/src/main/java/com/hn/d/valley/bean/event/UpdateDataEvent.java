package com.hn.d.valley.bean.event;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/26 11:04
 * 修改人员：Robi
 * 修改时间：2016/12/26 11:04
 * 修改备注：
 * Version: 1.0.0
 */
public class UpdateDataEvent {
    public int num = 0;
    public int position = 0;

    /**
     * 未读消息数据
     */
    public UpdateDataEvent(int num, int position) {
        this.num = num;
        this.position = position;
    }

    public UpdateDataEvent() {
    }
}
