package com.hn.d.valley.main.home.recommend;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/01 08:58
 * 修改人员：Robi
 * 修改时间：2017/04/01 08:58
 * 修改备注：
 * Version: 1.0.0
 */
public interface LoadStatusCallback {
    //开始加载数据
    void onLoadStart();

    //加载结束
    void onLoadEnd();
}
