package com.hn.d.valley.main.message.p2pchat;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.groupchat.GroupMemberModel;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Created by hewking on 2017/3/23.
 */
public class P2PInfoUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {


    public P2PInfoUIView() {

    }


    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString("聊天信息");
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0 ) {
            return R.layout.item_message_group_chatinfo;
        }

       return R.layout.item_info_layout;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        final int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
        final int left = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindContactsInfo(holder);
            }
        }));



    }

    private void bindContactsInfo(RBaseViewHolder holder) {
//        GroupMemberModel.getInstanse().
    }
}
