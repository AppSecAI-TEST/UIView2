package com.hn.d.valley.control;

import com.hn.d.valley.realm.RRealm;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：草稿箱控制器
 * 创建人员：Robi
 * 创建时间：2017/05/16 16:07
 * 修改人员：Robi
 * 修改时间：2017/05/16 16:07
 * 修改备注：
 * Version: 1.0.0
 */
public class DraftControl {
    public static void getDraft(final OnDraftListener listener) {
        RRealm.where(new Action1<Realm>() {
            @Override
            public void call(Realm realm) {
                RealmResults<PublishTaskRealm> taskRealms = realm.where(PublishTaskRealm.class).findAll();
                if (listener != null) {
                    listener.onDraft(taskRealms);
                }
            }
        });
    }

    public static abstract class OnDraftListener {
        protected void onDraft(RealmResults<PublishTaskRealm> taskRealms) {

        }
    }
}
