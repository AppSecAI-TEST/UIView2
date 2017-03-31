package com.hn.d.valley.main.friend;

import com.hn.d.valley.main.message.provider.ContactDataProvider;
import com.hn.d.valley.main.message.provider.GroupListProvider;
import com.hn.d.valley.main.message.provider.MsgDataProvider;
import com.hn.d.valley.main.message.query.TextQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewking on 2017/3/23.
 */
public class DataResourceRepository implements IDataResource.IDataRepository {

    private DataResourceRepository(){}

    private static class Holder {
        private static DataResourceRepository sInstance = new DataResourceRepository();
    }

    public static DataResourceRepository getInstance() {
        return Holder.sInstance;
    }

    @Override
    public List<AbsContactItem> provide(TextQuery query,int... types) {

        List<AbsContactItem> dataList = new ArrayList<>();
        for (int type : types) {
            dataList.addAll(provide(type,query));
        }

        return dataList;
    }


    @Override
    public List<AbsContactItem> provide(int type, TextQuery query) {
        switch (type) {
            case ItemTypes.PHONECOTACT:
                return PhoneContactDataProvider.getInstance().provide(query);
            case ItemTypes.MSG:
                return MsgDataProvider.provide(query);
            case ItemTypes.FRIEND:
                return ContactDataProvider.getInstance().provide(query);
            case ItemTypes.GROUP:
                return GroupListProvider.getInstance().provide(query);
            default:
                return new ArrayList<>();
        }
    }


}
