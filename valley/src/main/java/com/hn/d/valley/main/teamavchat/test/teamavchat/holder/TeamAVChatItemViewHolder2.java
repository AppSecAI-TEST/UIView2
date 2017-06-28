package com.hn.d.valley.main.teamavchat.test.teamavchat.holder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.main.teamavchat.test.adapter.BaseMultiItemFetchLoadAdapter;
import com.hn.d.valley.main.teamavchat.test.holder.BaseViewHolder;
import com.hn.d.valley.main.teamavchat.test.teamavchat.module.TeamAVChatItem;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;
import com.netease.nimlib.sdk.nos.model.NosThumbParam;
import com.netease.nimlib.sdk.nos.util.NosThumbImageUtil;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import static android.view.View.GONE;

/**
 * Created by huangjun on 2017/5/4.
 */

public class TeamAVChatItemViewHolder2 extends TeamAVChatItemViewHolderBase {
    private static final int DEFAULT_AVATAR_THUMB_SIZE = 9;
    private ImageView avatarImage;
    private ImageView loadingImage;
    private AVChatVideoRender surfaceView;
    private TextView nickNameText;
    private TextView stateText;
    private ProgressBar volumeBar;


    public TeamAVChatItemViewHolder2(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    protected void inflate(final BaseViewHolder holder) {
        avatarImage = holder.getView(R.id.avatar_image);
        loadingImage = holder.getView(R.id.loading_image);
        surfaceView = holder.getView(R.id.surface);
        nickNameText = holder.getView(R.id.nick_name_text);
        stateText = holder.getView(R.id.avchat_state_text);
        volumeBar = holder.getView(R.id.avchat_volume);
    }

    protected void refresh(final TeamAVChatItem data) {
        nickNameText.setText(TeamDataCache.getInstance().getDisplayNameWithoutMe(data.teamId, data.account));

        final UserInfoProvider.UserInfo userInfo = NimUserInfoCache.getInstance().getUserInfo(data.account);
        final int defaultResId = R.drawable.default_avatar;
//        final String thumbUrl = makeAvatarThumbNosUrl(userInfo != null ? userInfo.getAvatar() : null, DEFAULT_AVATAR_THUMB_SIZE);
        final String thumbUrl = userInfo.getAvatar();
        Glide.with(ValleyApp.getApp())
                .load(thumbUrl).asBitmap().centerCrop()
                .placeholder(defaultResId)
                .error(defaultResId)
                .override(DEFAULT_AVATAR_THUMB_SIZE, DEFAULT_AVATAR_THUMB_SIZE)
                .into(avatarImage);

        if (data.state == TeamAVChatItem.STATE.STATE_WAITING) {
            // 等待接听
            Glide.with(ValleyApp.getApp())
                    .load(R.drawable.t_avchat_loading).asGif()
                    .into(loadingImage);
            loadingImage.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.INVISIBLE);
            stateText.setVisibility(GONE);
        } else if (data.state == TeamAVChatItem.STATE.STATE_PLAYING) {
            // 正在通话
            loadingImage.setVisibility(GONE);
            surfaceView.setVisibility(data.videoLive ? View.VISIBLE : View.INVISIBLE); // 有视频流才需要SurfaceView
            stateText.setVisibility(GONE);
        } else if (data.state == TeamAVChatItem.STATE.STATE_END || data.state == TeamAVChatItem.STATE.STATE_HANGUP) {
            // 未接听/挂断
            loadingImage.setVisibility(GONE);
            surfaceView.setVisibility(GONE);
            stateText.setVisibility(View.VISIBLE);
            stateText.setText(data.state == TeamAVChatItem.STATE.STATE_HANGUP ? R.string.avchat_no_pick_up : R.string.avchat_no_pick_up);
        }

        updateVolume(data.volume);
    }

    /**
     * 生成头像缩略图NOS URL地址（用作ImageLoader缓存的key）
     */
    private static String makeAvatarThumbNosUrl(final String url, final int thumbSize) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }

        return thumbSize > 0 ? NosThumbImageUtil.makeImageThumbUrl(url, NosThumbParam.ThumbType.Crop, thumbSize, thumbSize) : url;
    }

    public AVChatVideoRender getSurfaceView() {
        return surfaceView;
    }

    public void updateVolume(int volume) {
        volumeBar.setProgress(volume);
    }
}
