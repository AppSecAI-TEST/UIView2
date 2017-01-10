package com.hn.d.valley.control;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.github.all.base.adapter.ViewGroupUtils;
import com.angcyo.uiview.github.all.base.adapter.adapter.cache.BaseCacheAdapter;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.widget.RTextCheckView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.sub.user.service.SocialService;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action1;
import rx.functions.Action2;

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
        RealmResults<Tag> results = RRealm.realm().where(Tag.class).findAll();
        int size = results.size();
        L.i("数据库中标签数量:" + size);
        if (size != 0 && listAction1 != null) {
            List<Tag> tags = new ArrayList<>();
            for (Tag t : results) {
                Tag tag = new Tag();
                tag.setId(t.getId());
                tag.setName(t.getName());
                tags.add(tag);
                L.i("->:" + tag.string());
            }
            listAction1.call(tags);
        }

        RRetrofit.create(SocialService.class)
                .getTags(Param.buildMap())
                .compose(Rx.transformerList(Tag.class))
                .subscribe(new BaseSingleSubscriber<List<Tag>>() {
                    @Override
                    public void onNext(final List<Tag> tags) {
                        if (tags.isEmpty()) {
                            return;
                        }

                        /**先清空*/
                        final RealmResults<Tag> results = RRealm.realm().where(Tag.class).findAll();

                        /**后保存*/
                        RRealm.exe(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                results.deleteAllFromRealm();

                                for (Tag tag : tags) {
                                    realm.copyToRealm(tag);
                                }
                            }
                        });
                    }
                });
    }

    public static void inflate(final AppCompatActivity activity, final ViewGroup rootLayout, final Action2<RTextCheckView, Tag> onAddView) {
        TagsControl.getTags(new Action1<List<Tag>>() {
            @Override
            public void call(List<Tag> tags) {
                if (tags.isEmpty()) {
                    T_.show(activity.getString(R.string.fetch_tag_failed));
                    return;
                }

                ViewGroupUtils.addViews(rootLayout, new BaseCacheAdapter<Tag>(activity, tags) {

                    @Override
                    public View getView(ViewGroup parent, int pos, Tag data) {
                        RTextCheckView checkView = new RTextCheckView(activity);
                        checkView.setBackgroundResource(R.drawable.base_dark_color_border_check_selector);
                        checkView.setText(data.getName());
                        checkView.setTextColor(activity.getResources().getColorStateList(R.color.base_dark_to_main_color_selector));
                        checkView.setTag(data);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
                        params.leftMargin = (int) ResUtil.dpToPx(activity, 20);
                        params.bottomMargin = (int) ResUtil.dpToPx(activity, 20);
                        checkView.setLayoutParams(params);

                        if (onAddView != null) {
                            onAddView.call(checkView, data);
                        }
                        return checkView;
                    }
                });
            }
        });
    }
}
