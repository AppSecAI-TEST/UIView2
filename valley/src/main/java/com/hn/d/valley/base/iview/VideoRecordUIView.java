package com.hn.d.valley.base.iview;

import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;

import com.angcyo.uiview.view.UIIViewImpl;
import com.hn.d.valley.R;
import com.m3b.rbrecoderlib.GPUImage;
import com.m3b.rbrecoderlib.GPUImageMovieWriter;
import com.m3b.rbrecoderlib.utils.CameraHelper;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：视频录制
 * 创建人员：Robi
 * 创建时间：2017/03/07 15:47
 * 修改人员：Robi
 * 修改时间：2017/03/07 15:47
 * 修改备注：
 * Version: 1.0.0
 */
public class VideoRecordUIView extends UIIViewImpl {

    public boolean mIsRecording = false;
    int rotationRecord = 0;
    private GPUImage mGPUImage;
    private OrientationEventListener mOrientationEventListener;
    private CameraHelper mCameraHelper;
    private CameraLoader mCamera;
    private GPUImageMovieWriter mMovieWriter;

    @Override
    protected View inflateBaseView(FrameLayout container, LayoutInflater inflater) {
        return LayoutInflater.from(mActivity).inflate(R.layout.view_video_record_layout, container);
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        mGPUImage = new GPUImage(mActivity);
        mGPUImage.setGLSurfaceView((GLSurfaceView) mViewHolder.v(R.id.surfaceView));

        mCameraHelper = new CameraHelper(mActivity);
        mCamera = new CameraLoader();

        mMovieWriter = new GPUImageMovieWriter();
        mGPUImage.setFilter(mMovieWriter);

        rotationListener();
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        mCamera.onResume();
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        mCamera.onPause();
        if (mIsRecording) {
            mMovieWriter.stopRecording();
        }
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
    }

    /**
     * 旋转
     */
    private void rotationListener() {
        mOrientationEventListener = new OrientationEventListener(mActivity) {
            @Override
            public void onOrientationChanged(int rotation) {
                if (mIsRecording == false) {
                    if (((rotation >= 0) && (rotation <= 30)) || (rotation >= 330)) {
                        // 竖屏拍摄
                        rotationRecord = 0;
                    } else if (((rotation >= 230) && (rotation <= 310))) {
                        // 横屏拍摄
                        rotationRecord = 270;

                    } else if (rotation > 30 && rotation < 95) {
                        // 反横屏拍摄
                        rotationRecord = 90;
                    }
                }
            }
        };
        mOrientationEventListener.enable();
    }

    /**
     * CameraLoader
     */
    private class CameraLoader {

        private int mCurrentCameraId = 0;
        private Camera mCameraInstance;

        public void onResume() {
            setUpCamera(mCurrentCameraId);
        }

        public void onPause() {
            releaseCamera();
        }

        public void switchCamera() {
            releaseCamera();
            mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
            setUpCamera(mCurrentCameraId);
        }

        private void setUpCamera(final int id) {
            mCameraInstance = getCameraInstance(id);
            if (mCameraInstance == null)
                return;
            Camera.Parameters parameters = mCameraInstance.getParameters();
            // TODO adjust by getting supportedPreviewSizes and then choosing
            // the best one for screen size (best fill screen)

            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            parameters.setPreviewSize(640, 480);
            mCameraInstance.setParameters(parameters);

            int orientation = mCameraHelper.getCameraDisplayOrientation(mActivity, mCurrentCameraId);
            CameraHelper.CameraInfo2 cameraInfo = new CameraHelper.CameraInfo2();
            mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
            boolean flipHorizontal = cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;

            mGPUImage.setUpCamera(mCameraInstance, orientation, flipHorizontal, false);
        }

        /**
         * A safe way to get an instance of the Camera object.
         */
        private Camera getCameraInstance(final int id) {
            Camera c = null;
            try {
                c = mCameraHelper.openCamera(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }

        private void releaseCamera() {
            mCameraInstance.setPreviewCallback(null);
            mCameraInstance.release();
            mCameraInstance = null;
        }
    }
}
