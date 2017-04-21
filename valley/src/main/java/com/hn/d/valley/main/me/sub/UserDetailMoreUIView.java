package com.hn.d.valley.main.me.sub;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.base.Item;
import com.angcyo.uiview.base.SingleItem;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseItemUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.SettingService;
import com.hn.d.valley.sub.other.InputUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.user.ReportUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/19 11:02
 * 修改人员：Robi
 * 修改时间：2017/04/19 11:02
 * 修改备注：
 * Version: 1.0.0
 */
public class UserDetailMoreUIView extends BaseItemUIView {

    private UserInfoBean mUserInfoBean;

    public UserDetailMoreUIView(UserInfoBean userInfoBean) {
        mUserInfoBean = userInfoBean;
    }

    public static void initShareControlLayout(RBaseViewHolder holder, ILayout iLayout) {
        holder.v(R.id.share_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.v(R.id.share_wxc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.v(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.v(R.id.share_qqz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected int getTitleResource() {
        return R.string.more;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType > 1 && viewType < 6) {
            return R.layout.item_switch_view;
        }
        if (viewType == 7) {
            return R.layout.item_share_button_view;
        }
        if (viewType == 8) {
            return R.layout.item_button_view;
        }
        return super.getItemLayoutId(viewType);
    }

    @Override
    protected void createItems(List<SingleItem> items) {
        //设置备注名
        items.add(new SingleItem() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.item(R.id.base_item_info_layout)
                        .setItemText(getString(R.string.set_remark_tip))
                        .setItemDarkText(mUserInfoBean.getContact_remark())
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setMark();
                            }
                        });
            }
        });
        //把TA推荐给好友
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.item(R.id.base_item_info_layout)
                        .setItemText(getString(R.string.recommend_to_other_tip))
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
            }
        });
        //标为星标好友
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.item(R.id.item_info_layout)
                        .setItemText(getString(R.string.set_star_tip))
                        .setRightDrawableRes(-1)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setStar();
                            }
                        });
                holder.cV(R.id.switch_view).setChecked(isSetStar());
                holder.cV(R.id.switch_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setStar();
                    }
                });
            }
        });
        //不让TA看我的动态
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.item(R.id.item_info_layout)
                        .setItemText(getString(R.string.cant_see_my_status_tip))
                        .setRightDrawableRes(-1)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setLook_my_discuss();
                            }
                        });
                holder.cV(R.id.switch_view).setChecked(mUserInfoBean.getLook_my_discuss() != 1);
                holder.cV(R.id.switch_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setLook_my_discuss();
                    }
                });
            }
        });
        //不看TA的动态
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.item(R.id.item_info_layout)
                        .setItemText(getString(R.string.not_see_status))
                        .setRightDrawableRes(-1)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setLook_his_discuss();
                            }
                        });
                holder.cV(R.id.switch_view).setChecked(mUserInfoBean.getLook_his_discuss() != 1);
                holder.cV(R.id.switch_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setLook_his_discuss();
                    }
                });
            }
        });

        //加入黑名单
        items.add(new SingleItem(SingleItem.Type.TOP) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.item(R.id.item_info_layout)
                        .setItemText(getString(R.string.add_blackList_tip))
                        .setRightDrawableRes(-1)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addBlackList();
                            }
                        });
                holder.cV(R.id.switch_view).setChecked(isInBlackList());
                holder.cV(R.id.switch_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addBlackList();
                    }
                });
            }
        });

        //举报
        items.add(new SingleItem(SingleItem.Type.TOP_LINE) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.item(R.id.base_item_info_layout)
                        .setItemText(getString(R.string.report))
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startIView(new ReportUIView(mUserInfoBean));
                            }
                        });
            }
        });

        //分享到
        items.add(new SingleItem(SingleItem.Type.TOP) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                holder.itemView.setBackgroundColor(Color.WHITE);
                initShareControlLayout(holder, mILayout);
            }
        });

        if (mUserInfoBean.getIs_contact() == 1) {
            //解除好友
            items.add(new SingleItem() {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                    TextView textView = holder.tv(R.id.text_view);
                    textView.setText(getString(R.string.del_friend));
                    ResUtil.setBgDrawable(textView, SkinHelper.getSkin().getThemeMaskBackgroundRoundSelector());
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            add(RRetrofit.create(ContactService.class)
                                    .delFriend(Param.buildMap("to_uid:" + mUserInfoBean.getUid()))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {

                                        @Override
                                        public void onSucceed(String bean) {
                                            mUserInfoBean.setIs_attention(0);
                                            mUserInfoBean.setIs_contact(0);
                                            T_.show(bean);
                                            finishIView();
                                        }
                                    }));
                        }
                    });
                    //UserDetailUIView2.initCommandView(holder.tv(R.id.text_view), mUserInfoBean, mILayout, mSubscriptions);
                }
            });
        }
    }

    boolean isInBlackList() {
        return mUserInfoBean.getIs_blacklist() == 1;
    }

    /**
     * 加入黑名单
     */
    private void addBlackList() {
        if (isInBlackList()) {
            add(RRetrofit.create(ContactService.class)
                    .cancelBlackList(Param.buildMap("to_uid:" + mUserInfoBean.getUid()))
                    .compose(Rx.transformer(String.class))
                    .subscribe(new BaseSingleSubscriber<String>() {

                        @Override
                        public void onSucceed(String bean) {
//                            T_.show(bean);
                            mUserInfoBean.setIs_blacklist(0);
                        }

                        @Override
                        public void onEnd() {
                            super.onEnd();
                            mExBaseAdapter.notifyItemChanged(5);
                        }
                    }));
        } else {
            add(RRetrofit.create(ContactService.class)
                    .addBlackList(Param.buildMap("to_uid:" + mUserInfoBean.getUid()))
                    .compose(Rx.transformer(String.class))
                    .subscribe(new BaseSingleSubscriber<String>() {

                        @Override
                        public void onSucceed(String bean) {
//                            T_.show(bean);
                            mUserInfoBean.setIs_blacklist(1);
                        }

                        @Override
                        public void onEnd() {
                            super.onEnd();
                            mExBaseAdapter.notifyItemChanged(5);
                        }
                    }));
        }
    }

    /**
     * 不让TA看我的动态
     */
    private void setLook_my_discuss() {
        int value = mUserInfoBean.getLook_my_discuss();
        value = value == 1 ? 0 : 1;

        final int val = value;
        add(RRetrofit.create(SettingService.class)
                .personal(Param.buildMap("to_uid:" + mUserInfoBean.getUid(), "key:1001", "val:" + val))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        mUserInfoBean.setLook_my_discuss(val);
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        mExBaseAdapter.notifyItemChanged(3);
                    }
                }));
    }

    /**
     * 不看TA的动态
     */
    private void setLook_his_discuss() {
        int value = mUserInfoBean.getLook_his_discuss();
        value = value == 1 ? 0 : 1;

        final int val = value;
        add(RRetrofit.create(SettingService.class)
                .personal(Param.buildMap("to_uid:" + mUserInfoBean.getUid(), "key:1002", "val:" + val))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        mUserInfoBean.setLook_his_discuss(val);
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        mExBaseAdapter.notifyItemChanged(4);
                    }
                }));
    }

    /**
     * 设置星标好友
     */
    private void setStar() {
        if (isSetStar()) {
            add(RRetrofit.create(ContactService.class)
                    .cancelStar(Param.buildMap("to_uid:" + mUserInfoBean.getUid()))
                    .compose(Rx.transformer(String.class))
                    .subscribe(new BaseSingleSubscriber<String>() {

                        @Override
                        public void onSucceed(String bean) {
                            mUserInfoBean.setIs_star(0);
                        }

                        @Override
                        public void onEnd() {
                            super.onEnd();
                            mExBaseAdapter.notifyItemChanged(2);
                        }
                    }));
        } else {
            add(RRetrofit.create(ContactService.class)
                    .setStar(Param.buildMap("to_uid:" + mUserInfoBean.getUid()))
                    .compose(Rx.transformer(String.class))
                    .subscribe(new BaseSingleSubscriber<String>() {

                        @Override
                        public void onSucceed(String bean) {
                            mUserInfoBean.setIs_star(1);
                        }

                        @Override
                        public void onEnd() {
                            super.onEnd();
                            mExBaseAdapter.notifyItemChanged(2);
                        }
                    }));
        }
    }

    private boolean isSetStar() {
        return mUserInfoBean.getIs_star() == 1;
    }

    /**
     * 设置备注
     */
    private void setMark() {
        startIView(InputUIView.build(new InputUIView.InputConfigCallback() {
            @Override
            public TitleBarPattern initTitleBar(TitleBarPattern titleBarPattern) {
                return super.initTitleBar(titleBarPattern)
                        .setTitleString(mActivity, R.string.set_mark_title)
                        .addRightItem(TitleBarPattern.TitleBarItem.build(getString(R.string.finish), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String string = mExEditText.string();
                                if (TextUtils.isEmpty(string)) {
                                    Anim.band(mExEditText);
                                    return;
                                }
                                finishIView(mIView);
                                add(RRetrofit.create(ContactService.class)
                                        .setMark(Param.buildMap("to_uid:" + mUserInfoBean.getUid(),
                                                "mark:" + string))
                                        .compose(Rx.transformer(String.class))
                                        .subscribe(new BaseSingleSubscriber<String>() {

                                            @Override
                                            public void onSucceed(String bean) {
                                                mUserInfoBean.setContact_remark(string);
                                                T_.show(bean);
                                                mExBaseAdapter.notifyItemChanged(0);
                                            }
                                        }));
                            }
                        }));
            }

            @Override
            public void initInputView(RBaseViewHolder holder, ExEditText editText, ItemRecyclerUIView.ViewItemInfo bean) {
                super.initInputView(holder, editText, bean);
                TextView tipView = holder.v(R.id.input_tip_view);
                tipView.setVisibility(View.VISIBLE);
                tipView.setText(R.string.set_mark_tip);

                String mark = mUserInfoBean.getContact_remark();
                if (!TextUtils.isEmpty(mark)) {
                    setInputText(mark);
//                    mExEditText.setSelection(2, mark.length() + 2);
//                    mExEditText.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            showSoftInput(mExEditText);
//                        }
//                    }, DEFAULT_ANIM_TIME);
                }
            }
        }));
    }
}
