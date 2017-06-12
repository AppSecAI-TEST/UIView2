package com.hn.d.valley.base.iview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.github.utilcode.utils.FileUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RLoopRecyclerView;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.media.BitmapDecoder;
import com.angcyo.uiview.view.UIIViewImpl;
import com.angcyo.uiview.viewgroup.ExpandRecordLayout;
import com.angcyo.uiview.widget.RSeekBar;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.widget.HnLoading;
import com.m3b.rbrecoderlib.GPUImage;
import com.m3b.rbrecoderlib.GPUImageFilter;
import com.m3b.rbrecoderlib.GPUImageFilterGroup;
import com.m3b.rbrecoderlib.GPUImageMovieWriter;
import com.m3b.rbrecoderlib.filters.MagicBeautyFilter;
import com.m3b.rbrecoderlib.filters.RBLogoFilter;
import com.m3b.rbrecoderlib.utils.CameraHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Action3;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.m3b.rbrecoderlib.Rotation.ROTATION_270;

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
public class VideoRecordUIView extends UIBaseView {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_VOICE = 3;
    public boolean mIsRecording = false;
    int rotationRecord = 0;
    //    RecordButton mRecordView;
    RLoopRecyclerView mLoopRecyclerView;
    ExpandRecordLayout mRecordLayout;
    Action3<UIIViewImpl, String, String> publishAction;
    /**
     * 默认录像level
     */
    GPUImageMovieWriter.Level DefaultLevel = GPUImageMovieWriter.Level.HIGH;
    private GPUImage mGPUImage;
    private OrientationEventListener mOrientationEventListener;
    private CameraHelper mCameraHelper;
    private CameraLoader mCamera;
    private GPUImageMovieWriter mMovieWriter;
    private GPUImageFilter mFilter;
    private File mRecordFile;
    private RLoopRecyclerView.LoopAdapter<FilterTools.FilterBean> mLoopAdapter;
    private RBLogoFilter logofilter;
    private GPUImageFilterGroup initFilter;
    private boolean isBeautyed = true;
    private boolean isSwitched = false;
    private GPUImageFilter currentFilter;
    private boolean needRotate = false;
    private GPUImageFilterGroup filters;
    private MagicBeautyFilter magicBeautyFilter;
    private GLSurfaceView mGLSurfaceView;
    private RModelAdapter mLjAdapter;
    private View prettyLayout;
    private int currentLevel = 0;

    public VideoRecordUIView(Action3<UIIViewImpl, String, String> publishAction) {
        this.publishAction = publishAction;
    }

