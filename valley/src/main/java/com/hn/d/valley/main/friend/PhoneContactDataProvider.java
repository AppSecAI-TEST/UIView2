package com.hn.d.valley.main.friend;

import com.angcyo.uiview.utils.ContactsPickerHelper;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.main.message.query.ContactSearch;
import com.hn.d.valley.main.message.query.TextQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hewking on 2017/3/23.
 */
public class PhoneContactDataProvider implements IDataResource.IDataProvider {

    private PhoneContactDataProvider() {
    }

    public static PhoneContactDataProvider getInstance() {
        return Holder.sInstance;
    }

    @Override
    public List<AbsContactItem> provide() {
        List<ContactsPickerHelper.ContactsInfo> contactsInfos = ContactsPickerHelper.getContactsList(ValleyApp.getApp().getApplicationContext());
        ArrayList<AbsContactItem> datas = new ArrayList<>(contactsInfos.size());
        for (ContactsPickerHelper.ContactsInfo info : contactsInfos) {
            datas.add(new PhoneContactItem(info));
        }
        return datas;
    }

    public List<AbsContactItem> provide(TextQuery textQuery) {
        if (textQuery != null) {
            ArrayList<AbsContactItem> datas = new ArrayList<>();
            List<ContactsPickerHelper.ContactsInfo> contactsInfos = ContactsPickerHelper.getContactsList(ValleyApp.getApp().getApplicationContext());
            for (Iterator<ContactsPickerHelper.ContactsInfo> it = contactsInfos.iterator(); it.hasNext(); ) {
                ContactsPickerHelper.ContactsInfo info = it.next();
                //匹配到去除
                boolean hit = ContactSearch.hitContactInfo(info, textQuery);
                if (!hit) {
                    continue;
                }
                datas.add(new PhoneContactItem(info));
            }
            return datas;
        } else {
            return provide();
        }

    }

    private static class Holder {
        private static PhoneContactDataProvider sInstance = new PhoneContactDataProvider();
    }
}
