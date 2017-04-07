package com.hn.d.valley.main.home.recommend;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RDragCallback;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RGroupAdapter;
import com.angcyo.uiview.recycler.adapter.RGroupData;
import com.angcyo.uiview.utils.RUtils;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.control.TagsControl;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：标签管理2
 * 创建人员：Robi
 * 创建时间：2017-4-7
 * 修改人员：Robi
 * 修改时间：2017-4-7
 * 修改备注：
 * Version: 1.0.0
 */
public class TagsManageUIView2 extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    List<Tag> mAllTag, mMyTag, mOtherTag;
    boolean isChanged = false;
    Action1<List<Tag>> mTagsAction1;
    private RGroupAdapter<String, Tag, String> mGroupAdapter;
    private RGroupData<Tag> mMyGroup;
    private RGroupData<Tag> mOtherGroup;
    private RExItemDecoration mRExItemDecoration;

    public TagsManageUIView2(List<Tag> allTag, Action1<List<Tag>> tagsAction1) {
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
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().addRightItem(TitleBarPattern.buildText(getString(R.string.finish), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder builder = new StringBuilder();
                for (Tag entity : mMyGroup.getAllDatas()) {
                    builder.append(entity.getId());
                    builder.append(",");
                }

                TagsControl.setMyTags(RUtils.safe(builder));

                if (mTagsAction1 != null) {
                    List<Tag> tags = new ArrayList<>();
                    TagsControl.initMyTags(mAllTag, tags);
                    mTagsAction1.call(tags);
                }

                finishIView();
            }
        }));
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
    public int getDefaultBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, final int posInData, ViewItemInfo dataBean) {
                RRecyclerView recyclerView = holder.v(R.id.recycler_view);
                recyclerView.setTag("GV1");
                recyclerView.setItemAnimator(new DefaultItemAnimator());

//                ItemDragHelperCallback.OnDragCallback dragCallback = new ItemDragHelperCallback.OnDragCallback() {
//                    @Override
//                    public boolean canDragDirs(RecyclerView.ViewHolder viewHolder) {
//                        if (mGroupAdapter.isInGroup(viewHolder.getAdapterPosition()) || viewHolder.getAdapterPosition() == 1) {
//                            return false;
//                        }
//                        return true;
//                    }
//                };
//                ItemDragHelperCallback callback = new ItemDragHelperCallback(dragCallback);


                final ItemTouchHelper helper = new ItemTouchHelper(new RDragCallback(new RDragCallback.SingleDragCallback() {
                    @Override
                    public boolean canHorizontalDragDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        return false;
                    }

                    @Override
                    public boolean isLongPressDragEnabled() {
                        return false;
                    }

                    @Override
                    public boolean canDragDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        if (adapterPosition > 1 && adapterPosition < mMyGroup.getDataCount() + 1) {
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void onMove(RecyclerView recyclerView, int fromPosition, int toPosition) {
                        List allDatas = mMyGroup.getAllDatas();

                        int size = allDatas.size();
                        int start = fromPosition - 1;
                        int end = toPosition - 1;
                        if (size > start && size > end) {
                            Collections.swap(allDatas, start, end);
                        }
                        mGroupAdapter.notifyItemMoved(fromPosition, toPosition);
                    }

                    @Override
                    public void onClearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        mGroupAdapter.notifyItemRangeChanged(0, mGroupAdapter.getDataCount());
                    }
                }));
                helper.attachToRecyclerView(recyclerView);

