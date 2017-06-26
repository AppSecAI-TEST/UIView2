package com.hn.d.valley.main.found.sub;

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
import com.angcyo.uiview.utils.RUtils;
import com.hn.d.valley.R;
import com.hn.d.valley.control.HotTagsControl;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * 热点资讯, 标签管理
 */
public class HotTagsManageUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    List<String> mAllTag, mMyTag, mOtherTag;
    boolean isChanged = false;
    Action1<List<String>> mTagsAction1;

    public HotTagsManageUIView(List<String> allTag) {
        mAllTag = allTag;
        mMyTag = HotTagsControl.INSTANCE.getMyTags(mAllTag);
        mOtherTag = HotTagsControl.INSTANCE.getOtherTags(mAllTag, mMyTag);
    }

    @Override
    protected int getTitleResource() {
        return R.string.hot_tags_manage_title;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_recycler_view;
    }

    public HotTagsManageUIView setTagsAction1(Action1<List<String>> tagsAction1) {
        mTagsAction1 = tagsAction1;
        return this;
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
                adapter.setMyTagTitle(getString(R.string.my_channel_tip));
                adapter.setOtherTagTitle(getString(R.string.other_channel_tip));
                adapter.setOnDragCallback(dragCallback);
                adapter.setOnFinishListener(new ChannelAdapter.OnFinishListener() {
                    @Override
                    public void onFinish(String ids) {
                        isChanged = true;
                        mMyTag = RUtils.split(ids);
                        HotTagsControl.INSTANCE.setMyTagsString(ids);
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

    List<ChannelEntity> get(List<String> tags) {
        List<ChannelEntity> entity = new ArrayList<>();
        for (String tag : tags) {
            entity.add(new ChannelEntity(tag, tag));
        }
        return entity;
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        if (isChanged && mTagsAction1 != null) {
            mTagsAction1.call(mMyTag);
        }
    }
}