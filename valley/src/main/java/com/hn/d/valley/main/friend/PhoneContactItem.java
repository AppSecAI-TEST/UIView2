package com.hn.d.valley.main.friend;

import com.angcyo.uiview.utils.ContactsPickerHelper;

/**
 * Created by hewking on 2017/3/23.
 */
public class PhoneContactItem extends AbsContactItem {

    private ContactsPickerHelper.ContactsInfo contactsInfo;

    public PhoneContactItem(ContactsPickerHelper.ContactsInfo info) {
        this.contactsInfo = info;
        groupText = info.letter;
        itemType = ItemTypes.PHONECOTACT;
    }

    public ContactsPickerHelper.ContactsInfo getContactsInfo() {
        return contactsInfo;
    }
}
