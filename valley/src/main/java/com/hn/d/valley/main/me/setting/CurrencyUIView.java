package com.hn.d.valley.main.me.setting;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.angcyo.library.glide.GlideCacheUtil;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.widget.HnLoading;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.orhanobut.hawk.Hawk;

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

    /**
     * 是否是听筒模式
     */
    public static final String KEY_IS_RECEIVER_MODE = "is_receiver_mode";

    private ItemInfoLayout mStorageSpaceItemInfoLayout;

    public static void setIsReceiverMode(boolean isChecked) {
        Hawk.put(KEY_IS_RECEIVER_MODE, isChecked);
    }

    public static boolean isReceiverMode() {
        return Hawk.get(KEY_IS_RECEIVER_MODE, false);
    }

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
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        if (mStorageSpaceItemInfoLayout != null) {
            //String cacheSize = GlideCacheUtil.getInstance().getCacheSize(mActivity);
            String cacheFolderSize = GlideCacheUtil.getInstance().getCacheFolderSize(mActivity);
            mStorageSpaceItemInfoLayout.setItemDarkText(cacheFolderSize);
        }
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        final int size = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
        //多语言
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                switchCompat.setVisibility(View.GONE);

                itemInfoLayout.setItemText(mActivity.getString(R.string.language));
                itemInfoLayout.setRightDrawableRes(R.drawable.base_next);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new LanguageUIView());
                    }
                });
            }
        }));
        //听筒模式
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                itemInfoLayout.setItemText(getString(R.string.receiver_mode));
                switchCompat.setChecked(isReceiverMode());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        setIsReceiverMode(isChecked);
                    }
                });
            }
        }));
        //存储空间
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                mStorageSpaceItemInfoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                switchCompat.setVisibility(View.GONE);

                mStorageSpaceItemInfoLayout.setItemText(getString(R.string.storage_space));
                mStorageSpaceItemInfoLayout.setRightDrawableRes(R.drawable.base_next);

                mStorageSpaceItemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showClearDialog();
                    }
                });
            }
        }));
        //清理聊天记录
        items.add(ViewItemInfo.build(new ItemOffsetCallback(3 * size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView textView = holder.v(R.id.text_view);
                textView.setText(R.string.clear_chat_record);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showClearChatRecordDialog();
                    }
                });
            }
        }));
    }

    /**
     * 清理聊天记录对话框
     */
    private void showClearChatRecordDialog() {
        UIDialog.build()
                .setDialogContent(mActivity.getString(R.string.text_is_empty))
                .setOkText(mActivity.getString(R.string.ok))
                .setCancelText(mActivity.getString(R.string.cancel))
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NIMClient.getService(MsgService.class).clearMsgDatabase(true);
                    }
                })
                .showDialog(mParentILayout);
    }

    protected void showClearDialog() {
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
                                HnLoading.show(mParentILayout, false);
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
                                    mStorageSpaceItemInfoLayout.setItemDarkText(cacheFolderSize);
                                    T_.ok(getString(R.string.clear_success));
                                } else {
                                    T_.ok(getString(R.string.clear_faild));
                                }
                            }
                        });
                    }
                })
                .showDialog(mParentILayout);
    }

}
