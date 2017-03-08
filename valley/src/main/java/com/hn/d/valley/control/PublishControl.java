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
            return;
        }
        isStart = true;
        mPublishListener.onPublishStart();

        onPublishStart();
    }

    private void onPublishStart() {
        if (mPublishTasks.isEmpty()) {
            isStart = false;
            mDataListBeen.clear();
            mPublishListener.onPublishEnd();
            return;
        }

        final PublishTask publishTask = mPublishTasks.valueAt(0);

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
                onPublishStart();
            }
        }).uploadCircleImg(files);
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

        //媒体图片
        List<String> photos = new ArrayList<>();
        for (Luban.ImageItem item : task.photos) {
            photos.add(item.thumbPath);
        }
        bean.setMedia(RUtils.connect(photos));


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
                                   onPublishStart();
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
        public List<Luban.ImageItem> photos;
        /**
         * 选中的tags
         */
        public List<Tag> mSelectorTags;

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

        @Override
        public boolean equals(Object obj) {
            return TextUtils.equals(uuid, ((PublishTask) obj).uuid);
        }
    }

}
