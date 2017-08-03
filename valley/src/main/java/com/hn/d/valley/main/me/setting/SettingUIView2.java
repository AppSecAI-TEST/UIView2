package com.hn.d.valley.main.me.setting;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.activity.HnSplashActivity;
import com.hn.d.valley.control.MainControl;
import com.hn.d.valley.start.LoginUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.netease.nimlib.sdk.StatusCode;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/02/15 11:22
 * 修改人员：Robi
 * 修改时间：2017/02/15 11:22
 * 修改备注：
 * Version: 1.0.0
 */
public class SettingUIView2 extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    @Override
    protected void onBindDataView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
        if (mRExBaseAdapter.isLast(posInData)) {
            TextView textView = holder.v(R.id.text_view);
            textView.setText(dataBean.itemString);
            textView.setOnClickListener(dataBean.itemClickListener);

            ResUtil.setBgDrawable(textView, LoginUIView.createLoginDrawable(mActivity));

            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    T_.show("当前版本:" + RUtils.getAppVersionName(mActivity));
                    return false;
                }
            });
        } else {
            ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
            infoLayout.setItemText(dataBean.itemString);
            infoLayout.setOnClickListener(dataBean.itemClickListener);
        }
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (mRExBaseAdapter.isLast(viewType)) {
            return R.layout.item_button_view;
        } else {
            return R.layout.item_info_layout;
        }
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
//        mRecyclerView.setBackgroundColor(Color.WHITE);
        mRecyclerView.addItemDecoration(new RExItemDecoration(new RExItemDecoration.ItemDecorationCallback() {
            @Override
            public Rect getItemOffsets(RecyclerView.LayoutManager layoutManager, int position, int edge) {
                Rect rect = new Rect(0, 0, 0, 0);
                if (position == 0 || position == 1 || position == 4) {
                    rect.top = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
                } else if (position == layoutManager.getItemCount() - 1) {
                    rect.top = 0;
                } else {
                    rect.top = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
                }
                return rect;
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                if (position == 0 || position == 1 || position == 4) {
//                    paint.setColor(getDefaultBackgroundColor());
//                    offsetRect.set(itemView.getLeft(), itemView.getTop() - offsetRect.top, itemView.getRight(), itemView.getTop());
//                    canvas.drawRect(offsetRect, paint);
                } else if (position == itemCount - 1) {

                } else {
                    paint.setColor(Color.WHITE);
                    offsetRect.set(itemView.getLeft(), itemView.getTop() - offsetRect.top,
                            itemView.getLeft() + mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi), itemView.getTop());
                    canvas.drawRect(offsetRect, paint);
//                    paint.setColor(mActivity.getResources().getColor(R.color.line_color));
//                    offsetRect.set(itemView.getLeft() + mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi),
//                            itemView.getTop() - offsetRect.top, itemView.getRight(), itemView.getTop());
//                    canvas.drawRect(offsetRect, paint);
                }
            }
        }));
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(new ViewItemInfo(mActivity.getString(R.string.account_safe), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new AccountSafeUIView());
            }
        }));

        items.add(new ViewItemInfo(mActivity.getString(R.string.message_notify), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new MessageNotifyUIView());
            }
        }));
        items.add(new ViewItemInfo(mActivity.getString(R.string.conceal), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new ConcealUIView());
            }
        }));
        items.add(new ViewItemInfo(mActivity.getString(R.string.currency), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new CurrencyUIView());
            }
        }));

        items.add(new ViewItemInfo(mActivity.getString(R.string.feedback), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startIView(new X5WebUIView("http://www.baidu.com"));
                startIView(new FeedBackUIView());
            }
        }));
//        items.add(new ViewItemInfo(mActivity.getString(R.string.faq), new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startIView(new X5WebUIView("http://www.baidu.com"));
//            }
//        }));
        items.add(new ViewItemInfo(mActivity.getString(R.string.about_me), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new AboutKlgUIView().setEnableClipMode(ClipMode.CLIP_BOTH, v));
            }
        }));
//        items.add(new ViewItemInfo(mActivity.getString(R.string.user_agreement), new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startIView(new X5WebUIView("http://www.baidu.com"));
//            }
//        }));

        items.add(new ViewItemInfo(mActivity.getString(R.string.quit_login), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIDialog.build()
                        .setDialogTitle(mActivity.getString(R.string.dialog_title_hint))
                        .setDialogContent(mActivity.getString(R.string.quit_login_hint))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainControl.onLoginOut();
                                HnSplashActivity.launcher(mActivity, false, StatusCode.LOGINED.getValue());
                                mActivity.finish();
                            }
                        })
                        .showDialog(mParentILayout);
            }
        }));
    }

    @Override
    protected String getTitleString() {
        return mActivity.getString(R.string.settting);
    }

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);
        refreshLayout();
    }
}
