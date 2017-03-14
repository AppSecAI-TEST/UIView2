package com.hn.d.valley.main.friend;

import com.angcyo.uiview.github.utilcode.utils.StringUtils;
import com.github.promeg.pinyinhelper.Pinyin;
import com.hn.d.valley.bean.FriendBean;

/**
 * Created by hewking on 2017/3/8.
 */
public class FriendItem extends AbsFriendItem{

    FriendBean friendBean;

    public FriendItem(FriendBean bean){
        this.friendBean = bean;
        itemType = ItemTypes.FRIEND;
        char ch = bean.getDefaultMark().charAt(0);
        String letter = StringUtils.getAsciiLeadingUp(ch);
        if (letter != null ) {
            groupText = letter;
        } else if (Pinyin.isChinese(ch)) {
            groupText = String.valueOf(Pinyin.toPinyin(ch).toUpperCase().charAt(0));
        } else {
            groupText = "#";
        }
    }

    public FriendBean getFriendBean() {
        return friendBean;
    }

    public void setFriendBean(FriendBean friendBean) {
        this.friendBean = friendBean;
    }
}
