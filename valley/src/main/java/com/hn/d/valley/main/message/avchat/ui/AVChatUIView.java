package com.hn.d.valley.main.message.avchat.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.utils.NetworkUtil;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.main.avchat.AVChatNotification;
import com.hn.d.valley.main.avchat.AVChatSoundPlayer;
import com.hn.d.valley.main.avchat.activity.AVChatExitCode;
import com.hn.d.valley.main.avchat.constant.CallStateEnum;
import com.hn.d.valley.main.avchat.receiver.PhoneCallStateObserver;
import com.hn.d.valley.main.message.avchat.AVChatControl;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
import com.netease.nimlib.sdk.avchat.constant.AVChatEventType;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatAudioFrame;
import com.netease.nimlib.sdk.avchat.model.AVChatCalleeAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatControlEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatOnlineAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoFrame;

import java.util.Map;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/22 17:03
 * 修改人员：hewking
 * 修改时间：2017/05/22 17:03
 * 修改备注：
 * Version: 1.0.0
 */
public class AVChatUIView extends BaseUIView implements AVChatStateObserver{

    //constant
    private static final String TAG = AVChatUIView.class.getSimpleName();
    public static final String KEY_IN_CALLING = "KEY_IN_CALLING";
    public static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    public static final String KEY_CALL_TYPE = "KEY_CALL_TYPE";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_CALL_CONFIG = "KEY_CALL_CONFIG";
    public static final String INTENT_ACTION_AVCHAT = "INTENT_ACTION_AVCHAT";

    /**
     * 来自广播
     */
    public static final int FROM_BROADCASTRECEIVER = 0;
    /**
     * 来自发起方
     */
    public static final int FROM_INTERNAL = 1;
    /**
     * 来自通知栏
     */
    public static final int FROM_NOTIFICATION = 2;
    /**
     * 未知的入口
     */
    public static final int FROM_UNKNOWN = -1;

    // data
    private AVChatControl avChatControl; // 音视频总管理器
    private AVChatData avChatData; // config for connect video server

    // state
    private boolean isUserFinish = false;
    private boolean mIsInComingCall = false;// is incoming call or outgoing call
    private boolean isCallEstablished = false; // 电话是否接通
    private static boolean needFinish = true; // 若来电或去电未接通时，点击home。另外一方挂断通话。从最近任务列表恢复，则finish
    private boolean hasOnPause = false; // 是否暂停音视频

    // notification
    private AVChatNotification notifier;

    // 启动传递的参数
    private String sessionId;
    private int callType;
    private int source;


