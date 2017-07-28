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
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.Arrays;
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
        this(R.drawable.calling_card_xiaoxi_n, "名片");
    }

    public PersonalCardCommandItem(int icoResId, String text) {
        super(icoResId, text);
    }

    @Override
    protected void onClick() {
        BaseContactSelectAdapter.Options option = new BaseContactSelectAdapter.Options(RModelAdapter.MODEL_SINGLE);

        //个人名片
        if (getContainer().sessionType == SessionTypeEnum.P2P) {
            //
            option.showDialog(true);
            option.showUnSelectUids(true);
            ContactSelectUIVIew.start(getContainer().mLayout, option
                    , null,null, Arrays.asList(getContainer().account),false, new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                        @Override
                        public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, RequestCallback requestCallback) {
                            sendCardMsg(absContactItems, requestCallback);
                        }
                    });
        } else if(getContainer().sessionType == SessionTypeEnum.Team){
            option.showDialog(true);
            ContactSelectUIVIew.start(getContainer().mLayout, option
                    , null, new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                        @Override
                        public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, RequestCallback requestCallback) {
                            sendCardMsg(absContactItems,requestCallback);
                        }
                    });
        }
    }

    private void sendCardMsg(List<AbsContactItem> absContactItems, RequestCallback requestCallback) {
        requestCallback.onSuccess("");
        ContactItem contactItem = (ContactItem) absContactItems.get(0);
        FriendBean friendBean = contactItem.getFriendBean();
        PersonalCardAttachment attachment = new PersonalCardAttachment(friendBean);
        IMMessage message = MessageBuilder.createCustomMessage(getContainer().account, getContainer().sessionType, friendBean.getIntroduce(), attachment);
        getContainer().proxy.sendMessage(message);
    }
}
