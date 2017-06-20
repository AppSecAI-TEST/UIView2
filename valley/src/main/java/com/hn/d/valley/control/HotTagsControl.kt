package com.hn.d.valley.control

import com.angcyo.uiview.utils.RUtils
import com.orhanobut.hawk.Hawk

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/06/20 09:35
 * 修改人员：Robi
 * 修改时间：2017/06/20 09:35
 * 修改备注：
 * Version: 1.0.0
 */
object HotTagsControl {
    const val KEY_HOT_TAGS = "key_hot_tags"


    private fun getMyTagsString(): String = Hawk.get(KEY_HOT_TAGS, "")

    fun setMyTagsString(myTags: List<String>) {
        setMyTagsString(RUtils.connect(myTags))
    }

    fun setMyTagsString(myTags: String) {
        Hawk.put(KEY_HOT_TAGS, myTags)
    }

    /**返回我的标签*/
    fun getMyTags(allTag: List<String>): List<String> {
        val myTags = RUtils.split(getMyTagsString())
        if (myTags.isEmpty()) {
            return allTag
        }

        return myTags.filter {
            allTag.contains(it)
        }
    }

    /**返回其他标签*/
    fun getOtherTags(allTag: List<String>, myTag: List<String>): List<String> {
        return allTag.filterNot {
            myTag.contains(it)
        }
    }
}