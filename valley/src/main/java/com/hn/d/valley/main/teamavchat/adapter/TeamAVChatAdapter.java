package com.hn.d.valley.main.teamavchat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hn.d.valley.main.teamavchat.module.TeamAVChatItem;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangjun on 2017/5/4.
 */

public class TeamAVChatAdapter extends RecyclerView.ViewHolder{

    private static final int VIEW_TYPE_DATA = 1;
    private static final int VIEW_TYPE_ADD = 2;
    private static final int VIEW_TYPE_HOLDER = 3;


    public TeamAVChatAdapter(View itemView) {
        super(itemView);
    }
}
