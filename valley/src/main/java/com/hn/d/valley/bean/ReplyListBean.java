package com.hn.d.valley.bean;

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
public class ReplyListBean {

    /**
     * data_count : 5
     * data_list : [{"reply_id":"125","comment_id":"1","uid":"50001","to_user_id":"50001","content":"确实不错","is_first_level":"1","like_cnt":"0","created":"1482118971","username":"幽灵","grade":"1","sex":"0","avatar":"http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG","show_time":"12分钟前","is_like":0,"to_user_username":"幽灵"}]
     * like_users : [{"uid":"50001","avatar":"http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG"}]
     * images :
     */

    private int data_count;
    private String images;
    private List<DataListBean> data_list;
    private List<LikeUsersBean> like_users;

    public int getData_count() {
        return data_count;
    }

    public void setData_count(int data_count) {
        this.data_count = data_count;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public List<DataListBean> getData_list() {
        return data_list;
    }

    public void setData_list(List<DataListBean> data_list) {
        this.data_list = data_list;
    }

    public List<LikeUsersBean> getLike_users() {
        return like_users;
    }

    public void setLike_users(List<LikeUsersBean> like_users) {
        this.like_users = like_users;
    }

    public static class DataListBean {
        /**
         * reply_id : 125
         * comment_id : 1
         * uid : 50001
         * to_user_id : 50001
         * content : 确实不错
         * is_first_level : 1
         * like_cnt : 0
         * created : 1482118971
         * username : 幽灵
         * grade : 1
         * sex : 0
         * avatar : http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG
         * show_time : 12分钟前
         * is_like : 0
         * to_user_username : 幽灵
         */

        private String reply_id;
        private String comment_id;
        private String uid;
        private String to_user_id;
        private String content;
        private String is_first_level;
        private String like_cnt;
        private String created;
        private String username;
        private String grade;
        private String sex;
        private String avatar;
        private String show_time;
        private int is_like;
        private String to_user_username;

        public String getReply_id() {
            return reply_id;
        }

        public void setReply_id(String reply_id) {
            this.reply_id = reply_id;
        }

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

        public String getTo_user_id() {
            return to_user_id;
        }

        public void setTo_user_id(String to_user_id) {
            this.to_user_id = to_user_id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getIs_first_level() {
            return is_first_level;
        }

        public void setIs_first_level(String is_first_level) {
            this.is_first_level = is_first_level;
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

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
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

        public String getTo_user_username() {
            return to_user_username;
        }

        public void setTo_user_username(String to_user_username) {
            this.to_user_username = to_user_username;
        }
    }

    public static class LikeUsersBean {
        /**
         * uid : 50001
         * avatar : http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG
         */

        private String uid;
        private String avatar;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
