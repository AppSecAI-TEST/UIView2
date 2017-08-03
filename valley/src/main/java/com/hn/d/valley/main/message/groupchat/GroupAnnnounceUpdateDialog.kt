package com.hn.d.valley.main.message.groupchat

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.angcyo.uiview.base.UIIDialogImpl
import com.angcyo.uiview.container.UIParam
import com.angcyo.uiview.utils.TimeUtil
import com.angcyo.uiview.widget.ItemInfoLayout
import com.angcyo.uiview.widget.RTextView
import com.hn.d.valley.R
import com.hn.d.valley.cache.TeamDataCache
import com.hn.d.valley.cache.UserCache
import com.hn.d.valley.main.message.notify.GroupAnnounceNotification
import com.hn.d.valley.main.message.setImageUrl
import com.hn.d.valley.widget.HnButton
import com.netease.nimlib.sdk.team.model.Team


/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/06/14 11:41
 * 修改人员：hewking
 * 修改时间：2017/06/14 11:41
 * 修改备注：
 * Version: 1.0.0
 */

class GroupAnnnounceUpdateDialog(sessionid : String ,notification : GroupAnnounceNotification) : UIIDialogImpl(){

    val notification = notification
    val mSessionId = sessionid

    var isSelfAdmin : Boolean = false

    //view
    var rootlayout : LinearLayout? = null
    var title_info : ItemInfoLayout? = null
//    var user_detail : ItemInfoLayout? = null
    var tv_announcement : TextView? = null
    var btn_known : HnButton? = null
    var rl_user_info : RelativeLayout? = null

    init {
        loadTeamInfo()
    }

    override fun inflateDialogView(dialogRootLayout: FrameLayout?, inflater: LayoutInflater?): View {
        setGravity(Gravity.CENTER)
        return inflater!!.inflate(R.layout.dialog_group_announce_update,dialogRootLayout)
    }

    override fun loadContentView(rootView: View?) {
        super.loadContentView(rootView)
        if (rootView == null) return

        rootlayout = rootView.findViewById(R.id.base_dialog_root_layout) as LinearLayout?
        title_info = rootView.findViewById(R.id.info_title) as ItemInfoLayout
//        user_detail = rootView.findViewById(R.id.info_user_detail) as ItemInfoLayout
        tv_announcement = rootView.findViewById(R.id.tv_announcement) as TextView
        btn_known = rootView.findViewById(R.id.text_view) as HnButton
        rl_user_info = rootView.findViewById(R.id.info_user_detail) as RelativeLayout

        val user_ico : ImageView = rootView.findViewById(R.id.iv_user_ico) as ImageView
        val username : RTextView = rootView.findViewById(R.id.tv_username) as RTextView
        val time : RTextView = rootView.findViewById(R.id.tv_time) as RTextView

        user_ico.setImageUrl(notification.avatar)

        title_info?.setLeftDrawPadding(15)
        title_info?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(GroupMemberUIVIew.GID, notification.gid)
            bundle.putBoolean(GroupMemberUIVIew.IS_ADMIN, isSelfAdmin)
            val param = UIParam().setBundle(bundle)
            mParentILayout.startIView(GroupAnnouncementUIView(), param)
            finishDialog()

        }

        title_info?.setLeftDrawableRes(R.drawable.announcement_icon)
        title_info?.setItemText(mActivity.getString(R.string.text_group_announcement))
        title_info?.setItemDarkText(mActivity.getString(R.string.more))

        username.text = notification.username
        time.text = TimeUtil.getTimeShowString(notification.created.toLong() * 1000,true)

//        user_detail?.setItemText(TimeUtil.getTimeShowString(notification.created.toLong() * 1000,true) )
        tv_announcement?.text = notification.content

        btn_known?.setOnClickListener {
            finishIView()
        }



    }

    /**
     * 初始化群组基本信息
     */
    private fun loadTeamInfo() {
        val t = TeamDataCache.getInstance().getTeamById(mSessionId)
        if (t != null) {
            updateTeamInfo(t)
        } else {
            TeamDataCache.getInstance().fetchTeamById(mSessionId) { success, result ->
                if (success && result != null) {
                    updateTeamInfo(result)
                } else {
                }
            }
        }
    }


    private fun updateTeamInfo(t: Team?) {
        if (t == null) {
            finishIView()
            return
        } else {
            if (t.creator == UserCache.getUserAccount()) {
                isSelfAdmin = true
            }

        }
    }

//    val onDataLoadListener : DialogInterface.OnClickListener by lazy {
//
//
//    }


}

