package com.hn.d.valley.main.me;

import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.rsen.PlaceholderView;
import com.angcyo.uiview.skin.BaseSkin;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.skin.BlackSkin;
import com.hn.d.valley.skin.BlueSkin;
import com.hn.d.valley.skin.GreenSkin;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/01 14:02
 * 修改人员：Robi
 * 修改时间：2017/04/01 14:02
 * 修改备注：
 * Version: 1.0.0
 */
public class SkinManagerUIView extends BaseRecyclerUIView<String, ISkin, String> {

    public final static int SKIN_BLACK = 1;
    public final static int SKIN_GREEN = 2;
    public final static int SKIN_BLUE = 3;
    public static String SKIN_KEY = "skin_key";

    @Override
    protected TitleBarPattern getTitleBar() {
        return createTitleBarPattern().setShowBackImageView(true);
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }

    @Override
    protected boolean hasScrollListener() {
        return false;
    }

    @Override
    protected int getTitleResource() {
        return R.string.personalized_dress;
    }

    @Override
    protected RExBaseAdapter<String, ISkin, String> initRExBaseAdapter() {
        return new RExBaseAdapter<String, ISkin, String>(mActivity) {

            @Override
            protected int getDataItemType(int posInData) {
                if (posInData == 0) {
                    return TYPE_DATA - 1;
                }
                return super.getDataItemType(posInData);
            }

            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TYPE_DATA - 1) {
                    return R.layout.item_single_main_text_view;
                }
                return R.layout.item_skin_layout;
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, final int posInData, ISkin dataBean) {
                if (posInData == 0) {
                    RTextView textView = holder.v(R.id.text_view);
                    textView.setText(R.string.theme_skin);
                    textView.setPadding(getDimensionPixelOffset(R.dimen.base_hdpi), 0, 0, 0);
                    textView.setLeftColor(SkinHelper.getSkin().getThemeColor(), getDimensionPixelOffset(R.dimen.base_mdpi));
                } else {
                    holder.tv(R.id.text_view).setText(dataBean.skinName());
                    holder.v(R.id.skin_view).setBackgroundColor(dataBean.getThemeColor());

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setSelectorPosition(posInData);
                        }
                    });
                }
            }

            @Override
            protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, ISkin bean) {
                if (position != 0) {
                    holder.v(R.id.check_view).setVisibility(isSelector ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            protected boolean onSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
                SkinHelper.changeSkin(mAllDatas.get(position), mILayout);
                Hawk.put(SKIN_KEY, position);
                notifyItemChanged(0);
                return super.onSelectorPosition(viewHolder, position, isSelector);
            }
        };
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        mRefreshLayout.setTopView(new PlaceholderView(mActivity));
        mRefreshLayout.setNotifyListener(false);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRExBaseAdapter.resetAllData(getSkins());
        mRExBaseAdapter.setModel(RModelAdapter.MODEL_SINGLE);
        mRExBaseAdapter.setSelectorPosition(Hawk.get(SkinManagerUIView.SKIN_KEY, SkinManagerUIView.SKIN_BLACK));
    }

    private List<ISkin> getSkins() {
        List<ISkin> skinBeanList = new ArrayList<>();
        skinBeanList.add(new BaseSkin(mActivity));
        skinBeanList.add(new BlackSkin(mActivity));
        skinBeanList.add(new GreenSkin(mActivity));
        skinBeanList.add(new BlueSkin(mActivity));
        return skinBeanList;
    }
}
