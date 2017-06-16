package com.hn.d.valley.main.me.sub;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import com.angcyo.uiview.base.Item;
import com.angcyo.uiview.base.SingleItem;
import com.angcyo.uiview.github.utilcode.utils.ClipboardUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.RImageView;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseItemUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.IcoInfoBean;
import com.hn.d.valley.bean.realm.RelationDataBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.main.me.MeUIView2;
import com.hn.d.valley.main.me.setting.EditInfoUIView;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.sub.adapter.ImageAdapter;
import com.hn.d.valley.sub.other.RelationListUIView;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnIcoRecyclerView;
import com.hn.d.valley.x5.X5WebUIView;

import java.util.List;

import rx.functions.Action0;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：个人详情页2, 资料子页面
 * 创建人员：Robi
 * 创建时间：2017/04/10 16:26
 * 修改人员：Robi
 * 修改时间：2017/04/10 16:26
 * 修改备注：
 * Version: 1.0.0
 */
public class UserInfoSubUIView extends BaseItemUIView {

    RBaseItemDecoration mDecor;
    UserInfoBean mUserInfoBean;
    Action0 mAction0;

    public UserInfoSubUIView(UserInfoBean userInfoBean, Action0 action0) {
        mUserInfoBean = userInfoBean;
        mAction0 = action0;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    public int getDefaultBackgroundColor() {
        return getColor(R.color.chat_bg_color);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_search_layout_user_recommend;
        } else if (viewType == 1) {
            return R.layout.item_user_info_sub_view;
        }
        return R.layout.item_user_introduce_view;
    }

