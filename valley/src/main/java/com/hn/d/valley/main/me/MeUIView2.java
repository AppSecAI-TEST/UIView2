package com.hn.d.valley.main.me;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.setting.EditInfoUIView;
import com.hn.d.valley.main.me.setting.MyQrCodeUIView;
import com.hn.d.valley.main.me.setting.SettingUIView2;
import com.hn.d.valley.sub.other.FansRecyclerUIView;
import com.hn.d.valley.sub.other.FollowersRecyclerUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.other.MyVisitorUserUIView;
import com.hn.d.valley.sub.other.SeeStateUserUIView;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action0;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/03/09 18:02
 * 修改人员：Robi
 * 修改时间：2017/03/09 18:02
 * 修改备注：
 * Version: 1.0.0
 */
public class MeUIView2 extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    static void resize(View view, int size, int margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.rightMargin = margin;
        layoutParams.width = size;
        layoutParams.height = size;
        view.setLayoutParams(layoutParams);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setShowBackImageView(false)
                .setTitleString(getString(R.string.me))
                .addRightItem(TitleBarPattern.TitleBarItem.build(R.drawable.shezhi_n,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOtherILayout.startIView(new SettingUIView2());
                            }
                        }));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_user_top_layout;
        }
        return R.layout.item_info_layout;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();

        int size = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
        int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.v(R.id.user_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new EditInfoUIView(new ArrayList<String>(), new Action0() {
                            @Override
                            public void call() {
                                //initMeUIView();
                            }
                        }));
                    }
                });
                //粉丝
                holder.v(R.id.follower_item_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new FansRecyclerUIView());
                    }
                });
                //关注
                holder.v(R.id.follow_item_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new FollowersRecyclerUIView());
                    }
                });
                //动态
                holder.v(R.id.status_item_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                HnGlideImageView userIcoView = holder.v(R.id.user_ico_view);
                userIcoView.setImageThumbUrl(userInfoBean.getAvatar());

                holder.tv(R.id.user_name_view).setText(userInfoBean.getUsername());
                holder.tv(R.id.user_id_view).setText(userInfoBean.getUid());

                holder.tv(R.id.fans_count).setText(userInfoBean.getFans_count() + "");
                holder.tv(R.id.attention_count).setText(userInfoBean.getAttention_count() + "");
                holder.tv(R.id.status_count).setText(userInfoBean.getDiscuss_count() + "");


            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText("我的相册");
                itemInfoLayout.setItemDarkText(userInfoBean.getDiscuss_pic_count() + "");
            }
        }));
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText("我的收藏");
                itemInfoLayout.setItemDarkText(userInfoBean.getCollect_count() + "");

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new MyCollectUIView());
                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText("动态通知");

                ImageView imageView = itemInfoLayout.getImageView();
                int offset = getResources().getDimensionPixelOffset(R.dimen.base_hdpi);
                resize(imageView, offset, offset * 3);
                imageView.setBackgroundResource(R.drawable.base_red_circle_shape);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new SeeStateUserUIView());
                    }
                });
            }
        }));
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText("我的访客");

                ImageView imageView = itemInfoLayout.getImageView();
                int offset = getResources().getDimensionPixelOffset(R.dimen.base_hdpi);
                resize(imageView, offset, offset * 3);
                imageView.setBackgroundResource(R.drawable.base_red_circle_shape);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new MyVisitorUserUIView());
                    }
                });
            }
        }));


        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText("等级");
                itemInfoLayout.setItemDarkTag("V%1$s");
                itemInfoLayout.setItemDarkText(userInfoBean.getGrade());

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new MyGradeUIView());
                    }
                });
            }
        }));
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText("我的身份");
                itemInfoLayout.setItemDarkText("未认证");
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText("邀请好友");

                TextView textView = new TextView(mActivity);
                textView.setText("有奖");
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.default_text_size));
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundResource(R.drawable.base_orange_color_round_bg);
                int margin = getResources().getDimensionPixelOffset(R.dimen.base_xxhdpi);
                itemInfoLayout.addRightView(textView, -2, -2, margin);
            }
        }));
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText("我的二维码");
                ImageView imageView = itemInfoLayout.getImageView();
                imageView.setImageResource(R.drawable.qr_code);
                int offset = getResources().getDimensionPixelOffset(R.dimen.base_xxxhdpi);
                resize(imageView, offset, offset / 2);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new MyQrCodeUIView());
                    }
                });
            }
        }));
    }
}
