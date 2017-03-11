package com.hn.d.valley.emoji;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.EmojiLayoutControl;

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
public class EmojiRecyclerView extends RRecyclerView {
    EmojiLayoutControl.OnEmojiSelectListener mOnEmojiSelectListener;

    public EmojiRecyclerView(Context context) {
        this(context, null);
    }


    public EmojiRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setItemAnim(false);
        setTag("GV8");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setAdapter(new EmojiAdapter(getContext()));
    }

    public void setOnEmojiSelectListener(EmojiLayoutControl.OnEmojiSelectListener onEmojiSelectListener) {
        mOnEmojiSelectListener = onEmojiSelectListener;
    }

    public interface OnEmojiSelectListener {
        void onEmojiText(String emoji);
    }

    class EmojiAdapter extends RBaseAdapter<String> {

        public EmojiAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return 0;
        }

        @Override
        protected View createContentView(ViewGroup parent, int viewType) {
            RelativeLayout layout = new RelativeLayout(mContext);
            layout.setBackgroundResource(R.drawable.base_bg_selector);

            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            final float density = mContext.getResources().getDisplayMetrics().density;
            int width = (int) (density * 28);
            int height = (int) (density * 40);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(imageView, params);
            return layout;
        }

        @Override
        public int getItemCount() {
            return EmojiManager.getDisplayCount();
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, final int position, String bean) {
            ((ImageView) ((ViewGroup) holder.itemView).getChildAt(0)).setImageDrawable(EmojiManager.getDisplayDrawable(mContext, position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnEmojiSelectListener != null) {
                        mOnEmojiSelectListener.onEmojiText(EmojiManager.getDisplayText(position));
                    }
                }
            });
        }
    }
}
