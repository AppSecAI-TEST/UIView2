package com.hn.d.valley.bean;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/06/22 15:50
 * 修改人员：Robi
 * 修改时间：2017/06/22 15:50
 * 修改备注：
 * Version: 1.0.0
 */
public class CollectInformationListBean {

    /**
     * lastid : 1
     * list : [{"id":630299,"title":"22分15板4助2断2帽！莱昂纳德就算带伤，也是一头猛兽！","date":1494390600,"author":"篮球世家","from":"","classify":"美文","tags":"莱昂纳德;卡哇伊;波波维奇;右膝;钢锯;妖异","type":"article","imgs":"http://klg-news.oss-cn-shenzhen.aliyuncs.com/8b8061c3675938a5da1a54951eb634d5.png","reply_cnt":0,"logo":"http://p3.pstatp.com/large/1bf30013409a78d13038"}]
     */

    private int lastid;
    private List<HotInfoListBean> list;

    public int getLastid() {
        return lastid;
    }

    public void setLastid(int lastid) {
        this.lastid = lastid;
    }

    public List<HotInfoListBean> getList() {
        return list;
    }

    public void setList(List<HotInfoListBean> list) {
        this.list = list;
    }
}
