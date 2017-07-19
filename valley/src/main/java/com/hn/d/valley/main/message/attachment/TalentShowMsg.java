package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/18
 * 修改人员：cjh
 * 修改时间：2017/7/18
 * 修改备注：
 * Version: 1.0.0
 */
public class TalentShowMsg  extends BaseCustomMsg{

    /**
     * created : 1500282424
     * sex : 1
     * rank :
     * username : weli
     * uid : 50037
     * issue : 1
     * images : http://circleimg.klgwl.com/60047/1499843704111_s_440x419.jpeg
     * msg : 恭喜weli荣膺“恐龙谷最帅”称号
     */

    private int created;
    private String sex;
    private String rank;
    private String username;
    private String uid;
    private String issue;
    private String images;
    private String msg;

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
