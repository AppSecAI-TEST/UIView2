package com.hn.d.valley.main.avchat;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.activity.HnUIMainActivity;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.main.avchat.constant.CallStateEnum;
import com.hn.d.valley.utils.RBus;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

/**
 * 视频管理器， 视频界面初始化和相关管理
 * Created by hzxuwen on 2015/5/5.
 */
public class AVChatVideo implements View.OnClickListener {

    // data
    private Activity mActivity;
    private View root;
    private AVChatUI manager;
    private Chronometer time;
    //中间控制按钮
    private View middleRoot;
    private HnGlideImageView headImg;
    private TextView nickNameTV;
    private TextView notifyTV;
    private View refuse_receive;
    private TextView refuseTV;
    private TextView receiveTV;
    //底部控制按钮
    private View bottomRoot;
    private LinearLayout ll_receive;
    private LinearLayout ll_cancelchat;
    private LinearLayout ll_switchCamera;
    private LinearLayout ll_switchAudio;

    //摄像头权限提示显示
    private View permissionRoot;

    //顶部最小化
    private View iv_scale;


    private int topRootHeight = 0;
    private int bottomRootHeight = 0;

    private AVChatUIListener listener;

    // state
    private boolean init = false;
    private boolean shouldEnableToggle = false;
    private boolean isReceived = false;
    private boolean isInSwitch = false;

    public AVChatVideo(Activity context, View root, AVChatUIListener listener, AVChatUI manager) {
        this.mActivity = context;
        this.root = root;
        this.listener = listener;
        this.manager = manager;
    }

    private void findViews() {
        if (init || root == null)
            return;

        middleRoot = root.findViewById(R.id.avchat_video_middle_control);
        headImg = (HnGlideImageView) middleRoot.findViewById(R.id.avchat_video_head);
        nickNameTV = (TextView) middleRoot.findViewById(R.id.avchat_video_nickname);
        notifyTV = (TextView) middleRoot.findViewById(R.id.avchat_video_notify);
        iv_scale = root.findViewById(R.id.iv_avaudio_scale);
        iv_scale.setOnClickListener(this);

        refuse_receive = middleRoot.findViewById(R.id.avchat_video_refuse_receive);
        refuseTV = (TextView) refuse_receive.findViewById(R.id.refuse);
        receiveTV = (TextView) refuse_receive.findViewById(R.id.receive);
        refuseTV.setOnClickListener(this);
        receiveTV.setOnClickListener(this);

        bottomRoot = root.findViewById(R.id.avchat_video_bottom_control);
        ll_switchCamera = (LinearLayout) bottomRoot.findViewById(R.id.avchat_switch_camera);
        ll_receive = (LinearLayout) bottomRoot.findViewById(R.id.avchat_video_receive);
        ll_cancelchat = (LinearLayout) bottomRoot.findViewById(R.id.avchat_video_logout);
        ll_switchAudio = (LinearLayout) bottomRoot.findViewById(R.id.avchat_switch_mode);

        ll_switchCamera.setOnClickListener(this);
        ll_receive.setOnClickListener(this);
        ll_cancelchat.setOnClickListener(this);
        ll_switchAudio.setOnClickListener(this);

        permissionRoot = root.findViewById(R.id.avchat_video_permission_control);
        init = true;
    }

    /**
     * 音视频状态变化及界面刷新
     *
     * @param state
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
                isReceived = true;
//                enableCameraToggle(false);   //使用音视频预览时这里可以开启切换摄像头按钮
//                enableReceiveToggle(false);
                setMiddleRoot(true);
                setBottomRoot(true);
                callingVideoUI();
                break;
            case INCOMING_VIDEO_CALLING:
                showProfile();//对方的详细信息
                showNotify(R.string.avchat_video_call_request);
                setRefuseReceive(false);
                isReceived = false;
//                receiveTV.setText(R.string.text_pickup);
                setMiddleRoot(true);
                setBottomRoot(true);
                waigingVideoUI();
                break;
            case VIDEO:
                isInSwitch = false;
                isReceived = true;
                enableToggle();
                setTime(true);
                setMiddleRoot(false);
                setBottomRoot(true);
//                enableCameraToggle(true);
//                enableReceiveToggle(true);
                receingVideoUI();
                showNoneCameraPermissionView(false);
                break;
            case VIDEO_CONNECTING:
                showNotify(R.string.text_connecting);
                shouldEnableToggle = true;
                break;
            case OUTGOING_AUDIO_TO_VIDEO:
                isInSwitch = true;
                setTime(true);
                setMiddleRoot(false);
                setBottomRoot(true);
                break;
            default:
                break;
        }
        setRoot(CallStateEnum.isVideoMode(state));
    }

    /********************** 界面显示 **********************************/

    /**
     * 显示个人信息
     */
    private void showProfile() {
        String account = manager.getAccount();
        UserInfoProvider.UserInfo userInfo = NimUserInfoCache.getInstance().getUserInfo(account);
        headImg.setImageThumbUrl(userInfo.getAvatar());
        nickNameTV.setText(NimUserInfoCache.getInstance().getUserDisplayName(account));
    }

