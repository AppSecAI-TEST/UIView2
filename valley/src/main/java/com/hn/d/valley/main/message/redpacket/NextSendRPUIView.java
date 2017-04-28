package com.hn.d.valley.main.message.redpacket;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：红包发放结果
 * 创建人员：hewking
 * 创建时间：2017/04/24 19:48
 * 修改人员：hewking
 * 修改时间：2017/04/24 19:48
 * 修改备注：
 * Version: 1.0.0
 */
public class NextSendRPUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo>{

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleBarBGColor(ContextCompat.getColor(mActivity,R.color.base_red_d85940))
                .setTitleString("恐龙谷红包");
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_redpacket_result;
    }

    @Override
    protected void inflateRecyclerRootLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        baseContentLayout.setBackgroundResource(R.color.base_red_d85940);
        super.inflateRecyclerRootLayout(baseContentLayout,inflater);
    }



    @Override
    protected void createItems(List<ViewItemInfo> items) {

        items.add(ViewItemInfo.build(new ItemConfig() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {

            }

            @Override
            public void setItemOffsets(Rect rect) {

            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {

            }
        }));

    }
}
