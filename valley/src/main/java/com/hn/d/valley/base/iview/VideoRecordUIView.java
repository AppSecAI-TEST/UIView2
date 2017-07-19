package com.hn.d.valley.base.iview;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.angcyo.github.utilcode.utils.FileUtils;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.base.UIViewConfig;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RLoopRecyclerView;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.ScreenUtil;
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
import com.lzy.imagepicker.ImageDataSource;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.ImagePickerHelper;
import com.lzy.imagepicker.bean.ImageItem;
import com.m3b.rbrecoderlib.GPUImage;
import com.m3b.rbrecoderlib.GPUImageFilter;
import com.m3b.rbrecoderlib.GPUImageFilterGroup;
import com.m3b.rbrecoderlib.GPUImageMovieWriter;
import com.m3b.rbrecoderlib.Rotation;
import com.m3b.rbrecoderlib.filters.MagicBeautyFilter;
import com.m3b.rbrecoderlib.filters.RBLogoFilter;
import com.m3b.rbrecoderlib.utils.CameraHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    GPUImageMovieWriter.Level DefaultLevel = GPUImageMovieWriter.Level.MEDIUM;
    long lastSwitchTime = 0l;
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

//    public static Observable
    /**
     * 是否包含logo
     */
    private boolean hasLogo = true;

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

    /**
     * 保证执行的正确性
     */
    public static void handle(HandleCallback callback) {
        int index = 0;
        L.i("start--缩略图重命名");
        while (!callback.onHandle(index) && index < 5) {
            L.i("start--缩略图重命名:" + index);
            index++;
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
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
                .setFloating(true)
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
                        long timeMillis = System.currentTimeMillis();
                        if (timeMillis - lastSwitchTime > 1 * 1000) {
                            switchCamera();
                        }
                        lastSwitchTime = timeMillis;
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
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
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

        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.shuiyin_1);
        logofilter = new RBLogoFilter();// left, top, right, bottom
        logofilter.setBitmap(logo);
        logofilter.setPostion(RBLogoFilter.Gravity.RIGHT | RBLogoFilter.Gravity.TOP, 40, 50);
        logofilter.setBitmap(logo);

        // 实时美颜
        magicBeautyFilter = new MagicBeautyFilter(mActivity);
        magicBeautyFilter.setBeautyLevel(currentLevel);

        mMovieWriter = new GPUImageMovieWriter();
        initFilter = new GPUImageFilterGroup();
        if (hasLogo) {
            initFilter.addFilter(logofilter);
        }
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
                ImagePickerHelper.startImagePicker(mActivity, false, false, 0, ImageDataSource.VIDEO);
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
                mViewHolder.tv(R.id.seek_tip_view).setText(progress + "%");
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
                    fixVideoPath(mRecordFile.getAbsolutePath(), null, progress, DefaultLevel.getWidth(), DefaultLevel.getHeight(), rotationRecord);
                }
            }
        });

        if (BuildConfig.DEBUG) {
            mRecordLayout.setMaxTime(6);
        } else {
            mRecordLayout.setMaxTime(getMaxRecordLength());
        }
    }

    private int getMaxRecordLength() {
        return getResources().getInteger(R.integer.max_video_record_length);
    }

    /**
     * 为视频添加时间命名, 为缩略图添加宽高
     */
    private void fixVideoPath(String videoPath, final File thumbFile, int videoTime /*秒*/, int videoWidth, int videoHeight, int rotationRecord) {
        final VideoBean videoBean = new VideoBean();
        videoBean.videoPath = videoPath;
        final File videoFile = new File(videoPath);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {

            if (videoTime <= 0 || videoWidth <= 0 || videoHeight <= 0 || rotationRecord < 0) {
                retriever.setDataSource(mActivity, Uri.fromFile(videoFile));
            }

            if (videoTime <= 0) {
                videoTime = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
            }
            if (videoWidth <= 0) {
                videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            }
            if (videoHeight <= 0) {
                videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            }
            if (rotationRecord < 0) {
                rotationRecord = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            }
            L.i("videoTime:" + videoTime + " videoWidth:" + videoWidth + " videoHeight:" + videoHeight + " rotationRecord:" + rotationRecord);
        } finally {
            try {
                retriever.release();
            } catch (Exception ex) {
                // Ignore failures while cleaning up.
            }
        }

        final String newName = UUID.randomUUID().toString() + OssHelper.createVideoFileName(videoTime);
        final String thumbPath, thumbName;

        if (rotationRecord == 0 || rotationRecord == 180) {
            thumbName = UUID.randomUUID().toString()
                    + OssHelper.createImageFileName(videoWidth, videoHeight);
        } else {
            thumbName = UUID.randomUUID().toString()
                    + OssHelper.createImageFileName(videoHeight, videoWidth);
        }

        final String parent = videoFile.getParent();
        thumbPath = parent + File.separator + thumbName;
        videoBean.thumbPath = thumbPath;

        HnLoading.show(mParentILayout, false);
        Observable.just("")
                .delay(100, TimeUnit.MILLISECONDS)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        if (thumbFile == null || !thumbFile.exists()) {
                            L.i("start--视频截图");
                            handle(new HandleCallback() {
                                @Override
                                public boolean onHandle(int index) {
                                    L.i("start--视频截图->" + index);
                                    return BitmapDecoder.extractThumbnail(videoFile.getAbsolutePath(), thumbPath);
                                }
                            });
                            L.i("视频截图完成:" + thumbPath);
                        } else {
                            L.i("start--缩略图重命名");
                            handle(new HandleCallback() {
                                @Override
                                public boolean onHandle(int index) {
                                    L.i("start--缩略图重命名:" + index);
                                    return FileUtils.rename(thumbFile, thumbName);
                                }
                            });

                            videoBean.thumbPath = thumbFile.getParent() + File.separator + thumbName;
                            L.i("缩略图重命名完成:" + videoBean.thumbPath);
                        }
                        return "";
                    }
                })
                .delay(100, TimeUnit.MILLISECONDS)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        L.i("start--视频重命名");
                        handle(new HandleCallback() {
                            @Override
                            public boolean onHandle(int index) {
                                L.i("start--视频重命名:" + index);
                                return FileUtils.rename(videoFile, newName);
                            }
                        });

                        videoBean.videoPath = parent + File.separator + newName;
                        ImagePicker.galleryAddPic(mActivity, new File(videoBean.videoPath));

                        L.i("视频重命名完成: ->" + videoBean.videoPath);
                        return videoBean.videoPath;
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
                        onPushAction(videoBean.thumbPath, videoBean.videoPath);
                    }
                });
    }

    private void onPushAction(String thumbPath, String videoPath) {
        publishAction.call(VideoRecordUIView.this, thumbPath, videoPath);
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
        if (mRecordLayout.getState() != ExpandRecordLayout.STATE_CLOSE) {
            mRecordLayout.expandLayout(false);
            return false;
        }
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

        RVideoEdit.INSTANCE.release();
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        VideoEditUIView.Companion.initShuiYin(getResources());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<ImageItem> items = ImagePickerHelper.getItems(mActivity, requestCode, resultCode, data);
        if (items.size() > 0) {
            final ImageItem item = items.get(0);
            if (item.loadType == ImageDataSource.VIDEO) {
                //选择视频后返回
                VideoPreviewUIView videoPreviewUIView = new VideoPreviewUIView(new UIViewConfig() {
                    @Override
                    public void initOnShowContentLayout(final UIIViewImpl uiview, RBaseViewHolder viewHolder) {
                        super.initOnShowContentLayout(uiview, viewHolder);
                        View videoView = viewHolder.v(R.id.videoView);
                        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                        layoutParams.width = ScreenUtil.screenWidth;
                        layoutParams.height = (int) (item.height * (ScreenUtil.screenWidth * 1.f / item.width));
                        videoView.setLayoutParams(layoutParams);

                        viewHolder.v(R.id.root_layout).setBackgroundColor(SkinHelper.getSkin().getThemeTranColor(0x40));
                        viewHolder.tv(R.id.ok_view).setTextColor(SkinHelper.getSkin().getThemeSubColor());

                        if (item.videoDuration / 1000 > getMaxRecordLength()) {
                            viewHolder.tv(R.id.tip_view).setText("朋友圈只能分享不大于30秒的视频.");
                            viewHolder.tv(R.id.ok_view).setText("编辑");

                            viewHolder.click(R.id.ok_view, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    uiview.replaceIView(new VideoEditUIView(item, new Action1<EditVideoInfo>() {
                                        @Override
                                        public void call(EditVideoInfo editVideoInfo) {
                                            fixVideoPath(editVideoInfo.getVideoPath(), null, (int) editVideoInfo.getVideoDuration() / 1000, 0, 0, 0);
                                        }
                                    }));
                                }
                            });

                        } else if (item.videoDuration / 1000 < 3) {
                            viewHolder.tv(R.id.tip_view).setText("视频时长需要大于3秒.");
                            viewHolder.tv(R.id.ok_view).setEnabled(false);
                            viewHolder.tv(R.id.ok_view).setText("完成");
                        } else if (item.size / 1024f > 50 * 1024) {
                            viewHolder.tv(R.id.tip_view).setText("视频过大,请先编辑.");
                            viewHolder.tv(R.id.ok_view).setText("编辑");

                            viewHolder.click(R.id.ok_view, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    uiview.replaceIView(new VideoEditUIView(item, new Action1<EditVideoInfo>() {
                                        @Override
                                        public void call(EditVideoInfo editVideoInfo) {
                                            fixVideoPath(editVideoInfo.getVideoPath(), null, (int) editVideoInfo.getVideoDuration() / 1000, 0, 0, 0);
                                        }
                                    }));
                                }
                            });
                        } else {
                            viewHolder.tv(R.id.tip_view).setText("");
                            viewHolder.tv(R.id.ok_view).setText("完成");

                            viewHolder.click(R.id.ok_view, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finishIView(uiview, new UIParam(false).setUnloadRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            fixVideoPath(item.path, new File(item.videoThumbPath), (int) (item.videoDuration / 1000), 0, 0, 0);
                                        }
                                    }));
                                }
                            });
                        }
                    }
                });
                videoPreviewUIView.setVideoPath(item.path);
                startIView(videoPreviewUIView);

