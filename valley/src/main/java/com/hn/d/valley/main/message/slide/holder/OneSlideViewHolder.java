package com.hn.d.valley.main.message.slide.holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.widget.HnGlideImageView;
/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/12 14:22
 * 修改人员：hewking
 * 修改时间：2017/05/12 14:22
 * 修改备注：
 * Version: 1.0.0
 */
public class OneSlideViewHolder extends SlideViewHolder {

    private HnGlideImageView iv_head;
    private TextView tv_friend_name;
    private RelativeLayout rlContent;
    private CheckBox cb_select;

    public OneSlideViewHolder(View itemView, int viewType) {
        super(itemView, viewType);

        iv_head = (HnGlideImageView) itemView.findViewById(R.id.iv_item_head);
        tv_friend_name = (TextView) itemView.findViewById(R.id.tv_friend_name);
        rlContent = (RelativeLayout) itemView.findViewById(R.id.rl_content);
        cb_select = (CheckBox) itemView.findViewById(R.id.cb_friend_addfirend);

    }

    @Override
    public void doAnimationSet(int offset, float fraction) {
        rlContent.scrollTo(offset, 0);

        cb_select.setScaleX(fraction);
        cb_select.setScaleY(fraction);
        cb_select.setAlpha(fraction * 255);
    }

    @Override
    public void slideClose() {
        super.slideClose();
        cb_select.setChecked(false);
    }

    @Override
    public void slideOpen() {
        super.slideOpen();
    }

    @Override
    public void onBindSlideClose(int state) {

    }

    @Override
    public void doAnimationSetOpen(int state) {

    }

    public void bind() {
        setOffset(30);

        //slide must call
        onBindSlide(rlContent);
    }
}
