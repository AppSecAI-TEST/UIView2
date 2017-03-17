package com.hn.d.valley.main.message.groupchat;

/**
 * Created by hewking on 2017/3/16.
 */
public interface RequestCallback<T> {

    void onStart();

    void onSuccess(T t);

    void onError(String msg);

}
