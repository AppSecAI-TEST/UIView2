package com.hn.d.valley.control;

import com.angcyo.uiview.github.luban.Luban;

import io.realm.RealmObject;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/16 14:58
 * 修改人员：Robi
 * 修改时间：2017/05/16 14:58
 * 修改备注：
 * Version: 1.0.0
 */
public class ImageItemRealm extends RealmObject {
    private String path = "";//源文件的路径
    private String thumbPath = "";//压缩后的文件路径
    private String url = "";//网络地址

    public ImageItemRealm() {
    }

    public ImageItemRealm(String path, String thumbPath, String url) {
        this.path = path;
        this.thumbPath = thumbPath;
        this.url = url;
    }

    public ImageItemRealm(Luban.ImageItem imageItem) {
        this(imageItem.path, imageItem.thumbPath, imageItem.url);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
