package com.hn.d.valley.main.message.p2pchat;

import android.os.Bundle;
import android.view.View;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.event.EmptyChatEvent;
import com.hn.d.valley.main.message.chat.ChatUIView2;
import com.hn.d.valley.main.message.redpacket.NewRedPacketUIView;
import com.hn.d.valley.main.message.session.CommandItemInfo;
import com.hn.d.valley.main.message.session.RedPacketCommandItem;
import com.hn.d.valley.main.message.session.SessionCustomization;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：聊天界面
 * 创建人员：Robi
 * 创建时间：2016/12/27 17:46
 * 修改人员：Robi
 * 修改时间：2016/12/27 17:46
 * 修改备注：
 * Version: 1.0.0
 */
public class P2PChatUIView extends ChatUIView2 {

    /**
     * @param sessionId   聊天对象账户
     * @param sessionType 聊天类型, 群聊, 单聊
     */
    public static void start(ILayout mLayout, String sessionId, SessionTypeEnum sessionType,IMMessage anchor, SessionCustomization customization) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SESSION_ID, sessionId);
        bundle.putInt(KEY_SESSION_TYPE, sessionType.getValue());
        bundle.putSerializable(KEY_ANCHOR, anchor);
        bundle.putSerializable(KEY_SESSION_CUSTOMIZATION,customization);
        mLayout.startIView(new P2PChatUIView(), new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }


    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        rightItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.top_friends).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startIView(new P2PInfoUIView());
                P2PInfoUIView.start(mOtherILayout, mSessionId, sessionType);

            }
        }));

        return super.getTitleBar().setRightItems(rightItems);
    }

    @Subscribe
    public void onEvent(EmptyChatEvent event) {
        if (!mSessionId.equals(event.sessionId)) {
            return;
        }
        msgService().queryMessageListEx(
                getEmptyMessage(),
                QueryDirectionEnum.QUERY_OLD, mActivity.getResources().getInteger(R.integer.message_limit)
                , true)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            mChatControl.resetData(result);
                        }
                    }
                });
    }
}
