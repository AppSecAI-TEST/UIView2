package com.hn.d.valley.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

        mLevelImageView.setVisibility(VISIBLE);
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

    }

//    @Override
//    public void setText(CharSequence text, BufferType type) {
//        super.setText("V" + text, type);
//    }
}
