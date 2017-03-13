package com.angcyo.uiview.base;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;

import com.angcyo.uiview.recycler.RBaseViewHolder;

/**
 * Created by angcyo on 2017-03-12.
 */

public interface Item {
    void onBindView(RBaseViewHolder holder, int posInData, Item dataBean);

    void setItemOffsets(Rect rect);

    void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position);
}
