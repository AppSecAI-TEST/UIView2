package com.hn.d.valley.bean;

import com.hn.d.valley.bean.realm.MusicRealm;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/03 17:10
 * 修改人员：Robi
 * 修改时间：2017/05/03 17:10
 * 修改备注：
 * Version: 1.0.0
 */
public class MusicBean {

    /**
     * data_count : 502
     * data_list : [{"song_id":"qq_107192078","name":"告白气球","time":"215","signer":"周杰伦","album":"周杰伦的床边故事","thumb":"http://imgcache.qq.com/music/photo/album_300/91/300_albumpic_1458791_0.jpg","mp3":"http://ws.stream.qqmusic.qq.com/107192078.m4a?fromtag=46"}]
     */

    private int data_count;
    private List<MusicRealm> data_list;

    public int getData_count() {
        return data_count;
    }

    public void setData_count(int data_count) {
        this.data_count = data_count;
    }

    public List<MusicRealm> getData_list() {
        return data_list;
    }

    public void setData_list(List<MusicRealm> data_list) {
        this.data_list = data_list;
    }
}
