package com.hn.d.valley.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.angcyo.uiview.skin.SkinHelper;

/**
 * Created by angcyo on 2017-04-21.
 */

public class HnPasswordSeeBox extends AppCompatCheckBox {
    public HnPasswordSeeBox(Context context) {
        super(context);
    }

    public HnPasswordSeeBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ColorStateList stateList = new ColorStateList(
                new int[][]{{android.R.attr.state_checked}, {}},
                new int[]{SkinHelper.getSkin().getThemeSubColor(), Color.WHITE});
        setButtonTintList(stateList);
    }
}
