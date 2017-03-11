package com.hn.d.valley.main.friend;

/**
 * Created by hewking on 2017/3/8.
 */
public abstract class AbsFriendItem {

    protected int itemType;

    protected String groupText;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getGroupText() {
        return groupText;
    }

    public void setGroupText(String groupText) {
        this.groupText = groupText;
    }
}
