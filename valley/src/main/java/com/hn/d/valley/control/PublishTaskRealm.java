package com.hn.d.valley.control;

import android.text.TextUtils;

import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.sub.user.DynamicType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class PublishTaskRealm extends RealmObject {
    public static final int STATUS_NORMAL = 0;//正常
    public static final int STATUS_ING = 1;//发布中
    public static final int STATUS_SUCCESS = 2;//发布成功
    public static final int STATUS_ERROR = 3;//发布失败
    /**
     * 需要上传的图片
     */
    private RealmList<ImageItemRealm> photos = new RealmList<>();

    /**
     * 选中的tags
     */
    private RealmList<TagRealm> mSelectorTags = new RealmList<>();
    /**
     * 是否置顶
     */
    private boolean isTop = false;
    /**
     * 是否共享地址位置
     */
    private boolean shareLocation = false;
    /**
     * 发布的文本内容
     */
    private String content = "";
    /**
     * 发布的文本显示的内容, 只用来做本地显示
     */
    private String showContent = "";

    /**
     * 重发时, 用来显示的内容
     */
    private String showContent2 = "";
    /**
     * 地理坐标和位置信息
     */
    private String address = "", lng = "", lat = "";
    /**
     * 1-纯文本 2-视频 3-图片
     */
    private String type = "0";
    private VideoStatusInfo mVideoStatusInfo;
    private VoiceStatusInfo mVoiceStatusInfo;
    /**
     * 是否允许下载【0-不允许 1-允许】
     */
    private int allow_download = 1;
    /**
     * 查看类型【1-公开 2-私密 3-部分好友可见 4-不给谁看,5-仅好友可见】
     */
    private int scan_type = 1;
    /**
     * 部分好友可见/不给谁看的用户id 多个以英文，分割
     */
    private String scan_user = "";
    private String uuid = "";
    /**
     * 任务发布状态
     */
    private int publishStatus = STATUS_NORMAL;

    private long createTime = 0l;
    private String package_id = "";

    /**
     * 构建一个图文动态的任务
     */
    public PublishTaskRealm(List<Luban.ImageItem> photos, List<Tag> selectorTags,
                            boolean isTop, boolean shareLocation, String content, String address, String lng, String lat) {
        uuid = UUID.randomUUID().toString();
        setPhotos2(photos);
        setSelectorTags2(selectorTags);
        this.isTop = isTop;
        this.shareLocation = shareLocation;
        this.content = content;
        this.address = address;
        this.lng = lng;
        this.lat = lat;
        type = DynamicType.IMAGE.getValueString();
        createTime = System.currentTimeMillis();
    }

    public PublishTaskRealm(List<Luban.ImageItem> photos) {
        uuid = UUID.randomUUID().toString();
        setPhotos2(photos);
        type = DynamicType.IMAGE.getValueString();
        createTime = System.currentTimeMillis();
    }

    public PublishTaskRealm() {
        uuid = UUID.randomUUID().toString();
        type = DynamicType.TEXT.getValueString();
        createTime = System.currentTimeMillis();
    }

    /**
     * 语音动态
     */
    public PublishTaskRealm(VoiceStatusInfo voiceStatusInfo) {
        uuid = UUID.randomUUID().toString();
        type = DynamicType.VOICE.getValueString();
        mVoiceStatusInfo = voiceStatusInfo;
        createTime = System.currentTimeMillis();
    }

    /**
     * 构建一个视频动态的任务
     */
    public PublishTaskRealm(VideoStatusInfo videoStatusInfo, List<Tag> selectorTags,
                            boolean isTop, boolean shareLocation, String content, String address, String lng, String lat) {
        uuid = UUID.randomUUID().toString();
        mVideoStatusInfo = videoStatusInfo;
        setSelectorTags2(selectorTags);
        this.isTop = isTop;
        this.shareLocation = shareLocation;
        this.content = content;
        this.address = address;
        this.lng = lng;
        this.lat = lat;
        type = DynamicType.VIDEO.getValueString();
        createTime = System.currentTimeMillis();
    }

    public PublishTaskRealm(VideoStatusInfo videoStatusInfo) {
        uuid = UUID.randomUUID().toString();
        mVideoStatusInfo = videoStatusInfo;
        type = DynamicType.VIDEO.getValueString();
        createTime = System.currentTimeMillis();
    }

    /**
     * 保存至数据库
     */
    public static void save(PublishTaskRealm task) {
        RRealm.save(task);
    }


    /**
     * 改变任务状态
     */
    public static void changeStatus(final String uuid, final int newStatus) {
        if (newStatus == STATUS_SUCCESS) {
            RRealm.exe(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<PublishTaskRealm> results = realm.where(PublishTaskRealm.class).equalTo("uuid", uuid).findAll();
                    results.deleteAllFromRealm();

                }
            });
        } else {
            RRealm.exe(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<PublishTaskRealm> results = realm.where(PublishTaskRealm.class).equalTo("uuid", uuid).findAll();
                    for (PublishTaskRealm task : results) {
                        task.setPublishStatus(newStatus);
                    }
                }
            });
        }
    }

    public List<Luban.ImageItem> getPhotos2() {
        List<Luban.ImageItem> images = new ArrayList<>();
        for (ImageItemRealm image : photos) {
            images.add(new Luban.ImageItem(image.getPath(), image.getThumbPath(), image.getUrl()));
        }
        return images;
    }

    public void setPhotos2(RealmList<ImageItemRealm> photos) {
        this.photos = photos;
    }

    public PublishTaskRealm setPhotos2(final List<Luban.ImageItem> photos) {
        RRealm.exe(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Luban.ImageItem item : photos) {
                    ImageItemRealm toRealm = realm.createObject(ImageItemRealm.class);
                    toRealm.setPath(item.path);
                    toRealm.setThumbPath(item.thumbPath);
                    toRealm.setUrl(item.url);
                    PublishTaskRealm.this.photos.add(toRealm);
                }
            }
        });
        return this;
    }

    public List<Tag> getSelectorTags2() {
        List<Tag> tags = new ArrayList<>();
        for (TagRealm t : mSelectorTags) {
            tags.add(new Tag(t.getId(), t.getName()));
        }
        return tags;
    }

    public void setSelectorTags2(RealmList<TagRealm> selectorTags) {
        mSelectorTags = selectorTags;
    }

    public PublishTaskRealm setSelectorTags2(final List<Tag> selectorTags) {
        RRealm.exe(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Tag tag : selectorTags) {
                    TagRealm toRealm = realm.createObject(TagRealm.class);
                    toRealm.setName(tag.getName());
                    toRealm.setId(tag.getId());
                    PublishTaskRealm.this.mSelectorTags.add(toRealm);
                }
            }
        });
        return this;
    }

    public RealmList<ImageItemRealm> getPhotos() {
        return photos;
    }

    public void setPhotos(RealmList<ImageItemRealm> photos) {
        this.photos = photos;
    }

    public RealmList<TagRealm> getSelectorTags() {
        return mSelectorTags;
    }

    public void setSelectorTags(RealmList<TagRealm> selectorTags) {
        mSelectorTags = selectorTags;
    }

    public boolean isTop() {
        return isTop;
    }

    public PublishTaskRealm setTop(boolean top) {
        isTop = top;
        return this;
    }

    public boolean isShareLocation() {
        return shareLocation;
    }

    public PublishTaskRealm setShareLocation(boolean shareLocation) {
        this.shareLocation = shareLocation;
        return this;
    }

    public String getContent() {
        return content;
    }

    public PublishTaskRealm setContent(String content) {
        this.content = content;
        return this;
    }

    public String getShowContent() {
        return showContent;
    }

    public PublishTaskRealm setShowContent(String showContent) {
        this.showContent = showContent;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public PublishTaskRealm setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getLng() {
        return lng;
    }

    public PublishTaskRealm setLng(String lng) {
        this.lng = lng;
        return this;
    }

    public String getLat() {
        return lat;
    }

    public PublishTaskRealm setLat(String lat) {
        this.lat = lat;
        return this;
    }

    public String getType() {
        return type;
    }

    public PublishTaskRealm setType(String type) {
        this.type = type;
        return this;
    }

    public VideoStatusInfo getVideoStatusInfo() {
        return mVideoStatusInfo;
    }

    public void setVideoStatusInfo(VideoStatusInfo videoStatusInfo) {
        mVideoStatusInfo = videoStatusInfo;
    }

    public VoiceStatusInfo getVoiceStatusInfo() {
        return mVoiceStatusInfo;
    }

    public void setVoiceStatusInfo(VoiceStatusInfo voiceStatusInfo) {
        mVoiceStatusInfo = voiceStatusInfo;
    }

    public int getAllow_download() {
        return allow_download;
    }

    public PublishTaskRealm setAllow_download(int allow_download) {
        this.allow_download = allow_download;
        return this;
    }

    public int getScan_type() {
        return scan_type;
    }

    public PublishTaskRealm setScan_type(int scan_type) {
        this.scan_type = scan_type;
        return this;
    }

    public String getScan_user() {
        return scan_user;
    }

    public PublishTaskRealm setScan_user(String scan_user) {
        this.scan_user = scan_user;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(int publishStatus) {
        this.publishStatus = publishStatus;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getShowContent2() {
        return showContent2;
    }

    public PublishTaskRealm setShowContent2(String showContent2) {
        this.showContent2 = showContent2;
        return this;
    }

    public String getTimeString() {
        long createTime = getCreateTime();
        String formatDateTime = TimeUtil.getDateTimeString(createTime, "yyyy/MM/dd");
        String[] split = formatDateTime.split("/");
        int year = Integer.valueOf(split[0]);

        formatDateTime = TimeUtil.getDateTimeString(System.currentTimeMillis(), "yyyy/MM/dd");
        split = formatDateTime.split("/");
        int nowYear = Integer.valueOf(split[0]);

        if (nowYear == year) {
            return TimeUtil.getDateTimeString(createTime, "MM-dd HH:mm");
        } else {
            return TimeUtil.getDateTimeString(createTime, "yyyy-MM-dd HH:mm");
        }
    }

    public String getPackage_id() {
        return package_id;
    }

    public PublishTaskRealm setPackage_id(String package_id) {
        this.package_id = package_id;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return TextUtils.equals(uuid, ((PublishTaskRealm) obj).uuid);
    }
}