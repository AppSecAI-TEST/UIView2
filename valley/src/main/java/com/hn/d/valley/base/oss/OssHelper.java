package com.hn.d.valley.base.oss;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.net.TransformUtils;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.media.ImageUtil;
import com.angcyo.uiview.utils.string.MD5;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.TokenBean;
import com.hn.d.valley.bean.realm.FileUrlRealm;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.OtherService;
import com.hn.d.valley.service.UploadService;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
    public static final String BASE_AUDIO_URL = "http://audio.klgwl.com";


    public static final String BASE_AVATOR_BUCKET = "klg-useravator";
    public static final String BASE_CIRCLE_BUCKET = "klg-circleimg";
    public static final String BASE_VIDEO_BUCKET = "klg-video";
    public static final String BASE_AUDIO_BUCKET = "klg-audio";

    static OSSClient avatorOss, circleOss, videoOss, audioOss;
//    static OSSCredentialProvider credentialProvider =
//            new OSSPlainTextAKSKCredentialProvider("UuyoRLLDaiTyRYD5", "06a8SRzXM0ELLnOluUMmkR9rLySFYh");
//
//    static OSSCredentialProvider credetialProvider = new OSSFederationCredentialProvider() {
//        @Override
//        public OSSFederationToken getFederationToken() {
//            try {
//                URL stsUrl = new URL("http://localhost:8080/distribute-token.json");
//                HttpURLConnection conn = (HttpURLConnection) stsUrl.openConnection();
//                InputStream input = conn.getInputStream();
//                String jsonText = IOUtils.readStreamAsString(input, OSSConstants.DEFAULT_CHARSET_NAME);
//                JSONObject jsonObjs = new JSONObject(jsonText);
//                String ak = jsonObjs.getString("accessKeyId");
//                String sk = jsonObjs.getString("accessKeySecret");
//                String token = jsonObjs.getString("securityToken");
//                String expiration = jsonObjs.getString("expiration");
//                return new OSSFederationToken(ak, sk, token, expiration);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    };

    static OSSCredentialProvider credetialProvider;
    static long lastTokenTime = 0;

    private static void checkToken(final Action1<OSSCredentialProvider> action) {
        final long time = System.currentTimeMillis();
        if (credetialProvider == null || time - lastTokenTime >= 60 * 30 * 1000) {
            RRetrofit.create(OtherService.class)
                    .getToken(Param.buildMap())
                    .compose(Rx.transformer(TokenBean.class))
                    .observeOn(Schedulers.io())
                    .subscribe(new BaseSingleSubscriber<TokenBean>() {
                        @Override
                        public void onSucceed(TokenBean bean) {
                            lastTokenTime = time;
                            credetialProvider = new OSSStsTokenCredentialProvider(bean.getAccess_key(), bean.getAccess_key_secret(), bean.getToken());
                            action.call(credetialProvider);
                        }
                    });
        } else {
            action.call(credetialProvider);
        }
    }

    private static void getAvatorOss(final Action1<OSSClient> action) {
        checkToken(new Action1<OSSCredentialProvider>() {
            @Override
            public void call(OSSCredentialProvider ossCredentialProvider) {
                if (avatorOss == null) {
                    avatorOss = new OSSClient(ValleyApp.getApp(), BASE_AVATOR_URL, credetialProvider);
                } else {
                    avatorOss.updateCredentialProvider(credetialProvider);
                }
                action.call(avatorOss);
            }
        });
    }


    private static void getAudioOss(final Action1<OSSClient> action) {
        checkToken(new Action1<OSSCredentialProvider>() {
            @Override
            public void call(OSSCredentialProvider ossCredentialProvider) {
                if (audioOss == null) {
                    audioOss = new OSSClient(ValleyApp.getApp(), BASE_AUDIO_URL, credetialProvider);
                } else {
                    audioOss.updateCredentialProvider(credetialProvider);
                }
                action.call(audioOss);
            }
        });
    }

    private static void getCircleOss(final Action1<OSSClient> action) {
        checkToken(new Action1<OSSCredentialProvider>() {
            @Override
            public void call(OSSCredentialProvider ossCredentialProvider) {
                if (circleOss == null) {
                    circleOss = new OSSClient(ValleyApp.getApp(), BASE_CIRCLE_URL, credetialProvider);
                } else {
                    circleOss.updateCredentialProvider(credetialProvider);
                }
                action.call(circleOss);
            }
        });
    }

    private static void getVideoOss(final Action1<OSSClient> action) {
        checkToken(new Action1<OSSCredentialProvider>() {
            @Override
            public void call(OSSCredentialProvider ossCredentialProvider) {
                if (videoOss == null) {
                    videoOss = new OSSClient(ValleyApp.getApp(), BASE_VIDEO_URL, credetialProvider);
                } else {
                    videoOss.updateCredentialProvider(credetialProvider);
                }
                action.call(videoOss);
            }
        });
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

    public static String getAudioUrl(String key) {
        return BASE_AUDIO_URL + File.separator + key;
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
     * 上传录音
     */
    public static Observable uploadAudio(final String uploadFilePath) {
        return Observable.create(new OssObservable(BASE_AUDIO_BUCKET, uploadFilePath))
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
        final String[] pictureheight_s;
        try {
            pictureheight_s = url.substring(0, url.lastIndexOf('.')).split("_s_");
        } catch (Exception e) {
            return new int[]{0, 0};
        }
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
        if (width > 0 && height > 0) {
            return url + "?x-oss-process=image/resize,m_mfit,h_" + height + ",w_" + width;
        } else {
            return url;
        }
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
     * 根据url提供的宽高, 获取缩略图的宽高
     */
    public static int[] getImageThumbSize2(String url) {
        final int[] widthHeightWithUrl = getWidthHeightWithUrl(url);
        return getThumbDisplaySize3(widthHeightWithUrl[0], widthHeightWithUrl[1]);
    }

    public static int[] getThumbDisplaySize3(float srcWidth, float srcHeight) {
        final float TARGET_WIDTH = 124 * ScreenUtil.density;//3f / 4 * ScreenUtil.screenWidth;
        final float TARGET_HEIGHT = 180 * ScreenUtil.density;//3f / 4 * ScreenUtil.screenHeight;

        return getThumbDisplaySize3(srcWidth, srcHeight, TARGET_WIDTH, TARGET_HEIGHT);
    }

    public static int[] getThumbDisplaySize3(float srcWidth, float srcHeight, float maxWidth, float maxHeight) {
        int[] size = new int[2];
        final float TARGET_WIDTH = maxWidth;
        final float TARGET_HEIGHT = maxHeight;

        float scale = 1f;
        if (srcWidth > srcHeight) {
            //宽图
            if (srcWidth > TARGET_HEIGHT) {
                scale = TARGET_HEIGHT / srcWidth;
            }
        } else {
            //高图
            if (srcHeight > TARGET_HEIGHT) {
                scale = TARGET_HEIGHT / srcHeight;
            }
        }

        size[0] = (int) (srcWidth * scale);
        size[1] = (int) (srcHeight * scale);
        return size;
    }

    public static int[] getThumbDisplaySize2(float srcWidth, float srcHeight) {
        int[] size = new int[2];
        final float TARGET_WIDTH = 124 * ScreenUtil.density;//3f / 4 * ScreenUtil.screenWidth;
        final float TARGET_HEIGHT = 180 * ScreenUtil.density;//3f / 4 * ScreenUtil.screenHeight;

        //float srcScale = srcWidth / srcHeight;//原始宽高的比例

        float wScale = TARGET_WIDTH / srcWidth;
        float hScale = TARGET_HEIGHT / srcHeight;

        if (srcHeight < srcWidth) {
            wScale = TARGET_WIDTH / srcHeight;
            hScale = TARGET_HEIGHT / srcWidth;
        }

        float tScale;
        if (srcWidth > TARGET_WIDTH) {
            if (srcHeight > TARGET_HEIGHT) {
                tScale = Math.min(wScale, hScale);
//                size[0] = (int) TARGET_WIDTH;
//                size[1] = (int) TARGET_HEIGHT;
            } else {
                tScale = wScale;
            }
        } else {
            if (srcHeight > TARGET_HEIGHT) {
                tScale = hScale;
            } else {
                tScale = Math.min(wScale, hScale);
            }
        }
//        if (TARGET_WIDTH > srcWidth && TARGET_HEIGHT > srcHeight) {
//            //放大图片
//            tScale = Math.min(wScale, hScale);
//        } else {
//            //缩小图片
//            tScale = Math.min(wScale, hScale);
//        }

        size[0] = (int) (tScale * srcWidth);
        size[1] = (int) (tScale * srcHeight);

//        if (size[0] < TARGET_WIDTH && size[1] < TARGET_HEIGHT) {
//            //一张很小的图片
//            L.e("call: getThumbDisplaySize2([srcWidth, srcHeight])-> " + size[0] + ":" + size[1] +
//                    " " + TARGET_WIDTH + ":" + TARGET_HEIGHT);
//            size[0] = (int) TARGET_WIDTH;
//            size[1] = (int) TARGET_HEIGHT;
//        }

        return size;
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

    /**
     * 根据宽高, 创建一个符合规则的文件名
     */
    public static String createImageFileName(int width, int height) {
        return "_s_" + width + "x" + height + ".png";
    }

    public static String createImageFileNameGif(int width, int height) {
        return "_s_" + width + "x" + height + ".gif";
    }

    public static String createVideoFileName(int time) {
        return "_t_" + time + ".mp4";
    }

    public static String createVoiceFileName(int time) {
        return "_t_" + time + ".aac";
    }

    /**
     * 检查是否已经上传过
     */
    public static void checkUrl(final String filePath, final Action1<String> result) {
        RRetrofit.create(UploadService.class)
                .checkMd5(Param.buildMap("md5:" + "[\"" + MD5.getStreamMD5(filePath) + "\"]"))
                .compose(Rx.transformerList(String.class))
                .retry(3)
                .subscribe(new BaseSingleSubscriber<List<String>>() {
                    @Override
                    public void onSucceed(List<String> bean) {
                        super.onSucceed(bean);
                        if (bean.isEmpty()) {
                            result.call("");
                        } else {
                            result.call(bean.get(0));
                        }
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if (isError) {
                            RRealm.exe(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmResults<FileUrlRealm> md5 = realm.where(FileUrlRealm.class).equalTo("md5", MD5.getStreamMD5(filePath)).findAll();
                                    if (md5.size() > 0) {
                                        result.call(md5.first().getUrl());
                                    } else {
                                        result.call("");
                                    }
                                }
                            });
                        }
                    }
                });
    }

    public static void saveUrl(String filePath, String url) {
        final String streamMD5 = MD5.getStreamMD5(filePath);
        RRealm.save(new FileUrlRealm(streamMD5, filePath, url));

        RRetrofit.create(UploadService.class)
                .success(Param.buildMap("md5:" + "{\"" + streamMD5 + "\":\"" + url + "\"}"))
                .compose(Rx.transformer(String.class))
                .retry(3)
                .subscribe(new BaseSingleSubscriber<String>() {
                });
    }

    static class OssObservable implements Observable.OnSubscribe<String> {

        String bucket;
        String uploadFilePath;

        public OssObservable(String bucket, String uploadFilePath) {
            this.bucket = bucket;
            this.uploadFilePath = uploadFilePath;
        }

        Action1<OSSClient> createAction(final String uuid, final PutObjectRequest put,
                                        final Subscriber<? super String> subscriber) {
            return new Action1<OSSClient>() {
                @Override
                public void call(OSSClient ossClient) {
                    try {
                        ossClient.putObject(put);
                        subscriber.onNext(uuid);
                    } catch (Exception e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }
            };
        }

        @Override
        public void call(final Subscriber<? super String> subscriber) {
            if (subscriber.isUnsubscribed()) {
                return;
            }
//            String uuid = UUID.randomUUID().toString();
            String uuid = UserCache.getUserAccount() + uploadFilePath.substring(uploadFilePath.lastIndexOf('/'));
            // 构造上传请求
            final PutObjectRequest put = new PutObjectRequest(bucket, uuid, uploadFilePath);
            try {
                if (TextUtils.equals(bucket, BASE_AVATOR_BUCKET)) {
                    getAvatorOss(createAction(uuid, put, subscriber));
                } else if (TextUtils.equals(bucket, BASE_VIDEO_BUCKET)) {
                    getVideoOss(createAction(uuid, put, subscriber));
                } else if (TextUtils.equals(bucket, BASE_CIRCLE_BUCKET)) {
                    getCircleOss(createAction(uuid, put, subscriber));
                } else if (TextUtils.equals(bucket, BASE_AUDIO_BUCKET)) {
                    getAudioOss(createAction(uuid, put, subscriber));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
