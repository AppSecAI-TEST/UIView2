package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：性别和等级一体
 * 创建人员：Robi
 * 创建时间：2017/01/05 17:02
 * 修改人员：Robi
 * 修改时间：2017/01/05 17:02
 * 修改备注：
 * Version: 1.0.0
 */
public class HnGenderView extends LinearLayout {

    private ImageView mSexImageView;
    private ImageView mLevelImageView;
    private TextView mConstellationTextView;
    private TextView mCharmTextView;

    public HnGenderView(Context context) {
        super(context);
        initLayout();
    }

    public HnGenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public HnGenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }

    private void initLayout() {
        mSexImageView = new ImageView(getContext());
        mSexImageView.setVisibility(GONE);
        addView(mSexImageView, new ViewGroup.LayoutParams(-2, -2));

        mLevelImageView = new ImageView(getContext());
        mLevelImageView.setVisibility(GONE);
        LinearLayout.LayoutParams params = new LayoutParams(-2, -2);
        params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.base_mdpi);
        addView(mLevelImageView, params);

        float density = getResources().getDisplayMetrics().density;
        mConstellationTextView = new TextView(getContext());
        mConstellationTextView.setVisibility(GONE);
        mConstellationTextView.setTextSize(9);
        mConstellationTextView.setTextColor(Color.WHITE);
        UI.setBackgroundDrawable(mConstellationTextView,
                ResUtil.createDrawable(Color.parseColor("#bd74e8"), 1 * density));
        params = new LayoutParams(-2, -2);
        params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.base_mdpi);
        mConstellationTextView.setPadding(((int) (2 * density)), (int) (1 * density), (int) (2 * density), (int) (1 * density));
        addView(mConstellationTextView, params);

        mCharmTextView = new RTextView(getContext());
        mCharmTextView.setVisibility(GONE);
        mCharmTextView.setTextSize(9);
        mCharmTextView.setTextColor(Color.WHITE);
        mCharmTextView.setTag("魅力%1$s");
        UI.setBackgroundDrawable(mCharmTextView,
                ResUtil.createDrawable(Color.parseColor("#51D4E0"), 1 * density));
        params = new LayoutParams(-2, -2);
        params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.base_mdpi);
        mCharmTextView.setPadding(((int) (2 * density)), (int) (1 * density), (int) (2 * density), (int) (1 * density));
        addView(mCharmTextView, params);
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.default_text_size10));
//        setTextColor(Color.WHITE);
//        setGravity(Gravity.CENTER_VERTICAL);
//        final float density = getResources().getDisplayMetrics().density;
//        setPadding((int) (density * 4), 0, 0, 0);
//        setMinWidth((int) (density * 20));
//        if (getTag() == null) {
//            setTag("1");
//        }
//    }

//    @Override
//    public void setTag(Object tag) {
//        super.setTag(tag);
//        if (tag.equals("1")) {
//            setBackgroundResource(R.drawable.gender_boy);
//        } else if (tag.equals("2")) {
//            setBackgroundResource(R.drawable.gender_gril);
//        } else {
//            setBackgroundColor(getResources().getColor(R.color.colorAccent));
//        }
//    }

    /**
     * 设置行性别和等级
     */
    public void setGender(String sex, String level) {
        setGender(sex, level, "", "");
    }

    public void setGender(String sex) {
        setGender(sex, "", "", "");
    }

    /**
     * 星座
     */
    public void setGender(String sex, String level, String constellation, String charm) {
        //setTag(sex);
        //setText(level);
        if (TextUtils.isEmpty(sex)) {
            return;
        }
        if (sex.equals("1")) {
            mSexImageView.setVisibility(VISIBLE);
            mSexImageView.setImageResource(R.drawable.boy);
        } else if (sex.equals("2")) {
            mSexImageView.setVisibility(VISIBLE);
            mSexImageView.setImageResource(R.drawable.girl);
        } else {
            mSexImageView.setVisibility(GONE);
        }

        //mLevelImageView.setVisibility(VISIBLE);//取消等级显示,星期三 2017-8-2
        switch (level) {
            case "2":
                mLevelImageView.setImageResource(R.drawable.level_2);
                break;
            case "3":
                mLevelImageView.setImageResource(R.drawable.level_3);
                break;
            case "4":
                mLevelImageView.setImageResource(R.drawable.level_4);
                break;
            case "5":
                mLevelImageView.setImageResource(R.drawable.level_5);
                break;
            case "6":
                mLevelImageView.setImageResource(R.drawable.level_6);
                break;
            default:
                mLevelImageView.setImageResource(R.drawable.level_1);
                break;
        }

        if (TextUtils.isEmpty(constellation)) {
            mConstellationTextView.setVisibility(GONE);
        } else {
            mConstellationTextView.setVisibility(VISIBLE);
            mConstellationTextView.setText(constellation);
        }

        if (TextUtils.isEmpty(charm)) {
            mCharmTextView.setVisibility(GONE);
        } else {
            mCharmTextView.setVisibility(VISIBLE);
            mCharmTextView.setText(charm);
        }
    }

//    @Override
//    public void setText(CharSequence text, BufferType type) {
//        super.setText("V" + text, type);
//    }
}
