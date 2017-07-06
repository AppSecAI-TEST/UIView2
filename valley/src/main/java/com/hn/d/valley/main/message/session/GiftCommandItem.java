package com.hn.d.valley.main.message.session;

import com.hn.d.valley.R;
import com.hn.d.valley.main.message.gift.GiftListUIView;
import com.hn.d.valley.main.message.redpacket.NewRedPacketUIView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

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
        //红包
        if (getContainer().sessionType == SessionTypeEnum.P2P) {
            getContainer().mLayout.startIView(new GiftListUIView(getContainer().account));
        } else if (getContainer().sessionType == SessionTypeEnum.Team) {

        }
    }
}
