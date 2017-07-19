package com.hn.d.valley.sub.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.angcyo.github.ripple.RippleBackground;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.RSeekBar;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.iview.VideoRecordUIView;
import com.hn.d.valley.bean.realm.MusicRealm;
import com.hn.d.valley.sub.user.sub.AddBgmUIView;
import com.hn.d.valley.widget.HnRecTextView;
import com.hn.d.valley.widget.HnRecordTimeView;
import com.m3b.rbaudiomixlibrary.Record;
import com.m3b.rbaudiomixlibrary.WaveCanvas;
import com.m3b.rbaudiomixlibrary.view.WaveSurfaceView;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：发布语音动态
 * 创建人员：Robi
 * 创建时间：2017/05/03 15:36
 * 修改人员：Robi
 * 修改时间：2017/05/03 15:36
 * 修改备注：
 * Version: 1.0.0
 */
public class PublishVoiceDynamicUIView extends BaseContentUIView {

    private static int progress = 50;
    Action0 mPublishAction;
    private RippleBackground mRippleBackground;
    private HnRecordTimeView mHnRecordTimeView;
    private HnRecTextView mHnRecTextView;
    private MusicRealm mMusicRealm;
    private RSeekBar mSeekBar;
    private WaveCanvas mwaveCanvas;
    private MusicIntentReceiver mMusicIntentReceiver;

    public PublishVoiceDynamicUIView() {
    }

    public PublishVoiceDynamicUIView(MusicRealm musicRealm) {
        mMusicRealm = musicRealm;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(mActivity, R.string.publish_voice)
                .setShowBackImageView(true);
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_publish_voice_dynamic);
    }

    @Override
    public int getDefaultBackgroundColor() {
        return getColor(R.color.base_chat_bg_color);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        mRippleBackground.stopRippleAnimation();
        mHnRecordTimeView.stopRecord();

        mActivity.unregisterReceiver(mMusicIntentReceiver);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        //添加BGM
        mViewHolder.v(R.id.add_bgm_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorMusic();
            }
        });

        mViewHolder.v(R.id.music_control_layout).setVisibility(View.GONE);
        mViewHolder.v(R.id.add_bgm_layout).setVisibility(View.VISIBLE);

        mRippleBackground = mViewHolder.v(R.id.ripple_layout);
        mHnRecordTimeView = mViewHolder.v(R.id.record_time_view);
        mHnRecTextView = mViewHolder.v(R.id.rec_view);
        mViewHolder.v(R.id.mic_view).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        recordStart();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        recordStop();
                        mViewHolder.v(R.id.mic_view).setEnabled(false);
                        break;
                }
                return true;
            }
        });

        mSeekBar = mViewHolder.v(R.id.seek_bar);

        if (mMusicRealm != null) {
            initMusicLayout(mMusicRealm);
        }
    }

    protected void recordStop() {
        mViewHolder.v(R.id.music_root_layout).setEnabled(true);
        mHnRecTextView.setRec(false);
        mViewHolder.tv(R.id.voice_tip_view).setText(R.string.record_voice_tip);
        mRippleBackground.stopRippleAnimation();
        mHnRecordTimeView.stopRecord();
        mViewHolder.v(R.id.surfaceView).postInvalidate();
        Record.instance().stopRecord();
    }

    protected void recordStart() {
        mViewHolder.v(R.id.music_root_layout).setEnabled(false);
        mHnRecTextView.setRec(true);
        mViewHolder.tv(R.id.voice_tip_view).setText(R.string.recording_voice_tip);
        mRippleBackground.startRippleAnimation();
        mHnRecordTimeView.startRecord(new HnRecordTimeView.OnMaxTimeListener() {
            @Override
            public void onMaxTime(long maxTime) {
                recordStop();
            }
        });
        String bgmPath = "";
        if (mMusicRealm != null) {
            bgmPath = mMusicRealm.getFilePath();
        }
        Record.instance()
                .setMusicVol(mSeekBar.getCurProgress() / 100f)
                .startRecord(bgmPath,
                        VideoRecordUIView.getOutputMediaFile(VideoRecordUIView.MEDIA_TYPE_VOICE).getAbsolutePath(),
                        new Record.RecordListener() {
                            @Override
                            public void onRecordStart(String bgmPath, String filePath) {

                            }

                            @Override
                            public void onRecordStop(String bgmPath, final String filePath, final long time) {
                                L.e("call: onRecordStop([bgmPath, filePath, time])-> " + filePath + " " + Math.floor(time / 1000f));
                                //Record.playFile(filePath);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Math.floor(time / 1000f) < 3) {
                                            T_.error(getString(R.string.record_time_short_tip));
                                            postDelayed(500, new Runnable() {
                                                @Override
                                                public void run() {
                                                    Record.instance().stopRecord();
                                                }
                                            });
                                            postDelayed(100, new Runnable() {
                                                @Override
                                                public void run() {
                                                    mViewHolder.v(R.id.mic_view).setEnabled(true);
                                                }
                                            });
                                        } else {
                                            replaceIView(new PublishVoiceNextDynamicUIView(filePath, time, mMusicRealm)
                                                    .setPublishAction(mPublishAction));
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onRecordError(Exception error) {

                            }

                            @Override
                            public void onRecordTimeChanged(long millis) {

                            }

                            @Override
                            public void onAmplitudeChanged(int amplitude) {

                            }

                            @Override
                            public void onAudioDataChanged(short[] buffer, int readsize, boolean mformRight) {
                                mwaveCanvas.updateAudioData(buffer, readsize, mformRight);
                            }
                        });
    }

    public PublishVoiceDynamicUIView setPublishAction(Action0 publishAction) {
        mPublishAction = publishAction;
        return this;
    }

    private void WaveCanvasInit() {
        mwaveCanvas = new WaveCanvas();
        WaveSurfaceView surfaceView = mViewHolder.v(R.id.surfaceView);
        mwaveCanvas.baseLine = surfaceView.getHeight() / 2;
        mwaveCanvas.init(surfaceView);
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        WaveCanvasInit();
        mMusicIntentReceiver = new MusicIntentReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        mActivity.registerReceiver(mMusicIntentReceiver, filter);
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        recordStop();
    }

    protected void selectorMusic() {
        startIView(new AddBgmUIView(new Action1<MusicRealm>() {
            @Override
            public void call(MusicRealm musicRealm) {
                //选中后
                initMusicLayout(musicRealm);
            }
        }));
    }

    private void initMusicLayout(MusicRealm musicRealm) {
        mMusicRealm = musicRealm;
        mViewHolder.v(R.id.music_control_layout).setVisibility(View.VISIBLE);
        mViewHolder.v(R.id.add_bgm_layout).setVisibility(View.GONE);

        mViewHolder.tv(R.id.name_view).setText(musicRealm.getName());
        mViewHolder.v(R.id.modify_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorMusic();
            }
        });

        final RTextView volumeView = mViewHolder.v(R.id.volume_view);

        mSeekBar.addOnProgressChangeListener(new RSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgress(int progress) {
                PublishVoiceDynamicUIView.progress = progress;
                volumeView.setText(progress + "");
            }
        });

        mSeekBar.setCurProgress(progress);
    }

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        //耳机拔出
                        Log.d("####", "Headset is unplugged");
                        Record.instance().setMixBgm(false);
                        break;
                    case 1:
                        Log.d("####", "Headset is plugged");
                        Record.instance().setMixBgm(true);
                        break;
                    default:
                        Log.d("####", "I have no idea what the headset state is");
                }
            }
        }
    }
}
