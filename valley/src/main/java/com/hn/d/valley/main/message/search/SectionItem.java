package com.hn.d.valley.main.message.search;

import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ItemTypes;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/31 14:33
 * 修改人员：hewking
 * 修改时间：2017/03/31 14:33
 * 修改备注：
 * Version: 1.0.0
 */
public class SectionItem extends AbsContactItem {

    public SectionItem(String title) {
        groupText = title;
        itemType = ItemTypes.SECTION;
    }

}
