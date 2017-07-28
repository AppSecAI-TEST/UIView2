package com.hn.d.valley.main.found;

import android.Manifest;
import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.base.Item;
import com.angcyo.uiview.base.SingleItem;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.ScreenUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseItemUIView;
import com.hn.d.valley.main.MainUIView;
import com.hn.d.valley.main.found.sub.GameListUIView;
import com.hn.d.valley.main.found.sub.HnScanUIView;
import com.hn.d.valley.main.found.sub.HotInformationUIView;
import com.hn.d.valley.main.found.sub.SearchUIView;
import com.hn.d.valley.main.home.nearby.NearbyUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：发现
 * 创建人员：Robi
 * 创建时间：2016/12/16 13:58
 * 修改人员：Robi
 * 修改时间：2016/12/16 13:58
 * 修改备注：
 * Version: 1.0.0
 */
public class FoundUIView2 extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    public static final String GAME_URL = "http://222.189.228.182:88/webplat/";

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_find;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.nav_found_text)).setShowBackImageView(false);
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);

    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        final int size = ScreenUtil.dip2px(10);

        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindInitView(holder, getString(R.string.hot_information), "全球资讯轻松浏览热点资讯", R.drawable.img_news, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new HotInformationUIView());
                    }
                });
            }
        }));


        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindInitView(holder, getString(R.string.search_title), "全网检索,即刻呈现", R.drawable.img_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainUIView uiView = (MainUIView) mParentILayout.getIViewWith(MainUIView.class);
                        mParentILayout.startIView(new SearchUIView().setJumpToDynamicListAction(uiView));
                    }
                });
            }
        }));


        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindInitView(holder, getString(R.string.scan_title), "快速读取条码内容", R.drawable.img_scan, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.checkPermissions(new String[]{
                                        Manifest.permission.CAMERA
                                },
                                new Action1<Boolean>() {
                                    @Override
                                    public void call(Boolean aBoolean) {
                                        if (aBoolean) {
                                            mParentILayout.startIView(new HnScanUIView());
                                        }
                                    }
                                });
                    }
                });
            }
        }));


        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindInitView(holder, getString(R.string.game), "约上小伙伴一起边聊边玩", R.drawable.img_game, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new GameListUIView());
                    }
                });
            }
        }));
    }

    private void bindInitView(RBaseViewHolder holder, String title , String desc , @DrawableRes int res, View.OnClickListener listener) {
        TextView tv_friend_name = holder.tv(R.id.tv_friend_name);
        TextView tv_func_desc = holder.tv(R.id.tv_func_desc);
        ImageView iv_find = holder.imgV(R.id.iv_find);
        tv_friend_name.setText(title);
        tv_func_desc.setText(desc);
        iv_find.setImageResource(res);
        holder.itemView.setOnClickListener(listener);

    }


}
