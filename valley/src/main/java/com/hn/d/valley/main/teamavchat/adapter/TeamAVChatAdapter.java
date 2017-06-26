package com.hn.d.valley.main.teamavchat.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.utils.UI;
import com.hn.d.valley.R;
import com.hn.d.valley.main.teamavchat.module.TeamAVChatItem;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;

import java.util.List;

/**
 * Created by huangjun on 2017/5/4.
 */

public class TeamAVChatAdapter extends RBaseAdapter<TeamAVChatItem>{

    private static final int VIEW_TYPE_DATA = 1;
    private static final int VIEW_TYPE_ADD = 2;
    private static final int VIEW_TYPE_HOLDER = 3;

    private int excludeWidth = 0;

    private RecyclerView.LayoutManager layoutManager;

    /**
     * 每一行中, Item的数量, 用来计算item的宽高
     */
    private int mItemCountLine = 3;


    public TeamAVChatAdapter(Context context) {
        super(context);
    }

    public TeamAVChatAdapter(Context ctx, List<TeamAVChatItem> data) {
        super(ctx,data);
    }

    @Override
    public int getItemCount() {
        return mAllDatas.size();
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_team_av_chat;
    }

    @Override
    public int getItemType(int position) {
        TeamAVChatItem item = mAllDatas.get(position);
        if (item.type == TeamAVChatItem.TYPE.TYPE_DATA) {
            return VIEW_TYPE_DATA;
        } else if (item.type == TeamAVChatItem.TYPE.TYPE_HOLDER) {
            return VIEW_TYPE_HOLDER;
        }
        return VIEW_TYPE_DATA;
    }

    @Override
    protected void onBindView(RBaseViewHolder holder, int position, TeamAVChatItem data) {

        if (getItemType(position) == VIEW_TYPE_DATA) {
            int itemSize = getItemSize();
            UI.setViewHeight(holder.itemView, itemSize);

//            ImageView iv_icon_head = holder.imgV(R.id.iv_icon_head);
            AVChatVideoRender render = holder.v(R.id.render_avchat);
//            TextView tv_avchat_tip = holder.tv(R.id.tv_avchat_tip);
//            ImageView iv_audio_tip = holder.imgV(R.id.iv_audio_tip);
//            ImageView iv_avchat_loading = holder.imgV(R.id.iv_avchat_loading);

//        nickNameText.setText(TeamDataCache.getInstance().getDisplayNameWithoutMe(data.teamId, data.account));

//            final UserInfoProvider.UserInfo userInfo = DefaultUserInfoProvider.getInstance().getUserInfo(data.account);
//            final int defaultResId = R.drawable.default_avatar;
//            final String thumbUrl = userInfo.getAvatar();
//            Glide.with(mContext)
//                    .load(thumbUrl).asBitmap().centerCrop()
//                    .placeholder(defaultResId)
//                    .error(defaultResId)
////                .override(DEFAULT_AVATAR_THUMB_SIZE, DEFAULT_AVATAR_THUMB_SIZE)
//                    .into(iv_icon_head);
//
//            if (data.state == TeamAVChatItem.STATE.STATE_WAITING) {
//                // 等待接听
//                Glide.with(mContext)
//                        .load(R.drawable.t_avchat_loading).asGif()
//                        .into(iv_avchat_loading);
//                iv_avchat_loading.setVisibility(View.VISIBLE);
//                render.setVisibility(View.GONE);
//                tv_avchat_tip.setVisibility(GONE);
//            } else if (data.state == TeamAVChatItem.STATE.STATE_PLAYING) {
//                // 正在通话
//                iv_icon_head.setVisibility(GONE);
//                iv_avchat_loading.setVisibility(GONE);
//                render.setVisibility(data.videoLive ? View.VISIBLE : View.INVISIBLE); // 有视频流才需要SurfaceView
//                L.d("TeamAVChatAdapter","videolive : " + data.videoLive);
//                tv_avchat_tip.setVisibility(GONE);
//            } else if (data.state == TeamAVChatItem.STATE.STATE_END || data.state == TeamAVChatItem.STATE.STATE_HANGUP) {
//                // 未接听/挂断
//                iv_icon_head.setVisibility(View.VISIBLE);
//                iv_avchat_loading.setVisibility(GONE);
//                render.setVisibility(GONE);
//                tv_avchat_tip.setVisibility(View.VISIBLE);
//                tv_avchat_tip.setText(data.state == TeamAVChatItem.STATE.STATE_HANGUP ? R.string.text_had_hangup : R.string.avchat_no_pick_up);
//            }
//
//            updateVolume(data.volume);
//            if (data.volume > 0.1) {
//                iv_audio_tip.setVisibility(View.VISIBLE);
//            } else {
//                iv_audio_tip.setVisibility(GONE);
//            }

        }

    }

    public TeamAVChatAdapter attachRecyclerView(RecyclerView recyclerView) {
        layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            mItemCountLine = spanCount;
            UI.setViewHeight(recyclerView, getItemSize() * spanCount);
        }
        return this;
    }

    private int getItemSize() {
        int screenWidth = ResUtil.getScreenWidth(mContext) - excludeWidth;
        int itemSize = screenWidth / mItemCountLine;
        return itemSize;
    }

    private void updateVolume(int volume) {

    }

    public AVChatVideoRender getViewHolderSurfaceView(int index) {
        if (layoutManager == null) {
            return null;
        }
        View view = layoutManager.getChildAt(index);
        return (AVChatVideoRender) view.findViewById(R.id.render_avchat);
    }



    public void updateVolumeBar(TeamAVChatItem item) {
//        if (item.volume > 0.1) {
//            iv_audio_tip.setVisibility(View.VISIBLE);
//        } else {
//            iv_audio_tip.setVisibility(GONE);
//        }
    }
}
