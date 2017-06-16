package com.hn.d.valley.main.me.setting;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.glide.GlideCircleTransform;
import com.angcyo.library.utils.Anim;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.github.pickerview.DateDialog;
import com.angcyo.uiview.github.pickerview.view.WheelTime;
import com.angcyo.uiview.github.utilcode.utils.FileUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RDragRecyclerView;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.utils.file.FileUtil;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.angcyo.uiview.widget.RTextView;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.bumptech.glide.Glide;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.adapter.HnAddImageAdapter;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.dialog.CityDialog;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.oss.OssControl;
import com.hn.d.valley.base.oss.OssControl2;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.ProvinceBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserControl;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.message.audio.AudioRecordPlayable;
import com.hn.d.valley.main.message.audio.BaseAudioControl;
import com.hn.d.valley.main.message.audio.PathAudioControl;
import com.hn.d.valley.main.message.audio.Playable;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.skin.SkinUtils;
import com.hn.d.valley.sub.other.InputUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.utils.Image;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnLoading;
import com.lzy.imagepicker.ImagePickerHelper;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：编辑个人资料
 * 创建人员：Robi
 * 创建时间：2017/02/17 17:42
 * 修改人员：Robi
 * 修改时间：2017/02/17 17:42
 * 修改备注：
 * Version: 1.0.0
 */
public class EditInfoUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> implements IAudioRecordCallback {

    // 语音
    protected AudioRecorder audioMessageHelper;
    OldParams mOldParams;
    boolean canBack = false;
    FrameLayout over_layout;
    LinearLayout layoutPlayAudio;
    Chronometer mTimer;
    LinearLayout timpContainer;
    private ItemInfoLayout mUserIcoInfoLayout;
    ImageView iv_play;

    private HnAddImageAdapter mHnAddImageAdapter;
    private List<Luban.ImageItem> mOldItems = new ArrayList<>();//原先的照片地址
    private List<Luban.ImageItem> mPhones = new ArrayList<>();//原先的照片地址用来adapter
    private SparseArray<String> mUrls = new SparseArray<>();//所有照片墙的图片网络地址
    private OssControl2 mOssControl;
    private Action0 mOnFinishAction;
    private Action0 mOnAudioRecordSuccess;
    private Action1 mOnAudioRecordStatus;
    //选择用户头像图片返回
    private boolean isSetUserIco = false;
    private String mUserSetIco;
    private PathAudioControl mPathAudioControl;
    private boolean touched;
    private boolean started;
    private boolean cancelled;
    private boolean audioPlaying;


