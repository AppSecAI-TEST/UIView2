package com.hn.d.valley.start

import android.os.Bundle
import android.view.View
import com.angcyo.library.utils.Anim
import com.angcyo.uiview.base.Item
import com.angcyo.uiview.base.SingleItem
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.skin.SkinHelper
import com.angcyo.uiview.utils.T_
import com.angcyo.uiview.widget.ExEditText
import com.angcyo.uiview.widget.PasswordInputEditText
import com.angcyo.uiview.widget.VerifyButton
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseItemUIView
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.service.OtherService

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/07/05 08:28
 * 修改人员：Robi
 * 修改时间：2017/07/05 08:28
 * 修改备注：
 * Version: 1.0.0
 */
class LoginProtectCodeUIView(var phone: String, val pwd: String, val open_id: String, val open_type: String, val open_nick: String,
                             val open_avatar: String, val open_sex: String, val type: String) : BaseItemUIView() {

    var code = ""

    override fun getTitleString(): String = getString(R.string.input_code)

    override fun getItemLayoutId(viewType: Int): Int {
        return R.layout.view_login_protect
    }

    override fun createItems(items: MutableList<SingleItem>) {
        items.add(object : SingleItem() {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: Item?) {
                val editInputText: ExEditText = holder.v(R.id.edit_text_view)
                val editLayout: View = holder.v(R.id.edit_layout)

                if (phone.isEmpty()) {
                    editLayout.visibility = View.VISIBLE
                }

                val verifyButton = holder.v<VerifyButton>(R.id.verify_view)
                verifyButton.setOnClickListener(View.OnClickListener {
                    code = ""
                    if (!editInputText.isPhone) {
                        Anim.band(editInputText)
                        return@OnClickListener
                    }
                    verifyButton.run()
                    phone = editInputText.string()
                    sendPhoneVerifyCode()
                    holder.tv(R.id.phone_view).text = phone
                })

                holder.tv(R.id.phone_view).text = phone
                holder.tv(R.id.text_tip_view2).setTextColor(SkinHelper.getSkin().themeSubColor)

                holder.click(R.id.text_tip_view2) {
                    //重发验证码
                    sendPhoneVerifyCode()
                }

                val editText: PasswordInputEditText = holder.v(R.id.code_view)
                editText.passwordHighlightColor = SkinHelper.getSkin().themeSubColor

                holder.click(R.id.next_view) {
                    if (editText.isInputError()) {
                        T_.error(getString(R.string.input_code_tip))
                    } else {
                        LoginUIView2.login(mActivity, mParentILayout, mSubscriptions,
                                phone, pwd, open_id, open_type, open_nick, open_avatar, open_sex, editText.string())
                    }
                }
            }
        })
    }

    override fun onViewShowFirst(bundle: Bundle?) {
        super.onViewShowFirst(bundle)
        sendPhoneVerifyCode()
    }

    private fun sendPhoneVerifyCode() {
        if (phone.isNullOrEmpty()) {
            return
        }

        add(RRetrofit.create(OtherService::class.java)
                .sendPhoneVerifyCode(Param.buildMap("phone:$phone", "type:$type"))
                .compose(Rx.transformer(String::class.java))
                .subscribe(object : BaseSingleSubscriber<String>() {
                    override fun onSucceed(s: String) {
                        code = s
                        T_.show(mActivity.getString(R.string.code_send_tip))
                    }
                })
        )
    }
}