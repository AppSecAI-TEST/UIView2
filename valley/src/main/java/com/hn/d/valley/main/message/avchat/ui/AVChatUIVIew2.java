package com.hn.d.valley.main.message.avchat.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.main.avchat.AVChatFloatEvent;
import com.hn.d.valley.main.avchat.AVChatNotification;
import com.hn.d.valley.main.avchat.AVChatProfile;
import com.hn.d.valley.main.avchat.AVChatSoundPlayer;
import com.hn.d.valley.main.avchat.AVChatUI;
import com.hn.d.valley.main.avchat.activity.AVChatExitCode;
import com.hn.d.valley.main.avchat.constant.CallStateEnum;
import com.hn.d.valley.main.avchat.receiver.PhoneCallStateObserver;
import com.hn.d.valley.main.message.p2pchat.P2PChatUIView;
import com.hn.d.valley.utils.RBus;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
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
 * 音视频界面
 */
public class AVChatUIVIew2 extends BaseUIView implements AVChatUI.AVChatListener, AVChatStateObserver {
    // constant
    private static final String TAG = "AVChatActivity";
    private static final String KEY_IN_CALLING = "KEY_IN_CALLING";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private static final String KEY_CALL_TYPE = "KEY_CALL_TYPE";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_CALL_CONFIG = "KEY_CALL_CONFIG";
    public static final String INTENT_ACTION_AVCHAT = "INTENT_ACTION_AVCHAT";

    public static final int PREVIEW_REQUESTCODE = 50000;

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
    private AVChatUI avChatUI; // 音视频总管理器
    private AVChatData avChatData; // config for connect video server
    private int state; // calltype 音频或视频
    private String receiverId; // 对方的account

    // state
    private boolean isUserFinish = false;
    private boolean mIsInComingCall = false;// is incoming call or outgoing call
    private boolean isCallEstablished = false; // 电话是否接通
    private static boolean needFinish = true; // 若来电或去电未接通时，点击home。另外一方挂断通话。从最近任务列表恢复，则finish
    private boolean hasOnPause = false; // 是否暂停音视频

    // notification
    private AVChatNotification notifier;

    private Button movetoback;

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    public static void launch(Activity context, String account, int callType, int source) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, AVChatUIVIew2.class);
        intent.putExtra(KEY_ACCOUNT, account);
        intent.putExtra(KEY_IN_CALLING, false);
        intent.putExtra(KEY_CALL_TYPE, callType);
        intent.putExtra(KEY_SOURCE, source);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivityForResult(intent,PREVIEW_REQUESTCODE);
    }

    public static void start(ILayout layout , String sessionId , int callType , int source) {
        needFinish = false;
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ACCOUNT,sessionId);
        bundle.putInt(KEY_CALL_TYPE,callType);
        bundle.putInt(KEY_SOURCE, source);
        bundle.putBoolean(KEY_IN_CALLING, false);
        layout.startIView(new AVChatUIVIew2(),new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    /**
     * incoming call
     *
     * @param context
     */
    public static void launch(Context context, AVChatData config, int source) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, AVChatUIVIew2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_CALL_CONFIG, config);
        intent.putExtra(KEY_IN_CALLING, true);
        intent.putExtra(KEY_SOURCE, source);
        context.startActivity(intent);
    }

    public static void launch(Context activity) {
        L.d(TAG,"launch 返回视频聊天");
        needFinish = false;
        Intent intent = new Intent(activity,AVChatUIVIew2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
    }


    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);

        if (needFinish || !checkSource(param.mBundle)) {
            finishIView();
            return;
        }

    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);



    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        avChatUI = new AVChatUI(mActivity, mRootView, this);
        if (!avChatUI.initiation()) {
            this.finishIView();
            return;
        }

        //启动悬浮窗service
        avChatUI.bindService();

//        avChatUI.setFloatActionListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AVChatUIVIew2.start(mParentILayout,receiverId,state,FROM_INTERNAL);
//            }
//        });

//        mIsInComingCall = bundle.getBoolean(KEY_IN_CALLING, false);

        mIsInComingCall = false;

        registerNetCallObserver(true);
        if (mIsInComingCall) {
            inComingCalling();
        } else {
            outgoingCalling();
        }

        notifier = new AVChatNotification(mActivity);
        notifier.init(receiverId != null ? receiverId : avChatData.getAccount());
        isCallEstablished = false;
        //放到所有UI的基类里面注册，所有的UI实现onKickOut接口
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, true);

        //创建广播
        InnerRecevier innerReceiver = new InnerRecevier();
        //动态注册广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //启动广播
