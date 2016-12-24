package com.hn.d.valley.base.oss;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.angcyo.uiview.net.TransformUtils;
import com.hn.d.valley.ValleyApp;

import java.io.File;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：阿里云文件 操作助手
 * 创建人员：Robi
 * 创建时间：2016/12/14 9:59
 * 修改人员：Robi
 * 修改时间：2016/12/14 9:59
 * 修改备注：
 * Version: 1.0.0
 */
public class OssHelper {

    public static final String BASE_AVATOR_URL = "http://avatorimg.klgwl.com";
    public static final String BASE_CIRCLE_URL = "http://circleimg.klgwl.com";
    public static final String BASE_VIDEO_URL = "http://video.klgwl.com";

    public static final String BASE_AVATOR_BUCKET = "klg-useravator";
    public static final String BASE_CIRCLE_BUCKET = "klg-circleimg";
    public static final String BASE_VIDEO_BUCKET = "klg-video";

    static OSSClient avatorOss, circleOss, videoOss;
    static OSSCredentialProvider credentialProvider =
            new OSSPlainTextAKSKCredentialProvider("UuyoRLLDaiTyRYD5", "06a8SRzXM0ELLnOluUMmkR9rLySFYh");

    private static OSSClient getAvatorOss() {
        if (avatorOss == null) {
            avatorOss = new OSSClient(ValleyApp.getApp(), BASE_AVATOR_URL, credentialProvider);
        }
        return avatorOss;
    }

    private static OSSClient getCircleOss() {
        if (circleOss == null) {
            circleOss = new OSSClient(ValleyApp.getApp(), BASE_CIRCLE_URL, credentialProvider);
        }
        return circleOss;
    }


    private static OSSClient getVideoOss() {
        if (videoOss == null) {
            videoOss = new OSSClient(ValleyApp.getApp(), BASE_VIDEO_URL, credentialProvider);
        }
        return videoOss;
    }


    public static String getVideoUrl(String key) {
        return BASE_VIDEO_URL + File.separator + key;
    }

    public static String getAvatorUrl(String key) {
        return BASE_AVATOR_URL + File.separator + key;
    }

    public static String getCircleUrl(String key) {
        return BASE_CIRCLE_URL + File.separator + key;
    }

    /**
     * 上传用户头像文件
     */
    public static Observable<String> uploadAvatorImg(final String uploadFilePath) {
        return Observable.create(new OssObservable(BASE_AVATOR_BUCKET, uploadFilePath))
                .compose(TransformUtils.<String>defaultSchedulers());
    }

    /**
     * 上传朋友圈文件
     */
    public static Observable uploadCircleImg(final String uploadFilePath) {
        return Observable.create(new OssObservable(BASE_CIRCLE_BUCKET, uploadFilePath))
                .compose(TransformUtils.<String>defaultSchedulers());
    }

    /**
     * 上传视频文件
     */
    public static Observable uploadVideo(final String uploadFilePath) {
        return Observable.create(new OssObservable(BASE_VIDEO_BUCKET, uploadFilePath))
                .compose(TransformUtils.<String>defaultSchedulers());
    }

    static class OssObservable implements Observable.OnSubscribe<String> {

        String bucket;
        String uploadFilePath;

        public OssObservable(String bucket, String uploadFilePath) {
            this.bucket = bucket;
            this.uploadFilePath = uploadFilePath;
        }

        @Override
        public void call(Subscriber<? super String> subscriber) {
            if (subscriber.isUnsubscribed()) {
                return;
            }
            String uuid = UUID.randomUUID().toString();
            // 构造上传请求
            PutObjectRequest put = new PutObjectRequest(bucket, uuid, uploadFilePath);
            try {
                getAvatorOss().putObject(put);
                subscriber.onNext(uuid);
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        }
    }
}
