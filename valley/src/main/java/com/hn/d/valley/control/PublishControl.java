package com.hn.d.valley.control;

import android.support.v4.util.ArraySet;
import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.receiver.NetworkStateReceiver;
import com.angcyo.uiview.utils.RUtils;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.oss.OssControl;
import com.hn.d.valley.base.oss.OssControl2;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.DiscussService;
import com.hn.d.valley.utils.RBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：后台管理动态的发布
 * 创建人员：Robi
 * 创建时间：2017/03/08 15:03
 * 修改人员：Robi
 * 修改时间：2017/03/08 15:03
 * 修改备注：
 * Version: 1.0.0
 */
public class PublishControl {

    /**
     * 待发布的动态
     */
    List<UserDiscussListBean.DataListBean> mDataListBeen;

    /**
     * 需要发布的动态任务
     */
    ArraySet<PublishTaskRealm> mPublishTasks;

    /**
     * 是否已经开始了
     */
    boolean isStart = false;

    OnPublishListener mPublishListener;

    /**
     * 失败后的下一个
     */
    int index = 0;
    /**
     * 语音封面图片
     */
    private String voiceImagePath = VoiceStatusInfo.NOPIC;

    private PublishControl() {
        mDataListBeen = new ArrayList<>();
        mPublishTasks = new ArraySet<>();
    }

    public static PublishControl instance() {
        return Holder.instance;
    }

    public List<UserDiscussListBean.DataListBean> getDataListBeen() {
        return mDataListBeen;
    }

    /**
     * 根据uuid, 返回任务状态
     */
    public int getTaskStatus(String uuid) {
        for (PublishTaskRealm task : mPublishTasks) {
            if (TextUtils.equals(task.getUuid(), uuid)) {
                return task.getPublishStatus();
            }
        }
        return PublishTaskRealm.STATUS_NORMAL;
    }

    /**
     * 添加一个后台任务
     */
    public void addTask(final PublishTaskRealm task) {
        if (task == null) {
            return;
        }
        mPublishTasks.add(task);
        mDataListBeen.add(createBean(task));

        PublishTaskRealm.save(task);
    }

    /**
     * 开始发布任务
     */
    public void startPublish(OnPublishListener listener) {
        if (mPublishTasks.isEmpty()) {
            L.w("no publish task need start.");
            return;
        }

        mPublishListener = listener;
        if (isStart) {
            mPublishListener.onPublishStart();
            return;
        }
        isStart = true;

        onPublishStart(false);
    }

    private void onPublishStart(boolean next) {
        if (mPublishTasks.isEmpty()) {
            isStart = false;
            mDataListBeen.clear();
            mPublishListener.onPublishEnd();
            return;
        }

        if (next) {
            index++;
            if (index >= mPublishTasks.size()) {
                index = 0;
            }
        } else {
            index = 0;
        }

        final PublishTaskRealm publishTask = mPublishTasks.valueAt(index);

        mPublishListener.onPublishStart();
        PublishTaskRealm.changeStatus(publishTask.getUuid(), PublishTaskRealm.STATUS_ING);

        if (NetworkStateReceiver.getNetType().value() <= 0) {
            for (PublishTaskRealm task : mPublishTasks) {
                PublishTaskRealm.changeStatus(task.getUuid(), PublishTaskRealm.STATUS_ERROR);
            }
            mPublishListener.onPublishError("No network.");
            isStart = false;
            return;
        }

        if ("2".equalsIgnoreCase(publishTask.getType())) {
            //发布视频
            publishVideo(publishTask);
        } else if ("1".equalsIgnoreCase(publishTask.getType())) {
            //发布纯文字
            publish(publishTask, null);
        } else if ("4".equalsIgnoreCase(publishTask.getType())) {
            //发布语音
            publishVoice(publishTask);
        } else {
            //发布图文
            List<String> files = new ArrayList<>();
            for (Luban.ImageItem item : publishTask.getPhotos2()) {
                files.add(item.thumbPath);
            }

            new OssControl(new OssControl.OnUploadListener() {
                @Override
                public void onUploadStart() {

                }

                @Override
                public void onUploadSucceed(List<String> list) {
                    publish(publishTask, list);
                }

                @Override
                public void onUploadFailed(int code, String msg) {
                    onPublishStart(true);
                }
            }).uploadCircleImg(files);
        }
    }

