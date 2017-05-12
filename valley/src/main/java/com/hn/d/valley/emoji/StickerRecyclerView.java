package com.hn.d.valley.emoji;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/14 11:46
 * 修改人员：Robi
 * 修改时间：2017/01/14 11:46
 * 修改备注：
 * Version: 1.0.0
 */
public class StickerRecyclerView extends RRecyclerView {

    IEmoticonSelectedListener mOnEmojiSelectListener;

    private Context context;
    private StickerCategory category;
    private int startIndex;

    public StickerRecyclerView(Context mContext, StickerCategory category, int startIndex) {
        this(mContext, null);
        this.context = mContext;
        this.category = category;
        this.startIndex = startIndex;
    }


    public StickerRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setItemAnim(false);
        setTag("GV5");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setAdapter(new StickerAdapter(getContext()));
    }

    public void setOnEmojiSelectListener(IEmoticonSelectedListener onEmojiSelectListener) {
        mOnEmojiSelectListener = onEmojiSelectListener;
    }


    class StickerAdapter extends RBaseAdapter<String> {

        public StickerAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.msg_sticker_picker_view;
        }


        @Override
        public int getItemCount() {
            int count = category.getStickers().size() - startIndex;
            count = Math.min(count, EmoticonView.STICKER_PER_PAGE);
            return count;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, final int position, String bean) {
            ImageView thumb_image = holder.imgV(R.id.sticker_thumb_image);
            TextView descLabel = holder.tv(R.id.sticker_desc_label);

            int index = startIndex + position;
            if (index >= category.getStickers().size()) {
                return ;
            }
            final StickerItem sticker = category.getStickers().get(index);
            if (sticker == null) {
                return ;
            }

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnEmojiSelectListener != null) {
                        mOnEmojiSelectListener.onStickerSelected(sticker.getCategory(),sticker.getName());
                    }
                }
            });

            descLabel.setText(sticker.getMeaning());

//            String path = "file:///android_asset/assetName";
//            Glide.with(mContext).load(R.drawable.default_avatar).into(thumb_image);
            Glide.with(mContext)
//                    .using(new AssetUriLoader(mContext))
                    .load(Uri.parse(StickerManager.getInstance().getStickerBitmapUri(sticker.getCategory()
                    , sticker.getName())))
//                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new GlideDrawableImageViewTarget(thumb_image,1));

//            Bitmap assetBitmap = EmojiManager.loadAssetBitmap(mContext, StickerManager.getInstance().getStickerBitmapUri(sticker.getCategory()
//                    , sticker.getName()));
//            thumb_image.setImageBitmap(assetBitmap);


        }
    }
}
