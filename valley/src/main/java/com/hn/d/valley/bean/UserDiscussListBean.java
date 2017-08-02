package com.hn.d.valley.bean;

import android.text.TextUtils;

import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.RUtils;
import com.hn.d.valley.control.PublishTaskRealm;
import com.hn.d.valley.sub.user.DynamicType;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/05 18:11
 * 修改人员：Robi
 * 修改时间：2017/01/05 18:11
 * 修改备注：
 * Version: 1.0.0
 */
public class UserDiscussListBean {

    /**
     * data_count : 12
     * data_list : [{"discuss_id":"14","uid":"50004","tags_name":"搞笑,励志,感动","content":"深圳市恐龙谷网络科技有限公司","media":"http://static.bzsns.cn/pic/M00/01/12/CixiMlhTPgCAaDUXAAF8vDAwf2Q184.jpg","media_type":"3","like_cnt":"0","fav_cnt":"0","comment_cnt":"0","forward_cnt":"0","view_cnt":"0","share_original_type":"","share_original_item_id":"0","parent_item_id":"0","is_top":"1","created":"1481871094","address":"深圳大冲国际","lng":"113.961974","lat":"22.547832","user_info":{"uid":"50004","username":"大鹏","sex":"0","avatar":"http://static.bzsns.cn/pic/M00/00/69/CixiMlctybOAWCO9AAAbZx76WXw10.JPEG?w=200&h=200&s=1","grade":"1","is_contact":1,"contact_mark":"张伟","is_attention":1},"is_like":1,"is_collection":1,"original_info":[],"show_time":"2小时前"}]
     */

    private int data_count;
    private int last_id;
    private List<DataListBean> data_list;
    /**
     * type : news
     */

    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLast_id() {
        return last_id;
    }

    public void setLast_id(int last_id) {
        this.last_id = last_id;
    }

    public static class DataListBean implements RExBaseAdapter.ObjectEmpty, ILikeData {
        public String uuid;
        public int publishStatus = PublishTaskRealm.STATUS_NORMAL;
        boolean isEmpty = false;
        /**
         * discuss_id : 14
         * uid : 50004
         * tags_name : 搞笑,励志,感动
         * content : 深圳市恐龙谷网络科技有限公司
         * media : http://static.bzsns.cn/pic/M00/01/12/CixiMlhTPgCAaDUXAAF8vDAwf2Q184.jpg
         * media_type : 3
         * like_cnt : 0
         * fav_cnt : 0
         * comment_cnt : 0
         * forward_cnt : 0
         * view_cnt : 0
         * share_original_type :
         * share_original_item_id : 0
         * parent_item_id : 0
         * is_top : 1
         * created : 1481871094
         * address : 深圳大冲国际
         * lng : 113.961974
         * lat : 22.547832
         * user_info : {"uid":"50004","username":"大鹏","sex":"0","avatar":"http://static.bzsns.cn/pic/M00/00/69/CixiMlctybOAWCO9AAAbZx76WXw10.JPEG?w=200&h=200&s=1","grade":"1","is_contact":1,"contact_mark":"张伟","is_attention":1}
         * is_like : 1
         * is_collection : 1
         * original_info : []
         * show_time : 2小时前
         */

        private String discuss_id;
        private String uid;
        private String tags_name;
        private String content;
        private String media;
        private String media_type;
        private String like_cnt = "0";
        private String fav_cnt = "0";
        private String comment_cnt = "0";
        private String forward_cnt = "0";
        private String view_cnt = "0";
        private String reward_cnt = "0";
        private String share_original_type;
        private String share_original_item_id;
        private String parent_item_id;
        private String is_top = "0";
        private String created = String.valueOf(System.currentTimeMillis());
        private String address;
        private String lng;
        private String lat;
        private LikeUserInfoBean user_info;
        private int is_like;
        private int is_collection;
        private String show_time = "刚刚";
        private OriginalInfo original_info;
        /**
         * is_collect : 1
         * like_users : [{"uid":"50001","avatar":"http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG"}]
         */

        private int is_collect;
        private List<LikeUserInfoBean> like_users;
        /**
         * scan_type : 1
         */

        private String scan_type = "1";
        /**
         * status : 1
         */

        private String status;
        /**
         * 资讯相关字段
         * news_id : 1
         * title : 111
         * type : article
         * from_platform :
         * original_url :
         * publish_time : 0
         * author :
         */

        private String news_id;
        private String title;
        private String type;
        private String from_platform;
        private String original_url;
        private String publish_time;
        private String author;
//        private Object original_info;
        /**
         * allow_download : 1
         */

        private String allow_download;
        /**
         * package_id :
         * is_recommend : 1
         */

        private String package_id;
        private String is_recommend;


        public DataListBean() {
        }

        public DataListBean(boolean isEmpty) {
            this.isEmpty = isEmpty;
        }

        public String getDiscuss_id() {
            return discuss_id;
        }

