package com.hn.d.valley.main.message.p2pchat;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.event.EmptyChatEvent;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.SessionSettingDelegate;
import com.hn.d.valley.main.message.chatfile.ChatFileUIView;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter;
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.GroupReportUIView;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.message.groupchat.TeamCreateHelper;
import com.hn.d.valley.main.message.search.ChatRecordSearchUIView;
import com.hn.d.valley.main.message.search.DefaultUserInfoProvider;
import com.hn.d.valley.main.message.search.GlobalSearchUIView2;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.utils.RBus;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import java.util.List;

import rx.functions.Action3;

/**
 * Created by hewking on 2017/3/26.
 */

public class P2PInfoUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    static final String KEY_SESSION_ID = "key_account";
    static final String KEY_SESSION_TYPE = "key_sessiontype";

    private String mSessionId;
    private SessionTypeEnum sessionType;

    private UserInfoProvider.UserInfo mUserInfo;

    public static void start(ILayout mLayout, String sessionId, SessionTypeEnum sessionType) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SESSION_ID, sessionId);
        bundle.putInt(KEY_SESSION_TYPE, sessionType.getValue());
        mLayout.startIView(new P2PInfoUIView(), new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_chat_info));
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        Bundle bundle = param.mBundle;
        if (bundle != null) {
            mSessionId = bundle.getString(KEY_SESSION_ID);
            sessionType = SessionTypeEnum.typeOfValue(bundle.getInt(KEY_SESSION_TYPE));
            mUserInfo = DefaultUserInfoProvider.getInstance().getUserInfo(mSessionId);
        }
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);

    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_message_p2p_chatinfo;
        }

        if (viewType == 1 || viewType == 2) {
            return R.layout.item_switch_view;
        }
        return R.layout.item_info_layout;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        final int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
        final int left = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindMemberInfo(holder);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                itemInfoLayout.setItemText(mActivity.getString(R.string.text_stick_chat));
                switchCompat.setChecked(SessionSettingDelegate.getInstance().checkTop(mSessionId));
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            SessionSettingDelegate.getInstance().setTop(mSessionId,sessionType,1);

                        } else {
                            SessionSettingDelegate.getInstance().setTop(mSessionId,sessionType,0);
                        }

                        RBus.post(Constant.TAG_UPDATE_RECENT_CONTACTS, new UpdateDataEvent());

                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                final SwitchCompat switchCompat = holder.v(R.id.switch_view);
                itemInfoLayout.setItemText(mActivity.getString(R.string.text_messge_notallow));
                boolean notice = NIMClient.getService(FriendService.class).isNeedMessageNotify(mSessionId);
                switchCompat.setChecked(notice);
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SessionSettingDelegate.getInstance().setMessageNotify(mSessionId,isChecked,switchCompat);
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
                        mOtherILayout.startIView(new ChatFileUIView(mSessionId,sessionType));
                    }
                });

            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_find_chat_record));
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatRecordSearchUIView.start(mOtherILayout, GlobalSearchUIView2.Options.sOptions,mSessionId,sessionType,new int[]{ItemTypes.MSG});
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
                                .setDialogContent("确定清空吗?")
                                .setOkText("确定")
                                .setCancelText("取消")
                                .setOkListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        NIMClient.getService(MsgService.class).clearChattingHistory(mSessionId, sessionType);
                                        RBus.post(new EmptyChatEvent(mSessionId));
                                    }
                                })
                                .showDialog(mOtherILayout);
                    }
                });

            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_report));

                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new GroupReportUIView());
                    }
                });

            }
        }));


    }

    private void bindMemberInfo(RBaseViewHolder holder) {
        TextView tv = holder.tv(R.id.tv_username);
        HnGlideImageView imageView = holder.v(R.id.image_view);
        TextView tv_add_group = holder.tv(R.id.tv_add_group);
        ImageView iv_user_detail = holder.imgV(R.id.iv_user_detail);

        tv.setText(mUserInfo.getName());
        imageView.setImageUrl(mUserInfo.getAvatar());
        iv_user_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mILayout.startIView(new UserDetailUIView2(mUserInfo.getAccount()));
            }
        });
        tv_add_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactSelectUIVIew targetView = new ContactSelectUIVIew(new BaseContactSelectAdapter.Options());
                targetView.setSelectAction(new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                    @Override
                    public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, RequestCallback requestCallback) {
                        TeamCreateHelper.createAndSavePhoto(uiBaseDataView, absContactItems, requestCallback);
                    }
                });
                mOtherILayout.startIView(targetView);
            }
        });
    }

}
