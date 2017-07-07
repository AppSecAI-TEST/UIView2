package com.hn.d.valley.main.message.attachment;

import com.hn.d.valley.bean.GiftBean;

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
public class GiftReceiveMsg extends BaseCustomMsg {


    /**
     * msg : 送了一个冰淇淋
     * created : 1499247439
     * gift_info : {"gift_id":"1","name":"冰淇淋","thumb":"http://avatorimg.klgwl.com/13/13933.png","is_vip":"1","coins":"10"}
     * to_uid : 62280
     */

    private String msg;
    private int created;
    private GiftBean gift_info;
    private String to_uid;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public GiftBean getGift_info() {
        return gift_info;
    }

    public void setGift_info(GiftBean gift_info) {
        this.gift_info = gift_info;
    }

    public String getTo_uid() {
        return to_uid;
    }

    public void setTo_uid(String to_uid) {
        this.to_uid = to_uid;
    }


}
