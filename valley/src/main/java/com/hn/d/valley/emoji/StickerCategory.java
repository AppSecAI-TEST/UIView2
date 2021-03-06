package com.hn.d.valley.emoji;

import android.content.Context;
import android.content.res.AssetManager;

import com.angcyo.uiview.RApplication;
import com.hn.d.valley.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.hn.d.valley.emoji.StickerManager.CATEGORY_EXPRESSION;
import static com.hn.d.valley.emoji.StickerManager.CATEGORY_HN;
import static com.hn.d.valley.emoji.StickerManager.DICE;
import static com.hn.d.valley.emoji.StickerManager.POCKER;

public class StickerCategory implements Serializable {
    private static final long serialVersionUID = -81692490861539040L;

    private String name; // 贴纸包名
    private String title; // 显示的标题
    private boolean system; // 是否是系统内置表情
    private int order = 0; // 默认顺序

    private transient List<StickerItem> stickers;

    public StickerCategory(String name, String title, boolean system, int order) {
        this.title = title;
        this.name = name;
        this.system = system;
        this.order = order;

        loadStickerData();
    }

    public boolean system() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StickerItem> getStickers() {
        return stickers;
    }

    public boolean hasStickers() {
        return stickers != null && stickers.size() > 0;
    }

    public InputStream getCoverNormalInputStream(Context context) {
        String filename = name + "_s_normal.png";
        return makeFileInputStream(context, filename);
    }

    public InputStream getCoverPressedInputStream(Context context) {
        String filename = name + "_s_pressed.png";
        return makeFileInputStream(context, filename);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        if (stickers == null || stickers.isEmpty()) {
            return 0;
        }

        return stickers.size();
    }

    public int getOrder() {
        return order;
    }

    private InputStream makeFileInputStream(Context context, String filename) {
        try {
            if (system) {
                AssetManager assetManager = context.getResources().getAssets();
                String path = "sticker/" + filename;
                return assetManager.open(path);
            } else {
                // for future
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<StickerItem> loadStickerData() {
//        String[] meanArray = name.equals(CATEGORY_HN)?RApplication.getApp().getResources().getStringArray(R.array.hn_sticker)
//                :RApplication.getApp().getResources().getStringArray(R.array.gifsticker);

        String[] meanArray = RApplication.getApp().getResources().getStringArray(R.array.gifsticker);
        switch (name) {
            case DICE:
                meanArray = RApplication.getApp().getResources().getStringArray(R.array.dice_sticker);
                break;
            case POCKER:
                meanArray = RApplication.getApp().getResources().getStringArray(R.array.poker_sticker);
                break;
            case CATEGORY_EXPRESSION:
                meanArray = RApplication.getApp().getResources().getStringArray(R.array.gifsticker);
                break;
            case CATEGORY_HN:
                meanArray = RApplication.getApp().getResources().getStringArray(R.array.hn_sticker);
                break;
        }

//        String[] meanArray = StickerManager.getInstance().genNameArray(name);

        List<StickerItem> stickers = new ArrayList<>();
        AssetManager assetManager = RApplication.getApp().getResources().getAssets();
        try {
            int index = 0;
            String[] files = assetManager.list("sticker/" + name);
            for (String file : files) {
                stickers.add(new StickerItem(name, file,meanArray[index++]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.stickers = stickers;
        return stickers;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof StickerCategory)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        StickerCategory r = (StickerCategory) o;
        return r.getName().equals(getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
