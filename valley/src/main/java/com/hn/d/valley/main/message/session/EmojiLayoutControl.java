package com.hn.d.valley.main.message.session;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.media.BitmapDecoder;
import com.angcyo.uiview.widget.viewpager.RPagerAdapter;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.emoji.EmojiManager;
import com.hn.d.valley.emoji.EmojiRecyclerView;
import com.hn.d.valley.emoji.EmoticonView;
import com.hn.d.valley.emoji.IEmoticonCategoryChanged;
import com.hn.d.valley.emoji.IEmoticonSelectedListener;
import com.hn.d.valley.emoji.StickerCategory;
import com.hn.d.valley.emoji.StickerManager;
import com.hn.d.valley.widget.CheckedImageButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.hn.d.valley.emoji.EmoticonView.EMOJI_PER_PAGE;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/30 16:02
 * 修改人员：Robi
 * 修改时间：2016/12/30 16:02
 * 修改备注：
 * Version: 1.0.0
 */
public class EmojiLayoutControl implements IEmoticonCategoryChanged{

    RBaseViewHolder mViewHolder;
    UIViewPager mUIViewPager;
    LinearLayout pageNumberLayout;
    private HorizontalScrollView scrollView;
    private LinearLayout tabView;

    Context context;
    OnEmojiSelectListener mOnEmojiSelectListener;

    private IEmoticonSelectedListener listener;

    private EmoticonView gifView;

    private boolean loaded = false;
    private int categoryIndex;
    private boolean withSticker = true;

    private Handler uiHandler;

    /**
     * 总页数.
     */
    private int pageCount;

    public EmojiLayoutControl(RBaseViewHolder viewHolder, final IEmoticonSelectedListener listener) {
        mViewHolder = viewHolder;
        mUIViewPager = mViewHolder.v(R.id.view_pager);
        pageNumberLayout = mViewHolder.v(R.id.layout_scr_bottom);
        tabView = mViewHolder.v(R.id.emoj_tab_view);
        scrollView = mViewHolder.v(R.id.emoj_tab_view_container);

        this.listener = listener;
        context = viewHolder.getContext();

        this.uiHandler = new Handler(context.getMainLooper());

//        mUIViewPager.setAdapter(pagerAdapter);

//        mUIViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
////                mTabLayout.setCheckedIndex(position, false);
//                setCurEmotionPage(position);
//            }
//        });

//        showEmojiGridView();
        show(listener);

    }

    RPagerAdapter pagerAdapter = new RPagerAdapter() {
        @Override
        protected View getView(LayoutInflater from, ViewGroup container, int position) {

            int pos = position;

            EmojiRecyclerView recyclerView = new EmojiRecyclerView(context, pos * EMOJI_PER_PAGE);
//            recyclerView.setOnEmojiSelectListener(mOnEmojiSelectListener);
            container.addView(recyclerView);
            return recyclerView;
        }

        @Override
        public int getCount() {
            return pageCount == 0 ? 1 : pageCount;
        }
    };

    private void setCurEmotionPage(int position) {
        setCurPage(position, pageCount);
    }

    public void show(IEmoticonSelectedListener listener) {
        setListener(listener);

        if (loaded)
            return;
        loadStickers();
        loaded = true;

        show();
    }

    private void show() {
        if (listener == null) {
            L.i("sticker", "show picker view when listener is null");
        }
        if (!withSticker) {
            showEmojiGridView();
        } else {
            onEmoticonBtnChecked(0);
            setSelectedVisible(0);
        }
    }

    public void showEmoji() {
        // 只显示emoji
        // 1. pageNumberLayout 只显示第一个emoji 其余隐藏
        for (int i = 0 ; i < tabView.getChildCount() ; i ++) {
            if (i != 0) {
                tabView.getChildAt(i).setVisibility(View.GONE);
                continue;
            }
            tabView.getChildAt(0).setVisibility(View.VISIBLE);
        }
        // 2. EmoticonView 判断显示数据源
        gifView.showEmojiOnly();
    }

    public void showSticker() {
        // 只显示自定义表情
        // 1. pageNumberLayout 第一个emoji 隐藏
        for (int i = 0 ; i < tabView.getChildCount() ; i ++) {
            if (i == 0) {
                tabView.getChildAt(i).setVisibility(View.GONE);
            } else {
                tabView.getChildAt(i).setVisibility(View.VISIBLE);
            }
        }

        gifView.showExpressionOnly();
    }

    private void onEmoticonBtnChecked(int index) {
        updateTabButton(index);
        showEmotPager(index);
    }

