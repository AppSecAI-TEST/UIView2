package com.hn.d.valley.main.teamavchat.test.teamavchat.holder;


import com.hn.d.valley.main.teamavchat.test.adapter.BaseMultiItemFetchLoadAdapter;
import com.hn.d.valley.main.teamavchat.test.holder.BaseViewHolder;
import com.hn.d.valley.main.teamavchat.test.holder.RecyclerViewHolder;
import com.hn.d.valley.main.teamavchat.test.teamavchat.module.TeamAVChatItem;

/**
 * Created by huangjun on 2017/5/9.
 */

abstract class TeamAVChatItemViewHolderBase extends RecyclerViewHolder<BaseMultiItemFetchLoadAdapter, BaseViewHolder, TeamAVChatItem> {

    TeamAVChatItemViewHolderBase(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public void convert(final BaseViewHolder holder, TeamAVChatItem data, int position, boolean isScrolling) {
        inflate(holder);
        refresh(data);
    }

    protected abstract void inflate(final BaseViewHolder holder);

    protected abstract void refresh(final TeamAVChatItem data);
}
