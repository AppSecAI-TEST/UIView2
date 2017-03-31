package com.hn.d.valley.main.message.query;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/29 11:37
 * 修改人员：hewking
 * 修改时间：2017/03/29 11:37
 * 修改备注：
 * Version: 1.0.0
 */
/**
 * 全文检索记录高亮区域
 */
public class RecordHitInfo {

    public int start;
    public int end;

    public boolean isInvalied() {
        return start == end;
    }
}
