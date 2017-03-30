package com.hn.d.valley.bean.realm;

import io.realm.RealmObject;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：保存上传成功过的图片地址
 * 创建人员：Robi
 * 创建时间：2017/03/29 09:24
 * 修改人员：Robi
 * 修改时间：2017/03/29 09:24
 * 修改备注：
 * Version: 1.0.0
 */
public class FileUrlRealm extends RealmObject {
    private String md5;//文件MD5值
    private String absPath;//文件绝对路径
    private String url;//文件网络地址

    public FileUrlRealm() {
    }

    public FileUrlRealm(String md5, String absPath, String url) {
        this.md5 = md5;
        this.absPath = absPath;
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getAbsPath() {
        return absPath;
    }

    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
