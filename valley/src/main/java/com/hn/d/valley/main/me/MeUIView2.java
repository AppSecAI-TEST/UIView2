package com.hn.d.valley.main.me;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.github.utilcode.utils.AppUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.DraftControl;
import com.hn.d.valley.control.PublishTaskRealm;
import com.hn.d.valley.main.me.setting.MyQrCodeUIView;
import com.hn.d.valley.main.me.setting.SettingUIView2;
import com.hn.d.valley.main.me.sub.InviteFriendsUIDialog;
import com.hn.d.valley.main.message.uinfo.DynamicFuncManager2;
import com.hn.d.valley.main.wallet.MyWalletUIView;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.NewsService;
import com.hn.d.valley.sub.MyStatusUIView;
import com.hn.d.valley.sub.other.FansRecyclerUIView;
import com.hn.d.valley.sub.other.FollowersRecyclerUIView;
import com.hn.d.valley.sub.other.FriendsRecommendUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.other.KLGCoinUIVIew;
import com.hn.d.valley.sub.other.MyVisitorUserUIView2;
import com.hn.d.valley.sub.user.NewNotifyUIView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.x5.VipWebUIView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

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
    TestImageView mTestImageView;
    private List<String> mPhotos = new ArrayList<>();
    private int mDrawPadding;
    /**
     * 收藏的资讯数量
     */
    private int mCollectCount = 0;

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
                //list.add(UserCache.instance().getAvatar());
            } else {
                list.addAll(stringList);
            }
        } else {
            //list.add(UserCache.instance().getAvatar());
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
                                mParentILayout.startIView(new SettingUIView2());
                            }
                        }));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_user_top_layout;
        }
        if (BuildConfig.SHOW_DEBUG && mRExBaseAdapter.isLast(viewType)) {
            return R.layout.item_version_layout;
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
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        mRExBaseAdapter.notifyItemChanged(5);
    }

    @Override
    public void onViewShow(long viewShowCount) {
        super.onViewShow(viewShowCount);
        loadCollectCount();
        if (viewShowCount >= 1) {
            mRExBaseAdapter.notifyItemRangeChanged(1, 5);
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

    private void loadCollectCount() {
        add(RRetrofit.create(NewsService.class)
                .collectcount(Param.buildInfoMap("uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(String.class))
                .subscribe(new RSubscriber<String>() {
                    @Override
                    public void onSucceed(final String bean) {
                        super.onSucceed(bean);
                        mCollectCount = Integer.parseInt(bean);
                    }
                }));
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();

        final int size = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
        int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
        mDrawPadding = size;

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.v(R.id.user_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new UserDetailUIView2());
                    }
                });
                //粉丝
                holder.v(R.id.follower_item_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new FansRecyclerUIView());
                    }
                });
                //关注
                holder.v(R.id.follow_item_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new FollowersRecyclerUIView());
                    }
                });
                //动态
                holder.v(R.id.status_item_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new MyStatusUIView());
                    }
                });
                // 魅力
                holder.v(R.id.charm_item_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                if (userInfoBean != null) {
                    HnGlideImageView userIcoView = holder.v(R.id.user_ico_view);
                    userIcoView.setImageThumbUrl(userInfoBean.getAvatar());

                    holder.tv(R.id.user_name_view).setText(userInfoBean.getUsername());
                    holder.tv(R.id.user_id_view).setText(userInfoBean.getUid());

                    holder.tv(R.id.fans_count).setText(RUtils.getShortString(userInfoBean.getFans_count()));
                    holder.tv(R.id.attention_count).setText(RUtils.getShortString(userInfoBean.getAttention_count()));
                    holder.tv(R.id.status_count).setText(RUtils.getShortString(userInfoBean.getDiscuss_count()));
                    holder.tv(R.id.charm_count).setText(userInfoBean.getCharm());
                }

                holder.tv(R.id.fans_count).setTextColor(SkinHelper.getSkin().getThemeSubColor());
                holder.tv(R.id.attention_count).setTextColor(SkinHelper.getSkin().getThemeSubColor());
                holder.tv(R.id.status_count).setTextColor(SkinHelper.getSkin().getThemeSubColor());
                holder.tv(R.id.charm_count).setTextColor(SkinHelper.getSkin().getThemeSubColor());

//                mTestImageView = holder.v(R.id.test_image_view);

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
//                        mParentILayout.startIView(new MyAlbumUIView());
//                    }
//                });
//            }
//        }));


        //动态通知
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);

                ImageView imageView = itemInfoLayout.getImageView();
                int offset = getResources().getDimensionPixelOffset(R.dimen.base_hdpi);
                resize(imageView, offset, offset * 3);

                if (userInfoBean != null && userInfoBean.isNew_notification()) {
                    imageView.setBackgroundResource(R.drawable.base_red_circle_shape);
                } else {
                    imageView.setBackground(null);
                }

                initItemLayout(itemInfoLayout, R.string.dynamic_notification_text, R.drawable.icon_notice, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mParentILayout.startIView(new SeeStateUserUIView());
                        mParentILayout.startIView(new NewNotifyUIView());

                        RRealm.exe(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (userInfoBean != null) {
                                    userInfoBean.setNew_notification(false);
                                }
                            }
                        });
                    }
                });
            }
        }));
        //我的访客
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);

                ImageView imageView = itemInfoLayout.getImageView();
                int offset = getResources().getDimensionPixelOffset(R.dimen.base_hdpi);
                resize(imageView, offset, offset * 3);
                if (userInfoBean != null && userInfoBean.isNew_visitor()) {
                    imageView.setBackgroundResource(R.drawable.base_red_circle_shape);
                } else {
                    imageView.setBackground(null);
                }

                initItemLayout(itemInfoLayout, R.string.my_visitor_text, R.drawable.icon_vistor, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new MyVisitorUserUIView2());

                        RRealm.exe(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (userInfoBean != null) {
                                    userInfoBean.setNew_visitor(false);
                                }
                            }
                        });
                    }
                });
            }
        }));

        //我的二维码/我的名片
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                ImageView imageView = itemInfoLayout.getImageView();
                imageView.setImageResource(R.drawable.qr_code);
                int offset = getResources().getDimensionPixelOffset(R.dimen.base_xxxhdpi);
                resize(imageView, offset, offset / 2);

                initItemLayout(itemInfoLayout, R.string.my_code, R.drawable.icon_mycard, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new MyQrCodeUIView());
                    }
                });
            }
        }));
        //我的收藏
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                if (userInfoBean != null) {
                    itemInfoLayout.setItemDarkText((userInfoBean.getCollect_count() + mCollectCount) + "");
                }

                initItemLayout(itemInfoLayout, R.string.my_collect_tip, R.drawable.icon_collection, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new MyCollectUIView());
                    }
                });
            }
        }));
        //我的草稿
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                final ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemDarkText("0");

                initItemLayout(itemInfoLayout, R.string.my_draft_tip, R.drawable.icon_draft, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new DraftManagerUIView());
                    }
                });

                DraftControl.getDraft(new DraftControl.OnDraftListener() {
                    @Override
                    protected void onDraft(RealmResults<PublishTaskRealm> taskRealms) {
                        itemInfoLayout.setItemDarkText(taskRealms.size() + "");
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
                itemInfoLayout.setLeftDrawPadding(mDrawPadding);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new VipWebUIView());
                    }
                });
            }
        }));


        if (DynamicFuncManager2.instance().dynamicFuncResult.isShowWallet() || BuildConfig.SHOW_DEBUG) {
            //我的钱包
            items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                    initItemLayout(itemInfoLayout, R.string.my_wallet_tip, R.drawable.icon_wallet, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mParentILayout.startIView(new MyWalletUIView());
                        }
                    });
                }
            }));
        }

        //龙币
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                initItemLayout(itemInfoLayout, R.string.klg_coin, R.drawable.icon_purchase, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new KLGCoinUIVIew());
                    }
                });
                itemInfoLayout.setItemDarkText(UserCache.instance().getLoginBean().getCoins());
                itemInfoLayout.getDarkTextView().setTextColor(getColor(R.color.yellow_ffac2d));
                itemInfoLayout.setDarkDrawableRes(R.drawable.longbi);
            }
        }));

        //个性装扮
        items.add(ViewItemInfo.build(new ItemOffsetCallback(line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                initItemLayout(itemInfoLayout, R.string.personalized_dress, R.drawable.icon_skin, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new SkinManagerUIView());
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
//                                mParentILayout.startIView(new MyAuthStatusUIView(is_auth));
//                                break;
//                            default:
//                                mParentILayout.startIView(new MyAuthUIView());
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
//                        mParentILayout.startIView(new MyGradeUIView());
//                    }
//                });
//            }
//        }));

        //邀请好友
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);

                TextView textView = new TextView(mActivity);
                textView.setText(R.string.prize_tip);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.default_text_little_size));
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundResource(R.drawable.base_orange_color_round_bg);
                int margin = getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
                textView.setPadding(margin / 2, margin / 4, margin / 2, margin / 4);
                //itemInfoLayout.addRightView(textView, -2, -2, 2 * margin);

                initItemLayout(itemInfoLayout, R.string.invite_friends, R.drawable.icon_invitation, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new InviteFriendsUIDialog());
                    }
                });
            }
        }));

        //好友推荐
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {

            @Override
            public void setItemOffsets(Rect rect) {
                super.setItemOffsets(rect);
                if (!BuildConfig.SHOW_DEBUG) {
                    rect.bottom = size / 2;
                }
            }

            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                initItemLayout(itemInfoLayout, R.string.friends_recommend_tip, R.drawable.icon_intrduction, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new FriendsRecommendUIView(false));
                    }
                });
            }
        }));

        if (BuildConfig.SHOW_DEBUG) {
            //版本 编译时间
            items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {

                @Override
                public void setItemOffsets(Rect rect) {
                    super.setItemOffsets(rect);
                    rect.bottom = size / 2;
                }

                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    holder.itemView.setPadding(0, 0, 0, 0);
                    holder.tv(R.id.text_view).setText(AppUtils.getAppVersionName(mActivity) +
                            " by " + getString(R.string.build_time) +
                            " on " + getString(R.string.os_name));

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //MainControl.checkVersion(mParentILayout);
                            //AdsControl.INSTANCE.updateAds();

                            //{"phone":"18770090887,18770080909","name":"张三，李四"}

//                            RRetrofit.create(ContactService.class)
//                                    .phoneUser2(new PhonesBody("{\"phone\":\"18770090887,18770080909\",\"name\":\"张三，李四\"}"))
//                                    .compose(Rx.transformer(String.class))
//                                    .subscribe(new BaseSingleSubscriber<String>() {
//                                        @Override
//                                        public void onSucceed(String bean) {
//                                            super.onSucceed(bean);
//                                        }
//                                    });

//                            RRetrofit.create(ContactService.class)
//                                    .phoneUser3(Param.buildMap("uid:" + UserCache.getUserAccount(), "phones:{\"phone\":\"18770090887,18770080909\",\"name\":\"张三，李四\"}"
//                                            , "phone_model:" + Build.MODEL, "device_id:" + Build.DEVICE))
//                                    .compose(Rx.transformer(String.class))
//                                    .subscribe(new BaseSingleSubscriber<String>() {
//                                        @Override
//                                        public void onSucceed(String bean) {
//                                            super.onSucceed(bean);
//                                        }
//                                    });

                            //int i = 1 / 0;

//                            Uri webPage = Uri.parse("klg://klg.com/?type=user&id=62194");
//                            Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);
//                            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            mActivity.startActivity(webIntent);

//                            Glide.with(mActivity)
//                                    .load("http://circleimg.klgwl.com/62215/1500875757920_s_460x1200.jpeg")
//                                    .asBitmap()
//                                    .into(new SimpleTarget<Bitmap>() {
//                                        @Override
//                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                            final TransitionDrawable td = new TransitionDrawable(new Drawable[]{
//                                                    ContextCompat.getDrawable(mActivity, R.drawable.base_image_placeholder_shape),
//                                                    new BitmapDrawable(getResources(), resource)});
//                                            mTestImageView.setImageDrawable(td);
//                                            td.startTransition(300);
//                                        }
//                                    });

                            Glide.with(mActivity)
                                    .load("http://circleimg.klgwl.com/62410/1500601458974_s_248x209.gif")
                                    .asGif()
                                    .into(new SimpleTarget<GifDrawable>() {
                                        @Override
                                        public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
                                            final TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                                                    ContextCompat.getDrawable(mActivity, R.drawable.base_image_placeholder_shape),
                                                    resource});
                                            mTestImageView.setImageDrawable(td);
                                            td.startTransition(300);
                                            resource.start();
                                        }
                                    });
                        }
                    });

                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //int i = 1 / 0;
                            Glide.with(mActivity)
                                    .load("http://circleimg.klgwl.com/62215/1500875757920_s_460x1200.jpeg")
                                    .into(new SimpleTarget<GlideDrawable>() {
                                        @Override
                                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {

                                        }
                                    });
                            return false;
                        }
                    });
                }
            }));
        }
    }

    protected void initItemLayout(ItemInfoLayout itemInfoLayout, int titleId, int leftRes, View.OnClickListener listener) {
        itemInfoLayout.setItemText(getString(titleId));

        itemInfoLayout.setLeftDrawableRes(leftRes);
        itemInfoLayout.setLeftDrawPadding(mDrawPadding);

        itemInfoLayout.setOnClickListener(listener);
    }

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);
        mRExBaseAdapter.notifyItemChanged(0);
    }
}
