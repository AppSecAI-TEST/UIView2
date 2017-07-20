package com.hn.d.valley.main.message.attachment;

import com.hn.d.valley.bean.HotInfoListBean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/31 17:24
 * 修改人员：hewking
 * 修改时间：2017/05/31 17:24
 * 修改备注：
 * Version: 1.0.0
 */
public class ShareNewsMsg extends BaseCustomMsg {


    /**
     * author :
     * logo : http://klg-news.oss-cn-shenzhen.aliyuncs.com/app_material/default_log
     * title : 《双世宠妃》女主被称下一个张天爱刚红就曝恋情
     * type : article
     * item_id : 1318654
     */

    private String author;
    private String logo;
    private String title;
    private String type;
    private String item_id;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public static ShareNewsMsg create(HotInfoListBean mHotInfoListBean) {
        ShareNewsMsg msg = new ShareNewsMsg();
        msg.author = mHotInfoListBean.getAuthor();
        msg.logo = mHotInfoListBean.getLogo();
        msg.title = mHotInfoListBean.getTitle();
        msg.item_id = String.valueOf(mHotInfoListBean.getId());
        msg.extend_type = CustomAttachmentType.SHARE_NEWS;
        msg.type = mHotInfoListBean.getType();
        return msg;
    }
}
