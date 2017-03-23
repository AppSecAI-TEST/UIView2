package com.hn.d.valley.main.message;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;

/**
 * Created by hewking on 2017/3/22.
 */
public class AddressBookUIView extends SingleRecyclerUIView<AbsContactItem> {

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString("添加手机联系人");
    }

    public AddressBookUIView() {

    }

    @Override
    protected RExBaseAdapter initRExBaseAdapter() {
        return null;
    }
}
