package com.hn.d.valley.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RMaxAdapter;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.IcoInfoBean;
import com.hn.d.valley.library.fresco.DraweeViewUtil;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/14 15:34
 * 修改人员：Robi
 * 修改时间：2017/01/14 15:34
 * 修改备注：
 * Version: 1.0.0
 */
public class HnIcoRecyclerView extends RRecyclerView {

    OnItemClickListener mOnItemClickListener;
    private RMaxAdapter<IcoInfoBean> mMaxAdapter;

    public HnIcoRecyclerView(Context context) {
        this(context, null);
    }

    public HnIcoRecyclerView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setItemAnim(false);
        setTag("H");
        mMaxAdapter = new RMaxAdapter<IcoInfoBean>(context) {

            @Override
            protected int getItemLayoutId(int viewType) {
                return 0;
            }

            @Override
            protected View createContentView(ViewGroup parent, int viewType) {
                RelativeLayout layout = new RelativeLayout(context);

                SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context);
                GenericDraweeHierarchy hierarchy = simpleDraweeView.getHierarchy();
                if (!TextUtils.equals("not_circle", getContentDescription())) {
                    hierarchy.setRoundingParams(RoundingParams.asCircle());
                }
                hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
                hierarchy.setPlaceholderImage(R.drawable.defauit_avatar_contact, ScalingUtils.ScaleType.CENTER_CROP);
                simpleDraweeView.setTag("image");

                int size = getSize();
                int padding = (int) (getResources().getDisplayMetrics().density * 4);
                layout.setPadding(padding, padding, padding, padding);
                layout.addView(simpleDraweeView, new ViewGroup.LayoutParams(size, size));
                return layout;
            }

            @Override
            protected void onBindView(final RBaseViewHolder holder, final int position, final IcoInfoBean bean) {
                int size = getSize();
                DraweeViewUtil.resize((SimpleDraweeView) holder.tag("image"), bean.avatar, size, size);

                holder.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(holder, position, bean);
                        }
                    }
                });
            }

            private int getSize() {
                return (int) (getResources().getDisplayMetrics().density * 40);
            }


        };

        mMaxAdapter.setMaxCount(5);
    }

    public void remove(String avatar) {
        //迭代器删除多个数据会出现异常 因为数据源发生变化，删除一个没有问题
        List<IcoInfoBean> icoInfos = getMaxAdapter().getAllDatas();
        for (IcoInfoBean icon : icoInfos) {
            if (icon.avatar.equals(avatar)) {
                icoInfos.remove(icon);
                break;
            }
        }
        mMaxAdapter.notifyDataSetChanged();
    }

    public void setMaxCount(int maxCount) {
        mMaxAdapter.setMaxCount(maxCount);
    }

    public RMaxAdapter<IcoInfoBean> getMaxAdapter() {
        return mMaxAdapter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setAdapter(mMaxAdapter);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(final RBaseViewHolder holder, int position, final IcoInfoBean bean);
    }

}
