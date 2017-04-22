package com.hn.d.valley.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.angcyo.uiview.skin.SkinHelper;

/**
 * Created by angcyo on 2017-04-22.
 */

public class HnRadioButton extends AppCompatRadioButton {
    public HnRadioButton(Context context) {
        super(context);
    }

    public HnRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
