package com.hn.d.valley.main.me.setting;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import com.angcyo.library.glide.GlideCacheUtil;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.widget.HnLoading;

import java.util.List;

import rx.Subscriber;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：通用设置界面
 * 创建人员：Robi
 * 创建时间：2017/02/17 14:47
 * 修改人员：Robi
 * 修改时间：2017/02/17 14:47
 * 修改备注：
 * Version: 1.0.0
 */
public class CurrencyUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    @Override
    protected int getTitleResource() {
        return R.string.currency;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (mRExBaseAdapter.isLast(viewType)) {
            return R.layout.item_button_view;
        }
        return R.layout.item_switch_view;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        final int size = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                switchCompat.setVisibility(View.GONE);

                itemInfoLayout.setItemText(mActivity.getString(R.string.language));
                itemInfoLayout.setRightDrawableRes(R.drawable.ic_right);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new LanguageUIView());
                    }
                });
            }
        }));
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                itemInfoLayout.setItemText("听筒模式");
            }
        }));
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                final ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                switchCompat.setVisibility(View.GONE);

                itemInfoLayout.setItemText("存储空间");
                itemInfoLayout.setRightDrawableRes(R.drawable.ic_right);
                //String cacheSize = GlideCacheUtil.getInstance().getCacheSize(mActivity);
                String cacheFolderSize = GlideCacheUtil.getInstance().getCacheFolderSize(mActivity);
                itemInfoLayout.setItemDarkText(cacheFolderSize);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIDialog.build()
                                .setDialogTitle(mActivity.getString(R.string.dialog_title_hint))
                                .setDialogContent(mActivity.getString(R.string.dialog_clear_cache_tip))
                                .setOkListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        GlideCacheUtil.getInstance().clearCacheFolder(mActivity).subscribe(new Subscriber<Boolean>() {

                                            @Override
                                            public void onStart() {
                                                super.onStart();
                                                HnLoading.show(mOtherILayout, false);
                                            }

                                            @Override
                                            public void onCompleted() {
                                                HnLoading.hide();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                T_.error(e.getMessage());
                                            }

                                            @Override
                                            public void onNext(Boolean aBoolean) {
                                                if (aBoolean) {
                                                    String cacheFolderSize = GlideCacheUtil.getInstance().getCacheFolderSize(mActivity);
                                                    itemInfoLayout.setItemDarkText(cacheFolderSize);
                                                    T_.ok("清理成功.");
                                                } else {
                                                    T_.ok("清理失败.");
                                                }
                                            }
                                        });
                                    }
                                })
                                .showDialog(mOtherILayout);
                    }
                });
            }
        }));
        items.add(ViewItemInfo.build(new ItemOffsetCallback(3 * size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView textView = holder.v(R.id.text_view);
                textView.setText("清空聊天记录");
            }
        }));
    }
}