    // 添加各个tab按钮
    View.OnClickListener tabCheckListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onEmoticonBtnChecked(v.getId());
        }
    };

    private void loadStickers() {
        if (!withSticker) {
            scrollView.setVisibility(View.GONE);
            return;
        }

        final StickerManager manager = StickerManager.getInstance();

        tabView.removeAllViews();

        int index = 0;

        // emoji表情
        CheckedImageButton btn = addEmoticonTabBtn(index++, tabCheckListener);
        btn.setNormalImageId(R.drawable.nim_emoji_icon_inactive);
        btn.setCheckedImageId(R.drawable.nim_emoji_icon);

        // 贴图
        List<StickerCategory> categories = manager.getCategories();
        for (StickerCategory category : categories) {
            btn = addEmoticonTabBtn(index++, tabCheckListener);
            setCheckedButtomImage(btn, category);
        }
    }

    private CheckedImageButton addEmoticonTabBtn(int index, View.OnClickListener listener) {
        CheckedImageButton emotBtn = new CheckedImageButton(context);
        emotBtn.setNormalBkResId(R.drawable.emoji_sticker_button_background_normal_layer_list);
        emotBtn.setCheckedBkResId(R.drawable.emoji_sticker_button_background_pressed_layer_list);
        emotBtn.setId(index);
        emotBtn.setOnClickListener(listener);
        emotBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        emotBtn.setPaddingValue(ScreenUtil.dip2px(7));

        final int emojiBtnWidth = ScreenUtil.dip2px(50);
        final int emojiBtnHeight = ScreenUtil.dip2px(44);

        tabView.addView(emotBtn);

        ViewGroup.LayoutParams emojBtnLayoutParams = emotBtn.getLayoutParams();
        emojBtnLayoutParams.width = emojiBtnWidth;
        emojBtnLayoutParams.height = emojiBtnHeight;
        emotBtn.setLayoutParams(emojBtnLayoutParams);

        return emotBtn;
    }

    private void setSelectedVisible(final int index) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (scrollView.getChildAt(0).getWidth() == 0) {
                    uiHandler.postDelayed(this, 100);
                }
                int x = -1;
                View child = tabView.getChildAt(index);
                if (child != null) {
                    if (child.getRight() > scrollView.getWidth()) {
                        x = child.getRight() - scrollView.getWidth();
                    }
                }
                if (x != -1) {
                    scrollView.smoothScrollTo(x, 0);
                }
            }
        };
        uiHandler.postDelayed(runnable, 100);
    }

    private void showEmotPager(int index) {
        if (gifView == null) {
            gifView = new EmoticonView(context, listener, mUIViewPager, pageNumberLayout);
            gifView.setCategoryChangCheckedCallback(this);
        }

        gifView.showStickers(index);
    }

    private void setCheckedButtomImage(CheckedImageButton btn, StickerCategory category) {
        try {
            InputStream is = category.getCoverNormalInputStream(context);
            if (is != null) {
                Bitmap bmp = BitmapDecoder.decode(is);
                btn.setNormalImage(bmp);
                is.close();
            }
            is = category.getCoverNormalInputStream(context);
            if (is != null) {
                Bitmap bmp = BitmapDecoder.decode(is);
                btn.setCheckedImage(bmp);
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTabButton(int index) {
        for (int i = 0; i < tabView.getChildCount(); ++i) {
            View child = tabView.getChildAt(i);
            if (child instanceof FrameLayout) {
                child = ((FrameLayout) child).getChildAt(0);
            }

            if (child != null && child instanceof CheckedImageButton) {
                CheckedImageButton tabButton = (CheckedImageButton) child;
                if (tabButton.isChecked() && i != index) {
                    tabButton.setChecked(false);
                } else if (!tabButton.isChecked() && i == index) {
                    tabButton.setChecked(true);
                }
            }
        }
    }

    private void showEmojiGridView() {
        pageCount = (int) Math.ceil(EmojiManager.getDisplayCount() / (float) EMOJI_PER_PAGE);
        pagerAdapter.notifyDataSetChanged();
        resetEmotionPager();
    }

    public void setListener(IEmoticonSelectedListener listener) {
        if (listener != null) {
            this.listener = listener;
        } else {
            L.i("sticker", "listener is null");
        }
    }

    private void resetEmotionPager() {
        setCurEmotionPage(0);
        mUIViewPager.setCurrentItem(0, false);
    }

    private void setCurPage(int page, int pageCount) {
        int hasCount = pageNumberLayout.getChildCount();
        int forMax = Math.max(hasCount, pageCount);

        ImageView imgCur = null;
        for (int i = 0; i < forMax; i++) {
            if (pageCount <= hasCount) {
                if (i >= pageCount) {
                    pageNumberLayout.getChildAt(i).setVisibility(View.GONE);
                    continue;
                } else {
                    imgCur = (ImageView) pageNumberLayout.getChildAt(i);
                }
            } else {
                if (i < hasCount) {
                    imgCur = (ImageView) pageNumberLayout.getChildAt(i);
                } else {
                    imgCur = new ImageView(context);
                    imgCur.setBackgroundResource(R.drawable.nim_view_pager_indicator_selector);
                    pageNumberLayout.addView(imgCur);
                }
            }

            imgCur.setId(i);
            imgCur.setSelected(i == page); // 判断当前页码来更新
            imgCur.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCategoryChanged(int index) {
        if (categoryIndex == index) {
            return;
        }

        categoryIndex = index;
        updateTabButton(index);
    }


    public interface OnEmojiSelectListener {
        void onEmojiText(String emoji);
    }

}
