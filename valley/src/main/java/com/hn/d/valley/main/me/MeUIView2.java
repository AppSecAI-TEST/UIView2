package com.hn.d.valley.main.me;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.setting.MyQrCodeUIView;
import com.hn.d.valley.main.me.setting.SettingUIView2;
import com.hn.d.valley.sub.MyStatusUIView;
import com.hn.d.valley.sub.other.FansRecyclerUIView;
import com.hn.d.valley.sub.other.FollowersRecyclerUIView;
import com.hn.d.valley.sub.other.FriendsRecommendUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.other.MyVisitorUserUIView;
import com.hn.d.valley.sub.other.SeeStateUserUIView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.x5.X5WebUIView;

import java.util.ArrayList;
import java.util.List;

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

    Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadUserData();
        }
    };
    private List<String> mPhotos = new ArrayList<>();

    static void resize(View view, int size, int margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.rightMargin = margin;
        layoutParams.width = size;
        layoutParams.height = size;
        view.setLayoutParams(layoutParams);
    }

    public static List<String> initPhotos(UserInfoBean userInfoBean) {
        List<String> list = new ArrayList<>();
        if (userInfoBean != null) {
            List<String> stringList = RUtils.split(userInfoBean.getPhotos());
            if (stringList.isEmpty()) {
                list.add(UserCache.instance().getAvatar());
            } else {
                list.addAll(stringList);
            }
        } else {
            list.add(UserCache.instance().getAvatar());
        }
        return list;
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
    public void onViewShow(Bundle bundle) {
        if (System.currentTimeMillis() - mLastShowTime > 30 * 1000) {
            //30秒后, 重新拉取新的信息
            loadUserData();
        } else {
            postDelayed(refreshRunnable, 1000);
        }
        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
        mPhotos = initPhotos(userInfoBean);
        super.onViewShow(bundle);
    }

    @Override
    public void onViewShow(long viewShowCount) {
        super.onViewShow(viewShowCount);
        if (viewShowCount >= 1) {
            mRExBaseAdapter.notifyItemChanged(6);
        }
    }

    private void loadUserData() {
        UserCache.instance()
                .fetchUserInfo()
                .subscribe(new RSubscriber<UserInfoBean>() {
                    @Override
                    public void onSucceed(UserInfoBean userInfoBean) {
                        if (userInfoBean != null) {
                            UserCache.instance().setUserInfoBean(userInfoBean);
                            refreshLayout();
                        }
                    }
                });
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();

        final int size = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
        int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
        final int drawPadding = size / 2;

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.v(R.id.user_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new UserDetailUIView2());
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
                        mOtherILayout.startIView(new MyStatusUIView());
                    }
                });

                HnGlideImageView userIcoView = holder.v(R.id.user_ico_view);
                userIcoView.setImageThumbUrl(userInfoBean.getAvatar());

                holder.tv(R.id.user_name_view).setText(userInfoBean.getUsername());
                holder.tv(R.id.user_id_view).setText(userInfoBean.getUid());

                holder.tv(R.id.fans_count).setText(userInfoBean.getFans_count() + "");
                holder.tv(R.id.attention_count).setText(userInfoBean.getAttention_count() + "");
                holder.tv(R.id.status_count).setText(userInfoBean.getDiscuss_count() + "");

                holder.tv(R.id.fans_count).setTextColor(SkinHelper.getSkin().getThemeSubColor());
                holder.tv(R.id.attention_count).setTextColor(SkinHelper.getSkin().getThemeSubColor());
                holder.tv(R.id.status_count).setTextColor(SkinHelper.getSkin().getThemeSubColor());

            }
        }));

        //我的相册
