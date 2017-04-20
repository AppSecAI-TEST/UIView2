package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.main.found.sub.ScanUIView;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.NewFriend3UIView;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter;
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.message.groupchat.TeamCreateHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;
import rx.functions.Action3;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/07 16:34
 * 修改人员：hewking
 * 修改时间：2017/04/07 16:34
 * 修改备注：
 * Version: 1.0.0
 */
public class MenuPopUpWindow extends PopupWindow implements View.OnClickListener {


    private final View masker;

    private RRecyclerView recyclerView;

    private MenuPopAdapter mAdapter;

    private LayoutInflater mLayoutInflater;

    private ILayout mLayout;


    public MenuPopUpWindow(Context context, ILayout layout) {
        super(context);

        this.mLayout = layout;
        mLayoutInflater = LayoutInflater.from(context);

        final View view = View.inflate(context, R.layout.menu_friend_popwindow, null);
        masker = view.findViewById(R.id.masker);
        masker.setOnClickListener(this);
        recyclerView = (RRecyclerView) view.findViewById(R.id.rv_menu);
        recyclerView.addItemDecoration(new RBaseItemDecoration());
        mAdapter = new MenuPopAdapter();

        List<MenuItem> items = createItems();

        mAdapter.reset(items);

        recyclerView.setAdapter(mAdapter);

        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);  //如果不设置，就是 AnchorView 的宽度
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        setAnimationStyle(0);

    }

    private List<MenuItem> createItems() {

        List<MenuItem> items = new ArrayList<>();

        items.add(new MenuItem("添加群聊",R.drawable.tianjialianxiren, new Action1<String>() {
            @Override
            public void call(String s) {
                ContactSelectUIVIew targetView = new ContactSelectUIVIew(new BaseContactSelectAdapter.Options());
                targetView.setSelectAction(new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                    @Override
                    public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, RequestCallback requestCallback) {
                        TeamCreateHelper.createAndSavePhoto(uiBaseDataView, absContactItems, requestCallback);
                    }
                });
                mLayout.startIView(targetView);
                dismiss();
            }
        }));

        items.add(new MenuItem("添加好友",R.drawable.tianjiahaoyou, new Action1<String>() {
            @Override
            public void call(String s) {
                mLayout.startIView(new NewFriend3UIView());
                dismiss();
            }
        }));

        items.add(new MenuItem("扫一扫",R.drawable.saoyisao, new Action1<String>() {
            @Override
            public void call(String s) {
                mLayout.startIView(new ScanUIView());
                dismiss();
            }
        }));

        return items;

    }
    private class MenuPopAdapter extends RecyclerView.Adapter<MenuVH> {

        private List<MenuItem> mData;

        MenuPopAdapter() {
            mData = new ArrayList<>();
        }

        void reset(List<MenuItem> datas) {
            mData.clear();
            mData.addAll(datas);
            notifyDataSetChanged();
        }

        @Override
        public MenuVH onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = mLayoutInflater.inflate(R.layout.item_search_section,parent,false);

            return new MenuVH(view);
        }

        @Override
        public void onBindViewHolder(MenuVH holder, int position) {

            holder.bind(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }


    }

    public static class MenuVH extends RecyclerView.ViewHolder{

        @BindView(R.id.tip_view)
        RTextView tipView;
        @BindView(R.id.iv_item_head)
        ImageView ivHead;

        public MenuVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void bind(final MenuItem item) {
            tipView.setText(item.item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.action.call(item.item);
                }
            });
            ivHead.setVisibility(View.VISIBLE);
            ivHead.setImageResource(item.drawableRes);
        }


    }

    public static class MenuItem {

        String item;

        Action1<String> action ;

        @DrawableRes
        int drawableRes;

        public MenuItem(String item,int drawableRes ,Action1<String> action) {
            this.item = item;
            this.action = action;
            this.drawableRes = drawableRes;
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }



    @Override
    public void onClick(View v) {
        dismiss();
    }

}
