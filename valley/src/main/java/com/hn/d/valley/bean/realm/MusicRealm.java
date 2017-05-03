package com.hn.d.valley.bean.realm;

import io.realm.RealmObject;

public class MusicRealm extends RealmObject {
    /**
     * song_id : qq_107192078
     * name : 告白气球
     * time : 215
     * signer : 周杰伦
     * album : 周杰伦的床边故事
     * thumb : http://imgcache.qq.com/music/photo/album_300/91/300_albumpic_1458791_0.jpg
     * mp3 : http://ws.stream.qqmusic.qq.com/107192078.m4a?fromtag=46
     */

    private String song_id;
    private String name;
    private String time;
    private String signer;
    private String album;
    private String thumb;
    private String mp3;

    /**
     * 本地路径
     */
    private String filePath;

    public String getSong_id() {
        return song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getMp3() {
        return mp3;
    }

    public void setMp3(String mp3) {
        this.mp3 = mp3;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}