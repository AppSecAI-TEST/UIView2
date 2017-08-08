package com.hn.d.valley.main.me.setting;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.ListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/06/01 14:30
 * 修改人员：hewking
 * 修改时间：2017/06/1 14:30
 * 修改备注：
 * Version: 1.0.0
 */
public class SpecialListUIView extends SingleRecyclerUIView<LikeUserInfoBean> {

    public static final int NOT_ALLOW_ACCESS_ME = 2;
    public static final int NOT_ACCESS_OTHER = 1;

    private int type;

    public SpecialListUIView(int type) {
        this.type = type;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        if (type == 1) {
            return super.getTitleBar().setTitleString(mActivity, R.string.text_not_see_other);
        } else {
            return super.getTitleBar().setTitleString(mActivity, R.string.text_not_allow_access_me);
        }
    }

    @Override
    protected RExBaseAdapter<String, LikeUserInfoBean, String> initRExBaseAdapter() {
        return new BlackListAdapter(mActivity);
    }

    @Override
    protected boolean hasDecoration() {
        return true;
    }

    @Override
    protected void onEmptyData(boolean isEmpty) {
        super.onEmptyData(isEmpty);
        String s;
        if (type == 1) {
            s = "添加到此处的用户,\n他们发布的动态将不会出现在你的关注动态中";
        } else {
            s = "添加到此处的用户,\n你发布的动态将不会被他们看到";
        }
        initOverEmptyLayout(s, R.drawable.image_nothing);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(ContactService.class)
                .specialList(Param.buildMap("uid:" + UserCache.getUserAccount(), "page:" + page, "type:" + this.type))
                .compose(Rx.transformer(SpecialUserList.class))
                .subscribe(new SingleRSubscriber<SpecialUserList>(this) {
                    @Override
                    protected void onResult(SpecialUserList bean) {
                        if (bean == null || bean.getData_list().isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(bean.getData_list());
                        }
                    }
                }));
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    private static class SpecialUserList extends ListModel<LikeUserInfoBean> {
    }

    private class BlackListAdapter extends RExBaseAdapter<String, LikeUserInfoBean, String> {

        public BlackListAdapter(Context context) {
            super(context);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, final LikeUserInfoBean dataBean) {
            super.onBindDataView(holder, posInData, dataBean);

            SimpleDraweeView iv_head = holder.v(R.id.iv_item_head);
            TextView tv_friend_name = holder.tv(R.id.tv_friend_name);
            DraweeViewUtil.setDraweeViewHttp(iv_head, dataBean.getAvatar());
            tv_friend_name.setText(dataBean.getUsername());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

        }


        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_friends_item;
        }
    }
}
