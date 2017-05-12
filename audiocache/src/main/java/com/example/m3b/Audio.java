package com.example.m3b;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.example.m3b.audiocachedemo.Player;
import com.example.m3b.audiocachedemo.PreLoad;

import java.io.File;

import static com.example.m3b.audiocachedemo.utils.Tools.generateTime;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/12 11:18
 * 修改人员：Robi
 * 修改时间：2017/05/12 11:18
 * 修改备注：
 * Version: 1.0.0
 */
public class Audio {

    public static Context sContext;
    private final Player mPlayer;


    private Audio() {
        mPlayer = new Player();
    }

    public static void init(Application application) {
        sContext = application;
    }

    public static Audio instance() {
        return Holder.instance;
    }

    public void addOnPlayListener(Player.OnPlayListener listener) {
        mPlayer.addOnPlayListener(listener);
    }

    public void removeOnPlayListener(Player.OnPlayListener listener) {
        mPlayer.removeOnPlayListener(listener);
    }

    /**
     * 预加载
     */
    public void preLoad(String url) {
        PreLoad load = new PreLoad(url);
        load.download(300 * 1000);//预加载大小
    }

    /**
     * 可以播放本地音频, 和在线音频, 在线缓存
     */
    public void play(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mPlayer.playUrl(url, !new File(url).exists());
    }

    public void pause() {
        mPlayer.pause();
    }

    public void stop() {
        mPlayer.stop();
    }

    /**
     * 返回当前播放时长, 毫秒
     */
    public int getCurrentPosition(String url) {
        if (isPause() || isPlaying(url)) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 返回当前播放总时长, 毫秒
     */
    public int getDuration(String url) {
        if (isPause() || isPlaying(url)) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 00:00:00格式
     */
    public String getCurrentPositionString(String url) {
        return generateTime(getCurrentPosition(url));
    }

    /**
     * 00:00:00格式
     */
    public String getDurationString(String url) {
        return generateTime(getDuration(url));
    }

    /**
     * 是否正在播放
     */
    public boolean isPlaying(String url) {
        return mPlayer.isPlaying(url);
    }

    public boolean isPause() {
        return mPlayer.isPause();
    }

    private static class Holder {
        static Audio instance = new Audio();
    }

}