//        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
//                itemInfoLayout.setItemText(getString(R.string.my_photos));
//                itemInfoLayout.setItemDarkText(userInfoBean.getDiscuss_pic_count() + "");
//                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mOtherILayout.startIView(new MyAlbumUIView());
//                    }
//                });
//            }
//        }));


        //动态通知
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText(getString(R.string.dynamic_notification_text));

                ImageView imageView = itemInfoLayout.getImageView();
                int offset = getResources().getDimensionPixelOffset(R.dimen.base_hdpi);
                resize(imageView, offset, offset * 3);
                imageView.setBackgroundResource(R.drawable.base_red_circle_shape);

                itemInfoLayout.setLeftDrawableRes(R.drawable.icon_notice);
                itemInfoLayout.setLeftDrawPadding(drawPadding);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new SeeStateUserUIView());
                    }
                });
            }
        }));
        //我的访客
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText(getString(R.string.my_visitor_text));

                ImageView imageView = itemInfoLayout.getImageView();
                int offset = getResources().getDimensionPixelOffset(R.dimen.base_hdpi);
                resize(imageView, offset, offset * 3);
                imageView.setBackgroundResource(R.drawable.base_red_circle_shape);

                itemInfoLayout.setLeftDrawableRes(R.drawable.icon_vistor);
                itemInfoLayout.setLeftDrawPadding(drawPadding);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new MyVisitorUserUIView());
                    }
                });
            }
        }));

        //我的二维码/我的名片
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText(getString(R.string.my_code));
                ImageView imageView = itemInfoLayout.getImageView();
                imageView.setImageResource(R.drawable.qr_code);
                int offset = getResources().getDimensionPixelOffset(R.dimen.base_xxxhdpi);
                resize(imageView, offset, offset / 2);

                itemInfoLayout.setLeftDrawableRes(R.drawable.icon_mycard);
                itemInfoLayout.setLeftDrawPadding(drawPadding);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new MyQrCodeUIView());
                    }
                });
            }
        }));
        //我的收藏
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText(getString(R.string.my_collect_tip));
                itemInfoLayout.setItemDarkText(userInfoBean.getCollect_count() + "");

                itemInfoLayout.setLeftDrawableRes(R.drawable.icon_collection);
                itemInfoLayout.setLeftDrawPadding(drawPadding);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new MyCollectUIView());
                    }
                });
            }
        }));

        //会员中心
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText(getString(R.string.vip_tip));

                itemInfoLayout.setLeftDrawableRes(R.drawable.icon_vipcenter);
                itemInfoLayout.setLeftDrawPadding(drawPadding);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new X5WebUIView("http://www.baidu.com"));
                    }
                });
            }
        }));
        //我的钱包
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText(getString(R.string.my_wallet_tip));

                itemInfoLayout.setLeftDrawableRes(R.drawable.icon_wallet);
                itemInfoLayout.setLeftDrawPadding(drawPadding);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new X5WebUIView("http://www.baidu.com"));
                    }
                });
            }
        }));
        //个性装扮
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText(getString(R.string.personalized_dress));

                itemInfoLayout.setLeftDrawableRes(R.drawable.icon_skin);
                itemInfoLayout.setLeftDrawPadding(drawPadding);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new SkinManagerUIView());
                    }
                });
            }
        }));

//        //我的身份
//        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
//                itemInfoLayout.setItemText(getString(R.string.my_auth_title));
//                //是否已认证【0-未认证，1-已认证，2-认证中-查看自己信息才会有，3-认证失败-查看自己信息才会有，以前没有认证成功过才会有该值】
//                final int is_auth = Integer.parseInt(userInfoBean.getIs_auth());
//                //是否已认证【0-未认证，1-职场名人，2-娱乐明星，3-体育任务，4-政府人员】
//                int auth_type = Integer.parseInt(userInfoBean.getAuth_type());
//                String darkString;
//                switch (is_auth) {
//                    case 1:
//                        switch (auth_type) {
//                            case 1:
//                                darkString = getString(R.string.zhichangmingren);
//                                break;
//                            case 2:
//                                darkString = getString(R.string.yulemingxing);
//                                break;
//                            case 3:
//                                darkString = getString(R.string.tiyurenwu);
//                                break;
//                            case 4:
//                                darkString = getString(R.string.zhengfurenyuan);
//                                break;
//                            default:
//                                darkString = getString(R.string.weizhi);
//                                break;
//                        }
//                        break;
//                    case 2:
//                        darkString = getString(R.string.auth_ing);
//                        break;
//                    case 3:
//                        darkString = getString(R.string.auth_error);
//                        break;
//                    default:
//                        darkString = getString(R.string.not_auth);
//                        break;
//                }
//                itemInfoLayout.setItemDarkText(darkString);
//
//                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        switch (is_auth) {
//                            case 1:
//                            case 2:
//                            case 3:
//                                mOtherILayout.startIView(new MyAuthStatusUIView(is_auth));
//                                break;
//                            default:
//                                mOtherILayout.startIView(new MyAuthUIView());
//                                break;
//                        }
//                    }
//                });
//            }
//        }));

//        //等级
//        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
//                itemInfoLayout.setItemText(getString(R.string.level));
//                itemInfoLayout.setItemDarkTag("V%1$s");
//                itemInfoLayout.setItemDarkText(userInfoBean.getGrade());
//
//                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mOtherILayout.startIView(new MyGradeUIView());
//                    }
//                });
//            }
//        }));

        //邀请好友
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText(getString(R.string.invite_friends));

                TextView textView = new TextView(mActivity);
                textView.setText(R.string.prize_tip);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.default_text_little_size));
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundResource(R.drawable.base_orange_color_round_bg);
                int margin = getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
                textView.setPadding(margin / 2, margin / 4, margin / 2, margin / 4);
                itemInfoLayout.addRightView(textView, -2, -2, 2 * margin);

                itemInfoLayout.setLeftDrawableRes(R.drawable.icon_invitation);
                itemInfoLayout.setLeftDrawPadding(drawPadding);
            }
        }));

        //好友推荐
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText(getString(R.string.friends_recommend_tip));

                itemInfoLayout.setLeftDrawableRes(R.drawable.icon_intrduction);
                itemInfoLayout.setLeftDrawPadding(drawPadding);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new FriendsRecommendUIView(false));
                    }
                });
            }
        }));

    }

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);
        mRExBaseAdapter.notifyItemChanged(0);
    }
}
