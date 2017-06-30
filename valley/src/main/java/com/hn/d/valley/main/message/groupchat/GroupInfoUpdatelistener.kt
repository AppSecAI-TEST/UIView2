package com.hn.d.valley.main.message.groupchat

/**
 * Created by cjh on 2017/6/29.
 */

interface GroupInfoUpdatelistener {

    fun onGroupMemebeerChanged()

    fun onGroupNameChanged(name : String)

    fun onGroupTop()

    fun onGroupNotifySetting()


}
