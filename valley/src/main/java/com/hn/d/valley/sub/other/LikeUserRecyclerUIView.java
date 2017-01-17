package com.hn.d.valley.sub.other;

import android.text.TextUtils;
import android.view.View;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.LikeUserModel;
import com.hn.d.valley.main.me.UserDetailUIView;
import com.hn.d.valley.sub.user.service.SocialService;
import com.hn.d.valley.widget.HnGenderView;

import java.util.Locale;

import static android.view.View.VISIBLE;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：点赞用户列表界面
 * 创建人员：Robi
 * 创建时间：2017/01/14 18:10
 * 修改人员：Robi
 * 修改时间：2017/01/14 18:10
 * 修改备注：
 * Version: 1.0.0
 */
public class LikeUserRecyclerUIView extends SingleRecyclerUIView<LikeUserInfoBean> {

    private String discuss_id;

    public LikeUserRecyclerUIView(String discuss_id) {
        this.discuss_id = discuss_id;
    }

    @Override
    protected String getTitleString() {
        return mActivity.getString(R.string.like_title);
    }

    @Override
    protected RExBaseAdapter<String, LikeUserInfoBean, String> initRExBaseAdapter() {
        return new RExBaseAdapter<String, LikeUserInfoBean, String>(mActivity) {
            @Override
            protected int getItemLayoutId(int viewType) {
                return R.layout.layout_user_info;
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, final LikeUserInfoBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                holder.fillView(dataBean, true);
                holder.v(R.id.user_info_root_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                holder.v(R.id.right_layout).setVisibility(View.GONE);
                holder.v(R.id.bottom_line_view).setVisibility(View.GONE);
                holder.v(R.id.avatar).setVisibility(VISIBLE);
                HnGenderView genderView = holder.v(R.id.grade);
                genderView.setGender(dataBean.getSex(), dataBean.getGrade());

                holder.v(R.id.auth).setVisibility("1".equalsIgnoreCase(dataBean.getIs_auth()) ? View.VISIBLE : View.GONE);
                holder.tv(R.id.introduce).setVisibility(TextUtils.isEmpty(dataBean.getSignature()) ? View.GONE : VISIBLE);
                holder.tv(R.id.auth_desc).setVisibility(TextUtils.isEmpty(dataBean.getCompany()) ? View.GONE : VISIBLE);
                holder.tv(R.id.introduce).setText(dataBean.getSignature());
                holder.tv(R.id.auth_desc).setText(dataBean.getCompany());

                holder.v(R.id.user_info_root_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new UserDetailUIView(dataBean.getUid()));
                    }
                });
            }
        };
    }

    @Override
    protected void onEmptyData(boolean isEmpty) {
        super.onEmptyData(isEmpty);
        if (isEmpty) {
            setTitleString(getTitleString());
        } else {
            setTitleString(String.format(Locale.CHINA,
                    mActivity.getString(R.string.like_title_formater),
                    mRExBaseAdapter.getRawItemCount()));
        }
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(SocialService.class)
                .likeList(Param.buildMap("type:discuss", "item_id:" + discuss_id, "page:" + page))
                .compose(Rx.transformer(LikeUserModel.class))
                .subscribe(new SingleRSubscriber<LikeUserModel>(this) {
                    @Override
                    protected void onResult(LikeUserModel bean) {
                        if (bean == null || bean.getData_list() == null || bean.getData_list().isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(bean.getData_list(), bean.getData_count());
                        }
                    }
                }));
    }
}
