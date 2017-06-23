package com.hn.d.valley.bean;

import java.util.List;

public class DynamicsBean {
    private List<String> words;
    private List<LikeUserInfoBean> dynamics;

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public List<LikeUserInfoBean> getDynamics() {
        return dynamics;
    }

    public void setDynamics(List<LikeUserInfoBean> dynamics) {
        this.dynamics = dynamics;
    }

}