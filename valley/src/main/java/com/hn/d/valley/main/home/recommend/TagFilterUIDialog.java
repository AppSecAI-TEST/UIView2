package com.hn.d.valley.main.home.recommend;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.widget.RFlowLayout;
import com.angcyo.uiview.widget.RTextCheckView;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.control.TagsControl;

import rx.functions.Action1;
import rx.functions.Action2;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/10 15:25
 * 修改人员：Robi
 * 修改时间：2017/01/10 15:25
 * 修改备注：
 * Version: 1.0.0
 */
public class TagFilterUIDialog extends UIIDialogImpl {

    private RFlowLayout mFlowLayout;
    private RelativeLayout mRootLayout;

    private Action1<Tag> selectorAction;
    /**
     * 当前高亮的tag
     */
    private Tag currentTag;


    public TagFilterUIDialog(Action1<Tag> selectorAction, Tag currentTag) {
        this.selectorAction = selectorAction;
        this.currentTag = currentTag;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        mDialogRootLayout.fixInsertsTop();
        mRootLayout = new RelativeLayout(mActivity);

        mFlowLayout = new RFlowLayout(mActivity);
        mFlowLayout.setBackgroundColor(Color.WHITE);

        int padding = (int) ResUtil.dpToPx(mActivity, 10);
        mFlowLayout.setPadding(padding, padding, padding, padding);
        mRootLayout.addView(mFlowLayout, new ViewGroup.LayoutParams(-1, -2));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        params.setMargins(0, mActivity.getResources().getDimensionPixelSize(R.dimen.tags_dialog_offset), 0, 0);
        dialogRootLayout.addView(mRootLayout, params);

        return mRootLayout;
    }

    @Override
    public View getAnimView() {
        return mFlowLayout;
    }

    @Override
    public View getDialogDimView() {
        return mRootLayout;
    }

    @Override
    protected void initDialogContentView() {
        super.initDialogContentView();
        mRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });
        TagsControl.inflate(mActivity, mFlowLayout, true, new Action2<RTextCheckView, Tag>() {
            @Override
            public void call(RTextCheckView rTextCheckView, Tag tag) {
                if (currentTag.equals(tag)) {
                    rTextCheckView.setChecked(true);
                }

                rTextCheckView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishDialog();
                        selectorAction.call((Tag) v.getTag());
                    }
                });
            }
        });
    }

    @Override
    public Animation loadStartAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        setDefaultConfig(translateAnimation);
        setDefaultConfig(alphaAnimation);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        return animationSet;
    }

    @Override
    public Animation loadFinishAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        setDefaultConfig(translateAnimation);
        setDefaultConfig(alphaAnimation);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        return animationSet;
    }

    @Override
    public Animation loadLayoutAnimation() {
        Animation animation = super.loadLayoutAnimation();
        animation.setDuration(160);
        return animation;
    }
}
