package com.hn.d.valley.base.oss;

import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/09 19:23
 * 修改人员：Robi
 * 修改时间：2017/01/09 19:23
 * 修改备注：
 * Version: 1.0.0
 */
public class OssControl2 {

    public static final int uploadCircleImg = 1;
    public static final int uploadAvatorImg = 2;
    public static final int uploadVideo = 3;
    public static final int uploadAudio = 4;

    List<String> needUploadList;
    List<String> uploadList;
    int index = 0;//正在上传的索引位置
    int type = uploadAvatorImg;
    OnUploadListener mUploadListener;
    boolean isCancel = false;

    public OssControl2(OnUploadListener uploadListener) {
        mUploadListener = uploadListener;
    }

    public void uploadCircleImg(List<String> files) {
        if (files.isEmpty()) {
            mUploadListener.onUploadSucceed(files);
            return;
        }

        needUploadList = files;
        index = 0;
        type = uploadCircleImg;
        uploadList = new ArrayList<>();

        mUploadListener.onUploadStart();

        startUpload();
    }

    public void uploadVideo(final String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            mUploadListener.onUploadSucceed(null);
            return;
        }

        File file = new File(videoPath);
        if (!file.exists()) {
            mUploadListener.onUploadSucceed(null);
            return;
        }

        String upload = OssControl.isUpload(videoPath);
        if (upload == null) {
            L.d("准备上传视频:" + videoPath);
            OssHelper.uploadVideo(videoPath)
                    .subscribe(new BaseSingleSubscriber<String>() {
                        @Override
                        public void onSucceed(String s) {
                            if (BuildConfig.DEBUG) {
                                L.i(videoPath + " 上传成功至->" + s);
                            }
                            List<String> list = new ArrayList<>();
                            String videoUrl = OssHelper.getVideoUrl(s);
                            OssControl.urlMap.put(videoPath, videoUrl);
                            list.add(videoUrl);
                            mUploadListener.onUploadSucceed(list);
                        }

                        @Override
                        public void onError(int code, String msg) {
                            super.onError(code, msg);
                            mUploadListener.onUploadFailed(code, msg);
                        }
                    });
        } else {
            List<String> list = new ArrayList<>();
            list.add(upload);
            mUploadListener.onUploadSucceed(list);
        }
    }

    public void uploadAudio(final String audioPath) {
        if (TextUtils.isEmpty(audioPath)) {
            mUploadListener.onUploadSucceed(null);
            return;
        }

        File file = new File(audioPath);
        if (!file.exists()) {
            mUploadListener.onUploadSucceed(null);
            return;
        }

        String upload = OssControl.isUpload(audioPath);
        if (upload == null) {
            L.d("准备上传语音:" + audioPath);
            OssHelper.uploadAudio(audioPath)
                    .subscribe(new BaseSingleSubscriber<String>() {
                        @Override
                        public void onSucceed(String s) {
                            if (BuildConfig.DEBUG) {
                                L.i(audioPath + " 上传成功至->" + s);
                            }
                            List<String> list = new ArrayList<>();
                            String audioUrl = OssHelper.getAudioUrl(s);
                            OssControl.urlMap.put(audioPath, audioUrl);
                            list.add(audioUrl);
                            mUploadListener.onUploadSucceed(list);
                        }

                        @Override
                        public void onError(int code, String msg) {
                            super.onError(code, msg);
                            mUploadListener.onUploadFailed(code, msg);
                        }
                    });
        } else {
            List<String> list = new ArrayList<>();
            list.add(upload);
            mUploadListener.onUploadSucceed(list);
        }
    }

    /**
     * 取消上传
     */
    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    private void startUpload() {
        if (isCancel) {
            mUploadListener.onUploadFailed(-1, "用户取消上传!");
            return;
        }

        if (index >= needUploadList.size()) {
            mUploadListener.onUploadSucceed(uploadList);
            return;
        }

        final String needUploadPath = needUploadList.get(index);
        if (BuildConfig.DEBUG) {
            L.i("开始上传:" + needUploadPath);
        }
        OssHelper.uploadCircleImg(needUploadPath)
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String s) {
                        if (BuildConfig.DEBUG) {
                            L.i(needUploadPath + " 上传成功至->" + s);
                        }
                        uploadList.add(needUploadPath + "|" + OssHelper.getCircleUrl(s));
                        index++;
                        startUpload();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        mUploadListener.onUploadFailed(code, msg);
                    }
                });

    }

    public interface OnUploadListener {
        /**
         * 开始上传
         */
        void onUploadStart();

        void onUploadSucceed(List<String> list);

        /**
         * 上传失败
         */
        void onUploadFailed(int code, String msg);
    }
}
