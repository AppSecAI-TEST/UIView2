package com.hn.d.valley.control;

import com.angcyo.uiview.net.RRetrofit;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.Transform;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.TagsListBean;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.sub.user.service.SocialService;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import rx.functions.Func1;

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

    public static void getTags() {
        RRetrofit.create(SocialService.class)
                .getTags(Param.buildMap())
                .compose(Transform.defaultStringSchedulers(TagsListBean.class))
                .map(new Func1<TagsListBean, Boolean>() {
                    @Override
                    public Boolean call(TagsListBean tagsListBean) {
                        if (tagsListBean.getData().isEmpty()) {
                            return false;
                        }
                        Realm realm = RRealm.getRealm();
                        realm.beginTransaction();
                        for (TagsControl.Tag tag : tagsListBean.getData()) {
                            realm.copyToRealm(tag);
                        }
                        realm.commitTransaction();
                        return true;
                    }
                })
                .subscribe(new BaseSingleSubscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }


    public static class Tag extends RealmObject {

        /**
         * id : 1
         * name : 搞笑
         */

        @PrimaryKey
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