    public static void start(ILayout layout , String sessionId , int callType , int source) {
        needFinish = false;
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ACCOUNT,sessionId);
        bundle.putInt(KEY_CALL_TYPE,callType);
        bundle.putInt(KEY_SOURCE, source);
        bundle.putBoolean(KEY_IN_CALLING, false);
        layout.startIView(new AVChatUIView(),new UIParam().setBundle(bundle));
    }

    public static void start(ILayout layout , AVChatData config , int source) {
        needFinish = false;
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CALL_CONFIG,config);
        bundle.putInt(KEY_SOURCE, source);
        bundle.putBoolean(KEY_IN_CALLING, true);
        layout.startIView(new AVChatUIView(),new UIParam().setBundle(bundle));
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_message_avchat_layout);
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        parseParam(param);
        registerNetCallObserver(true);


    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    /**
     * 拨打
     */
    private void outgoingCalling() {
        if (!NetworkUtil.isNetAvailable(mActivity)) { // 网络不可用
            T_.show(getString(R.string.no_network));
            finishIView();
            return;
        }
        avChatControl.outGoingCalling(sessionId, AVChatType.typeOfValue(callType));
    }

    /**
     * 接听
     */
    private void inComingCalling() {
        avChatControl.inComingCalling(avChatData);
    }

    private void parseParam(UIParam param) {
        if (param.mBundle != null) {
            this.sessionId = param.mBundle.getString(KEY_ACCOUNT);
            this.callType = param.mBundle.getInt(KEY_CALL_TYPE,-1);
            this.source = param.mBundle.getInt(KEY_SOURCE,FROM_UNKNOWN);
            avChatData = (AVChatData) param.mBundle.getSerializable(KEY_CALL_CONFIG);
        }
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        cancelCallingNotifier();
        if (hasOnPause) {
            avChatControl.resumeVideo();
            hasOnPause = false;
        }
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        avChatControl.pauseVideo(); // 暂停视频聊天（用于在视频聊天过程中，APP退到后台时必须调用）
        hasOnPause = true;
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        registerNetCallObserver(false);
        activeCallingNotifier();

    }

    private void activeCallingNotifier() {
        if (notifier != null && !isUserFinish) {
            notifier.activeCallingNotification(true);
        }
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        avChatControl = new AVChatControl(mActivity,mViewHolder,this);

        if (mIsInComingCall) {
            inComingCalling();
        } else {
            outgoingCalling();
        }

//        notifier = new AVChatNotification(mActivity);
//        notifier.init(receiverId != null ? receiverId : avChatData.getAccount());
    }

    /**
     * 注册监听
     *
     * @param register
     */
    private void registerNetCallObserver(boolean register) {
        AVChatManager.getInstance().observeAVChatState(this, register);
        AVChatManager.getInstance().observeCalleeAckNotification(callAckObserver, register);
        AVChatManager.getInstance().observeControlNotification(callControlObserver, register);
        AVChatManager.getInstance().observeHangUpNotification(callHangupObserver, register);
        AVChatManager.getInstance().observeOnlineAckNotification(onlineAckObserver, register);
        AVChatManager.getInstance().observeTimeoutNotification(timeoutObserver, register);
        PhoneCallStateObserver.getInstance().observeAutoHangUpForLocalPhone(autoHangUpForLocalPhoneObserver, register);
    }

    /**
     * 注册/注销网络通话被叫方的响应（接听、拒绝、忙）
     */
    Observer<AVChatCalleeAckEvent> callAckObserver = new Observer<AVChatCalleeAckEvent>() {
        @Override
        public void onEvent(AVChatCalleeAckEvent ackInfo) {
            AVChatSoundPlayer.instance().stop();
            if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_BUSY) {
                AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.PEER_BUSY);
                avChatControl.closeSessions(AVChatExitCode.PEER_BUSY);
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_REJECT) {
                avChatControl.closeSessions(AVChatExitCode.REJECT);
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_AGREE) {
                avChatControl.isCallEstablish.set(true);
                avChatControl.canSwitchCamera = true;
            }
        }
    };

    /**
     * 注册/注销网络通话控制消息（音视频模式切换通知）
     */
    Observer<AVChatControlEvent> callControlObserver = new Observer<AVChatControlEvent>() {
        @Override
        public void onEvent(AVChatControlEvent netCallControlNotification) {
            handleCallControl(netCallControlNotification);
        }
    };

    /**
     * 注册/注销网络通话对方挂断的通知
     */
    Observer<AVChatCommonEvent> callHangupObserver = new Observer<AVChatCommonEvent>() {
        @Override
        public void onEvent(AVChatCommonEvent avChatHangUpInfo) {

            AVChatSoundPlayer.instance().stop();

            avChatControl.closeSessions(AVChatExitCode.HANGUP);
            cancelCallingNotifier();
            // 如果是incoming call主叫方挂断，那么通知栏有通知
            if (mIsInComingCall && !isCallEstablished) {
                activeMissCallNotifier();
            }
        }
    };

    /**
     * 注册/注销同时在线的其他端对主叫方的响应
     */
    Observer<AVChatOnlineAckEvent> onlineAckObserver = new Observer<AVChatOnlineAckEvent>() {
        @Override
        public void onEvent(AVChatOnlineAckEvent ackInfo) {

            AVChatSoundPlayer.instance().stop();

            String client = null;
            switch (ackInfo.getClientType()) {
                case ClientType.Web:
                    client = "Web";
                    break;
                case ClientType.Windows:
                    client = "Windows";
                    break;
                case ClientType.Android:
                    client = "Android";
                    break;
                case ClientType.iOS:
                    client = "iOS";
                    break;
                default:
                    break;
            }
            if (client != null) {
                String option = ackInfo.getEvent() == AVChatEventType.CALLEE_ONLINE_CLIENT_ACK_AGREE ? "接听！" : "拒绝！";
                T_.show("通话已在" + client + "端被" + option);
            }
            avChatControl.closeSessions(-1);
        }
    };


    private void cancelCallingNotifier() {
        if (notifier != null) {
            notifier.activeCallingNotification(false);
        }
    }

    private void activeMissCallNotifier() {
        if (notifier != null) {
            notifier.activeMissCallNotification(true);
        }
    }

    /**
     * 处理音视频切换请求
     *
     * @param notification
     */
    private void handleCallControl(AVChatControlEvent notification) {
        switch (notification.getControlCommand()) {
            case AVChatControlCommand.SWITCH_AUDIO_TO_VIDEO:
                avChatControl.incomingAudioToVideo();
                break;
            case AVChatControlCommand.SWITCH_AUDIO_TO_VIDEO_AGREE:
                onAudioToVideo();
                break;
            case AVChatControlCommand.SWITCH_AUDIO_TO_VIDEO_REJECT:
                avChatControl.onCallStateChange(CallStateEnum.AUDIO);
                T_.show("音视频切换拒绝!");
                break;
            case AVChatControlCommand.SWITCH_VIDEO_TO_AUDIO:
                onVideoToAudio();
                break;
            case AVChatControlCommand.NOTIFY_VIDEO_OFF:
                avChatControl.peerVideoOff();
                break;
            case AVChatControlCommand.NOTIFY_VIDEO_ON:
                avChatControl.peerVideoOn();
                break;
            default:
                T_.show("对方发来指令值：" + notification.getControlCommand());
                break;
        }
    }

    Observer<Long> timeoutObserver = new Observer<Long>() {
        @Override
        public void onEvent(Long chatId) {
            AVChatData info = avChatControl.getAvChatData();
            if (info != null && info.getChatId() == chatId) {
                avChatControl.closeSessions(AVChatExitCode.PEER_NO_RESPONSE);
                // 来电超时，自己未接听
                if (mIsInComingCall) {
                    activeMissCallNotifier();
                }
                AVChatSoundPlayer.instance().stop();
            }

        }
    };

    Observer<Integer> autoHangUpForLocalPhoneObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer integer) {
            AVChatSoundPlayer.instance().stop();
            avChatControl.closeSessions(AVChatExitCode.PEER_BUSY);
        }
    };

    /**
     * 音频切换为视频
     */
    private void onAudioToVideo() {
        avChatControl.onAudioToVideo();
        avChatControl.initAllSurfaceView(avChatControl.getVideoAccount());
    }

    /**
     * 视频切换为音频
     */
    private void onVideoToAudio() {
        avChatControl.onCallStateChange(CallStateEnum.AUDIO);
        avChatControl.onVideoToAudio();
    }


    @Override
    public void onTakeSnapshotResult(String account, boolean success, String file) {

    }

    @Override
    public void onConnectionTypeChanged(int netType) {

    }

    @Override
    public void onAVRecordingCompletion(String account, String filePath) {
        if (account != null && filePath != null && filePath.length() > 0) {
            String msg = "音视频录制已结束, " + "账号：" + account + " 录制文件已保存至：" + filePath;
//            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "录制已结束.", Toast.LENGTH_SHORT).show();
        }
        if (avChatControl != null) {
            avChatControl.resetRecordTip();
        }
    }

    @Override
    public void onAudioRecordingCompletion(String filePath) {
        if (filePath != null && filePath.length() > 0) {
            String msg = "音频录制已结束, 录制文件已保存至：" + filePath;
//            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "录制已结束.", Toast.LENGTH_SHORT).show();
        }
        if (avChatControl != null) {
            avChatControl.resetRecordTip();
        }
    }

    @Override
    public void onLowStorageSpaceWarning(long availableSize) {
        if (avChatControl != null) {
            avChatControl.showRecordWarning();
        }
    }

    @Override
    public void onFirstVideoFrameAvailable(String account) {

    }

    @Override
    public void onVideoFpsReported(String account, int fps) {

    }

    @Override
    public void onJoinedChannel(int code, String audioFile, String videoFile) {
        handleWithConnectServerResult(code);
    }

    private void handleWithConnectServerResult(int auth_result) {
        if (auth_result == 200) {
            Log.d(TAG, "onConnectServer success");
        } else if (auth_result == 101) { // 连接超时
            avChatControl.closeSessions(AVChatExitCode.PEER_NO_RESPONSE);
        } else if (auth_result == 401) { // 验证失败
            avChatControl.closeSessions(AVChatExitCode.CONFIG_ERROR);
        } else if (auth_result == 417) { // 无效的channelId
            avChatControl.closeSessions(AVChatExitCode.INVALIDE_CHANNELID);
        } else { // 连接服务器错误，直接退出
            avChatControl.closeSessions(AVChatExitCode.CONFIG_ERROR);
        }
    }

    @Override
    public void onLeaveChannel() {

    }

    @Override
    public void onUserJoined(String account) {
        avChatControl.setVideoAccount(account);
        avChatControl.initLargeSurfaceView(avChatControl.getVideoAccount());
    }

    @Override
    public void onUserLeave(String account, int event) {

    }

    @Override
    public void onProtocolIncompatible(int status) {

    }

    @Override
    public void onDisconnectServer() {

    }

    @Override
    public void onNetworkQuality(String user, int value) {

    }

    @Override
    public void onCallEstablished() {
        if (avChatControl.getTimeBase() == 0)
            avChatControl.setTimeBase(SystemClock.elapsedRealtime());

        if (callType == AVChatType.AUDIO.getValue()) {
            avChatControl.onCallStateChange(CallStateEnum.AUDIO);
        } else {
            avChatControl.initSmallSurfaceView();
            avChatControl.onCallStateChange(CallStateEnum.VIDEO);
        }
        isCallEstablished = true;
    }

    @Override
    public void onDeviceEvent(int code, String desc) {

    }

    @Override
    public void onFirstVideoFrameRendered(String user) {

    }

    @Override
    public void onVideoFrameResolutionChanged(String user, int width, int height, int rotate) {

    }

    @Override
    public boolean onVideoFrameFilter(AVChatVideoFrame frame) {
        return false;
    }

    @Override
    public boolean onAudioFrameFilter(AVChatAudioFrame frame) {
        return false;
    }

    @Override
    public void onAudioDeviceChanged(int device) {

    }

    @Override
    public void onReportSpeaker(Map<String, Integer> speakers, int mixedEnergy) {

    }

    @Override
    public void onAudioMixingEvent(int event) {

    }
}
