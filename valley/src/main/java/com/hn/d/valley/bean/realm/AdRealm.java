package com.hn.d.valley.bean.realm;

import io.realm.RealmObject;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/06/19 11:16
 * 修改人员：Robi
 * 修改时间：2017/06/19 11:16
 * 修改备注：
 * Version: 1.0.0
 */
public class AdRealm extends RealmObject {

    /**
     * image : http://avatorimg.klgwl.com/13/13163.png
     * content_type : link
     * title : 广告
     * value :
     */

    private String image;
    private String content_type;
    private String title;
    private String value;
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
