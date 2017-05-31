package com.hn.d.valley.main.message.chat.viewholder;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.message.AudioViewControl;
import com.hn.d.valley.main.message.audio.AudioPlayCallback;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.skin.SkinUtils;
import com.netease.nimlib.sdk.avchat.constant.AVChatRecordState;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;
import java.util.Map;

import static com.hn.d.valley.R.id.image;
import static com.hn.d.valley.R.id.message_item_audio_duration;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHAVChat extends MsgViewHolderBase {

    private RelativeLayout msgAudioLayout;
    private ImageView imageView;
    private TextView timeView;

    public MsgVHAVChat(BaseMultiAdapter adapter) {
        super(adapter);
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
    }

    @Override
    protected void bindContentView() {
        AVChatAttachment attachment = (AVChatAttachment) message.getAttachment();
        int duration = attachment.getDuration();
        AVChatRecordState state = attachment.getState();
        AVChatType type = attachment.getType();
        imageView.setBackground(null);
        timeView.setTextColor(Color.BLACK);
        if (isReceivedMessage()) {
            msgAudioLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            if (type == AVChatType.VIDEO) {
                imageView.setImageResource(R.drawable.shipinjieshu_shipinliaotian_2);
            } else {
                imageView.setImageResource(R.drawable.yuyinjieshu_yuyingliaotian_2);
            }
        } else {
            msgAudioLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            if (type == AVChatType.VIDEO) {
                imageView.setImageResource(R.drawable.shipinjieshu_shipinliaotian_1);
            } else {
                imageView.setImageResource(R.drawable.yuyinjieshu_yuyingliaotian_1);
            }
        }



        switch (state) {
            case Success:
                timeView.setText("聊天时长: " + TimeUtil.secToTime(duration));
                break;
            case Rejected:
                timeView.setText("对方已取消");
                break;
            case Missed:
                timeView.setText("对方没接听");
                break;
            case Canceled:
                timeView.setText("取消聊天");
                break;
        }
    }
}
