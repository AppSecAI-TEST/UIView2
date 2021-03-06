package com.m3b.rbaudiomixlibrary;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/09 09:36
 * 修改人员：Robi
 * 修改时间：2017/05/09 09:36
 * 修改备注：
 * Version: 1.0.0
 */
public class Record {

    private static MediaPlayer mPlayer;
    private final AudioRecorderNative _AudioMix;
    RecordListener mRecordListener;
    long startRecordTime = 0l, endRecordTime = 0l;//开始录制的时间, 用来计算总共录制时长
    private MusicPlayer mMusicPlayer;
    private boolean recording;
    /**
     * mix时背景音乐的音量
     */
    private float fMusicGain = 0.5f;
    /**
     * mix时mic的音量
     */
    private float fMicGain = 1.0f;
    //不录制mic
    private boolean noMic = false;
    //不录制music
    private boolean noMusic = false;
    private String bgmMusicPath;
    /**
     * 背景音乐循环播放
     */
    private boolean mBackMusicLoop = true;
    private FileOutputStream aacDataOutStream = null;
    /**
     * 当插入耳机时, 需要设置为true
     */
    private boolean isMixBgm = false;
    private String mRecordFilePath;

    private Record() {
        _AudioMix = new AudioRecorderNative();
        _AudioMix.NativeInit();
    }

    public static Record instance() {
        return Holder.instance;
    }

    public static void playFile(String filePath) {
        try {
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
            } else {
                mPlayer.reset();
            }
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param bgmPath 如果背景音乐路径为空, 表示没有
     */
    public void startRecord(String bgmPath, String recordFilePath, RecordListener recordListener) {
        String[] cpuInfo = getCpuInfo();

        if (recording) {
            return;
        }

        mRecordListener = recordListener;
        try {
            bgmMusicPath = bgmPath;
            mRecordFilePath = recordFilePath;
            aacDataOutStream = new FileOutputStream(recordFilePath);

            recording = true;
            if (new File(bgmMusicPath).exists()) {
                noMusic = false;
            } else {
                noMusic = true;
            }

            startRecordTime = System.currentTimeMillis();

            if (recordListener != null) {
                recordListener.onRecordStart(bgmMusicPath, mRecordFilePath);
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!noMusic) {
                        mMusicPlayer = new MusicPlayer(bgmMusicPath);
                        mMusicPlayer.setVol(fMusicGain);
                        mMusicPlayer.startPlayBackMusic();
                    }
                    StartMixThread();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            stopRecord();
            if (recordListener != null) {
                recordListener.onRecordError(e);
            }
        }
    }

    public String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return cpuInfo;
    }

    public void stopRecord() {
        recording = false;
        endRecordTime = System.currentTimeMillis();
        if (mMusicPlayer != null) {
            mMusicPlayer.release();
        }
    }

    public Record setMusicVol(float fMusicGain) {
        this.fMusicGain = fMusicGain;
        return this;
    }

    public Record setVoiceVol(float fMicGain) {
        this.fMicGain = fMicGain;
        return this;
    }

    public Record setMixBgm(boolean mixBgm) {
        isMixBgm = mixBgm;
        return this;
    }

    /**
     * @param noMic true 没有人声
     */
    public void setNoMic(boolean noMic) {
        this.noMic = noMic;
    }

    /**
     * @param noMusic true 没有音乐
     */
    public void setNoMusic(boolean noMusic) {
        this.noMusic = noMusic;
    }

    private void StartMixThread() {
        Thread MixEncodeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

                final int _iSampleRateDef = 16000;//44100;//do not modify,16000 is good for meizu phone

                final int _iRecorderBufferSize = AudioRecord.getMinBufferSize(_iSampleRateDef,
                        AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT) * 8;
                final AudioRecord _AudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        _iSampleRateDef, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT, _iRecorderBufferSize);

                final byte[] _RecorderBuffer = new byte[_iRecorderBufferSize];

