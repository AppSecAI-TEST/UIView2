package com.hn.d.valley.skin;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.hn.d.valley.R;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.me.setting.MyQrCodeUIView;
import com.orhanobut.hawk.Hawk;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/01 17:50
 * 修改人员：Robi
 * 修改时间：2017/04/01 17:50
 * 修改备注：
 * Version: 1.0.0
 */
public class SkinUtils {
    public static ISkin getSkin(Context context) {
        int skin = getSkin();
        ISkin iSkin = SkinHelper.getSkin();
        switch (skin) {
            case SkinManagerUIView.SKIN_BLACK:
                iSkin = new BlackSkin(context);
                break;
            case SkinManagerUIView.SKIN_BLUE:
                iSkin = new BlueSkin(context);
                break;
            case SkinManagerUIView.SKIN_GREEN:
                iSkin = new GreenSkin(context);
                break;
        }
        return iSkin;
    }

    public static Integer getSkin() {
        return Hawk.get(SkinManagerUIView.SKIN_KEY, SkinManagerUIView.SKIN_BLACK);
    }

    public static void setSkin(int skin) {
        Hawk.put(SkinManagerUIView.SKIN_KEY, skin);
        Hawk.put(MyQrCodeUIView.KEY_NEED_CREATE_QR, true);
    }

    public static int getSkinStyle() {
        int skin = getSkin();
        int style = R.style.AppTheme;
        switch (skin) {
            case SkinManagerUIView.SKIN_BLACK:
                style = R.style.BlackTheme;
                break;
            case SkinManagerUIView.SKIN_BLUE:
                style = R.style.BlueTheme;
                break;
            case SkinManagerUIView.SKIN_GREEN:
                style = R.style.GreenTheme;
                break;
        }
        return style;
    }

    /**
     * 根据主题设置,笑脸View的ico
     */
    public static void setExpressView(ImageView imageView) {
        int skin = getSkin();
        switch (skin) {
            case SkinManagerUIView.SKIN_BLACK:
                imageView.setImageDrawable(
                        ResUtil.generateClickDrawable(imageView.getContext(), R.drawable.message_expression_n_black, R.drawable.message_expression_s_black)
                );
                break;
            case SkinManagerUIView.SKIN_BLUE:
                imageView.setImageDrawable(
                        ResUtil.generateClickDrawable(imageView.getContext(), R.drawable.message_expression_n_blue, R.drawable.message_expression_s_blue)
                );
                break;
            case SkinManagerUIView.SKIN_GREEN:
                imageView.setImageDrawable(
                        ResUtil.generateClickDrawable(imageView.getContext(), R.drawable.message_expression_n, R.drawable.message_expression_s)
                );
                break;
        }
    }

    public static void setVoiceView(ImageView imageView) {
        int skin = getSkin();
        switch (skin) {
            case SkinManagerUIView.SKIN_BLACK:
                imageView.setImageDrawable(
                        ResUtil.generateClickDrawable(imageView.getContext(), R.drawable.message_voice_n_black, R.drawable.message_voice_s_black)
                );
                break;
            case SkinManagerUIView.SKIN_BLUE:
                imageView.setImageDrawable(
                        ResUtil.generateClickDrawable(imageView.getContext(), R.drawable.message_voice_n_blue, R.drawable.message_voice_s_blue)
                );
                break;
            case SkinManagerUIView.SKIN_GREEN:
                imageView.setImageDrawable(
                        ResUtil.generateClickDrawable(imageView.getContext(), R.drawable.message_voice_n, R.drawable.message_voice_s)
                );
                break;
        }
    }

    public static void setKeyboardView(ImageView imageView) {
        int skin = getSkin();
        switch (skin) {
            case SkinManagerUIView.SKIN_BLACK:
                imageView.setImageDrawable(
                        ResUtil.generateClickDrawable(imageView.getContext(), R.drawable.message_keyboard_n_black, R.drawable.message_keyboard_s_black)
                );
                break;
            case SkinManagerUIView.SKIN_BLUE:
                imageView.setImageDrawable(
                        ResUtil.generateClickDrawable(imageView.getContext(), R.drawable.message_keyboard_n_blue, R.drawable.message_keyboard_s_blue)
                );
                break;
            case SkinManagerUIView.SKIN_GREEN:
                imageView.setImageDrawable(
                        ResUtil.generateClickDrawable(imageView.getContext(), R.drawable.message_keyboard_n, R.drawable.message_keyboard_s)
                );
                break;
        }
    }

