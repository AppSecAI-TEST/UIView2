package com.hn.d.valley.sub.user;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.widget.RecordTimeView;
import com.example.m3b.Audio;
import com.example.m3b.audiocachedemo.Player;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.oss.OssControl;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.bean.realm.MusicRealm;
import com.hn.d.valley.control.MusicControl;
import com.hn.d.valley.control.PublishControl;
import com.hn.d.valley.control.PublishTaskRealm;
import com.hn.d.valley.control.VoiceStatusInfo;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.widget.HnBigPlayView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnLoading;
import com.lzy.imagepicker.ImagePickerHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.functions.Action0;
import rx.functions.Action1;

import static com.hn.d.valley.control.UserDiscussItemControl.getVideoTimeLong;

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
public class PublishVoiceNextDynamicUIView extends BaseContentUIView {

    private static PublishTaskRealm mPublishTaskRealm;
    String filePath;
    int recordTime;//秒
    Action0 mPublishAction;
    private String mImagePath = ""/*, mImageUrl = ""*/;//选择的图片, 上传之后的地址
    private MusicRealm mMusicRealm;
    private OssControl mOssControl;
    private RecordTimeView mTimeView;
    private Player.OnPlayListener mOnPlayListener;

    public PublishVoiceNextDynamicUIView(String filePath, long recordTime, MusicRealm musicRealm) {
        this.recordTime = (int) (Math.floor(recordTime / 1000f));
        final String newName = filePath + OssHelper.createVoiceFileName(this.recordTime);
        new File(filePath).renameTo(new File(newName));
        this.filePath = newName;
        mMusicRealm = musicRealm;
    }

    public PublishVoiceNextDynamicUIView(PublishTaskRealm publishTaskRealm) {
        mPublishTaskRealm = publishTaskRealm;
        this.filePath = publishTaskRealm.getVoiceStatusInfo().getVoicePath();
        this.mImagePath = publishTaskRealm.getVoiceStatusInfo().getVoiceImagePath();
        this.recordTime = getVideoTimeLong(this.filePath);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(mActivity, R.string.publish_voice)
                .setShowBackImageView(true)
                .addRightItem(TitleBarPattern.buildText(getString(R.string.publish), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPublish();
                    }
                }));
    }

    public PublishVoiceNextDynamicUIView setPublishAction(Action0 publishAction) {
        mPublishAction = publishAction;
        return this;
    }

    private void onPublish() {
        if (mPublishTaskRealm != null) {
            RRealm.exe(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mPublishTaskRealm.deleteFromRealm();
                    mPublishTaskRealm = null;
                }
            });
        }

        PublishTaskRealm publishTask = new PublishTaskRealm(
                new VoiceStatusInfo(mImagePath, filePath));
        PublishControl.instance().addTask(publishTask, true);

        finishIView();
        if (mPublishAction != null) {
            mPublishAction.call();
        } else {
            PublishControl.instance().startPublish();
        }
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_publish_voice_dynamic_next);
    }

    @Override
    public int getDefaultBackgroundColor() {
        return getColor(R.color.base_white);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        Audio.instance().removeOnPlayListener(mOnPlayListener);
        Audio.instance().stop();
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        showPreview();

        mTimeView = mViewHolder.v(R.id.time_view);
        mTimeView.setSumTime(recordTime);

        final HnBigPlayView playView = mViewHolder.v(R.id.play_view);
        playView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playView.isPlaying()) {
                    playView.setPlaying(false);
                    MusicControl.pausePlay(filePath);
                    mTimeView.stopRecord(false);
                } else {
                    playView.setPlaying(true);
                    MusicControl.play(filePath);
                    mTimeView.setTime(0);
                    mTimeView.startRecord(null);
                }
            }
        });


        mOnPlayListener = new Player.OnPlayListener() {
            @Override
            public void onPlay(String url, boolean isPause) {
            }

            @Override
            public void onPlayEnd(String url) {
                playView.setPlaying(false);
                mTimeView.stopRecord(false);
            }
        };
        Audio.instance().addOnPlayListener(mOnPlayListener);

        mViewHolder.v(R.id.re_record_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceIView(new PublishVoiceDynamicUIView(mMusicRealm).setPublishAction(mPublishAction));
            }
        });

        mViewHolder.v(R.id.select_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePickerHelper.startImagePicker(mActivity, true, true, false, false, 1);
            }
        });
    }

    /**
     * 上传图片
     */
    private void uploadImage() {
        mOssControl = new OssControl(new OssControl.OnUploadListener() {
            @Override
            public void onUploadStart() {

            }

            @Override
            public void onUploadSucceed(List<String> list) {
                HnLoading.hide();
                if (!list.isEmpty()) {
                    /*mImageUrl = list.get(0);*/
                }
            }

            @Override
            public void onUploadFailed(int code, String msg) {
                HnLoading.hide();
            }
        });
        mOssControl.uploadCircleImg(mImagePath);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ArrayList<String> images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data);
        final boolean origin = ImagePickerHelper.isOrigin(requestCode, resultCode, data);
        if (images.isEmpty()) {
            return;
        }
        HnLoading.show(mILayout);
        Luban.luban(mActivity, images)
                .subscribe(new Action1<ArrayList<String>>() {
                    @Override
                    public void call(ArrayList<String> strings) {
                        mImagePath = strings.get(0);
                        L.e("call: call([strings])-> " + mImagePath);

                        showPreview();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HnLoading.hide();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        HnLoading.hide();
                    }
                });
    }

    private void showPreview() {
        if (TextUtils.isEmpty(mImagePath)) {
            return;
        }
        HnGlideImageView glideImageView = mViewHolder.v(R.id.image_view);
        glideImageView.setImageUrl(mImagePath);
    }
}
