package com.hn.d.valley.main.message.groupchat;

import com.angcyo.github.utilcode.utils.StringUtils;
import com.github.promeg.pinyinhelper.Pinyin;
import com.hn.d.valley.bean.GroupMemberBean;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ItemTypes;

/**
 * Created by hewking on 2017/3/8.
 */
public class GroupMemberItem extends AbsContactItem {

    GroupMemberBean friendBean;

    public GroupMemberItem(GroupMemberBean bean){
        this.friendBean = bean;
        itemType = ItemTypes.GROUPMEMBER;
        char ch = bean.getDefaultNick().charAt(0);
        String letter = StringUtils.getAsciiLeadingUp(ch);
        if (letter != null ) {
            groupText = letter;
        } else if (Pinyin.isChinese(ch)) {
            groupText = String.valueOf(Pinyin.toPinyin(ch).toUpperCase().charAt(0));
        } else {
            groupText = "#";
        }
    }

    public GroupMemberBean getMemberBean() {
        return friendBean;
    }

    public void setMemberBean(GroupMemberBean friendBean) {
        this.friendBean = friendBean;
    }
}
