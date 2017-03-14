package com.hn.d.valley.main.message.groupchat;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.hn.d.valley.main.friend.GroupBean;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;

/**
 * Created by hewking on 2017/3/13.
 */
public class MyGroupUIView extends SingleRecyclerUIView<GroupBean> {


    @Override
    protected RExBaseAdapter<String, GroupBean, String> initRExBaseAdapter() {
        return new RExBaseAdapter<String, GroupBean, String>(mActivity) {
            @Override
            protected int getDataItemType(int posInData) {
                return super.getDataItemType(posInData);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, GroupBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
            }
        };
    }


}
