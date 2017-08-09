package com.hn.d.valley.main.home.recommend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.ScreenUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.Tag;

import java.util.List;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：标签过滤选择对话框
 * 创建人员：Robi
 * 创建时间：2017-4-21
 * 修改人员：Robi
 * 修改时间：2017-4-21
 * 修改备注：
 * Version: 1.0.0
 */
public class TagFilterUIDialog2 extends UIIDialogImpl {

    Action1<List<Tag>> tagsChangedAction;
    private LinearLayout mContentLayout;
    private RelativeLayout mRootLayout;
    private Action1<Tag> selectorAction;
    private List<Tag> mMyTagList;
    /**
     * 当前高亮的tag
     */
    private Tag currentTag;
    private ImageView mArrowView;
    private RRecyclerView mRecyclerView;
    private Rect clickViewRect = new Rect();

    public TagFilterUIDialog2(View clickView/**用来控制对话框显示的位置*/,
                              List<Tag> myTags,
                              Action1<Tag> selectorAction/**选择标签之后的回调*/,
                              Tag currentTag,
                              Action1<List<Tag>> tagsAction/**编辑标签之后的回调*/) {
        this.mMyTagList = myTags;
        this.selectorAction = selectorAction;
        this.currentTag = currentTag;
        clickView.getGlobalVisibleRect(clickViewRect);
        tagsChangedAction = tagsAction;
    }

    @Override
    protected View inflateDialogView(FrameLayout dialogRootLayout, LayoutInflater inflater) {
        //自动控制状态栏的padding
        mDialogRootLayout.fixInsertsTitleTop();
        mRootLayout = new RelativeLayout(mActivity);

        mContentLayout = new LinearLayout(mActivity);
        mArrowView = new ImageView(mActivity);
        mArrowView.setImageResource(R.drawable.jiantou_biaoqian);
        mRecyclerView = new RRecyclerView(mActivity);
        mRecyclerView.setHasFixedSize(true);
        ResUtil.setBgDrawable(mRecyclerView, getDrawable(R.drawable.base_white_round_bg));
        mRecyclerView.addItemDecoration(new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
            @Override
            public void getItemOffsets2(Rect outRect, int position, int edge) {
                int itemCount = mRecyclerView.getAdapter().getItemCount();
                if (position == itemCount - 1 || position == itemCount - 2) {
                    //倒数第一个, 第二个不需要分割线
                    outRect.bottom = 0;
                } else {
                    outRect.bottom = getDimensionPixelOffset(R.dimen.base_line);
                }
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                drawBottomMarginLine(canvas, paint, itemView, offsetRect, itemCount, position,
                        getDimensionPixelOffset(R.dimen.base_xxhdpi), getDimensionPixelOffset(R.dimen.base_xxhdpi));
            }

            @Override
            protected int getPaintColor(Context context) {
                return getColor(R.color.line_color);
            }
        }));

        mContentLayout.setOrientation(LinearLayout.VERTICAL);
        int width = (int) (density() * 200);
        LinearLayout.LayoutParams arrowParam = new LinearLayout.LayoutParams(-2, -2);
        arrowParam.leftMargin = (int) (width * 4 / 5 - 20 * density());//width / 2;
        mContentLayout.addView(mArrowView, arrowParam);
        mContentLayout.addView(mRecyclerView, new ViewGroup.LayoutParams(-2, -2));

        //int padding = (int) ResUtil.dpToPx(mActivity, 10);
        RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(width, ((int) (density() * 260)));
        contentParams.leftMargin = ScreenUtil.screenWidth / 2 - width / 2;//clickViewRect.left - width / 4;
        mRootLayout.addView(mContentLayout, contentParams);

        //顶部偏移标题栏的高度
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        //params.setMargins(0, mActivity.getResources().getDimensionPixelSize(R.dimen.tags_dialog_offset), 0, 0);
        dialogRootLayout.addView(mRootLayout, params);

        return mRootLayout;
    }

    @Override
    public View getAnimView() {
        return mContentLayout;
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
        mRecyclerView.setAdapter(new RBaseAdapter<Tag>(mActivity, mMyTagList) {
            @Override
            protected int getItemLayoutId(int viewType) {
                if (isLast(viewType)) {
                    return R.layout.item_last_tag_view;
                }
                return R.layout.item_single_main_text_view;
            }

            @Override
            public int getItemType(int position) {
                if (isLast(position)) {
                    return position;
                }
                return super.getItemType(position);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount() + 1;
            }

            @Override
            protected void onBindView(RBaseViewHolder holder, int position, final Tag bean) {
                TextView textView = holder.tv(R.id.text_view);
                textView.setGravity(Gravity.CENTER);

                int offset = getDimensionPixelOffset(R.dimen.base_ldpi);

                if (isLast(position)) {
                    holder.itemView.setPadding(offset * 2, offset, offset * 2, offset);
                } else {
                    holder.itemView.setPadding(0, offset, 0, offset);
                }

                if (isLast(position)) {
                    textView.setText(R.string.edit_tag_tip);
                    holder.v(R.id.text_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishDialog();
                            startIView(new TagsManageUIView2(tagsChangedAction));
                        }
                    });
                } else {

                    ((LinearLayout) holder.itemView).setGravity(Gravity.CENTER);

                    if (bean == currentTag) {
                        textView.setTextColor(SkinHelper.getSkin().getThemeSubColor());
                    } else {
                        textView.setTextColor(getColor(R.color.main_text_color));
                    }

                    textView.setText(bean.getName());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishDialog();
                            if (selectorAction != null) {
                                selectorAction.call(bean);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public Animation loadStartAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        setDefaultConfig(translateAnimation, false);
        setDefaultConfig(alphaAnimation, false);

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
        setDefaultConfig(translateAnimation, true);
        setDefaultConfig(alphaAnimation, true);

        translateAnimation.setDuration(DEFAULT_DIALOG_FINISH_ANIM_TIME);
        alphaAnimation.setDuration(DEFAULT_DIALOG_FINISH_ANIM_TIME);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        return animationSet;
    }

    @Override
    public Animation loadLayoutAnimation() {
        Animation animation = super.loadLayoutAnimation();
        if (animation != null) {
            animation.setDuration(160);
        }
        return animation;
    }
}
