package com.hn.d.valley.main.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：认证类型选择界面
 * 创建人员：Robi
 * 创建时间：2017/02/17 16:49
 * 修改人员：Robi
 * 修改时间：2017/02/17 16:49
 * 修改备注：
 * Version: 1.0.0
 */
public class AuthTypeSelectorUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {
    /**
     * 认证类别【1-职场名人，2-娱乐明星，3-体育人物，4-政府人员】
     */

    private MyAuthUIView.AuthType mAuthType;

    private Action1<MyAuthUIView.AuthType> selectorTypeAction;

    public AuthTypeSelectorUIView(MyAuthUIView.AuthType authType,
                                  Action1<MyAuthUIView.AuthType> selectorTypeAction) {
        mAuthType = authType;
        this.selectorTypeAction = selectorTypeAction;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_radio_view;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity, R.string.auth_type_tip);
    }

    @NonNull
    @Override
    protected RExBaseAdapter<String, ViewItemInfo, String> createRExBaseAdapter() {
        RExBaseAdapter<String, ViewItemInfo, String> baseAdapter = new RExBaseAdapter<String, ViewItemInfo, String>(mActivity, mItemsList) {

            @Override
            protected int getDataItemType(int posInData) {
                return AuthTypeSelectorUIView.this.getDataItemType(posInData);
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                AuthTypeSelectorUIView.this.onBindDataView(holder, posInData, dataBean);
            }

            @Override
            protected int getItemLayoutId(int viewType) {
                return AuthTypeSelectorUIView.this.getItemLayoutId(viewType);
            }

            @Override
            protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, ViewItemInfo bean) {
                super.onBindModelView(model, isSelector, holder, position, bean);
                CompoundButton compoundButton = holder.v(R.id.radio_view);
                compoundButton.setChecked(isSelector);
            }
        };
        baseAdapter.setModel(RModelAdapter.MODEL_SINGLE);
        if (mAuthType != null) {
            baseAdapter.setSelectorPosition(Integer.parseInt(mAuthType.getId()) - 1);
        }
        baseAdapter.addOnModelChangeListener(new RModelAdapter.OnModelChangeListener() {
            @Override
            public void onModelChange(@RModelAdapter.Model int fromModel, @RModelAdapter.Model int toModel) {

            }

            @Override
            public void onSelectorChange(List<Integer> selectorList) {
                //T_.show("选中:" + selectorList.get(0));
                finishIView();
                selectorTypeAction.call(MyAuthUIView.AuthType.from(selectorList.get(0) + 1));
            }
        });
        return baseAdapter;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        int size = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
        int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);

        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, final int posInData, ViewItemInfo dataBean) {
                initItem(holder, MyAuthUIView.AuthType.ZCMR, posInData);
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(size, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, final int posInData, ViewItemInfo dataBean) {
                initItem(holder, MyAuthUIView.AuthType.YLMX, posInData);
            }

        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(size, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, final int posInData, ViewItemInfo dataBean) {
                initItem(holder, MyAuthUIView.AuthType.TYRW, posInData);
            }

        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(size, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, final int posInData, ViewItemInfo dataBean) {
                initItem(holder, MyAuthUIView.AuthType.ZFRY, posInData);
            }

        }));
    }

    private void initItem(RBaseViewHolder holder, MyAuthUIView.AuthType authType, final int posInData) {
        ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
        infoLayout.setItemText(authType.getDes());

        holder.v(R.id.radio_view).setEnabled(false);
        holder.v(R.id.radio_view).setClickable(false);
        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRExBaseAdapter.setSelectorPosition(posInData);
            }
        });
    }
}
