package com.hn.d.valley.base.iview

import android.text.TextUtils
import com.angcyo.uiview.base.UIBaseRxView
import com.angcyo.uiview.container.ILayout
import com.angcyo.uiview.dialog.UIBottomItemDialog
import com.angcyo.uiview.recycler.adapter.RModelAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.github.chrisbanes.photoview.PhotoView
import com.hn.d.valley.R
import com.hn.d.valley.main.friend.AbsContactItem
import com.hn.d.valley.main.friend.ContactItem
import com.hn.d.valley.main.message.attachment.DynamicDetailAttachment
import com.hn.d.valley.main.message.attachment.DynamicDetailMsg
import com.hn.d.valley.main.message.chat.ChatUIView2.msgService
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew
import com.hn.d.valley.main.message.groupchat.RequestCallback
import com.hn.d.valley.main.message.session.ImageCommandItem
import com.lzy.imagepicker.bean.ImageItem
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import rx.functions.Action3
import java.io.File

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：既能保存图片, 又能转发图片的 长按事件监听
 * 创建人员：Robi
 * 创建时间：2017/06/29 14:39
 * 修改人员：Robi
 * 修改时间：2017/06/29 14:39
 * 修改备注：
 * Version: 1.0.0
 */
class RelayPhotoLongClickListener(iLayout: ILayout<*>) : ImagePagerUIView.SavePhotoLongClickListener(iLayout) {


    override fun onLongClickListener(photoView: PhotoView?, position: Int, item: ImageItem?) {
        if (item != null && item.canSave) {
            UIBottomItemDialog.build()
                    .addItem(mILayout.layout.context.getString(R.string.relay_image)) {
                        //点击发送给好友
                        if (!TextUtils.isEmpty(item.path) && File(item.path).exists()) {
                            //浏览的是本地图片
                            sendPicAndGif(item.path)
                        } else {
                            //浏览的是网络图片
                            Glide.with(mILayout.layout.context.applicationContext)
                                    .load(item.url)
                                    .downloadOnly(object : SimpleTarget<File>() {
                                        override fun onResourceReady(resource: File, glideAnimation: GlideAnimation<in File>) {
                                            sendPicAndGif(resource.absolutePath)
                                        }
                                    })
                        }
                    }
                    .addItem(mILayout.layout.context.getString(R.string.save_image), createSaveClickListener(item))
                    .showDialog(mILayout)
        }
    }

    private fun sendPicAndGif(path : String) {
        ContactSelectUIVIew.start(mILayout, BaseContactSelectAdapter.Options(RModelAdapter.MODEL_SINGLE), null, true) { uiBaseDataView, absContactItems, requestCallback ->
            requestCallback.onSuccess("")
            val contactItem = absContactItems[0] as ContactItem
            val friendBean = contactItem.friendBean
            var type = SessionTypeEnum.P2P
            // 暂时 size > 1 判断 team
            if (absContactItems.size > 1) {
                type = SessionTypeEnum.Team
            }
            val msg = ImageCommandItem.makePicAndGifMsg(path, friendBean.uid, type)
            msgService().sendMessage(msg, false)
        }
    }
}