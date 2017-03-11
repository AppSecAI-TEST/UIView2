package com.hn.d.valley.sub.other;

import android.view.View;
import android.widget.ImageView;

import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.angcyo.uiview.recycler.RModelAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.adapter.UserInfoAdapter;

/**
 * 用户信息列表界面
 * Created by angcyo on 2017-01-15.
 */

public abstract class UserInfoRecyclerUIView extends SingleRecyclerUIView<LikeUserInfoBean> {

    @Override
    protected boolean hasDecoration() {
        return true;
    }

    @Override
    protected RExBaseAdapter<String, LikeUserInfoBean, String> initRExBaseAdapter() {
        UserInfoAdapter userInfoAdapter = new UserInfoAdapter(mActivity, mOtherILayout) {
            @Override
            protected void onBindDataView(RBaseViewHolder holder, final int posInData, final LikeUserInfoBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                //关注
                final ImageView followView = holder.v(R.id.follow_image_view);
                final String to_uid = dataBean.getUid();

                View.OnClickListener clickListener = null;
                String titleString = "";
                if (isContact(dataBean)) {
                    titleString = mContext.getString(R.string.unattention_tip2);
                } else {
                    if (isAttention(dataBean)) {
                        titleString = mContext.getString(R.string.unattention_tip1);
                    } else {
                        //未关注
                        clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setSelectorPosition(posInData);
                                add(RRetrofit.create(UserInfoService.class)
                                        .attention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                                        .compose(Rx.transformer(String.class))
                                        .subscribe(new BaseSingleSubscriber<String>() {

                                            @Override
                                            public void onSucceed(String bean) {
                                                //dataBean.setIs_attention(1);
                                                onSetDataBean(dataBean, true);
                                                setSelectorPosition(posInData);
                                            }

                                            @Override
                                            public void onError(int code, String msg) {
                                                super.onError(code, msg);
                                                setSelectorPosition(posInData);
                                            }
                                        }));
                            }
                        };
                    }
                }

                if (isContact(dataBean) || isAttention(dataBean)) {
                    final String finalTitleString = titleString;
                    clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //取消关注
                            UIBottomItemDialog.build()
                                    .setTitleString(finalTitleString)
                                    .addItem(new UIItemDialog.ItemInfo(mActivity.getResources().getString(R.string.base_ok), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            setSelectorPosition(posInData);
                                            add(RRetrofit.create(UserInfoService.class)
                                                    .unAttention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                                                    .compose(Rx.transformer(String.class))
                                                    .subscribe(new BaseSingleSubscriber<String>() {

                                                        @Override
                                                        public void onSucceed(String bean) {
                                                            //dataBean.setIs_attention(0);
                                                            onSetDataBean(dataBean, false);
                                                            setSelectorPosition(posInData);
                                                        }

                                                        @Override
                                                        public void onError(int code, String msg) {
                                                            super.onError(code, msg);
                                                            setSelectorPosition(posInData);
                                                        }
                                                    }));
                                        }
                                    })).showDialog(mOtherILayout);
                        }
                    };
                }

                followView.setOnClickListener(clickListener);
            }

            @Override
            protected boolean isContact(LikeUserInfoBean dataBean) {
                return UserInfoRecyclerUIView.this.isContact(dataBean);
            }

            @Override
            protected boolean isAttention(LikeUserInfoBean dataBean) {
                return UserInfoRecyclerUIView.this.isAttention(dataBean);
            }

            @Override
            protected void onSetDataBean(LikeUserInfoBean dataBean, boolean value) {
                UserInfoRecyclerUIView.this.onSetDataBean(dataBean, value);
            }

            @Override
            protected boolean onUnSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
                if (!isSelector) {
                    onBindDataView(viewHolder, position, getAllDatas().get(position));
                }
                return super.onUnSelectorPosition(viewHolder, position, isSelector);
            }
        };
        userInfoAdapter.setModel(RModelAdapter.MODEL_MULTI);
        return userInfoAdapter;
    }

    //是否是联系人
    protected boolean isContact(final LikeUserInfoBean dataBean) {
        return false;
    }

    //是否已关注
    protected boolean isAttention(final LikeUserInfoBean dataBean) {
        return false;
    }

    //改变数据
    protected void onSetDataBean(final LikeUserInfoBean dataBean, boolean value) {

    }

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        return super.initItemDecoration()
                .setDividerSize(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_line))
                .setMarginStart(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi))
                .setDrawLastLine(true);
    }
}
