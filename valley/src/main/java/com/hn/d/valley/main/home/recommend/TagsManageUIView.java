package com.hn.d.valley.main.home.recommend;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextPaint;
import android.view.View;

import com.angcyo.uiview.github.item.touch.helper.ChannelAdapter;
import com.angcyo.uiview.github.item.touch.helper.ChannelEntity;
import com.angcyo.uiview.github.item.touch.helper.ItemDragHelperCallback;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.control.TagsControl;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：标签管理
 * 创建人员：Robi
 * 创建时间：2017/02/23 11:23
 * 修改人员：Robi
 * 修改时间：2017/02/23 11:23
 * 修改备注：
 * Version: 1.0.0
 */
public class TagsManageUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    List<Tag> mAllTag, mMyTag, mOtherTag;
    boolean isChanged = false;
    Action1<List<Tag>> mTagsAction1;

    public TagsManageUIView(List<Tag> allTag, Action1<List<Tag>> tagsAction1) {
        mAllTag = allTag;
        mMyTag = new ArrayList<>();
        mOtherTag = new ArrayList<>();
        mTagsAction1 = tagsAction1;
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        TagsControl.initMyTags(mAllTag, mMyTag, mOtherTag);
    }

    @Override
    protected int getTitleResource() {
        return R.string.tags_manage_title;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_recycler_view;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemConfig() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                RRecyclerView recyclerView = holder.v(R.id.recycler_view);
                recyclerView.setTag("GV4");
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                ItemDragHelperCallback.OnDragCallback dragCallback = new ItemDragHelperCallback.OnDragCallback() {
                    @Override
                    public boolean canDragDirs(RecyclerView.ViewHolder viewHolder) {
                        if (viewHolder.getAdapterPosition() == 1) {
                            return false;
                        }
                        return true;
                    }
                };
                ItemDragHelperCallback callback = new ItemDragHelperCallback(dragCallback);
                final ItemTouchHelper helper = new ItemTouchHelper(callback);
                helper.attachToRecyclerView(recyclerView);

                final ChannelAdapter adapter = new ChannelAdapter(mActivity, helper, get(mMyTag), get(mOtherTag));
                adapter.setOnDragCallback(dragCallback);
                adapter.setOnFinishListener(new ChannelAdapter.OnFinishListener() {
                    @Override
                    public void onFinish(String ids) {
                        isChanged = true;
                        TagsControl.setMyTags(ids);
                    }
                });
                ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int viewType = adapter.getItemViewType(position);
                        return viewType == ChannelAdapter.TYPE_MY || viewType == ChannelAdapter.TYPE_OTHER ? 1 : 4;
                    }
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void setItemOffsets(Rect rect) {

            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {

            }
        }));
    }

    List<ChannelEntity> get(List<Tag> tags) {
        List<ChannelEntity> entity = new ArrayList<>();
        for (Tag g : tags) {
            entity.add(new ChannelEntity(g.getId(), g.getName()));
        }
        return entity;
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        if (isChanged && mTagsAction1 != null) {
            List<Tag> tags = new ArrayList<>();
            TagsControl.initMyTags(mAllTag, tags);
            mTagsAction1.call(tags);
        }
    }
}
