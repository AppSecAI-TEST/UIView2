package com.hn.d.valley.bean.realm;

import io.realm.RealmObject;

public class NewestDiscussPicBean extends RealmObject {
    /**
     * media_type : 3
     * url : http://circleimg.klgwl.com/6500171482920243.642439
     */

    private int media_type;
    private String url;

    public int getMedia_type() {
        return media_type;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}