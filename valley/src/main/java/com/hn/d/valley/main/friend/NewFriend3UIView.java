package com.hn.d.valley.main.friend;

import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.found.sub.HnScanUIView;
import com.hn.d.valley.main.me.setting.MyQrCodeUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;

import static com.hn.d.valley.control.ShareControl.shareDynamic;
import static com.hn.d.valley.control.ShareControl.shareQrcode;

/**
 * Created by hewking on 2017/3/22.
 */
public class NewFriend3UIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    public NewFriend3UIView() {

    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity, R.string.add_friend);
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        final int line = mActivity.getResources().getDimensionPixelSize(R.dimen.default_text_size);
        final int left = mActivity.getResources().getDimensionPixelSize(R.dimen.base_70dpi);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView searchview = holder.tv(R.id.search_view);
                RelativeLayout layout_search_view = holder.v(R.id.layout_search_view);
                layout_search_view.setBackgroundResource(R.drawable.base_dark_round_selector_white);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new SearchUserUIView());
                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView tv_uid = holder.tv(R.id.tv_qrcode_desc);
                tv_uid.setText(String.format("我的id : %s", UserCache.getUserAccount()));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new MyQrCodeUIView());
                    }
                });
            }
        }));


        items.add(ViewItemInfo.build(new ItemLineCallback(0, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new AddressBookUI2View());
                    }
                });

                TextView tv_username = holder.tv(R.id.tv_friend_name);
                ImageView imgv = holder.imgV(R.id.iv_item_head);
                TextView tv_desc = holder.tv(R.id.tv_func_desc);
                bindItemView(holder,"扫描二维码图片",getString(R.string.text_context),R.drawable.tongxunlu_haoyou);

            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left, 1) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new HnScanUIView());
                    }
                });
                bindItemView(holder,getString(R.string.text_add_invite_contact_friend)
                        ,getString(R.string.text_saoyisao),R.drawable.shaoyishao);
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left, 1) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareQrcode(mActivity,SHARE_MEDIA.WEIXIN);
                    }
                });
                bindItemView(holder,"邀请微信好友","微信好友",R.drawable.weixin_tianjiahaoyou);
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left, 1) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareQrcode(mActivity,SHARE_MEDIA.QQ);
                    }
                });
                bindItemView(holder,"邀请QQ好友","QQ好友",R.drawable.qq_tianjiahaoyou);
            }
        }));


    }

    private void bindItemView(RBaseViewHolder holder, String title, String desc , @DrawableRes int res) {
        TextView tv_username = holder.tv(R.id.tv_friend_name);
        ImageView imgv = holder.imgV(R.id.iv_item_head);
        TextView tv_desc = holder.tv(R.id.tv_func_desc);
        tv_desc.setText(title);
        tv_username.setText(desc);
        imgv.setImageResource(res);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        switch (viewType) {
            case 0:
                return R.layout.item_recent_search;
            case 1:
                return R.layout.item_contact_qrcode;
            case 2:
            case 3:
                return R.layout.item_contact_func;
        }
        return R.layout.item_contact_func;
    }

    @Override
    protected void initOnShowContentLayout() {
        getRecyclerView().setBackgroundResource(R.color.default_base_bg_dark2);
        super.initOnShowContentLayout();
    }




}
