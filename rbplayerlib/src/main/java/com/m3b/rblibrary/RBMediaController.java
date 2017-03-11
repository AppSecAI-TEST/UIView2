package com.m3b.rblibrary;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.m3b.rblibrary.media.IRenderView;
import com.m3b.rblibrary.media.IjkVideoView;
import com.m3b.rblibrary.utils.NetUtils;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * Author m3b
 * 视频播放控制类
 */
public class RBMediaController extends RelativeLayout {

    public static final String SCALETYPE_FITPARENT = "fitParent";

    public static final String SCALETYPE_FILLPARENT = "fillParent";

    public static final String SCALETYPE_WRAPCONTENT = "wrapContent";

    public static final String SCALETYPE_FITXY = "fitXY";

    public static final String SCALETYPE_16_9 = "16:9";

    public static final String SCALETYPE_4_3 = "4:3";
    private static final int MESSAGE_SHOW_PROGRESS = 1;
    private static final int MESSAGE_FADE_OUT = 2;
    private static final int MESSAGE_SEEK_NEW_POSITION = 3;
    private static final int MESSAGE_HIDE_CENTER_BOX = 4;
    private static final int MESSAGE_RESTART_PLAY = 5;
    private Activity activity;
    private Context context;
    private View contentView;
    private IjkVideoView videoView;
    private SeekBar seekBar;
    private AudioManager audioManager;
    private boolean playerSupport;
    private String url;
    private Query $;
    private int STATUS_ERROR = -1;
    private int STATUS_IDLE = 0;
    private int STATUS_LOADING = 1;
    private int STATUS_PLAYING = 2;
    private int STATUS_PAUSE = 3;
    private int STATUS_COMPLETED = 4;
    private long pauseTime;
    private int status = STATUS_IDLE;
    private boolean isLive = false;// 是否为直播
    private boolean isShowCenterControl = true;// 是否显示中心控制器
    private boolean isHideControl = false;//是否隐藏视频控制栏
    private boolean isShowTopControl = false;//是否显示头部显示栏，true：竖屏也显示 false：竖屏不显示，横屏显示
    private boolean isSupportGesture = false;//是否至此手势操作，false ：小屏幕的时候不支持，全屏的支持；true : 小屏幕还是全屏都支持
    private boolean isPrepare = false;// 是否已经初始化播放
    private boolean isNetListener = true;// 是否添加网络监听 (默认是监听)

    private int defaultTimeout = 3000;
    private int screenWidthPixels;

    private int initWidth = 0;
    private int initHeight = 0;
    private ImageView mPreviewImageView;


