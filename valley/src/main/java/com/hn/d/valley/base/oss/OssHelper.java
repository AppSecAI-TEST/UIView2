package com.hn.d.valley.base.oss;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.angcyo.uiview.net.TransformUtils;
import com.angcyo.uiview.utils.media.ImageUtil;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.cache.UserCache;

import java.io.File;

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

    /**
     * 从url中, 返回图片的宽高信息
     */
    public static int[] getWidthHeightWithUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return new int[]{0, 0};
        }
//        width_640.0pictureheight_1136.0
        final String[] pictureheight_s = url.split("_s_");
        float height = 0, width = 0;
        if (pictureheight_s.length >= 2) {
            final String[] size = pictureheight_s[pictureheight_s.length - 1].split("x");
            if (size.length >= 2) {
                width = Float.valueOf(size[0]);
                height = Float.valueOf(size[1]);
            }
        }
        return new int[]{(int) width, (int) height};
    }

    /**
     * 根据宽高, 返回对应的oss url地址
     */
    public static String getImageThumb(String url, int width, int height) {
        return url + "?x-oss-process=image/resize,m_fill,h_" + height + ",w_" + width;
    }

    public static String getImageThumb(String url) {
        int width, height;
        final int[] widthHeightWithUrl = getWidthHeightWithUrl(url);
        final ImageUtil.ImageSize thumbnailDisplaySize = ImageUtil.getThumbnailDisplaySize(widthHeightWithUrl[0], widthHeightWithUrl[1]);
        width = thumbnailDisplaySize.width;
        height = thumbnailDisplaySize.height;
        return getImageThumb(url, width, height);
    }

    /**
     * 根据url提供的宽高, 获取缩略图的宽高
     */
    public static int[] getImageThumbSize(String url) {
        final int[] widthHeightWithUrl = getWidthHeightWithUrl(url);
        final ImageUtil.ImageSize thumbnailDisplaySize = ImageUtil.getThumbnailDisplaySize(widthHeightWithUrl[0], widthHeightWithUrl[1]);
        return new int[]{thumbnailDisplaySize.width, thumbnailDisplaySize.height};
    }

    /**
     * 根据url提供的宽高, 设置视图的宽高
     */
    public static void setViewSize(View view, String url) {
        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        final int[] imageThumbSize = getImageThumbSize(url);
        layoutParams.height = imageThumbSize[1];
        layoutParams.width = imageThumbSize[0];
        view.setLayoutParams(layoutParams);
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
//            String uuid = UUID.randomUUID().toString();
            String uuid = UserCache.getUserAccount() + uploadFilePath.substring(uploadFilePath.lastIndexOf('/'));
            // 构造上传请求
            PutObjectRequest put = new PutObjectRequest(bucket, uuid, uploadFilePath);
            try {
                if (TextUtils.equals(bucket, BASE_AVATOR_BUCKET)) {
                    getAvatorOss().putObject(put);
                } else if (TextUtils.equals(bucket, BASE_VIDEO_BUCKET)) {
                    getVideoOss().putObject(put);
                } else if (TextUtils.equals(bucket, BASE_CIRCLE_BUCKET)) {
                    getCircleOss().putObject(put);
                }
                subscriber.onNext(uuid);
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        }
    }
}
