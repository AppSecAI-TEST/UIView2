package com.hn.d.valley.main.message.avchat.ui;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.main.avchat.AVChatUIListener;
import com.hn.d.valley.main.avchat.constant.CallStateEnum;
import com.hn.d.valley.main.message.avchat.AVChatControl;
import com.hn.d.valley.widget.HnGlideImageView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/23 16:05
 * 修改人员：hewking
 * 修改时间：2017/05/23 16:05
 * 修改备注：
 * Version: 1.0.0
 */
public class AVChatVideo implements View.OnClickListener {

    private Context mContext;
    private View chatVideoRoot;
    private AVChatControl avChatControl;
    private boolean root;

    //顶部控制按钮
    private View topRoot;
    private View switchAudio;
    private Chronometer time;
    private TextView netUnstableTV;
    //中间控制按钮
    private View middleRoot;
    private HnGlideImageView headImg;
    private  TextView nickNameTV;
    private  TextView notifyTV;
    private View refuse_receive;
    private TextView refuseTV;
    private TextView receiveTV;
    //底部控制按钮
    private View bottomRoot;
    Button switchCameraToggle;
    Button closeCameraToggle;
    Button muteToggle;
    ImageView recordToggle;
    ImageView hangUpImg;

    //record
    private View recordView;
    private View recordTip;
    private View recordWarning;

    //摄像头权限提示显示
    private View permissionRoot;

    private AVChatUIListener listener;

    private boolean init = false;

    private boolean shouldEnableToggle;
    private boolean isInSwitch;

    private int topRootHeight = 0;
    private int bottomRootHeight = 0;

    public AVChatVideo(Context ctx , View view , AVChatControl control, AVChatUIListener listener){
        this.mContext = ctx;
        this.chatVideoRoot = view;
        this.avChatControl = control;
        this.listener = listener;
    }


    /**
     * 音视频状态变化及界面刷新
     *
     * @param state)
     */
    public void onCallStateChange(CallStateEnum state) {
        if (CallStateEnum.isVideoMode(state))
            findViews();
        switch (state) {
            case OUTGOING_VIDEO_CALLING:
                showProfile();//对方的详细信息
                showNotify(R.string.text_wait_receiving);
                setRefuseReceive(false);
                shouldEnableToggle = true;
                enableCameraToggle();   //使用音视频预览时这里可以开启切换摄像头按钮
                setTopRoot(false);
                setMiddleRoot(true);
                setBottomRoot(true);
                break;
            case INCOMING_VIDEO_CALLING:
                showProfile();//对方的详细信息
                showNotify(R.string.avchat_video_call_request);
                setRefuseReceive(true);
                receiveTV.setText(R.string.text_pickup);
                setTopRoot(false);
                setMiddleRoot(true);
                setBottomRoot(false);
                break;
            case VIDEO:
                isInSwitch = false;
                enableToggle();
                setTime(true);
                setTopRoot(true);
                setMiddleRoot(false);
                setBottomRoot(true);
                showNoneCameraPermissionView(false);
                break;
            case VIDEO_CONNECTING:
                showNotify(R.string.text_connecting);
                shouldEnableToggle = true;
                break;
            case OUTGOING_AUDIO_TO_VIDEO:
                isInSwitch = true;
                setTime(true);
                setTopRoot(true);
                setMiddleRoot(false);
                setBottomRoot(true);
                break;
            default:
                break;
        }
        setRoot(CallStateEnum.isVideoMode(state));

    }