//                if (item.videoDuration / 1000 > getMaxRecordLength()) {
//                    //视频时长大于限制, 剪切并压缩
//                    startIView(new VideoEditUIView(item, new Action1<EditVideoInfo>() {
//                        @Override
//                        public void call(EditVideoInfo editVideoInfo) {
//                            fixVideoPath(editVideoInfo.getVideoPath(), null, (int) editVideoInfo.getVideoDuration() / 1000, 0, 0, 0);
//                        }
//                    }));
//                } else if (item.size / 1024f > 50 * 1024) {
//                    //视频大小大于限制, 压缩
//                    final UIProgressDialog progressDialog = UIProgressDialog.build();
//                    progressDialog.setCanCancel(false);
//                    progressDialog.setDimBehind(false);
//                    progressDialog.setTipText(getString(R.string.handing_tip));
//
//                    final String outPath = Root.getAppExternalFolder("videos") + File.separator + Root.createFileName(".mp4");
//                    RVideoEdit.INSTANCE.compressVideo(mActivity,
//                            item.path,
//                            outPath,
//                            VideoEditUIView.Companion.getShuiyinPath(),
//                            item.videoDuration / 1000,
//                            new OnExecCommandListener() {
//                                @Override
//                                public void onExecProgress(int progress) {
//                                    progressDialog.setProgress(progress);
//                                }
//
//                                @Override
//                                public void onExecSuccess(String message) {
//                                    progressDialog.finishIView();
//                                    fixVideoPath(outPath, new File(item.videoThumbPath), (int) (item.videoDuration / 1000), 0, 0, 0);
//                                }
//
//                                @Override
//                                public void onExecStart() {
//                                    progressDialog.showDialog(mParentILayout);
//                                }
//
//                                @Override
//                                public void onExecFail(String reason) {
//                                    progressDialog.finishIView();
//                                }
//                            });
//                } else {
//                    fixVideoPath(item.path, new File(item.videoThumbPath), (int) (item.videoDuration / 1000), 0, 0, 0);
//                }
            }

