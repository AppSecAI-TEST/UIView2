package com.hn.d.valley.main.friend

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.angcyo.uiview.base.UIIDialogImpl
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.RSubscriber
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.skin.SkinHelper
import com.angcyo.uiview.widget.RTextView
import com.hn.d.valley.R
import com.hn.d.valley.base.Param
import com.hn.d.valley.bean.realm.UserInfoBean
import com.hn.d.valley.main.message.attachment.PersonalCard
import com.hn.d.valley.main.message.session.SessionHelper
import com.hn.d.valley.main.message.setImageUrl
import com.hn.d.valley.main.message.setThumbUrl
import com.hn.d.valley.service.UserService
import com.hn.d.valley.widget.HnGenderView
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import rx.functions.Action0

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/24
 * 修改人员：cjh
 * 修改时间：2017/7/24
 * 修改备注：
 * Version: 1.0.0
 */
class PersonCardUIDialog(val card: PersonalCard,val action : Action0) : UIIDialogImpl() {

    lateinit var fans_count : RTextView
    lateinit var chars_count : RTextView
    lateinit var grade : HnGenderView
    lateinit var auth_iview : ImageView

    override fun inflateDialogView(dialogRootLayout: FrameLayout?, inflater: LayoutInflater?): View {
        setGravity(Gravity.CENTER)
        return inflater!!.inflate(R.layout.show_personcard_dialog_layout,dialogRootLayout)
    }

    override fun loadContentView(rootView: View?) {
        super.loadContentView(rootView)

        val iv_finish = rootView?.findViewById(R.id.iv_finish) as ImageView
        val iv_thumb = rootView.findViewById(R.id.iv_thumb) as ImageView
        val username = rootView.findViewById(R.id.username) as RTextView
        fans_count = rootView.findViewById(R.id.fans_count) as RTextView
        chars_count = rootView.findViewById(R.id.chars_count) as RTextView
        grade = rootView.findViewById(R.id.grade) as HnGenderView
        auth_iview = rootView.findViewById(R.id.auth_iview) as ImageView
        val tv_talk = rootView.findViewById(R.id.tv_cancel) as TextView
        val tv_follow = rootView.findViewById(R.id.tv_ok) as TextView

        tv_talk.setBackground(SkinHelper.getSkin().themeMaskBackgroundRoundSelector)
        tv_follow.setBackground(SkinHelper.getSkin().themeMaskBackgroundRoundSelector)

        iv_finish.setOnClickListener {
            finishDialog()
        }

        tv_talk.setOnClickListener {
            SessionHelper.startP2PSession(mParentILayout, card.getUid(), SessionTypeEnum.P2P)
            finishDialog()
        }

        tv_follow.setOnClickListener {
            // 关注

        }

        username.setText(card.username)
        iv_thumb.setThumbUrl(card.avatar)

        loadData()

    }

    private fun loadData() {
        RRetrofit.create(UserService::class.java)
                .userInfo(Param.buildMap("to_uid:" + card.uid))
                .compose(Rx.transformer(UserInfoBean::class.java))
                .subscribe(object : RSubscriber<UserInfoBean>() {
                    override fun onSucceed(bean: UserInfoBean?) {
                        if (bean != null) {
                            initView(bean)
                        }
                    }
                })
    }

    private fun initView(bean: UserInfoBean) {
        fans_count.setText(bean.fans_count.toString())
        chars_count.setText(bean.charm)
        grade.setGender(bean.sex,bean.grade)
//        auth_iview

    }


}