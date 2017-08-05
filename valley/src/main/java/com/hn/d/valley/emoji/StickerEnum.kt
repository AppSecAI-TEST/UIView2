package com.netease.nimlib.sdk.msg.constant

import com.hn.d.valley.emoji.StickerManager.*

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：表情类型
 * 创建人员：hewking
 * 创建时间：2017/05/09 15:34
 * 修改人员：hewking
 * 修改时间：2017/05/09 15:34
 * 修改备注：
 * Version: 1.0.0
 */
enum class StickerEnum (val type: String,val value: Int) {
    None("none",-1),

    /**
     * 红鸟表情
     */
    HN_EXPRESSION(CATEGORY_HN,1),

    /**
     * 恐龙谷表情
     */
    KLG_EXPRESSION(CATEGORY_EXPRESSION,0),

    /**
     * 骰子
     */
    DICE_EXPRESSION(DICE,3),

    /**
     * 扑克牌
     */
    POCKER_EXPRESSION(POCKER,4);


    companion object {

        fun typeOfValue(value: Int): StickerEnum {
            for (e in values()) {
                if (e.value == value) {
                    return e
                }
            }
            return KLG_EXPRESSION
        }

        fun valueOfType(type : String) : StickerEnum{
            for (e in values()) {
                if (e.type .equals(type)) {
                    return e
                }
            }
            return KLG_EXPRESSION
        }
    }
}
