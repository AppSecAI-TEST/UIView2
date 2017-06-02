package com.hn.d.valley.main.me.setting;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.T_;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.ListModel;
import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;

import java.util.List;

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
public class BlackListUIView extends SingleRecyclerUIView<LikeUserInfoBean>{

    public BlackListUIView() {

    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity, R.string.blacklist);
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
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(ContactService.class)
                .blacklist(Param.buildMap("page:" + page))
                .compose(Rx.transformer(BlackUserList.class))
                .subscribe(new SingleRSubscriber<BlackUserList>(this) {
                    @Override
                    protected void onResult(BlackUserList bean) {
                        if (bean == null || bean.getData_list().isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(bean.getData_list());
                        }
                    }
                }));
    }

    private static class BlackUserList extends ListModel<LikeUserInfoBean> {}

    private class BlackListAdapter extends RExBaseAdapter<String,LikeUserInfoBean,String> {

        public BlackListAdapter(Context context) {
            super(context);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, final LikeUserInfoBean dataBean) {
            super.onBindDataView(holder, posInData, dataBean);

            SimpleDraweeView iv_head = holder.v(R.id.iv_item_head);
            TextView tv_friend_name = holder.tv(R.id.tv_friend_name);
            DraweeViewUtil.setDraweeViewHttp(iv_head,dataBean.getAvatar());
            tv_friend_name.setText(dataBean.getUsername());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCancelDialog(dataBean.getUid());
                }
            });

        }

        private void showCancelDialog(final String to_uid) {
            UIDialog.build()
                    .setDialogContent(getString(R.string.text_remove_from_blacklist))
                    .setOkText(mActivity.getString(R.string.ok))
                    .setCancelText(mActivity.getString(R.string.cancel))
                    .setOkListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelBlackList(to_uid);
                        }
                    })
                    .showDialog(mParentILayout);
        }

        private void cancelBlackList(String to_uid) {
            add(RRetrofit.create(ContactService.class)
                    .cancelBlackList(Param.buildMap("to_uid:" + to_uid))
                    .compose(Rx.transformer(String.class))
                    .subscribe(new BaseSingleSubscriber<String>() {
                        @Override
                        public void onSucceed(String bean) {
                            // 重新拉取数据
                            loadData();
                            T_.show(bean);
                        }
                    }));

        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_friends_item;
        }
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }
}
