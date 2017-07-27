package com.hn.d.valley.base.iview

import android.media.MediaPlayer
import android.net.Uri
import com.angcyo.uiview.container.ILayout
import com.angcyo.uiview.dialog.UIBottomItemDialog
import com.angcyo.uiview.recycler.adapter.RModelAdapter
import com.angcyo.uiview.utils.RUtils
import com.angcyo.uiview.utils.T_
import com.angcyo.uiview.utils.string.MD5
import com.hn.d.valley.R
import com.hn.d.valley.ValleyApp
import com.hn.d.valley.main.friend.ContactItem
import com.hn.d.valley.main.message.attachment.CustomAttachmentType
import com.hn.d.valley.main.message.attachment.OnlineVideoForwardAttachment
import com.hn.d.valley.main.message.attachment.OnlineVideoForwardMsg
import com.hn.d.valley.main.message.chat.ChatUIView2
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew.start
import com.hn.d.valley.main.message.session.VideoCommandItem.getVideoMediaPlayer
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import java.io.File
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL

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
                        sendVideo(videoUrl!!,null)
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

    fun onLongPress(videoUrl: String, thumb: String?) {
        if (canSave && !videoUrl.isNullOrEmpty()) {
            UIBottomItemDialog.build()
                    .addItem(mILayout.layout.context.getString(R.string.relay_image)) {
                        //点击发送给好友
                        sendVideo(videoUrl!!,thumb!!)
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



    private fun sendVideo(path: String ,thumb : String?) {
        start(mILayout, BaseContactSelectAdapter.Options(RModelAdapter.MODEL_SINGLE), null, true) { _, absContactItems, requestCallback ->
            requestCallback.onSuccess("")
            val contactItem = absContactItems[0] as ContactItem
            val friendBean = contactItem.friendBean
            var type = SessionTypeEnum.P2P
            // 暂时 size > 1 判断 team
            if (absContactItems.size > 1) {
                type = SessionTypeEnum.Team
            }

            val file = File(path)
            if (file.exists()) {
                val mediaPlayer = getVideoMediaPlayer(ValleyApp.getApp().applicationContext, file)
                val duration = (if (mediaPlayer == null) 0 else mediaPlayer.getDuration()).toLong()
                val height = if (mediaPlayer == null) 0 else mediaPlayer.getVideoHeight()
                val width = if (mediaPlayer == null) 0 else mediaPlayer.getVideoWidth()
                val md5 = MD5.getStreamMD5(path)
                val message = MessageBuilder.createVideoMessage(friendBean.uid, type, file, duration, width, height, md5)
                ChatUIView2.msgService().sendMessage(message, false)
            } else if (RUtils.isHttpUrl(path) && path.endsWith(".mp4")) {
                // 在线视频
                val mediaPlayer = MediaPlayer.create(ValleyApp.getApp().applicationContext, URL2Uri(path))
                val duration = (if (mediaPlayer == null) 0 else mediaPlayer.getDuration()).toLong()
                val height = if (mediaPlayer == null) 0 else mediaPlayer.getVideoHeight()
                val width = if (mediaPlayer == null) 0 else mediaPlayer.getVideoWidth()
                val msg = OnlineVideoForwardMsg()
                msg.msg = "[视频]"
                msg.extend_type = CustomAttachmentType.ONLINE_FORWARD_VIDEO
                msg.cover = thumb
                msg.videoURL = path
                msg.width = width
                msg.height = height
                val attachment = OnlineVideoForwardAttachment(msg)
                val message = MessageBuilder.createCustomMessage(friendBean.uid, type, friendBean.introduce, attachment)
                ChatUIView2.msgService().sendMessage(message, false)
            }
            T_.show(ValleyApp.getApp().getString(R.string.forward_succeed))

        }
    }

    companion object {
        fun URL2Uri(path: String): Uri? {
            val url: URL
            var uri: Uri? = null
            try {
                url = URL(path)
                uri = Uri.parse(url.toURI().toString())
            } catch (e1: MalformedURLException) {
                e1.printStackTrace()
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            return uri
        }
    }
}