    private void setRoot(boolean visible) {
        chatVideoRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示通知
     * @param resId
     */
    private void showNotify(int resId){
        notifyTV.setText(resId);
        notifyTV.setVisibility(View.VISIBLE);
    }

    private void setMiddleRoot(boolean visible){
        middleRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setBottomRoot(boolean visible){
        bottomRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
        if(bottomRootHeight == 0){
            bottomRootHeight = bottomRoot.getHeight();
        }
    }

    private void enableToggle() {

    }

    private void enableCameraToggle() {

    }

    private void setRefuseReceive(boolean b) {

    }

    /**
     * 显示个人信息
     */
    private void showProfile(){
        String account = avChatControl.getAccount();
//        headImg.loadBuddyAvatar(account);
        nickNameTV.setText(NimUserInfoCache.getInstance().getUserDisplayName(account));
    }

    private void findViews() {
        if(init || chatVideoRoot == null )
            return;
        topRoot = chatVideoRoot.findViewById(R.id.avchat_video_top_control);
        switchAudio = topRoot.findViewById(R.id.avchat_video_switch_audio);
        switchAudio.setOnClickListener(this);
        time = (Chronometer) topRoot.findViewById(R.id.avchat_video_time);
        netUnstableTV = (TextView) topRoot.findViewById(R.id.avchat_video_netunstable);

        middleRoot = chatVideoRoot.findViewById(R.id.avchat_video_middle_control);
        headImg = (HnGlideImageView) middleRoot.findViewById(R.id.avchat_video_head);
        nickNameTV = (TextView) middleRoot.findViewById(R.id.avchat_video_nickname);
        notifyTV = (TextView) middleRoot.findViewById(R.id.avchat_video_notify);

        refuse_receive = middleRoot.findViewById(R.id.avchat_video_refuse_receive);
        refuseTV = (TextView) refuse_receive.findViewById(R.id.refuse);
        receiveTV = (TextView) refuse_receive.findViewById(R.id.receive);
        refuseTV.setOnClickListener(this);
        receiveTV.setOnClickListener(this);

        recordView = chatVideoRoot.findViewById(R.id.avchat_record_layout);
        recordTip = recordView.findViewById(R.id.avchat_record_tip);
        recordWarning = recordView.findViewById(R.id.avchat_record_warning);

        bottomRoot = chatVideoRoot.findViewById(R.id.avchat_video_bottom_control);
        recordToggle = (ImageView) bottomRoot.findViewById(R.id.avchat_video_record);
        recordToggle.setEnabled(false);
        recordToggle.setOnClickListener(this);
        hangUpImg = (ImageView) bottomRoot.findViewById(R.id.avchat_video_logout);
        hangUpImg.setOnClickListener(this);

//        permissionRoot = chatVideoRoot.findViewById(R.id.avchat_video_permission_control);
        init = true;

    }

    public void onAudioToVideo(boolean localAudioMuted, boolean isRecording, boolean recordWarning) {

    }

    public void closeSession(int exitCode) {

    }

    public void showNoneCameraPermissionView(boolean b) {

    }

    private void setTime(boolean visible){
        time.setVisibility(visible ? View.VISIBLE : View.GONE);
        if(visible){
            time.setBase(avChatControl.getTimeBase());
            time.start();
        }
    }

    private void setTopRoot(boolean visible){
        topRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
        if(topRootHeight == 0){
            Rect rect = new Rect();
            topRoot.getGlobalVisibleRect(rect);
            topRootHeight = rect.bottom;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avchat_video_logout:
                listener.onHangUp();
                break;
            case R.id.refuse:
                listener.onRefuse();
                break;
            case R.id.receive:
                listener.onReceive();
                break;
            case R.id.avchat_video_mute:
                listener.toggleMute();
                break;
            case R.id.avchat_video_switch_audio:
                if(isInSwitch) {
//                    Toast.makeText(context, R.string.avchat_in_switch, Toast.LENGTH_SHORT).show();
                } else {
                    listener.videoSwitchAudio();
                }
                break;
            case R.id.avchat_switch_camera:
                listener.switchCamera();
                break;
            case R.id.avchat_close_camera:
                listener.closeCamera();
                break;
            case R.id.avchat_video_record:
                listener.toggleRecord();
                break;
            default:
                break;
        }
    }

    public void showRecordView(boolean isRecording, boolean recordWarning) {

    }
}
