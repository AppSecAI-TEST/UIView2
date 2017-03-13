package com.hn.d.valley.main.me;

import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：我的身份/身份认证
 * 创建人员：Robi
 * 创建时间：2017/03/10 10:39
 * 修改人员：Robi
 * 修改时间：2017/03/10 10:39
 * 修改备注：
 * Version: 1.0.0
 */
public class MyAuthUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    /**
     * 认证类别【1-职场名人，2-娱乐明星，3-体育人物，4-政府人员】
     */

    private AuthType mAuthType;

    @Override
    protected int getTitleResource() {
        return R.string.my_auth_title;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_info_layout;
        } else if (viewType == 1) {
            return R.layout.item_single_text_view;
        } else {
            if (mAuthType == AuthType.ZCMR) {
                if (viewType == 7 || viewType == 9) {
                    return R.layout.item_single_text_view;
                } else if (viewType == 8 || viewType == 10) {
                    return R.layout.item_single_input_view;
                } else if (viewType == 11) {
                    return R.layout.item_button_view;
                } else {
                    return R.layout.item_input_view;
                }
            }

        }
        return R.layout.item_input_view;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        //清理之前的缓存Views
        mRecyclerView.getRecycledViewPool().clear();

        items.add(ViewItemInfo.build(new ItemOffsetCallback(mBaseOffsetSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                itemInfoLayout.setItemText(getString(R.string.auth_type_tip));

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new AuthTypeSelectorUIView(mAuthType, new Action1<AuthType>() {
                            @Override
                            public void call(AuthType authType) {
                                mAuthType = authType;
                                refreshLayout();
                            }
                        }));
                    }
                });

                if (mAuthType != null) {
                    switch (mAuthType) {
                        case ZCMR:
                            itemInfoLayout.setItemDarkText(getString(R.string.auth_zcmr));
                            break;
                        case YLMX:
                            itemInfoLayout.setItemDarkText(getString(R.string.auth_ylmx));
                            break;
                        case ZFRY:
                            itemInfoLayout.setItemDarkText(getString(R.string.auth_zfry));
                            break;
                        case TYRW:
                            itemInfoLayout.setItemDarkText(getString(R.string.auth_tyrw));
                            break;
                    }
                }
            }
        }));

        if (mAuthType != null) {

            items.add(ViewItemInfo.build(new ItemCallback() {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    holder.tv(R.id.text_view).setText("基本信息");
                    holder.tv(R.id.text_view).setTextColor(getResources().getColor(R.color.main_text_color));
                }
            }));

            switch (mAuthType) {
                case ZCMR:
                    zcmr(items);
                    break;
                case YLMX:
                    ylmx(items);
                    break;
                case ZFRY:
                    zfry(items);
                    break;
                case TYRW:
                    tyrw(items);
                    break;
            }
        }
    }

    /**
     * 职场名人
     */
    private void zcmr(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText("真实姓名");
                holder.tv(R.id.edit_text_view).setImeOptions(EditorInfo.IME_ACTION_NEXT);
//                holder.tv(R.id.edit_text_view).setHint("真实姓名");
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText("身份证号");
                holder.tv(R.id.edit_text_view).setImeOptions(EditorInfo.IME_ACTION_NEXT);
//                holder.tv(R.id.edit_text_view).setHint("真实姓名");
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText("行业");
//                holder.tv(R.id.edit_text_view).setHint("真实姓名");
                holder.tv(R.id.edit_text_view).setImeOptions(EditorInfo.IME_ACTION_NEXT);
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText("公司");
//                holder.tv(R.id.edit_text_view).setHint("真实姓名");
                holder.tv(R.id.edit_text_view).setImeOptions(EditorInfo.IME_ACTION_NEXT);
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText("职位");
//                holder.tv(R.id.edit_text_view).setHint("真实姓名");
                holder.tv(R.id.edit_text_view).setImeOptions(EditorInfo.IME_ACTION_NEXT);
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.text_view).setText("人物介绍");
                holder.tv(R.id.text_view).setTextColor(getResources().getColor(R.color.main_text_color));
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ExEditText editText = holder.v(R.id.edit_text_view);
                editText.setHint("您可以输入您的个人经历,重大业绩,获奖记录等.");
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                editText.setSingleLine(false);
                editText.setMaxLines(5);
                editText.setGravity(Gravity.TOP);
                UI.setViewHeight(editText, mActivity.getResources().getDimensionPixelOffset(R.dimen.base_100dpi));
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.text_view).setText("个人链接");
                holder.tv(R.id.text_view).setTextColor(getResources().getColor(R.color.main_text_color));
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.edit_text_view).setHint("请输入链接地址");
                holder.tv(R.id.edit_text_view).setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(mBaseOffsetSize * 2) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.text_view).setText("下一步");
            }
        }));
    }

    /**
     * 娱乐明星
     */
    private void ylmx(List<ViewItemInfo> items) {

    }

    /**
     * 政府人员
     */
    private void zfry(List<ViewItemInfo> items) {

    }

    /**
     * 体育明星
     */
    private void tyrw(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
            }
        }));
    }

    public enum AuthType {
        ZCMR(0, ValleyApp.getApp().getResources().getString(R.string.auth_zcmr)),
        YLMX(1, ValleyApp.getApp().getResources().getString(R.string.auth_ylmx)),
        TYRW(2, ValleyApp.getApp().getResources().getString(R.string.auth_tyrw)),
        ZFRY(3, ValleyApp.getApp().getResources().getString(R.string.auth_zfry));

        int id;
        String des;

        AuthType(int id, String des) {
            this.id = id;
            this.des = des;
        }

        static AuthType valueOf(int id) {
            switch (id) {
                case 0:
                    return ZCMR;
                case 1:
                    return YLMX;
                case 2:
                    return TYRW;
                case 3:
                    return ZFRY;
                default:
                    return null;
            }
        }

        public int getId() {
            return id;
        }

        public String getDes() {
            return des;
        }
    }
}
