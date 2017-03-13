package com.hn.d.valley.bean;

import com.hn.d.valley.bean.realm.Tag;

import java.util.List;

public class IndustriesBean {

    /**
     * data_count : 140
     * data_list : [{"id":"11","name":"计算机软件"},{"id":"12","name":"计算机硬件"},{"id":"13","name":"计算机服务(系统、数据服务、维修)"},{"id":"14","name":"通信/电信/网络设备"},{"id":"15","name":"通信/电信运营、增值服务"},{"id":"16","name":"互联网/电子商务"},{"id":"17","name":"网络游戏"},{"id":"18","name":"电子技术/半导体/集成电路"},{"id":"19","name":"仪器仪表/工业自动化"},{"id":"20","name":"会计/审计"}]
     */

    private int data_count;
    private List<Tag> data_list;

    public int getData_count() {
        return data_count;
    }

    public void setData_count(int data_count) {
        this.data_count = data_count;
    }

    public List<Tag> getData_list() {
        return data_list;
    }

    public void setData_list(List<Tag> data_list) {
        this.data_list = data_list;
    }
}