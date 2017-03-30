package com.hn.d.valley.bean;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/03/30 15:21
 * 修改人员：Robi
 * 修改时间：2017/03/30 15:21
 * 修改备注：
 * Version: 1.0.0
 */
public class SearchResultBean {

    /**
     * users : [{"id":50029,"username":"那我也","avatar":"http://static.bzsns.cn/pic/MAcIyVbDhw52.J26h=200&s=1","grade":1,"sex":1,"is_auth":0,"signature":""},{"id":50039,"username":"贿赂我","avatar":"http://avatorimg.klgwl.com/07bc6da9-178b-4767-a8f2-ad36b316c1ea","grade":1,"sex":2,"is_auth":0,"signature":""}]
     * news : {"words":["我"],"news":[{"id":5207,"title":"老公和我闺蜜生子，还要将房车给她，却不知正中我下怀","type":"article","images":"http://klg-news.oss-cn-shenzhen.aliyun06b.png","date":1487325300,"form":"","author":"谈客","reply":0},{"id":7770,"title":"孕7月hellp综合征差点要了我和孩子的命","type":"article","images":";http://klg-news.oss-cn-shenzhen.aliyuncsedf.png","date":1487346840,"form":"","author":"播种网","reply":0}]}
     * dynamics : {"words":["我"],"dynamics":[{"id":804,"user_id":50017,"content":"进ink明明ink您快也快也快也快也快我咯我就下咯也快","media_type":3,"media":"http://circleimg.klgwl.com/47500171485226191.286038","created":1485226259,"username":"丰大哥","grade":3,"avatar":"http://circleimg.klgwl.com/36500171484136425.555554","sex":1,"is_auth":1},{"id":1987,"user_id":60008,"content":"说的太他妈的经典了！重要！","media_type":3,"media":"http://circleimg.klgwl.com/600045940_s_510.0x382.0","created":1489045940,"username":"王法苏","grade":1,"avatar":"http://avatorimg.klgwl.com/15888779855","sex":1,"is_auth":1}]}
     */

    private NewsBeanX news;
    private DynamicsBeanX dynamics;
    private List<LikeUserInfoBean> users;

    public NewsBeanX getNews() {
        return news;
    }

    public void setNews(NewsBeanX news) {
        this.news = news;
    }

    public DynamicsBeanX getDynamics() {
        return dynamics;
    }

    public void setDynamics(DynamicsBeanX dynamics) {
        this.dynamics = dynamics;
    }

    public List<LikeUserInfoBean> getUsers() {
        return users;
    }

    public void setUsers(List<LikeUserInfoBean> users) {
        this.users = users;
    }

    public static class NewsBeanX {
        private List<String> words;
        private List<HotInfoListBean> news;

        public List<String> getWords() {
            return words;
        }

        public void setWords(List<String> words) {
            this.words = words;
        }

        public List<HotInfoListBean> getNews() {
            return news;
        }

        public void setNews(List<HotInfoListBean> news) {
            this.news = news;
        }
    }

    public static class DynamicsBeanX {
        private List<String> words;
        private List<DynamicsBean> dynamics;

        public List<String> getWords() {
            return words;
        }

        public void setWords(List<String> words) {
            this.words = words;
        }

        public List<DynamicsBean> getDynamics() {
            return dynamics;
        }

        public void setDynamics(List<DynamicsBean> dynamics) {
            this.dynamics = dynamics;
        }

        public static class DynamicsBean {
            /**
             * id : 804
             * user_id : 50017
             * content : 进ink明明ink您快也快也快也快也快我咯我就下咯也快
             * media_type : 3
             * media : http://circleimg.klgwl.com/47500171485226191.286038
             * created : 1485226259
             * username : 丰大哥
             * grade : 3
             * avatar : http://circleimg.klgwl.com/36500171484136425.555554
             * sex : 1
             * is_auth : 1
             */

            private String id;
            private String user_id;
            private String content;
            private String media_type;
            private String media;
            private long created;
            private String username;
            private String grade;
            private String avatar;
            private String sex;
            private String is_auth;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getGrade() {
                return grade;
            }

            public void setGrade(String grade) {
                this.grade = grade;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public void setIs_auth(String is_auth) {
                this.is_auth = is_auth;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getMedia_type() {
                return media_type;
            }

            public void setMedia_type(String media_type) {
                this.media_type = media_type;
            }

            public String getMedia() {
                return media;
            }

            public void setMedia(String media) {
                this.media = media;
            }

            public long getCreated() {
                return created;
            }

            public void setCreated(long created) {
                this.created = created;
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

            public String getSex() {
                return sex;
            }

            public String getIs_auth() {
                return is_auth;
            }
        }
    }
}
