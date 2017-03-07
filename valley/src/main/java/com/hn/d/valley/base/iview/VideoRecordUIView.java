package com.hn.d.valley.base.iview;

import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;

import com.angcyo.uiview.github.utilcode.utils.FileUtils;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RLoopRecyclerView;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.media.BitmapDecoder;
import com.angcyo.uiview.view.UIIViewImpl;
import com.angcyo.uiview.widget.RecordButton;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.m3b.rbrecoderlib.GPUImage;
import com.m3b.rbrecoderlib.GPUImageFilter;
import com.m3b.rbrecoderlib.GPUImageFilterGroup;
import com.m3b.rbrecoderlib.GPUImageMovieWriter;
import com.m3b.rbrecoderlib.utils.CameraHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;

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

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public boolean mIsRecording = false;
    int rotationRecord = 0;
    @BindView(R.id.record_view)
    RecordButton mRecordView;
    @BindView(R.id.loop_recycler_view)
    RLoopRecyclerView mLoopRecyclerView;
    private GPUImage mGPUImage;
    private OrientationEventListener mOrientationEventListener;
    private CameraHelper mCameraHelper;
    private CameraLoader mCamera;
    private GPUImageMovieWriter mMovieWriter;
    private GPUImageFilter mFilter;
    private File mRecordFile;
    private RLoopRecyclerView.LoopAdapter<FilterTools.FilterBean> mLoopAdapter;

    private static File getOutputMediaFile(final int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "videotest");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        File mediaStorageDir = new File(ValleyApp.getApp().getCacheDir(), "record_video");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("videotest", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

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

        mRecordView.setOnRecordListener(new RecordButton.OnRecordListener() {
            @Override
            public void onRecordStart() {
                startRecord();
            }

            @Override
            public void onRecordEnd(int progress) {
                if (progress < 3) {
                    T_.error(getString(R.string.recore_time_short));
                } else {
                    stopRecord();
                    T_.info(progress + " s" + mRecordFile.getAbsolutePath());
                    final String parent = mRecordFile.getParent();

                    final String newName = UUID.randomUUID().toString() + "_t_" + progress;
                    final String thumbPath;
                    if (rotationRecord == 0) {
                        thumbPath = parent + File.separator + UUID.randomUUID().toString() + "_s_" + "960" + "x" + "1280";

                    } else {
                        thumbPath = parent + File.separator + UUID.randomUUID().toString() + "_s_" + "1280" + "x" + "960";
                    }
                    FileUtils.rename(mRecordFile, newName);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String videoPath = parent + File.separator + newName;
                            BitmapDecoder.extractThumbnail(videoPath, thumbPath);
                            startIView(new VideoPlayUIView(thumbPath, videoPath));
                        }
                    }, 100);
                }
            }
        });
        mRecordView.setMaxProgress(getResources().getInteger(R.integer.max_video_record_length));

        mLoopAdapter = new RLoopRecyclerView.LoopAdapter<FilterTools.FilterBean>(mActivity, FilterTools.createFilterList()) {
            @Override
            protected int getItemLayoutId(int viewType) {
                return R.layout.item_gpuiimage_filter;
            }

            @Override
            public void onBindLoopViewHolder(RBaseViewHolder holder, int position, FilterTools.FilterBean bean) {
                holder.tv(R.id.text_view).setText(bean.name);
            }
        };
        mLoopRecyclerView.setAdapter(mLoopAdapter);
        mLoopRecyclerView.setOnPageListener(new RLoopRecyclerView.OnPageListener() {
            @Override
            public void onPageSelector(int position) {
                switchFilterTo(mLoopAdapter.getAllDatas().get(position).createFilterForType(mActivity));
            }
        });
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
     * 开始录制
     */
    private void startRecord() {
        mIsRecording = true;
        mLoopRecyclerView.setVisibility(View.GONE);
        mRecordFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        mMovieWriter.startRecording(mRecordFile.getAbsolutePath(), "high", rotationRecord);//do not change it without standrad
    }

    private void stopRecord() {
        mIsRecording = false;
        mLoopRecyclerView.setVisibility(View.VISIBLE);
        mMovieWriter.stopRecording();
    }


    /**
     * 是否有2个摄像头
     */
    private boolean hasTwoCamera() {
        return mCameraHelper.hasFrontCamera() && mCameraHelper.hasBackCamera();
    }

    /**
     * 切换摄像头
     */
    private void switchCamera() {
        mCamera.switchCamera();
    }

    /**
     * 切换滤镜
     */
    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;

            GPUImageFilterGroup filters = new GPUImageFilterGroup();
            filters.addFilter(mFilter);
            filters.addFilter(mMovieWriter);

            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_center_pause);
            //LogoFilter logoFilter = new LogoFilter(bitmap,new Rect(100,100,200,200));
            //filters.addFilter(logoFilter);

            mGPUImage.setFilter(filters);
        }
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
