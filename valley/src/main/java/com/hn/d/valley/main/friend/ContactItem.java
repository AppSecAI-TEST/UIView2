package com.hn.d.valley.main.friend;

import com.angcyo.github.utilcode.utils.StringUtils;
import com.github.promeg.pinyinhelper.Pinyin;
import com.hn.d.valley.bean.FriendBean;

/**
 * Created by hewking on 2017/3/8.
 */
public class ContactItem extends AbsContactItem {

    FriendBean friendBean;

    public ContactItem(FriendBean bean) {
        this(bean, true);
    }

    public ContactItem(FriendBean bean, boolean showGroupText) {
        this.friendBean = bean;
        itemType = ItemTypes.FRIEND;
        if (showGroupText) {
            if ("1".equals(bean.getIsStar())) {
                groupText = "☆";
                return;
            }
            char ch = bean.getTrueName().charAt(0);
            String letter = StringUtils.getAsciiLeadingUp(ch);
            if (letter != null) {
                groupText = letter;
            } else if (Pinyin.isChinese(ch)) {
                groupText = String.valueOf(Pinyin.toPinyin(ch).toUpperCase().charAt(0));
            } else {
                groupText = "#";
            }
        } else {
            groupText = "";
        }
    }

    public FriendBean getFriendBean() {
        return friendBean;
    }

    public void setFriendBean(FriendBean friendBean) {
        this.friendBean = friendBean;
    }
}
