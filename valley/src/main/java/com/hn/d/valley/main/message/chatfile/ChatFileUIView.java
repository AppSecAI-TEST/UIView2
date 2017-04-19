package com.hn.d.valley.main.message.chatfile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.angcyo.uiview.github.utilcode.utils.FileUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.RImageView;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.iview.VideoPlayUIView;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.utils.PhotoPager;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

import static com.hn.d.valley.main.message.chat.ChatUIView2.msgService;

/**
 * Created by hewking on 2017/3/21.
 */
public class ChatFileUIView extends SingleRecyclerUIView<ChatFile> {

    private String mSessionId;
    private SessionTypeEnum mSessionType;

    private ChatFileAdapter mAdapter;

    public ChatFileUIView(String sessionId,SessionTypeEnum sessionType) {
        this.mSessionId = sessionId;
        this.mSessionType = sessionType;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString("聊天文件");
    }

    @Override
    protected RExBaseAdapter<String, ChatFile, String> initRExBaseAdapter() {
        mAdapter = new ChatFileAdapter(mActivity);
        return mAdapter;
    }


    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);

    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3, LinearLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return ((mAdapter.isEnableLoadMore() && mAdapter.isLast(position))) ?
                        layoutManager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);

        msgService().queryMessageList(mSessionId,
                mSessionType, 0
                , 150)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        List<ChatFile> fileList = new ArrayList<>();
                        for (IMMessage message : result) {
                            if (message.getAttachment() instanceof FileAttachment) {

                                FileAttachment attachment = (FileAttachment) message.getAttachment();

                                attachment.getThumbPath();

                                if (attachment.getThumbPath() == null) {
                                    continue;
                                }

                                fileList.add(ChatFile.create(message));
                            }
                        }
                        onUILoadDataEnd(fileList);
                        onUILoadDataFinish();
                        mRExBaseAdapter.setEnableLoadMore(false);
                    }
                });

    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    private class ChatFileAdapter extends RExBaseAdapter<String, ChatFile, String> {

        public ChatFileAdapter(Context context) {
            super(context);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, final int posInData, final ChatFile chatFile) {

            final RImageView imageView = holder.v(R.id.image_view);

            int size = mRecyclerView.getMeasuredWidth() / 3;
            UI.setView(imageView, size, size);
            int offset = getDimensionPixelOffset(R.dimen.base_mdpi);
            holder.itemView.setPadding(offset, offset, offset, offset);

            //图片
            if ("2".equalsIgnoreCase(chatFile.media_type)) {
                //图片
                Glide.with(holder.getContext())
                        .load(chatFile.thumbPath)
                        .placeholder(R.drawable.zhanweitu_1)
                        .into(imageView);

                imageView.setPlayDrawable(null);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePagerUIView.start(mOtherILayout, imageView,
                                PhotoPager.getImageItems(getAllPhotos()), getPhotoStartIndex(posInData));
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
                        if (!FileUtils.isFileExists(chatFile.getPath())){
                            T_.show("视频已过期！");
                            return;
                        }
                        mOtherILayout.startIView(new VideoPlayUIView(chatFile.getPath(),
                                imageView.getDrawable().getConstantState().newDrawable(),
                                ((VideoFile)chatFile).genWidthAndHeight()));

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
                    photos.add(chatFile.getThumbPath());
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

        @Override
        protected int getItemLayoutId(int viewType) {
                return R.layout.item_single_image_view;
        }
    }


//    public class FileGroup extends RGroupData<ChatFile> {
//
//        int year;//年
//        int month;//月
//
//        public FileGroup(List<ChatFile> allDatas, int year, int month) {
//            super(allDatas);
//            this.year = year;
//            this.month = month;
//        }
//
//        @Override
//        protected void onBindGroupView(RBaseViewHolder holder, int position, int indexInGroup) {
//            holder.tv(R.id.text_view).setText(getSlectedSpannable(
//                    String.format(Locale.CHINA, "%02d", month) + "/" + year));
//        }
//
//
//        @Override
//        protected void onBindDataView(RBaseViewHolder holder, int position, final int indexInData) {
//            final RImageView imageView = holder.v(R.id.image_view);
//            final ChatFile chatFile = mAllDatas.get(indexInData);
//
//            int size = mRecyclerView.getMeasuredWidth() / 3;
//            UI.setView(imageView, size, size);
//            int offset = getDimensionPixelOffset(R.dimen.base_mdpi);
//            holder.itemView.setPadding(offset, offset, offset, offset);
//
//            if ("3".equalsIgnoreCase(chatFile.media_type)) {
//                //图片
//                Glide.with(holder.getContext())
//                        .load(chatFile.thumbPath)
//                        .placeholder(R.drawable.zhanweitu_1)
//                        .into(imageView);
//
//                imageView.setPlayDrawable(null);
//
//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        ImagePagerUIView.start(mOtherILayout, imageView,
////                                PhotoPager.getImageItems(getAllPhotos()), getPhotoStartIndex(indexInData));
//                    }
//                });
//            } else if ("2".equalsIgnoreCase(chatFile.media_type)) {
//                //视频
//
//                Glide.with(holder.getContext())
//                        .load(chatFile.thumbPath)
//                        .placeholder(R.drawable.zhanweitu_1)
//                        .into(imageView);
//                imageView.setPlayDrawable(R.drawable.play);
//
//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        mOtherILayout.startIView(new VideoPlayUIView(videoUrl,
////                                imageView.getDrawable().getConstantState().newDrawable(),
////                                OssHelper.getWidthHeightWithUrl(videoUrl)));
//
//                    }
//                });
//            } else {
//                imageView.setPlayDrawable(null);
//            }
//        }
//    }

}
