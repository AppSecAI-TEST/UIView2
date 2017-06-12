package com.hn.d.valley.main.avchat;

import android.content.Context;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.library.glide.GlideBlurTransformation;
import com.angcyo.uiview.utils.NetworkUtil;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hn.d.valley.R;
import com.hn.d.valley.activity.HnUIMainActivity;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.main.avchat.constant.CallStateEnum;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * 音频管理器， 音频界面初始化和管理
 * Created by hzxuwen on 2015/4/24.
 */
public class AVChatAudio implements View.OnClickListener{
    // constant
//    private static final int[] NETWORK_GRADE_DRAWABLE = new int[]{R.drawable.network_grade_0,R.drawable.network_grade_1,R.drawable.network_grade_2,R.drawable.network_grade_3};
//    private static final int[] NETWORK_GRADE_LABEL = new int[]{R.string.avchat_network_grade_0,R.string.avchat_network_grade_1,R.string.avchat_network_grade_2,R.string.avchat_network_grade_3};

    private Context context;
    // view
    private View rootView ;
    private View switchVideo;
    private HnGlideImageView headImg;
    private TextView nickNameTV;
    private Chronometer time;
//    private TextView wifiUnavailableNotifyTV;
    private TextView notifyTV;
    private ImageView ivPreImg;
//    private TextView netUnstableTV;

    private View iv_avaudio_scale;

    private View ll_avaudio_bottom;
//    private ToggleView muteToggle;
//    private ToggleView speakerToggle;
//    private View recordToggle;
//    private Button recordToggleButton;
//    private View hangup;

    // bottom control
    private View ll_avaudio_jinyin;
    private View ll_avaudio_logout;
    private View ll_avaudio_handsfree;
    private View ll_avaudio_receive;
    private View ll_avaudio_audiotovideo;


//    private View refuse_receive;
//    private TextView refuseTV;
//    private TextView receiveTV;

    //record
//    private View recordView;
//    private View recordTip;
//    private View recordWarning;

    // data
    private AVChatUI manager;
    private AVChatUIListener listener;

    // state
    private boolean init = false;

    // is in switch
    private boolean isInSwitch = false;

    //是否接听
    private boolean isReceived = false;

    public AVChatAudio(Context context, View root, AVChatUIListener listener, AVChatUI manager) {
        this.context = context;
        this.rootView = root;
        this.listener = listener;
        this.manager = manager;
    }

    /**
     * 音视频状态变化及界面刷新
     * @param state
     */
    public void onCallStateChange(CallStateEnum state){
        if(CallStateEnum.isAudioMode(state)) {
            findViews();
            genBlurBg();
        }
        switch (state){
            case OUTGOING_AUDIO_CALLING: //拨打出的免费通话
                setSwitchVideo(false);
                showProfile();//对方的详细信息
                showNotify(R.string.text_wait_receiving);
                setWifiUnavailableNotifyTV(true);
                setMuteSpeakerHangupControl(true);
                setRefuseReceive(false);
                isReceived = true;
                showCallingUI();
                break;
            case INCOMING_AUDIO_CALLING://免费通话请求
                setSwitchVideo(false);
                showProfile();//对方的详细信息
                showNotify(R.string.text_wait_receiving);
                setMuteSpeakerHangupControl(true);
                setRefuseReceive(true);
                isReceived = false;
                showWaitingUI();
//                receiveTV.setText(R.string.text_pickup);
                break;
            case AUDIO:
                isInSwitch = false;
                setWifiUnavailableNotifyTV(false);
                showNetworkCondition(1);
                showProfile();
                setSwitchVideo(true);
                setTime(true);
                hideNotify();
                setMuteSpeakerHangupControl(true);
                setRefuseReceive(false);
                enableToggle();
                showReceivedUI();
                isReceived = true;
                break;
            case AUDIO_CONNECTING:
                showNotify(R.string.text_connecting);
                break;
            case INCOMING_AUDIO_TO_VIDEO:
                isInSwitch = true;
                showNotify(R.string.text_invite_start_video_chat);
                setMuteSpeakerHangupControl(true);
                setRefuseReceive(true);
                // 直接开启视频聊天 不需要点击同意
//                showWaitingUI();
                if(listener != null) {
                    listener.onReceive();
                }
//                receiveTV.setText(R.string.text_av_receive);
                break;
            default:
                break;
        }
        setRoot(CallStateEnum.isAudioMode(state));
    }

    private boolean isEnabled = false;

    private void enableToggle() {
        if(!isEnabled) {
//            recordToggle.setEnabled(true);
        }
        isEnabled = true;
    }

