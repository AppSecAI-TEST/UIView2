package com.hn.d.valley.bean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/26 16:59
 * 修改人员：Robi
 * 修改时间：2017/04/26 16:59
 * 修改备注：
 * Version: 1.0.0
 */
public interface ILikeData {
    String getLikeCount();

    void setLikeCount(String like_cnt);

    int getIsLike();

    void setIsLike(int is_like);

    String getDiscussId();
}
