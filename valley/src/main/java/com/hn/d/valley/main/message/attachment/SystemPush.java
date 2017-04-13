package com.hn.d.valley.main.message.attachment;

/**
 * Created by Administrator on 2017/4/12.
 */

public class SystemPush extends BaseCustomMsg {


    /**
     * created : 1491977919
     * msg : 多图 加 标题
     * tpl_type : 3
     */

    private int created;
    private String msg;
    private String tpl_type;

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTpl_type() {
        return tpl_type;
    }

    public void setTpl_type(String tpl_type) {
        this.tpl_type = tpl_type;
    }
}
