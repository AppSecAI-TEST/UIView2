package com.hn.d.valley.control;

import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import com.angcyo.uiview.Root;
import com.angcyo.uiview.utils.ThreadExecutor;
import com.angcyo.uiview.widget.RDownloadView;
import com.example.m3b.Audio;
import com.hn.d.valley.bean.realm.MusicRealm;
import com.hn.d.valley.realm.RRealm;
import com.liulishuo.FDown;
import com.liulishuo.FDownListener;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/05 10:14
 * 修改人员：Robi
 * 修改时间：2017/05/05 10:14
 * 修改备注：
 * Version: 1.0.0
 */
public class MusicControl {

    static List<MusicRealm> sMusicRealmList = new ArrayList<>();

    public static String generateFilePath(String name) {
        String musicFolder = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator +
                Root.APP_FOLDER +
                File.separator +
                "music";
        new File(musicFolder).mkdirs();
        return musicFolder + File.separator + name + ".mp3";
    }

    public static String generateApkFilePath(String name) {
        String musicFolder = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator +
                Root.APP_FOLDER +
                File.separator +
                "apk";
        new File(musicFolder).mkdirs();
        return musicFolder + File.separator + name;
    }

    /**
     * 返回已经下载好的音乐
     */
    public static void loadMusic(final Action1<List<MusicRealm>> listAction) {
        RRealm.exe(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MusicRealm> musicRealms = realm.where(MusicRealm.class).findAll();
                List<MusicRealm> realms = new ArrayList<>();

                for (MusicRealm r : musicRealms) {
                    if (new File(r.getFilePath()).exists() && !realms.contains(r)) {
                        realms.add(r);
                    }
                }

                sMusicRealmList.clear();
                sMusicRealmList.addAll(realms);

                if (listAction != null) {
                    listAction.call(realms);
                }
            }
        });
    }

    @Deprecated
    public static boolean isDowned(String id) {
        for (MusicRealm realm : sMusicRealmList) {
            if (TextUtils.equals(realm.getSong_id(), id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否已经下载
     */
    public static boolean isDowned(final MusicRealm music) {
        for (MusicRealm realm : sMusicRealmList) {
            if (TextUtils.equals(realm.getSong_id(), music.getSong_id())) {
                music.setFilePath(realm.getFilePath());
                return true;
            }
        }
        return false;
    }

    public static void initDownView(final MusicRealm music, final WeakReference<RDownloadView> downViewWeak) {
        RDownloadView downView = downViewWeak.get();
        if (isDowned(music)) {
            downView.setDownloadState(RDownloadView.DownloadState.FINISH);
            downView.setOnClickListener(null);
        } else {
            int status = FDown.getStatusIgnoreCompleted(music.getMp3(), generateFilePath(music.getName()));
            //L.e("call: initDownView([music, downViewWeak])-> " + music.getName() + " " + status);
            if (status == FileDownloadStatus.progress) {
                downView.setDownloadState(RDownloadView.DownloadState.DOWNING);
                downView.setOnClickListener(null);
            } else {
                downView.setDownloadState(RDownloadView.DownloadState.NORMAL);
                downView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RDownloadView downloadView = downViewWeak.get();
                        if (downloadView != null) {
                            downloadView.setDownloadState(RDownloadView.DownloadState.DOWNING);
                        }

                        FDown.build(music.getMp3())
                                .setFullPath(generateFilePath(music.getName()))
                                .download(new FDownListener() {
                                    @Override
                                    protected void onCompleted(BaseDownloadTask task) {
                                        super.onCompleted(task);

                                        MusicRealm musicRealm = new MusicRealm(music);
                                        musicRealm.setFilePath(task.getPath());

                                        RRealm.save(musicRealm);

                                        RDownloadView downloadView = downViewWeak.get();
                                        if (downloadView != null && TextUtils.equals(String.valueOf(downloadView.getTag()), task.getUrl())) {
                                            downloadView.setDownloadState(RDownloadView.DownloadState.FINISH);
                                        }

                                        loadMusic(null);
                                    }
                                });
                    }
                });
            }
        }
    }

    /**
     * @param mp3 是否正在播放指定的音乐
     */
    public static boolean isPlaying(String mp3) {
        return Audio.instance().isPlaying(mp3);
    }

    /**
     * 暂停播放
     */
    public static void pausePlay(String mp3) {
        Audio.instance().pause();
    }

    /**
     * 播放mp3
     */
    public static void play(final String mp3) {
        ThreadExecutor.instance().onThread(new Runnable() {
            @Override
            public void run() {
                Audio.instance().play(mp3);
            }
        });
    }
}
