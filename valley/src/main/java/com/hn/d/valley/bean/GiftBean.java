package com.hn.d.valley.bean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/5
 * 修改人员：cjh
 * 修改时间：2017/7/5
 * 修改备注：
 * Version: 1.0.0
 */
public class GiftBean {

    /**
     * gift_id : 1
     * name : 冰淇淋
     * thumb : http://avatorimg.klgwl.com/13/13933.png
     * coins : 10
     * is_vip : 0
     * charm
     */

    private String gift_id;
    private String name;
    private String thumb;

    public String getCharm() {
        return charm;
    }

    public void setCharm(String charm) {
        this.charm = charm;
    }

    private String coins;
    private String is_vip;
    private String charm;


    //打赏列表需要用到的礼物id
    private String id;

    public static GiftBean create(GiftReceiveBean receiveBean) {
        GiftBean bean = new GiftBean();
        bean.gift_id = receiveBean.getGift_id();
        bean.name = receiveBean.getName();
        bean.thumb = receiveBean.getThumb();
        bean.coins = receiveBean.getCoins();
        bean.is_vip = receiveBean.getIs_vip();
        bean.charm = receiveBean.getCharm();
        return bean;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGift_id() {
        return gift_id;
    }

    public void setGift_id(String gift_id) {
        this.gift_id = gift_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(String is_vip) {
        this.is_vip = is_vip;
    }
}
