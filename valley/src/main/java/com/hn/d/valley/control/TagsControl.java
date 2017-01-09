package com.hn.d.valley.control;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.sub.user.service.SocialService;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：标签控制
 * 创建人员：Robi
 * 创建时间：2017/01/09 16:13
 * 修改人员：Robi
 * 修改时间：2017/01/09 16:13
 * 修改备注：
 * Version: 1.0.0
 */
public class TagsControl {

    public static void getTags(final Action1<List<Tag>> listAction1) {
        RealmResults<Tag> results = RRealm.where(Tag.class).findAll();
        if (results.size() != 0 && listAction1 != null) {
            List<Tag> tags = new ArrayList<>();
            for (Tag t : results) {
                Tag tag = new Tag();
                tag.setId(t.getId());
                tag.setName(t.getName());
                tags.add(tag);
            }
            listAction1.call(tags);
        }

        RRetrofit.create(SocialService.class)
                .getTags(Param.buildMap())
                .compose(Rx.transformerList(Tag.class))
                .subscribe(new BaseSingleSubscriber<List<Tag>>() {
                    @Override
                    public void onNext(List<Tag> tags) {
                        /**先清空*/
                        final RealmResults<Tag> results = RRealm.where(Tag.class).findAll();

                        /**后保存*/
                        Realm realm = RRealm.getRealm();
                        realm.beginTransaction();
                        results.deleteAllFromRealm();
                        for (Tag tag : tags) {
                            realm.copyToRealm(tag);
                        }
                        realm.commitTransaction();
                    }
                });
    }
}
