package com.hn.d.valley.main.me.setting;

import android.view.View;
import android.widget.RadioButton;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/02/17 16:49
 * 修改人员：Robi
 * 修改时间：2017/02/17 16:49
 * 修改备注：
 * Version: 1.0.0
 */
public class LanguageUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private RadioButton mCheckBoxCn;
    private RadioButton mCheckBoxEn;
    private int old;

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_radio_view;
    }

    @Override
    public void onViewShow(long viewShowCount) {
        super.onViewShow(viewShowCount);
        old = Param.getLang();
        change(old);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().addRightItem(TitleBarPattern.TitleBarItem
                .build(mActivity.getResources().getString(R.string.save),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int lang;
                                if (mCheckBoxEn.isChecked()) {
                                    lang = 3;
                                } else {
                                    lang = 1;
                                }

                                if (old == lang) {
                                    finishIView();
                                } else {
                                    Param.changeLang(lang);
                                    mActivity.recreate();
                                }
                            }
                        }).setVisibility(View.GONE));
    }

    @Override
    protected int getTitleResource() {
        return R.string.language;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        int size = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
        int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);

        final View.OnClickListener listenerCn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change(1);
            }
        };
        final View.OnClickListener listenerEn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change(3);
            }
        };

        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                mCheckBoxCn = holder.v(R.id.radio_view);
                infoLayout.setItemText(mActivity.getString(R.string.zh));

                infoLayout.setOnClickListener(listenerCn);
                mCheckBoxCn.setOnClickListener(listenerCn);
            }
        }));

//        items.add(ViewItemInfo.build(new ItemLineCallback(size, line) {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
//                mCheckBoxEn = holder.v(R.id.radio_view);
//                infoLayout.setItemText(mActivity.getString(R.string.en));
//
//                infoLayout.setOnClickListener(listenerEn);
//                mCheckBoxEn.setOnClickListener(listenerEn);
//            }
//
//        }));
    }

    private void change(int lang) {
        if (mCheckBoxCn != null) {
            mCheckBoxCn.setChecked(lang != 3);
        }
        if (mCheckBoxEn != null) {
            mCheckBoxEn.setChecked(lang == 3);
        }
        getUITitleBarContainer().getRightControlLayout().getChildAt(0).setVisibility(old != lang ? View.VISIBLE : View.INVISIBLE);
    }
}
