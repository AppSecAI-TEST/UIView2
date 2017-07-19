package com.hn.d.valley.main.message.chatfile;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.github.utilcode.utils.FileUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RGroupAdapter;
import com.angcyo.uiview.recycler.adapter.RGroupData;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.RImageView;
import com.angcyo.uiview.widget.RTextView;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.iview.RelayPhotoLongClickListener;
import com.hn.d.valley.base.iview.RelayVideoLongClickListener;
import com.hn.d.valley.base.iview.VideoPlayUIView;
import com.hn.d.valley.main.message.slide.ISlideHelper;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.utils.PhotoPager;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hn.d.valley.main.message.chat.ChatUIView2.msgService;

/**
 * Created by hewking on 2017/3/21.
 */
public class ChatFileUIView extends SingleRecyclerUIView<ChatFileUIView.ChatFileGroup> {

    private String mSessionId;
    private SessionTypeEnum mSessionType;

    private ChatFileAdapter mGroupAdapter;

    private LinearLayout ll_bottom;
    private TextView tv_selected;
    private RTextView btn_delete;

    public ChatFileUIView(String sessionId, SessionTypeEnum sessionType) {
        this.mSessionId = sessionId;
        this.mSessionType = sessionType;
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.text_edit))
                .setVisibility(View.GONE)
                .setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 打开itemview checkbox animator 弹出底部button
                        editItems();
                    }
                }));
        return super.getTitleBar().setTitleString(getString(R.string.text_chat_file)).setRightItems(rightItems);
    }

    private void editItems() {
        TextView selectNum = (TextView) getUITitleBarContainer().getRightControlLayout().getChildAt(0);
        if (getString(R.string.text_edit).equals(selectNum.getText().toString())) {
            selectNum.setText(getString(R.string.cancel));
            mGroupAdapter.slideOpen();
        } else if (getString(R.string.cancel).equals(selectNum.getText().toString())) {
            selectNum.setText(getString(R.string.text_edit));
            mGroupAdapter.slideClose();
        }
    }

    @Override
    protected RExBaseAdapter<String, ChatFileGroup, String> initRExBaseAdapter() {
//        mAdapter = new ChatFileAdapter(mActivity);
//        return mAdapter;
        mGroupAdapter = new ChatFileAdapter(mActivity);
        return mGroupAdapter;
    }


    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);
    }

    private class ChatFileAdapter extends RGroupAdapter<String, ChatFileGroup, String> {

        private SparseBooleanArray mCheckStats = new SparseBooleanArray();

        private ISlideHelper mISlideHelper = new ISlideHelper();

        public ChatFileAdapter(Context context) {
            super(context);
            setModel(RModelAdapter.MODEL_MULTI);
        }

        public void slideOpen() {
            tv_selected.setText(String.format(getString(R.string.text_already_selected_file_number), 0));
            mISlideHelper.slideOpen();
            animBottom(true);
        }

        public void slideClose() {
            tv_selected.setText(String.format(getString(R.string.text_already_selected_file_number), 0));
            mISlideHelper.slideClose();
            animBottom(false);
            mCheckStats.clear();
            unSelectorAll(true);
        }

        public List<ChatFile> getSelectFile(List<Integer> selectIndexs) {
            List<ChatFile> chatFiles = new ArrayList<>();

            if (mAllDatas == null) {
                return chatFiles;
            }

            for (Integer i : selectIndexs) {
                int index = i;
                int count = 0;
                for (ChatFileGroup group : mAllDatas) {
                    int gIndex = index - count;
                    int gCount = group.getCount();
                    if (gIndex >= 0 && gIndex < gCount) {
                        chatFiles.add(group.getItem(gIndex));
                    }
                    count += group.getCount();
                }
            }
            return chatFiles;
        }

        @Override
        public void deleteItem(int position) {
            ChatFileGroup chatFileGroup = (ChatFileGroup) getGroupDataFromPosition(position);
            chatFileGroup.remove(getDataIndex(position));
            int size = getCountFromGroup(getAllDatas());
            if (size > position) {
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, size - position);
            }
        }

        @NonNull
        @Override
        protected RBaseViewHolder createBaseViewHolder(int viewType, View itemView) {
            if (viewType == TYPE_GROUP_DATA) {
                ItemSelectVH itemSelectVH = new ItemSelectVH(itemView, viewType);
                mISlideHelper.add(itemSelectVH);
                return itemSelectVH;
            } else {
                return super.createBaseViewHolder(viewType, itemView);
            }
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            if (viewType == TYPE_GROUP_HEAD) {
                return R.layout.item_single_main_text_view;
            } else if (viewType == TYPE_GROUP_DATA) {
                return R.layout.item_checkbox_image_view;
            }
            return super.getItemLayoutId(viewType);
        }

        @Override
        public int getItemType(int position) {
            return super.getItemType(position);
        }

        @Override
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, final int position, ChatFileGroup bean) {
            super.onBindModelView(model, isSelector, holder, position, bean);
            if (getItemViewType(position) != TYPE_GROUP_DATA) {
                return;
            }
            final CheckBox checkBox = holder.v(R.id.delete_view);
            checkBox.setTag(position);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectorPosition(position, checkBox);
                    int tag = (int) checkBox.getTag();
                    boolean selector = isPositionSelector(position);
                    if (selector) {
                        mCheckStats.put(tag, true);
                    } else {
                        mCheckStats.delete(tag);
                    }
                    tv_selected.setText(String.format(getString(R.string.text_already_selected_file_number), mCheckStats.size()));
                }
            };

            checkBox.setOnClickListener(listener);
            checkBox.setChecked(mCheckStats.get(position, false));
        }
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3, LinearLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (mGroupAdapter.isInGroup(position) ||
                        (mGroupAdapter.isEnableLoadMore() && mGroupAdapter.isLast(position))) ?
                        layoutManager.getSpanCount() : 1;
            }
        });

        mRecyclerView.setLayoutManager(layoutManager);

        final int top = getDimensionPixelOffset(R.dimen.base_xhdpi);
        final int line = getDimensionPixelOffset(R.dimen.base_line);

        mRecyclerView.addItemDecoration(
                new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {

                    @Override
                    public void getItemOffsets2(Rect outRect, int position, int edge) {
                        if (position == 0) {
                        } else if (mGroupAdapter.isInGroup(position)) {
                            outRect.top = top;
                        } else {
                            outRect.top = line;
                        }
                    }

                    @Override
                    public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                        paint.setColor(Color.WHITE);
                        if (mGroupAdapter.isInGroup(position)) {
                        } else if (mGroupAdapter.isInGroup(position - 1)) {
                            offsetRect.set(0, itemView.getTop() - offsetRect.top,
                                    top, itemView.getTop());
                            canvas.drawRect(offsetRect, paint);
                        } else {
                            offsetRect.set(0, itemView.getTop() - offsetRect.top,
                                    (int) (density() * 70), itemView.getTop());
                            canvas.drawRect(offsetRect, paint);
                        }
                    }
                }));

    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        msgService().queryMessageList(mSessionId,
                mSessionType, 500 * (Integer.valueOf(page) - 1)
                , 500)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        List<ChatFile> fileList = new ArrayList<>();
                        for (IMMessage message : result) {
                            if (message.getAttachment() instanceof FileAttachment) {
                                FileAttachment attachment = (FileAttachment) message.getAttachment();
                                if (TextUtils.isEmpty(attachment.getPath())) {
                                    continue;
                                }
//                                if (TextUtils.isEmpty(attachment.getThumbPath()) || TextUtils.isEmpty(attachment.getPath())) {
//                                    continue;
//                                }
                                fileList.add(ChatFile.create(message));
                            }
                        }
                        if (fileList.size() != 0) {
                            getUITitleBarContainer().showRightItem(0);
                        }

                        setChatFileGroups(fileList);
