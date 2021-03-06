package com.hn.d.valley.main.message.audio;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/10 18:13
 * 修改人员：hewking
 * 修改时间：2017/04/10 18:13
 * 修改备注：
 * Version: 1.0.0
 */
public interface AudioPlayCallback<T> {

    List<T> getAllData();

    void notifyDataSetChanged();

}
