package com.hn.d.valley.bean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/6
 * 修改人员：cjh
 * 修改时间：2017/7/6
 * 修改备注：
 * Version: 1.0.0
 */
public class GiftReceiveBean {


    /**
     * gift_id : 2
     * own_count : 4
     * thumb : http://avatorimg.klgwl.com/13/13257.png
     * is_vip : 0
     * enable : 1
     * coins : 0
     */

    private String gift_id;
    private String own_count;
    private String thumb;
    private String is_vip;
    private String enable;
    private String coins;
    /**
     * name : 冰淇淋
     */

    private String name;

    public String getGift_id() {
        return gift_id;
    }

    public void setGift_id(String gift_id) {
        this.gift_id = gift_id;
    }

    public String getOwn_count() {
        return own_count;
    }

    public void setOwn_count(String own_count) {
        this.own_count = own_count;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(String is_vip) {
        this.is_vip = is_vip;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
