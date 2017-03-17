package com.hn.d.valley.bean.event;

/**
 * Created by hewking on 2017/3/16.
 */
public class SelectedUserNumEvent {

    private int num;

    public SelectedUserNumEvent(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
