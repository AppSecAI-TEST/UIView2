package com.hn.d.valley.base.oss;

import com.hn.d.valley.base.rx.BaseSingleSubscriber;

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
public class OssControl {

    public static final int uploadCircleImg = 1;
    public static final int uploadAvatorImg = 2;
    public static final int uploadVideo = 3;
    List<String> needUploadList;
    List<String> uploadList;
    int index = 0;//正在上传的索引位置
    int type = uploadAvatorImg;
    OnUploadListener mUploadListener;
    boolean isCancel = false;

    public OssControl(OnUploadListener uploadListener) {
        mUploadListener = uploadListener;
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

        OssHelper.uploadCircleImg(needUploadList.get(index))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        uploadList.add(OssHelper.getCircleUrl(s));
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
