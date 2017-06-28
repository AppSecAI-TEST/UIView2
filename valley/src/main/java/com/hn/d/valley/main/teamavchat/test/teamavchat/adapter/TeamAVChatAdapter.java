package com.hn.d.valley.main.teamavchat.test.teamavchat.adapter;

import android.support.v7.widget.RecyclerView;

import com.hn.d.valley.R;
import com.hn.d.valley.main.teamavchat.test.adapter.BaseMultiItemFetchLoadAdapter;
import com.hn.d.valley.main.teamavchat.test.holder.BaseViewHolder;
import com.hn.d.valley.main.teamavchat.test.holder.RecyclerViewHolder;
import com.hn.d.valley.main.teamavchat.test.teamavchat.holder.TeamAVChatEmptyViewHolder;
import com.hn.d.valley.main.teamavchat.test.teamavchat.holder.TeamAVChatItemViewHolder;
import com.hn.d.valley.main.teamavchat.test.teamavchat.module.TeamAVChatItem;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TeamAVChatAdapter extends BaseMultiItemFetchLoadAdapter<TeamAVChatItem, BaseViewHolder> {

    private static final int VIEW_TYPE_DATA = 1;
    private static final int VIEW_TYPE_ADD = 2;
    private static final int VIEW_TYPE_HOLDER = 3;

    private Map<Class<? extends RecyclerViewHolder>, Integer> holder2ViewType;

    public TeamAVChatAdapter(RecyclerView recyclerView, List<TeamAVChatItem> data) {
        super(recyclerView, data);

        holder2ViewType = new HashMap<>();
        addItemType(VIEW_TYPE_DATA, R.layout.team_avchat_item, TeamAVChatItemViewHolder.class);
//        addItemType(VIEW_TYPE_DATA, R.layout.item_team_av_chat, TeamAVChatItemViewHolder.class);
        addItemType(VIEW_TYPE_HOLDER, R.layout.team_avchat_holder, TeamAVChatEmptyViewHolder.class);
        holder2ViewType.put(TeamAVChatItemViewHolder.class, VIEW_TYPE_DATA);
        holder2ViewType.put(TeamAVChatEmptyViewHolder.class, VIEW_TYPE_HOLDER);
    }

    @Override
    protected int getViewType(TeamAVChatItem item) {
        if (item.type == TeamAVChatItem.TYPE.TYPE_DATA) {
            return VIEW_TYPE_DATA;
        } else if (item.type == TeamAVChatItem.TYPE.TYPE_HOLDER) {
            return VIEW_TYPE_HOLDER;
        } else {
            return VIEW_TYPE_ADD;
        }
    }

    @Override
    protected String getItemKey(TeamAVChatItem item) {
        return item.type + "_" + item.teamId + "_" + item.account;
    }

    public AVChatVideoRender getViewHolderSurfaceView(TeamAVChatItem item) {
        RecyclerViewHolder holder = getViewHolder(VIEW_TYPE_DATA, getItemKey(item));
        if (holder instanceof TeamAVChatItemViewHolder) {
            return ((TeamAVChatItemViewHolder) holder).getSurfaceView();
        }

        return null;
    }

    public void updateVolumeBar(TeamAVChatItem item) {
        RecyclerViewHolder holder = getViewHolder(VIEW_TYPE_DATA, getItemKey(item));
        if (holder instanceof TeamAVChatItemViewHolder) {
            ((TeamAVChatItemViewHolder) holder).updateVolume(item.volume);
        }
    }
}
