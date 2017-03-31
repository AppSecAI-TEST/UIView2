package com.hn.d.valley.main.message.search;

import com.hn.d.valley.main.friend.GroupBean;
import com.hn.d.valley.main.message.query.RecordHitInfo;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/29 11:48
 * 修改人员：hewking
 * 修改时间：2017/03/29 11:48
 * 修改备注：
 * Version: 1.0.0
 */
public  class RecordGroupItem extends GroupItem {

    RecordHitInfo hitInfo;

    public RecordGroupItem(GroupBean bean, RecordHitInfo hitInfo) {
        super(bean);
        this.hitInfo = hitInfo;
    }

    public RecordHitInfo getHitInfo() {
        return hitInfo;
    }
}
