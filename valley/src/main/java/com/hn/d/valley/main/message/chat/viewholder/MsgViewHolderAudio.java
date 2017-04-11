package com.hn.d.valley.main.message.chat.viewholder;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.main.message.AudioViewControl;
import com.hn.d.valley.main.message.audio.AudioPlayCallback;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

import static com.hn.d.valley.R.id.message_item_audio_duration;
import static com.hn.d.valley.R.id.search_go_btn;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderAudio extends MsgViewHolderBase {

    private RelativeLayout msgAudioLayout;

    public MsgViewHolderAudio(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_audio_layout;
    }

    @Override
    protected void inflateContentView() {
        msgAudioLayout = (RelativeLayout) findViewById(R.id.message_item_audio_layout);
    }

    @Override
    protected void bindContentView() {
        ImageView imageView = (ImageView) findViewById(R.id.message_item_audio_playing_animation);
        final TextView timeView = (TextView) findViewById(message_item_audio_duration);

        if (isReceivedMessage()) {
            msgAudioLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            imageView.setBackgroundResource(R.drawable.nim_audio_animation_list_left);
            timeView.setTextColor(Color.BLACK);
        } else {
            msgAudioLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            imageView.setBackgroundResource(R.drawable.nim_audio_animation_list_right);
            timeView.setTextColor(Color.WHITE);
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

        msgAudioLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioViewControl.onItemClick();
            }
        });


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
}
