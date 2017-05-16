package com.hn.d.valley.main.me;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.github.utilcode.utils.IDCardUtil;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.ArrayList;
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
    /**
     * 选中的行业
     */
    private Tag mSelectorTag;

    //需要检查是否为空的Edit
    private List<ExEditText> mCheckEditTexts = new ArrayList<>();
    //不需要检查为空的Edit
    private List<ExEditText> mOtherEditTexts = new ArrayList<>();
    //其他Item布局
    private ItemInfoLayout mInfoLayout;

    public static boolean checkEmpty(ExEditText view) {
        if (view.isEmpty()) {
            view.requestFocus();
            view.setError(ValleyApp.getApp().getString(R.string.not_empty_tip));
            return true;
        }
        return false;
    }

    @Override
    protected int getTitleResource() {
        return R.string.my_auth_title;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_info_layout;
        } else if (viewType == 1 || viewType == 6 || viewType == 8) {
            return R.layout.item_single_text_view;
        } else if (viewType == 7 || viewType == 9) {
            return R.layout.item_single_input_view;
        } else if (viewType == 10) {
            return R.layout.item_button_view;
        } else if (viewType == 104) {
            return R.layout.item_info_layout;

        }
        return R.layout.item_input_view;
    }

    /**
     * 防止不同认证类型, 相同item type之间的共享BUG
     */
    @Override
    protected int getDataItemType(int posInData) {
        int type = posInData;
        if (mAuthType != null) {
            switch (mAuthType) {
                case ZCMR:
                    if (posInData == 4) {
                        type = 104;
                    } else if (posInData == 5) {
                        type = 105;
                    } else if (posInData == 6) {
                        type = 106;
                    } else if (posInData >= 7) {
                        return posInData - 1;
                    }
                    break;
                case YLMX:
                    if (posInData == 4) {
                        type = 204;
                    } else if (posInData == 5) {
                        type = 205;
                    }
                    break;
                case ZFRY:
                    if (posInData == 4) {
                        type = 304;
                    } else if (posInData == 5) {
                        type = 305;
                    }
                    break;
                case TYRW:
                    if (posInData == 4) {
                        type = 404;
                    } else if (posInData == 5) {
                        type = 405;
                    }
                    break;
            }
        }
        return type;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        clearEditTexts();

        //类型选择
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

            //基础信息
            items.add(ViewItemInfo.build(new ItemCallback() {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    holder.tv(R.id.text_view).setText(R.string.base_info_tip);
                    holder.tv(R.id.text_view).setTextColor(getResources().getColor(R.color.main_text_color));
                }
            }));

            //姓名
            items.add(ViewItemInfo.build(new ItemCallback() {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    holder.tv(R.id.input_tip_view).setText(R.string.true_name_tip);
                    ExEditText editText = holder.v(R.id.edit_text_view);
                    editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    editText.setMaxLength(4);

                    mCheckEditTexts.add(editText);
                }
            }));

            //身份证
            items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    holder.tv(R.id.input_tip_view).setText(R.string.id_card_tip);
                    ExEditText editText = holder.v(R.id.edit_text_view);
                    editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    editText.setMaxLength(18);
                    editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                    mCheckEditTexts.add(editText);
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

            //个人介绍
            items.add(ViewItemInfo.build(new ItemCallback() {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    holder.tv(R.id.text_view).setText(R.string.introduction_tip);
                    holder.tv(R.id.text_view).setTextColor(getResources().getColor(R.color.main_text_color));
                }
            }));
            items.add(ViewItemInfo.build(new ItemCallback() {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    ExEditText editText = holder.v(R.id.edit_text_view);
                    editText.setHint(R.string.introduction_hint);
                    editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                    editText.setSingleLine(false);
                    editText.setMaxLines(5);
                    editText.setGravity(Gravity.TOP);
                    UI.setViewHeight(editText, mActivity.getResources().getDimensionPixelOffset(R.dimen.base_100dpi));
                    mOtherEditTexts.add(editText);
                }
            }));

            //个人作品
            items.add(ViewItemInfo.build(new ItemCallback() {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    holder.tv(R.id.text_view).setText(R.string.link_tip);
                    holder.tv(R.id.text_view).setTextColor(getResources().getColor(R.color.main_text_color));
                }
            }));
            items.add(ViewItemInfo.build(new ItemCallback() {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    holder.tv(R.id.edit_text_view).setHint(R.string.link_hint);

                    ExEditText editText = holder.v(R.id.edit_text_view);
                    editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    mOtherEditTexts.add(editText);
                }
            }));

            //下一步
            items.add(ViewItemInfo.build(new ItemOffsetCallback(mBaseOffsetSize * 2) {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    holder.tv(R.id.text_view).setText(R.string.next);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            if (BuildConfig.DEBUG) {
//                                startIView(new MyAuthNextUIView(makeAuthInfo()));
//                                return;
//                            }

                            for (ExEditText edit : mCheckEditTexts) {
                                if (checkEmpty(edit)) {
                                    return;
                                }
                            }

                            if (checkEmpty(mOtherEditTexts.get(0))) {
                                return;
                            }

                            if (!TextUtils.isEmpty(mOtherEditTexts.get(1).string())) {
                                //如果不为空,则检测网址是否正确
//                                if (!RUtils.isHttpUrl(mOtherEditTexts.get(1).string())) {
//                                    T_.error(getString(R.string.url_error_tip));
//                                    return;
//                                }
                            }

                            ExEditText editText = mCheckEditTexts.get(1);
                            if (!BuildConfig.DEBUG && !"YES".equalsIgnoreCase(IDCardUtil.IDCardValidate(editText.string()))) {
                                //T_.error("无效的身份证号码!");
                                editText.requestFocus();
                                editText.setError(getString(R.string.error_id_card_tip));
                                return;
                            }
                            if (mSelectorTag == null) {
                                Anim.band(mInfoLayout);
                                return;
                            }
                            startIView(new MyAuthNextUIView(makeAuthInfo()));
                        }
                    });
                }
            }));
        }
    }

    private MyAuthNextUIView.AuthInfo makeAuthInfo() {
        List<String> baseInfo = new ArrayList<>();
        for (ExEditText editText : mCheckEditTexts) {
            baseInfo.add(editText.string());
        }
        return new MyAuthNextUIView.AuthInfo(baseInfo, mSelectorTag == null ? "" : mSelectorTag.getId(),
                mOtherEditTexts.get(0).string(), mOtherEditTexts.get(1).string(), mAuthType);
    }

    /**
     * 职场名人
     */
    private void zcmr(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                mInfoLayout = holder.v(R.id.item_info_layout);
                mInfoLayout.setItemText(getString(R.string.industries_tip));
                mInfoLayout.getTextView().setTextColor(getColor(R.color.main_text_color_dark));
                mInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new IndustriesSelectorUIView(new Action1<Tag>() {
                            @Override
                            public void call(Tag tag) {
                                mSelectorTag = tag;
                                mInfoLayout.setItemDarkText(tag.getName());
                            }
                        }));
                    }
                });
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.company_tip);
                ExEditText editText = holder.v(R.id.edit_text_view);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                mCheckEditTexts.add(editText);
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.job_tip);
                ExEditText editText = holder.v(R.id.edit_text_view);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                mCheckEditTexts.add(editText);
            }
        }));
    }

    private void clearEditTexts() {
        for (ExEditText editText : mCheckEditTexts) {
            editText.setError(null);
        }
        mCheckEditTexts.clear();
        for (ExEditText editText : mOtherEditTexts) {
            editText.setError(null);
        }
        mOtherEditTexts.clear();
    }

    /**
     * 娱乐明星
     */
    private void ylmx(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.manage_company_tip);
                ExEditText editText = holder.v(R.id.edit_text_view);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                mCheckEditTexts.add(editText);
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.occupation_tip);
                ExEditText editText = holder.v(R.id.edit_text_view);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                mCheckEditTexts.add(editText);
            }
        }));
    }

    /**
     * 政府人员
     */
    private void zfry(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.organization_tip);
                ExEditText editText = holder.v(R.id.edit_text_view);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                mCheckEditTexts.add(editText);
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.job_tip);
                ExEditText editText = holder.v(R.id.edit_text_view);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                mCheckEditTexts.add(editText);
            }
        }));
    }

    /**
     * 体育明星
     */
    private void tyrw(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.item_tip);
                ExEditText editText = holder.v(R.id.edit_text_view);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                mCheckEditTexts.add(editText);
            }
        }));
        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.job_tip);
                ExEditText editText = holder.v(R.id.edit_text_view);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                mCheckEditTexts.add(editText);
            }
        }));
    }

    /**
     * 认证类型
     */
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
