package com.hn.d.valley.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/03/01 09:26
 * 修改人员：Robi
 * 修改时间：2017/03/01 09:26
 * 修改备注：
 * Version: 1.0.0
 */
public class CollectModel {

    /**
     * type : discuss
     * data_count : 4
     */

    private String type;
    /**
     * data_count : 1
     * data_list : [{"uid":"50001","discuss_id":"18","status":"1","tags_name":"","content":"转发动态","media":"","media_type":"1","like_cnt":"0","fav_cnt":"1","comment_cnt":"0","forward_cnt":"0","view_cnt":"0","share_original_type":"discuss","share_original_item_id":"16","parent_item_id":"17","is_top":"0","created":"0","address":"","lng":"","lat":"","is_like":0,"is_collection":1,"original_info":{"status":"1","discuss_id":"16","media":"http://static.bzsns.cn/pic/M00/01/12/CixiMlhTPgCAaDUXAAF8vDAwf2Q184.jpg","media_type":"3","content":"深圳出租房税收上调"},"show_time":"1970-01-01 08:00","user_info":{"uid":"50001","username":"幽灵","sex":"0","avatar":"http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG","is_contact":0,"contact_mark":"","is_attention":0,"is_blacklist":0}}]
     */

    @SerializedName("data_count")
    private int data_count;
    private List<DataListBean> data_list;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
         * uid : 50001
         * discuss_id : 18
         * status : 1
         * tags_name :
         * content : 转发动态
         * media :
         * media_type : 1
         * like_cnt : 0
         * fav_cnt : 1
         * comment_cnt : 0
         * forward_cnt : 0
         * view_cnt : 0
         * share_original_type : discuss
         * share_original_item_id : 16
         * parent_item_id : 17
         * is_top : 0
         * created : 0
         * address :
         * lng :
         * lat :
         * is_like : 0
         * is_collection : 1
         * original_info : {"status":"1","discuss_id":"16","media":"http://static.bzsns.cn/pic/M00/01/12/CixiMlhTPgCAaDUXAAF8vDAwf2Q184.jpg","media_type":"3","content":"深圳出租房税收上调"}
         * show_time : 1970-01-01 08:00
         * user_info : {"uid":"50001","username":"幽灵","sex":"0","avatar":"http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG","is_contact":0,"contact_mark":"","is_attention":0,"is_blacklist":0}
         */

        private String uid;
        private String discuss_id;
        private String status;
        private String tags_name;
        private String content;
        private String media;
        private String media_type;
        private String like_cnt;
        private String fav_cnt;
        private String comment_cnt;
        private String forward_cnt;
        private String view_cnt;
        private String share_original_type;
        private String share_original_item_id;
        private String parent_item_id;
        private String is_top;
        private String created;
        private String address;
        private String lng;
        private String lat;
        private int is_like;
        private int is_collection;
        private OriginalInfoBean original_info;
        private String show_time;
        private UserInfoBean user_info;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getDiscuss_id() {
            return discuss_id;
        }

        public void setDiscuss_id(String discuss_id) {
            this.discuss_id = discuss_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTags_name() {
            return tags_name;
        }

        public void setTags_name(String tags_name) {
            this.tags_name = tags_name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMedia() {
            return media;
        }

        public void setMedia(String media) {
            this.media = media;
        }

        public String getMedia_type() {
            return media_type;
        }

        public void setMedia_type(String media_type) {
            this.media_type = media_type;
        }

        public String getLike_cnt() {
            return like_cnt;
        }

        public void setLike_cnt(String like_cnt) {
            this.like_cnt = like_cnt;
        }

        public String getFav_cnt() {
            return fav_cnt;
        }

        public void setFav_cnt(String fav_cnt) {
            this.fav_cnt = fav_cnt;
        }

        public String getComment_cnt() {
            return comment_cnt;
        }

        public void setComment_cnt(String comment_cnt) {
            this.comment_cnt = comment_cnt;
        }

        public String getForward_cnt() {
            return forward_cnt;
        }

        public void setForward_cnt(String forward_cnt) {
            this.forward_cnt = forward_cnt;
        }

        public String getView_cnt() {
            return view_cnt;
        }

        public void setView_cnt(String view_cnt) {
            this.view_cnt = view_cnt;
        }

        public String getShare_original_type() {
            return share_original_type;
        }

        public void setShare_original_type(String share_original_type) {
            this.share_original_type = share_original_type;
        }

        public String getShare_original_item_id() {
            return share_original_item_id;
        }

        public void setShare_original_item_id(String share_original_item_id) {
            this.share_original_item_id = share_original_item_id;
        }

        public String getParent_item_id() {
            return parent_item_id;
        }

        public void setParent_item_id(String parent_item_id) {
            this.parent_item_id = parent_item_id;
        }

        public String getIs_top() {
            return is_top;
        }

        public void setIs_top(String is_top) {
            this.is_top = is_top;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public int getIs_like() {
            return is_like;
        }

        public void setIs_like(int is_like) {
            this.is_like = is_like;
        }

        public int getIs_collection() {
            return is_collection;
        }

        public void setIs_collection(int is_collection) {
            this.is_collection = is_collection;
        }

        public OriginalInfoBean getOriginal_info() {
            return original_info;
        }

        public void setOriginal_info(OriginalInfoBean original_info) {
            this.original_info = original_info;
        }

        public String getShow_time() {
            return show_time;
        }

        public void setShow_time(String show_time) {
            this.show_time = show_time;
        }

        public UserInfoBean getUser_info() {
            return user_info;
        }

        public void setUser_info(UserInfoBean user_info) {
            this.user_info = user_info;
        }

        public static class OriginalInfoBean {
            /**
             * status : 1
             * discuss_id : 16
             * media : http://static.bzsns.cn/pic/M00/01/12/CixiMlhTPgCAaDUXAAF8vDAwf2Q184.jpg
             * media_type : 3
             * content : 深圳出租房税收上调
             */

            private String status;
            private String discuss_id;
            private String media;
            private String media_type;
            private String content;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getDiscuss_id() {
                return discuss_id;
            }

            public void setDiscuss_id(String discuss_id) {
                this.discuss_id = discuss_id;
            }

            public String getMedia() {
                return media;
            }

            public void setMedia(String media) {
                this.media = media;
            }

            public String getMedia_type() {
                return media_type;
            }

            public void setMedia_type(String media_type) {
                this.media_type = media_type;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }

        public static class UserInfoBean {
            /**
             * uid : 50001
             * username : 幽灵
             * sex : 0
             * avatar : http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG
             * is_contact : 0
             * contact_mark :
             * is_attention : 0
             * is_blacklist : 0
             */

            private String uid;
            private String username;
            private String sex;
            private String avatar;
            private int is_contact;
            private String contact_mark;
            private int is_attention;
            private int is_blacklist;

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
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

            public int getIs_contact() {
                return is_contact;
            }

            public void setIs_contact(int is_contact) {
                this.is_contact = is_contact;
            }

            public String getContact_mark() {
                return contact_mark;
            }

            public void setContact_mark(String contact_mark) {
                this.contact_mark = contact_mark;
            }

            public int getIs_attention() {
                return is_attention;
            }

            public void setIs_attention(int is_attention) {
                this.is_attention = is_attention;
            }

            public int getIs_blacklist() {
                return is_blacklist;
            }

            public void setIs_blacklist(int is_blacklist) {
                this.is_blacklist = is_blacklist;
            }
        }
    }
}