    private View.OnTouchListener audioRecordListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touched = true;
                initAudioRecord();
                onStartAudioRecord();
                over_layout.setVisibility(View.VISIBLE);
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                    || event.getAction() == MotionEvent.ACTION_UP) {
                touched = false;
                onEndAudioRecord(isCancelled(layoutPlayAudio, event));
                over_layout.setVisibility(View.GONE);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                // 不允许recyclerview 处理滑动事件
                mRecyclerView.requestDisallowInterceptTouchEvent(true);
                touched = false;
                boolean isCancel = isCancelled(layoutPlayAudio, event);
                if (isCancel) {
                    timpContainer.setBackgroundResource(R.drawable.nim_cancel_record_red_bg);
                } else {
                    timpContainer.setBackgroundResource(0);
                }
                cancelAudioRecord(isCancel);
            }

            return true;
        }
    };
    private AudioRecordPlayable mAudioRecordPlayable;

    public EditInfoUIView(List<String> urls, Action0 onFinishAction) {
        for (String url : urls) {
            mOldItems.add(new Luban.ImageItem(url));
        }
        mPhones.addAll(mOldItems);
        mOnFinishAction = onFinishAction;

        //保存初始值
        UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
        mOldParams = new OldParams();
        mOldParams.mPhotos.addAll(urls);
        mOldParams.mUserIco = userInfoBean.getAvatar();
        mOldParams.mUserName = userInfoBean.getUsername();
        mOldParams.mUserSex = userInfoBean.getSex();
        mOldParams.mBirthday = userInfoBean.getBirthday();
        mOldParams.mArea = userInfoBean.getProvince_id() + "_" + userInfoBean.getCity_id();
        mOldParams.mUserVoiceUrl = userInfoBean.getVoice_introduce();
        mOldParams.mSignature = userInfoBean.getSignature();
        mOldParams.pName = userInfoBean.getProvince_name();
        mOldParams.cName = userInfoBean.getCity_name();
    }

    // 滑动到指定view区域取消录音判断
    private static boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        RectF rectf = new RectF(location[0], location[1], location[0] + view.getWidth()
                , location[1] + view.getHeight());
        if (rectf.contains(event.getRawX(), event.getRawY())) {
            return true;
        }
        return false;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        over_layout = v(R.id.over_layout);
        layoutPlayAudio = v(R.id.layoutPlayAudio);
        mTimer = v(R.id.timer);
        timpContainer = v(R.id.timer_tip_container);

        updateRecordImageResource((ImageView) v(R.id.audio_tip_view));
    }

    @Override
    protected void inflateRecyclerRootLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        super.inflateRecyclerRootLayout(baseContentLayout, inflater);
    }

    @Override
    protected int getTitleResource() {
        return R.string.edit_into_title;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().addRightItem(TitleBarPattern.TitleBarItem
                .build(mActivity.getResources().getString(R.string.save), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSaveInfo();
                    }
                }));
    }

    /**
     * 保存用户信息
     */
    private void onSaveInfo() {
        startUpload();
    }

    String getAllPhotos() {
        if (mUrls == null || mUrls.size() == 0) {
            return "empty";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mUrls.size(); i++) {
            result.append(mUrls.get(i));
            result.append(",");
        }
        return RUtils.safe(result);
    }

    /**
     * 3:保存信息...end
     */
    private void startSave() {
        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
        final String allPhotos = getAllPhotos();
        if ("empty".equalsIgnoreCase(allPhotos)) {
            RRealm.exe(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    userInfoBean.setPhotos("");
                }
            });
        } else {
            RRealm.exe(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    userInfoBean.setPhotos(allPhotos);
                }
            });
        }

        String signature = userInfoBean.getSignature();
        if (TextUtils.isEmpty(signature)) {
            signature = "empty";
        }

        add(RRetrofit.create(UserService.class)
                .editInfo(Param.buildMap("photos:" + allPhotos,
                        "avatar:" + mUserSetIco,
                        "username:" + userInfoBean.getUsername(),
                        "sex:" + userInfoBean.getSex(),
                        "signature:" + signature,
                        "province_id:" + userInfoBean.getProvince_id(),
                        "city_id:" + userInfoBean.getCity_id(),
                        "voice:" + userInfoBean.getVoice_introduce(),
                        "birthday:" + userInfoBean.getBirthday()
                ))
                .compose(Rx.transformer(UserInfoBean.class))
                .subscribe(new BaseSingleSubscriber<UserInfoBean>() {
                    @Override
                    public void onSucceed(UserInfoBean bean) {
                        super.onSucceed(bean);
                        UserCache.instance().setUserInfoBean(bean);
                        T_.ok(mActivity.getString(R.string.save_ok_tip));
                        HnLoading.hide();
                        finishIView();
                        if (mOnFinishAction != null) {
                            mOnFinishAction.call();
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        HnLoading.hide();
                    }
                }));
    }

    private void onNotSave() {
        RRealm.exe(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
                UserCache.setUserAvatar(mOldParams.mUserIco);
                userInfoBean.setAvatar(mOldParams.mUserIco);
                userInfoBean.setUsername(mOldParams.mUserName);
                userInfoBean.setSex(mOldParams.mUserSex);
                userInfoBean.setBirthday(mOldParams.mBirthday);
                userInfoBean.setSignature(mOldParams.mSignature);
                userInfoBean.setProvince_id(mOldParams.mArea.split("_")[0]);
                userInfoBean.setCity_id(mOldParams.mArea.split("_")[1]);
                userInfoBean.setProvince_id(mOldParams.mArea.split("_")[0]);
                userInfoBean.setCity_id(mOldParams.mArea.split("_")[1]);
                userInfoBean.setProvince_name(mOldParams.pName);
                userInfoBean.setCity_name(mOldParams.cName);
            }
        });
    }

    /**
     * 2:检查是否需要上传用户头像
     */
    private void checkUserIco() {
        if (TextUtils.isEmpty(mUserSetIco)) {
            mUserSetIco = UserCache.getUserAvatar();
            startSave();
        } else {
            List<String> files = new ArrayList<>();
            File file = new File(mUserSetIco);
            if (file.exists()) {
                files.add(mUserSetIco);
                new OssControl(new OssControl.OnUploadListener() {
                    @Override
                    public void onUploadStart() {

                    }

                    @Override
                    public void onUploadSucceed(List<String> list) {
                        mUserSetIco = list.get(0);
                        RRealm.exe(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                UserCache.setUserAvatar(mUserSetIco);
                                UserCache.instance().getUserInfoBean().setAvatar(mUserSetIco);
                            }
                        });
                        startSave();
                    }

                    @Override
                    public void onUploadFailed(int code, String msg) {
                        T_.show(msg);
                        HnLoading.hide();
                    }
                }).uploadCircleImg(files, true);
            } else if (mUserSetIco.startsWith("http")) {
                startSave();
                //startSave();
            } else {
                startSave();
            }
        }
    }

    /**
     * 1:先上传图片
     */
    private void startUpload() {
        mUrls.clear();
        final List<Luban.ImageItem> allDatas = mHnAddImageAdapter.getAllDatas();
        final List<String> needUploadFiles = new ArrayList<>();

        HnLoading.show(mParentILayout, false);

        if (allDatas.isEmpty()) {
//            checkUserIco();
            checkAudioRecord();
            return;
        }

        for (int i = 0; i < allDatas.size(); i++) {
            Luban.ImageItem imageItem = allDatas.get(i);
            if (TextUtils.isEmpty(imageItem.url)) {
                needUploadFiles.add(imageItem.thumbPath);
            } else {
                mUrls.put(i, imageItem.url);
            }
        }

        mOssControl = new OssControl2(new OssControl2.OnUploadListener() {
            @Override
            public void onUploadStart() {

            }

            @Override
            public void onUploadSucceed(List<String> list) {
                //得到上传成功的文件在图片墙中的位置
                for (String upload : list) {
                    String[] split = upload.split("\\|");
                    String thumbPath = split[0];
                    for (int i = 0; i < allDatas.size(); i++) {
                        Luban.ImageItem imageItem = allDatas.get(i);
                        if (TextUtils.equals(imageItem.thumbPath, thumbPath)) {
                            mUrls.put(i, split[1]);
                        }
                    }
                }
//                checkUserIco();
                checkAudioRecord();
            }

            @Override
            public void onUploadFailed(int code, String msg) {
                T_.show(msg);
                HnLoading.hide();
            }
        });
        mOssControl.uploadCircleImg(needUploadFiles, true);
    }

    private void checkAudioRecord() {
        if (mAudioRecordPlayable == null) {
            checkUserIco();
            return;
        }
        String audioPath = mAudioRecordPlayable.getPath();
        File audioFile = new File(audioPath);
        if (!audioFile.exists()) {
            checkUserIco();
            return;
        }
        add(OssHelper.uploadAudio(audioPath)
                        .subscribe(new BaseSingleSubscriber<String>() {
                            @Override
                            public void onSucceed(String s) {
                                L.i(s + ":::  audio upload success");
                                final String audioUrl = OssHelper.getAudioUrl(s);
                                RRealm.exe(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        // TODO: 2017/3/17 更改上传URL显示格式
//                                String en = FileUtil.getExtensionName(audioUrl);
//                                StringBuilder sb = new StringBuilder(FileUtil.getFileNameNoEx(audioUrl));
//                                String audioUrlAndTime = audioUrl + "--" + mAudioRecordPlayable.getDuration() / 1000;

//                                sb.append("_t_")
//                                        .append(mAudioRecordPlayable.getDuration() / 1000)
//                                        .append(".")
//                                        .append(en);
                                        UserCache.instance().getUserInfoBean().setVoice_introduce(audioUrl);
                                    }
                                });
                                checkUserIco();
                            }

                            @Override
                            public void onError(int code, String msg) {
                                T_.show(msg);
                                HnLoading.hide();
                            }
                        })
        );
    }

    @Override
    public boolean canTryCaptureView() {
        //如果用户信息更改了, 不允许滑动返回
        return !mOldParams.isChanged(UserCache.instance().getUserInfoBean());
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_drag_recycler_view;
        }

        if (viewType == 1) {
            return R.layout.item_profile_recordvoice;
        }

        return R.layout.item_info_layout;
    }

    @Override
    public void onViewCreate(View rootView) {
        super.onViewCreate(rootView);
        ImagePickerHelper.clearAllSelectedImages();
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        mPathAudioControl.stopAudio();
    }

    @Override
    public boolean canSwipeBackPressed() {
        if (canBack) {
            return true;
        }
        return changeBack();
    }

    @Override
    public boolean onBackPressed() {
        return changeBack();
    }

    boolean changeBack() {
        if (mOldParams.isChanged(UserCache.instance().getUserInfoBean())) {
            UIDialog.build()
                    .setDialogContent(mActivity.getString(R.string.edit_info_quit_tip))
                    .setOkText(mActivity.getString(R.string.save))
                    .setCancelText(mActivity.getString(R.string.not_save))
                    .setCancelListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNotSave();
                            canBack = true;
                            finishIView(EditInfoUIView.this, new UIParam(true, true, false));
                        }
                    })
                    .setOkListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSaveInfo();
                        }
                    })
                    .showDialog(mParentILayout);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选中的图片列表
        final ArrayList<String> images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data);
        if (isSetUserIco) {
            if (!images.isEmpty()) {
                mUserSetIco = images.get(0);
                Glide.with(mActivity)
                        .load(Uri.fromFile(new File(mUserSetIco)))
                        .override(mUserIcoInfoLayout.getMeasuredHeight(), mUserIcoInfoLayout.getMeasuredHeight())
                        .transform(new GlideCircleTransform(mActivity))
                        .into(mUserIcoInfoLayout.getImageView());
            }
        } else {
            final List<Luban.ImageItem> oldDatas = mHnAddImageAdapter == null ? null : mHnAddImageAdapter.getAllDatas();

            //请求压缩图片,排除掉已经压缩的图片
            Observable<ArrayList<Luban.ImageItem>> observable = Image.onActivityResult(mActivity, requestCode, resultCode, data, oldDatas);
            if (observable != null) {
                observable.subscribe(new BaseSingleSubscriber<ArrayList<Luban.ImageItem>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        HnLoading.show(mILayout, false);
                    }

                    @Override
                    public void onSucceed(ArrayList<Luban.ImageItem> strings) {
                        if (BuildConfig.DEBUG) {
                            Luban.logFileItems(mActivity, strings);
                        }
                        //新增的图片,和原有的图片匹配, 进行组合, 产生新的图片集合
                        List<Luban.ImageItem> imageItemList = new ArrayList<>();
                        for (int i = 0; i < images.size(); i++) {
                            String needPath = images.get(i);
                            Luban.ImageItem inListItem = Image.isInList(strings, needPath);
                            if (inListItem != null) {
                                imageItemList.add(inListItem);
                            } else {
                                inListItem = Image.isInList(oldDatas, needPath);
                                imageItemList.add(inListItem);
                            }
                        }

                        if (mHnAddImageAdapter != null) {
                            List<Luban.ImageItem> oldItems = getOldItems();
                            oldItems.addAll(imageItemList);
                            mHnAddImageAdapter.resetData(oldItems);
                        }
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        HnLoading.hide();
                    }
                });
            }
        }
    }

    /**
     * 初始化AudioRecord
     */
    private void initAudioRecord() {
        if (audioMessageHelper == null) {
            audioMessageHelper = new AudioRecorder(mActivity, RecordType.AAC, AudioRecorder.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND, this);
        }
    }

    /**
     * 取消语音录制
     *
     * @param cancel
     */
    private void cancelAudioRecord(boolean cancel) {
        // reject
        if (!started) {
            return;
        }
        // no change
        if (cancelled == cancel) {
            return;
        }

        cancelled = cancel;
    }

    /**
     * 开始语音录制
     */
    private void onStartAudioRecord() {
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

       /* started = */
        audioMessageHelper.startRecord();
        cancelled = false;
//        if (!started) {
//            T_.show(mActivity.getString(R.string.recording_init_failed));
//            return;
//        }

        if (!touched) {
            return;
        }

        if (mOnAudioRecordStatus != null) {
            mOnAudioRecordStatus.call(true);
        }

        playAudioRecordAnim();
    }

    /**
     * 开始语音录制动画
     */
    private void playAudioRecordAnim() {
        mTimer.setBase(SystemClock.elapsedRealtime());
        mTimer.start();
    }

    /**
     * 结束语音录制动画
     */
    private void stopAudioRecordAnim() {
        mTimer.stop();
        mTimer.setBase(SystemClock.elapsedRealtime());
    }

    @Override
    protected void inflateOverlayLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.layout_audio_record);

        mPathAudioControl = PathAudioControl.getInstance(mActivity);
        mPathAudioControl.setEarPhoneModeEnable(false);
    }

    /**
     * 结束语音录制
     *
     * @param cancel
     */
    private void onEndAudioRecord(boolean cancel) {
        mActivity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        audioMessageHelper.completeRecord(cancel);
        if (mOnAudioRecordStatus != null) {
            mOnAudioRecordStatus.call(false);
        }
        stopAudioRecordAnim();
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        final int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
        final int left = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);

        final int size = 2 * line;

        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();

        //照片墙...
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, final int posInData, ViewItemInfo dataBean) {
                bindPhoneWallItem(holder, size);
            }
        }));

        //语音录制
        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindAudioIntroduce(holder, userInfoBean);
            }
        }));

        //头像
        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                mUserIcoInfoLayout = holder.v(R.id.item_info_layout);
                mUserIcoInfoLayout.setItemText("头像");
                mUserIcoInfoLayout.setRightDrawableRes(-1);
                mUserIcoInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isSetUserIco = true;
                        ImagePickerHelper.startImagePicker(mActivity, true, true, true, false, 1);
                    }
                });
                mUserIcoInfoLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(mActivity)
                                .load(UserCache.getUserAvatar())
                                .override(mUserIcoInfoLayout.getMeasuredHeight(), mUserIcoInfoLayout.getMeasuredHeight())
                                .transform(new GlideCircleTransform(mActivity))
                                .into(mUserIcoInfoLayout.getImageView());
                    }
                });
            }
        }));

        //昵称
        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindNameItem(holder, userInfoBean);
            }
        }));
        //ID
