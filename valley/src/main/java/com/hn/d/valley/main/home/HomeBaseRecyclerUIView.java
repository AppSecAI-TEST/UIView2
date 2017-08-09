package com.hn.d.valley.main.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import com.angcyo.uiview.recycler.RExItemDecoration;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.UserDiscussListBean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/22 15:36
 * 修改人员：Robi
 * 修改时间：2017/05/22 15:36
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class HomeBaseRecyclerUIView extends NoTitleBaseRecyclerUIView<UserDiscussListBean.DataListBean> {

    @Override
    protected RecyclerView.ItemDecoration initItemDecoration() {
        return new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
            @Override
            public void getItemOffsets2(Rect outRect, int position, int edge) {
                if (mRExBaseAdapter.getItemCount() < 10) {
                    outRect.bottom = getDimensionPixelOffset(R.dimen.base_hdpi);
                } else {
                    if (position == 0 || (mRExBaseAdapter.isEnableLoadMore() &&
                            position + 1 == mRExBaseAdapter.getItemCount())) {
                        //第一个, 和倒数第一个都不需要分割线
                    } else {
                        outRect.top = getDimensionPixelOffset(R.dimen.base_hdpi);
                    }
                }
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                drawTopLine(canvas, paint, itemView, new Rect(offsetRect), itemCount, position);
                drawBottomLine(canvas, paint, itemView, new Rect(offsetRect), itemCount, position);
            }

            @Override
            protected int getPaintColor(Context context) {
                return getColor(R.color.base_main_color_bg_color);
            }
        });
    }
}
