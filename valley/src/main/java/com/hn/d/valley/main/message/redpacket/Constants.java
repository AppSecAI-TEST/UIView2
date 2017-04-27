package com.hn.d.valley.main.message.redpacket;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/26 11:53
 * 修改人员：hewking
 * 修改时间：2017/04/26 11:53
 * 修改备注：
 * Version: 1.0.0
 */
public interface Constants {

    /**
     * SUCCESS	0	int	成功
     FAIL_DIRECT	1	int	直接失败，未进入请求队列
     FAIL	2	int	请求失败，可能进入了请求队列，但是还是没有成功
     IN_QUEUE	3	int	正在请求队列，请继续轮询结果
     CAN_BE_GRAB	4	int	红包状态码，表示能够抢
     ALREADY_GRAB	5	int	红包状态码，表示已经抢过
     EXPORE	6	int	红包状态码，表示红包已经过期
     LOOT_OUT	7	int	红包状态码，表示红包已经被抢光
     CAN_NOT_GRAB	8	int	红包状态码，表示不能抢，没有权限
     */

    int SUCCESS =           0;
    int FAIL_DIRECT =       1;
    int FAIL =              2;
    int IN_QUEUE =          3;
    int CAN_BE_GRAB =       4;
    int ALREADY_GRAB =      5;
    int EXPORE =            6;
    int LOOT_OUT =          7;
    int CAN_NOTE_GRAB =     8;

}
