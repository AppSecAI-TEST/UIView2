package com.hn.d.valley.emoji;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.widget.viewpager.RPagerAdapter;
import com.hn.d.valley.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 贴图显示viewpager
 */
public class EmoticonView {

    private static final String TAG = EmoticonView.class.getSimpleName();

    private ViewPager emotPager;
    private LinearLayout pageNumberLayout;
    /**
     * 总页数.
     */
    private int pageCount;

    /**
     * 每页显示的数量，Adapter保持一致.
     */
    public static final int EMOJI_PER_PAGE = 26; // 最后一个是删除键
    public static final int STICKER_PER_PAGE = 10;

    private Context context;
    private IEmoticonSelectedListener listener;
    private EmoticonViewPaperAdapter pagerAdapter = new EmoticonViewPaperAdapter();

    /**
     * 所有表情贴图支持横向滑动切换
     */
    private int categoryIndex;                           // 当套贴图的在picker中的索引
    private boolean isDataInitialized = false;             // 数据源只需要初始化一次,变更时再初始化
    private List<StickerCategory> categoryDataList;       // 表情贴图数据源
    private List<Integer> categoryPageNumberList;           // 每套表情贴图对应的页数
    private int[] pagerIndexInfo = new int[2];           // 0：category index；1：pager index in category
    private IEmoticonCategoryChanged categoryChangedCallback; // 横向滑动切换时回调picker
    private boolean isShowSticker;                          // 是否显示贴图
    private boolean onNormal = true;                          // 是否正常状态显示 不区分emoji 和自定义表情
    private boolean isFilter = false;