    /**
     * 显示通知
     *
     * @param resId
     */
    private void showNotify(int resId) {
        notifyTV.setText(resId);
        notifyTV.setVisibility(View.VISIBLE);
    }

    /************************ 布局显隐设置 ****************************/

    private void setRoot(boolean visible) {
        root.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setRefuseReceive(boolean visible) {
        refuse_receive.setVisibility(visible ? View.VISIBLE : View.GONE);
    }


    private void setMiddleRoot(boolean visible) {
        middleRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setBottomRoot(boolean visible) {
        bottomRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (bottomRootHeight == 0) {
            bottomRootHeight = bottomRoot.getHeight();
        }
    }

    private void setTime(boolean visible) {
//        time.setVisibility(visible ? View.VISIBLE : View.GONE);
//        if(visible){
//            time.setBase(manager.getTimeBase());
//            time.start();
//        }
    }

    /**
     * 底部控制开关可用
     */
    private void enableToggle() {
        if (shouldEnableToggle) {
            if (manager.canSwitchCamera() && AVChatManager.getInstance().hasMultipleCameras())
//                switchCameraToggle.enable();
//            closeCameraToggle.enable();
//            muteToggle.enable();
//            recordToggle.setEnabled(true);
                shouldEnableToggle = false;
        }
    }

    private void enableCameraToggle(boolean boo) {
        if (shouldEnableToggle) {
            if (manager.canSwitchCamera() && AVChatManager.getInstance().hasMultipleCameras()) {
//                switchCameraToggle.enable();
                ll_switchCamera.setVisibility(boo ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void enableReceiveToggle(boolean boo) {
        ll_receive.setVisibility(boo ? View.VISIBLE : View.GONE);
    }

    private void callingVideoUI() {
        ll_cancelchat.setVisibility(View.VISIBLE);
        ll_switchAudio.setVisibility(View.GONE);
        ll_switchCamera.setVisibility(View.GONE);
        ll_receive.setVisibility(View.GONE);
    }

    private void waigingVideoUI() {
        ll_cancelchat.setVisibility(View.VISIBLE);
        ll_switchAudio.setVisibility(View.GONE);
        ll_switchCamera.setVisibility(View.GONE);
        ll_receive.setVisibility(View.VISIBLE);
    }

    private void receingVideoUI() {
        ll_cancelchat.setVisibility(View.VISIBLE);
        ll_switchAudio.setVisibility(View.VISIBLE);
        ll_switchCamera.setVisibility(View.VISIBLE);
        ll_receive.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avchat_video_logout:
                if (isReceived) {
                    listener.onHangUp();
                } else {
                    listener.onRefuse();
                }
                break;
            case R.id.refuse:
                listener.onRefuse();
                break;
            case R.id.receive:
                listener.onReceive();
                break;
            case R.id.avchat_video_receive:
                listener.onReceive();
                break;
            case R.id.avchat_video_switch_audio:
                if (isInSwitch) {
//                    Toast.makeText(mActivity, R.string.avchat_in_switch, Toast.LENGTH_SHORT).show();
                } else {
                    listener.videoSwitchAudio();
                }
                break;
            case R.id.avchat_switch_camera:
                listener.switchCamera();
                break;
            case R.id.avchat_switch_mode:
                listener.videoSwitchAudio();
                break;
            case R.id.iv_avaudio_scale:
//                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//                lp.alpha = 0f;
//                mActivity.getWindow().setAttributes(lp);

                backToMainActivity();

                break;
//            case R.id.avchat_close_camera:
//                listener.closeCamera();
//                break;
//            case R.id.avchat_video_record:
//                listener.toggleRecord();
//                break;
            default:
                break;
        }

    }

    private void backToMainActivity() {
        HnUIMainActivity.launch(mActivity);

        manager.showFloatingView();
//        manager.showIView();

//        RBus.post(new AVChatFloatEvent(true));
//        AVChatDelegete.getInstance().showFloatingView();


    }


    public void showNoneCameraPermissionView(boolean show) {
        permissionRoot.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 音频切换为视频, 界面控件是否开启显示
     *
     * @param muteOn
     */
    public void onAudioToVideo(boolean muteOn, boolean recordOn, boolean recordWarning) {
//        muteToggle.toggle(muteOn ? ToggleState.ON : ToggleState.OFF);
//        closeCameraToggle.toggle(ToggleState.OFF);
        if (manager.canSwitchCamera()) {
//            switchCameraToggle.off(false);
        }
//        recordToggle.setEnabled(true);
//        recordToggle.setSelected(recordOn);
    }

    /******************************* toggle listener *************************/
//    @Override
//    public void toggleOn(View v) {
//        onClick(v);
//    }
//
//    @Override
//    public void toggleOff(View v) {
//        onClick(v);
//    }
//
//    @Override
//    public void toggleDisable(View v) {
//
//    }
    public void closeSession(int exitCode) {
        if (init) {
//            time.stop();
//            switchCameraToggle.disable(false);
//            muteToggle.disable(false);
//            recordToggle.setEnabled(false);
//            closeCameraToggle.disable(false);
            receiveTV.setEnabled(false);
            refuseTV.setEnabled(false);
//            hangUpImg.setEnabled(false);
        }
    }
}