        public void setDiscuss_id(String discuss_id) {
            this.discuss_id = discuss_id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
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

        public String[] getMediaValue() {
            String[] result = new String[]{"", ""};
            if (getMediaList().isEmpty()) {
                return result;
            }
            String[] split = getMediaList().get(0).split("\\?");
            if (split.length > 0) {
                result[0] = split[0];
            }
            if (split.length > 1) {
                result[1] = split[1];
            }

            return split;
        }

        /**
         * 是否可以转发
         */
        public boolean canForward() {
            return "1".equalsIgnoreCase(scan_type);
        }

        public List<String> getMediaList() {
            return RUtils.split(getMedia());
        }

        public String getMedia_type() {
            return media_type;
        }

        public void setMedia_type(String media_type) {
            this.media_type = media_type;
        }

        public boolean isImageMediaType() {
            return "3".equalsIgnoreCase(getMedia_type());
        }

        public boolean isVideoMediaType() {
            return "2".equalsIgnoreCase(getMedia_type());
        }

        public boolean isVoiceMediaType() {
            return "4".equalsIgnoreCase(getMedia_type());
        }

        public boolean isTextMediaType() {
            return "1".equalsIgnoreCase(getMedia_type());
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

        public boolean isForwardInformation() {
            return "news".equalsIgnoreCase(getShare_original_type());
        }

        public OriginalInfo getOriginal_info() {
            return original_info;
        }

//        public String getOriginal_info() {
//            return original_info;
//        }
//
//        public void setOriginal_info(String original_info) {
//            this.original_info = original_info;
//        }

        public void setOriginal_info(OriginalInfo original_info) {
            this.original_info = original_info;
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

        public LikeUserInfoBean getUser_info() {
            return user_info;
        }

        public void setUser_info(LikeUserInfoBean user_info) {
            this.user_info = user_info;
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

        public String getShow_time() {
            return show_time;
        }

        public void setShow_time(String show_time) {
            this.show_time = show_time;
        }

        @Override
        public boolean isDataEmpty() {
            return isEmpty;
        }

        public int getIs_collect() {
            return is_collect;
        }

        public void setIs_collect(int is_collect) {
            this.is_collect = is_collect;
        }

        public List<LikeUserInfoBean> getLike_users() {
            return like_users;
        }

        public void setLike_users(List<LikeUserInfoBean> like_users) {
            this.like_users = like_users;
        }

        public String getScan_type() {
            return scan_type;
        }

        public void setScan_type(String scan_type) {
            this.scan_type = scan_type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNews_id() {
            return news_id;
        }

        public void setNews_id(String news_id) {
            this.news_id = news_id;
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

        public String getFrom_platform() {
            return from_platform;
        }

        public void setFrom_platform(String from_platform) {
            this.from_platform = from_platform;
        }

        public String getOriginal_url() {
            return original_url;
        }

        public void setOriginal_url(String original_url) {
            this.original_url = original_url;
        }

        public String getPublish_time() {
            return publish_time;
        }

        public void setPublish_time(String publish_time) {
            this.publish_time = publish_time;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getAllow_download() {
            return allow_download;
        }

        public void setAllow_download(String allow_download) {
            this.allow_download = allow_download;
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
//            if ("comment".equalsIgnoreCase(type)) {
//                return getComment_id();
//            }
//            if ("reply".equalsIgnoreCase(type)) {
//                return getReply_id();
//            }
            return getDiscuss_id();
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        @Override
        public boolean equals(Object obj) {
            return TextUtils.equals(uuid, ((DataListBean) obj).uuid);
        }

        /**
         * 是否有红包
         */
        public boolean haveHotPackage() {
            return !TextUtils.isEmpty(getPackage_id());
        }

        public String getPackage_id() {
            return package_id;
        }

        public void setPackage_id(String package_id) {
            this.package_id = package_id;
        }

        public String getIs_recommend() {
            return is_recommend;
        }

        public void setIs_recommend(String is_recommend) {
            this.is_recommend = is_recommend;
        }

        public String getReward_cnt() {
            return reward_cnt;
        }

        public void setReward_cnt(String reward_cnt) {
            this.reward_cnt = reward_cnt;
        }

        public static class OriginalInfo {

            boolean isForwardInformation = false;
            /**
             * status : 1
             * discuss_id : 180
             * uid : 60004
             * media : http://circleimg.klgwl.com/17600041484224471.016469
             * media_type : 3
             * content : 厉害👍
             * username : 蒲公英
             * avatar : http://avatorimg.klgwl.com/18617036401
             */

            private String status = "1";
            private String discuss_id;
            private String uid;
            private String media;
            private String media_type;
            private String content;
            private String username;
            private String avatar;
            /**
             * content : 转发资讯
             * title : 习近平今天会见了特兰普
             * news_id : 1002
             * media : http://circleimg.klgwl.com/11.jpg
             * media_type : 3
             * logo : http://circleimg.klgwl.com/11.jpg
             * author : 头条新闻
             */

            private String title;
            private String news_id;
            private String logo;
            private String author;

            public String getInformationVideoUrl() {
                try {
                    String media = getMedia();
                    return media.substring(media.lastIndexOf('?') + 1, media.length());
                } catch (Exception e) {
                    return media;
                }
            }

            public String getInformationVideoThumbUrl() {
                try {
                    String media = getMedia();
                    return media.substring(0, media.lastIndexOf('?'));
                } catch (Exception e) {
                    return media;
                }
            }

            public boolean isPicture() {
                return "picture".equalsIgnoreCase(media_type) || DynamicType.isImage(media_type);
            }

            public boolean isVideo() {
                return "video".equalsIgnoreCase(media_type) || DynamicType.isVideo(media_type);
            }

            public boolean isInformationVideoType() {
                return "2".equalsIgnoreCase(getMedia_type()) || "video".equalsIgnoreCase(getMedia_type());
            }

            public boolean isForwardInformation() {
                return isForwardInformation;
            }

            public void setForwardInformation(boolean forwardInformation) {
                isForwardInformation = forwardInformation;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getNews_id() {
                return news_id;
            }

            public void setNews_id(String news_id) {
                this.news_id = news_id;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

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

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getMedia() {
                if (TextUtils.isEmpty(media)) {
                    return "";
                }
                return media.replaceAll(";", ",");
            }

            public void setMedia(String media) {
                this.media = media;
            }

            public List<String> getMediaList() {
                return RUtils.split(getMedia());
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

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }
    }
}
