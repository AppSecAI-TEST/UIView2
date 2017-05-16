package com.hn.d.valley.main.message.attachment;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/15 15:06
 * 修改人员：hewking
 * 修改时间：2017/05/15 15:06
 * 修改备注：
 * Version: 1.0.0
 */
public class HotSpotInfo extends BaseCustomMsg {

    /**
     * msg : 《欢乐颂2》曝终极预告，又甜又虐还透露了结局！
     * news : [{"id":617563,"title":"《欢乐颂2》曝终极预告，又甜又虐还透露了结局！","type":"article","imgs":"http://klg-news.oss-cn-shenzhen.aliyuncs.com/9f40d85daa7afb0b676e436a7813a804.png;http://klg-news.oss-cn-shenzhen.aliyuncs.com/e69ba5595b04fe9afcde3b2403e240bd.png;http://klg-news.oss-cn-shenzhen.aliyuncs.com/57cda8f61997a0b5717601a72368fc4f.png"},{"id":625594,"title":"女友妆前妆后 简直就是二个 世界！","type":"article","imgs":"http://klg-news.oss-cn-shenzhen.aliyuncs.com/0887528221bf43cfd2b9ada7d818ca02.png;http://klg-news.oss-cn-shenzhen.aliyuncs.com/57d810895224129cbdb98c7451871f5c.png;http://klg-news.oss-cn-shenzhen.aliyuncs.com/cd0ceeaecd303e05ff9ca8252fb48bf6.png"}]
     */

    private String msg;
    private List<NewsBean> news;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<NewsBean> getNews() {
        return news;
    }

    public void setNews(List<NewsBean> news) {
        this.news = news;
    }

    public static class NewsBean {
        /**
         * id : 617563
         * title : 《欢乐颂2》曝终极预告，又甜又虐还透露了结局！
         * type : article
         * imgs : http://klg-news.oss-cn-shenzhen.aliyuncs.com/9f40d85daa7afb0b676e436a7813a804.png;http://klg-news.oss-cn-shenzhen.aliyuncs.com/e69ba5595b04fe9afcde3b2403e240bd.png;http://klg-news.oss-cn-shenzhen.aliyuncs.com/57cda8f61997a0b5717601a72368fc4f.png
         */

        private int id;
        private String title;
        private String type;
        private String imgs;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getImgs() {
            return imgs;
        }

        public void setImgs(String imgs) {
            this.imgs = imgs;
        }
    }
}
