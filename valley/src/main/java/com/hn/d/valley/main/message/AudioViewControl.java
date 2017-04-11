package com.hn.d.valley.main.message;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.audio.AudioPlayCallback;
import com.hn.d.valley.main.message.audio.MessageAudioControl;
import com.hn.d.valley.main.message.audio.Playable;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/04 17:50
 * 修改人员：Robi
 * 修改时间：2017/01/04 17:50
 * 修改备注：
 * Version: 1.0.0
 */
public class AudioViewControl {

    public static final int CLICK_TO_PLAY_AUDIO_DELAY = 500;
    public static int MAX_AUDIO_TIME_SECOND = 120;
    private final MessageAudioControl audioControl;
    View itemView;
    IMMessage mIMMessage;
    AudioPlayCallback playCallback;

    private MessageAudioControl.AudioControlListener onPlayListener = new MessageAudioControl.AudioControlListener() {

        @Override
        public void updatePlayingProgress(Playable playable, long curPosition) {
            if (curPosition > playable.getDuration()) {
                return;
            }
            updateTime(curPosition);
        }

        @Override
        public void onAudioControllerReady(Playable playable) {
            play();
        }

        @Override
        public void onEndPlay(Playable playable) {
            updateTime(playable.getDuration());

            stop();
        }
    };

    public AudioViewControl(Context context, View itemView, AudioPlayCallback callback, IMMessage message) {
        this.itemView = itemView;
        mIMMessage = message;
        playCallback = callback;

        audioControl = MessageAudioControl.getInstance(context);

        controlPlaying();

        refreshStatus();
    }

    public static int getAudioMaxEdge() {
        return (int) (0.5 * ScreenUtil.screenMin);
    }

    public static int getAudioMinEdge() {
//        return (int) (0.1875 * ScreenUtil.screenMin);
        return (int) (0.0875 * ScreenUtil.screenMin);
    }

    public static boolean isMessagePlaying(MessageAudioControl audioControl, IMMessage message) {
        if (audioControl.getPlayingAudio() != null && audioControl.getPlayingAudio().isTheSame(message)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据语音长度, 设置宽度
     */
    private void setAudioBubbleWidth(long milliseconds) {
        long seconds = TimeUtil.getSecondsByMilliseconds(milliseconds);
        int currentBubbleWidth = calculateBubbleWidth(seconds, MAX_AUDIO_TIME_SECOND);
        ViewGroup.LayoutParams layoutParams = getContainverView().getLayoutParams();
        layoutParams.width = currentBubbleWidth;
        getContainverView().setLayoutParams(layoutParams);
    }

    private View getContainverView() {
//        return itemView.v(R.id.message_item_audio_layout);
        return itemView.findViewById(R.id.message_item_audio_layout);
    }

    private int calculateBubbleWidth(long seconds, int MAX_TIME) {
        int maxAudioBubbleWidth = getAudioMaxEdge();
        int minAudioBubbleWidth = getAudioMinEdge();

        int currentBubbleWidth;
        if (seconds <= 0) {
            currentBubbleWidth = minAudioBubbleWidth;
        } else if (seconds > 0 && seconds <= MAX_TIME) {
            currentBubbleWidth = (int) ((maxAudioBubbleWidth - minAudioBubbleWidth) * (2.0 / Math.PI)
                    * Math.atan(seconds / 20.0) + minAudioBubbleWidth);
        } else {
            currentBubbleWidth = maxAudioBubbleWidth;
        }

        if (currentBubbleWidth < minAudioBubbleWidth) {
            currentBubbleWidth = minAudioBubbleWidth;
        } else if (currentBubbleWidth > maxAudioBubbleWidth) {
            currentBubbleWidth = maxAudioBubbleWidth;
        }

        return currentBubbleWidth;
    }

    public void controlPlaying() {
        final AudioAttachment msgAttachment = (AudioAttachment) mIMMessage.getAttachment();
        long duration = msgAttachment.getDuration();
        setAudioBubbleWidth(duration);

        if (!isMessagePlaying(audioControl, mIMMessage)) {
            if (audioControl.getAudioControlListener() != null
                    && audioControl.getAudioControlListener().equals(onPlayListener)) {
                audioControl.changeAudioControlListener(null);
            }

            updateTime(duration);
            stop();
        } else {
            audioControl.changeAudioControlListener(onPlayListener);
            play();
        }
    }

    private void updateTime(long milliseconds) {
        long seconds = TimeUtil.getSecondsByMilliseconds(milliseconds);

        if (seconds >= 0) {
            getTimeView().setText(seconds + "\"");
        } else {
            getTimeView().setText("");
        }
    }

    private TextView getTimeView() {
        return (TextView) itemView.findViewById(R.id.message_item_audio_duration);
    }

    private void play() {
        if (getAnimationView().getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) getAnimationView().getBackground();
            animation.start();
        }
    }

    private ImageView getAnimationView() {
        return (ImageView) itemView.findViewById(R.id.message_item_audio_playing_animation);
    }

    private void stop() {
        if (getAnimationView().getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) getAnimationView().getBackground();
            animation.stop();
            animation.selectDrawable(2);
        }
    }

    private void refreshStatus() {// 消息状态
        AudioAttachment attachment = (AudioAttachment) mIMMessage.getAttachment();
        MsgStatusEnum status = mIMMessage.getStatus();
        AttachStatusEnum attachStatus = mIMMessage.getAttachStatus();

        // alert button
        if (TextUtils.isEmpty(attachment.getPath())) {
            if (attachStatus == AttachStatusEnum.fail || status == MsgStatusEnum.fail) {
                //alertButton.setVisibility(View.VISIBLE);
            } else {
                //alertButton.setVisibility(View.GONE);
            }
        }

        // progress bar indicator
        if (status == MsgStatusEnum.sending || attachStatus == AttachStatusEnum.transferring) {
            //progressBar.setVisibility(View.VISIBLE);
        } else {
            //progressBar.setVisibility(View.GONE);
        }

        // unread indicator
        if (isReceivedMessage() && attachStatus == AttachStatusEnum.transferred && status != MsgStatusEnum.read) {
            getUnreadIndicator().setVisibility(View.VISIBLE);
        } else {
            getUnreadIndicator().setVisibility(View.GONE);
        }
    }

    private View getUnreadIndicator() {
        return itemView.findViewById(R.id.message_item_audio_unread_indicator);
    }

    // 判断消息方向，是否是接收到的消息
    private boolean isReceivedMessage() {
        return mIMMessage.getDirect() == MsgDirectionEnum.In;
    }

    public void onItemClick() {
        if (audioControl != null) {
            if (mIMMessage.getDirect() == MsgDirectionEnum.In && mIMMessage.getAttachStatus() != AttachStatusEnum.transferred) {
                return;
            }

            if (mIMMessage.getStatus() != MsgStatusEnum.read) {
                // 将未读标识去掉,更新数据库
                getUnreadIndicator().setVisibility(View.GONE);
            }

            audioControl.startPlayAudioDelay(CLICK_TO_PLAY_AUDIO_DELAY, mIMMessage, onPlayListener);
            audioControl.setPlayNext(true, playCallback, mIMMessage);
        }
    }
}
