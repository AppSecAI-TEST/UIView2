package com.hn.d.valley.bean;

import java.util.List;

public class NewsBean {
    private List<String> words;
    private List<HotInfoListBean> news;

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public List<HotInfoListBean> getNews() {
        return news;
    }

    public void setNews(List<HotInfoListBean> news) {
        this.news = news;
    }
}