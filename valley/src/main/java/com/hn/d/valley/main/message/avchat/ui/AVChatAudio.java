//package com.hn.d.valley.main.message.avchat.ui;
//
//import android.content.Context;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Chronometer;
//import android.widget.TextView;
//
//import com.angcyo.uiview.utils.NetworkUtil;
//import com.hn.d.valley.R;
//import com.hn.d.valley.ValleyApp;
//import com.hn.d.valley.cache.NimUserInfoCache;
//import com.hn.d.valley.main.avchat.AVChatUIListener;
//import com.hn.d.valley.main.avchat.constant.CallStateEnum;
//import com.hn.d.valley.main.message.avchat.AVChatControl;
//import com.hn.d.valley.widget.HnGlideImageView;
//
///**
// * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
// * 项目名称：
// * 类的描述：
// * 创建人员：hewking
// * 创建时间：2017/05/23 16:04
// * 修改人员：hewking
// * 修改时间：2017/05/23 16:04
// * 修改备注：
// * Version: 1.0.0
// */
//public class AVChatAudio implements View.OnClickListener {
//
//
//    // constant
////    private static final int[] NETWORK_GRADE_DRAWABLE = new int[]{R.drawable.network_grade_0,R.drawable.network_grade_1,R.drawable.network_grade_2,R.drawable.network_grade_3};
////    private static final int[] NETWORK_GRADE_LABEL = new int[]{R.string.avchat_network_grade_0,R.string.avchat_network_grade_1,R.string.avchat_network_grade_2,R.string.avchat_network_grade_3};
//
//    private Context context;
//    // view
//    private View rootView ;
//    private View switchVideo;
//    private HnGlideImageView headImg;
//    private TextView nickNameTV;
//    private Chronometer time;
//    private TextView wifiUnavailableNotifyTV;
//    private TextView notifyTV;
//    private TextView netUnstableTV;
//
//    private View mute_speaker_hangup;
////    private ToggleView muteToggle;
////    private ToggleView speakerToggle;
//    private View recordToggle;
//    private Button recordToggleButton;
//    private View hangup;
//
//    private View refuse_receive;
//    private TextView refuseTV;
//    private TextView receiveTV;
//
//    //record
//    private View recordView;
//    private View recordTip;
//    private View recordWarning;
//
//    // data
//    private AVChatControl manager;
//    private AVChatUIListener listener;
//
//    // state
//    private boolean init = false;
//
//    // is in switch
//    private boolean isInSwitch = false;
//
//    private boolean isEnabled = false;
//
//    public AVChatAudio(Context ctx , View view , AVChatControl control,AVChatUIListener listener) {
//        this.context = ctx;
//        rootView = view;
//        manager = control;
//        this.listener = listener;
//    }
//
//    public void onCallStateChange(CallStateEnum state) {
//        if(CallStateEnum.isAudioMode(state))
//            findViews();
//        switch (state){
//            case OUTGOING_AUDIO_CALLING: //拨打出的免费通话
//                setSwitchVideo(false);
//                showProfile();//对方的详细信息
//                showNotify(R.string.text_wait_receiving);
//                setWifiUnavailableNotifyTV(true);
//                setMuteSpeakerHangupControl(true);
//                setRefuseReceive(false);
//                break;
//            case INCOMING_AUDIO_CALLING://免费通话请求
//                setSwitchVideo(false);
//                showProfile();//对方的详细信息
//                showNotify(R.string.avchat_video_call_request);
//                setMuteSpeakerHangupControl(false);
//                setRefuseReceive(true);
//                receiveTV.setText(R.string.text_pickup);
//                break;
//            case AUDIO:
//                isInSwitch = false;
//                setWifiUnavailableNotifyTV(false);
//                showNetworkCondition(1);
//                showProfile();
//                setSwitchVideo(true);
//                setTime(true);
//                hideNotify();
//                setMuteSpeakerHangupControl(true);
//                setRefuseReceive(false);
//                enableToggle();
//                break;
//            case AUDIO_CONNECTING:
//                showNotify(R.string.text_connecting);
//                break;
//            case INCOMING_AUDIO_TO_VIDEO:
//                isInSwitch = true;
//                showNotify(R.string.text_invite_start_video_chat);
//                setMuteSpeakerHangupControl(false);
//                setRefuseReceive(true);
//                receiveTV.setText(R.string.text_av_receive);
//                break;
//            default:
//                break;
//        }
//        setRoot(CallStateEnum.isAudioMode(state));
//
//    }
//
//    private void enableToggle() {
//        if(!isEnabled) {
//            recordToggle.setEnabled(true);
//        }
//        isEnabled = true;
//    }
//
//    private void setRoot(boolean visible){
//        rootView.setVisibility(visible ? View.VISIBLE : View.GONE);
//    }
//
//    /**
//     * 隐藏界面文案
//     */
//    private void hideNotify(){
//        notifyTV.setVisibility(View.GONE);
//    }
//
//    /**
//     * 设置通话时间显示
//     * @param visible
//     */
//    private void setTime(boolean visible){
//        time.setVisibility(visible ? View.VISIBLE : View.GONE);
//        if(visible){
//            time.setBase(manager.getTimeBase());
//            time.start();
//        }
//    }
//
//    /**
//     * 显示网络状态
//     * @param grade
//     */
//    public void showNetworkCondition(int grade){
////        if(grade >= 0 && grade < NETWORK_GRADE_DRAWABLE.length){
////            netUnstableTV.setText(NETWORK_GRADE_LABEL[grade]);
////            Drawable drawable = DemoCache.getContext().getResources().getDrawable(NETWORK_GRADE_DRAWABLE[grade]);
////            if(drawable != null){
////                drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
////                netUnstableTV.setCompoundDrawables(null,null,drawable,null);
////            }
////            netUnstableTV.setVisibility(View.VISIBLE);
////        }
//    }
//
//    /**
//     * 显示或隐藏拒绝，开启布局
//     * @param visible
//     */
//    private void setRefuseReceive(boolean visible){
//        refuse_receive.setVisibility(visible ? View.VISIBLE : View.GONE);
//    }
//
//    /**
//     * 显示或者隐藏是否为wifi的提示
//     * @param show
//     */
//    private void setWifiUnavailableNotifyTV(boolean show){
//        if(show && !NetworkUtil.isWifi(ValleyApp.getApp().getApplicationContext())){
//            wifiUnavailableNotifyTV.setVisibility(View.VISIBLE);
//        }else {
//            wifiUnavailableNotifyTV.setVisibility(View.GONE);
//        }
//    }
//
//    /**
//     * 显示或隐藏禁音，结束通话布局
//     * @param visible
//     */
//    private void setMuteSpeakerHangupControl(boolean visible){
//        mute_speaker_hangup.setVisibility(visible ? View.VISIBLE : View.GONE);
//    }
//
//    /**
//     * 界面状态文案设置
//     * @param resId 文案
//     */
//    private void showNotify(int resId) {
//        notifyTV.setText(resId);
//        notifyTV.setVisibility(View.VISIBLE);
//    }
//
//    /**
//     * 个人信息设置
//     */
//    private void showProfile(){
//        String account = manager.getAccount();
////        headImg.loadBuddyAvatar(account);
//        nickNameTV.setText(NimUserInfoCache.getInstance().getUserDisplayName(account));
//    }
//
//    /**
//     * 显示或隐藏音视频切换
//     * @param visible
//     */
//    private void setSwitchVideo(boolean visible){
//        switchVideo.setVisibility(visible ? View.VISIBLE : View.GONE);
//    }
//
//    /**
//     * 界面初始化
//     */
//    private void findViews() {
//        if(init || rootView == null){
//            return;
//        }
//        switchVideo = rootView.findViewById(R.id.avchat_audio_switch_video);
//        switchVideo.setOnClickListener(this);
//
//        headImg = (HnGlideImageView) rootView.findViewById(R.id.avchat_audio_head);
//        nickNameTV = (TextView) rootView.findViewById(R.id.avchat_audio_nickname);
//        time = (Chronometer) rootView.findViewById(R.id.avchat_audio_time);
//        wifiUnavailableNotifyTV = (TextView) rootView.findViewById(R.id.avchat_audio_wifi_unavailable);
//        notifyTV = (TextView) rootView.findViewById(R.id.avchat_audio_notify);
//        netUnstableTV = (TextView) rootView.findViewById(R.id.avchat_audio_netunstable);
//
//        mute_speaker_hangup = rootView.findViewById(R.id.avchat_audio_mute_speaker_huangup);
//        View mute = mute_speaker_hangup.findViewById(R.id.avchat_audio_mute);
////        muteToggle = new ToggleView(mute, ToggleState.OFF, this);
//        View speaker = mute_speaker_hangup.findViewById(R.id.avchat_audio_speaker);
////        speakerToggle = new ToggleView(speaker, ToggleState.OFF, this);
//        recordToggle = mute_speaker_hangup.findViewById(R.id.avchat_audio_record);
////        recordToggleButton = (Button) mute_speaker_hangup.findViewById(R.id.avchat_audio_record_button);
//
//        hangup = mute_speaker_hangup.findViewById(R.id.avchat_audio_hangup);
//        hangup.setOnClickListener(this);
//        recordToggle.setOnClickListener(this);
//        recordToggle.setEnabled(false);
//
//        refuse_receive = rootView.findViewById(R.id.avchat_audio_refuse_receive);
//        refuseTV = (TextView) refuse_receive.findViewById(R.id.refuse);
//        receiveTV = (TextView) refuse_receive.findViewById(R.id.receive);
//        refuseTV.setOnClickListener(this);
//        receiveTV.setOnClickListener(this);
//
//        recordView = rootView.findViewById(R.id.avchat_record_layout);
//        recordTip = rootView.findViewById(R.id.avchat_record_tip);
//        recordWarning = rootView.findViewById(R.id.avchat_record_warning);
//
//        init = true;
//    }
//
//    public void onVideoToAudio(boolean localAudioMuted, boolean b, boolean isRecording, boolean recordWarning) {
//
//    }
//
//    public void closeSession(int exitCode) {
//        if(init){
//            time.stop();
////            muteToggle.disable(false);
////            speakerToggle.disable(false);
//            recordToggle.setEnabled(false);
//            refuseTV.setEnabled(false);
//            receiveTV.setEnabled(false);
//            hangup.setEnabled(false);
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
////            case R.id.avchat_video_logout:
////                listener.onHangUp();
////                break;
//            case R.id.refuse:
//                listener.onRefuse();
//                break;
//            case R.id.receive:
//                listener.onReceive();
//                break;
////            case R.id.avchat_video_mute:
////                listener.toggleMute();
////                listener.onHangUp();
////                break;
//            case R.id.avchat_video_switch_audio:
//                if(isInSwitch) {
////                    Toast.makeText(context, R.string.avchat_in_switch, Toast.LENGTH_SHORT).show();
//                } else {
//                    listener.videoSwitchAudio();
//                }
//                break;
//            case R.id.avchat_switch_camera:
//                listener.switchCamera();
//                break;
////            case R.id.avchat_close_camera:
////                listener.closeCamera();
////                break;
////            case R.id.avchat_video_record:
////                listener.toggleRecord();
////                break;
//            default:
//                break;
//        }
//
//    }
//
//    public void showRecordView(boolean isRecording, boolean recordWarning) {
//
//    }
//}
