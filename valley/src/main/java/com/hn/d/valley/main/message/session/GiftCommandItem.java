package com.hn.d.valley.main.message.session;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.message.gift.GiftListUIView;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter;
import com.hn.d.valley.main.message.groupchat.GroupMemberSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.message.redpacket.NewRedPacketUIView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.List;

import rx.functions.Action3;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/18 15:17
 * 修改人员：hewking
 * 修改时间：2017/05/18 15:17
 * 修改备注：
 * Version: 1.0.0
 */
public class GiftCommandItem extends CommandItemInfo {

    public GiftCommandItem() {
        this(R.drawable.liwu_xiaoxi, "礼物");
    }

    public GiftCommandItem(int icoResId, String text) {
        super(icoResId, text);
    }

    @Override
    protected void onClick() {
        //礼物
        if (getContainer().sessionType == SessionTypeEnum.P2P) {
            getContainer().mLayout.startIView(new GiftListUIView(getContainer()));
        } else if(getContainer().sessionType == SessionTypeEnum.Team) {
//            GroupMemberSelectUIVIew.start(mParentILayout, new BaseContactSelectAdapter.Options(RModelAdapter.MODEL_SINGLE), null, , new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
//                @Override
//                public void call(UIBaseRxView uiBaseRxView, List<AbsContactItem> items, RequestCallback callback) {
//                    if (items.size() == 0) {
//                        T_.show("不能为空!");
//                        return;
//                    }
//                    callback.onSuccess("");

//                    GroupMemberItem item = (GroupMemberItem) items.get(0);
//
//                    if (selectedMembers == null) {
//                        selectedMembers = new HashMap<>();
//                    }
////                        selectedMembers.put(item.getMemberBean().getUserId(), item.getMemberBean());
//                    selectedMembers.put(item.getMemberBean().getDefaultNick(), item.getMemberBean());
//                    mInputView.addMention(item.getMemberBean().getDefaultNick());
//                }
//            });
        }
    }
}
