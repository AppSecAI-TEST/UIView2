package com.hn.d.valley.sub.user;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.RFlowLayout;
import com.angcyo.uiview.widget.RTextCheckView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.control.TagsControl;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Action2;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：标签选择界面
 * 创建人员：Robi
 * 创建时间：2017/01/09 16:41
 * 修改人员：Robi
 * 修改时间：2017/01/09 16:41
 * 修改备注：
 * Version: 1.0.0
 */
public class TagsUIView extends BaseContentUIView {

    private Action1<List<Tag>> mListAction;

    /**
     * 已经选择标签
     */
    private List<Tag> selectorTag;

    private RFlowLayout mFlowLayout;

    /**
     * 强制选中视频tag
     */
    private boolean isVideo = false;

    public TagsUIView(Action1<List<Tag>> listAction, List<Tag> selectorTag) {
        this(listAction, selectorTag, false);
    }

    public TagsUIView(Action1<List<Tag>> listAction, List<Tag> selectorTag, boolean isVideo) {
        mListAction = listAction;
        this.selectorTag = selectorTag;
        this.isVideo = isVideo;
    }

    public TagsUIView setIsVideo(boolean video) {
        isVideo = video;
        return this;
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        mFlowLayout = new RFlowLayout(mActivity);
        int padding = (int) ResUtil.dpToPx(mActivity, 10);
        mFlowLayout.setPadding(padding, padding, padding, padding);
        baseContentLayout.addView(mFlowLayout, new ViewGroup.LayoutParams(-1, -1));
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setShowBackImageView(true)
                .setTitleString(mActivity.getString(R.string.add_tags_text))
                .addRightItem(TitleBarPattern.TitleBarItem.build(mActivity.getString(R.string.ok),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<Tag> tags = getTags();
                                if (tags.isEmpty()) {
                                    T_.show(mActivity.getString(R.string.tag_empty_tip));
                                    return;
                                }
                                if (tags.size() > getResources().getInteger(R.integer.max_tags_count)) {
                                    T_.show(mActivity.getString(R.string.tag_max_tip));
                                    return;
                                }
                                mListAction.call(tags);
                                finishIView();
                            }
                        }));
    }

    /**
     * 返回选中的tags
     */
    protected List<Tag> getTags() {
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < mFlowLayout.getChildCount(); i++) {
            RTextCheckView view = (RTextCheckView) mFlowLayout.getChildAt(i);
            if (view.isChecked()) {
                tags.add((Tag) view.getTag());
            }
        }
        return tags;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        TagsControl.inflate(mActivity, mFlowLayout, false, new Action2<RTextCheckView, Tag>() {
            @Override
            public void call(RTextCheckView rTextCheckView, Tag tag) {
                rTextCheckView.setChecked(selectorTag.contains(tag));
                UI.setBackgroundDrawable(rTextCheckView, ResUtil.selector(
                        ResUtil.createDrawable(getColor(R.color.line_color), Color.TRANSPARENT,
                                getDimensionPixelOffset(R.dimen.base_line), getDimensionPixelOffset(R.dimen.little_round_radius)),
                        ResUtil.createDrawable(SkinHelper.getSkin().getThemeSubColor(), Color.TRANSPARENT,
                                getDimensionPixelOffset(R.dimen.base_line), getDimensionPixelOffset(R.dimen.little_round_radius)),
                        ResUtil.createDrawable(SkinHelper.getSkin().getThemeSubColor(), Color.TRANSPARENT,
                                getDimensionPixelOffset(R.dimen.base_line), getDimensionPixelOffset(R.dimen.little_round_radius)),
                        ResUtil.createDrawable(getColor(R.color.base_color_disable), Color.TRANSPARENT,
                                getDimensionPixelOffset(R.dimen.base_line), getDimensionPixelOffset(R.dimen.little_round_radius))
                ));

                if (tag.getName().equalsIgnoreCase(getString(R.string.video))) {
                    rTextCheckView.setEnabled(false);
                    if (isVideo) {
                        rTextCheckView.setTextColor(SkinHelper.getSkin().getThemeSubColor());
                        rTextCheckView.setChecked(true);
                        UI.setBackgroundDrawable(rTextCheckView, ResUtil.createDrawable(SkinHelper.getSkin().getThemeSubColor(), Color.TRANSPARENT,
                                getDimensionPixelOffset(R.dimen.base_line), getDimensionPixelOffset(R.dimen.little_round_radius)));
                    }
                }
            }
        });
    }
}
