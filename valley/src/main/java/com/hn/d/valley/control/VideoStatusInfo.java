package com.hn.d.valley.control;

import io.realm.RealmObject;

/**
 * 视频动态
 */
public class VideoStatusInfo extends RealmObject {
    private String videoThumbPath, videoPath;

    public VideoStatusInfo() {
    }

    public VideoStatusInfo(String videoThumbPath, String videoPath) {
        this.videoThumbPath = videoThumbPath;
        this.videoPath = videoPath;
    }

    public String getVideoThumbPath() {
        return videoThumbPath;
    }

    public void setVideoThumbPath(String videoThumbPath) {
        this.videoThumbPath = videoThumbPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}