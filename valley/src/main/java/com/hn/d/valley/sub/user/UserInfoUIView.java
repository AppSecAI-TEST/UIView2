package com.hn.d.valley.sub.user;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.angcyo.uiview.utils.Utils;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.base.Transform;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.SearchUserBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.ChatUIView;
import com.hn.d.valley.sub.user.service.UserInfoService;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnItemTextView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/05 17:43
 * 修改人员：Robi
 * 修改时间：2017/01/05 17:43
 * 修改备注：
 * Version: 1.0.0
 */
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
        mRecyclerView.addItemDecoration(new RBaseItemDecoration(mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi))
                .setDrawLastLine(true));
    }

    @Override
    protected void inflateOverlayLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
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
        final String to_uid = mSearchUserBean.getUid();
        final String uid = UserCache.getUserAccount();
        if (TextUtils.equals(uid, to_uid)) {
            mCommandImageView.setVisibility(View.GONE);
        } else {
            mCommandImageView.setVisibility(View.VISIBLE);
            if (mSearchUserBean.getIs_contact() == 1) {
                //是联系人
                mCommandImageView.setImageResource(R.drawable.send_message_selector);
                mCommandImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatUIView.start(getILayout(), to_uid, SessionTypeEnum.P2P);
                    }
                });
            } else {
                //不是联系人
                mCommandImageView.setImageResource(R.drawable.add_contact2_selector);
                mCommandImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add(RRetrofit.create(UserInfoService.class)
                                .addContact(Param.buildMap("uid:" + uid, "to_uid:" + to_uid,
                                        "tip:" + mActivity.getResources().getString(R.string.add_contact_tip,
                                                UserCache.instance().getUserInfoBean().getUsername())))
                                .compose(Transform.defaultStringSchedulers(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onNext(String bean) {
                                        T_.show(bean);
                                    }
                                }));
                    }
                });
            }
        }
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        Map<String, String> map = getPageMap();
        map.put("uid", UserCache.getUserAccount());
        map.put("to_uid", mSearchUserBean.getUid());

        add(RRetrofit.create(UserInfoService.class)
                .discussList(Param.map(map))
                .compose(Transform.defaultStringSchedulers(UserDiscussListBean.class))
                .subscribe(new BaseSingleSubscriber<UserDiscussListBean>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onNext(UserDiscussListBean userDiscussListBean) {
                        onUILoadDataEnd(userDiscussListBean.getData_list(), userDiscussListBean.getData_count());
                    }

                    @Override
                    protected void onEnd() {
                        hideLoadView();
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
        protected void onBindHeaderView(RBaseViewHolder holder, int posInHeader, SearchUserBean hBean) {
            ArrayList<String> photos = new ArrayList<>();
            photos.add(hBean.getAvatar());
            photos.addAll(Utils.split(hBean.getPhotos()));
            PhotoPager.init(mILayout,
                    ((TextIndicator) holder.v(R.id.single_text_indicator_view)),
                    (ViewPager) holder.v(R.id.view_pager), photos);
            holder.fillView(hBean, true);
            HnGenderView hnGenderView = holder.v(R.id.grade);
            hnGenderView.setGender(hBean.getSex(), hBean.getGrade());

            holder.v(R.id.star).setVisibility(1 == hBean.getIs_star() ? View.VISIBLE : View.GONE);
            holder.v(R.id.auth).setVisibility("1".equalsIgnoreCase(hBean.getIs_auth()) ? View.VISIBLE : View.GONE);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, UserDiscussListBean.DataListBean tBean) {
            if (holder.getItemViewType() == 100) {
                initEmpty(holder, true, getEmptyTipString());
                return;
            }

            holder.fillView(tBean, true);
            holder.fillView(tBean.getUser_info(), true);

            final TextView isMe = holder.tV(R.id.is_me_view);
            final TextView mediaCountView = holder.tV(R.id.media_count_view);
            final SimpleDraweeView mediaImageType = holder.v(R.id.media_image_view);
            final View mediaControlLayout = holder.v(R.id.media_control_layout);

            final List<String> medias = Utils.split(tBean.getMedia());
            if (medias.isEmpty()) {
                mediaControlLayout.setVisibility(View.GONE);
            } else {
                mediaControlLayout.setVisibility(View.VISIBLE);
                mediaCountView.setText("" + medias.size());
                if ("3".equalsIgnoreCase(tBean.getMedia_type())) {
                    mediaImageType.setVisibility(View.VISIBLE);
                    DraweeViewUtil.setDraweeViewHttp(mediaImageType, medias.get(0));

                    mediaImageType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImagePagerUIView.start(getILayout(), v, PhotoPager.getImageItems(medias), 0);
                        }
                    });
                } else {
                    mediaImageType.setVisibility(View.GONE);
                }
            }

            if (posInData == 0) {
                isMe.setVisibility(View.VISIBLE);
                if (TextUtils.equals(tBean.getUser_info().getUid(), UserCache.getUserAccount())) {
                    isMe.setText(R.string.me_dynamic_state);
                } else {
                    isMe.setText(R.string.ta_dynamic_state);
                }
            } else {
                isMe.setVisibility(View.GONE);
            }

            HnItemTextView fav_cnt = holder.v(R.id.fav_cnt);
            HnItemTextView like_cnt = holder.v(R.id.fav_cnt);

            if (tBean.getIs_collect() == 1) {
                //是否收藏
                fav_cnt.setLeftIco(R.drawable.collection_icon_s);
            } else {
                fav_cnt.setLeftIco(R.drawable.collection_icon_n);
            }

            if (tBean.getIs_like() == 1) {
                //是否点赞
                like_cnt.setLeftIco(R.drawable.thumb_up_icon_s);
            } else {
                like_cnt.setLeftIco(R.drawable.thumb_up_icon_n);
            }
        }
    }
}