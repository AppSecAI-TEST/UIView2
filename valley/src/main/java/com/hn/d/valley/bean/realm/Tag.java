package com.hn.d.valley.bean.realm;

import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Tag extends RealmObject {

    /**
     * id : 1
     * name : 搞笑
     */

    private String id;
    private String name;


    //举报消息的内容
    /**
     * content : 垃圾营销
     */

    private String content;
    /**
     * "attention_count": "4"
     */
    private String attention_count;
    /**
     * thumb : http://avatorimg.klgwl.com/13/13584.png
     */

    private String thumb;

    public Tag(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Tag() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return TextUtils.equals(id, ((Tag) obj).id);
    }

    public String string() {
        return id + "->" + name;
    }

    @Override
    public String toString() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttention_count() {
        return attention_count;
    }

    public void setAttention_count(String attention_count) {
        this.attention_count = attention_count;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
