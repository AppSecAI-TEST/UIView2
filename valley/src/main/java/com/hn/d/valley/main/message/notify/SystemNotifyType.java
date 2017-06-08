package com.hn.d.valley.main.message.notify;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/24 11:17
 * 修改人员：hewking
 * 修改时间：2017/04/24 11:17
 * 修改备注：
 * Version: 1.0.0
 */
public interface SystemNotifyType {

    /**
     * 好友发了新动态
     扩展名【extend_type】：

     new_discuss
     */
    String NEW_DISCUSS = "new_discuss";

    /**
     * 新的访客
     扩展名【extend_type】：

     new_visitor
     */
    String NEW_VISITOR = "new_visitor";

    /**
     * 群被迫解散
     扩展名【extend_type】：

     group_dismiss
     */
    String GROUP_DISMISS = "group_dismiss";

    /**
     * 群主添加/修改群公告时发送
     扩展名【extend_type】：

     group_announcement
     */
    String GROUP_ANNOUNCEMENT = "group_announcement";

}
