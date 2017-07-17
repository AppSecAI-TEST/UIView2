package com.hn.d.valley.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.skin.SkinUtils;

/**
 * Created by angcyo on 2017-04-21.
 */

public class HnEditText extends ExEditText {
    public HnEditText(Context context) {
        super(context);
    }

    public HnEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            SkinUtils.setEditText(this);
        }
    }
}
