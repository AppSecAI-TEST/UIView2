package com.hn.d.valley.main.message.chatfile;

import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;

/**
 * Created by hewking on 2017/4/16.
 */

public class PictureFile extends ChatFile {

    /**
     * 图片的宽度
     */
    private int width;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * 图片的高度
     */
    private int height;


    public PictureFile(String path, long size, String displayName, String thumbPath, long time, int width, int height) {
        super(path, size, displayName, thumbPath, time);
        this.width = width;
        this.height = height;
        media_type = "2";

    }

    public static PictureFile createImage(ImageAttachment attachment, long time) {

        return new PictureFile(attachment.getPath(), attachment.getSize(), attachment.getDisplayName(), attachment.getThumbPath()
                , time, attachment.getWidth(), attachment.getHeight());

    }
}
