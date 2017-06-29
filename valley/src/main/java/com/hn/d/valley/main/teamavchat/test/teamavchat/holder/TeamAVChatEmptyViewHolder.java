package com.hn.d.valley.main.teamavchat.test.teamavchat.holder;


import android.view.View;

import com.hn.d.valley.R;
import com.hn.d.valley.main.teamavchat.test.adapter.BaseMultiItemFetchLoadAdapter;
import com.hn.d.valley.main.teamavchat.test.holder.BaseViewHolder;
import com.hn.d.valley.main.teamavchat.test.teamavchat.module.TeamAVChatItem;

/**
 * Created by huangjun on 2017/5/9.
 */

public class TeamAVChatEmptyViewHolder extends TeamAVChatItemViewHolderBase {

    private View contentView;

    public TeamAVChatEmptyViewHolder(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected void inflate(BaseViewHolder holder) {
        contentView = holder.convertView;
    }

    @Override
    protected void refresh(TeamAVChatItem data,int position) {
        if (position % 2 == 1) {
            contentView.setBackgroundResource(R.color.black_2D2D2E);
        } else {
            contentView.setBackgroundResource(R.color.black_2D2D2E);
        }
    }


}
