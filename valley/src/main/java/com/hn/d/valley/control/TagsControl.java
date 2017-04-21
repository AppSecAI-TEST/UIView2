package com.hn.d.valley.control;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.angcyo.uiview.github.all.base.adapter.ViewGroupUtils;
import com.angcyo.uiview.github.all.base.adapter.adapter.cache.BaseCacheAdapter;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.RTextCheckView;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.DiscussService;
import com.orhanobut.hawk.Hawk;

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

    public static List<Tag> cacheTags;
    /**
     * 推荐标签
     */
    public static Tag recommendTag;
    private static List<Tag> sAllTags = new ArrayList<>();
    private static List<Tag> sMyTags = new ArrayList<>();

    static {
        recommendTag = new Tag();
        recommendTag.setId("0");
        recommendTag.setName(ValleyApp.getApp().getResources().getString(R.string.tag_recommend));
    }

    /**
     * 缓存包括 推荐在内的所有标签, 并且返回过滤后的自己的标签
     */
    public static List<Tag> initAllTags(List<Tag> allTags) {
        sAllTags = allTags;
        if (!allTags.contains(recommendTag)) {
            sAllTags.add(0, recommendTag);
        }
        sMyTags = new ArrayList<>();
        Boolean first = Hawk.get(Constant.MY_TAGS_FIRST, true);
        Hawk.put(Constant.MY_TAGS_FIRST, false);
        if (first) {
            sMyTags.addAll(allTags);
            setMyTagString(sMyTags);
        } else {
            initMyTags(allTags, sMyTags);
        }
        return sMyTags;
    }

    public static boolean hasMyTags() {
        return sMyTags != null && !sMyTags.isEmpty();
    }

    public static List<Tag> getAllTags() {
        return sAllTags;
    }

    public static List<Tag> getMyTags() {
        return sMyTags;
    }

    public static void setMyTags(List<Tag> myTags) {
        sMyTags = myTags;
    }

    /**
     * 保存排好序的标签
     */
    public static void setMyTagString(String tags) {
        Hawk.put(Constant.MY_TAGS, tags);
    }

    /**
     * 从服务器获取标签, 优先从数据库中获取
     */
    public static void getTags(final Action1<List<Tag>> listAction1) {
//        RealmResults<Tag> results = RRealm.realm().where(Tag.class).findAll();
//        final int size = results.size();
//        L.i("数据库中标签数量:" + size);
//        if (size != 0 && listAction1 != null) {
//            List<Tag> tags = new ArrayList<>();
//            for (Tag t : results) {
//                Tag tag = new Tag();
//                tag.setId(t.getId());
//                tag.setName(t.getName());
//                tags.add(tag);
//                L.i("->:" + tag.string());
//            }
//            //listAction1.call(tags);
//        }

        if (cacheTags == null) {
            RRetrofit.create(DiscussService.class)
                    .getTags(Param.buildMap())
                    .compose(Rx.transformerList(Tag.class))
                    .subscribe(new BaseSingleSubscriber<List<Tag>>() {
                        @Override
                        public void onSucceed(final List<Tag> tags) {
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

                            cacheTags = tags;

                            if (listAction1 != null) {
                                listAction1.call(cacheTags);
                            }
                        }

                        @Override
                        public void onError(int code, String msg) {
                            super.onError(code, msg);
                            if (listAction1 != null) {
                                listAction1.call(new ArrayList<Tag>());
                            }
                        }
                    });
        } else {
            if (listAction1 != null) {
                listAction1.call(cacheTags);
            }
        }
    }

    public static void inflate(final AppCompatActivity activity, final ViewGroup rootLayout,
                               final boolean showAll, final Action2<RTextCheckView, Tag> onAddView) {
        TagsControl.getTags(new Action1<List<Tag>>() {
            @Override
            public void call(List<Tag> tags) {
                if (tags.isEmpty()) {
                    T_.show(activity.getString(R.string.fetch_tag_failed));
                    return;
                }

                if (showAll) {
                    tags.add(0, recommendTag);
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
                        int px = (int) ResUtil.dpToPx(activity, 20);
                        params.leftMargin = px;
                        params.bottomMargin = px;
                        checkView.setPadding(px / 2, px / 4, px / 2, px / 4);
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

    /**
     * 获取排好序的标签列表(标签id列表)
     */
    public static List<String> getMyTagString() {
        String tags = Hawk.get(Constant.MY_TAGS, "");
        List<String> split = RUtils.split(tags);
        if (!split.contains(recommendTag.getId())) {
            split.add(0, recommendTag.getId());
        }
        return split;
    }

    /**
     * 保存排好序的标签
     */
    public static void setMyTagString(List<Tag> tags) {
        StringBuilder build = new StringBuilder();
        for (Tag g : tags) {
            build.append(g.getId());
            build.append(",");
        }

        setMyTagString(RUtils.safe(build));
    }

    public static void initMyTags(@NonNull List<Tag> inAllTag, List<Tag> outMyTag, List<Tag> outOtherTag) {
        Boolean first = Hawk.get(Constant.MY_TAGS_FIRST, true);
        Hawk.put(Constant.MY_TAGS_FIRST, false);
        if (first) {
            setMyTagString(inAllTag);
            if (outMyTag != null) {
                outMyTag.addAll(inAllTag);
            }
        } else {
            List<String> myTags = getMyTagString();
            initMyTags(inAllTag, outMyTag);
            //其他标签
            for (Tag g : inAllTag) {
                if (!myTags.contains(g.getId())) {
                    if (outOtherTag != null) {
                        outOtherTag.add(g);
                    }
                }
            }
        }
    }

    public static void initMyTags(@NonNull List<Tag> inAllTag, List<Tag> outMyTag) {
        List<String> myTags = getMyTagString();
        if (myTags.isEmpty()) {
            outMyTag.addAll(inAllTag);
            return;
        }

        //根据顺序, 返回标签
        for (int i = 0; i < myTags.size(); i++) {
            String tagId = myTags.get(i);
            for (int j = 0; j < inAllTag.size(); j++) {
                Tag tag = inAllTag.get(j);
                if (TextUtils.equals(tag.getId(), tagId)) {
                    if (outMyTag != null) {
                        outMyTag.add(tag);
                    }
                }
            }
        }
    }
}
