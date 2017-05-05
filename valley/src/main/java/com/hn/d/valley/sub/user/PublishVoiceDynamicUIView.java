package com.hn.d.valley.sub.user;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.github.ripple.RippleBackground;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.widget.RSeekBar;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.bean.realm.MusicRealm;
import com.hn.d.valley.sub.user.sub.AddBgmUIView;
import com.hn.d.valley.widget.HnRecTextView;
import com.hn.d.valley.widget.HnRecordTimeView;

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

    private RippleBackground mRippleBackground;
    private HnRecordTimeView mHnRecordTimeView;
    private HnRecTextView mHnRecTextView;
    private MusicRealm mMusicRealm;
    private RSeekBar mSeekBar;

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(mActivity, R.string.publish_voice)
                .setShowBackImageView(true);
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
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
                        mViewHolder.v(R.id.music_root_layout).setEnabled(false);
                        mHnRecTextView.setRec(true);
                        mRippleBackground.startRippleAnimation();
                        mHnRecordTimeView.startRecord(new HnRecordTimeView.OnMaxTimeListener() {
                            @Override
                            public void onMaxTime(long maxTime) {

                            }
                        });
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mViewHolder.v(R.id.music_root_layout).setEnabled(true);
                        mHnRecTextView.setRec(false);
                        mRippleBackground.stopRippleAnimation();
                        mHnRecordTimeView.stopRecord();
                        break;
                }
                return true;
            }
        });
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

        mSeekBar = mViewHolder.v(R.id.seek_bar);
        final RTextView volumeView = mViewHolder.v(R.id.volume_view);
        mSeekBar.addOnProgressChangeListener(new RSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgress(int progress) {
                volumeView.setText(progress + "");
            }
        });
    }

}
