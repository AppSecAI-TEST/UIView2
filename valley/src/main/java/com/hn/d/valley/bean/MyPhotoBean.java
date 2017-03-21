package com.hn.d.valley.bean;

/**
 * Created by hewking on 2017/3/20.
 */
public class MyPhotoBean {


    /**
     * media : http://klg-circleimg.oss-cn-shenzhen.aliyuncs.com/50500/50500110.jpeg_s_1200x795,http://klg-circleimg.oss-cn-shenzhen.aliyuncs.com/50500/50500130.jpeg_s_1920x1200,http://klg-circleimg.oss-cn-shenzhen.aliyuncs.com/50500/50500675.jpeg_s_1280x657,http://klg-circleimg.oss-cn-shenzhen.aliyuncs.com/50500/50500233.jpeg_s_1920x1080,http://klg-circleimg.oss-cn-shenzhen.aliyuncs.com/50500/50500832.jpeg_s_2715x1841
     * media_type : 3
     * created : 1488944683
     * content :
     */

    private String media;
    private String media_type;
    private String created;
    private String content;

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
