package com.hn.d.valley.main.message.audio;

/**
 * Created by hewking on 2017/3/3.
 */
public class AudioRecordPlayable implements Playable {

    private String path;
    private long duration;


    public AudioRecordPlayable(String path,long duration){
        this.path = path;
        this.duration = duration;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public String getPath() {
        if (path == null ) {
            return "";
        }
        return path;
    }

    @Override
    public boolean isAudioEqual(Playable audio) {
        return false;
    }
}