    public static File getOutputMediaFile(final int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "videotest");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        File mediaStorageDir = new File(ValleyApp.getApp().getCacheDir(), "record");

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
        } else if (type == MEDIA_TYPE_VOICE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "AAC_" + timeStamp + ".aac");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        //mRecordView = v(R.id.record_view);
        mRecordLayout = v(R.id.record_layout);
        mLoopRecyclerView = v(R.id.loop_recycler_view);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        int offset = getDimensionPixelOffset(R.dimen.base_xhdpi);
        return super.getTitleBar()
                .setShowBackImageView(true)
                .setTitleString("")
                .setBackImageRes(R.drawable.quxiao_paishiping)
                .addRightItem(TitleBarPattern.buildImage(R.drawable.meiyan, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (prettyLayout.getVisibility() == View.GONE) {
                            animShowPrettyLayout();
                        } else {
                            animHidePrettyLayout();
                        }
                    }
                }).setRightMargin(offset))
                .addRightItem(TitleBarPattern.buildImage(R.drawable.no_flashlight, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCamera.isCameraFont()) {
                            //前置没有闪光灯
                            return;
                        }

                        ImageView imageView = (ImageView) v;
                        if (v.getTag() == null) {
                            //打开闪光灯
                            openFlashLight(imageView);
                        } else {
                            //关闭闪关灯
                            closeFlashLight(imageView);
                        }
                        mCamera.toggleFlashLight();
                    }
                }).setRightMargin(offset))
                .addRightItem(TitleBarPattern.buildImage(R.drawable.qiehuan_shexiangtou, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchCamera();
                    }
                }))
                ;
    }

    protected void closeFlashLight(ImageView imageView) {
        imageView.setImageResource(R.drawable.no_flashlight);
        imageView.setTag(null);
    }

    protected void openFlashLight(ImageView imageView) {
        imageView.setImageResource(R.drawable.flashlight);
        imageView.setTag("on");
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_video_record_layout);
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        mGPUImage = new GPUImage(mActivity);
        mGLSurfaceView = mViewHolder.v(R.id.surfaceView);
        mGPUImage.setGLSurfaceView(mGLSurfaceView);

        mCameraHelper = new CameraHelper(mActivity);
        mCamera = new CameraLoader();

        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        logofilter = new RBLogoFilter(new Rect(100, 100, 100 + logo.getWidth(), 100 + logo.getHeight()));// left, top, right, bottom
        logofilter.setBitmap(logo);

        // 实时美颜
        magicBeautyFilter = new MagicBeautyFilter(mActivity);
        magicBeautyFilter.setBeautyLevel(currentLevel);

        mMovieWriter = new GPUImageMovieWriter();
        initFilter = new GPUImageFilterGroup();
        initFilter.addFilter(logofilter);
        initFilter.addFilter(magicBeautyFilter);
        initFilter.addFilter(mMovieWriter);
        mGPUImage.setFilter(initFilter);

        rotationListener();

        initRecordLayout();

        final List<FilterTools.FilterBean> filterList = FilterTools.createFilterList();

        mLoopAdapter = new RLoopRecyclerView.LoopAdapter<FilterTools.FilterBean>(mActivity, filterList) {
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
                mLjAdapter.setSelectorPosition(position);
                switchFilterTo(mLoopAdapter.getAllDatas().get(position).createFilterForType(mActivity));
            }
        });

        mLoopRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    autoShow();
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    autoHide();
                }
                return false;
            }
        });

        //滤镜选择
        mViewHolder.click(R.id.lj_selector_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordLayout.expandLayout(true);
            }
        });

        //视频文件选择
        mViewHolder.click(R.id.video_selector_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //滤镜选择
        RRecyclerView ljRecyclerView = mViewHolder.v(R.id.lj_recycler_view);
        mLjAdapter = new RExBaseAdapter<String, FilterTools.FilterBean, String>(mActivity, filterList) {

            @Override
            protected int getItemLayoutId(int viewType) {
                return R.layout.item_lj_layout;
            }

            @Override
            protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, FilterTools.FilterBean bean) {
                super.onBindModelView(model, isSelector, holder, position, bean);
                holder.v(R.id.check_view).setVisibility(isSelector ? View.VISIBLE : View.INVISIBLE);
                holder.v(R.id.check_view).setBackgroundColor(SkinHelper.getTranColor(bean.bgColor, 0x80));
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, final int posInData, final FilterTools.FilterBean dataBean) {
                holder.imgV(R.id.image_view).setImageResource(dataBean.resId);
                holder.tv(R.id.text_view).setText(dataBean.name);
                holder.tv(R.id.text_view).setBackgroundColor(dataBean.bgColor);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSelectorPosition(posInData);
                        mLoopRecyclerView.scrollTo(posInData, false);
                        switchFilterTo(dataBean.createFilterForType(mActivity));
                    }
                });
            }
        }.setModel(RModelAdapter.MODEL_SINGLE);
        mLjAdapter.addSelectorPosition(0);//默认是原画
        ljRecyclerView.setAdapter(mLjAdapter);

        //美颜布局控制
        prettyLayout = mViewHolder.v(R.id.pretty_layout);
        RSeekBar seekBar = mViewHolder.v(R.id.seek_bar);
        seekBar.addOnProgressChangeListener(new RSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgress(int progress) {
                mViewHolder.tv(R.id.seek_tip_view).setText(progress + "");
                currentLevel = progress / 20;
                magicBeautyFilter.setBeautyLevel(currentLevel);
            }
        });
        seekBar.setCurProgress(50);
    }

    /**
     * 录制按钮初始化
     */
    private void initRecordLayout() {
        mRecordLayout.setListener(new ExpandRecordLayout.OnRecordListener() {
            @Override
            public void onRecording(int progress) {
                L.i("call: onRecording([progress])-> " + progress);
            }

            @Override
            public void onRecordStart() {
                startRecord();
            }

            @Override
            public void onRecordEnd(int progress) {
                if (progress < 3) {
                    T_.error(getString(R.string.record_time_short));
                    mRecordLayout.setEnabled(false);
                    stopRecord();
                    ViewCompat.animate(mRecordLayout)
                            .alpha(1).setDuration(100)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    mRecordLayout.setEnabled(true);
                                }
                            }).start();
                } else {
                    stopRecord();
                    if (BuildConfig.DEBUG) {
                        T_.info(progress + " s" + mRecordFile.getAbsolutePath());
                    }
                    final String parent = mRecordFile.getParent();

                    final String newName = UUID.randomUUID().toString() + OssHelper.createVideoFileName(progress);
                    final String thumbPath;
                    if (rotationRecord == 0) {
                        thumbPath = parent + File.separator + UUID.randomUUID().toString()
                                + OssHelper.createImageFileName(DefaultLevel.getHeight(), DefaultLevel.getWidth());

                    } else {
                        thumbPath = parent + File.separator + UUID.randomUUID().toString()
                                + OssHelper.createImageFileName(DefaultLevel.getHeight(), DefaultLevel.getWidth());
                    }

                    HnLoading.show(mParentILayout, false);
                    Observable.just("")
                            .delay(100, TimeUnit.MILLISECONDS)
                            .map(new Func1<String, String>() {
                                @Override
                                public String call(String s) {
                                    int index = 0;
                                    L.i("start--视频截图");
                                    while (!BitmapDecoder.extractThumbnail(mRecordFile.getAbsolutePath(), thumbPath) && index < 5) {
                                        L.i("start--视频截图:" + index);
                                        index++;
                                        try {
                                            Thread.sleep(100);
                                        } catch (Exception e) {
                                        }
                                    }
                                    L.i("视频截图完成:" + thumbPath);
                                    return "";
                                }
                            })
                            .delay(100, TimeUnit.MILLISECONDS)
                            .map(new Func1<String, String>() {
                                @Override
                                public String call(String s) {
                                    int index = 0;
                                    L.i("start--视频重命名");
                                    while (!FileUtils.rename(mRecordFile, newName) && index < 5) {
                                        L.i("start--视频重命名:" + index);
                                        index++;
                                        try {
                                            Thread.sleep(100);
                                        } catch (Exception e) {
                                        }
                                    }
                                    String videoPath = parent + File.separator + newName;
                                    L.i("视频重命名完成:" + mRecordFile.getAbsolutePath() + "->" + videoPath);
                                    return videoPath;
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    //startIView(new VideoPlayUIView(thumbPath, s));
                                    HnLoading.hide();
//                                    replaceIView(new PublishDynamicUIView(new PublishDynamicUIView.VideoStatusInfo(thumbPath, s), publishAction));
                                    publishAction.call(VideoRecordUIView.this, thumbPath, s);
                                }
                            });

                }
            }
        });

        if (BuildConfig.DEBUG) {
            mRecordLayout.setMaxTime(6);
        } else {
            mRecordLayout.setMaxTime(getResources().getInteger(R.integer.max_video_record_length));
        }
    }

    void autoHide() {
        ViewCompat
                .animate(mLoopRecyclerView)
                .alpha(0f)
                .setDuration(2000)
                .start();
    }

    void autoShow() {
        ViewCompat
                .animate(mLoopRecyclerView)
                .alpha(1f)
                .setDuration(100)
                .start();
    }

    void animShowPrettyLayout() {
        int height = prettyLayout.getMeasuredHeight();
        Runnable action = new Runnable() {
            @Override
            public void run() {
                prettyLayout.setVisibility(View.VISIBLE);
                int measuredHeight = prettyLayout.getMeasuredHeight();
                ViewCompat.setTranslationY(prettyLayout, -measuredHeight);
                ViewCompat
                        .animate(prettyLayout)
                        .translationY(0)
                        .setDuration(300)
                        .start();
            }
        };
        if (height == 0) {
            prettyLayout.setVisibility(View.INVISIBLE);
            prettyLayout.post(action);
        } else {
            action.run();
        }
    }

    void animHidePrettyLayout() {
        int height = prettyLayout.getMeasuredHeight();
        ViewCompat
                .animate(prettyLayout)
                .translationY(-height)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        prettyLayout.setVisibility(View.GONE);
                    }
                })
                .setDuration(300)
                .start();
    }

    @Override
    public boolean onBackPressed() {
        return !mIsRecording;
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        mCamera.onResume();
        autoHide();
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
        mRecordFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        mMovieWriter.startRecording(mRecordFile.getAbsolutePath(), DefaultLevel, rotationRecord);//do not change it without standrad
    }

    private void stopRecord() {
        mIsRecording = false;
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
        if (mCamera.isCameraFont()) {
            closeFlashLight((ImageView) getUITitleBarContainer().getRightView(1));
        }
    }

    /**
     * 切换滤镜
     */
    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;

            isSwitched = true;
            currentFilter = filter;
            //mFilter.destroy();//FIXME filter switch fuxking bug

            if (mCamera.isCameraFont()) {
                mGPUImage.setRotation(ROTATION_270, true, false, true);
            }


            filters = new GPUImageFilterGroup();
            filters.addFilter(logofilter);
            magicBeautyFilter.setBeautyLevel(currentLevel);
            filters.addFilter(magicBeautyFilter);
            filters.addFilter(mFilter);
            filters.addFilter(mMovieWriter);

            mGPUImage.setFilter(filters);

        }
    }

