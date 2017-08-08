package com.hn.d.valley.emoji;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.utils.file.FileUtil;
import com.hn.d.valley.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 贴图管理类
 */
public class StickerManager {
    private final String TAG = "StickerManager";

    private static StickerManager instance;

    public static final String CATEGORY_EXPRESSION = "expression";
    public static final String CATEGORY_HN = "hn_expression";
    public static final String DICE = "dice";
    public static final String POCKER = "pocker";
    public static final String BIG_SMALL = "big";

    /**
     * 数据源
     */
    private List<StickerCategory> stickerCategories = new ArrayList<>();
    private Map<String, StickerCategory> stickerCategoryMap = new HashMap<>();
    private Map<String, Integer> stickerOrder = new HashMap<>(3);

    public static StickerManager getInstance() {
        if (instance == null) {
            instance = new StickerManager();
        }

        return instance;
    }

    public StickerManager() {
        initStickerOrder();
        loadStickerCategory();
    }

    public void init() {
        Log.i(TAG, "Sticker Manager init...");
    }

    private void initStickerOrder() {
        // 默认贴图顺序
        stickerOrder.put(DICE, 1);
        stickerOrder.put(POCKER, 2);
        stickerOrder.put(CATEGORY_EXPRESSION, 3);
        stickerOrder.put(CATEGORY_HN, 4);
    }

    public String[] genNameArray(String name) {
        Resources resources = RApplication.getApp().getResources();
        String[] meanArray = resources.getStringArray(R.array.gifsticker);
        switch (name) {
            case DICE:
                meanArray = resources.getStringArray(R.array.dice_sticker);
                break;
            case POCKER:
                meanArray = resources.getStringArray(R.array.poker_sticker);
                break;
            case CATEGORY_EXPRESSION:
                meanArray = resources.getStringArray(R.array.gifsticker);
                break;
            case CATEGORY_HN:
                meanArray = resources.getStringArray(R.array.hn_sticker);
                break;
        }

        return meanArray;

    }

    private boolean isSystemSticker(String category) {
        return CATEGORY_EXPRESSION.equals(category);
    }

    private int getStickerOrder(String categoryName) {
        if (stickerOrder.containsKey(categoryName)) {
            return stickerOrder.get(categoryName);
        } else {
            return 100;
        }
    }

    private void loadStickerCategory() {
        AssetManager assetManager = RApplication.getApp().getResources().getAssets();
        try {
            String[] files = assetManager.list("sticker");
            StickerCategory category;
            for (String name : files) {
                if (!FileUtil.hasExtentsion(name)) {
                    category = new StickerCategory(name, name, true, getStickerOrder(name));
                    stickerCategories.add(category);
                    stickerCategoryMap.put(name, category);
                }
            }
            // 排序
            Collections.sort(stickerCategories, new Comparator<StickerCategory>() {
                @Override
                public int compare(StickerCategory l, StickerCategory r) {
                    return l.getOrder() - r.getOrder();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<StickerCategory> getCategories() {
        return stickerCategories;
    }

    public synchronized StickerCategory getCategory(String name) {
        return stickerCategoryMap.get(name);
    }

    public String getStickerBitmapUri(String categoryName, String stickerName) {
        StickerManager manager = StickerManager.getInstance();
        StickerCategory category = manager.getCategory(categoryName);
        if (category == null) {
            return null;
        }

//        if (isSystemSticker(categoryName)) {
        if (categoryName.equals(DICE) || categoryName.equals(POCKER)) {
            if (stickerName.contains("dice") && !stickerName.contains("t_")) {
                stickerName = "t_" + stickerName;
            }
            if (!stickerName.contains(".png")) {
                stickerName += ".png";
            }
        }  else {
            if (!stickerName.contains(".gif")) {
                stickerName += ".gif";
            }
        }

        String path = "sticker/" + category.getName() + "/" + stickerName;
        L.d("stickermanager getStickerBitmap : path " + path);
        return "file:///android_asset/" + path;
//        }

//        return null;
    }


    private Bitmap resize(Bitmap source, int size) {
        if (source == null) {
            return null;
        }
        int scale = 1;
        if (size < source.getWidth() / 4) {
            scale = 4;
        } else if (size < source.getWidth() * 3 / 4) {
            scale = 2;
        } else if (size < source.getWidth()) {
            scale = 1;
        }
        int width = source.getWidth() / scale;
        int height = source.getHeight() / scale;

        if (width >= source.getWidth() && height >= source.getHeight()) {
            return source;
        } else {
            return ThumbnailUtils.extractThumbnail(source, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
    }
}
