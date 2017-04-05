package com.hn.d.valley.main.message.search;

import android.text.TextUtils;

import com.angcyo.uiview.container.ILayout;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.friend.ItemTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/31 14:20
 * 修改人员：hewking
 * 修改时间：2017/03/31 14:20
 * 修改备注：
 * Version: 1.0.0
 */
public class SearchResultList {

    private Map<String, Group> groupMap = new HashMap<>();

    private final Group groupNull = new Group(null, GlobalSearchUIView2.Options.sOptions);

    List<Group> groups;

    GlobalSearchUIView2.Options option;

    public SearchResultList(GlobalSearchUIView2.Options option) {
        groups = new ArrayList<>();
        this.option = option;
        build();
    }


    public void resetData(List<AbsContactItem> items) {

        clean();

        for (AbsContactItem item : items) {

            add(item);

        }

        build();

    }

    public void clean(){

        groups.clear();
        groupMap.clear();

    }

    public int getCount() {
        int count = 0;
        for (Group group : groups) {
            count += group.getCount();
        }
        return count;
    }

    public List<AbsContactItem> getItems() {
        List<AbsContactItem> items = new ArrayList<AbsContactItem>();
        for (Group group : groups) {
            AbsContactItem head = group.getHead();
            if (head != null) {
                items.add(head);
            }
            items.addAll(group.getItems());
        }

        return items;
    }

    public AbsContactItem getItem(int index) {
        int count = 0;
        for (Group group : groups) {
            int gIndex = index - count;
            int gCount = group.getCount();

            if (gIndex >= 0 && gIndex < gCount) {
                return group.getItem(gIndex);
            }

            count += gCount;
        }

        return null;
    }

    public void build() {
        //
        // GROUPS
        //

        groups.add(groupNull);
        groups.addAll(groupMap.values());
//        sortGroups(groups);
    }


    public final void add(AbsContactItem item) {
        if (item == null) {
            return;
        }

        Group group;

        String id = item.getGroupText();
        if (TextUtils.isEmpty(id)) {
            group = groupNull;
        } else {
            group = groupMap.get(id);
            if (group == null) {
                group = new Group(id,option);
                groupMap.put(id, group);
            }
        }

        group.add(item);
    }



    public static class Group {

        String title;

        private boolean hasHead;

        private GlobalSearchUIView2.Options option;

        List<AbsContactItem> groupItems = new ArrayList<>();

        public Group(String title, GlobalSearchUIView2.Options option) {
            this.title = title;
            this.option = option;
            hasHead = !TextUtils.isEmpty(title);
        }

        public int getCount() {
            return groupItems.size() + (hasHead ? 1 : 0);
        }

        public void add(AbsContactItem item) {

            if (option.isSearchMuti()) {

                if (groupItems.size() >= 4 ) {
                    return;
                }

                if (groupItems.size() == 3) {
                    groupItems.add(getFuncItem());
                    return;
                }
            }

            groupItems.add(item);
        }

        List<AbsContactItem> getItems() {
            return groupItems;
        }

        AbsContactItem getItem(int index) {
            if (hasHead) {
                if (index == 0) {
                    return getHead();
                } else {
                    index--;
                    return index >= 0 && index < groupItems.size() ? groupItems.get(index) : null;
                }
            } else {
                return index >= 0 && index < groupItems.size() ? groupItems.get(index) : null;
            }
        }

        AbsContactItem getHead() {
            return hasHead ? new SectionItem(title) : null;
        }

        AbsContactItem getFuncItem() {
            return new FuncItem<>(title, new Action1<ILayout>() {
                @Override
                public void call(ILayout s) {
//                    GlobalSingleSearchUIView.start(s,,new GlobalSearchUIView2.Options(false),new int[]{ItemTypes.GROUP});
                }
            });
        }
    }



}
