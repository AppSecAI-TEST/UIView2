package com.hn.d.valley.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.skin.SkinUtils;

/**
 * Created by angcyo on 2017-04-21.
 */

public class HnButton extends RTextView {
    public HnButton(Context context) {
        super(context);
    }

    public HnButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        SkinUtils.setButton(this);
    }
}