    public static void setExpressView(CompoundButton compoundButton) {
        int skin = getSkin();
        switch (skin) {
            case SkinManagerUIView.SKIN_BLACK:
                compoundButton.setButtonDrawable(
                        ResUtil.generateClickDrawable(compoundButton.getContext(), R.drawable.message_expression_n_black, R.drawable.message_expression_s_black)
                );
                break;
            case SkinManagerUIView.SKIN_BLUE:
                compoundButton.setButtonDrawable(
                        ResUtil.generateClickDrawable(compoundButton.getContext(), R.drawable.message_expression_n_blue, R.drawable.message_expression_s_blue)
                );
                break;
            case SkinManagerUIView.SKIN_GREEN:
                compoundButton.setButtonDrawable(
                        ResUtil.generateClickDrawable(compoundButton.getContext(), R.drawable.message_expression_n, R.drawable.message_expression_s)
                );
                break;
        }
    }

    public static void setVoiceView(CompoundButton compoundButton) {
        int skin = getSkin();
        switch (skin) {
            case SkinManagerUIView.SKIN_BLACK:
                compoundButton.setButtonDrawable(
                        ResUtil.generateClickDrawable(compoundButton.getContext(), R.drawable.message_voice_n_black, R.drawable.message_voice_s_black)
                );
                break;
            case SkinManagerUIView.SKIN_BLUE:
                compoundButton.setButtonDrawable(
                        ResUtil.generateClickDrawable(compoundButton.getContext(), R.drawable.message_voice_n_blue, R.drawable.message_voice_s_blue)
                );
                break;
            case SkinManagerUIView.SKIN_GREEN:
                compoundButton.setButtonDrawable(
                        ResUtil.generateClickDrawable(compoundButton.getContext(), R.drawable.message_voice_n, R.drawable.message_voice_s)
                );
                break;
        }
    }

    public static void setKeyboardView(CompoundButton compoundButton) {
        int skin = getSkin();
        switch (skin) {
            case SkinManagerUIView.SKIN_BLACK:
                compoundButton.setButtonDrawable(
                        ResUtil.generateClickDrawable(compoundButton.getContext(), R.drawable.message_keyboard_n_black, R.drawable.message_keyboard_s_black)
                );
                break;
            case SkinManagerUIView.SKIN_BLUE:
                compoundButton.setButtonDrawable(
                        ResUtil.generateClickDrawable(compoundButton.getContext(), R.drawable.message_keyboard_n_blue, R.drawable.message_keyboard_s_blue)
                );
                break;
            case SkinManagerUIView.SKIN_GREEN:
                compoundButton.setButtonDrawable(
                        ResUtil.generateClickDrawable(compoundButton.getContext(), R.drawable.message_keyboard_n, R.drawable.message_keyboard_s)
                );
                break;
        }
    }

    public static void setAddView(CompoundButton compoundButton) {
        int skin = getSkin();
        switch (skin) {
            case SkinManagerUIView.SKIN_BLACK:
                compoundButton.setButtonDrawable(
                        ResUtil.generateClickDrawable(compoundButton.getContext(), R.drawable.message_add_n_black, R.drawable.message_add_s_black)
                );
                break;
            case SkinManagerUIView.SKIN_BLUE:
                compoundButton.setButtonDrawable(
                        ResUtil.generateClickDrawable(compoundButton.getContext(), R.drawable.message_add_n_blue, R.drawable.message_add_s_blue)
                );
                break;
            case SkinManagerUIView.SKIN_GREEN:
                compoundButton.setButtonDrawable(
                        ResUtil.generateClickDrawable(compoundButton.getContext(), R.drawable.message_add_n, R.drawable.message_add_s)
                );
                break;
        }
    }
}