//        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
//                infoLayout.setItemText("ID");
//                infoLayout.setRightDrawableRes(-1);
//                infoLayout.setItemDarkText(UserCache.getUserAccount());
//            }
//        }));

        //我的二维码
//        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
//                infoLayout.setItemText(mActivity.getResources().getString(R.string.my_qr_code));
//                infoLayout.setDarkDrawableRes(R.drawable.qr_code);
//                infoLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startIView(new MyQrCodeUIView());
//                    }
//                });
//            }
//        }));

        //性别
        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                final ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getResources().getString(R.string.sex));
                infoLayout.setItemDarkText(UserControl.getSex(mActivity, userInfoBean.getSex()));
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(UIItemDialog.build()
                                .addItem(mActivity.getString(R.string.man), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updateSex(infoLayout, userInfoBean, "1");
                                    }
                                })
                                .addItem(mActivity.getString(R.string.women), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updateSex(infoLayout, userInfoBean, "2");
                                    }
                                }));
                    }
                });
            }
        }));
        //出生日期
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindBirthdayItem(holder, userInfoBean);
            }
        }));
        //地区
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindAreaItem(holder, userInfoBean);
            }
        }));
        //语音介绍
//        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
//                infoLayout.setItemText(mActivity.getString(R.string.audio_introduce_desc));
//                infoLayout.setItemDarkText(mActivity.getString(R.string.click_add_desc));
//            }
//        }));
        //个性签名
        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {

            @Override
            public void setItemOffsets(Rect rect) {
                super.setItemOffsets(rect);
                rect.bottom = left;
            }

            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindSignatureItem(holder, userInfoBean);
            }
        }));
    }

    private void bindAudioIntroduce(RBaseViewHolder holder, UserInfoBean userInfoBean) {
        final View record_layout = holder.v(R.id.record_layout);
        iv_play = holder.v(R.id.iv_play_audio);
        final RTextView tv_record_second = holder.v(R.id.tv_record_second);
        final ImageView iv_audio_record = holder.imgV(R.id.iv_audio_record);
        final TextView tv_audio_record = holder.tv(R.id.tv_audio_record);

        updateRecordImageResource(iv_audio_record);
        updateRecordPlayEndResource(iv_play);

        if (!TextUtils.isEmpty(userInfoBean.getVoice_introduce())) {

            String audioUrlAndTime = userInfoBean.getVoice_introduce();

            //// TODO: 2017/3/17 兼容上版本ios录音格式 --

            String[] voiceIntroduces;
            voiceIntroduces = audioUrlAndTime.split("--");
            if (voiceIntroduces.length == 2) {
                iv_play.setVisibility(View.VISIBLE);
                tv_record_second.setVisibility(View.VISIBLE);
                tv_record_second.setText(String.format("%d″", Long.parseLong(voiceIntroduces[1])));
                mAudioRecordPlayable = new AudioRecordPlayable(voiceIntroduces[0], Long.parseLong(voiceIntroduces[1]));
            } else {
                voiceIntroduces = audioUrlAndTime.split("_t_");
                String introd1 = voiceIntroduces[1];
                String duration = FileUtil.getFileNameNoEx(introd1);

                if (voiceIntroduces.length == 2) {
                    iv_play.setVisibility(View.VISIBLE);
                    tv_record_second.setVisibility(View.VISIBLE);
                    tv_record_second.setText(String.format("%s″", duration));
                    mAudioRecordPlayable = new AudioRecordPlayable(audioUrlAndTime, Long.parseLong(duration));
                }
            }
        }

        mOnAudioRecordSuccess = new Action0() {
            @Override
            public void call() {
                iv_play.setVisibility(View.VISIBLE);
                tv_record_second.setVisibility(View.VISIBLE);
                tv_record_second.setText(String.format("%d″", mAudioRecordPlayable.getDuration() / 1000));
            }
        };

        mOnAudioRecordStatus = new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    updateRecordLayout(record_layout);
                    iv_audio_record.setImageResource(R.drawable.recording_2_s);
                    tv_audio_record.setTextColor(getResources().getColor(R.color.white));
                } else {
                    record_layout.setBackgroundResource(R.drawable.recording_dise);
                    updateRecordImageResource(iv_audio_record);
                    tv_audio_record.setTextColor(getResources().getColor(R.color.main_text_color));
                }
            }
        };

        record_layout.setOnTouchListener(audioRecordListener);
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioRecordPlayable == null) {
                    return;
                }

                if (audioPlaying) {
                    mPathAudioControl.stopAudio();
                    onAudioEnd(iv_play);
                    return;
                }

                mPathAudioControl.startPlayAudioDelay(0, mAudioRecordPlayable, new BaseAudioControl.AudioControlListener() {
                    @Override
                    public void onAudioControllerReady(Playable playable) {
                        //iv_play.setImageResource(R.drawable.voice_playing_n);
                        onAudioReady(iv_play);
                    }

                    @Override
                    public void onEndPlay(Playable playable) {
                        onAudioEnd(iv_play);
                    }

                    @Override
                    public void updatePlayingProgress(Playable playable, long curPosition) {

                    }
                });
            }
        });

        iv_play.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UIDialog.build()
                        .setDialogContent(getString(R.string.text_delete_audio_introduce))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                iv_play.setVisibility(View.GONE);
                                tv_record_second.setVisibility(View.GONE);
                                mAudioRecordPlayable = null;
                                RRealm.exe(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        UserCache.instance().getUserInfoBean().setVoice_introduce("empty");
                                    }
                                });
                                deleteAudoIntrod();
                            }
                        })
                        .showDialog(mILayout);
                return true;
            }
        });
    }

    private void deleteAudoIntrod() {
        onAudioEnd(iv_play);
        add(RRetrofit.create(UserService.class)
                .editInfo(Param.buildMap("voice:empty" ))
                .compose(Rx.transformer(UserInfoBean.class))
                .subscribe(new BaseSingleSubscriber<UserInfoBean>() {
                    @Override
                    public void onSucceed(UserInfoBean bean) {
                        super.onSucceed(bean);
                        T_.show(getString(R.string.text_delete_success));
                    }
                }));
    }

    private void onAudioEnd(ImageView iv_play) {
        audioPlaying = false;
        iv_play.clearAnimation();
        //iv_play.setImageResource(R.drawable.dynamic_notification);
        updateRecordPlayEndResource(iv_play);
    }

    private void onAudioReady(ImageView iv_play) {
        audioPlaying = true;
        updateRecordPlayingResource(iv_play);
        animRecordPlay(iv_play);
    }

    private void animRecordPlay(ImageView iv_play) {
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.base_rotate);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        iv_play.setAnimation(animation);
        iv_play.startAnimation(animation);
    }

    private void updateRecordImageResource(final ImageView iv_audio_record) {
        switch (SkinUtils.getSkin()) {
            case SkinManagerUIView.SKIN_BLACK:
                iv_audio_record.setImageResource(R.drawable.recording_2_n_black);
                break;
            case SkinManagerUIView.SKIN_GREEN:
                iv_audio_record.setImageResource(R.drawable.recording_2_n);
                break;
            case SkinManagerUIView.SKIN_BLUE:
                iv_audio_record.setImageResource(R.drawable.recording_2_n_blue);
                break;
        }
    }

    private void updateRecordPlayingResource(final ImageView iv_play) {
        switch (SkinUtils.getSkin()) {
            case SkinManagerUIView.SKIN_BLACK:
                iv_play.setImageResource(R.drawable.near_voice_playing_black_s);
                break;
            case SkinManagerUIView.SKIN_GREEN:
                iv_play.setImageResource(R.drawable.near_voice_playing_s);
                break;
            case SkinManagerUIView.SKIN_BLUE:
                iv_play.setImageResource(R.drawable.near_voice_playing_blue_s);
                break;
        }
    }

    private void updateRecordPlayEndResource(final ImageView iv_play) {
        switch (SkinUtils.getSkin()) {
            case SkinManagerUIView.SKIN_BLUE:
                iv_play.setImageResource(R.drawable.voice_playing_blue);
                break;
            case SkinManagerUIView.SKIN_GREEN:
                iv_play.setImageResource(R.drawable.voice_playing);
                break;
            default:
                iv_play.setImageResource(R.drawable.voice_playing_black);
                break;
        }
    }

    private void updateRecordLayout(final View record_layout) {
        switch (SkinUtils.getSkin()) {
            case SkinManagerUIView.SKIN_BLACK:
                record_layout.setBackgroundResource(R.drawable.recording_dise_s_black);
                break;
            case SkinManagerUIView.SKIN_GREEN:
                record_layout.setBackgroundResource(R.drawable.recording_dise_s);
                break;
            case SkinManagerUIView.SKIN_BLUE:
                record_layout.setBackgroundResource(R.drawable.recording_dise_s_blue);
                break;
        }
    }

    /**
     * 照片墙
     */
    protected void bindPhoneWallItem(RBaseViewHolder holder, final int size) {
        final RDragRecyclerView dragRecyclerView = holder.v(R.id.drag_recycler_view);
        RelativeLayout rl_add_photo = holder.v(R.id.rl_add_photo);
        ViewGroup.LayoutParams layoutParams = dragRecyclerView.getLayoutParams();
        layoutParams.width = ScreenUtil.screenWidth;

        int itemHeight = layoutParams.width / 3;
        layoutParams.height = itemHeight * 2 + ScreenUtil.dip2px(20);
        dragRecyclerView.setLayoutParams(layoutParams);

        rl_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePickerHelper.startImagePicker(mActivity, true, false, false, true, getMaxSelectorCount());
            }
        });

        //分割线
        dragRecyclerView.addItemDecoration(RExItemDecoration.build(new RExItemDecoration.ItemDecorationCallback() {
            @Override
            public Rect getItemOffsets(RecyclerView.LayoutManager layoutManager, int position, int edge) {
                int size = ScreenUtil.dip2px(10);
                Rect rect = new Rect(0, 0, 0, 0);
                rect.top = size / 2;
                rect.right = size / 2;
                if (position == 0 | position == 3) {
                    rect.left = size;
                } else if (position == 2 | position == 5) {
                    rect.right = size;
                }
                if (position > 2) {
                    rect.bottom = size;
                }
                return rect;
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {

            }
        }));

        //拖拽处理
        dragRecyclerView.setDragCallback(new RDragRecyclerView.OnDragCallback() {

            @Override
            public boolean canDragDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                int itemCount = mHnAddImageAdapter.getAllDatas().size();
                if (itemCount == 6) {
                    return true;
                } else {
                    return position != mHnAddImageAdapter.getItemCount() - 1;
                }
            }
        });

        mHnAddImageAdapter = new HnAddImageAdapter(mActivity);
        mHnAddImageAdapter.setItemHeight(itemHeight);
        mHnAddImageAdapter.resetData(mPhones);


        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mHnAddImageAdapter.getAllDatas().size() == 0 ? 3 : 1;
            }
        });
        dragRecyclerView.setLayoutManager(layoutManager);

        dragRecyclerView.setAdapter(mHnAddImageAdapter);

        //事件处理
        mHnAddImageAdapter.setAddListener(new HnAddImageAdapter.OnAddListener() {

            @Override
            public void onAddClick(View view, int position, Luban.ImageItem item) {
                isSetUserIco = false;
                ImagePickerHelper.startImagePicker(mActivity, true, false, false, true, getMaxSelectorCount());
            }

            @Override
            public void onItemClick(View view, int position, Luban.ImageItem item) {
                ImagePagerUIView.start(mParentILayout, view,
                        PhotoPager.getImageItems2(mHnAddImageAdapter.getAllDatas()), position);
            }

            @Override
            public void onItemLongClick(View view, int position, Luban.ImageItem item) {
                mHnAddImageAdapter.setDeleteModel(dragRecyclerView);
            }

            @Override
            public void onDeleteClick(View view, int position, Luban.ImageItem item) {
                mHnAddImageAdapter.getAllDatas().remove(position);
                ImagePickerHelper.deleteItemFromSelected(item.path);
//                if (position == 5) {
//                    mHnAddImageAdapter.notifyItemChanged(position);
//                } else {
                mHnAddImageAdapter.notifyItemRemoved(position);
                mHnAddImageAdapter.notifyItemRangeChanged(position, 6);
//                }
            }
        });
    }

    /**
     * 图片最大选择的数量, 需要减去已经存在的网络图片
     */
    private int getMaxSelectorCount() {
        int urlCount = 0;
        for (Luban.ImageItem item : mPhones) {
            if (!TextUtils.isEmpty(item.url)) {
                urlCount++;
            }
        }
        return 6 - urlCount;
    }

    private List<Luban.ImageItem> getOldItems() {
        List<Luban.ImageItem> items = new ArrayList<>();
        for (Luban.ImageItem item : mPhones) {
            if (!TextUtils.isEmpty(item.url)) {
                items.add(item);
            }
        }
        return items;
    }

    private void updateSex(final ItemInfoLayout infoLayout, final UserInfoBean old, final String sex) {
        if (TextUtils.equals(old.getSex(), sex)) {
            return;
        }
        RRealm.exe(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                old.setSex(sex);
            }
        });
        infoLayout.setItemDarkText(UserControl.getSex(mActivity, sex));
