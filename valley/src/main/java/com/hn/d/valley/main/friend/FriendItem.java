package com.hn.d.valley.main.friend;

import com.github.promeg.pinyinhelper.Pinyin;
import com.hn.d.valley.bean.FriendBean;

import rx.functions.Action1;

/**
 * Created by hewking on 2017/3/8.
 */
public class FriendItem extends AbsFriendItem{

    FriendBean friendBean;

    public FriendItem(FriendBean bean){
        this.friendBean = bean;
        itemType = ItemTypes.FRIEND;
        String firstLetter = String.valueOf(Pinyin.toPinyin(bean.getDefaultMark().charAt(0)).toUpperCase().charAt(0));
        groupText = firstLetter;
    }

    public FriendBean getFriendBean() {
        return friendBean;
    }

    public void setFriendBean(FriendBean friendBean) {
        this.friendBean = friendBean;
    }
}
