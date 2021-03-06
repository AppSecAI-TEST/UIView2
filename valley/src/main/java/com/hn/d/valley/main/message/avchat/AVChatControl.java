//package com.hn.d.valley.main.message.avchat;
//
//import android.Manifest;
//import android.app.Activity;
//import android.text.TextUtils;
//import android.widget.Toast;
//
//import com.angcyo.library.utils.L;
//import com.angcyo.uiview.base.UIBaseView;
//import com.angcyo.uiview.github.utilcode.utils.SPUtils;
//import com.angcyo.uiview.recycler.RBaseViewHolder;
//import com.hn.d.valley.R;
//import com.hn.d.valley.cache.UserCache;
//import com.hn.d.valley.main.avchat.AVChatSoundPlayer;
//import com.hn.d.valley.main.avchat.AVChatUIListener;
//import com.hn.d.valley.main.avchat.activity.AVChatExitCode;
//import com.hn.d.valley.main.avchat.constant.CallStateEnum;
//import com.hn.d.valley.main.message.avchat.ui.AVChatAudio;
//import com.hn.d.valley.main.message.avchat.ui.AVChatSurface;
//import com.hn.d.valley.main.message.avchat.ui.AVChatVideo;
//import com.netease.nimlib.sdk.ResponseCode;
//import com.netease.nimlib.sdk.avchat.AVChatCallback;
//import com.netease.nimlib.sdk.avchat.AVChatManager;
//import com.netease.nimlib.sdk.avchat.constant.AVChatAudioEffectMode;
//import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
//import com.netease.nimlib.sdk.avchat.constant.AVChatMediaCodecMode;
//import com.netease.nimlib.sdk.avchat.constant.AVChatType;
//import com.netease.nimlib.sdk.avchat.constant.AVChatUserRole;
//import com.netease.nimlib.sdk.avchat.model.AVChatData;
//import com.netease.nimlib.sdk.avchat.model.AVChatNotifyOption;
//import com.netease.nimlib.sdk.avchat.model.AVChatParameters;
//import com.tbruyelle.rxpermissions.Permission;
//import com.tbruyelle.rxpermissions.RxPermissions;
//
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import rx.functions.Action1;
//
///**
// * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
// * 项目名称：
// * 类的描述：
// * 创建人员：hewking
// * 创建时间：2017/05/23 14:42
// * 修改人员：hewking
// * 修改时间：2017/05/23 14:42
// * 修改备注：
// * Version: 1.0.0
// */
//public class AVChatControl implements AVChatUIListener {
//
//    private static final String TAG = AVChatControl.class.getSimpleName();
//
//    //data
//    private AVChatSurface avChatSurface;
//    private AVChatAudio avChatAudio;
//    private AVChatVideo avChatVideo;
//    private String videoAccount; // 发送视频请求，onUserJoin回调的user account
//    private AVChatParameters avChatParameters;
//
//    // state
//    public boolean canSwitchCamera = false;
//    private boolean isClosedCamera = false;
//    public AtomicBoolean isCallEstablish = new AtomicBoolean(false);
//
//    private final String[] BASIC_PERMISSIONS = new String[]{Manifest.permission.CAMERA,};
//
//    //是否在录制
//    private boolean isRecording = false;
//
//    // 检查存储
//    private boolean recordWarning = false;
//
//    private CallStateEnum callingState = CallStateEnum.INVALID;
//    private AVChatData avChatData;
//
//    private Activity mActivity;
//    private RBaseViewHolder mViewHolder;
//    private UIBaseView mBaseView;
//
//    private boolean needRestoreLocalVideo = false;
//    private boolean needRestoreLocalAudio = false;
//
//    private String mSessionId;
//
//    private long timeBase = 0;
//
//
//    //Config from Preference
//    private int videoCropRatio;
//    private boolean videoAutoRotate;
//    private int videoQuality;
//    private boolean serverRecordAudio;
//    private boolean serverRecordVideo;
//    private boolean defaultFrontCamera;
//    private boolean autoCallProximity;
//    private int videoHwEncoderMode;
//    private int videoHwDecoderMode;
//    private boolean videoFpsReported;
//    private int audioEffectAecMode;
//    private int audioEffectNsMode;
//    private int videoMaxBitrate;
//    private int deviceDefaultRotation;
//    private int deviceRotationOffset;
//    private boolean audioHighQuality;
//    private boolean audioDtx;
//
//    public AVChatControl(Activity ctx, RBaseViewHolder viewHolder, UIBaseView uiBaseView) {
//        this.mActivity = ctx;
//        this.mViewHolder = viewHolder;
//        this.mBaseView = uiBaseView;
//        this.avChatParameters = new AVChatParameters();
//
//        configFromPreference(new SPUtils(TAG));
//        updateAVChatOptionalConfig();
//        avChatAudio = new AVChatAudio(ctx, viewHolder.v(R.id.avchat_audio_layout), this,this);
//        avChatVideo = new AVChatVideo(ctx, viewHolder.v(R.id.avchat_video_layout), this,this);
//        avChatSurface = new AVChatSurface(ctx, viewHolder.v(R.id.avchat_surface_layout), this);
//
//    }
//
//    /**
//     * 1, autoCallProximity: 语音通话时使用, 距离感应自动黑屏
//     * 2, videoCropRatio: 制定固定的画面裁剪比例，发送端有效
//     * 3, videoAutoRotate: 结合自己设备角度和对方设备角度自动旋转画面
//     * 4, serverRecordAudio: 需要服务器录制语音, 同时需要 APP KEY 下面开通了服务器录制功能
//     * 5, serverRecordVideo: 需要服务器录制视频, 同时需要 APP KEY 下面开通了服务器录制功能
//     * 6, defaultFrontCamera: 默认是否使用前置摄像头
//     * 7, videoQuality: 视频质量调整, 最高建议使用480P
//     * 8, videoFpsReported: 是否开启视频绘制帧率汇报
//     * 9, deviceDefaultRotation: 99.99%情况下你不需要设置这个参数, 当设备固定在水平方向时,并且设备不会移动, 这时是无法确定设备角度的,可以设置一个默认角度
//     * 10, deviceRotationOffset: 99.99%情况下你不需要设置这个参数, 当你的设备传感器获取的角度永远偏移固定值时设置,用于修正旋转角度
//     * 11, videoMaxBitrate: 视频最大码率设置, 100K ~ 5M. 如果没有特殊需求不要去设置,会影响SDK内部的调节机制
//     * 12, audioEffectAecMode: 语音处理选择, 默认使用平台内置,当你发现平台内置不好用时可以设置到SDK内置
//     * 13, audioEffectNsMode: 语音处理选择, 默认使用平台内置,当你发现平台内置不好用时可以设置到SDK内置
//     * 14, videoHwEncoderMode: 视频编码类型, 默认情况下不用设置.
//     * 15, videoHwDecoderMode: 视频解码类型, 默认情况下不用设置.
//     * 16, audioHighQuality: 高清语音，采用更高的采样率来传输语音
//     * 17, audioDtx: 非连续发送，当监测到人声非活跃状态时减少数据包的发送
//     */
//    private void updateAVChatOptionalConfig() {
//        avChatParameters.setBoolean(AVChatParameters.KEY_AUDIO_CALL_PROXIMITY, autoCallProximity);
//        avChatParameters.setInteger(AVChatParameters.KEY_VIDEO_FIXED_CROP_RATIO, videoCropRatio);
//        avChatParameters.setBoolean(AVChatParameters.KEY_VIDEO_ROTATE_IN_RENDING, videoAutoRotate);
////        avChatParameters.setBoolean(AVChatParameters.KEY_SERVER_AUDIO_RECORD, serverRecordAudio);
////        avChatParameters.setBoolean(AVChatParameters.KEY_SERVER_VIDEO_RECORD, serverRecordVideo);
//        avChatParameters.setBoolean(AVChatParameters.KEY_VIDEO_DEFAULT_FRONT_CAMERA, defaultFrontCamera);
//        avChatParameters.setInteger(AVChatParameters.KEY_VIDEO_QUALITY, videoQuality);
//        avChatParameters.setBoolean(AVChatParameters.KEY_VIDEO_FPS_REPORTED, videoFpsReported);
////        avChatParameters.setInteger(AVChatParameters.KEY_DEVICE_DEFAULT_ROTATION, deviceDefaultRotation);
////        avChatParameters.setInteger(AVChatParameters.KEY_DEVICE_ROTATION_FIXED_OFFSET, deviceRotationOffset);
//
//        if (videoMaxBitrate > 0) {
//            avChatParameters.setInteger(AVChatParameters.KEY_VIDEO_MAX_BITRATE, videoMaxBitrate * 1024);
//        }
//        switch (audioEffectAecMode) {
//            case 0:
//                avChatParameters.setString(AVChatParameters.KEY_AUDIO_EFFECT_ACOUSTIC_ECHO_CANCELER, AVChatAudioEffectMode.DISABLE);
//                break;
//            case 1:
//                avChatParameters.setString(AVChatParameters.KEY_AUDIO_EFFECT_ACOUSTIC_ECHO_CANCELER, AVChatAudioEffectMode.SDK_BUILTIN);
//                break;
//            case 2:
//                avChatParameters.setString(AVChatParameters.KEY_AUDIO_EFFECT_ACOUSTIC_ECHO_CANCELER, AVChatAudioEffectMode.PLATFORM_BUILTIN);
//                break;
//        }
//        switch (audioEffectNsMode) {
//            case 0:
//                avChatParameters.setString(AVChatParameters.KEY_AUDIO_EFFECT_NOISE_SUPPRESSOR, AVChatAudioEffectMode.DISABLE);
//                break;
//            case 1:
//                avChatParameters.setString(AVChatParameters.KEY_AUDIO_EFFECT_NOISE_SUPPRESSOR, AVChatAudioEffectMode.SDK_BUILTIN);
//                break;
//            case 2:
//                avChatParameters.setString(AVChatParameters.KEY_AUDIO_EFFECT_NOISE_SUPPRESSOR, AVChatAudioEffectMode.PLATFORM_BUILTIN);
//                break;
//        }
//        switch (videoHwEncoderMode) {
//            case 0:
//                avChatParameters.setString(AVChatParameters.KEY_VIDEO_ENCODER_MODE, AVChatMediaCodecMode.MEDIA_CODEC_AUTO);
//                break;
//            case 1:
//                avChatParameters.setString(AVChatParameters.KEY_VIDEO_ENCODER_MODE, AVChatMediaCodecMode.MEDIA_CODEC_SOFTWARE);
//                break;
//            case 2:
//                avChatParameters.setString(AVChatParameters.KEY_VIDEO_ENCODER_MODE, AVChatMediaCodecMode.MEDIA_CODEC_HARDWARE);
//                break;
//        }
//        switch (videoHwDecoderMode) {
//            case 0:
//                avChatParameters.setString(AVChatParameters.KEY_VIDEO_DECODER_MODE, AVChatMediaCodecMode.MEDIA_CODEC_AUTO);
//                break;
//            case 1:
//                avChatParameters.setString(AVChatParameters.KEY_VIDEO_DECODER_MODE, AVChatMediaCodecMode.MEDIA_CODEC_SOFTWARE);
//                break;
//            case 2:
//                avChatParameters.setString(AVChatParameters.KEY_VIDEO_DECODER_MODE, AVChatMediaCodecMode.MEDIA_CODEC_HARDWARE);
//                break;
//        }
//        avChatParameters.setBoolean(AVChatParameters.KEY_AUDIO_HIGH_QUALITY, audioHighQuality);
//        avChatParameters.setBoolean(AVChatParameters.KEY_AUDIO_DTX_ENABLE, audioDtx);
//
//        //观众角色,多人模式下使用. IM Demo没有多人通话, 全部设置为AVChatUserRole.NORMAL.
//        avChatParameters.setInteger(AVChatParameters.KEY_SESSION_MULTI_MODE_USER_ROLE, AVChatUserRole.NORMAL);
//    }
//
//    private void configFromPreference(SPUtils preferences) {
//        videoCropRatio = Integer.parseInt(preferences.getString(mActivity.getString(R.string.nrtc_setting_vie_crop_ratio_key), "0"));
//        videoAutoRotate = preferences.getBoolean(mActivity.getString(R.string.nrtc_setting_vie_rotation_key), true);
//        videoQuality = Integer.parseInt(preferences.getString(mActivity.getString(R.string.nrtc_setting_vie_quality_key), 0 + ""));
//        serverRecordAudio = preferences.getBoolean(mActivity.getString(R.string.nrtc_setting_other_server_record_audio_key), false);
//        serverRecordVideo = preferences.getBoolean(mActivity.getString(R.string.nrtc_setting_other_server_record_video_key), false);
//        defaultFrontCamera = preferences.getBoolean(mActivity.getString(R.string.nrtc_setting_vie_default_front_camera_key), true);
//        autoCallProximity = preferences.getBoolean(mActivity.getString(R.string.nrtc_setting_voe_call_proximity_key), true);
//        videoHwEncoderMode = Integer.parseInt(preferences.getString(mActivity.getString(R.string.nrtc_setting_vie_hw_encoder_key), 0 + ""));
//        videoHwDecoderMode = Integer.parseInt(preferences.getString(mActivity.getString(R.string.nrtc_setting_vie_hw_decoder_key), 0 + ""));
//        videoFpsReported = preferences.getBoolean(mActivity.getString(R.string.nrtc_setting_vie_fps_reported_key), true);
//        audioEffectAecMode = Integer.parseInt(preferences.getString(mActivity.getString(R.string.nrtc_setting_voe_audio_aec_key), 2 + ""));
//        audioEffectNsMode = Integer.parseInt(preferences.getString(mActivity.getString(R.string.nrtc_setting_voe_audio_ns_key), 2 + ""));
//        String value1 = preferences.getString(mActivity.getString(R.string.nrtc_setting_vie_max_bitrate_key), 0 + "");
//        videoMaxBitrate = Integer.parseInt(TextUtils.isDigitsOnly(value1) && !TextUtils.isEmpty(value1) ? value1 : 0 + "");
//        String value2 = preferences.getString(mActivity.getString(R.string.nrtc_setting_other_device_default_rotation_key), 0 + "");
//        deviceDefaultRotation = Integer.parseInt(TextUtils.isDigitsOnly(value2) && !TextUtils.isEmpty(value2) ? value2 : 0 + "");
//        String value3 = preferences.getString(mActivity.getString(R.string.nrtc_setting_other_device_rotation_fixed_offset_key), 0 + "");
//        deviceRotationOffset = Integer.parseInt(TextUtils.isDigitsOnly(value3) && !TextUtils.isEmpty(value3) ? value3 : 0 + "");
//        audioHighQuality = preferences.getBoolean(mActivity.getString(R.string.nrtc_setting_voe_high_quality_key), false);
//        audioDtx = preferences.getBoolean(mActivity.getString(R.string.nrtc_setting_voe_dtx_key), true);
//    }
//
//
//
//    public void closeSessions(int exitCode) {
//        if (avChatAudio != null)
//            avChatAudio.closeSession(exitCode);
//        if (avChatVideo != null)
//            avChatVideo.closeSession(exitCode);
////        showQuitToast(exitCode);
//        isCallEstablish.set(false);
//        canSwitchCamera = false;
//        isClosedCamera = false;
////        aVChatListener.uiExit();
//    }
//
//    public String getVideoAccount() {
//        return videoAccount;
//    }
//
//    /**
//     * 音频切换为视频的请求
//     */
//    public void incomingAudioToVideo() {
//        onCallStateChange(CallStateEnum.INCOMING_AUDIO_TO_VIDEO);
//    }
//
//    /**
//     * 状态改变
//     *
//     * @param stateEnum
//     */
//    public void onCallStateChange(CallStateEnum stateEnum) {
//        callingState = stateEnum;
//        avChatSurface.onCallStateChange(stateEnum);
//        avChatAudio.onCallStateChange(stateEnum);
//        avChatVideo.onCallStateChange(stateEnum);
//    }
//
//    public void peerVideoOff() {
//        avChatSurface.peerVideoOff();
//    }
//
//    public void peerVideoOn() {
//        avChatSurface.peerVideoOn();
//    }
//
//    /**
//     * 音频切换为视频
//     */
//    public void onAudioToVideo() {
//        onCallStateChange(CallStateEnum.VIDEO);
//        avChatVideo.onAudioToVideo(AVChatManager.getInstance().isLocalAudioMuted(),
//                isRecording, recordWarning); // isMute是否处于静音状态
//
//        //打开视频
//        AVChatManager.getInstance().enableVideo();
//        AVChatManager.getInstance().startVideoPreview();
//
//        // 是否在发送视频 即摄像头是否开启
//        if (AVChatManager.getInstance().isLocalVideoMuted()) {
//            AVChatManager.getInstance().muteLocalVideo(false);
//            avChatSurface.localVideoOn();
//            isClosedCamera = false;
//        }
//    }
//
//    /**
//     * 视频切换为音频
//     */
//    public void onVideoToAudio() {
//
//        //关闭视频
//        AVChatManager.getInstance().stopVideoPreview();
//        AVChatManager.getInstance().disableVideo();
//
//        // 判断是否静音，扬声器是否开启，对界面相应控件进行显隐处理。
//        avChatAudio.onVideoToAudio(AVChatManager.getInstance().isLocalAudioMuted(),
//                AVChatManager.getInstance().speakerEnabled(),
//                isRecording, recordWarning);
//    }
//
//    /**
//     * 初始化大小图像
//     *
//     * @param largeAccount 对方的帐号
//     */
//    public void initAllSurfaceView(String largeAccount) {
//        avChatSurface.initLargeSurfaceView(largeAccount);
//        avChatSurface.initSmallSurfaceView(UserCache.getUserAccount());
//    }
//
//
//    public AVChatData getAvChatData() {
//        return avChatData;
//    }
//
//    public void resumeVideo() {
//        if (needRestoreLocalVideo) {
//            AVChatManager.getInstance().muteLocalVideo(false);
//            needRestoreLocalVideo = false;
//        }
//
//        if (needRestoreLocalAudio) {
//            AVChatManager.getInstance().muteLocalAudio(false);
//            needRestoreLocalAudio = false;
//        }
//    }
//
//    public void pauseVideo() {
//        if (!AVChatManager.getInstance().isLocalVideoMuted()) {
//            AVChatManager.getInstance().muteLocalVideo(true);
//            needRestoreLocalVideo = true;
//        }
//
//        if (!AVChatManager.getInstance().isLocalAudioMuted()) {
//            AVChatManager.getInstance().muteLocalAudio(true);
//            needRestoreLocalAudio = true;
//        }
//    }
//
//    /**
//     * 拨打音视频
//     */
//    public void outGoingCalling(String receiverId, final AVChatType callTypeEnum) {
//
//        AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.CONNECTING);
//        mSessionId = receiverId;
//
//        AVChatNotifyOption notifyOption = new AVChatNotifyOption();
//        notifyOption.extendMessage = "extra_data";
////        默认forceKeepCalling为true，开发者如果不需要离线持续呼叫功能可以将forceKeepCalling设为false
////        notifyOption.forceKeepCalling = false;
//
//        AVChatManager.getInstance().enableRtc();
//        this.callingState = (callTypeEnum == AVChatType.VIDEO ? CallStateEnum.VIDEO : CallStateEnum.AUDIO);
//        AVChatManager.getInstance().setParameters(avChatParameters);
//        if (callTypeEnum == AVChatType.VIDEO) {
//            AVChatManager.getInstance().enableVideo();
//            AVChatManager.getInstance().startVideoPreview();
//        }
//
//        AVChatManager.getInstance().call2(mSessionId, callTypeEnum, notifyOption, new AVChatCallback<AVChatData>() {
//            @Override
//            public void onSuccess(AVChatData data) {
//                avChatData = data;
//                //如果需要使用视频预览功能，在此进行设置，调用setupLocalVideoRender
//                //如果不需要视频预览功能，那么删掉下面if语句代码即可
//                if (callTypeEnum == AVChatType.VIDEO) {
//                    new RxPermissions(mActivity).requestEach(BASIC_PERMISSIONS).subscribe(new Action1<Permission>() {
//                        @Override
//                        public void call(Permission permission) {
//                            if (permission.granted) {
//                                initLargeSurfaceView(UserCache.getUserAccount());
//                                canSwitchCamera = true;
//                                onCallStateChange(CallStateEnum.OUTGOING_VIDEO_CALLING);
//                            } else {
//                                avChatVideo.showNoneCameraPermissionView(true);
//                                return;
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailed(int code) {
//                L.d(TAG, "avChat call failed code->" + code);
//
//                AVChatSoundPlayer.instance().stop();
//
//                if (code == ResponseCode.RES_FORBIDDEN) {
////                    Toast.makeText(context, R.string.avchat_no_permission, Toast.LENGTH_SHORT).show();
//                } else {
////                    Toast.makeText(context, R.string.avchat_call_failed, Toast.LENGTH_SHORT).show();
//                }
//                closeSessions(-1);
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                L.d(TAG, "avChat call onException->" + exception);
////                DialogMaker.dismissProgressDialog();
//
//                AVChatSoundPlayer.instance().stop();
//            }
//        });
//
//        if (callTypeEnum == AVChatType.AUDIO) {
//            onCallStateChange(CallStateEnum.OUTGOING_AUDIO_CALLING);
//        } else {
//            onCallStateChange(CallStateEnum.OUTGOING_VIDEO_CALLING);
//        }
//    }
//
//    public void inComingCalling(AVChatData avChatData) {
//        this.avChatData = avChatData;
//        mSessionId = avChatData.getAccount();
//
//        AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.RING);
//
//        if (avChatData.getChatType() == AVChatType.AUDIO) {
//            onCallStateChange(CallStateEnum.INCOMING_AUDIO_CALLING);
//        } else {
//            onCallStateChange(CallStateEnum.INCOMING_VIDEO_CALLING);
//        }
//
//    }
//
//    public void initLargeSurfaceView(String account) {
//        avChatSurface.initLargeSurfaceView(account);
//    }
//
//    public CallStateEnum getCallingState() {
//        return null;
//    }
//
//    public String getAccount() {
//        if (mSessionId != null)
//            return mSessionId;
//        return null;
//    }
//
//    public long getTimeBase() {
//        return timeBase;
//    }
//
//    public void resetRecordTip() {
//        recordWarning = false;
//        isRecording = false;
//        updateRecordTip();
//    }
//
//    private void updateRecordTip() {
//        if (CallStateEnum.isAudioMode(callingState)) {
//            avChatAudio.showRecordView(isRecording, recordWarning);
//        }
//        if (CallStateEnum.isVideoMode(callingState)) {
//            avChatVideo.showRecordView(isRecording, recordWarning);
//        }
//    }
//
//    public void setVideoAccount(String account) {
//        this.videoAccount = account;
//    }
//
//    public void showRecordWarning() {
//        recordWarning = true;
//        updateRecordTip();
//    }
//
//
//    public void setTimeBase(long timeBase) {
//        this.timeBase = timeBase;
//    }
//
//    public void initSmallSurfaceView() {
//        avChatSurface.initSmallSurfaceView(UserCache.getUserAccount());
//    }
//
//    @Override
//    public void onHangUp() {
//        if (isCallEstablish.get()) {
//            hangUp(AVChatExitCode.HANGUP);
//        } else {
//            hangUp(AVChatExitCode.CANCEL);
//        }
//    }
//
//
//    /**
//     * 挂断
//     *
//     * @param type 音视频类型
//     */
//    private void hangUp(final int type) {
//        if (callingState == CallStateEnum.INCOMING_VIDEO_CALLING || callingState == CallStateEnum.VIDEO) {
//            AVChatManager.getInstance().stopVideoPreview();
//        }
//        if ((type == AVChatExitCode.HANGUP || type == AVChatExitCode.PEER_NO_RESPONSE || type == AVChatExitCode.CANCEL) && avChatData != null) {
//            AVChatManager.getInstance().hangUp2(avChatData.getChatId(), new AVChatCallback<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                }
//
//                @Override
//                public void onFailed(int code) {
//                    L.d(TAG, "hangup onFailed->" + code);
//                }
//
//                @Override
//                public void onException(Throwable exception) {
//                    L.d(TAG, "hangup onException->" + exception);
//                }
//            });
//        }
//        AVChatManager.getInstance().disableRtc();
//        closeSessions(type);
//        AVChatSoundPlayer.instance().stop();
//    }
//
//    /**
//     * 拒绝操作，根据当前状态来选择合适的操作
//     */
//    @Override
//    public void onRefuse() {
//        switch (callingState) {
//            case INCOMING_AUDIO_CALLING:
//            case AUDIO_CONNECTING:
//                rejectInComingCall();
//                break;
//            case INCOMING_AUDIO_TO_VIDEO:
//                rejectAudioToVideo();
//                break;
//            case INCOMING_VIDEO_CALLING:
//            case VIDEO_CONNECTING: // 连接中点击拒绝
//                rejectInComingCall();
//                break;
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 拒绝音视频切换
//     */
//    private void rejectAudioToVideo() {
//        AVChatManager.getInstance().sendControlCommand(avChatData.getChatId(), AVChatControlCommand.SWITCH_AUDIO_TO_VIDEO_REJECT, new AVChatCallback<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                L.i(TAG, "rejectAudioToVideo success");
//                onCallStateChange(CallStateEnum.AUDIO);
//                updateRecordTip();
//            }
//
//            @Override
//            public void onFailed(int code) {
//                L.i(TAG, "rejectAudioToVideo onFailed");
//
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                L.i(TAG, "rejectAudioToVideo onException");
//            }
//        });
//    }
//
//    /**
//     * 拒绝来电
//     */
//    private void rejectInComingCall() {
//        /**
//         * 接收方拒绝通话
//         * AVChatCallback 回调函数
//         */
//        AVChatManager.getInstance().hangUp2(avChatData.getChatId(), new AVChatCallback<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                L.d(TAG, "reject onSuccess-" );
//            }
//
//            @Override
//            public void onFailed(int code) {
//                L.d(TAG, "reject onFailed->" + code);
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                L.d(TAG, "reject onException");
//            }
//        });
//        closeSessions(AVChatExitCode.REJECT);
//        AVChatSoundPlayer.instance().stop();
//    }
//
//    /**
//     * 开启操作，根据当前状态来选择合适的操作
//     */
//    @Override
//    public void onReceive() {
//        switch (callingState) {
//            case INCOMING_AUDIO_CALLING:
//                receiveInComingCall();
//                onCallStateChange(CallStateEnum.AUDIO_CONNECTING);
//                break;
//            case AUDIO_CONNECTING: // 连接中，继续点击开启 无反应
//                break;
//            case INCOMING_VIDEO_CALLING:
//                receiveInComingCall();
//                onCallStateChange(CallStateEnum.VIDEO_CONNECTING);
//                break;
//            case VIDEO_CONNECTING: // 连接中，继续点击开启 无反应
//                break;
//            case INCOMING_AUDIO_TO_VIDEO:
//                receiveAudioToVideo();
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 接听来电
//     */
//    private void receiveInComingCall() {
//        //接听，告知服务器，以便通知其他端
//
//        if (callingState == CallStateEnum.INCOMING_AUDIO_CALLING) {
//            onCallStateChange(CallStateEnum.AUDIO_CONNECTING);
//        } else {
//            onCallStateChange(CallStateEnum.VIDEO_CONNECTING);
//        }
//
//        AVChatManager.getInstance().enableRtc();
//        AVChatManager.getInstance().setParameters(avChatParameters);
//        if (callingState == CallStateEnum.VIDEO_CONNECTING) {
//            AVChatManager.getInstance().enableVideo();
//            AVChatManager.getInstance().startVideoPreview();
//        }
//
//        AVChatManager.getInstance().accept2(avChatData.getChatId(), new AVChatCallback<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                L.i(TAG, "accept success");
//
//                isCallEstablish.set(true);
//                canSwitchCamera = true;
//            }
//
//            @Override
//            public void onFailed(int code) {
//                if (code == -1) {
//                    Toast.makeText(mActivity, "本地音视频启动失败", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(mActivity, "建立连接失败", Toast.LENGTH_SHORT).show();
//                }
//                L.e(TAG, "accept onFailed->" + code);
//                closeSessions(AVChatExitCode.CANCEL);
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                L.d(TAG, "accept exception->" + exception);
//            }
//        });
//        AVChatSoundPlayer.instance().stop();
//    }
//
//    /**
//     * 同意音频切换为视频
//     */
//    private void receiveAudioToVideo() {
//        AVChatManager.getInstance().sendControlCommand(avChatData.getChatId(), AVChatControlCommand.SWITCH_AUDIO_TO_VIDEO_AGREE, new AVChatCallback<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                L.d(TAG, "receiveAudioToVideo onSuccess");
//                // 切换
//                AVChatManager.getInstance().enableVideo();
//
//                onAudioToVideo();
//                initAllSurfaceView(videoAccount);
//            }
//
//            @Override
//            public void onFailed(int code) {
//                L.d(TAG, "receiveAudioToVideo onFailed");
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                L.d(TAG, "receiveAudioToVideo onException");
//            }
//        });
//
//    }
//
//    @Override
//    public void toggleMute() {
//        if (!isCallEstablish.get()) { // 连接未建立，在这里记录静音状态
//            return;
//        } else { // 连接已经建立
//            if (!AVChatManager.getInstance().isLocalAudioMuted()) { // isMute是否处于静音状态
//                // 关闭音频
//                AVChatManager.getInstance().muteLocalAudio(true);
//            } else {
//                // 打开音频
//                AVChatManager.getInstance().muteLocalAudio(false);
//            }
//        }
//    }
//
//    @Override
//    public void toggleSpeaker() {
//        AVChatManager.getInstance().setSpeaker(!AVChatManager.getInstance().speakerEnabled()); // 设置扬声器是否开启
//    }
//
//    @Override
//    public void toggleRecord() {
//
//        //录制...
//
//    }
//    /**
//     * 请求视频切换到音频
//     */
//    @Override
//    public void videoSwitchAudio() {
//        AVChatManager.getInstance().sendControlCommand(avChatData.getChatId(), AVChatControlCommand.SWITCH_VIDEO_TO_AUDIO, new AVChatCallback<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                L.d(TAG, "videoSwitchAudio onSuccess");
//                //直接切换
//                AVChatManager.getInstance().disableVideo();
//
//                // 界面布局切换。
//                onCallStateChange(CallStateEnum.AUDIO);
//                onVideoToAudio();
//            }
//
//            @Override
//            public void onFailed(int code) {
//                L.d(TAG, "videoSwitchAudio onFailed");
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                L.d(TAG, "videoSwitchAudio onException");
//            }
//        });
//    }
//
//
//    /**
//     * 请求音频切换到视频
//     */
//    @Override
//    public void audioSwitchVideo() {
//
//        AVChatManager.getInstance().sendControlCommand(avChatData.getChatId(), AVChatControlCommand.SWITCH_AUDIO_TO_VIDEO, new AVChatCallback<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                L.d(TAG, "requestSwitchToVideo onSuccess");
//                onCallStateChange(CallStateEnum.OUTGOING_AUDIO_TO_VIDEO);
//                updateRecordTip();
//            }
//
//            @Override
//            public void onFailed(int code) {
//                L.d(TAG, "requestSwitchToVideo onFailed" + code);
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                L.d(TAG, "requestSwitchToVideo onException" + exception);
//            }
//        });
//    }
//
//    @Override
//    public void switchCamera() {
//        AVChatManager.getInstance().switchCamera(); // 切换摄像头（主要用于前置和后置摄像头切换）
//    }
//
//    @Override
//    public void closeCamera() {
//        if (!isClosedCamera) {
//            // 关闭摄像头
//            AVChatManager.getInstance().muteLocalVideo(true);
//            isClosedCamera = true;
//            avChatSurface.localVideoOff();
//        } else {
//            // 打开摄像头
//            AVChatManager.getInstance().muteLocalVideo(false);
//            isClosedCamera = false;
//            avChatSurface.localVideoOn();
//        }
//    }
//}
