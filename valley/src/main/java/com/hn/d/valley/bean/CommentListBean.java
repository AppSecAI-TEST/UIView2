package com.hn.d.valley.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/13 19:30
 * 修改人员：Robi
 * 修改时间：2017/01/13 19:30
 * 修改备注：
 * Version: 1.0.0
 */
public class CommentListBean {

    /**
     * data_count : 4
     * data_list : [{"comment_id":"1","uid":"50001","grade":"1","content":"挺不错的哦","reply_cnt":"7","like_cnt":"1","created":"1481970259","username":"幽灵","sex":"0","avatar":"http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG","show_time":"昨天 18:24","is_like":1}]
     */

    private int data_count;
    private List<DataListBean> data_list;

    public int getData_count() {
        return data_count;
    }

    public void setData_count(int data_count) {
        this.data_count = data_count;
    }

    public List<DataListBean> getData_list() {
        return data_list;
    }

    public void setData_list(List<DataListBean> data_list) {
        this.data_list = data_list;
    }

    public static class DataListBean {
        /**
         * comment_id : 1
         * uid : 50001
         * grade : 1
         * content : 挺不错的哦
         * reply_cnt : 7
         * like_cnt : 1
         * created : 1481970259
         * username : 幽灵
         * sex : 0
         * avatar : http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG
         * show_time : 昨天 18:24
         * is_like : 1
         */

        private String comment_id;
        private String uid;
        private String grade;
        private String content;
        private String reply_cnt;
        private String like_cnt;
        private String created;
        private String username;
        private String sex;
        private String avatar;
        private String show_time;
        private int is_like;

        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getReply_cnt() {
            return reply_cnt;
        }

        public void setReply_cnt(String reply_cnt) {
            this.reply_cnt = reply_cnt;
        }

        public String getLike_cnt() {
            return like_cnt;
        }

        public void setLike_cnt(String like_cnt) {
            this.like_cnt = like_cnt;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getShow_time() {
            return show_time;
        }

        public void setShow_time(String show_time) {
            this.show_time = show_time;
        }

        public int getIs_like() {
            return is_like;
        }

        public void setIs_like(int is_like) {
            this.is_like = is_like;
        }

        @Override
        public boolean equals(Object obj) {
            return TextUtils.equals(comment_id, ((DataListBean) obj).comment_id);
        }
    }
}