//            L.i("视频大小:" + item.size / 1024f + "Kb'");
//            if (item.size / 1024f > 50 * 1024) {
//                T_.ok("视频过大.");
//            } else {
//            }
        }
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
                mGPUImage.setRotation(Rotation.ROTATION_90, true, false, true);
            }

            filters = new GPUImageFilterGroup();
            if (hasLogo) {
                filters.addFilter(logofilter);
            }
            filters.addFilter(mFilter);
            magicBeautyFilter.setBeautyLevel(currentLevel);
            filters.addFilter(magicBeautyFilter);
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

    public interface HandleCallback {
        boolean onHandle(int index);
    }

    private static class VideoBean {
        public String thumbPath;
        public String videoPath;
    }

    public class CameraLoader {

        private int mCurrentCameraId = 0;
        private Camera mCameraInstance;

        public void onResume() {
            mActivity.checkPermissions(new String[]{Manifest.permission.CAMERA}, new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    if (aBoolean) {
                        setUpCamera(mCurrentCameraId);
                    } else {
                        finishIView();
                    }
                }
            });
        }

        public void onPause() {
            releaseCamera();
        }

        public void switchCamera() {
            releaseCamera();
            mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
            mGLSurfaceView.requestLayout();
            if (isCameraFont() && hasLogo)
                needRotate = true;
            //else
            //  needRotate = true;
            mActivity.checkPermissions(new String[]{Manifest.permission.CAMERA}, new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    if (aBoolean) {
                        setUpCamera(mCurrentCameraId);
                    } else {
                        finishIView();
                    }
                }
            });
        }


        private void setUpCamera(final int id) {
            mCameraInstance = getCameraInstance(id);
            if (mCameraInstance == null)
                return;
            Camera.Parameters parameters = mCameraInstance.getParameters();
            // TODO adjust by getting supportedPreviewSizes and then choosing
            // the best one for screen size (best fill screen)

            Camera.Size optimalSize = getOptimalPreviewSize(mCameraInstance.getParameters().getSupportedPreviewSizes(),
                    DefaultLevel.getWidth(), DefaultLevel.getHeight());

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
