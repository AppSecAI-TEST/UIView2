package com.hn.d.valley.base.constant;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/14 16:24
 * 修改人员：Robi
 * 修改时间：2016/12/14 16:24
 * 修改备注：
 * Version: 1.0.0
 */
public interface Constant {

    /**
     * 保存默认的语言设置
     */
    String LANG = "lang";

    /**
     * 登录界面信息
     */
    String LOGIN_INFO = "login_info";

    /**
     * 最后一次用户登录的手机号码
     */
    String USER_LOGIN_PHONE = "user_phone";

    /**
     * 用户登录信息
     */
    String USER_ACCOUNT = "user_account";
    String USER_TOKEN = "user_token";
    String USER_AVATAR = "user_avatar";

    /**
     * 抖动时间, 多虑时间之内的事件
     */
    long DEBOUNCE_TIME = 100;
    long DEBOUNCE_TIME_300 = 300;
    long DEBOUNCE_TIME_700 = 700;

    /**
     * 极光注册返回的id
     */
    String JPUSH_ID = "jpush_id";

    /**
     * 需要更新联系人列表
     */
    String TAG_UPDATE_RECENT_CONTACTS = "tag_update_recent_contacts";

    /**
     * 需要更新圈子
     */
    String TAG_UPDATE_CIRCLE = "tag_update_circle";
    /**
     * 需要更新未读消息数量
     */
    String TAG_NO_READ_NUM = "tag_no_read_num";
    int POS_HOME = 0;//恐龙谷
    int POS_FOUND = 200;//发现, 已经被废弃 星期四 2017-8-3
    int POS_CONNECT = 400;//联系人
    int POS_MESSAGE = 2;//消息
    int POS_ME = 3;//我
    int POS_SEEK = 1;//寻觅

    /**
     * Amap
     */
    int DEFAULT_ZOOM_LEVEL = 15;

    /**
     * 接口默认一页返回多少条数据
     */
    int DEFAULT_PAGE_DATA_COUNT = 20;

    /**
     * 保存排序的标签
     */
    String MY_TAGS = "my_tags";
    /**
     * 第一次设置标签, 返回所有的标签都是自己的
     */
    String MY_TAGS_FIRST = "my_tags_first";
    /**
     * 键盘的高度
     */
    String KEYBOARD_HEIGHT = "keyboard_height";

    /**
     * 关注了对方且不是好友，发送添加为联系人请求给对方
     */
    String add_contact = "15";
    /**
     * 单方面关注时 发送给对方消息
     */
    String attenion = "15";
    /**
     * 点赞【动态/评论、回复】
     */
    String like = "14";
    /**
     * 评论
     */
    String comment = "14";
    /**
     * 回复
     */
    String reply = "14";
    /**
     * 转发
     */
    String forward = "14";

    /**
     * 恐龙君消息
     */
    String klj = "13";

    /**
     * 钱包消息
     */
    String wallet = "16";

    /**
     * 热点资讯
     */
    String hot_news = "17";

    /**
     * 默认的头像地址
     */
    String DEFAULE_AVATER = "http://avatorimg.klgwl.com/default/avatar.png";
}