//        add(RRetrofit.create(UserService.class)
//                .editInfo(Param.buildMap("sex:" + sex))
//                .compose(Rx.transformer(UserInfoBean.class))
//                .subscribe(new BaseSingleSubscriber<UserInfoBean>() {
//                    @Override
//                    public void onSucceed(UserInfoBean bean) {
//                        super.onSucceed(bean);
//                        UserCache.instance().setUserInfoBean(bean);
//                    }
//                }));
    }

    /**
     * 个性签名
     */
    protected void bindSignatureItem(RBaseViewHolder holder, final UserInfoBean userInfoBean) {
        final ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
        infoLayout.setItemText(mActivity.getString(R.string.signature_title));
        infoLayout.setItemDarkText(userInfoBean.getSignature());
        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(InputUIView.build(new InputUIView.InputConfigCallback() {
                    @Override
                    public TitleBarPattern initTitleBar(TitleBarPattern titleBarPattern) {
                        return super.initTitleBar(titleBarPattern)
                                .setTitleString(mActivity, R.string.signature_title)
                                .addRightItem(TitleBarPattern.TitleBarItem.build(mActivity.getResources().getString(R.string.finish),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String value = "empty";
                                                if (!mExEditText.isEmpty()) {
                                                    value = mExEditText.string();
                                                }

                                                RRealm.exe(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        userInfoBean.setSignature(mExEditText.string());
                                                    }
                                                });
                                                infoLayout.setItemDarkText(mExEditText.string());

//                                                add(RRetrofit.create(UserService.class)
//                                                        .editInfo(Param.buildMap("signature:" + value))
//                                                        .compose(Rx.transformer(UserInfoBean.class))
//                                                        .subscribe(new BaseSingleSubscriber<UserInfoBean>() {
//                                                            @Override
//                                                            public void onSucceed(UserInfoBean bean) {
//                                                                super.onSucceed(bean);
//                                                                UserCache.instance().setUserInfoBean(bean);
//                                                            }
//                                                        }));

                                                finishIView(mIView);
                                            }
                                        }));
                    }

                    @Override
                    public void initInputView(RBaseViewHolder holder, ExEditText editText, ViewItemInfo bean) {
                        super.initInputView(holder, editText, bean);
                        editText.setSingleLine(false);
                        editText.setMaxLines(5);
                        int maxCount = mActivity.getResources().getInteger(R.integer.signature_count);
                        editText.setMaxLength(maxCount);
                        String signature = userInfoBean.getSignature();
                        setInputText(signature);
                        editText.setGravity(Gravity.TOP);
                        UI.setViewHeight(editText, mActivity.getResources().getDimensionPixelOffset(R.dimen.base_100dpi));

                        final TextIndicator textIndicator = holder.v(R.id.single_text_indicator_view);
                        textIndicator.setVisibility(View.VISIBLE);
                        textIndicator.initIndicator(TextUtils.isEmpty(signature) ? 0 : signature.length(), maxCount);

                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                textIndicator.setCurrentCount(s.length());
                            }
                        });
                    }
                }));
            }
        });
    }

    /**
     * 用户昵称
     */
    protected void bindNameItem(RBaseViewHolder holder, final UserInfoBean userInfoBean) {
        final ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
        infoLayout.setItemText(mActivity.getString(R.string.name_text));
        infoLayout.setItemDarkText(userInfoBean.getUsername());
        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(InputUIView.build(new InputUIView.InputConfigCallback() {
                    @Override
                    public TitleBarPattern initTitleBar(TitleBarPattern titleBarPattern) {
                        return super.initTitleBar(titleBarPattern)
                                .setTitleString(mActivity.getString(R.string.modify_name_title))
                                .addRightItem(TitleBarPattern.TitleBarItem.build(mActivity.getResources().getString(R.string.save)
                                        , new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (mExEditText.isEmpty()) {
                                                    Anim.band(mExEditText);
                                                    return;
                                                }

                                                final String name = RUtils.fixName(mExEditText.string());
                                                RRealm.exe(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        userInfoBean.setUsername(name);
                                                    }
                                                });
                                                infoLayout.setItemDarkText(name);

//                                                add(RRetrofit.create(UserService.class)
//                                                        .editInfo(Param.buildMap("username:" + name))
//                                                        .compose(Rx.transformer(UserInfoBean.class))
//                                                        .subscribe(new BaseSingleSubscriber<UserInfoBean>() {
//                                                            @Override
//                                                            public void onSucceed(UserInfoBean bean) {
//                                                                super.onSucceed(bean);
//                                                                UserCache.instance().setUserInfoBean(bean);
//                                                            }
//                                                        }));
                                                finishIView(mIView);
                                            }
                                        }))
                                ;
                    }

                    @Override
                    public void initInputView(RBaseViewHolder holder, ExEditText editText, ViewItemInfo bean) {
                        super.initInputView(holder, editText, bean);
                        editText.setMaxLength(mActivity.getResources().getInteger(R.integer.name_count));
                        editText.setHint(R.string.input_name_hint);
                        setInputText(userInfoBean.getUsername());
                    }
                }));
            }
        });
    }

    /**
     * 出生日期
     */
    protected void bindBirthdayItem(RBaseViewHolder holder, final UserInfoBean userInfoBean) {
        final ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
        infoLayout.setItemText(mActivity.getString(R.string.birthday2));
        infoLayout.setItemDarkText(userInfoBean.getBirthday());
        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new DateDialog(new DateDialog.SimpleDateConfig() {
                    @Override
                    public void onDateSelector(WheelTime wheelTime) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(wheelTime.getSelectorYear());
                        builder.append("-");
                        builder.append(wheelTime.getSelectorMonth());
                        builder.append("-");
                        builder.append(wheelTime.getSelectorDay());
                        final String time = builder.toString();

                        RRealm.exe(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                userInfoBean.setBirthday(time);
                            }
                        });
                        infoLayout.setItemDarkText(time);

//                        add(RRetrofit.create(UserService.class)
//                                .editInfo(Param.buildMap("birthday:" + time))
//                                .compose(Rx.transformer(UserInfoBean.class))
//                                .subscribe(new BaseSingleSubscriber<UserInfoBean>() {
//                                    @Override
//                                    public void onSucceed(UserInfoBean bean) {
//                                        super.onSucceed(bean);
//                                        UserCache.instance().setUserInfoBean(bean);
//                                    }
//                                }));
                    }

                    @Override
                    public String getCurrentDate() {
                        return userInfoBean.getBirthday();
                    }
                }));
            }
        });
    }

    protected void bindAreaItem(RBaseViewHolder holder, final UserInfoBean userInfoBean) {
        final ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
        infoLayout.setItemText(mActivity.getString(R.string.area));
        infoLayout.setItemDarkText(userInfoBean.getProvince_name() + " " + userInfoBean.getCity_name());
        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new CityDialog(userInfoBean.getProvince_id(), userInfoBean.getCity_id(), new Action2<ProvinceBean, ProvinceBean>() {
                    @Override
                    public void call(final ProvinceBean provinceBean, final ProvinceBean provinceBean2) {
                        RRealm.exe(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                userInfoBean.setProvince_id(provinceBean.getId());
                                userInfoBean.setCity_id(provinceBean2.getId());

                                userInfoBean.setProvince_name(provinceBean.getShort_name());
                                userInfoBean.setCity_name(provinceBean2.getShort_name());
                            }
                        });
                        infoLayout.setItemDarkText(userInfoBean.getProvince_name() + " " + userInfoBean.getCity_name());

//                        add(RRetrofit.create(UserService.class)
//                                .editInfo(Param.buildMap("province_id:" + provinceBean.getId(), "city_id:" + provinceBean2.getId()))
//                                .compose(Rx.transformer(UserInfoBean.class))
//                                .subscribe(new BaseSingleSubscriber<UserInfoBean>() {
//                                    @Override
//                                    public void onSucceed(UserInfoBean bean) {
//                                        super.onSucceed(bean);
//                                        UserCache.instance().setUserInfoBean(bean);
//                                    }
//                                }));
                    }
                }));
            }
        });
    }

    protected String getBirthday(String date) {
        return getString(R.string.birthday_format, DateDialog.getBirthday(date));
    }

    @Override
    public void onRecordReady() {

    }

    @Override
    public void onRecordStart(File audioFile, RecordType recordType) {

    }

    @Override
    public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {

        // 可播放
        // 上传语音

        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis() / 1000)
                .append("_t_")
                .append(audioLength / 1000)
                .append(".")
                .append("aac");

        String newName = sb.toString();
        if (FileUtils.rename(audioFile, newName)) {
            mAudioRecordPlayable = new AudioRecordPlayable(FileUtils.getDirName(audioFile) + newName, audioLength);
            if (mOnAudioRecordSuccess != null) {
                mOnAudioRecordSuccess.call();
            }
        }
    }

    @Override
    public void onRecordFail() {

    }

    @Override
    public void onRecordCancel() {
        T_.show("录音取消了");
    }

    @Override
    public void onRecordReachedMaxTime(final int maxTime) {
        stopAudioRecordAnim();
        startIView(UIDialog.build()
                .setDialogContent(mActivity.getString(R.string.recording_max_time))
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        audioMessageHelper.handleEndRecord(true, maxTime);
                    }
                })
                .setGravity(Gravity.CENTER));
    }

    /**
     * 用来判断是否更改了信息
     */
    class OldParams {
        public List<String> mPhotos = new ArrayList<>();
        public String mUserIco;
        public String mUserName;
        public String mUserSex;
        public String mBirthday;
        public String mArea;
        public String mUserVoiceUrl;
        public String mSignature;
        public String pName;
        public String cName;

        public boolean isChanged(UserInfoBean userInfoBean) {
            boolean changed = false;

            List<Luban.ImageItem> allDatas = mHnAddImageAdapter.getAllDatas();
            for (int i = 0; i < allDatas.size(); i++) {
                //照片墙给变了
                try {
                    Luban.ImageItem imageItem = allDatas.get(i);
                    String url = mPhotos.get(i);
                    if (!TextUtils.equals(url, imageItem.url)) {
                        changed = true;
                        return changed;
                    }
                } catch (Exception e) {
                    changed = true;
                    return changed;
                }
            }

            if (!TextUtils.isEmpty(mUserSetIco)) {
                //头像改变了
                changed = true;
                return changed;
            }
            if (!TextUtils.equals(mUserName, userInfoBean.getUsername())) {
                changed = true;
                return changed;
            }
            if (!TextUtils.equals(mUserSex, userInfoBean.getSex())) {
                changed = true;
                return changed;
            }
            if (!TextUtils.equals(mBirthday, userInfoBean.getBirthday())) {
                changed = true;
                return changed;
            }
            if (!TextUtils.equals(mArea, userInfoBean.getProvince_id() + "_" + userInfoBean.getCity_id())) {
                changed = true;
                return changed;
            }
            if (!TextUtils.equals(mUserVoiceUrl, userInfoBean.getVoice_introduce())) {
                changed = true;
                return changed;
            }
            if (!TextUtils.equals(mSignature, userInfoBean.getSignature())) {
                changed = true;
                return changed;
            }
            return changed;
        }
    }

}