    /**
     * 界面初始化
     */
    private void findViews() {
        if(init || rootView == null){
            return;
        }
        switchVideo = rootView.findViewById(R.id.ll_avaudio_audiotovideo);
        switchVideo.setOnClickListener(this);

        headImg = (HnGlideImageView) rootView.findViewById(R.id.iv_audio_head);
        nickNameTV = (TextView) rootView.findViewById(R.id.tv_avaudio_nickname);
        time = (Chronometer) rootView.findViewById(R.id.tv_avaudio_time);
        notifyTV = (TextView) rootView.findViewById(R.id.tv_avaudio_notify);
        iv_avaudio_scale = rootView.findViewById(R.id.iv_avaudio_scale);
        ivPreImg = (ImageView) rootView.findViewById(R.id.iv_preview_img);

        ll_avaudio_bottom = rootView.findViewById(R.id.audio_call_bottom_switch_layout);
        ll_avaudio_audiotovideo = ll_avaudio_bottom.findViewById(R.id.ll_avaudio_audiotovideo);
        ll_avaudio_jinyin = ll_avaudio_bottom.findViewById(R.id.ll_avaudio_jinyin);
        ll_avaudio_logout = ll_avaudio_bottom.findViewById(R.id.ll_avaudio_logout);
        ll_avaudio_receive = ll_avaudio_bottom.findViewById(R.id.ll_avaudio_receive);
        ll_avaudio_handsfree = ll_avaudio_bottom.findViewById(R.id.ll_avaudio_handsfree);

        ll_avaudio_handsfree.setOnClickListener(this);
        ll_avaudio_receive.setOnClickListener(this);
        ll_avaudio_jinyin.setOnClickListener(this);
        ll_avaudio_audiotovideo.setOnClickListener(this);
        ll_avaudio_logout.setOnClickListener(this);
        iv_avaudio_scale.setOnClickListener(this);

//        recordToggle = ll_avaudio_bottom.findViewById(R.id.avchat_audio_record);
//        recordToggleButton = (Button) ll_avaudio_bottom.findViewById(R.id.avchat_audio_record_button);

//        hangup = ll_avaudio_bottom.findViewById(R.id.avchat_audio_hangup);
//        hangup.setOnClickListener(this);
//        recordToggle.setOnClickListener(this);
//        recordToggle.setEnabled(false);

//        refuse_receive = rootView.findViewById(R.id.avchat_audio_refuse_receive);
//        refuseTV = (TextView) refuse_receive.findViewById(R.id.refuse);
//        receiveTV = (TextView) refuse_receive.findViewById(R.id.receive);
//        refuseTV.setOnClickListener(this);
//        receiveTV.setOnClickListener(this);

//        recordView = rootView.findViewById(R.id.avchat_record_layout);
//        recordTip = rootView.findViewById(R.id.avchat_record_tip);
//        recordWarning = rootView.findViewById(R.id.avchat_record_warning);

        init = true;
    }

    /**
     * ********************************* 界面设置 *************************************
     */

    private void genBlurBg() {
        // 显示预览图 头像高斯模糊
        UserInfoProvider.UserInfo userInfo = NimUserInfoCache.getInstance().getUserInfo(manager.getAccount());
        DrawableRequestBuilder<String> builder = Glide.with(context)
                .load(userInfo.getAvatar())
                .placeholder(R.drawable.avchat_call_bg)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        builder.bitmapTransform(new BlurTransformation(context))
                .into(ivPreImg);
    }


    private void showWaitingUI(){
        ll_avaudio_logout.setVisibility(View.VISIBLE);
        ll_avaudio_receive.setVisibility(View.VISIBLE);
        ll_avaudio_handsfree.setVisibility(View.GONE);
        ll_avaudio_audiotovideo.setVisibility(View.GONE);
        ll_avaudio_jinyin.setVisibility(View.GONE);
        time.setVisibility(View.GONE);

    }

    private void showReceivedUI(){
        ll_avaudio_logout.setVisibility(View.VISIBLE);
        ll_avaudio_receive.setVisibility(View.GONE);
        ll_avaudio_handsfree.setVisibility(View.VISIBLE);
        ll_avaudio_audiotovideo.setVisibility(View.VISIBLE);
        ll_avaudio_jinyin.setVisibility(View.VISIBLE);
//        iv_avaudio_scale.setVisibility(View.VISIBLE);
    }

    private void showCallingUI(){
        ll_avaudio_logout.setVisibility(View.VISIBLE);
        ll_avaudio_receive.setVisibility(View.GONE);
        ll_avaudio_handsfree.setVisibility(View.GONE);
        ll_avaudio_audiotovideo.setVisibility(View.GONE);
        ll_avaudio_jinyin.setVisibility(View.GONE);
        time.setVisibility(View.GONE);
    }


    /**
     * 个人信息设置
     */
    private void showProfile(){
        String account = manager.getAccount();
        UserInfoProvider.UserInfo userInfo = NimUserInfoCache.getInstance().getUserInfo(account);
        headImg.setImageThumbUrl(userInfo.getAvatar());
        nickNameTV.setText(NimUserInfoCache.getInstance().getUserDisplayName(account));
    }

