package com.hn.d.valley.bean;

/**
 * Created by Administrator on 2017/2/27.
 */

public class EntityResponse<T> {

    private String result;

    private T data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
