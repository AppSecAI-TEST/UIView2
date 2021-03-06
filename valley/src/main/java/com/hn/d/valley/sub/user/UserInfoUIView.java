package com.hn.d.valley.sub.user;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.SearchUserBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnGenderView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：用户信息界面
 * 创建人员：Robi
 * 创建时间：2017/01/05 17:43
 * 修改人员：Robi
 * 修改时间：2017/01/05 17:43
 * 修改备注：
 * Version: 1.0.0
 */
@Deprecated
public class UserInfoUIView extends BaseRecyclerUIView<SearchUserBean, UserDiscussListBean.DataListBean, String> {

    SearchUserBean mSearchUserBean;
    private ImageView mCommandImageView;

    public UserInfoUIView(SearchUserBean searchUserBean) {
        mSearchUserBean = searchUserBean;
    }

    @Override
    protected RExBaseAdapter<SearchUserBean, UserDiscussListBean.DataListBean, String> initRExBaseAdapter() {
        return new UserInfoAdapter(mActivity);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(mSearchUserBean.getUsername())
                .addRightItem(TitleBarPattern.TitleBarItem.build()
                        .setRes(R.drawable.more)
                        .setListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }));
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        mRExBaseAdapter.setHeaderData(mSearchUserBean);
        mRecyclerView.setBackgroundResource(R.color.line_color);
    }

    @Override
    protected void inflateOverlayLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        mCommandImageView = new ImageView(mActivity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMargins(0, 0,
                mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xxhdpi),
                mActivity.getResources().getDimensionPixelOffset(R.dimen.base_60dpi));
        mCommandImageView.setVisibility(View.GONE);
        baseContentLayout.addView(mCommandImageView, params);
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        initCommandView();
    }

    private void initCommandView() {
        final String to_uid = mSearchUserBean.getUid();
        final String uid = UserCache.getUserAccount();
        if (TextUtils.equals(uid, to_uid)) {
            mCommandImageView.setVisibility(View.GONE);
        } else {
            mCommandImageView.setVisibility(View.VISIBLE);
            if (mSearchUserBean.getIs_attention() == 1) {
                //已关注
                if (mSearchUserBean.getIs_contact() == 1) {
                    //是联系人
                    mCommandImageView.setImageResource(R.drawable.send_message_selector);
                    mCommandImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SessionHelper.startP2PSession(getILayout(), to_uid, SessionTypeEnum.P2P);
                        }
                    });
                } else {
                    //不是联系人
                    mCommandImageView.setImageResource(R.drawable.add_contact2_selector);
                    mCommandImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            add(RRetrofit.create(UserService.class)
                                    .addContact(Param.buildMap("uid:" + uid, "to_uid:" + to_uid,
                                            "tip:" + mActivity.getResources().getString(R.string.add_contact_tip,
                                                    UserCache.instance().getUserInfoBean().getUsername())))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {

                                        @Override
                                        public void onSucceed(String bean) {
                                            T_.show(bean);
                                        }
                                    }));
                        }
                    });
                }
            } else {
                //未关注
                mCommandImageView.setImageResource(R.drawable.attention_selector);
                mCommandImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add(RRetrofit.create(UserService.class)
                                .attention(Param.buildMap("uid:" + uid, "to_uid:" + to_uid))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onSucceed(String bean) {
                                        T_.show(getString(R.string.handle_success));
                                        mSearchUserBean.setIs_attention(1);
                                        initCommandView();
                                        loadData();
                                    }
                                }));
                    }
                });
            }
        }
    }

    @Override
    protected boolean isDataEmpty(List<UserDiscussListBean.DataListBean> datas) {
        return datas == null || datas.isEmpty();
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        Map<String, String> map = getPageMap();
        map.put("uid", UserCache.getUserAccount());
        map.put("to_uid", mSearchUserBean.getUid());

        add(RRetrofit.create(UserService.class)
                .discussList(Param.map(map))
                .compose(Rx.transformer(UserDiscussListBean.class))
                .subscribe(new BaseSingleSubscriber<UserDiscussListBean>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onSucceed(UserDiscussListBean userDiscussListBean) {
                        if (userDiscussListBean == null) {
                            mRExBaseAdapter.setEnableLoadMore(false);
                            onUILoadDataEnd();
                            return;
                        }
                        List<UserDiscussListBean.DataListBean> data_list = userDiscussListBean.getData_list();
                        if (data_list != null && data_list.size() > 0) {
                            UserDiscussListBean.DataListBean lastBean = data_list.get(data_list.size() - 1);
                            mSearchUserBean.setIs_attention(lastBean.getUser_info().getIs_attention());
                            mSearchUserBean.setIs_contact(lastBean.getUser_info().getIs_contact());
                            initCommandView();
                            mRExBaseAdapter.setEnableLoadMore(true);
                        }
                        onUILoadDataEnd(data_list, userDiscussListBean.getData_count());
                    }


                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        hideLoadView();
                        onUILoadDataFinish();
                    }
                }));
    }

    @Override
    protected String getEmptyTipString() {
        return mActivity.getResources().getString(R.string.default_empty_no_status_tip);
    }

    @Override
    protected void onEmptyData(boolean isEmpty) {
        if (isEmpty) {
            List<UserDiscussListBean.DataListBean> datas = new ArrayList<>();
            UserDiscussListBean.DataListBean empty = new UserDiscussListBean.DataListBean(true);
            datas.add(empty);
            mRExBaseAdapter.resetAllData(datas);
        }
    }

    private class UserInfoAdapter extends RExBaseAdapter<SearchUserBean, UserDiscussListBean.DataListBean, String> {

        public UserInfoAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            if (viewType == TYPE_HEADER) {
                return R.layout.item_search_user_top_layout;
            } else if (viewType == 100) {
                return R.layout.layout_default_pager;
            }
            return R.layout.item_search_user_item_layout;
        }

        @Override
        protected int getDataItemType(int posInData) {
            final UserDiscussListBean.DataListBean dataListBean = getAllDatas().get(posInData);
            if (dataListBean.isDataEmpty()) {
                return 100;
            }
            return super.getDataItemType(posInData);
        }

        @Override
        protected void onBindHeaderView(RBaseViewHolder holder, int posInHeader, SearchUserBean headerBean) {
            ArrayList<String> photos = new ArrayList<>();
            photos.add(headerBean.getAvatar());
            photos.addAll(RUtils.split(headerBean.getPhotos()));
            PhotoPager.init(mILayout,
                    ((TextIndicator) holder.v(R.id.single_text_indicator_view)),
                    (ViewPager) holder.v(R.id.view_pager), photos);
            holder.fillView(headerBean, true);
            HnGenderView hnGenderView = holder.v(R.id.grade);
            hnGenderView.setGender(headerBean.getSex(), headerBean.getGrade());

            if (!TextUtils.isEmpty(headerBean.getIntroduce())) {
                holder.v(R.id.introduce).setVisibility(View.VISIBLE);
            }
            holder.v(R.id.star).setVisibility(1 == headerBean.getIs_star() ? View.VISIBLE : View.GONE);
            holder.v(R.id.auth).setVisibility("1".equalsIgnoreCase(headerBean.getIs_auth()) ? View.VISIBLE : View.GONE);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, final int posInData, final UserDiscussListBean.DataListBean dataBean) {
            if (holder.getItemViewType() == 100) {
                initEmpty(holder, true, getEmptyTipString());
                return;
            }

            final String uid = UserCache.getUserAccount();
            final String to_uid = dataBean.getUser_info().getUid();

            //UserDiscussItemControl.initItem(holder, tBean);

            final SimpleDraweeView mediaImageType = holder.v(R.id.media_image_view);
            final List<String> medias = RUtils.split(dataBean.getMedia());
            if ("3".equalsIgnoreCase(dataBean.getMedia_type())) {
                mediaImageType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePagerUIView.start(getILayout(), v, PhotoPager.getImageItems(medias), 0);
                    }
                });
            }

            final TextView isMe = holder.tV(R.id.is_me_view);
            if (posInData == 0) {
                isMe.setVisibility(View.VISIBLE);
                if (TextUtils.equals(to_uid, uid)) {
                    isMe.setText(R.string.me_dynamic_state);
                } else {
                    isMe.setText(R.string.ta_dynamic_state);
                }
            } else {
                isMe.setVisibility(View.GONE);
            }

            //bindCommandItemView(holder, posInData, tBean, uid, to_uid);
            UserDiscussItemControl.initItem(mSubscriptions, holder, dataBean, new Action1<UserDiscussListBean.DataListBean>() {
                @Override
                public void call(UserDiscussListBean.DataListBean dataListBean) {
                    loadData();//星期五 2017-2-10
                }
            }, new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    //mParentILayout.startIView(new DynamicDetailUIView2(dataBean.getDiscuss_id()));
                    UserDiscussItemControl.jumpToDynamicDetailUIView(mParentILayout, dataBean.getDiscuss_id(),
                            false, false, false, aBoolean);
                }
            }, mParentILayout);
        }

        /**
         * 关注和取消关注
         */
        private void bindCommandItemView(RBaseViewHolder holder, final int posInData,
                                         UserDiscussListBean.DataListBean tBean,
                                         final String uid, final String to_uid) {
            TextView commandItemView = holder.v(R.id.command_item_view);
            commandItemView.setVisibility(View.VISIBLE);
            if (tBean.getUser_info().getIs_attention() == 1) {
                commandItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add(RRetrofit.create(UserService.class)
                                .unAttention(Param.buildMap("uid:" + uid, "to_uid:" + to_uid))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onSucceed(String bean) {
                                        T_.show(bean);
                                        notifyItemChanged(getHeaderCount() + posInData);
                                    }
                                }));
                    }
                });
            } else {
                commandItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add(RRetrofit.create(UserService.class)
                                .attention(Param.buildMap("uid:" + uid, "to_uid:" + to_uid))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onSucceed(String bean) {
                                        T_.show(getString(R.string.handle_success));
                                        notifyItemChanged(getHeaderCount() + posInData);
                                    }
                                }));
                    }
                });
            }
        }
    }
}