    @Override
    protected void createItems(final List<SingleItem> items) {
        //照片墙
        items.add(new SingleItem(SingleItem.Type.TOP) {
            @Override
            public void onBindView(RBaseViewHolder holder, final int posInData, Item dataBean) {
                RTextView tv = holder.v(R.id.tip_view);
                tv.setDefaultSKin(R.string.photo_wall);

                RRecyclerView recyclerView = holder.reV(R.id.recycler_view);
                if (mDecor == null) {
                    mDecor = new RBaseItemDecoration((int) (density() * 10), Color.TRANSPARENT);
                } else {
                    recyclerView.removeItemDecoration(mDecor);
                }
                recyclerView.addItemDecoration(mDecor);
                final List<String> stringList = RUtils.split(mUserInfoBean.getPhotos());
                stringList.add("");

                recyclerView.setAdapter(new ImageAdapter(mActivity, new ImageAdapter.ImageAdapterConfig() {
                    @Override
                    public List<String> getDatas() {
                        return stringList;
                    }

                    @Override
                    public void onItemClick(RBaseViewHolder holder, RImageView view, int position, String bean) {
                        ImagePagerUIView.start(mParentILayout,
                                view,
                                PhotoPager.getImageItems(stringList, view.copyDrawable(), position),
                                position);
                    }

                    @Override
                    public boolean onBindView(final RBaseViewHolder holder, final int position, final String bean) {
                        if (RUtils.isLast(stringList, position)) {
                            if (mUserInfoBean.isMe()) {
                                holder.imgV(R.id.image_view).setImageResource(R.drawable.shangchuanzhaopian_zhaopianqiang);
                                holder.imgV(R.id.image_view).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mParentILayout.startIView(new EditInfoUIView(MeUIView2.initPhotos(mUserInfoBean), mAction0));
                                    }
                                });
                            } else {
                                holder.imgV(R.id.image_view).setImageResource(R.drawable.yaoqingshangchuan_zhaopianqiang);
                                holder.imgV(R.id.image_view).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        add(RRetrofit.create(UserService.class)
                                                .inviteUploadPhotos(Param.buildMap("to_uid:" + mUserInfoBean.getUid()))
                                                .compose(Rx.transformer(String.class))
                                                .subscribe(new BaseSingleSubscriber<String>() {
                                                    @Override
                                                    public void onSucceed(String bean) {
                                                        super.onSucceed(bean);
                                                        T_.show(bean);
                                                    }
                                                })
                                        );
                                    }
                                });
                            }
                            return true;
                        }
                        return false;
                    }
                }));
            }
        });

        //基本资料
        items.add(new SingleItem(SingleItem.Type.TOP) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                RTextView tv = holder.v(R.id.tip_view);
                tv.setDefaultSKin(R.string.base_info);
                //tv.setBackgroundColor(Color.RED);

                holder.sub(R.id.id_item).setItemDarkText(mUserInfoBean.getUid());
                holder.sub(R.id.id_item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardUtils.copyText(mUserInfoBean.getUid());
                        T_.show(getString(R.string.text_id_had_copy_to_clipboard));
                    }
                });

                if (TextUtils.isEmpty(mUserInfoBean.getSignature())) {
                    holder.sub(R.id.signature_item).setItemDarkText(getString(R.string.signature_empty_tip));
                } else {
                    holder.sub(R.id.signature_item).setItemDarkText(mUserInfoBean.getSignature());
                }

                holder.sub(R.id.birthday_item).setItemDarkText(mUserInfoBean.getBirthday(getResources()));

                if (TextUtils.isEmpty(mUserInfoBean.getAddress())) {
                    holder.sub(R.id.address_item).setItemDarkText(getString(R.string.address_empty_tip));
                } else {
                    holder.sub(R.id.address_item).setItemDarkText(mUserInfoBean.getAddress());
                }

                holder.v(R.id.more_info_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new MoreInfoUIView(mUserInfoBean));
                    }
                });

                //共同关注的好友
                View relationLayout = holder.v(R.id.relation_layout);
                View relationLine = holder.v(R.id.relation_line);

                RelationDataBean relation = mUserInfoBean.getRelation();
                if (relation != null && relation.getCount() > 0) {
                    relationLayout.setVisibility(View.VISIBLE);
                    relationLine.setVisibility(View.VISIBLE);

                    holder.tv(R.id.relation_count_view).setText(relation.getCount() + "");

                    //点赞列表
                    List<IcoInfoBean> icoInfoList = relation.getList();

                    HnIcoRecyclerView icoRecyclerView = holder.v(R.id.ico_recycler_view);
                    icoRecyclerView.getMaxAdapter().resetData(icoInfoList);

                    relationLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mParentILayout.startIView(new RelationListUIView(mUserInfoBean.getUid()));
                        }
                    });

                    icoRecyclerView.setOnItemClickListener(new HnIcoRecyclerView.OnItemClickListener() {
                        @Override
                        public void onItemClick(RBaseViewHolder holder, int position, IcoInfoBean bean) {
                            mParentILayout.startIView(new RelationListUIView(mUserInfoBean.getUid()));
                        }
                    });
                }
            }
        });

        if (mUserInfoBean.isAuth()) {
            //人物介绍
            items.add(new SingleItem(SingleItem.Type.TOP) {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                    RTextView tv = holder.v(R.id.tip_view);
                    tv.setDefaultSKin(R.string.base_introduce);

                    String introduce = mUserInfoBean.getIntroduce();
                    if (TextUtils.isEmpty(introduce)) {
                        holder.tv(R.id.text_view).setText(R.string.introduce_empty_tip);
                    } else {
                        holder.tv(R.id.text_view).setText(introduce);
                    }

                    final String website = mUserInfoBean.getWebsite();
                    if (TextUtils.isEmpty(website)) {
                        holder.v(R.id.more_info_view).setVisibility(View.GONE);
                        holder.v(R.id.bottom_line_view).setVisibility(View.GONE);
                    } else {
                        holder.v(R.id.more_info_view).setVisibility(View.VISIBLE);
                        holder.v(R.id.bottom_line_view).setVisibility(View.VISIBLE);
                    }

                    holder.v(R.id.more_info_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mParentILayout.startIView(new X5WebUIView(website));
                        }
                    });
                }
            });
        }
    }
}
