package com.hn.d.valley.main.other;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.angcyo.uiview.base.Item;
import com.angcyo.uiview.base.SingleItem;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.SkinHelper;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseItemUIView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.x5.X5WebUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：恐龙君 个人详情页
 * 创建人员：Robi
 * 创建时间：2017/04/10 09:25
 * 修改人员：Robi
 * 修改时间：2017/04/10 09:25
 * 修改备注：
 * Version: 1.0.0
 */
public class KLJUIView extends BaseItemUIView {

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setFloating(true)
                .setTitleHide(true)
                .setTitleBarBGColor(Color.TRANSPARENT);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_klj_layout;
    }

    @Override
    protected void createItems(List<SingleItem> items) {
        items.add(new SingleItem() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.v(R.id.text_view).setBackground(SkinHelper.getSkin().getThemeMaskBackgroundRoundSelector());
                HnGlideImageView iv = holder.v(R.id.iv_klj_logo);

                iv.setImageUrl("http://avatorimg.klgwl.com/13/13915.png");

                holder.v(R.id.text_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new X5WebUIView("http://www.baidu.com"));
                    }
                });
            }
        });
    }
}