                while (recording && !checkExit()) {

                    //开始录制mic声音
                    _AudioRecorder.startRecording();

                    byte[] result = null;

                    //读取mic数据
                    int iMicLen = _AudioRecorder.read(_RecorderBuffer, 0, _RecorderBuffer.length); // Fill buffer

                    if (iMicLen != AudioRecord.ERROR_BAD_VALUE) {

                        _AudioMix.SetMusicVol(fMusicGain);
                        _AudioMix.SetVoiceVol(fMicGain);

                        if (noMic && !noMusic) {

                            byte[] bgm = mMusicPlayer.getBackGroundBytes();

                            if (bgm != null) {
                                result = _AudioMix.MusicEncode(44100, 2, bgm, bgm.length);
                                showAmplitude(bgm, bgm.length, fMusicGain);
                                drawWavefrom(_RecorderBuffer);
                            } else {
                                if (!mMusicPlayer.isPlayingMusic() && mMusicPlayer.isPCMDataEos() && mBackMusicLoop) {
                                    mMusicPlayer.release();
                                    mMusicPlayer.startPlayBackMusic();
                                }
                                continue;
                            }
                        } else if (!noMic && noMusic) {
                            result = _AudioMix.VoiceEncode(_iSampleRateDef, 2, _RecorderBuffer, iMicLen);
                            showAmplitude(_RecorderBuffer, iMicLen, fMicGain);
                            drawWavefrom(_RecorderBuffer);
                        } else if (!noMic && !noMusic) {
                            byte[] bgm = null;
                            if (mMusicPlayer != null) {
                                bgm = mMusicPlayer.getBackGroundBytes();
                            }

                            if (bgm != null)
                                showAmplitude(bgm, bgm.length, fMusicGain);

                            if (bgm != null && isMixBgm != false) {
                                Log.e("valley", "call: run([])-> 1");
                                _AudioMix.VoiceMixEncode(_iSampleRateDef, 2, _RecorderBuffer, iMicLen);
                                result = _AudioMix.MusicMixEncode(44100, 2, bgm, bgm.length);
                                showAmplitude(_RecorderBuffer, iMicLen, fMicGain);
                                showAmplitude(bgm, bgm.length, fMusicGain);
                                drawWavefrom(_RecorderBuffer);
                            } else if (bgm == null && mMusicPlayer != null && mMusicPlayer.isPCMDataEos() && mBackMusicLoop) {
                                Log.i("@@@@@@@@@@@@", "music finish.....");
                                mMusicPlayer.release();
                                mMusicPlayer.startPlayBackMusic();
                            } else {
                                Log.e("valley", "call: run([])-> 2");
                                result = _AudioMix.VoiceEncode(_iSampleRateDef, 2, _RecorderBuffer, iMicLen);
                                showAmplitude(_RecorderBuffer, iMicLen, fMicGain);
                                drawWavefrom(_RecorderBuffer);
                            }


                        } else {
                            result = null;
                        }

                        if (result != null && aacDataOutStream != null) {
                            try {
                                aacDataOutStream.write(result);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    }

                    if (mRecordListener != null) {
                        mRecordListener.onRecordTimeChanged(getTime());
                    }
                }
                //while end

                _AudioRecorder.stop();
                _AudioRecorder.release();

                if (aacDataOutStream != null) {
                    try {
                        aacDataOutStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (mRecordListener != null) {
                    mRecordListener.onRecordStop(bgmMusicPath, mRecordFilePath, getTime());
                }
            }
        });
        MixEncodeThread.start();
    }

    private boolean checkExit() {
        if (recording) {
            return false;
        } else {
            return getTime() % 1000 > 800;
        }
    }

    public boolean isRecording() {
        return recording;
    }

    private long getTime() {
        return System.currentTimeMillis() - startRecordTime;
    }


    /**
     * 声音的振幅, 0-~
     */
    private void showAmplitude(byte[] sound, int len, float gain) {
        //
        double sum = 0;
        int size = sound.length;

        for (int i = 0; i < size; i += 2) {
            // convert byte pair to int
            short buf1 = sound[i + 1];
            short buf2 = sound[i];

            buf1 = (short) ((buf1 & 0xff) << 8);
            buf2 = (short) (buf2 & 0xff);

            short res = (short) (buf1 | buf2);
            sum += res * res * gain;
        }

        final double amplitude = sum / len;
        if (mRecordListener != null) {
            mRecordListener.onAmplitudeChanged((int) Math.sqrt(amplitude));
        }
    }

    private void drawWavefrom(byte[] _RecorderBuffer) {
        if (noMic) {
            return;
        }

        short[] shorts = new short[_RecorderBuffer.length / 2];
        ByteBuffer.wrap(_RecorderBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        if (mRecordListener != null) {
            mRecordListener.onAudioDataChanged(shorts, _RecorderBuffer.length / 2, false);
        }
    }

    public interface RecordListener {
        void onRecordStart(String bgmPath, String filePath);

        void onRecordStop(String bgmPath, String filePath, long time);

        void onRecordError(Exception error);

        void onRecordTimeChanged(long millis);


        /**
         * 录制声音的振幅
         */
        void onAmplitudeChanged(int amplitude);

        /**
         * 用来绘制声音波形图
         */
        void onAudioDataChanged(short[] buffer, int readsize, boolean mformRight);
    }

    private static class Holder {
        static Record instance = new Record();
    }
}
