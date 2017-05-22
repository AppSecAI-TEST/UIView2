package com.hn.d.valley.main.message.session;

import android.view.View;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.message.attachment.PersonalCardAttachment;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter;
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

import rx.functions.Action3;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/18 15:15
 * 修改人员：hewking
 * 修改时间：2017/05/18 15:15
 * 修改备注：
 * Version: 1.0.0
 */
public class PersonalCardCommandItem extends CommandItemInfo {

    public PersonalCardCommandItem() {
        this(R.drawable.message_plus_rts_normal, "个人名片");
    }

    public PersonalCardCommandItem(int icoResId, String text) {
        super(icoResId, text);
    }

    @Override
    protected void onClick() {
        //个人名片
        ContactSelectUIVIew.start(getContainer().mLayout, new BaseContactSelectAdapter.Options(RModelAdapter.MODEL_SINGLE)
                , null, new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                    @Override
                    public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, RequestCallback requestCallback) {

                        requestCallback.onSuccess("");

                        ContactItem contactItem = (ContactItem) absContactItems.get(0);
                        FriendBean friendBean = contactItem.getFriendBean();
                        PersonalCardAttachment attachment = new PersonalCardAttachment(friendBean);
                        IMMessage message = MessageBuilder.createCustomMessage(getContainer().account, getContainer().sessionType, friendBean.getIntroduce(), attachment);
                        getContainer().proxy.sendMessage(message);

                    }
                });
    }
}