//                        mGroupAdapter.setEnableLoadMore(false);
                    }
                });

    }

    private void animBottom(boolean show) {
        if (ll_bottom.getVisibility() == View.GONE) {
            ll_bottom.setVisibility(View.VISIBLE);
        }

        float start = show ? ScreenUtil.dip2px(48) : 0;
        float end = show ? 0 : ScreenUtil.dip2px(48);
        ObjectAnimator animator = ObjectAnimator.ofFloat(ll_bottom, "translationY", start, end);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    @Override
    protected void inflateRecyclerRootLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        View root = inflater.inflate(R.layout.view_member_select, baseContentLayout);
        mRefreshLayout = (RefreshLayout) root.findViewById(R.id.refresh_layout);
        mRecyclerView = (RRecyclerView) root.findViewById(R.id.recycler_view);
        ll_bottom = (LinearLayout) root.findViewById(R.id.ll_bottom);
        tv_selected = (TextView) root.findViewById(R.id.tv_selected);
        btn_delete = (RTextView) root.findViewById(R.id.btn_send);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> integerList = mGroupAdapter.getAllSelectorList();
                if (integerList.size() == 0) {
                    T_.show(mActivity.getString(R.string.text_unselected_chat_file));
                    return;
                }
                UIDialog.build()
                        .setDialogContent(mActivity.getString(R.string.text_is_delete_file))
                        .setOkText(mActivity.getString(R.string.ok))
                        .setCancelText(mActivity.getString(R.string.cancel))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteFile();
                            }
                        })
                        .showDialog(mParentILayout);
            }
        });
    }

    private void deleteFile() {
        List<Integer> integerList = mGroupAdapter.getAllSelectorList();

        if (integerList.size() == 0) {
            T_.show(mActivity.getString(R.string.text_unselected));
            return;
        }

        List<ChatFile> selectFile = mGroupAdapter.getSelectFile(integerList);
        for (ChatFile chatFile : selectFile) {
            String path = chatFile.getThumbPath();
            File file = new File(path);
            if (FileUtils.isFileExists(file)) {
                FileUtils.deleteFile(file);
            }
        }
        // 删除adapter 中的item 升序排列 循环删除时能够 删除前一个 后一个 的pos -1
        Collections.sort(integerList);
        int offset = 0;
        for (Integer index : integerList) {
            mGroupAdapter.deleteItem(index - offset);
            offset ++;
        }

        // 关闭编辑选项
        editItems();
    }

    public void setChatFileGroups(List<ChatFile> list) {
        showContentLayout();
        //先要判断之前最后一条数据的日期, 决定是否需要插入
        ChatFileGroup lastGroup = null;
        if (page != 1) {
            List<ChatFileGroup> allDatas = mGroupAdapter.getAllDatas();
            if (allDatas.size() > 0) {
                lastGroup = allDatas.get(allDatas.size() - 1);
            }
        }
        List<ChatFileGroup> userInfoGroups = getUserInfoGroups(list);

        if (lastGroup == null) {
            mGroupAdapter.resetAllData(userInfoGroups);
        } else {
            if (userInfoGroups.size() > 0) {
                ChatFileGroup firstGroup = userInfoGroups.get(0);
                if (lastGroup.year == firstGroup.year &&
                        lastGroup.month == firstGroup.month &&
                        lastGroup.day == firstGroup.day) {
                    lastGroup.appendDatas(mGroupAdapter, firstGroup.getAllDatas());
                    userInfoGroups.remove(0);
                }
                mGroupAdapter.appendData(userInfoGroups);
            }
        }

        if (mRExBaseAdapter.getItemCount() > 0) {
            showContentLayout();
        } else {
            showEmptyLayout();
        }

        if (1 == page && list.size() >= Constant.DEFAULT_PAGE_DATA_COUNT) {
            mGroupAdapter.setEnableLoadMore(true);
        }
        onUILoadDataFinish();
    }

    protected List<ChatFileGroup> getUserInfoGroups(List<ChatFile> bean) {
        int lastYear = -1, lastMonth = -1, lastDay = -1;
        int nowYear = -1, nowMonth = -1, nowDay = -1;//现在的年月份

        List<ChatFileGroup> groups = new ArrayList<>();

        String now = TimeUtil.getDateTimeString(System.currentTimeMillis(), "yyyy/MM/dd");
        String[] nowSplit = now.split("/");
        if (nowSplit.length == 3) {
            nowYear = Integer.valueOf(nowSplit[0]);
            nowMonth = Integer.valueOf(nowSplit[1]);
            nowDay = Integer.valueOf(nowSplit[2]);
        }

        ChatFileGroup group = null;

        List<ChatFile> files = new ArrayList<>();
        for (int i = 0; i < bean.size(); i++) {
            ChatFile chatFile = bean.get(i);
            String formatDateTime = TimeUtil.getDateTimeString(chatFile.getTime(), "yyyy/MM/dd");

            String[] split = formatDateTime.split("/");
            if (split.length == 3) {
                if (i == 0) {
                    lastYear = Integer.valueOf(split[0]);
                    lastMonth = Integer.valueOf(split[1]);
                    lastDay = Integer.valueOf(split[2]);

                    files.add(chatFile);
                    group = new ChatFileGroup(files,
                            createTime(lastYear, lastMonth, lastDay, nowYear, nowMonth, nowDay))
                            .setYear(lastYear).setMonth(lastMonth).setDay(lastDay);
                } else {
                    int year = Integer.valueOf(split[0]);
                    int month = Integer.valueOf(split[1]);
                    int day = Integer.valueOf(split[2]);

                    if (lastYear == year && lastMonth == month && lastDay == day) {
                        files.add(chatFile);
                    } else {
                        groups.add(group);
                        lastYear = year;
                        lastMonth = month;
                        lastDay = day;

                        files = new ArrayList<>();
                        files.add(chatFile);
                        group = new ChatFileGroup(files,
                                createTime(lastYear, lastMonth, lastDay, nowYear, nowMonth, nowDay))
                                .setYear(lastYear).setMonth(lastMonth).setDay(lastDay);
                    }
                }
            }
        }
        if (group != null) {
            groups.add(group);
        }
        return groups;
    }

    private String createTime(int lastYear, int lastMonth, int lastDay,
                              int nowYear, int nowMonth, int nowDay) {
        String time;
        if (lastYear == nowYear && lastMonth == nowMonth && lastDay == nowDay) {
            time = getString(R.string.today);
        } else if (lastYear == nowYear && lastMonth == nowMonth && lastDay + 1 == nowDay) {
            time = getString(R.string.yesterday);
        } else if (lastYear == nowYear) {
            time = getString(R.string.time_format, lastMonth, lastDay);
        } else {
            time = getString(R.string.full_time_format, lastYear, lastMonth, lastDay);
        }
        return time;
    }


    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return super.getDefaultLayoutState();
    }


    public class ChatFileGroup extends RGroupData<ChatFile> {

        int year;//年
        int month;//月
        public int day;
        private String time;

        public ChatFileGroup(List<ChatFile> allDatas, String time) {
            super(allDatas);
            this.time = time;
        }

        public ChatFileGroup setYear(int year) {
            this.year = year;
            return this;
        }

        public ChatFileGroup setMonth(int month) {
            this.month = month;
            return this;
        }

        public ChatFileGroup setDay(int day) {
            this.day = day;
            return this;
        }

        public ChatFile getItem(int index) {
            return getAllDatas().get(index - 1);
        }

        public void remove(int pos) {
            getAllDatas().remove(pos);
        }

        @Override
        protected void onBindGroupView(RBaseViewHolder holder, int position, int indexInGroup) {
            holder.tv(R.id.text_view).setText(time);
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, final int position, final int indexInData) {
            final RImageView imageView = holder.v(R.id.image_view);
            final ChatFile chatFile = getAllDatas().get(indexInData);
            int size = mRecyclerView.getMeasuredWidth() / 3;
            UI.setView(imageView, size, size);
            int offset = getDimensionPixelOffset(R.dimen.base_mdpi);
            holder.itemView.setPadding(offset, offset, offset, offset);

            //图片
            if ("2".equalsIgnoreCase(chatFile.media_type)) {
                //图片
                Glide.with(holder.getContext())
                        .load(chatFile.getPath())
                        .placeholder(R.drawable.zhanweitu_1)
                        .into(imageView);

                imageView.setPlayDrawable(null);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePagerUIView.start(mParentILayout, imageView,
                                PhotoPager.getImageItems(getAllPhotos()), getPhotoStartIndex(position))
                                .setPhotoViewLongClickListener(new RelayPhotoLongClickListener(getILayout()));
                    }
                });
            } else if ("3".equalsIgnoreCase(chatFile.media_type)) {
                //视频
                Glide.with(holder.getContext())
                        .load(chatFile.thumbPath)
                        .placeholder(R.drawable.zhanweitu_1)
                        .into(imageView);
                imageView.setPlayDrawable(R.drawable.play);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!FileUtils.isFileExists(chatFile.getPath())) {
                            T_.show("视频已过期！");
                            return;
                        }
                        mParentILayout.startIView(new VideoPlayUIView(chatFile.getPath(),
                                RImageView.copyDrawable(imageView),
                                ((VideoFile) chatFile).genWidthAndHeight()).setOnLongPress(new RelayVideoLongClickListener(mParentILayout)));
                    }
                });
            } else {
                imageView.setPlayDrawable(null);
            }
        }

        private List<String> getAllPhotos() {
            List<String> photos = new ArrayList<>();
            for (int i = 0; i < mAllDatas.size(); i++) {
                ChatFile chatFile = mAllDatas.get(i);
                if ("2".equalsIgnoreCase(chatFile.media_type)) {
                    photos.add(chatFile.getPath());
                }
            }
            return photos;
        }

        private int getPhotoStartIndex(int position) {
            int index = 0;
            for (int i = 0; i < mAllDatas.size(); i++) {
                ChatFile chatFile = mAllDatas.get(i);
                if (i == position) {
                    break;
                }
                if ("2".equalsIgnoreCase(chatFile.media_type)) {
                    index++;
                }
            }
            return index;
        }
    }

}
