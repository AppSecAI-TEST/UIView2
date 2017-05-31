package com.hn.d.valley.bean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/31 14:58
 * 修改人员：Robi
 * 修改时间：2017/05/31 14:58
 * 修改备注：
 * Version: 1.0.0
 */
public class ThemeBean {

    /**
     code码	说明
     1001	主题皮肤之黑色
     1002	主题皮肤之绿色
     1003	主题皮肤之蓝色
     2001	聊天气泡之蓝色河马
     2002	聊天气泡之萌萌哒
     2003	聊天气泡之水果系列
     3001	主题背景之圣诞节*/

    /**
     * theme_skin : 1002
     * chat_bubble :
     * theme_cover :
     */

    private String theme_skin;
    private String chat_bubble;
    private String theme_cover;

    public String getTheme_skin() {
        return theme_skin;
    }

    public void setTheme_skin(String theme_skin) {
        this.theme_skin = theme_skin;
    }

    public String getChat_bubble() {
        return chat_bubble;
    }

    public void setChat_bubble(String chat_bubble) {
        this.chat_bubble = chat_bubble;
    }

    public String getTheme_cover() {
        return theme_cover;
    }

    public void setTheme_cover(String theme_cover) {
        this.theme_cover = theme_cover;
    }
}
