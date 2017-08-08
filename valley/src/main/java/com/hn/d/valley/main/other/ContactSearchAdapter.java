package com.hn.d.valley.main.other;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;

import com.amap.api.services.core.PoiItem;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.library.fresco.DraweeViewUtil;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/12 14:32
 * 修改人员：Robi
 * 修改时间：2017/01/12 14:32
 * 修改备注：
 * Version: 1.0.0
 */
public class ContactSearchAdapter extends RModelAdapter<FriendBean> {

    OnContactSelecteListener mSelectListener;

    public ContactSearchAdapter(Context context) {
        this(context, null);
    }

    public ContactSearchAdapter(Context context, OnContactSelecteListener contactSelecteListener) {
        super(context);
        mSelectListener = contactSelecteListener;
        setModel(MODEL_SINGLE);
    }


    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_firends_addgroup_item;
    }

    @Override
    protected void onBindCommonView(RBaseViewHolder holder, int position, FriendBean bean) {
        holder.tv(R.id.tv_friend_name).setText(bean.getTrueName());
        SimpleDraweeView imageView = holder.v(R.id.iv_item_head);
        DraweeViewUtil.setDraweeViewHttp(imageView,bean.getAvatar());
    }

    @Override
    protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, final int position, FriendBean bean) {
        holder.itemView.setBackgroundResource(R.color.default_base_bg_dark2);
        final CheckBox checkBox = holder.v(R.id.cb_friend_addfirend);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectorPosition(position,checkBox);
            }
        };
        holder.itemView.setOnClickListener(listener);
        checkBox.setOnClickListener(listener);

        if (isSelector) {
            onSelectorPosition(holder, position, isSelector);
        } else {
            onUnSelectorPosition(holder, position, isSelector);
        }
    }

    @Override
    protected boolean onSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
        super.onSelectorPosition(viewHolder, position, isSelector);

        if (mSelectListener != null) {
            mSelectListener.onContactSelected(getAllDatas().get(position));
        }

        return true;
    }

    @Override
    protected boolean onUnSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
        super.onUnSelectorPosition(viewHolder, position, isSelector);

        return true;
    }

    @Override
    protected void onBindNormalView(RBaseViewHolder holder, int position, FriendBean bean) {

    }




    public interface OnContactSelecteListener {
        void onContactSelected(FriendBean item);

    }
}
