package com.hn.d.valley.main.found;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.utils.UIItem;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.main.found.sub.HotInformationUIView;
import com.hn.d.valley.main.found.sub.InviteFriendsUIView;
import com.hn.d.valley.main.found.sub.ScanUIView;
import com.hn.d.valley.main.found.sub.SearchUIView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：发现
 * 创建人员：Robi
 * 创建时间：2016/12/16 13:58
 * 修改人员：Robi
 * 修改时间：2016/12/16 13:58
 * 修改备注：
 * Version: 1.0.0
 */
public class FoundUIView extends BaseUIView {
    @BindView(R.id.found_root_layout)
    LinearLayout mFoundRootLayout;
    @BindView(R.id.item_root_layout)
    LinearLayout mItemRootLayout;

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main_found_layout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.nav_found_text));
    }

    @Override
    public Animation loadLayoutAnimation() {
        return null;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        ArrayList<UIItem.ItemInfo> items = new ArrayList<>();
        UIItem.ItemInfo info = new UIItem.ItemInfo(R.drawable.hot_news, R.drawable.next, R.drawable.default_bg_selector,
                mActivity.getResources().getColor(R.color.main_text_color),
                (int) mActivity.getResources().getDimension(R.dimen.default_text_size),
                (int) ResUtil.dpToPx(mActivity.getResources(), 60), mActivity.getString(R.string.hot_information),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new HotInformationUIView());
                    }
                });
        items.add(info);
        items.add(info.copy().setLeftIcoResId(R.drawable.search).setItemText(mActivity.getString(R.string.search_title)).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOtherILayout.startIView(new SearchUIView());
            }
        }));
        items.add(info.copy().setLeftIcoResId(R.drawable.scan).setItemText(mActivity.getString(R.string.scan_title)).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOtherILayout.startIView(new ScanUIView());
            }
        }));
        items.add(info.copy().setLeftIcoResId(R.drawable.invite_friends).setItemText(mActivity.getString(R.string.invite_friends)).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOtherILayout.startIView(new InviteFriendsUIView());
            }
        }));
        UIItem.fill(mItemRootLayout, items);
    }

}
