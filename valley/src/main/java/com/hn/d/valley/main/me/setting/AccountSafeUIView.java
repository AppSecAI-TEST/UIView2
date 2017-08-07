package com.hn.d.valley.main.me.setting;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：账户安全界面
 * 创建人员：Robi
 * 创建时间：2017/02/15 09:50
 * 修改人员：Robi
 * 修改时间：2017/02/15 09:50
 * 修改备注：
 * Version: 1.0.0
 */
public class AccountSafeUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    @Override
    protected String getTitleString() {
        return mActivity.getString(R.string.account_safe);
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, int posInData, ItemRecyclerUIView.ViewItemInfo dataBean) {
        ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
        infoLayout.setItemText(dataBean.itemString);

        final String phone = UserCache.instance().getUserInfoBean().getPhone();

        if (dataBean.itemClickListener == null) {
            infoLayout.setClickable(false);
        } else {
            infoLayout.setOnClickListener(dataBean.itemClickListener);
        }
        if (posInData == 0) {
            infoLayout.setItemDarkText(UserCache.getUserAccount());
            infoLayout.setRightDrawableRes(-1);
        } else if (posInData == 1) {
            if (TextUtils.isEmpty(phone)) {
                infoLayout.setItemDarkText(mActivity.getString(R.string.not_bind_phone));
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //绑定手机号码
                        startIView(new BindPhoneUIView());
                    }
                });
            } else {
                infoLayout.setItemDarkText(phone);
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //修改手机号码
                        startIView(new BindPhoneUIView().setOldPhone(phone));
                    }
                });
            }
        } else if (posInData == 2) {
            if (UserCache.instance().getUserInfoBean().getIs_set_password() == 1) {
                infoLayout.setItemDarkText("");
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new SetPasswordUIView(SetPasswordUIView.TYPE_MODIFY_PW));
                    }
                });
            } else {
                infoLayout.setItemDarkText(mActivity.getString(R.string.not_set));
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new SetPasswordUIView(SetPasswordUIView.TYPE_SET_PW));
                    }
                });
            }
        } else if (posInData == 3) {
            final int is_login_protect = UserCache.instance().getUserInfoBean().getIs_login_protect();
            if (is_login_protect == 1) {
                infoLayout.setItemDarkText(mActivity.getString(R.string.is_open));
            } else {
                infoLayout.setItemDarkText(mActivity.getString(R.string.not_open));
            }
            if (TextUtils.isEmpty(phone)) {
                UIDialog.build()
                        .setDialogContent(mActivity.getString(R.string.text_please_bind_phone))
                        .setOkText(mActivity.getString(R.string.text_bind_phone))
                        .setCancelText(mActivity.getString(R.string.cancel))
                        .setCancelListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startIView(new BindPhoneUIView());
                            }
                        })
                        .showDialog(mParentILayout);
            } else {
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new LoginProtectUIView(is_login_protect == 1));
                    }
                });
            }
        }
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_info_layout;
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        mRecyclerView.addItemDecoration(new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
            @Override
            public void getItemOffsets(Rect outRect, int position) {
                if (position == 0) {
                    outRect.top = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
                } else {
                    outRect.top = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
                }
            }
        }));
    }

    @Override
    public void onViewReShow(Bundle bundle) {
        super.onViewReShow(bundle);
        mRExBaseAdapter.notifyDataSetChanged();
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(new ViewItemInfo(mActivity.getString(R.string.id), null));
        items.add(new ViewItemInfo(mActivity.getString(R.string.phone_number2), null));
        items.add(new ViewItemInfo(mActivity.getString(R.string.password), null));
        items.add(new ViewItemInfo(mActivity.getString(R.string.login_safe), null));
    }
}
