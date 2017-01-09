package com.hn.d.valley.realm;

import com.angcyo.library.utils.L;
import com.hn.d.valley.ValleyApp;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmSchema;

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
        Realm realm = getRealm();
        try {
            realm.beginTransaction();
            realm.copyToRealm(object);
            realm.commitTransaction();
        } finally {
            realm.close();
        }

        L.i("保存至数据库:" + object.toString());
    }

    public static Realm getRealm() {
        Realm.getDefaultInstance().close();
        return Realm.getDefaultInstance();
    }


    /**
     * 同步的方式保存一组realm对象
     */
    public static <R extends RealmObject> void save(Iterable<R> objects) {
        Realm realm = getRealm();
        try {
            realm.beginTransaction();
            realm.copyToRealm(objects);
            realm.commitTransaction();
        } finally {
            realm.close();
        }
    }

    /**
     * 同步执行,并出现错误自动回滚事务
     */
    public static void exe(final Realm.Transaction transaction) {
        Realm realm = getRealm();
        try {
            realm.executeTransaction(transaction);
        } finally {
            realm.close();
        }
    }

    /**
     * 异步执行,并出现错误自动回滚事务
     */
    public static void async(final Realm.Transaction transaction) {
        Realm realm = getRealm();
        realm.executeTransactionAsync(transaction);
    }

    /**
     * 异步执行,并出现错误自动回滚事务
     */
    public static void async(final Realm.Transaction transaction, final Realm.Transaction.OnSuccess onSuccess) {
        Realm realm = getRealm();
        realm.executeTransactionAsync(transaction, onSuccess);
    }

    /**
     * 异步执行,并出现错误自动回滚事务
     */
    public static void async(final Realm.Transaction transaction, final Realm.Transaction.OnSuccess onSuccess, final Realm.Transaction.OnError onError) {
        Realm realm = getRealm();
        realm.executeTransactionAsync(transaction, onSuccess, onError);
    }

    public static <E extends RealmObject> RealmQuery<E> where(Class<E> clazz) {
        Realm realm = getRealm();
        return realm.where(clazz);
    }

    /**
     * 初始化
     */
    public static void init(ValleyApp valleyApp) {
        Realm.init(valleyApp);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("valley.realm")
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        L.e("数据库升级:" + oldVersion + "->" + newVersion);
                        RealmSchema schema = realm.getSchema();
//                        if (oldVersion == 0) {
//                            schema.create("Person")
//                                    .addField("name", String.class)
//                                    .addField("age", int.class);
//                            oldVersion++;
//                        }
//
//                        if (oldVersion == 1) {
//                            schema.get("Person")
//                                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
//                                    .addRealmObjectField("favoriteDog", schema.get("Dog"))
//                                    .addRealmListField("dogs", schema.get("Dog"));
//                            oldVersion++;
//                        }
                    }
                })
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
