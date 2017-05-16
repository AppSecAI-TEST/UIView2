package com.hn.d.valley.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.angcyo.uiview.skin.SkinHelper;
import com.hn.d.valley.R;

/**
 * Created by angcyo on 2017-04-22.
 */

public class HnCheckBox extends AppCompatCheckBox {
    public HnCheckBox(Context context) {
        super(context);
    }

    public HnCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isInEditMode()) {
            return;
        }
        ColorStateList stateList = new ColorStateList(
                new int[][]{{android.R.attr.state_checked}, {}},
                new int[]{SkinHelper.getSkin().getThemeSubColor(), ContextCompat.getColor(getContext(), R.color.default_base_bg_disable)});
        setSupportButtonTintList(stateList);
    }
}
