package com.hn.d.valley.helper;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/26 10:24
 * 修改人员：Robi
 * 修改时间：2016/12/26 10:24
 * 修改备注：
 * Version: 1.0.0
 */
public class RecentContactsHelper {
    private RecentContactsHelper() {
    }

    public static RecentContactsHelper instance() {
        return Holder.instance;
    }

    private static class Holder {
        static RecentContactsHelper instance = new RecentContactsHelper();
    }

}
