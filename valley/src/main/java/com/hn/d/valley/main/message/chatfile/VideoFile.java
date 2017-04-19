package com.hn.d.valley.main.message.chatfile;

import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;

/**
 * Created by hewking on 2017/4/16.
 */

public class VideoFile extends ChatFile {


    private int width;

    private int height;

    private long duration;

    public VideoFile(String path, long size, String displayName, String thumbPath, long time, int width, int height, long duration) {
        super(path, size, displayName, thumbPath, time);
        this.width = width;
        this.height = height;
        this.duration = duration;
        media_type = "3";

    }

    public int[] genWidthAndHeight() {
        return new int[]{width, height};
    }

    public static VideoFile createVideo(VideoAttachment attachment, long time) {

        return new VideoFile(attachment.getPath(), attachment.getSize(), attachment.getDisplayName(), attachment.getThumbPath()
                , time, attachment.getWidth(), attachment.getHeight(), attachment.getDuration());

    }
}
