package com.hn.d.valley.main.message.query;

import com.angcyo.uiview.utils.ContactsPickerHelper;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/23 19:01
 * 修改人员：hewking
 * 修改时间：2017/03/23 19:01
 * 修改备注：
 * Version: 1.0.0
 */
public class ContactSearch {

    public static boolean hitContactInfo(ContactsPickerHelper.ContactsInfo info,TextQuery textQuery){
        String name = info.name;
        String phonenum = info.phone;

        return TextSearcher.contains(textQuery.t9,name,textQuery.text) || TextSearcher.contains(textQuery.t9,phonenum,textQuery.text);
    }

}
