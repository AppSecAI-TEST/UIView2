package com.hn.d.valley.bean;

import android.text.TextUtils;

import com.angcyo.uiview.utils.RUtils;

import java.util.ArrayList;
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


    private List<DataListBean> data_list;
    private List<DataListBean> hot_list;
    /**
     * data_count : 5
     * like_users : [{"uid":"50001","avatar":"http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG"}]
     * images :
     */

    private int data_count;
    private String images;
    private List<DataListBean> like_users;

    public List<DataListBean> getData_list() {
        return data_list;
    }

    public void setData_list(List<DataListBean> data_list) {
        this.data_list = data_list;
    }

    public List<DataListBean> getHot_list() {
        return hot_list;
    }

    public void setHot_list(List<DataListBean> hot_list) {
        this.hot_list = hot_list;
    }

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

    public List<DataListBean> getLike_users() {
        return like_users;
    }

    public void setLike_users(List<DataListBean> like_users) {
        this.like_users = like_users;
    }

    public static class DataListBean implements ILikeData {
        /**
         * comment_id : 129
         * uid : 50500
         * content : 叽叽歪歪4
         * reply_cnt : 4
         * like_cnt : 0
         * created : 1485312352
         * username : 呱呱坠地
         * sex : 1
         * avatar : http://avatorimg.klgwl.com/0f0dc142-d0a5-411e-ab8c-bc8d5293f32b
         * grade : 1
         * show_time : 2017-01-25 10:45
         * is_like : 0
         * reply_list : [{"uid":"50505","content":"风力风气","reply_id":"195","username":"胡浩三雄","to_uid":"50507","to_username":"回复谁xxx"},{"uid":"50503","content":"风力风气","reply_id":"194","username":"盖伦","to_uid":"50507","to_username":"回复谁xxx"}]
         * images :
         */

        private String comment_id;
        private String uid;
        private String content;
        private String reply_cnt;
        private String like_cnt;
        private String created;
        private String username;
        private String sex;
        private String avatar;
        private String grade;
        private String show_time;
        private int is_like;
        private String images;
        private List<ReplyListBean> reply_list;
        /**
         * discuss_id : 206
         * is_auth : 1
         * signature :
         */

        private String discuss_id;
        private String is_auth;
        private String signature;

        private boolean isHot = false;
        /**
         * reply_id : 125
         * to_user_id : 50001
         * is_first_level : 1
         * to_user_username : 幽灵
         */

        private String reply_id;
        private String to_user_id;
        private String is_first_level;
        private String to_user_username;
        private List<String> mediaList = new ArrayList<>();
        /**
         * 资讯评论 相关字段
         * id : 11
         * uid : 60006
         * itemid : 2709
         * type : comment
         * reply_cnt : 0
         * like_cnt : 0
         * report_cnt : 0
         * status : 1
         * created : 1486627369
         * is_official : 0
         * is_hot : 0
         * sex : 1
         * grade : 1
         */

        private String id;
        private String itemid;
        private String type;
        private int report_cnt;
        private int status;
        private int is_official;
        private int is_hot;
        /**
         * parent : {"id":"793","uid":"60006","content":"换啦换啦","status":"1","images":"","username":"宋大哥"}
         */

        private ParentBean parent;

        public boolean isHot() {
            return isHot;
        }

        public void setHot(boolean hot) {
            isHot = hot;
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

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
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

        public String getImages() {
            return images;
        }

        public void setImages(String images) {
            this.images = images;
        }

        public List<ReplyListBean> getReply_list() {
            return reply_list;
        }

        public void setReply_list(List<ReplyListBean> reply_list) {
            this.reply_list = reply_list;
        }

        @Override
        public boolean equals(Object obj) {
            if (TextUtils.isEmpty(id)) {
                return TextUtils.equals(comment_id, ((DataListBean) obj).comment_id);
            } else {
                //资讯使用id判断
                return TextUtils.equals(id, ((DataListBean) obj).id);
            }
        }

        public String getDiscuss_id() {
            return discuss_id;
        }

        public void setDiscuss_id(String discuss_id) {
            this.discuss_id = discuss_id;
        }

        public String getIs_auth() {
            return is_auth;
        }

        public void setIs_auth(String is_auth) {
            this.is_auth = is_auth;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        @Override
        public String getLikeCount() {
            return getLike_cnt();
        }

        @Override
        public void setLikeCount(String like_cnt) {
            setLike_cnt(like_cnt);
        }

        @Override
        public int getIsLike() {
            return getIs_like();
        }

        @Override
        public void setIsLike(int is_like) {
            setIs_like(is_like);
        }

        @Override
        public String getDiscussId(String type) {
            if ("comment".equalsIgnoreCase(type)) {
                return getComment_id();
            }
            if ("reply".equalsIgnoreCase(type)) {
                return getReply_id();
            }
            if ("info_comment".equalsIgnoreCase(type)) {
                return getId();
            }
            return getDiscuss_id();
        }

        public String getReply_id() {
            return reply_id;
        }

        public void setReply_id(String reply_id) {
            this.reply_id = reply_id;
        }

        public String getTo_user_id() {
            return to_user_id;
        }

        public void setTo_user_id(String to_user_id) {
            this.to_user_id = to_user_id;
        }

        public String getIs_first_level() {
            return is_first_level;
        }

        public void setIs_first_level(String is_first_level) {
            this.is_first_level = is_first_level;
        }

        public String getTo_user_username() {
            return to_user_username;
        }

        public void setTo_user_username(String to_user_username) {
            this.to_user_username = to_user_username;
        }

        public List<String> getMediaList() {
            mediaList.clear();
            mediaList.addAll(RUtils.split(getImages()));
            return mediaList;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getItemid() {
            return itemid;
        }

        public void setItemid(String itemid) {
            this.itemid = itemid;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getReport_cnt() {
            return report_cnt;
        }

        public void setReport_cnt(int report_cnt) {
            this.report_cnt = report_cnt;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getIs_official() {
            return is_official;
        }

        public void setIs_official(int is_official) {
            this.is_official = is_official;
        }

        public int getIs_hot() {
            return is_hot;
        }

        public void setIs_hot(int is_hot) {
            this.is_hot = is_hot;
        }

        public ParentBean getParent() {
            return parent;
        }

        public void setParent(ParentBean parent) {
            this.parent = parent;
        }

        public static class ReplyListBean {
            private String uid;
            private String content;
            private String reply_id;
            private String username;
            private String to_uid;
            private String images;
            private String to_username;

            public String getImages() {
                return images;
            }

            public void setImages(String images) {
                this.images = images;
            }

            /**
             * uid : 50505
             * content : 风力风气
             * reply_id : 195
             * username : 胡浩三雄
             * to_uid : 50507
             * to_username : 回复谁xxx
             */

            @Override
            public boolean equals(Object obj) {
                return TextUtils.equals(uid, ((DataListBean) obj).uid);
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getReply_id() {
                return reply_id;
            }

            public void setReply_id(String reply_id) {
                this.reply_id = reply_id;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getTo_uid() {
                return to_uid;
            }

            public void setTo_uid(String to_uid) {
                this.to_uid = to_uid;
            }

            public String getTo_username() {
                return to_username;
            }

            public void setTo_username(String to_username) {
                this.to_username = to_username;
            }
        }

        public static class ParentBean {

            /**
             * id : 793
             * uid : 60006
             * content : 换啦换啦
             * status : 1
             * images :
             * username : 宋大哥
             */

            private String id;
            private String uid;
            private String content;
            private String status;
            private String images;
            private String username;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getImages() {
                return images;
            }

            public void setImages(String images) {
                this.images = images;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }
        }
    }
}
