package com.hn.d.valley.bean.realm;

import android.text.TextUtils;

import com.hn.d.valley.cache.UserCache;

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
public class SearchMusicHistoryRealm extends RealmObject {

    /**
     * 搜索的文本
     */
    private String text;

    /**
     * 搜索的时间
     */
    private long time;

    private String uid;

    public SearchMusicHistoryRealm() {
        setUid(UserCache.getUserAccount());
    }

    public SearchMusicHistoryRealm(String text, long time) {
        this.text = text;
        this.time = time;
        setUid(UserCache.getUserAccount());
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    @Override
    public boolean equals(Object obj) {
        return TextUtils.equals(((SearchMusicHistoryRealm) obj).getText(), getText());
    }
}
