package com.hn.d.valley.realm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/24 9:13
 * 修改人员：Robi
 * 修改时间：2016/12/24 9:13
 * 修改备注：
 * Version: 1.0.0
 */
public class RRealm {

    /**
     * 同步的方式保存一个realm对象
     */
    public static <R extends RealmObject> void save(R object) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(object);
        realm.commitTransaction();
    }


    /**
     * 同步的方式保存一组realm对象
     */
    public static <R extends RealmObject> void save(Iterable<R> objects) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(objects);
        realm.commitTransaction();
    }

    /**
     * 同步执行,并出现错误自动回滚事务
     */
    public static void exe(final Realm.Transaction transaction) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(transaction);
    }

    /**
     * 异步执行,并出现错误自动回滚事务
     */
    public static void async(final Realm.Transaction transaction) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(transaction);
    }

    /**
     * 异步执行,并出现错误自动回滚事务
     */
    public static void async(final Realm.Transaction transaction, final Realm.Transaction.OnSuccess onSuccess) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(transaction, onSuccess);
    }

    /**
     * 异步执行,并出现错误自动回滚事务
     */
    public static void async(final Realm.Transaction transaction, final Realm.Transaction.OnSuccess onSuccess, final Realm.Transaction.OnError onError) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(transaction, onSuccess, onError);
    }

    public static <E extends RealmObject> RealmQuery<E> where(Class<E> clazz) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(clazz);
    }
}
