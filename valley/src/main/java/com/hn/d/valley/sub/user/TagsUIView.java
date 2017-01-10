package com.hn.d.valley.sub.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.widget.RFlowLayout;
import com.angcyo.uiview.widget.RTextCheckView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.control.TagsControl;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Action2;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
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

    public TagsUIView(Action1<List<Tag>> listAction, List<Tag> selectorTag) {
        mListAction = listAction;
        this.selectorTag = selectorTag;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
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
    protected void initContentLayout() {
        super.initContentLayout();
        TagsControl.inflate(mActivity, mFlowLayout, new Action2<RTextCheckView, Tag>() {
            @Override
            public void call(RTextCheckView rTextCheckView, Tag tag) {
                rTextCheckView.setChecked(selectorTag.contains(tag));
            }
        });
    }
}
