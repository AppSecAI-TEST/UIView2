package com.example.m3b.audiocachedemo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;
import android.util.Log;

import com.example.m3b.ThreadExecutor;
import com.example.m3b.audiocachedemo.utils.MediaPlayerProxy;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class Player implements OnBufferingUpdateListener,
        OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener {
    public MediaPlayer mediaPlayer;
    public int mbufferingProgress;
    MediaPlayerProxy proxy;
    private boolean USE_PROXY = true;
    private String mUrl = "";
    private boolean isPause = false;

    private Set<OnPlayListener> mPlayListeners = new HashSet<>();

    public Player() {

        ensureMediaPlayer();

        proxy = new MediaPlayerProxy();
        proxy.init();
        proxy.start();
    }

    public void addOnPlayListener(OnPlayListener listener) {
        mPlayListeners.add(listener);
    }

    public void removeOnPlayListener(OnPlayListener listener) {
        mPlayListeners.remove(listener);
    }

    protected void ensureMediaPlayer() {
        if (mediaPlayer != null) {
            return;
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }
    }


    public boolean isPlaying(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return TextUtils.equals(mUrl, url) && !isPause;
    }

    public boolean isPause() {
        return isPause;
    }

    public void play() {
        mediaPlayer.start();
        playStart(mUrl);
    }

    public void playUrl(String url, boolean userProxy) {

        ensureMediaPlayer();

        if (TextUtils.equals(mUrl, url)) {
            if (isPause) {
                play();
            }
            return;
        }

        String proxyUrl = url;

        if (userProxy) {
            startProxy();
            proxyUrl = proxy.getProxyURL(url);
        }

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(proxyUrl);
            long p = System.currentTimeMillis();
            Log.e("P", String.valueOf(p));
            mediaPlayer.prepare();
            long s = System.currentTimeMillis();
            Log.e("S", String.valueOf(s) + " " + (p - s));
            mediaPlayer.start();

            long x = System.currentTimeMillis();
            Log.e("X", String.valueOf(x) + " " + (x - s));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playStart(url);
    }

    public void pause() {
        isPause = true;
        mediaPlayer.pause();
        for (OnPlayListener listener : mPlayListeners) {
            listener.onPlay(mUrl, true);
        }
    }

    public void stop() {
        playEnd();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    /**
     * 通过onPrepared播放
     */
    public void onPrepared(MediaPlayer arg0) {
        Log.e("mediaPlayer", "onPrepared");
        arg0.start();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        Log.e("mediaPlayer", "onCompletion");
        playEnd();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        int currentProgress = getCurrentPosition() / getDuration();
        this.mbufferingProgress = bufferingProgress;
        Log.e(currentProgress + "% play", bufferingProgress + "% buffer");
    }

    private void startProxy() {
        if (proxy == null) {
            proxy = new MediaPlayerProxy();
            proxy.init();
            proxy.start();
        }
    }

    public int getDuration() {
        return this.mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return this.mediaPlayer.getCurrentPosition();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        playEnd();
        return false;
    }

    private void playEnd() {
        final String url = mUrl;
        mUrl = "";
        isPause = false;
        ThreadExecutor.instance().onMain(new Runnable() {
            @Override
            public void run() {
                for (OnPlayListener listener : mPlayListeners) {
                    listener.onPlayEnd(url);
                }
            }
        });
    }

    private void playStart(final String url) {
        isPause = false;
        mUrl = url;

        ThreadExecutor.instance().onMain(new Runnable() {
            @Override
            public void run() {
                for (OnPlayListener listener : mPlayListeners) {
                    listener.onPlay(url, false);
                }
            }
        });
    }

    public interface OnPlayListener {
        void onPlay(String url, boolean isPause);

        void onPlayEnd(String url);
    }
}