    /**
     * 界面状态文案设置
     * @param resId 文案
     */
    private void showNotify(int resId) {
        notifyTV.setText(resId);
        notifyTV.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏界面文案
     */
    private void hideNotify(){
        notifyTV.setVisibility(View.GONE);
    }

    public void showRecordView(boolean show, boolean warning) {
        if(show) {
//            recordToggle.setSelected(true);
//            recordToggleButton.setText("结束");
//            recordView.setVisibility(View.VISIBLE);
//            recordTip.setVisibility(View.VISIBLE);
            if(warning) {
//                recordWarning.setVisibility(View.VISIBLE);
            } else {
//                recordWarning.setVisibility(View.GONE);
            }
        } else {
//            recordToggle.setSelected(false);
//            recordToggleButton.setText("录制");
//            recordView.setVisibility(View.INVISIBLE);
//            recordTip.setVisibility(View.INVISIBLE);
//            recordWarning.setVisibility(View.GONE);
        }
    }

    /**
     * 显示网络状态
     * @param grade
     */
    public void showNetworkCondition(int grade){
//        if(grade >= 0 && grade < NETWORK_GRADE_DRAWABLE.length){
//            netUnstableTV.setText(NETWORK_GRADE_LABEL[grade]);
//            Drawable drawable = DemoCache.getContext().getResources().getDrawable(NETWORK_GRADE_DRAWABLE[grade]);
//            if(drawable != null){
//                drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
//                netUnstableTV.setCompoundDrawables(null,null,drawable,null);
//            }
//            netUnstableTV.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * ***************************** 布局显隐设置 ***********************************
     */

    private void setRoot(boolean visible){
        rootView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示或隐藏音视频切换
     * @param visible
     */
    private void setSwitchVideo(boolean visible){
        switchVideo.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示或者隐藏是否为wifi的提示
     * @param show
     */
    private void setWifiUnavailableNotifyTV(boolean show){
        if(show && !NetworkUtil.isWifi(context)){
//            wifiUnavailableNotifyTV.setVisibility(View.VISIBLE);
        }else {
//            wifiUnavailableNotifyTV.setVisibility(View.GONE);
        }
    }

    /**
     * 显示或隐藏禁音，结束通话布局
     * @param visible
     */
    private void setMuteSpeakerHangupControl(boolean visible){
        ll_avaudio_bottom.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示或隐藏拒绝，开启布局
     * @param visible
     */
    private void setRefuseReceive(boolean visible){
//        refuse_receive.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置通话时间显示
     * @param visible
     */
    private void setTime(boolean visible){
        time.setVisibility(visible ? View.VISIBLE : View.GONE);
        if(visible){
            time.setBase(manager.getTimeBase());
            time.start();
        }
    }

    /**
     * 视频切换为音频时，禁音与扬声器按钮状态
     * @param muteOn
     * @param speakerOn
     */
    public void onVideoToAudio(boolean muteOn , boolean speakerOn, boolean recordOn, boolean recordWarning) {

//        muteToggle.toggle(muteOn ? ToggleState.ON : ToggleState.OFF);
//        speakerToggle.toggle(speakerOn ? ToggleState.ON : ToggleState.OFF);
//        recordToggle.setSelected(recordOn);
        showRecordView(recordOn, recordWarning);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_avaudio_logout:
                if (isReceived) {
                    listener.onHangUp();
                } else {
                    listener.onRefuse();
                }
                break;
            case R.id.refuse:
                listener.onRefuse();
                break;
            case R.id.ll_avaudio_receive:
                listener.onReceive();
                break;
            case R.id.ll_avaudio_handsfree:
                listener.toggleSpeaker();
                break;
            case R.id.ll_avaudio_jinyin:
                listener.toggleMute();
                break;
            case R.id.avchat_audio_speaker:
                listener.toggleSpeaker();
                break;
            case R.id.ll_avaudio_audiotovideo:
                listener.audioSwitchVideo();
                break;
            case R.id.avchat_audio_record:
                listener.toggleRecord();
                break;
            case R.id.iv_avaudio_scale:
                backToMain();

                break;
            default:
                break;
        }
    }

    private void backToMain() {
        HnUIMainActivity.launch(context.getApplicationContext());
        manager.showAudioModeUI();
    }

    public void closeSession(int exitCode){
        if(init){
            time.stop();
//            muteToggle.disable(false);
//            speakerToggle.disable(false);
//            recordToggle.setEnabled(false);
//            refuseTV.setEnabled(false);
//            receiveTV.setEnabled(false);
//            hangup.setEnabled(false);
        }
    }

//    /******************************* toggle listener *************************/
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
}
