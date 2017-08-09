package com.hn.d.valley.main.message.audio;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/8/9
 * 修改人员：cjh
 * 修改时间：2017/8/9
 * 修改备注：
 * Version: 1.0.0
 */
public class AudioRecordManager {

    private AudioRecordManager(){}

    private static class Holder{
        private  static AudioRecordManager intance = new AudioRecordManager();
    }

    public static AudioRecordManager getInstance() {
        return Holder.intance;
    }

//    // 获得声音的level
//    public int getVoiceLevel(int maxLevel) {
//        // mRecorder.getMaxAmplitude()这个是音频的振幅范围，值域是1-32767
//        if (isPrepared) {
//            try {
//                // 取证+1，否则去不到7
//                return maxLevel * mRecorder.getMaxAmplitude() / 32768 + 1;
//            } catch (Exception e) {
//
//            }
//        }
//
//        return 1;
//    }

    /** 音量改变的监听器 */
    public interface OnVolumeChangeListener {
        void onVolumeChange(int volume);
    }
    public interface OnFinishedRecordListener {
        /** 用户手动取消 */
        void onCancleRecord();
        /** 录音完成 */
        void onFinishedRecord(String audioPath, int recordTime);
    }

}
