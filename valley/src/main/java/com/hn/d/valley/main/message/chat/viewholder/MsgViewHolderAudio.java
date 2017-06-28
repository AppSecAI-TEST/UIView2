    package com.hn.d.valley.main.message.chat.viewholder;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.message.AudioViewControl;
import com.hn.d.valley.main.message.audio.AudioPlayCallback;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgUIObserver;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.skin.SkinUtils;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;
import java.util.Map;

import static com.hn.d.valley.R.id.message_item_audio_duration;
import static com.hn.d.valley.R.id.search_go_btn;
import static com.hn.d.valley.R.id.time;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderAudio extends MsgViewHolderBase implements MsgUIObserver{

    private RelativeLayout msgAudioLayout;
    private ImageView imageView;
    private TextView timeView;
    private TextView tv_msg_forward_audio;
    private LinearLayout msg_item_forward_audio_layout;
    private RelativeLayout mRelativeLayout;

    public MsgViewHolderAudio(BaseMultiAdapter adapter) {
        super(adapter);
        getMsgAdapter().registerUIObserver(this);

    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_audio_layout;
    }

    @Override
    protected void inflateContentView() {
        msgAudioLayout = (RelativeLayout) findViewById(R.id.message_item_audio_layout);
        imageView = (ImageView) findViewById(R.id.message_item_audio_playing_animation);
        timeView = (TextView) findViewById(message_item_audio_duration);
        if(isForfardMsg(message)) {
            tv_msg_forward_audio = (TextView) findViewById(R.id.tv_msg_forward_audio);
            msg_item_forward_audio_layout = (LinearLayout) findViewById(R.id.msg_item_forward_audio_layout);
            mRelativeLayout = (RelativeLayout) findViewById(R.id.fl_audio_container);
        }
    }

    @Override
    protected void bindContentView() {
        if (isReceivedMessage()) {
            msgAudioLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            imageView.setBackgroundResource(R.drawable.nim_audio_animation_list_left);
            timeView.setTextColor(Color.BLACK);
        } else {
            msgAudioLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            imageView.setBackgroundResource(R.drawable.nim_audio_animation_list_right);
            timeView.setTextColor(Color.WHITE);
        }

        Map<String, Object> extension = message.getRemoteExtension();
        if(extension != null) {
            if (isReceivedMessage()) {
                msg_item_forward_audio_layout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                imageView.setBackgroundResource(R.drawable.nim_audio_animation_list_left);
                imageView.setBackgroundResource(R.drawable.nim_audio_animation_list_right);
                timeView.setTextColor(Color.WHITE);
                switch (SkinUtils.getSkin()) {
                    case SkinManagerUIView.SKIN_BLACK:
                        mRelativeLayout.setBackgroundResource(R.drawable.bubble_box_right_black_selector);
                        break;
                    case SkinManagerUIView.SKIN_GREEN:
                        mRelativeLayout.setBackgroundResource(R.drawable.bubble_box_right_green_selector);
                        break;
                    case SkinManagerUIView.SKIN_BLUE:
                        mRelativeLayout.setBackgroundResource(R.drawable.bubble_box_right_blue_selector);
                        break;
                }

            } else {
                msg_item_forward_audio_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                mRelativeLayout.setBackgroundResource(R.drawable.base_white_round_bg_5x);
                imageView.setBackgroundResource(R.drawable.nim_audio_animation_list_left);
                timeView.setTextColor(Color.BLACK);
            }
            String from = (String) extension.get("from");
            String forwardUser = NimUserInfoCache.getInstance().getUserDisplayName(from);
            tv_msg_forward_audio.setText(SpannableStringUtils.getBuilder(context.getString(R.string.text_audio_forward))
                    .append(forwardUser).setForegroundColor(ContextCompat.getColor(context,R.color.colorAccentBlue))
                    .create());
        }

        selectDrawable(imageView);

        final AudioViewControl audioViewControl = new AudioViewControl(context, view, new AudioPlayCallback<IMMessage>() {
            @Override
            public List<IMMessage> getAllData() {
                return getAdapter().getAllDatas();
            }

            @Override
            public void notifyDataSetChanged() {
                getAdapter().notifyDataSetChanged();
            }
        }, message);

        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioViewControl.onItemClick();
            }
        });

    }

    public static boolean isForfardMsg(IMMessage message) {
        Map<String, Object> extension = message.getRemoteExtension();
        if(extension != null) {
            // TODO: 2017/5/11 语音转发
            String from = (String) extension.get("from");
            return !TextUtils.isEmpty(from);
        } else {
            return false;
        }
    }

    private void selectDrawable(View view) {
        if (view != null) {
            if (view.getBackground() instanceof AnimationDrawable) {
                AnimationDrawable animation = (AnimationDrawable) view.getBackground();
                animation.stop();
                animation.selectDrawable(2);
            }
        }
    }

    @Override
    public void onViewHide() {
        // 停止语音播放
    }

    @Override
    public void onViewShow() {

    }
}
