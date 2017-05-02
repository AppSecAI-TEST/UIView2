package com.hn.d.valley.base.oss;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.utils.string.MD5;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.FileUrlRealm;
import com.hn.d.valley.realm.RRealm;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

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
public class OssControl {

    public static final int uploadCircleImg = 1;
    public static final int uploadAvatorImg = 2;
    public static final int uploadVideo = 3;
    /**
     * 保存上传成功了的文件映射,防止重复上传
     */
    public static ArrayMap<String, String> urlMap = new ArrayMap();
    List<String> needUploadList;
    List<String> uploadList;
    int index = 0;//正在上传的索引位置
    int type = uploadAvatorImg;
    OnUploadListener mUploadListener;
    boolean isCancel = false;
    private Subscription mSubscribe;

    public OssControl(OnUploadListener uploadListener) {
        mUploadListener = uploadListener;
    }

    /**
     * 检查路径是否已经上传成功过
     */
    public static String isUpload(final String path) {
        String s = urlMap.get(path);
        return s;
    }

    public void uploadCircleImg(List<String> files) {
        if (files.isEmpty()) {
            return;
        }

        needUploadList = files;
        index = 0;
        type = uploadCircleImg;
        uploadList = new ArrayList<>();

        mUploadListener.onUploadStart();

        startUpload();
    }

    public void uploadCircleImg(String file) {
        List<String> files = new ArrayList<>();
        if (TextUtils.isEmpty(file)) {
            mUploadListener.onUploadSucceed(files);
            return;
        }
        files.add(file);
        uploadCircleImg(files);
    }

    /**
     * 取消上传
     */
    public void setCancel(boolean cancel) {
        isCancel = cancel;
        if (mSubscribe != null) {
            mSubscribe.unsubscribe();
        }
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

        final String path = needUploadList.get(index);

        String upload = isUpload(path);
        if (upload == null) {
            L.d("准备上传图片:" + path);

            OssHelper.checkUrl(path, new Action1<String>() {
                @Override
                public void call(String s) {
                    if (TextUtils.isEmpty(s)) {
                        mSubscribe = OssHelper.uploadCircleImg(path)
                                .subscribe(new BaseSingleSubscriber<String>() {
                                    @Override
                                    public void onSucceed(String s) {
                                        String circleUrl = OssHelper.getCircleUrl(s);
                                        if (BuildConfig.DEBUG) {
                                            L.i(path + " 上传成功至->" + circleUrl);
                                        }
                                        urlMap.put(path, circleUrl);
                                        succeed(circleUrl);

                                        RRealm.save(new FileUrlRealm(MD5.getStreamMD5(path), path, circleUrl));
                                    }

                                    @Override
                                    public void onError(int code, String msg) {
                                        super.onError(code, msg);
                                        mUploadListener.onUploadFailed(code, msg);
                                    }
                                });
                    } else {
                        if (BuildConfig.DEBUG) {
                            L.i("图片已上传过 秒传->" + s);
                        }
                        urlMap.put(path, s);
                        succeed(s);
                    }
                }
            });
        } else {
            if (BuildConfig.DEBUG) {
                L.i("图片已上传过->" + upload);
            }
            succeed(upload);
        }
    }

    private void succeed(String circleUrl) {
        uploadList.add(circleUrl);
        index++;
        startUpload();
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
