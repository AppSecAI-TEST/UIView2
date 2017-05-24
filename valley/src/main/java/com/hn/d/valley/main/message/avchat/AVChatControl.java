package com.hn.d.valley.main.message.avchat;

import android.content.Context;

import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.main.message.avchat.ui.AVChatAudio;
import com.hn.d.valley.main.message.avchat.ui.AVChatSurface;
import com.hn.d.valley.main.message.avchat.ui.AVChatVideo;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatData;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/23 14:42
 * 修改人员：hewking
 * 修改时间：2017/05/23 14:42
 * 修改备注：
 * Version: 1.0.0
 */
public class AVChatControl {

    //data
    private AVChatSurface avChatSurface;
    private AVChatAudio avChatAudio;
    private AVChatVideo avChatVideo;
    private String videoAccount; // 发送视频请求，onUserJoin回调的user account

    // state
    public boolean canSwitchCamera = false;
    private boolean isClosedCamera = false;
    public AtomicBoolean isCallEstablish = new AtomicBoolean(false);

    //是否在录制
    private boolean isRecording = false;

    // 检查存储
    private boolean recordWarning = false;

    private CallStateEnum callingState = CallStateEnum.INVALID;
    private AVChatData avChatData;

    private Context mContext;
    private RBaseViewHolder mViewHolder;
    private UIBaseView mBaseView;

    private AVChatAudio mAVChatAudio;
    private AVChatVideo mAVChatVideo;
    private AVChatSurface mAVChatSurface;

    public AVChatControl(Context ctx, RBaseViewHolder viewHolder, UIBaseView uiBaseView) {
        this.mContext = ctx;
        this.mViewHolder = viewHolder;
        this.mBaseView = uiBaseView;

        mAVChatAudio = new AVChatAudio(ctx,viewHolder.v(R.id.avchat_audio_layout),this);
        mAVChatVideo = new AVChatVideo(ctx,viewHolder.v(R.id.avchat_video_layout),this);
        mAVChatSurface = new AVChatSurface(ctx,viewHolder.v(R.id.avchat_surface_layout),this);

    }

    public void closeSessions(int exitCode) {
        if (avChatAudio != null)
            avChatAudio.closeSession(exitCode);
        if (avChatVideo != null)
            avChatVideo.closeSession(exitCode);
//        showQuitToast(exitCode);
        isCallEstablish.set(false);
        canSwitchCamera = false;
        isClosedCamera = false;
//        aVChatListener.uiExit();
    }

    public String getVideoAccount() {
        return videoAccount;
    }

    /**
     * 音频切换为视频的请求
     */
    public void incomingAudioToVideo() {
        onCallStateChange(CallStateEnum.INCOMING_AUDIO_TO_VIDEO);
    }
    /**
     * 状态改变
     *
     * @param stateEnum
     */
    public void onCallStateChange(CallStateEnum stateEnum) {
        callingState = stateEnum;
        avChatSurface.onCallStateChange(stateEnum);
        avChatAudio.onCallStateChange(stateEnum);
        avChatVideo.onCallStateChange(stateEnum);
    }

    public void peerVideoOff() {
        avChatSurface.peerVideoOff();
    }

    public void peerVideoOn() {
        avChatSurface.peerVideoOn();
    }

    /**
     * 音频切换为视频
     */
    public void onAudioToVideo() {
        onCallStateChange(CallStateEnum.VIDEO);
        avChatVideo.onAudioToVideo(AVChatManager.getInstance().isLocalAudioMuted(),
                isRecording, recordWarning); // isMute是否处于静音状态

        //打开视频
        AVChatManager.getInstance().enableVideo();
        AVChatManager.getInstance().startVideoPreview();

        // 是否在发送视频 即摄像头是否开启
        if (AVChatManager.getInstance().isLocalVideoMuted()) {
            AVChatManager.getInstance().muteLocalVideo(false);
            avChatSurface.localVideoOn();
            isClosedCamera = false;
        }
    }

    /**
     * 视频切换为音频
     */
    public void onVideoToAudio() {

        //关闭视频
        AVChatManager.getInstance().stopVideoPreview();
        AVChatManager.getInstance().disableVideo();

        // 判断是否静音，扬声器是否开启，对界面相应控件进行显隐处理。
        avChatAudio.onVideoToAudio(AVChatManager.getInstance().isLocalAudioMuted(),
                AVChatManager.getInstance().speakerEnabled(),
                isRecording, recordWarning);
    }

    /**
     * 初始化大小图像
     *
     * @param largeAccount 对方的帐号
     */
    public void initAllSurfaceView(String largeAccount) {
        avChatSurface.initLargeSurfaceView(largeAccount);
        avChatSurface.initSmallSurfaceView(ValleyApp.getApp().getApplicationContext());
    }


    public AVChatData getAvChatData() {
        return avChatData;
    }

    public void resumeVideo() {

    }

    public void pauseVideo() {


    }

    public void outGoingCalling(String receiverId, AVChatType avChatType) {

    }

    public void inComingCalling(AVChatData avChatData) {


    }
}
