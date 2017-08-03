package com.hn.d.valley.main.message.redpacket;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/27 15:56
 * 修改人员：hewking
 * 修改时间：2017/04/27 15:56
 * 修改备注：
 * Version: 1.0.0
 */
public class GrabedRDDetail {


    /**
     * num : 5
     * content : 恭喜发财
     * money : 100
     * created : 1488278276
     * uid : 60006
     * avatar : http://circleimg.klgwl.com/60006/IMG_20170221_174352.jpg
     * username : 宋大哥
     * result : [{"id":8,"money":4,"created":1488278277,"username":"UI风格","uid":60009,"avatar":"http://circleimg.klgwl.com/58600091484536439.895175","best":0},{"id":7,"money":19,"created":1488278277,"username":"刘创建","uid":60007,"avatar":"http://avatorimg.klgwl.com/18820992517","best":0},{"id":6,"money":33,"created":1488278277,"username":"王法苏","uid":60008,"avatar":"http://avatorimg.klgwl.com/15888779855","best":0},{"id":5,"money":12,"created":1488278277,"username":"姚则国","uid":60010,"avatar":"http://circleimg.klgwl.com/71600101485082114.036955","best":0}]
     */

    private int num;
    private String content;
    private int money;
    private int created;
    private int uid;
    private String avatar;
    private String username;
    private List<ResultBean> result;
    /**
     * grabnum : 2
     * type : 1
     * random : 1
     */

    private int grabnum;
    private int type;
    private int random;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public int getGrabnum() {
        return grabnum;
    }

    public void setGrabnum(int grabnum) {
        this.grabnum = grabnum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRandom() {
        return random;
    }

    public void setRandom(int random) {
        this.random = random;
    }

    public static class ResultBean {
        /**
         * id : 8
         * money : 4
         * created : 1488278277
         * username : UI风格
         * uid : 60009
         * avatar : http://circleimg.klgwl.com/58600091484536439.895175
         * best : 0
         */

        private int id;
        private int money;
        private int created;
        private String username;
        private int uid;
        private String avatar;
        private int best;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public int getCreated() {
            return created;
        }

        public void setCreated(int created) {
            this.created = created;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getBest() {
            return best;
        }

        public void setBest(int best) {
            this.best = best;
        }
    }
}
