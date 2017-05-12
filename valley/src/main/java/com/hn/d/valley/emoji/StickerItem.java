package com.hn.d.valley.emoji;

public class StickerItem {
    private String category;//类别名
    private String name;
    private String meaning;

    public StickerItem(String category, String name,String meaning) {
        this.category = category;
        this.name = name;
        this.meaning = meaning;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getIdentifier() {
        return category + "/" + name;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof StickerItem) {
            StickerItem item = (StickerItem) o;
            return item.getCategory().equals(category) && item.getName().equals(name);
        }

        return false;
    }
}
