package com.hn.d.valley.control;

import android.text.TextUtils;

import io.realm.RealmObject;

/**
 * 语音动态
 */
public class VoiceStatusInfo extends RealmObject {
    public static final String NOPIC = "nopic";
    private String voiceImagePath, voicePath, voiceTitle;

    public VoiceStatusInfo() {
    }

    public VoiceStatusInfo(String voiceImagePath, String voicePath) {
        this(voiceImagePath, voicePath, "");
    }

    public VoiceStatusInfo(String voiceImagePath, String voicePath, String voiceTitle) {
        if (TextUtils.isEmpty(voiceImagePath)) {
            this.voiceImagePath = NOPIC;
        } else {
            this.voiceImagePath = voiceImagePath;
        }
        this.voicePath = voicePath;
        this.voiceTitle = voiceTitle;
    }

    public VoiceStatusInfo(String voicePath) {
        this(NOPIC, voicePath);
    }

    public boolean isNoPic() {
        return NOPIC.equals(voiceImagePath);
    }

    public String getVoiceImagePath() {
        return voiceImagePath;
    }

    public void setVoiceImagePath(String voiceImagePath) {
        this.voiceImagePath = voiceImagePath;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    public String getVoiceTitle() {
        return voiceTitle;
    }

    public void setVoiceTitle(String voiceTitle) {
        this.voiceTitle = voiceTitle;
    }
}