    public EmoticonView(Context context, IEmoticonSelectedListener mlistener,boolean isFilter,
                        ViewPager mCurPage, LinearLayout pageNumberLayout) {
        this.context = context.getApplicationContext();
        this.isFilter = isFilter;
        this.listener = mlistener;
        this.pageNumberLayout = pageNumberLayout;
        this.emotPager = mCurPage;

        emotPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (categoryDataList != null) {
                    // 显示所有贴图表情
                    setCurStickerPage(position);
                    if (categoryChangedCallback != null) {
                        int currentCategoryChecked = pagerIndexInfo[0];// 当前那种类别被选中
                        categoryChangedCallback.onCategoryChanged(currentCategoryChecked);
                    }
                } else {
                    // 只显示表情
                    setCurEmotionPage(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        emotPager.setAdapter(pagerAdapter);
        emotPager.setOffscreenPageLimit(1);
    }

    public void setCategoryDataReloadFlag() {
        isDataInitialized = false;
    }

    public void showStickers(int index) {
        // 判断是否需要变化
        if (isDataInitialized && getPagerInfo(emotPager.getCurrentItem()) != null
                && pagerIndexInfo[0] == index && pagerIndexInfo[1] == 0) {
            return;
        }

        this.categoryIndex = index;
        showStickerGridView();
    }

    public void showEmojiOnly() {
        onNormal = false;
        isShowSticker = false;
        this.categoryIndex = 0;
        pageCount = categoryPageNumberList.get(categoryIndex);
        setCurStickerPage(0);
        L.d(TAG,"show emojionly pagecount : " + pageCount );
        pagerAdapter.notifyDataSetChanged();
        emotPager.setCurrentItem(0);
    }

    public void emotNotifiDataSetChanged() {
        pagerAdapter.notifyDataSetChanged();
    }

    public void showExpressionOnly() {
        onNormal = false;
        isShowSticker = true;
        this.categoryIndex = 1;
        pageCount = 0;
        for (int i = categoryIndex ; i < categoryPageNumberList.size() ; i ++) {
            pageCount += categoryPageNumberList.get(i);
        }
        setCurStickerPage(0);
        L.d(TAG,"show showExpressionOnly pagecount : " + pageCount );
        pagerAdapter.notifyDataSetChanged();
        emotPager.setCurrentItem(0);
    }

    public void showEmojis() {
        showEmojiGridView();
    }

    private int getCategoryPageCount(StickerCategory category) {
        if (category == null) {
            return (int) Math.ceil(EmojiManager.getDisplayCount() / (float) EMOJI_PER_PAGE);
        } else {
            if (category.hasStickers()) {
                List<StickerItem> stickers = category.getStickers();
                return (int) Math.ceil(stickers.size() / (float) STICKER_PER_PAGE);
            } else {
                return 1;
            }
        }
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

    /**
     * ******************************** 表情  *******************************
     */
    private void showEmojiGridView() {
        pageCount = (int) Math.ceil(EmojiManager.getDisplayCount() / (float) EMOJI_PER_PAGE);
        pagerAdapter.notifyDataSetChanged();
        resetEmotionPager();
    }

    private void resetEmotionPager() {
        setCurEmotionPage(0);
        emotPager.setCurrentItem(0, false);
    }

    private void setCurEmotionPage(int position) {
        setCurPage(position, pageCount);
    }

    public OnItemClickListener emojiListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            int position = emotPager.getCurrentItem();
            int pos = position; // 如果只有表情，那么用默认方式计算
            if (categoryDataList != null && categoryPageNumberList != null) {
                // 包含贴图
                getPagerInfo(position);
                pos = pagerIndexInfo[1];
            }

            int index = arg2 + pos * EMOJI_PER_PAGE;

            if (listener != null) {
                int count = EmojiManager.getDisplayCount();
                if (arg2 == EMOJI_PER_PAGE || index >= count) {
                    listener.onEmojiSelected("/DEL");
                } else {
                    String text = EmojiManager.getDisplayText((int) arg3);
                    if (!TextUtils.isEmpty(text)) {
                        listener.onEmojiSelected(text);
                    }
                }
            }
        }
    };

    /**
     * ******************************** 贴图  *******************************
     */

    private void showStickerGridView() {
        initData();
        pagerAdapter.notifyDataSetChanged();

        // 计算起始的pager index
        int position = 0;
        for (int i = 0; i < categoryPageNumberList.size(); i++) {
            if (i == categoryIndex) {
                break;
            }
            position += categoryPageNumberList.get(i);
        }

        setCurStickerPage(position);
        emotPager.setCurrentItem(position, false);
    }

    private void initData() {
        if (isDataInitialized) {//数据已经初始化，未变动不重新加载数据
            return;
        }

        if (categoryDataList == null) {
            categoryDataList = new ArrayList<>();
        }

        if (categoryPageNumberList == null) {
            categoryPageNumberList = new ArrayList<>();
        }

        categoryDataList.clear();
        categoryPageNumberList.clear();

        final StickerManager manager = StickerManager.getInstance();

        categoryDataList.add(null); // 表情
        categoryPageNumberList.add(getCategoryPageCount(null));

        List<StickerCategory> categories = manager.getCategories(isFilter);

        categoryDataList.addAll(categories); // 贴图
        for (StickerCategory c : categories) {
            categoryPageNumberList.add(getCategoryPageCount(c));
        }

        pageCount = 0;//总页数
        for (Integer count : categoryPageNumberList) {
            pageCount += count;
        }

        isDataInitialized = true;
    }

    // 给定pager中的索引，返回categoryIndex和positionInCategory
    private int[] getPagerInfo(int position) {
        if (categoryDataList == null || categoryPageNumberList == null) {
            return pagerIndexInfo;
        }

        int cIndex = categoryIndex;
        int startIndex = 0;
        int pageNumberPerCategory = 0;

        int i = 0;
        if (isShowSticker && !onNormal) {
            i = 1;

        }
        for (; i < categoryPageNumberList.size(); i++) {
            pageNumberPerCategory = categoryPageNumberList.get(i);
            if (position < startIndex + pageNumberPerCategory) {
                cIndex = i;
                break;
            }
            startIndex += pageNumberPerCategory;
        }

        this.pagerIndexInfo[0] = cIndex;
        this.pagerIndexInfo[1] = position - startIndex;

        return pagerIndexInfo;
    }

    private void setCurStickerPage(int position) {
        getPagerInfo(position);
        int categoryIndex = pagerIndexInfo[0];
        int pageIndexInCategory = pagerIndexInfo[1];
        int categoryPageCount = categoryPageNumberList.get(categoryIndex);

        setCurPage(pageIndexInCategory, categoryPageCount);
    }

    public void setCategoryChangCheckedCallback(IEmoticonCategoryChanged callback) {
        this.categoryChangedCallback = callback;
    }

    private OnItemClickListener stickerListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            int position = emotPager.getCurrentItem();
            getPagerInfo(position);
            int cIndex = pagerIndexInfo[0];
            int pos = pagerIndexInfo[1];
            StickerCategory c = categoryDataList.get(cIndex);
            int index = arg2 + pos * STICKER_PER_PAGE; // 在category中贴图的index

            if (index >= c.getStickers().size()) {
                L.i("sticker", "index " + index + " larger than size " + c.getStickers().size());
                return;
            }

            if (listener != null) {
                StickerManager manager = StickerManager.getInstance();
                List<StickerItem> stickers = c.getStickers();
                StickerItem sticker = stickers.get(index);
                StickerCategory real = manager.getCategory(sticker.getCategory());

                if (real == null) {
                    return;
                }

                listener.onStickerSelected(sticker.getCategory(), sticker.getName());
            }
        }
    };


    /**
     * ***************************** PagerAdapter ****************************
     */
    private class EmoticonViewPaperAdapter extends RPagerAdapter {

        @Override
        public int getCount() {
            return pageCount == 0 ? 1 : pageCount;
        }


        @Override
        protected View getView(LayoutInflater from, ViewGroup container, int position) {
            L.d(TAG,"getView position : " + position );
            StickerCategory category;

            int pos;
            if (categoryDataList != null && categoryDataList.size() > 0 && categoryPageNumberList != null
                    && categoryPageNumberList.size() > 0 || (!onNormal && isShowSticker)) {
                // 显示所有贴图&表情
                getPagerInfo(position);
                int cIndex = pagerIndexInfo[0];
                category = categoryDataList.get(cIndex);
                pos = pagerIndexInfo[1];
            } else {
                // 只显示表情
                category = null;
                pos = position;
            }

            if (category == null) {
                pageNumberLayout.setVisibility(View.VISIBLE);
                EmojiRecyclerView recyclerView = new EmojiRecyclerView(context, pos * EMOJI_PER_PAGE);
                recyclerView.setOnEmojiSelectListener(listener);
                container.addView(recyclerView);
                return recyclerView;
            } else {
                pageNumberLayout.setVisibility(View.VISIBLE);
                StickerRecyclerView recyclerView = new StickerRecyclerView(context,category, pos * STICKER_PER_PAGE);
                recyclerView.setOnEmojiSelectListener(listener);
                container.addView(recyclerView);
                return recyclerView;
            }
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
