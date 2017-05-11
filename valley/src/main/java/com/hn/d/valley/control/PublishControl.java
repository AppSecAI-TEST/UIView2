package com.hn.d.valley.control;

import android.support.v4.util.ArraySet;
import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
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
import com.hn.d.valley.sub.user.PublishDynamicUIView;
import com.hn.d.valley.utils.RBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
    ArraySet<PublishTask> mPublishTasks;

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
    private String voiceImagePath = PublishDynamicUIView.VoiceStatusInfo.NOPIC;

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
     * 添加一个后台任务
     */
    public void addTask(final PublishTask task) {
        if (task == null) {
            return;
        }
        mPublishTasks.add(task);
        mDataListBeen.add(createBean(task));
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
        final PublishTask publishTask = mPublishTasks.valueAt(index);

        if ("2".equalsIgnoreCase(publishTask.type)) {
            //发布视频
            publishVideo(publishTask);
        } else if ("1".equalsIgnoreCase(publishTask.type)) {
            //发布纯文字
            publish(publishTask, null);
        } else if ("4".equalsIgnoreCase(publishTask.type)) {
            //发布语音
            publishVoice(publishTask);
        } else {
            //发布图文
            List<String> files = new ArrayList<>();
            for (Luban.ImageItem item : publishTask.photos) {
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

        mPublishListener.onPublishStart();
    }

    /**
     * 上传语音资源
     */
    private void publishVoice(final PublishTask publishTask) {
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

                    if (publishTask.mVoiceStatusInfo.isNoPic()) {
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
                        }).uploadCircleImg(publishTask.mVoiceStatusInfo.voiceImagePath);
                    }

                }
            }

            @Override
            public void onUploadFailed(int code, String msg) {
                onPublishStart(true);
            }
        }).uploadAudio(publishTask.mVoiceStatusInfo.voicePath);
    }

    private void publishVoiceNext(PublishTask task, String image, String voice) {
        List<String> media = new ArrayList<>();
        media.add(image + "?" + voice);
        publish(task, media);
    }

    /**
     * 上传视频资源
     */
    private void publishVideo(final PublishTask publishTask) {
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
                    files.add(publishTask.mVideoStatusInfo.videoThumbPath);

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
        }).uploadVideo(publishTask.mVideoStatusInfo.videoPath);
    }

    UserDiscussListBean.DataListBean createBean(final PublishTask task) {
        UserDiscussListBean.DataListBean bean = new UserDiscussListBean.DataListBean();
        bean.uuid = task.uuid;
        bean.setAddress(task.shareLocation ? task.address : "");
        bean.setDiscuss_id("-" + (new Random(System.currentTimeMillis()).nextInt(10000) + 1));
        bean.setLat(task.lat);
        bean.setLng(task.lng);
        bean.setMedia_type(task.type);
        bean.setUid(UserCache.getUserAccount());
        bean.setIs_top(task.isTop ? "1" : "0");

        //标签
        List<String> tagsName = new ArrayList<>();
        for (Tag tag : task.mSelectorTags) {
            tagsName.add(tag.getName());
        }
        bean.setTags_name(RUtils.connect(tagsName));

        bean.setContent(task.content);

        if ("2".equalsIgnoreCase(task.type)) {
            //视频
            bean.setMedia(task.mVideoStatusInfo.videoThumbPath + "?" + task.mVideoStatusInfo.videoPath);
        } else if ("4".equalsIgnoreCase(task.type)) {
            //音频
            bean.setMedia(task.mVoiceStatusInfo.voiceImagePath + "?" + task.mVoiceStatusInfo.voicePath);
        } else {
            //媒体图片
            List<String> photos = new ArrayList<>();
            for (Luban.ImageItem item : task.photos) {
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
    void publish(final PublishTask task, final List<String> photos) {
        RRetrofit.create(DiscussService.class)
                .publish(Param.buildMap(
                        "tags:" + RUtils.connect(task.mSelectorTags),
                        "media_type:" + task.type,
                        "media:" + RUtils.connect(photos),
                        "is_top:" + (task.isTop ? 1 : 0),
                        "open_location:" + (task.shareLocation ? 1 : 0),
                        "content:" + task.content,
                        "address:" + task.address,
                        "lng:" + task.lng,
                        "allow_download:" + task.allow_download,
                        "scan_type:" + task.scan_type,
                        "scan_user:" + task.scan_user,
                        "lat:" + task.lat))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                               @Override
                               public void onSucceed(String s) {
                                   mPublishTasks.remove(task);
                                   for (UserDiscussListBean.DataListBean bean : mDataListBeen) {
                                       if (TextUtils.equals(bean.uuid, task.uuid)) {
                                           mDataListBeen.remove(bean);
                                           break;
                                       }
                                   }
                                   RBus.post(Constant.TAG_UPDATE_CIRCLE, new UpdateDataEvent());
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
    }

    private static class Holder {
        static PublishControl instance = new PublishControl();
    }

    public static class PublishTask {
        /**
         * 需要上传的图片
         */
        public List<Luban.ImageItem> photos = new ArrayList<>();
        /**
         * 选中的tags
         */
        public List<Tag> mSelectorTags = new ArrayList<>();

        /**
         * 是否置顶
         */
        public boolean isTop;

        /**
         * 是否共享地址位置
         */
        public boolean shareLocation;

        /**
         * 发布的文本内容
         */
        public String content;

        /**
         * 地理坐标和位置信息
         */
        public String address, lng, lat;

        /**
         * 1-纯文本 2-视频 3-图片
         */
        public String type;
        public PublishDynamicUIView.VideoStatusInfo mVideoStatusInfo;
        public PublishDynamicUIView.VoiceStatusInfo mVoiceStatusInfo;
        /**
         * 是否允许下载【0-不允许 1-允许】
         */
        public int allow_download = 1;
        /**
         * 查看类型【1-公开 2-私密 3-部分好友可见 4-不给谁看,5-仅好友可见】
         */
        public int scan_type = 1;
        /**
         * 部分好友可见/不给谁看的用户id 多个以英文，分割
         */
        public String scan_user;
        private String uuid;

        /**
         * 构建一个图文动态的任务
         */
        public PublishTask(List<Luban.ImageItem> photos, List<Tag> selectorTags,
                           boolean isTop, boolean shareLocation, String content, String address, String lng, String lat) {
            uuid = UUID.randomUUID().toString();
            this.photos = photos;
            mSelectorTags = selectorTags;
            this.isTop = isTop;
            this.shareLocation = shareLocation;
            this.content = content;
            this.address = address;
            this.lng = lng;
            this.lat = lat;
            type = "3";
        }

        public PublishTask(List<Luban.ImageItem> photos) {
            uuid = UUID.randomUUID().toString();
            this.photos = photos;
            type = "3";
        }

        public PublishTask() {
            uuid = UUID.randomUUID().toString();
            type = "1";
        }

        /**
         * 语音动态
         */
        public PublishTask(PublishDynamicUIView.VoiceStatusInfo voiceStatusInfo) {
            uuid = UUID.randomUUID().toString();
            type = "4";
            mVoiceStatusInfo = voiceStatusInfo;
        }

        /**
         * 构建一个视频动态的任务
         */
        public PublishTask(PublishDynamicUIView.VideoStatusInfo videoStatusInfo, List<Tag> selectorTags,
                           boolean isTop, boolean shareLocation, String content, String address, String lng, String lat) {
            uuid = UUID.randomUUID().toString();
            mVideoStatusInfo = videoStatusInfo;
            mSelectorTags = selectorTags;
            this.isTop = isTop;
            this.shareLocation = shareLocation;
            this.content = content;
            this.address = address;
            this.lng = lng;
            this.lat = lat;
            type = "2";
        }

        public PublishTask(PublishDynamicUIView.VideoStatusInfo videoStatusInfo) {
            uuid = UUID.randomUUID().toString();
            mVideoStatusInfo = videoStatusInfo;
            type = "2";
        }

        public PublishTask setPhotos(List<Luban.ImageItem> photos) {
            this.photos = photos;
            return this;
        }

        public PublishTask setSelectorTags(List<Tag> selectorTags) {
            mSelectorTags = selectorTags;
            return this;
        }

        public PublishTask setTop(boolean top) {
            isTop = top;
            return this;
        }

        public PublishTask setShareLocation(boolean shareLocation) {
            this.shareLocation = shareLocation;
            return this;
        }

        public PublishTask setContent(String content) {
            this.content = content;
            return this;
        }

        public PublishTask setAddress(String address) {
            this.address = address;
            return this;
        }

        public PublishTask setLng(String lng) {
            this.lng = lng;
            return this;
        }

        public PublishTask setLat(String lat) {
            this.lat = lat;
            return this;
        }

        public PublishTask setType(String type) {
            this.type = type;
            return this;
        }

        public PublishTask setAllow_download(int allow_download) {
            this.allow_download = allow_download;
            return this;
        }

        public PublishTask setScan_type(int scan_type) {
            this.scan_type = scan_type;
            return this;
        }

        public PublishTask setScan_user(String scan_user) {
            this.scan_user = scan_user;
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            return TextUtils.equals(uuid, ((PublishTask) obj).uuid);
        }
    }

}