    /***************************************
     * 对外调用的方法
     ********************/
    private boolean isShowing;
    private boolean portrait;
    private long newPosition = -1;
    private long defaultRetryTime = 5000;
    private OnErrorListener onErrorListener;
    private Runnable oncomplete = new Runnable() {
        @Override
        public void run() {
        }
    };
    private OnInfoListener onInfoListener;
    private OnPreparedListener onPreparedListener;
    private int currentPosition;
    private long duration;
    private boolean instantSeeking;
    private boolean isDragging;
    @SuppressWarnings("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_FADE_OUT:
                    hide(false);
                    break;
                case MESSAGE_HIDE_CENTER_BOX:
                    $.id(R.id.app_video_fastForward_box).gone();
                    break;
                case MESSAGE_SEEK_NEW_POSITION:
                    if (!isLive && newPosition >= 0) {
                        videoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                case MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging && isShowing) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                        updatePausePlay();
                    }
                    break;
                case MESSAGE_RESTART_PLAY:
                    play(url);
                    break;
            }
        }
    };
    /**
     * 相应点击事件
     */
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.app_video_play) {
                doPauseResume();
                show(defaultTimeout);
            } else if (v.getId() == R.id.view_jky_player_center_play) {
                // videoView.seekTo(0);
                // videoView.start();
                doPauseResume();
                show(defaultTimeout);
            } else if (v.getId() == R.id.app_video_finish) {
                if (!portrait) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    activity.finish();
                }
            } else if (v.getId() == R.id.view_jky_player_tv_continue) {
                isNetListener = false;// 取消网络的监听
                $.id(R.id.view_jky_player_tip_control).gone();
                play(url, currentPosition);
            }
        }
    };
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (!fromUser)
                return;
            $.id(R.id.view_jky_player_tip_control).gone();
            int newPosition = (int) ((duration * progress * 1.0) / 1000);
            Log.d("+++++", "newPosition: " + newPosition);
            String time = generateTime(newPosition);
            if (instantSeeking) {
                videoView.seekTo(newPosition);
            }
            $.id(R.id.app_video_currentTime).text(time);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isDragging = true;
            show(3600000);
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            if (instantSeeking) {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!instantSeeking) {
                videoView
                        .seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
            }
            show(defaultTimeout);
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            isDragging = false;
            handler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
        }
    };

    /**********************************************************/

    public RBMediaController(Context context) {
        this(context, null);
    }

    public RBMediaController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RBMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        activity = (Activity) this.context;
        //初始化view和其他相关的
        initView();
    }

    /**
     * 是否显示中心控制器
     *
     * @param isShow true ： 显示 false ： 不显示
     */
    public RBMediaController showCenterControl(boolean isShow) {
        this.isShowCenterControl = isShow;
        return this;
    }

    public RBMediaController setShowTopControl(boolean isShowTopControl) {
        this.isShowTopControl = isShowTopControl;
        return this;
    }

    /**
     * 点击的时候是否显示控制栏
     *
     * @param isHideControl
     * @return
     */
    public RBMediaController setHideControl(boolean isHideControl) {
        this.isHideControl = isHideControl;
        return this;
    }

    /**
     * 设置播放视频是否有网络变化的监听
     *
     * @param isNetListener true ： 监听 false ： 不监听
     * @return
     */
    public RBMediaController setNetChangeListener(boolean isNetListener) {
        this.isNetListener = isNetListener;
        return this;
    }

    /**
     * 设置了竖屏的时候播放器的宽高
     *
     * @param width  0：默认是屏幕的宽度
     * @param height 0：默认是宽度的16:9
     * @return
     */
    public RBMediaController setPlayerWH(int width, int height) {
        this.initWidth = width;
        this.initHeight = height;
        return this;
    }

    /**
     * 获取到当前播放的状态
     *
     * @return
     */
    public int getVideoStatus() {
        return videoView.getCurrentState();
    }

    /**
     * 获得某个控件
     *
     * @param ViewId
     * @return
     */
    public View getView(int ViewId) {
        return activity.findViewById(ViewId);
    }

    public RBMediaController setTitle(CharSequence title) {
        $.id(R.id.app_video_title).text(title);
        return this;
    }

    private void doPauseResume() {
        if (status == STATUS_COMPLETED) {
            if (isShowCenterControl) {
                $.id(R.id.view_jky_player_center_control).visible();
            }
            videoView.seekTo(0);
            videoView.start();
        } else if (videoView.isPlaying()) {
            statusChange(STATUS_PAUSE);
            videoView.pause();
        } else {
            videoView.start();
        }
        updatePausePlay();
    }

    /**
     * 更新暂停状态的控件显示
     */
    private void updatePausePlay() {
        $.id(R.id.view_jky_player_center_control).visibility(
                isShowCenterControl ? View.VISIBLE : View.GONE);
        if (videoView.isPlaying()) {
            $.id(R.id.app_video_play).image(R.drawable.ic_pause);
            $.id(R.id.view_jky_player_center_play).image(
                    R.drawable.ic_center_pause);
        } else {
            $.id(R.id.app_video_play).image(R.drawable.ic_play);
            $.id(R.id.view_jky_player_center_play).image(
                    R.drawable.ic_center_play);
        }
    }

    /**
     * @param timeout
     */
    private void show(int timeout) {
        if (isHideControl) {
            showBottomControl(false);
            showCenterControl(false);
            showTopControl(false);
            return;
        }

        if (!isShowing && isPrepare) {
            if (!isShowTopControl && portrait) {
                showTopControl(false);
            } else {
                showTopControl(true);
            }
            if (isShowCenterControl) {
                $.id(R.id.view_jky_player_center_control).visible();
            }
            showBottomControl(true);
            isShowing = true;
        }
        updatePausePlay();
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        handler.removeMessages(MESSAGE_FADE_OUT);
        if (timeout != 0) {
            handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_FADE_OUT),
                    timeout);
        }
    }

    /**
     * 隐藏显示底部控制栏
     *
     * @param show true ： 显示 false ： 隐藏
     */
    private void showBottomControl(boolean show) {
        $.id(R.id.app_video_bottom_box).visibility(
                show ? View.VISIBLE : View.GONE);
        if (isLive) {
            $.id(R.id.app_video_play).gone();
            $.id(R.id.app_video_currentTime).gone();
            $.id(R.id.app_video_endTime).gone();
            $.id(R.id.app_video_seekBar).gone();
        }
    }

    /**
     * 隐藏和显示头部的一些控件
     *
     * @param show
     */
    private void showTopControl(boolean show) {
        $.id(R.id.app_video_top_box)
                .visibility(show ? View.VISIBLE : View.GONE);
        if (isLive) {

        }
    }

    /**
     * 返回预览图的控件
     */
    public ImageView getPreviewImageView() {
        return mPreviewImageView;
    }

    /**
     * 初始化视图
     */
    public void initView() {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            playerSupport = true;
        } catch (Throwable e) {
            Log.e("RBPlayer", "loadLibraries error", e);
        }
        screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;
        $ = new Query(activity);
        contentView = View.inflate(context, R.layout.view_player, this);
        mPreviewImageView = (ImageView) contentView.findViewById(R.id.preview_image_view);
        videoView = (IjkVideoView) contentView.findViewById(R.id.video_view);
        videoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                statusChange(STATUS_COMPLETED);
                oncomplete.run();
            }
        });
        videoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                Log.e("******", "Error happened, what = " + what + ", extra = " + extra);
                statusChange(STATUS_ERROR);
                if (onErrorListener != null) {
                    onErrorListener.onError(what, extra);
                }
                return true;
            }
        });
        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {

                Log.d("++++", " " + what);
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //表示缓冲开始 1,
                        statusChange(STATUS_LOADING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //2,
                        statusChange(STATUS_PLAYING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        // 显示 下载速度
                        //Toast.makeText(activity,"download rate:" + extra,Toast.LENGTH_SHORT).show();
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        //表示视频开始渲染 3,
                        statusChange(STATUS_PLAYING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        //表示音频开始渲染
                        statusChange(STATUS_PLAYING);
                        break;
                }
                if (onInfoListener != null) {
                    onInfoListener.onInfo(what, extra);
                }
                return false;
            }
        });
        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(IMediaPlayer mp) {

                isPrepare = true;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        hide(false);
                        //show(defaultTimeout);//TODO center hide
                    }
                }, 500);
                if (onPreparedListener != null) {
                    onPreparedListener.onPrepared();
                }
            }

        });

        seekBar = (SeekBar) contentView.findViewById(R.id.app_video_seekBar);
        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
        $.id(R.id.app_video_play).clicked(onClickListener);
        $.id(R.id.app_video_finish).clicked(onClickListener);
        $.id(R.id.view_jky_player_center_play).clicked(onClickListener);
        $.id(R.id.view_jky_player_tv_continue).clicked(onClickListener);

        audioManager = (AudioManager) activity
                .getSystemService(Context.AUDIO_SERVICE);
        final GestureDetector gestureDetector = new GestureDetector(activity,
                new PlayerGestureListener());

        View liveBox = contentView.findViewById(R.id.app_video_box);
        liveBox.setClickable(true);
        liveBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent))
                    return true;

                // 处理手势结束
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }

                return false;
            }
        });
        hideAll();
        if (!playerSupport) {
            showStatus(activity.getResources().getString(R.string.not_support),
                    "重试");
        }
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        if (newPosition >= 0) {
            handler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
            handler.sendEmptyMessage(MESSAGE_SEEK_NEW_POSITION);
        }
        handler.removeMessages(MESSAGE_HIDE_CENTER_BOX);
        handler.sendEmptyMessageDelayed(MESSAGE_HIDE_CENTER_BOX, 500);

    }

    /**
     * 视频播放状态的改变
     *
     * @param newStatus
     */
    private void statusChange(int newStatus) {
        status = newStatus;
        if (!isLive && newStatus == STATUS_COMPLETED) {// 当视频播放完成的时候
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            hideAll();
            if (isShowCenterControl) {
                updatePausePlay();// FIX completed pause button
                $.id(R.id.view_jky_player_center_control).visible();
            }
        } else if (newStatus == STATUS_ERROR) {
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            hideAll();
            if (isLive) {
                showStatus(
                        activity.getResources().getString(
                                R.string.small_problem), "重试");
                if (defaultRetryTime > 0) {
                    handler.sendEmptyMessageDelayed(MESSAGE_RESTART_PLAY,
                            defaultRetryTime);
                }
            } else {
                showStatus(
                        activity.getResources().getString(
                                R.string.small_problem), "重试");
            }
        } else if (newStatus == STATUS_LOADING) {
            hideAll();
            $.id(R.id.app_video_loading).visible();
        } else if (newStatus == STATUS_PLAYING) {
            hideAll();
        } else {
            hideAll();
        }

    }

    /**
     * 隐藏全部的控件
     */
    private void hideAll() {
        $.id(R.id.view_jky_player_center_control).gone();
        $.id(R.id.app_video_loading).gone();
        $.id(R.id.view_jky_player_tip_control).gone();
        showBottomControl(false);
        showTopControl(false);
    }


    /**
     * 暂停
     */
    public void onPause() {
        pauseTime = System.currentTimeMillis();
        show(0);// 把系统状态栏显示出来
        if (status == STATUS_PLAYING) {
            videoView.pause();
            if (!isLive) {
                currentPosition = videoView.getCurrentPosition();
            }
        }
    }

    public void onResume() {
        pauseTime = 0;
        if (status == STATUS_PLAYING) {
            if (isLive) {
                videoView.seekTo(0);
            } else {
                if (currentPosition > 0) {
                    videoView.seekTo(currentPosition);
                }
            }
            videoView.start();
        }
    }


    /**
     * 在activity中的onDestroy中需要回调
     */
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        videoView.stopPlayback();
    }

    /**
     * 显示错误信息
     *
     * @param statusText 错误提示
     * @param btnText    错误按钮提示
     */
    private void showStatus(String statusText, String btnText) {
        $.id(R.id.view_jky_player_tip_control).visible();
        $.id(R.id.view_jky_player_tip_text).text(statusText);
        $.id(R.id.view_jky_player_tv_continue).text(btnText);
        isPrepare = false;// 设置点击不能出现控制栏
    }

    /**
     * 开始播放
     *
     * @param url 播放视频的地址
     */
    public void play(String url) {
        this.url = url;
        play(url, 0);
    }

    /**
     * @param url             开始播放(可播放指定位置)
     * @param currentPosition 指定位置的大小(0-1000)
     */
    public void play(String url, int currentPosition) {
        this.url = url;
        if (videoView != null) {
            release();
        }
        if (isNetListener
                && (NetUtils.getNetworkType(activity) == 2 || NetUtils
                .getNetworkType(activity) == 4)) {// 手机网络的情况下
            $.id(R.id.view_jky_player_tip_control).visible();
        } else {
            if (playerSupport) {
                $.id(R.id.app_video_loading).visible();
                videoView.setVideoPath(url);
                if (isLive) {
                    videoView.seekTo(0);
                } else {
                    seekTo(currentPosition, true);
                }
                videoView.start();
            }
        }
    }

    /**
     * 格式化显示的时间
     *
     * @param time
     * @return
     */
    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes,
                seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    private int getScreenOrientation() {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                && height > width
                || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
                && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    private void onProgressSlide(float percent) {
        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);

        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / 1000;
        if (showDelta != 0) {
            $.id(R.id.app_video_fastForward_box).visible();
            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
            $.id(R.id.app_video_fastForward).text(text + "s");
            $.id(R.id.app_video_fastForward_target).text(
                    generateTime(newPosition) + "/");
            $.id(R.id.app_video_fastForward_all).text(generateTime(duration));
        }
    }

    private long setProgress() {
        if (isDragging) {
            return 0;
        }

        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = videoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
        }

        this.duration = duration;
        $.id(R.id.app_video_currentTime).text(generateTime(position));
        $.id(R.id.app_video_endTime).text(generateTime(this.duration));
        return position;
    }

    public void hide(boolean force) {
        if (force || isShowing) {
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            showBottomControl(false);
            $.id(R.id.view_jky_player_center_control).gone();
            showTopControl(false);
            isShowing = false;
        }
    }


    public void setScaleType(String scaleType) {
        if (SCALETYPE_FITPARENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        } else if (SCALETYPE_FILLPARENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
        } else if (SCALETYPE_WRAPCONTENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_WRAP_CONTENT);
        } else if (SCALETYPE_FITXY.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_MATCH_PARENT);
        } else if (SCALETYPE_16_9.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
        } else if (SCALETYPE_4_3.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_4_3_FIT_PARENT);
        }
    }

    public void start() {
        videoView.start();
    }

    public void pause() {
        videoView.pause();
    }

    public boolean onBackPressed() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }

    /**
     * is player support this device
     *
     * @return
     */
    public boolean SurfaceView() {
        return playerSupport;
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return videoView != null ? videoView.isPlaying() : false;
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        videoView.release(true);
        videoView.seekTo(0);
    }

    /**
     * seekTo position
     *
     * @param msec millisecond
     */
    public RBMediaController seekTo(int msec, boolean showControlPanle) {
        videoView.seekTo(msec);
        if (showControlPanle) {
            //show(defaultTimeout);
        }
        return this;
    }

    /**
     * 获取当前播放的currentPosition
     *
     * @return
     */
    public int getCurrentPosition() {
        if (!isLive) {
            currentPosition = videoView.getCurrentPosition();
        } else {// 直播
            currentPosition = -1;
        }
        return currentPosition;
    }

    /**
     * 获取视频的总长度
     *
     * @return
     */
    public int getDuration() {
        return videoView.getDuration();
    }

    public RBMediaController onError(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
        return this;
    }

    public RBMediaController onComplete(Runnable complete) {
        this.oncomplete = complete;
        return this;
    }

    public RBMediaController onInfo(OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
        return this;
    }

    public RBMediaController onPrepared(OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
        return this;
    }

    /**
     * set is live (can't seek forward)
     *
     * @param isLive
     * @return
     */
    public RBMediaController setLive(boolean isLive) {
        this.isLive = isLive;
        return this;
    }

    public interface OnErrorListener {
        void onError(int what, int extra);
    }

    public interface OnInfoListener {
        void onInfo(int what, int extra);
    }

    public interface OnPreparedListener {
        void onPrepared();
    }

    class Query {
        private final Activity activity;
        private View view;

        public Query(Activity activity) {
            this.activity = activity;
        }

        public Query id(int id) {
            view = contentView.findViewById(id);
            return this;
        }

        public Query image(int resId) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(resId);
            }
            return this;
        }

        public Query visible() {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            return this;
        }

        public Query gone() {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            return this;
        }

        public Query invisible() {
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
            return this;
        }

        public Query clicked(View.OnClickListener handler) {
            if (view != null) {
                view.setOnClickListener(handler);
            }
            return this;
        }

        public Query text(CharSequence text) {
            if (view != null && view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        public Query visibility(int visible) {
            if (view != null) {
                view.setVisibility(visible);
            }
            return this;
        }
    }

    public class PlayerGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        private boolean firstTouch;
        private boolean toSeek;


        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (!isSupportGesture && portrait) {
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                firstTouch = false;
            }

            if (toSeek) {
                if (!isLive) {
                    onProgressSlide(-deltaX / videoView.getWidth());
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!isPrepare) {// 视频没有初始化点击屏幕不起作用
                return false;
            }
            if (isShowing) {
                hide(false);
            } else {
                show(defaultTimeout);
            }
            return true;
        }
    }

}
