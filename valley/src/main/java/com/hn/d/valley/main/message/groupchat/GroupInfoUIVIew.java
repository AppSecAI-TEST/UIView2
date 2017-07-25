package com.hn.d.valley.main.message.groupchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupDescBean;
import com.hn.d.valley.bean.event.EmptyChatEvent;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.cache.SimpleCallback;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.main.me.setting.MsgNotifySetting;
import com.hn.d.valley.main.message.SessionSettingDelegate;
import com.hn.d.valley.main.message.chatfile.ChatFileUIView;
import com.hn.d.valley.main.message.search.ChatRecordSearchUIView;
import com.hn.d.valley.main.message.search.GlobalSearchUIView2;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.sub.other.InputUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.user.ReportUIView;
import com.hn.d.valley.utils.RBus;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.List;

import rx.functions.Action1;
import rx.functions.Action3;

/**
 * Created by hewking on 2017/3/10.
 */
public class GroupInfoUIVIew extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    protected static final String KEY_SESSION_ID = "key_account";
    protected static final String KEY_SESSION_TYPE = "key_sessiontype";

    private String mSessionId;
    private SessionTypeEnum sessionType;

    private GroupDescBean mGroupDescBean;

    // state
    private boolean isSelfAdmin = false;
    private boolean isSelfManager = false;

    //listener
    private GroupInfoUpdatelistener infoUpdatelistener;

    public GroupInfoUIVIew(GroupInfoUpdatelistener listener) {
        infoUpdatelistener = listener;
    }

    public static void start(ILayout mLayout, String sessionId, SessionTypeEnum sessionType, GroupInfoUpdatelistener listener) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SESSION_ID, sessionId);
        bundle.putInt(KEY_SESSION_TYPE, sessionType.getValue());
        mLayout.startIView(new GroupInfoUIVIew(listener), new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(getString(R.string.text_chat_info));
    }

    /**
     * 初始化群组基本信息
     */
    private void loadTeamInfo() {
        Team t = TeamDataCache.getInstance().getTeamById(mSessionId);
        if (t != null) {
            updateTeamInfo(t);
        } else {
            TeamDataCache.getInstance().fetchTeamById(mSessionId, new SimpleCallback<Team>() {
                @Override
                public void onResult(boolean success, Team result) {
                    if (success && result != null) {
                        updateTeamInfo(result);
                    } else {
//                        onGetTeamInfoFailed();
                    }
                }
            });
        }
    }

    /**
     * 更新群信息
     *
     * @param t
     */
    private void updateTeamInfo(final Team t) {
        if (t == null) {
            finishIView();
            return;
        } else {
            if (t.getCreator().equals(UserCache.getUserAccount())) {
                isSelfAdmin = true;
//                mRExBaseAdapter.notifyDataSetChanged();
            }

        }
    }

    /**
     * *************************** 加载&变更数据源 ********************************
     */
    private void requestMembers() {
        TeamDataCache.getInstance().fetchTeamMemberList(mSessionId, new SimpleCallback<List<TeamMember>>() {
            @Override
            public void onResult(boolean success, List<TeamMember> members) {
                if (success && members != null && !members.isEmpty()) {
                    updateTeamMember(members);
                }
            }
        });
    }

    /**
     * 更新群成员信息
     *
     * @param m
     */
    private void updateTeamMember(final List<TeamMember> m) {
        if (m != null && m.isEmpty()) {
            return;
        }
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
//        loadGroupInfo();
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        Bundle bundle = param.mBundle;
        if (bundle != null) {
            mSessionId = bundle.getString(KEY_SESSION_ID);
            sessionType = SessionTypeEnum.typeOfValue(bundle.getInt(KEY_SESSION_TYPE));
        }

        loadTeamInfo();

    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadGroupInfo();
    }

    private void loadGroupInfo() {
        add(RRetrofit.create(GroupChatService.class)
                .groupInfo(Param.buildMap("uid:" + UserCache.getUserAccount(), "yx_gid:" + mSessionId))
                .compose(Rx.transformer(GroupDescBean.class))
                .subscribe(new BaseSingleSubscriber<GroupDescBean>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        hideLoadView();
                        if (isError) {
                            showNonetLayout(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadData();
                                }
                            });

                        }
                    }

                    @Override
                    public void onSucceed(GroupDescBean bean) {
                        if (bean == null) {
                            showEmptyLayout();
                        } else {
                            mGroupDescBean = bean;
                            showContentLayout();
                            GroupMemberModel.getInstanse().loadData(bean);
                        }
                    }
                }));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_message_group_chatinfo;
        }

        if (mRExBaseAdapter.isLast(viewType)) {
            return R.layout.item_button_view;
        }

        if (viewType == 4 || viewType == 5) {
            return R.layout.item_switch_view;
        }
        return R.layout.item_info_layout;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        final int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
        final int left = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindGroupMemberInfo(holder);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindGroupName(holder);
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_group_qrcode));
                infoLayout.setDarkDrawableRes(R.drawable.qr_code);

                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        mParentILayout.startIView(new GroupQrCodeUIView());
                        GroupQrCodeUIView.start(mParentILayout, mSessionId, mGroupDescBean);
                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_group_announcement));
                infoLayout.setItemDarkText(mGroupDescBean.getAnnouncement() + "");

                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(GroupMemberUIVIew.GID, mGroupDescBean.getGid());
                        bundle.putBoolean(GroupMemberUIVIew.IS_ADMIN, isSelfAdmin);
                        UIParam param = new UIParam().setBundle(bundle);
                        mParentILayout.startIView(new GroupAnnouncementUIView(), param);
                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                CompoundButton switchCompat = holder.v(R.id.switch_view);
                itemInfoLayout.setItemText(mActivity.getString(R.string.text_top_chat));
                switchCompat.setChecked(SessionSettingDelegate.getInstance().checkTop(mSessionId));
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            SessionSettingDelegate.getInstance().setTop(mSessionId, sessionType, 1);
                        } else {
                            SessionSettingDelegate.getInstance().setTop(mSessionId, sessionType, 0);
                        }
                        RBus.post(Constant.TAG_UPDATE_RECENT_CONTACTS, new UpdateDataEvent());
                        //callback
                        infoUpdatelistener.onGroupTop();
                    }
                });

            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                final CompoundButton switchCompat = holder.v(R.id.switch_view);
                itemInfoLayout.setItemText(mActivity.getString(R.string.text_message_notity_no));
                Team t = TeamDataCache.getInstance().getTeamById(mSessionId);
                if (t != null) {
                    switchCompat.setChecked(t.mute());
                }
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
//                      SessionSettingDelegate.getInstance().setMessageNotify(mSessionId,isChecked,switchCompat);
                        NIMClient.getService(TeamService.class).muteTeam(mSessionId, isChecked).setCallback(new com.netease.nimlib.sdk.RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                infoUpdatelistener.onGroupNotifySetting();
                                MsgNotifySetting.instance().enableGroupMsgNotify(isChecked);
                            }

                            @Override
                            public void onFailed(int code) {

                            }

                            @Override
                            public void onException(Throwable exception) {

                            }
                        });
                    }
                });
            }
        }));


        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                final ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_me_in_group_nickname));
                infoLayout.setItemDarkText(mGroupDescBean.getNick());
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startInputView(infoLayout, mGroupDescBean.getNick(), new Action1<String>() {
                            @Override
                            public void call(String s) {
                                editNickName(UserCache.getUserAccount(), s);
                            }
                        });
                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_chat_file));
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentILayout.startIView(new ChatFileUIView(mSessionId, sessionType));
                    }
                });

            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_search_chat_record));
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatRecordSearchUIView.start(mParentILayout, GlobalSearchUIView2.Options.sOptions, mSessionId, sessionType, new int[]{ItemTypes.MSG});
                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_empty_record));
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIDialog.build()
                                .setDialogContent(mActivity.getString(R.string.text_sure_empty))
                                .setOkText(mActivity.getString(R.string.ok))
                                .setCancelText(mActivity.getString(R.string.cancel))
                                .setOkListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        NIMClient.getService(MsgService.class).clearChattingHistory(mSessionId, sessionType);
                                        RBus.post(new EmptyChatEvent(mSessionId));
                                    }
                                })
                                .showDialog(mParentILayout);
                    }
                });

            }
        }));

