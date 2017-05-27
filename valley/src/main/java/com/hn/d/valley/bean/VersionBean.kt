package com.hn.d.valley.bean

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：版本检测, 返回的信息
 * 创建人员：Robi
 * 创建时间：2017/05/26 17:37
 * 修改人员：Robi
 * 修改时间：2017/05/26 17:37
 * 修改备注：
 * Version: 1.0.0
 */
data class VersionBean(var version: String = "",
                       var limit_version: String = "",
                       var download_url: String = "",
                       var detail: String = "")