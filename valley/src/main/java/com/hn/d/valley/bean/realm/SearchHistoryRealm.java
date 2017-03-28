package com.hn.d.valley.bean.realm;

import io.realm.RealmObject;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/03/28 16:49
 * 修改人员：Robi
 * 修改时间：2017/03/28 16:49
 * 修改备注：
 * Version: 1.0.0
 */
public class SearchHistoryRealm extends RealmObject {

    /**
     * 搜索的文本
     */
    private String text;

    /**
     * 搜索的时间
     */
    private long time;

    public SearchHistoryRealm() {
    }

    public SearchHistoryRealm(String text, long time) {
        this.text = text;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
