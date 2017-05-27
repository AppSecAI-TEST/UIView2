package com.hn.d.valley.main.message.session;

import android.media.MediaPlayer;
import android.net.Uri;

import com.angcyo.uiview.utils.string.MD5;
import com.angcyo.uiview.view.UIIViewImpl;
import com.hn.d.valley.R;
import com.hn.d.valley.base.iview.VideoRecordUIView;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

import rx.functions.Action3;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/19 9:40
 * 修改人员：hewking
 * 修改时间：2017/05/19 9:40
 * 修改备注：
 * Version: 1.0.0
 */
public class VideoCommandItem extends CommandItemInfo {

    public VideoCommandItem() {
        this(R.drawable.paise_xiaoxi, "视频");
    }

    public VideoCommandItem(int icoResId, String text) {
        super(icoResId, text);
    }

    @Override
    protected void onClick() {
        //视频
        getContainer().mLayout.startIView(new VideoRecordUIView(new Action3<UIIViewImpl, String, String>() {
            @Override
            public void call(UIIViewImpl view, String s, String s2) {

                view.finishIView();

                File file = new File(s2);
                if (!file.exists()) {
                    return;
                }

                MediaPlayer mediaPlayer = getVideoMediaPlayer(file);
                long duration = mediaPlayer == null ? 0 : mediaPlayer.getDuration();
                int height = mediaPlayer == null ? 0 : mediaPlayer.getVideoHeight();
                int width = mediaPlayer == null ? 0 : mediaPlayer.getVideoWidth();
                String md5 = MD5.getStreamMD5(s2);
                IMMessage message = MessageBuilder.createVideoMessage(getContainer().account, getContainer().sessionType, file, duration, width, height, md5);
                getContainer().proxy.sendMessage(message);

            }
        }));
    }

    /**
     * 获取视频mediaPlayer
     *
     * @param file 视频文件
     * @return mediaPlayer
     */
    private MediaPlayer getVideoMediaPlayer(File file) {
        try {
            return MediaPlayer.create(getContainer().activity, Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