//    /**
//     * 开启美颜
//     */
//    public void setBeautyed(boolean enable) {
//        isBeautyed = enable;
//        updatefilter();
//    }
//
//    private void updatefilter() {
//
//        if (mCamera.isCameraFont()) {
//            if (!isSwitched) {
//                if (isBeautyed)
//                    mGPUImage.setRotation(ROTATION_90, true, false, needRotate);
//                else
//                    mGPUImage.setRotation(ROTATION_270, true, false, needRotate);
//            } else {
//                if (isBeautyed)
//                    mGPUImage.setRotation(ROTATION_270, true, false, needRotate);
//                else
//                    mGPUImage.setRotation(ROTATION_90, true, false, needRotate);
//            }
//        }
//
//        needRotate = true;
//
//        newfilters = new GPUImageFilterGroup();
//        newfilters.addFilter(logofilter);
//
//        if (isBeautyed) {
//            newfilters.addFilter(magicBeautyFilter);
//            isBeautyed = true;
//        }
//
//        newfilters.addFilter(currentFilter);
//        newfilters.addFilter(mMovieWriter);
//
//        mGPUImage.setFilter(newfilters);
//
//    }


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

    //TODO FIX MEIZU bug
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public class CameraLoader {

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
            mGLSurfaceView.requestLayout();
            if (isCameraFont() && isSwitched)
                needRotate = false;
            else
                needRotate = true;
            setUpCamera(mCurrentCameraId);
        }

        private void setUpCamera(final int id) {
            mCameraInstance = getCameraInstance(id);
            if (mCameraInstance == null)
                return;
            Camera.Parameters parameters = mCameraInstance.getParameters();
            // TODO adjust by getting supportedPreviewSizes and then choosing
            // the best one for screen size (best fill screen)


            Camera.Size optimalSize = getOptimalPreviewSize(mCameraInstance.getParameters().getSupportedPreviewSizes(), 720, 1280);
            Log.d("### ActivtyMain", "w: " + optimalSize.width + " h: " + optimalSize.height);
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);

            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCameraInstance.setParameters(parameters);

            int orientation = mCameraHelper.getCameraDisplayOrientation(
                    mActivity, mCurrentCameraId);

            boolean flipHorizontal = isCameraFont();

            if (flipHorizontal)//FIXME switch logo rorate bug
                logofilter.updateIcon(true);
            else
                logofilter.updateIcon(false);

            mGPUImage.setUpCamera(mCameraInstance, orientation, flipHorizontal, false, needRotate);
            mGPUImage.setScaleType(GPUImage.ScaleType.CENTER_CROP);//FIXME some device swap camera size get smaller

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

        /**
         * 是否是前置
         */
        public boolean isCameraFont() {
            CameraHelper.CameraInfo2 cameraInfo = new CameraHelper.CameraInfo2();
            mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
            return cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
        }

        private void releaseCamera() {
            if (mCameraInstance != null) {
                mCameraInstance.setPreviewCallback(null);
                mCameraInstance.stopPreview();
                mCameraInstance.release();
                mCameraInstance = null;
            }
        }

        public boolean toggleFlashLight() {
            try {
                Camera.Parameters parameters = mCameraInstance.getParameters();
                List<String> flashModes = parameters.getSupportedFlashModes();
                String flashMode = parameters.getFlashMode();
                if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
                    if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCameraInstance.setParameters(parameters);
                        return true;
                    }
                } else if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
                    if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCameraInstance.setParameters(parameters);
                        return true;
                    }
                }
            } catch (Exception e) {
                return false;
            }
            return false;
        }
    }
}
