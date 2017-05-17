package com.hn.d.valley.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.emoji.MoonUtil;

/**
 * Created by angcyo on 2017-04-21.
 */

public class HnExEditText extends ExEditText {
    public HnExEditText(Context context) {
        super(context);
    }

    public HnExEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnExEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void showEmoji() {
        MoonUtil.show(getContext(), this, string());
    }
}
