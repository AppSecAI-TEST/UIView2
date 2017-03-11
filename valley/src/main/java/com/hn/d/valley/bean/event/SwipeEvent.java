package com.hn.d.valley.bean.event;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：滑动返回事件, 参数是当前滑动的 时差距离
 * 创建人员：Robi
 * 创建时间：2016/12/28 10:23
 * 修改人员：Robi
 * 修改时间：2016/12/28 10:23
 * 修改备注：
 * Version: 1.0.0
 */
public class SwipeEvent {
    public int offsetX;

    public SwipeEvent(int offsetX) {
        this.offsetX = offsetX;
    }
}
