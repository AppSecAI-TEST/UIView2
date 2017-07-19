package com.hn.d.valley.main.friend;

import com.angcyo.github.utilcode.utils.StringUtils;
import com.github.promeg.pinyinhelper.Pinyin;
import com.hn.d.valley.bean.FriendBean;

import rx.functions.Action1;

/**
 * Created by hewking on 2017/3/8.
 */
public class SystemPushItem extends AbsContactItem {

    FriendBean friendBean;

    Action1 action;

    public SystemPushItem(FriendBean bean,Action1 action){
        this.friendBean = bean;
        this.action = action;
        itemType = ItemTypes.SYSTEMPUSH;
        char ch = bean.getTrueName().charAt(0);
        String letter = StringUtils.getAsciiLeadingUp(ch);
        if (letter != null ) {
            groupText = letter;
        } else if (Pinyin.isChinese(ch)) {
            groupText = String.valueOf(Pinyin.toPinyin(ch).toUpperCase().charAt(0));
        } else {
            groupText = "#";
        }
    }

    public <T> void onFuncClick(T t){
        //checkNotNull
        if (action == null) {
            return;
        }
        action.call(t);
    }

    public FriendBean getFriendBean() {
        return friendBean;
    }

    public void setFriendBean(FriendBean friendBean) {
        this.friendBean = friendBean;
    }
}