//        registerReceiver(innerReceiver, intentFilter);

    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    //    @Override
//    protected void onCreateView() {
//        movetoback = (Button) findViewById(R.id.btn_move_to_back);
//        movetoback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                L.d("AVChatSurface","movetoback");
////                moveTaskToBack(false);
//                HnUIMainActivity.launcher(AVChatActivity.this,true);
//            }
//        });

//    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        // 预览框进入 传入intent
//
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
////        avChatUI.pauseVideo(); // 暂停视频聊天（用于在视频聊天过程中，APP退到后台时必须调用）
//        hasOnPause = true;
//    }


    @Override
    public void onViewHide() {
        super.onViewHide();
        hasOnPause = true;
        activeCallingNotifier();

    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        cancelCallingNotifier();

    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
    }



    class InnerRecevier extends BroadcastReceiver {

        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        L.d("onKeyDown :: Home键被监听" );
                        RBus.post(new AVChatFloatEvent(true));
//                        HnUIMainActivity.launch(AVChatUIVIew2.this);
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {

                    }
                }
            }
        }
    }

//    // 拦截返回键 home键
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        T_.show("onKeyDown :: " + keyCode);
//        L.d("onKeyDown :: " + keyCode);
//        if (keyCode == KeyEvent.KEYCODE_BACK ) {
//            RBus.post(new AVChatFloatEvent(true));
//            HnUIMainActivity.launch(this);
//            return true;//return true;拦截事件传递,从而屏蔽back键。
//        }
//        if (KeyEvent.KEYCODE_HOME == keyCode) {
//            RBus.post(new AVChatFloatEvent(true));
//            return true;//同理
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PREVIEW_REQUESTCODE) {
//
//        }
//    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_message_avchat_layout);
    }


    /**
     * 判断来电还是去电
     *
     * @return
     */
    private boolean checkSource(Bundle bundle) {
//        Bundle bundle = param.mBundle;
        switch (bundle.getInt(KEY_SOURCE, FROM_UNKNOWN)) {
            case FROM_BROADCASTRECEIVER: // incoming call
                parseIncomingIntent(bundle);
                return true;
            case FROM_INTERNAL: // outgoing call
                parseOutgoingIntent(bundle);
                if (state == AVChatType.VIDEO.getValue() || state == AVChatType.AUDIO.getValue()) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * 来电参数解析
     */
    private void parseIncomingIntent(Bundle bundle) {
        avChatData = (AVChatData) bundle.getSerializable(KEY_CALL_CONFIG);
        state = avChatData.getChatType().getValue();
    }

    /**
     * 去电参数解析
     */
    private void parseOutgoingIntent(Bundle bundle) {
        receiverId = bundle.getString(KEY_ACCOUNT);
        state = bundle.getInt(KEY_CALL_TYPE, -1);
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
                avChatUI.closeSessions(AVChatExitCode.PEER_BUSY);
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_REJECT) {
                avChatUI.closeSessions(AVChatExitCode.REJECT);
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_AGREE) {
                avChatUI.isCallEstablish.set(true);
                avChatUI.canSwitchCamera = true;
            }
        }
    };

    Observer<Long> timeoutObserver = new Observer<Long>() {
        @Override
        public void onEvent(Long chatId) {
            AVChatData info = avChatUI.getAvChatData();
            if (info != null && info.getChatId() == chatId) {
                avChatUI.closeSessions(AVChatExitCode.PEER_NO_RESPONSE);

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

            avChatUI.closeSessions(AVChatExitCode.PEER_BUSY);
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

            avChatUI.closeSessions(AVChatExitCode.HANGUP);
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
            }
            avChatUI.closeSessions(-1);
        }
    };


    /**
     * 接听
     */
    private void inComingCalling() {
        avChatUI.inComingCalling(avChatData);
    }

    /**
     * 拨打
     */
    private void outgoingCalling() {
//        if (!NetworkUtil.isNetAvailable(AVChatUIVIew2.this)) { // 网络不可用
////            Toast.makeText(this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
//            finishIView();
//            return;
//        }
        avChatUI.outGoingCalling(receiverId, AVChatType.typeOfValue(state));
    }

    /**
     * *************************** AVChatListener *********************************
     */
    @Override
    public void uiExit() {
        finishIView();
    }

    @Override
    public void showIView() {
        T_.show("showIView");
        startIView(new P2PChatUIView());
    }

    @Override
    public void showPermissionCheckDialog() {

    }


    /****************************** 连接建立处理 ********************/

    /**
     * 处理连接服务器的返回值
     *
     * @param auth_result
     */
    protected void handleWithConnectServerResult(int auth_result) {
        L.i(TAG, "result code->" + auth_result);
        if (auth_result == 200) {
            Log.d(TAG, "onConnectServer success");
        } else if (auth_result == 101) { // 连接超时
            avChatUI.closeSessions(AVChatExitCode.PEER_NO_RESPONSE);
        } else if (auth_result == 401) { // 验证失败
            avChatUI.closeSessions(AVChatExitCode.CONFIG_ERROR);
        } else if (auth_result == 417) { // 无效的channelId
            avChatUI.closeSessions(AVChatExitCode.INVALIDE_CHANNELID);
        } else { // 连接服务器错误，直接退出
            avChatUI.closeSessions(AVChatExitCode.CONFIG_ERROR);
        }
    }

    /**************************** 处理音视频切换 *********************************/

    /**
     * 处理音视频切换请求
     *
     * @param notification
     */
    private void handleCallControl(AVChatControlEvent notification) {
        switch (notification.getControlCommand()) {
            case AVChatControlCommand.SWITCH_AUDIO_TO_VIDEO:
                avChatUI.incomingAudioToVideo();
                break;
            case AVChatControlCommand.SWITCH_AUDIO_TO_VIDEO_AGREE:
                onAudioToVideo();
                break;
            case AVChatControlCommand.SWITCH_AUDIO_TO_VIDEO_REJECT:
                avChatUI.onCallStateChange(CallStateEnum.AUDIO);
//                Toast.makeText(AVChatActivity.this, R.string.avchat_switch_video_reject, Toast.LENGTH_SHORT).show();
                break;
            case AVChatControlCommand.SWITCH_VIDEO_TO_AUDIO:
                onVideoToAudio();
                break;
            case AVChatControlCommand.NOTIFY_VIDEO_OFF:
                avChatUI.peerVideoOff();
                break;
            case AVChatControlCommand.NOTIFY_VIDEO_ON:
                avChatUI.peerVideoOn();
                break;
            default:
//                Toast.makeText(this, "对方发来指令值：" + notification.getControlCommand(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 音频切换为视频
     */
    private void onAudioToVideo() {
        avChatUI.onAudioToVideo();
        avChatUI.initAllSurfaceView(avChatUI.getVideoAccount());
    }

    /**
     * 视频切换为音频
     */
    private void onVideoToAudio() {
        avChatUI.onCallStateChange(CallStateEnum.AUDIO);
        avChatUI.onVideoToAudio();
    }

    /**
     * 通知栏
     */
    private void activeCallingNotifier() {
        if (notifier != null && !isUserFinish) {
            notifier.activeCallingNotification(true);
        }
    }

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

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        isUserFinish = true;
        L.d(TAG,"onDestroy");
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, false);
        AVChatProfile.getInstance().setAVChatting(false);
        registerNetCallObserver(false);
        cancelCallingNotifier();
        needFinish = true;

//        RBus.post(new AVChatFloatEvent(false));

        //销毁悬浮窗服务
        if (avChatUI != null) {
            avChatUI.destroy();
        }
    }

    /**
     * ************************ AVChatStateObserver ****************************
     */

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
        if (avChatUI != null) {
            avChatUI.resetRecordTip();
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
        if (avChatUI != null) {
            avChatUI.resetRecordTip();
        }
    }

    @Override
    public void onLowStorageSpaceWarning(long availableSize) {
        if (avChatUI != null) {
            avChatUI.showRecordWarning();
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

    @Override
    public void onLeaveChannel() {

    }

    @Override
    public void onUserJoined(String account) {
        Log.d(TAG, "onUserJoin -> " + account);
        avChatUI.setVideoAccount(account);
        avChatUI.initLargeSurfaceView(avChatUI.getVideoAccount());
    }

    @Override
    public void onUserLeave(String account, int event) {
        Log.d(TAG, "onUserLeave -> " + account);
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
        Log.d(TAG, "onCallEstablished");
        if (avChatUI.getTimeBase() == 0)
            avChatUI.setTimeBase(SystemClock.elapsedRealtime());

        if (state == AVChatType.AUDIO.getValue()) {
            avChatUI.onCallStateChange(CallStateEnum.AUDIO);
        } else {
            avChatUI.initSmallSurfaceView();
            avChatUI.onCallStateChange(CallStateEnum.VIDEO);
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
        return true;
    }

    @Override
    public boolean onAudioFrameFilter(AVChatAudioFrame frame) {
        return true;
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

    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code.wontAutoLogin()) {
                AVChatSoundPlayer.instance().stop();
                finishIView();
            }
        }
    };

}

