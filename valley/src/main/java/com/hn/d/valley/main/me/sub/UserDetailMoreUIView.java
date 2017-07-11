package com.hn.d.valley.main.me.sub;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.base.Item;
import com.angcyo.uiview.base.SingleItem;
import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseItemUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.control.ShareControl;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.message.attachment.PersonalCardAttachment;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter.Options;
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.SettingService;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.sub.other.InputUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.user.ReportUIView;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

import rx.functions.Action3;

import static com.hn.d.valley.main.message.chat.ChatUIView2.msgService;

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

    /**
     * 0	int	普通陌生人【没有拉黑情况】
     * 1	int	双方拉黑
     * 2	int	我拉对方黑
     * 3	int	对方拉我黑
     * 4	int	互为联系人【互相关注就为联系人】
     * 5	int	我关注了对方
     * 6	int	对方关注了我
     */
    private Integer relation = 0;//与用户之间的关系

    public UserDetailMoreUIView(UserInfoBean userInfoBean) {
        mUserInfoBean = userInfoBean;
    }

    public UserDetailMoreUIView(UserInfoBean userInfoBean, Integer relation) {
        mUserInfoBean = userInfoBean;
        this.relation = relation;
    }

    @Override
    protected int getTitleResource() {
        return R.string.more;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (relation == 4) {
            //互为联系人【互相关注就为联系人】
            if (viewType > 1 && viewType < 6) {
                return R.layout.item_switch_view;
            }
            if (isLast(viewType + 1)) {
                return R.layout.item_share_button_view;
            }
            if (isLast(viewType)) {
                return R.layout.item_button_view;
            }
        } else if (relation == 5 || relation == 6) {
            //我关注了对方              //对方关注了我
            if (viewType > 1 && viewType < 4) {
                return R.layout.item_switch_view;
            }
            if (isLast(viewType + 1)) {
                return R.layout.item_share_button_view;
            }
            if (isLast(viewType)) {
                return R.layout.item_button_view;
            }

        } else {
//             if (relation == 0) {
            //普通陌生人【没有拉黑情况】
            if (viewType > 1 && viewType < 3) {
                return R.layout.item_switch_view;
            }
            if (viewType == 4) {
                return R.layout.item_share_button_view;
            }
//             }
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
                                // 发送名片
                                Options option = new Options(RModelAdapter.MODEL_SINGLE);
                                ContactSelectUIVIew.start(mILayout, option
                                        , null, new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                                            @Override
                                            public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, RequestCallback requestCallback) {
                                                requestCallback.onSuccess("");
                                                ContactItem contactItem = (ContactItem) absContactItems.get(0);
                                                FriendBean friendBean = contactItem.getFriendBean();
                                                PersonalCardAttachment attachment = new PersonalCardAttachment(mUserInfoBean);
                                                IMMessage message = MessageBuilder.createCustomMessage(friendBean.getUid(), SessionTypeEnum.P2P, friendBean.getIntroduce(), attachment);
                                                msgService().sendMessage(message,false);

                                            }
                                        });

                            }
                        });
            }
        });

        if (relation == 4) {
            //互为联系人【互相关注就为联系人】
            startItem(items, SingleItem.Type.TOP_LINE);
            lookMyItem(items, SingleItem.Type.TOP_LINE);
            lookHisItem(items, SingleItem.Type.TOP_LINE);

            blackListItem(items, SingleItem.Type.TOP);
        } else if (relation == 5) {
            //我关注了对方
            lookHisItem(items, SingleItem.Type.TOP);
            blackListItem(items, SingleItem.Type.TOP);
        } else if (relation == 6) {
            //对方关注了我
            lookMyItem(items, SingleItem.Type.TOP);
            blackListItem(items, SingleItem.Type.TOP);
        } else {
//            if (relation == 0) {
            //普通陌生人【没有拉黑情况】
            blackListItem(items, SingleItem.Type.TOP);
//            }
        }


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
                ShareControl.shareCardControl(mActivity, holder,
                        mUserInfoBean.getAvatar(),
                        mUserInfoBean.getUid(),
                        mUserInfoBean.getUsername() + "的恐龙谷主页",
                        mUserInfoBean.getUsername() + "的恐龙谷主页\n" + "粉丝数: " + mUserInfoBean.getFans_count());
            }
        });

        if (relation == 4 || relation == 5 || relation == 6) {

            items.add(new SingleItem() {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                    TextView textView = holder.tv(R.id.text_view);
                    String string = "";
                    if (relation == 4) {
                        //互为联系人【互相关注就为联系人】
                        string = getString(R.string.del_friend);

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
                    } else if (relation == 5) {
                        //我关注了对方
                        string = getString(R.string.cancel_followers);

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                add(RRetrofit.create(UserService.class)
                                        .unAttention(Param.buildMap("to_uid:" + mUserInfoBean.getUid()))
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
                    } else if (relation == 6) {
                        //对方关注了我
                        string = getString(R.string.del_fans);

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                add(RRetrofit.create(ContactService.class)
                                        .delFans(Param.buildMap("to_uid:" + mUserInfoBean.getUid()))
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
                    }
                    textView.setText(string);

                    ResUtil.setBgDrawable(textView, SkinHelper.getSkin().getThemeMaskBackgroundRoundSelector());
                    //UserDetailUIView2.initCommandView(holder.tv(R.id.text_view), mUserInfoBean, mILayout, mSubscriptions);
                }
            });

        }
    }

    private void lookHisItem(List<SingleItem> items, SingleItem.Type type) {
        //不看TA的动态
        items.add(new SingleItem(type) {
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
    }

    private void lookMyItem(List<SingleItem> items, SingleItem.Type type) {
        //不让TA看我的动态
        items.add(new SingleItem(type) {
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
    }

    private void startItem(List<SingleItem> items, SingleItem.Type type) {
        //标为星标好友
        items.add(new SingleItem(type) {
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
    }

    private void blackListItem(List<SingleItem> items, SingleItem.Type type) {
        //加入黑名单
        items.add(new SingleItem(type) {
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
                        public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                            super.onEnd(isError, isNoNetwork, e);
                            mExBaseAdapter.notifyDataSetChanged();
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
                        public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                            super.onEnd(isError, isNoNetwork, e);
                            mExBaseAdapter.notifyDataSetChanged();
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
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        mExBaseAdapter.notifyDataSetChanged();
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
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        mExBaseAdapter.notifyDataSetChanged();
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
                        public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                            super.onEnd(isError, isNoNetwork, e);
                            mExBaseAdapter.notifyDataSetChanged();
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
                        public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                            super.onEnd(isError, isNoNetwork, e);
                            mExBaseAdapter.notifyDataSetChanged();
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
                                                mExBaseAdapter.notifyDataSetChanged();
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