//        if (!isSelfAdmin) {
            items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                    infoLayout.setItemText(mActivity.getString(R.string.text_report));
                    infoLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mParentILayout.startIView(new ReportUIView(mGroupDescBean));
                        }
                    });
                }
            }));
//        }
        bindGroupOwnerFunc(items, line, left);
        jugeGroupOwner(items, line, left);
    }

    private void jugeGroupOwner(List<ViewItemInfo> items, int line, int left) {
        items.add(ViewItemInfo.build(new ItemOffsetCallback(3 * left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView textView = holder.v(R.id.text_view);
                textView.setBackground(SkinHelper.getSkin().getThemeMaskBackgroundSelector());
                if (isSelfAdmin) {
                    textView.setText(R.string.text_disolve_group);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dissolveGroup();
                        }
                    });
                } else {
                    textView.setText(R.string.text_quit_group);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            leaveGroup();
                        }
                    });
                }

            }
        }));
    }

    private void leaveGroup() {
        add(RRetrofit.create(GroupChatService.class)
                .leave(Param.buildMap("uid:" + UserCache.getUserAccount()
                        , "gid:" + mGroupDescBean.getGid()))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        finishIView();
                        T_.info(mActivity.getString(R.string.text_quit_success));
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }
                }));
    }

    private void bindGroupOwnerFunc(List<ViewItemInfo> items, final int line, final int left) {
        if (isSelfAdmin) {

            items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    final ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                    infoLayout.setItemText(mActivity.getString(R.string.text_group_update));
                    infoLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mGroupDescBean != null && mGroupDescBean.canUpgrade()) {
                                UIDialog.build()
                                        .setDialogContent(String.format(mActivity.getString(R.string.text_group_upgrade_true_tip), mGroupDescBean.getTopMemberLimit()))
                                        .setCancelText(getString(R.string.cancel))
                                        .setOkText(getString(R.string.ok))
                                        .setOkListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                upgradeGroup();
                                            }
                                        })
                                        .showDialog(mILayout);
                            } else {
                                UIDialog.build()
                                        .setOkText(mActivity.getString(R.string.text_i_known))
                                        .setCancelText("")
                                        .setDialogContent(String.format(mActivity.getString(R.string.text_group_upgrade_false_tip), mGroupDescBean.getTopMemberLimit()))
                                        .showDialog(mILayout);
                            }

                        }
                    });
                }
            }));

            items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
                @Override
                public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                    ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                    infoLayout.setItemText(mActivity.getString(R.string.text_group_change_owner));

                    final BaseContactSelectAdapter.Options option = new BaseContactSelectAdapter.Options(RModelAdapter.MODEL_SINGLE);
                    option.showDialog = true;
                    option.showCheckBox = false;
                    infoLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GroupMemberSelectUIVIew.start(mParentILayout, option
                                    , null, mGroupDescBean.getGid(), new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                                        @Override
                                        public void call(UIBaseRxView uiBaseDataView, final List<AbsContactItem> absContactItems, final RequestCallback requestCallback) {
                                            requestCallback.onStart();
                                            TeamCreateHelper.changeOwner(uiBaseDataView, absContactItems
                                                    , mGroupDescBean.getGid(), requestCallback, new Action1<Boolean>() {
                                                        @Override
                                                        public void call(Boolean aBoolean) {
                                                            if (aBoolean) {
//                                                                loadGroupInfo();
                                                                mILayout.finishIView(GroupInfoUIVIew.class);
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
                }
            }));
        }
    }

    private void upgradeGroup() {
        add(RRetrofit.create(GroupChatService.class)
                .upgrade(Param.buildMap("uid:" + UserCache.getUserAccount()
                        , "gid:" + mGroupDescBean.getGid()))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        finishIView();
                        T_.info(mActivity.getString(R.string.text_upgrade_success));
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }
                }));

    }

    private void dissolveGroup() {
        add(RRetrofit.create(GroupChatService.class)
                .dissolve(Param.buildMap("uid:" + UserCache.getUserAccount()
                        , "gid:" + mGroupDescBean.getGid()))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        finishIView();
                        infoUpdatelistener.onGropuDissolve();
                        T_.info(mActivity.getString(R.string.text_desolve_success));
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }
                }));
    }

    private void bindGroupMemberInfo(RBaseViewHolder holder) {
        GroupMemberModel.getInstanse().init(holder, mActivity, this, mSessionId);
    }

    private void bindGroupName(RBaseViewHolder holder) {
        final ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
        infoLayout.setItemText(mActivity.getString(R.string.text_group_name));
        if (mGroupDescBean == null) {
            return;
        }

        boolean isRawname = mGroupDescBean.getName().equals("");
        final String currentName = isRawname ? mGroupDescBean.getDefaultName() : mGroupDescBean.getName();
        infoLayout.setItemDarkText(currentName);
        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelfAdmin) {
                    return;
                }
                startInputView(infoLayout, currentName, new Action1<String>() {
                    @Override
                    public void call(String s) {
                        editGroupName(s);
                    }
                });
            }

        });

    }

    private void editGroupName(final String name) {
        if (mGroupDescBean == null) {
            return;
        }
        add(RRetrofit.create(GroupChatService.class)
                .editGroupName(Param.buildMap("uid:" + UserCache.getUserAccount()
                        , "gid:" + mGroupDescBean.getGid(), "name:" + name))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        infoUpdatelistener.onGroupNameChanged(name);
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }
                }));
    }

    private void editNickName(String to_uid, String name) {
        if (mGroupDescBean == null) {
            return;
        }
        add(RRetrofit.create(GroupChatService.class)
                .updateNick(Param.buildMap("to_uid:" + UserCache.getUserAccount()
                        , "gid:" + mGroupDescBean.getGid(), "nick:" + name))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }
                }));
    }

    private void startInputView(final ItemInfoLayout infoLayout, final String content, final Action1<String> action) {
        startIView(InputUIView.build(new InputUIView.InputConfigCallback() {
            @Override
            public TitleBarPattern initTitleBar(TitleBarPattern titleBarPattern) {
                return super.initTitleBar(titleBarPattern)
                        .setTitleString(mActivity.getString(R.string.modify_name_title))
                        .addRightItem(TitleBarPattern.TitleBarItem.build(mActivity.getResources().getString(R.string.save)
                                , new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (mExEditText.isEmpty()) {
                                            Anim.band(mExEditText);
                                            return;
                                        }
                                        final String name = mExEditText.string();
                                        infoLayout.setItemDarkText(name);
                                        action.call(name);
                                        finishIView(mIView);
                                    }
                                }));
            }

            @Override
            public void initInputView(RBaseViewHolder holder, ExEditText editText, ViewItemInfo bean) {
                super.initInputView(holder, editText, bean);
                editText.setMaxLength(mActivity.getResources().getInteger(R.integer.name_count));
                editText.setHint(R.string.input_name_hint);
                if (mGroupDescBean != null) {
                    setInputText(content);
                }
            }
        }));
    }

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);


    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }
}
