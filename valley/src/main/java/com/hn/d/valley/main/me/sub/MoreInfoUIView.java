package com.hn.d.valley.main.me.sub;

import android.view.View;

import com.angcyo.uiview.base.Item;
import com.angcyo.uiview.base.SingleItem;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseItemUIView;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.control.UserControl;
import com.hn.d.valley.main.me.setting.EditInfoUIView;

import java.util.List;
import java.util.Locale;

import static com.hn.d.valley.main.me.UserDetailUIView2.isMe;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：更多基本信息
 * 创建人员：Robi
 * 创建时间：2017/04/10 17:55
 * 修改人员：Robi
 * 修改时间：2017/04/10 17:55
 * 修改备注：
 * Version: 1.0.0
 */
public class MoreInfoUIView extends BaseItemUIView {

    UserInfoBean mUserInfoBean;

    public MoreInfoUIView(UserInfoBean userInfoBean) {
        mUserInfoBean = userInfoBean;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        TitleBarPattern titleBar = super.getTitleBar();
        if (isMe(mUserInfoBean.getUid())) {
            titleBar.addRightItem(TitleBarPattern.buildImage(R.drawable.editor, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                startIView(new EditInfoUIView(MeUIView2.initPhotos(mUserInfoBean), null));
                    startIView(new EditInfoUIView());
                }
            }));
        }
        return titleBar;
    }

    @Override
    protected int getTitleResource() {
        return R.string.base_info;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_user_info;
    }

    @Override
    protected void createItems(List<SingleItem> items) {
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.tv(R.id.tip_view).setText(R.string.username);
                holder.tv(R.id.value_view).setText(mUserInfoBean.getUsername());
            }
        });
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.tv(R.id.tip_view).setText("ID");
                holder.tv(R.id.value_view).setText(mUserInfoBean.getUid());
            }
        });
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.tv(R.id.tip_view).setText(R.string.level);
                holder.tv(R.id.value_view).setText(String.format(Locale.CHINA, "V%1$s", mUserInfoBean.getGrade()));
            }
        });
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.tv(R.id.tip_view).setText(R.string.sex);
                holder.tv(R.id.value_view).setText(UserControl.getSex(mActivity, mUserInfoBean.getSex()));
            }
        });
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.tv(R.id.tip_view).setText(R.string.birthday);
                holder.tv(R.id.value_view).setText(mUserInfoBean.getBirthday(getResources()));
            }
        });
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.tv(R.id.tip_view).setText(R.string.address);
                holder.tv(R.id.value_view).setText(mUserInfoBean.getAddress());
            }
        });
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.tv(R.id.tip_view).setText(R.string.signature);
                holder.tv(R.id.value_view).setText(mUserInfoBean.getSignature());
            }
        });

        items.add(new SingleItem(SingleItem.Type.TOP) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.tv(R.id.tip_view).setText(R.string.register_time);
                holder.tv(R.id.value_view).setText(mUserInfoBean.getCreatedTime());
            }
        });
    }
}