//                final ChannelAdapter adapter = new ChannelAdapter(mActivity, helper, get(mMyTag), get(mOtherTag));
//                adapter.setOnDragCallback(dragCallback);
//                adapter.setOnFinishListener(new ChannelAdapter.OnFinishListener() {
//                    @Override
//                    public void onFinish(String ids) {
//                        isChanged = true;
//                        TagsControl.setMyTags(ids);
//                    }
//                });
//                ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                    @Override
//                    public int getSpanSize(int position) {
//                        int viewType = adapter.getItemViewType(position);
//                        return viewType == ChannelAdapter.TYPE_MY || viewType == ChannelAdapter.TYPE_OTHER ? 1 : 4;
//                    }
//                });
//                recyclerView.setAdapter(adapter);

                mMyGroup = new RGroupData<Tag>(mMyTag) {
                    @Override
                    protected void onBindDataView(final RBaseViewHolder holder, final int position, final int indexInData) {
                        holder.imgV(R.id.image_view).setImageResource(R.drawable.yichu_biaoqian_icon);
                        holder.imgV(R.id.right_image_view).setImageResource(R.drawable.yidong_biaoqian_icon);
                        holder.tv(R.id.name_view).setText(getAllDatas().get(indexInData).getName());
                        if (indexInData == 0) {
                            holder.tv(R.id.count_tip_view).setText(R.string.default_text);
                            holder.imgV(R.id.image_view).setVisibility(View.INVISIBLE);
                            holder.imgV(R.id.right_image_view).setVisibility(View.INVISIBLE);
                        } else {
                            holder.tv(R.id.count_tip_view).setText(R.string.attention_count_tip);
                            holder.imgV(R.id.image_view).setVisibility(View.VISIBLE);
                            holder.imgV(R.id.right_image_view).setVisibility(View.VISIBLE);
                        }
                        holder.tv(R.id.count_view).setText(getAllDatas().get(indexInData).getAttention_count());

                        holder.v(R.id.image_view).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Tag remove = mMyGroup.getAllDatas().remove(indexInData);
                                mOtherGroup.getAllDatas().add(remove);
                                mGroupAdapter.notifyItemMoved(position, mGroupAdapter.getDataCount() - 1);
                                mGroupAdapter.notifyItemRangeChanged(0, mGroupAdapter.getDataCount());
                            }
                        });

                        holder.imgV(R.id.right_image_view).setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (MotionEventCompat.getActionMasked(event)) {
                                    case MotionEvent.ACTION_DOWN:
                                        helper.startDrag(holder);
                                        break;
                                }
                                return true;
                            }
                        });
                    }

                    @Override
                    protected void onBindGroupView(RBaseViewHolder holder, int position, int indexInGroup) {
                        holder.tv(R.id.text_view).setText("我关注的标签");
                        holder.tv(R.id.right_text_view).setText("拖动");
                    }
                };

                //holder.tv(R.id.count_tip_view).setText(getAllDatas().get(indexInData).getName());
                mOtherGroup = new RGroupData<Tag>(mOtherTag) {
                    @Override
                    protected void onBindDataView(RBaseViewHolder holder, final int position, final int indexInData) {
                        holder.imgV(R.id.image_view).setImageResource(R.drawable.tianjia_biaoqian_icon);
                        holder.imgV(R.id.right_image_view).setImageDrawable(null);
                        holder.tv(R.id.name_view).setText(getAllDatas().get(indexInData).getName());
                        //holder.tv(R.id.count_tip_view).setText(getAllDatas().get(indexInData).getName());
                        holder.tv(R.id.count_view).setText(getAllDatas().get(indexInData).getAttention_count());

                        holder.v(R.id.image_view).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Tag remove = mOtherGroup.getAllDatas().remove(indexInData);
                                mMyGroup.getAllDatas().add(remove);
                                mGroupAdapter.notifyItemMoved(position, mMyGroup.getDataCount());
                                mGroupAdapter.notifyItemRangeChanged(0, mGroupAdapter.getDataCount());
                            }
                        });
                    }

                    @Override
                    protected void onBindGroupView(RBaseViewHolder holder, int position, int indexInGroup) {
                        holder.tv(R.id.text_view).setText("更多标签");
                        holder.tv(R.id.right_text_view).setText("");
                    }
                };

                List<RGroupData> mGroups = new ArrayList<>();
                mGroups.add(mMyGroup);
                mGroups.add(mOtherGroup);
                mGroupAdapter = new RGroupAdapter(mActivity, mGroups) {
                    @Override
                    protected int getItemLayoutId(int viewType) {
                        if (viewType == TYPE_GROUP_HEAD) {
                            return R.layout.item_single_text_view;
                        } else {
                            return R.layout.item_tag_manager_layout;
                        }
                    }
                };
                recyclerView.setAdapter(mGroupAdapter);

                if (mRExItemDecoration == null) {
                    mRExItemDecoration = new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
                        @Override
                        public void getItemOffsets(Rect outRect, int position) {
                            outRect.bottom = getResources().getDimensionPixelOffset(R.dimen.base_line);
                        }

                        @Override
                        public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                            if (mGroupAdapter.isInGroup(position)) {
                                offsetRect.set(0, itemView.getBottom(), itemView.getRight(), itemView.getBottom() + offsetRect.bottom);
                            } else {
                                int offset = getDimensionPixelOffset(R.dimen.base_xhdpi);
                                offsetRect.set(offset, itemView.getBottom(), itemView.getRight(), itemView.getBottom() + offsetRect.bottom);
                            }
                            paint.setColor(getColor(R.color.line_color));
                            canvas.drawRect(offsetRect, paint);
                        }
                    });
                }
                recyclerView.removeItemDecoration(mRExItemDecoration);
                recyclerView.addItemDecoration(mRExItemDecoration);
            }
        }));
    }
}
