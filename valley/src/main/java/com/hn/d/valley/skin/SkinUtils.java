package com.hn.d.valley.skin;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.ThemeBean;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.me.setting.MyQrCodeUIView;
import com.hn.d.valley.service.SettingService;
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

    /**
     * 从服务器获取主题信息
     */
    public static void init(final Activity activity, final ILayout layout) {
        RRetrofit.create(SettingService.class)
                .getSkin(Param.buildMap())
                .compose(Rx.transformer(ThemeBean.class))
                .subscribe(new BaseSingleSubscriber<ThemeBean>() {
                    @Override
                    public void onSucceed(ThemeBean bean) {
                        super.onSucceed(bean);
                        if ("1001".equalsIgnoreCase(bean.getTheme_skin())) {
                            setSkin(activity, layout, SkinManagerUIView.SKIN_BLACK);
                        } else if ("1002".equalsIgnoreCase(bean.getTheme_skin())) {
                            setSkin(activity, layout, SkinManagerUIView.SKIN_GREEN);
                        } else if ("1003".equalsIgnoreCase(bean.getTheme_skin())) {
                            setSkin(activity, layout, SkinManagerUIView.SKIN_BLUE);
                        }
                    }
                });
    }

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

        String s = "";
        if (skin == 1) {
            s = "1001";
        } else if (skin == 2) {
            s = "1002";
        } else if (skin == 3) {
            s = "1003";
        }
        RRetrofit.create(SettingService.class)
                .setSkin(Param.buildMap("key:theme_skin", "val:" + s))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {

                });
    }

    public static void setSkin(Activity activity, ILayout iLayout, int skin) {
        Integer oldSkin = getSkin();
        SkinUtils.setSkin(skin);
        activity.setTheme(SkinUtils.getSkinStyle());
        if (oldSkin != skin) {
            SkinHelper.changeSkin(getSkin(activity), iLayout);
        }
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

    public static void setEditText(View editText) {
        ResUtil.setBgDrawable(editText, SkinHelper.getThemeRoundBorderSelector());
    }

    public static void setButton(View editText) {
        ResUtil.setBgDrawable(editText, SkinHelper.getSkin().getThemeMaskBackgroundRoundSelector());
    }
}
