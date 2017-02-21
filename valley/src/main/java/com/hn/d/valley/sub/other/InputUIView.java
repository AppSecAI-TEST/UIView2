package com.hn.d.valley.sub.other;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.view.IView;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/02/20 19:00
 * 修改人员：Robi
 * 修改时间：2017/02/20 19:00
 * 修改备注：
 * Version: 1.0.0
 */
public class InputUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    InputConfigCallback mConfigCallback;

    private InputUIView(@NonNull InputConfigCallback configCallback) {
        mConfigCallback = configCallback;
        mConfigCallback.mIView = this;
    }

    public static InputUIView build(@NonNull InputConfigCallback configCallback) {
        return new InputUIView(configCallback);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return mConfigCallback.initTitleBar(super.getTitleBar());
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_single_input_view;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ExEditText editText = holder.v(R.id.edit_text_view);
                mConfigCallback.initInputView(holder, editText, dataBean);
            }

            @Override
            public void setItemOffsets(Rect rect) {
                mConfigCallback.setItemOffsets(rect);
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                mConfigCallback.draw(canvas, paint, itemView, offsetRect, itemCount, position);
            }
        }));
    }

    public static abstract class InputConfigCallback {

        protected RBaseViewHolder mViewHolder;
        protected ExEditText mExEditText;
        protected IView mIView;

        /**
         * 初始化标题bar
         */
        public TitleBarPattern initTitleBar(final TitleBarPattern titleBarPattern) {
            return titleBarPattern;
        }

        /**
         * 初始化输入控件
         */
        @CallSuper
        public void initInputView(RBaseViewHolder holder, ExEditText editText, ViewItemInfo bean) {
            mViewHolder = holder;
            mExEditText = editText;
            mExEditText.requestFocus();
        }

        protected void setInputText(String text) {
            mExEditText.setText(text);
            mExEditText.setSelection(TextUtils.isEmpty(text) ? 0 : text.length());
            //mExEditText.checkEdit(true);
        }

        /**
         * 设置分割线空隙
         */
        public void setItemOffsets(Rect rect) {
            int offset = ValleyApp.getApp().getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
            rect.top = offset;
        }

        /**
         * 绘制分割线
         */
        public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {

        }
    }
}