    /**
     * 上传语音资源
     */
    private void publishVoice(final PublishTaskRealm publishTask) {
        new OssControl2(new OssControl2.OnUploadListener() {
            @Override
            public void onUploadStart() {

            }

            @Override
            public void onUploadSucceed(List<String> list) {
                if (list == null || list.isEmpty()) {
                    onPublishStart(false);
                } else {
                    //音频上传成功后, 再上传图片
                    final String voiceUrl = list.get(0);
                    L.e("音频上传成功:" + voiceUrl);

                    if (publishTask.getVoiceStatusInfo().isNoPic()) {
                        publishVoiceNext(publishTask, voiceImagePath, voiceUrl);
                    } else {
                        new OssControl(new OssControl.OnUploadListener() {
                            @Override
                            public void onUploadStart() {

                            }

                            @Override
                            public void onUploadSucceed(List<String> list) {
                                voiceImagePath = list.get(0);
                                L.e("音频封面上传成功:" + voiceImagePath);
                                publishVoiceNext(publishTask, voiceImagePath, voiceUrl);
                            }

                            @Override
                            public void onUploadFailed(int code, String msg) {
                                onPublishStart(true);
                            }
                        }).uploadCircleImg(publishTask.getVoiceStatusInfo().getVoiceImagePath());
                    }

                }
            }

            @Override
            public void onUploadFailed(int code, String msg) {
                onPublishStart(true);
            }
        }).uploadAudio(publishTask.getVoiceStatusInfo().getVoicePath());
    }

    private void publishVoiceNext(PublishTaskRealm task, String image, String voice) {
        List<String> media = new ArrayList<>();
        media.add(image + "?" + voice);
        publish(task, media);
    }

    /**
     * 上传视频资源
     */
    private void publishVideo(final PublishTaskRealm publishTask) {
        new OssControl2(new OssControl2.OnUploadListener() {
            @Override
            public void onUploadStart() {

            }

            @Override
            public void onUploadSucceed(List<String> list) {
                if (list == null || list.isEmpty()) {
                    onPublishStart(false);
                } else {
                    //视频上传成功后, 再上传图片
                    final String videoUrl = list.get(0);
                    L.e("视频上传成功:" + videoUrl);

                    List<String> files = new ArrayList<>();
                    files.add(publishTask.getVideoStatusInfo().getVideoThumbPath());

                    new OssControl(new OssControl.OnUploadListener() {
                        @Override
                        public void onUploadStart() {

                        }

                        @Override
                        public void onUploadSucceed(List<String> list) {
                            List<String> media = new ArrayList<>();
                            String videoThumbUrl = list.get(0);
                            media.add(videoThumbUrl + "?" + videoUrl);

                            L.e("视频缩略图上传成功:" + videoThumbUrl);
                            publish(publishTask, media);
                        }

                        @Override
                        public void onUploadFailed(int code, String msg) {
                            onPublishStart(true);
                        }
                    }).uploadCircleImg(files);
                }
            }

            @Override
            public void onUploadFailed(int code, String msg) {
                onPublishStart(true);
            }
        }).uploadVideo(publishTask.getVideoStatusInfo().getVideoPath());
    }

