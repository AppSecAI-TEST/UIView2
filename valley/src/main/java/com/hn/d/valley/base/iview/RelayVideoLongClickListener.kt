package com.hn.d.valley.base.iview

import com.angcyo.uiview.container.ILayout
import com.angcyo.uiview.dialog.UIBottomItemDialog
import com.angcyo.uiview.recycler.adapter.RModelAdapter
import com.angcyo.uiview.utils.T_
import com.angcyo.uiview.utils.string.MD5
import com.hn.d.valley.R
import com.hn.d.valley.ValleyApp
import com.hn.d.valley.main.friend.ContactItem
import com.hn.d.valley.main.message.chat.ChatUIView2
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew.start
import com.hn.d.valley.main.message.session.VideoCommandItem.getVideoMediaPlayer
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import java.io.File

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：既能保存视频, 又能转发视频的 长按事件监听
 * 创建人员：Robi
 * 创建时间：2017/06/29 14:39
 * 修改人员：Robi
 * 修改时间：2017/06/29 14:39
 * 修改备注：
 * Version: 1.0.0
 */
class RelayVideoLongClickListener(iLayout: ILayout<*>) : VideoPlayUIView.SaveVideoLongClickListener(iLayout) {
    private var canSave: Boolean = true

    constructor(iLayout: ILayout<*>, canSave: Boolean) : this(iLayout) {
        this.canSave = canSave
    }

    override fun onLongPress(videoUrl: String?) {
        if (canSave && !videoUrl.isNullOrEmpty()) {
            UIBottomItemDialog.build()
                    .addItem(mILayout.layout.context.getString(R.string.relay_image)) {
                        //点击发送给好友
                        sendVideo(videoUrl!!)
                    }
                    .addItem(mILayout.layout.context.getString(R.string.save_video)) {
                        val file = File(videoUrl)
                        if (file.exists()) {
                            //本地视频
                            saveVideoFile(file)
                        } else {
                            //网络视频
                            saveVideoUrl(videoUrl)
                        }
                    }
                    .showDialog(mILayout)
        } else {
            T_.error("视频非公开，不能保存.")
        }
    }

    private fun sendVideo(path: String) {
        start(mILayout, BaseContactSelectAdapter.Options(RModelAdapter.MODEL_SINGLE), null, true) { _, absContactItems, requestCallback ->
            requestCallback.onSuccess("")
            val contactItem = absContactItems[0] as ContactItem
            val friendBean = contactItem.friendBean
            var type = SessionTypeEnum.P2P
            // 暂时 size > 1 判断 team
            if (absContactItems.size > 1) {
                type = SessionTypeEnum.Team
            }
            0
            val file = File(path)
            if (!file.exists()) {
                return@start
            }

            val mediaPlayer = getVideoMediaPlayer(ValleyApp.getApp().applicationContext, file)
            val duration = (if (mediaPlayer == null) 0 else mediaPlayer.getDuration()).toLong()
            val height = if (mediaPlayer == null) 0 else mediaPlayer.getVideoHeight()
            val width = if (mediaPlayer == null) 0 else mediaPlayer.getVideoWidth()
            val md5 = MD5.getStreamMD5(path)
            val message = MessageBuilder.createVideoMessage(friendBean.uid, type, file, duration, width, height, md5)
            ChatUIView2.msgService().sendMessage(message, false)
        }
    }
}