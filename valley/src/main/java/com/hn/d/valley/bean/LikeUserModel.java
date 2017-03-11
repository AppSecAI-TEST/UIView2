package com.hn.d.valley.bean;

import java.util.List;

/**
 * Created by angcyo on 2017-01-15.
 */

public class LikeUserModel {

    /**
     * data_count : 2
     * data_list : [{"avatar":"http://static.bzsns.cn/pic/M00/00/69/CixiMlctybOAWCO9AAAbZx76WXw10.JPEG?w=200&h=200&s=1","username":"张伟","sex":"0","uid":"50004","grade":"1","is_auth":"1","signature":"","company":"中国共产党","job":"国家主席","industry":"国务院"},{"avatar":"http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG","username":"幽灵","sex":"1","uid":"50001","grade":"1","is_auth":"1","signature":"","company":"红鸟","job":"it","industry":"计算机/互联网/通信/电子"}]
     */

    private int data_count;
    private List<LikeUserInfoBean> data_list;

    public int getData_count() {
        return data_count;
    }

    public void setData_count(int data_count) {
        this.data_count = data_count;
    }

    public List<LikeUserInfoBean> getData_list() {
        return data_list;
    }

    public void setData_list(List<LikeUserInfoBean> data_list) {
        this.data_list = data_list;
    }
}
