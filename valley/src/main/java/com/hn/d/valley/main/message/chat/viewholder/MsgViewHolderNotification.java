package com.hn.d.valley.main.message.chat.viewholder;

import com.hn.d.valley.helper.TeamNotificationHelper;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderNotification extends MsgViewHolderBase {

    public MsgViewHolderNotification(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return 0;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {
        msgTimeView.setText(TeamNotificationHelper.getTeamNotificationText(message, message.getSessionId()));
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }

    @Override
    protected boolean isShowBubble() {
        return false;
    }
}