    UserDiscussListBean.DataListBean createBean(final PublishTaskRealm task) {
        UserDiscussListBean.DataListBean bean = new UserDiscussListBean.DataListBean();
        bean.uuid = task.getUuid();
        bean.publishStatus = task.getPublishStatus();
        bean.setAddress(task.isShareLocation() ? task.getAddress() : "");
        bean.setDiscuss_id("-" + (new Random(System.currentTimeMillis()).nextInt(10000) + 1));
        bean.setLat(task.getLat());
        bean.setLng(task.getLng());
        bean.setMedia_type(task.getType());
        bean.setUid(UserCache.getUserAccount());
        bean.setIs_top(task.isTop() ? "1" : "0");

        //标签
        List<String> tagsName = new ArrayList<>();
        for (Tag tag : task.getSelectorTags2()) {
            tagsName.add(tag.getName());
        }
        bean.setTags_name(RUtils.connect(tagsName));

        bean.setContent(task.getContent());

        if ("2".equalsIgnoreCase(task.getType())) {
            //视频
            bean.setMedia(task.getVideoStatusInfo().getVideoThumbPath() + "?" + task.getVideoStatusInfo().getVideoPath());
        } else if ("4".equalsIgnoreCase(task.getType())) {
            //音频
            bean.setMedia(task.getVoiceStatusInfo().getVoiceImagePath() + "?" + task.getVoiceStatusInfo().getVoicePath());
        } else {
            //媒体图片
            List<String> photos = new ArrayList<>();
            for (Luban.ImageItem item : task.getPhotos2()) {
                photos.add(item.thumbPath);
            }
            bean.setMedia(RUtils.connect(photos));
        }

        LikeUserInfoBean userInfoBean = new LikeUserInfoBean();
        userInfoBean.setUid(UserCache.getUserAccount());
        userInfoBean.setAvatar(UserCache.getUserAvatar());
        userInfoBean.setGrade(UserCache.instance().getUserInfoBean().getGrade());
        userInfoBean.setSex(UserCache.instance().getUserInfoBean().getSex());
        userInfoBean.setUsername(UserCache.instance().getUserInfoBean().getUsername());
        bean.setUser_info(userInfoBean);

        return bean;
    }

    /**
     * 调用接口发布动态
     */
    void publish(final PublishTaskRealm task, final List<String> photos) {
        RRetrofit.create(DiscussService.class)
                .publish(Param.buildMap(
                        "tags:" + RUtils.connect(task.getSelectorTags2()),
                        "media_type:" + task.getType(),
                        "media:" + RUtils.connect(photos),
                        "is_top:" + (task.isTop() ? 1 : 0),
                        "open_location:" + (task.isShareLocation() ? 1 : 0),
                        "content:" + task.getContent(),
                        "address:" + task.getAddress(),
                        "lng:" + task.getLng(),
                        "allow_download:" + task.getAllow_download(),
                        "scan_type:" + task.getScan_type(),
                        "scan_user:" + task.getScan_user(),
                        "lat:" + task.getLat()))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                               @Override
                               public void onSucceed(String s) {
                                   task.setPublishStatus(PublishTaskRealm.STATUS_SUCCESS);
                                   PublishTaskRealm.changeStatus(task.getUuid(), PublishTaskRealm.STATUS_SUCCESS);

                                   mPublishTasks.remove(task);
                                   for (UserDiscussListBean.DataListBean bean : mDataListBeen) {
                                       if (TextUtils.equals(bean.uuid, task.getUuid())) {
                                           mDataListBeen.remove(bean);
                                           break;
                                       }
                                   }
                                   RBus.post(Constant.TAG_UPDATE_CIRCLE, new UpdateDataEvent());
                               }

                               @Override
                               public void onError(int code, String msg) {
                                   super.onError(code, msg);
                                   task.setPublishStatus(PublishTaskRealm.STATUS_ERROR);
                                   PublishTaskRealm.changeStatus(task.getUuid(), PublishTaskRealm.STATUS_ERROR);
                               }

                               @Override
                               public void onEnd() {
                                   super.onEnd();
                                   onPublishStart(false);
                               }
                           }
                );
    }

    public interface OnPublishListener {
        /**
         * 开始发布
         */
        void onPublishStart();

        /**
         * 所有任务结束
         */
        void onPublishEnd();

        void onPublishError(String msg);
    }

    private static class Holder {
        static PublishControl instance = new PublishControl();
    }
}
