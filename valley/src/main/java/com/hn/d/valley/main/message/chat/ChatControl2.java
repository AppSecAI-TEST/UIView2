package com.hn.d.valley.main.message.chat;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.github.utilcode.utils.ClipboardUtils;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.utils.NetworkUtil;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.event.LastMessageEvent;
import com.hn.d.valley.helper.MessageHelper;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.message.audio.MessageAudioControl;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew;
import com.hn.d.valley.utils.RBus;
import com.hn.d.valley.widget.HnRefreshLayout;
import com.lzy.imagepicker.bean.ImageItem;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.AttachmentProgress;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action3;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/29 11:31
 * 修改人员：Robi
 * 修改时间：2016/12/29 11:31
 * 修改备注：
 * Version: 1.0.0
 */
public class ChatControl2 {

    public RRecyclerView mRecyclerView;
    HnRefreshLayout mRefreshLayout;
    UIBaseView mUIBaseView;

    public RBaseViewHolder mViewHolder;

    String mSessionId = "";

    Activity mActivity;

    private List<IMMessage> items;

    public ChatAdapter2 mChatAdapter;

    // 待转发消息
    private IMMessage forwardMessage;

    public ChatControl2(Activity context, RBaseViewHolder viewHolder, UIBaseView uiBaseView) {
        mActivity = context;
        mViewHolder = viewHolder;
        mUIBaseView = uiBaseView;
        mRecyclerView = mViewHolder.v(R.id.recycler_view);
        mRefreshLayout = mViewHolder.v(R.id.refresh_layout);
//        mChatAdapter = new ChatAdapter2(context, null,viewHolder,uiBaseView);
        items = new ArrayList<>();
        mChatAdapter = new ChatAdapter2(mRecyclerView, items, mViewHolder, mUIBaseView);
        mChatAdapter.setEventListener(new MsgItemEventListener());
        mRecyclerView.setItemAnim(false);
        mRecyclerView.setAdapter(mChatAdapter);

    }


    public void onLoad(String sessionId) {
        this.mSessionId = sessionId;
        onUnload();
        registerObservers(true);
    }

    public void onUnload() {
        registerObservers(false);
        MessageAudioControl.getInstance(mActivity).stopAudio();
    }


    public void resetData(List<IMMessage> messages) {
        mChatAdapter.resetData(messages);
        scrollToEnd();
    }

    public void addData(IMMessage message) {
        mChatAdapter.addLastItem(message);
        scrollToEnd();
    }

    public void scrollToEnd() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);
        } else {
            mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
        }
    }

    // 删除消息
    private void deleteItem(IMMessage messageItem, boolean isRelocateTime) {
        NIMClient.getService(MsgService.class).deleteChattingHistory(messageItem);
        List<IMMessage> messages = new ArrayList<>();
        for (IMMessage message : items) {
            if (message.getUuid().equals(messageItem.getUuid())) {
                continue;
            }
            messages.add(message);
        }
        updateReceipt(messages);
        mChatAdapter.deleteItem(messageItem, isRelocateTime);
    }

    public void updateReceipt(final List<IMMessage> messages) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (receiveReceiptCheck(messages.get(i))) {
                mChatAdapter.setUuid(messages.get(i).getUuid());
                break;
            }
        }
    }

    private boolean receiveReceiptCheck(final IMMessage msg) {
        if (msg != null && msg.getSessionType() == SessionTypeEnum.P2P
                && msg.getDirect() == MsgDirectionEnum.Out
                && msg.getMsgType() != MsgTypeEnum.tip
                && msg.getMsgType() != MsgTypeEnum.notification
                && msg.isRemoteRead()) {
            return true;
        }

        return false;
    }

    private class MsgItemEventListener implements ChatAdapter2.ViewHolderEventListener {

        @Override
        public void onFailedBtnClick(IMMessage message) {
            if (message.getDirect() == MsgDirectionEnum.Out) {
                // 发出的消息，如果是发送失败，直接重发，否则有可能是漫游到的多媒体消息，但文件下载
                if (message.getStatus() == MsgStatusEnum.fail) {
                    resendMessage(message); // 重发
                } else {
                    if (message.getAttachment() instanceof FileAttachment) {
                        FileAttachment attachment = (FileAttachment) message.getAttachment();
                        if (TextUtils.isEmpty(attachment.getPath())
                                && TextUtils.isEmpty(attachment.getThumbPath())) {
//                            showReDownloadConfirmDlg(message);
                        }
                    } else {
                        resendMessage(message);
                    }
                }
            } else {
//                showReDownloadConfirmDlg(message);
            }
        }

        @Override
        public boolean onViewHolderLongClick(View clickView, View viewHolderView, IMMessage item) {
//            if (container.proxy.isLongClickEnabled()) {
            showLongClickAction(item);
//            }
            return true;
        }
    }

    // 长按消息Item后弹出菜单控制
    private void showLongClickAction(IMMessage selectedItem) {
        onNormalLongClick(selectedItem);
    }

    private void onNormalLongClick(IMMessage item) {
        UIItemDialog itemDialog = UIItemDialog.build();
        prepareDialogItems(item, itemDialog);
        itemDialog.showDialog(mUIBaseView.getILayout());

    }

    // 长按消息item的菜单项准备。如果消息item的MsgViewHolder处理长按事件(MsgViewHolderBase#onItemLongClick),且返回为true，
    // 则对应项的长按事件不会调用到此处
    private void prepareDialogItems(final IMMessage selectedItem, UIItemDialog itemDialog) {
        MsgTypeEnum msgType = selectedItem.getMsgType();

        MessageAudioControl.getInstance(mActivity).stopAudio();

        // 0 EarPhoneMode
        longClickItemEarPhoneMode(itemDialog, msgType);
        // 1 resend
        longClickItemResend(selectedItem, itemDialog);
        // 2 copy
        longClickItemCopy(selectedItem, itemDialog, msgType);
        // 3 revoke
        if (selectedItem.getDirect() == MsgDirectionEnum.Out && selectedItem.getStatus() == MsgStatusEnum.success ) {
            longClickRevokeMsg(selectedItem, itemDialog);
        }
        // 4 delete
        longClickItemDelete(selectedItem, itemDialog);
        // 5 trans
//        longClickItemVoidToText(selectedItem, itemDialog, msgType);

        // 6 forward
        longClickItemForward(selectedItem, SessionTypeEnum.P2P, itemDialog);
        longClickItemForward(selectedItem, SessionTypeEnum.Team, itemDialog);
    }

    // 长按菜单项 -- 转发到个人
    private void longClickItemForward(final IMMessage item, final SessionTypeEnum sessionType, UIItemDialog itemDialog) {
        String itemTitle = SessionTypeEnum.P2P == sessionType ? mActivity.getString(R.string.forward_to_person) : mActivity.getString(R.string.forward_to_team);
        itemDialog.addItem(itemTitle, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactSelectUIVIew.start(mUIBaseView.getILayout(),new BaseContactSelectUIVIew.Options(RModelAdapter.MODEL_SINGLE)
                        ,null,new Action3< UIBaseRxView, List<AbsContactItem>, com.hn.d.valley.main.message.groupchat.RequestCallback>() {
                            @Override
                            public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, com.hn.d.valley.main.message.groupchat.RequestCallback requestCallback) {
                                forwardMessage = item;
                                requestCallback.onSuccess("");
                                ContactItem contactItem = (ContactItem) absContactItems.get(0);
                                FriendBean friendBean = contactItem.getFriendBean();

                                IMMessage message = MessageBuilder.createForwardMessage(forwardMessage, friendBean.getUid(), sessionType);
                                if (message == null) {
                                    T_.show("该类型不支持转发");
                                    return;
                                }
                                NIMClient.getService(MsgService.class).sendMessage(message, false);
                                if (mSessionId.equals(mSessionId)) {
                                    onMsgSend(message);
                                }
                            }
                        });
            }
        });
    }

    // 长按菜单项--删除
    private void longClickItemDelete(final IMMessage selectedItem, UIItemDialog itemDialog) {
        itemDialog.addItem(mActivity.getString(R.string.delete_has_blank), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(selectedItem, true);
            }
        });
    }

    // 长按菜单项 -- 撤回消息
    private void longClickRevokeMsg(final IMMessage item, UIItemDialog itemDialog) {
        itemDialog.addItem(mActivity.getString(R.string.withdrawn_msg), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetAvailable(mActivity)) {
                    T_.show(mActivity.getString(R.string.no_network));
                    return;
                }
                NIMClient.getService(MsgService.class).revokeMessage(item).setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        deleteItem(item, false);
                        MessageHelper.getInstance().onRevokeMessage(item);
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == ResponseCode.RES_OVERDUE) {
                            T_.show(mActivity.getString(R.string.revoke_failed));
                        } else {
                            T_.show("revoke msg failed, code:" + code);
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
            }
        });
    }

    // 长按菜单项--复制
    private void longClickItemCopy(final IMMessage item, UIItemDialog itemDialog, MsgTypeEnum msgType) {
        if (msgType != MsgTypeEnum.text) {
            return;
        }
        itemDialog.addItem(mActivity.getString(R.string.copy_has_blank), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCopyMessageItem(item);

            }
        });
    }

    private void onCopyMessageItem(IMMessage item) {
        ClipboardUtils.clipboardCopyText(mActivity,item.getContent());
    }

    // 长按菜单项--重发
    private void longClickItemResend(final IMMessage item, UIItemDialog itemDialog) {
        if (item.getStatus() != MsgStatusEnum.fail) {
            return;
        }
        itemDialog.addItem(mActivity.getString(R.string.repeat_send_has_blank), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResendMessageItem(item);
            }
        });
    }

    private void onResendMessageItem(IMMessage message) {
        int index = getItemIndex(message.getUuid());
        if (index >= 0) {
            resendMessage(message); // 重发确认
        }
    }


    // 长按菜单项 -- 听筒扬声器切换
    private void longClickItemEarPhoneMode(UIItemDialog itemDialog, MsgTypeEnum msgType) {
        if (msgType != MsgTypeEnum.audio) return;

//        String content = UserPreferences.isEarPhoneModeEnable() ? "切换成扬声器播放" : "切换成听筒播放";
//        final String finalContent = content;
//        itemDialog.addItem(content, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setEarPhoneMode(!UserPreferences.isEarPhoneModeEnable());
//            }
//        });
    }

    // 重发消息到服务器
    private void resendMessage(IMMessage message) {
        // 重置状态为unsent
        int index = getItemIndex(message.getUuid());
        if (index >= 0 && index < items.size()) {
            IMMessage item = items.get(index);
            item.setStatus(MsgStatusEnum.sending);
            deleteItem(item, true);
            onMsgSend(item);
        }

        NIMClient.getService(MsgService.class).sendMessage(message, true);
    }

    // 发送消息后，更新本地消息列表
    public void onMsgSend(IMMessage message) {
        List<IMMessage> addedListItems = new ArrayList<>(1);
        addedListItems.add(message);
        mChatAdapter.updateShowTimeItem(addedListItems, false, true);

        mChatAdapter.addLastItem(message);
        scrollToEnd();
    }


    /**
     * ************************* 观察者 ********************************
     */

    private void registerObservers(boolean register) {

        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeMsgStatus(mMessageObserver, register);
        service.observeAttachmentProgress(attachmentProgressObserver, register);
        service.observeRevokeMessage(revokeMessageObserver, register);
        service.observeReceiveMessage(incomingMessageObserver, register);


        if (register) {
//            registerUserInfoObserver();
        } else {
//            unregisterUserInfoObserver();
        }

//        MessageListPanelHelper.getInstance().registerObserver(incomingLocalMessageObserver, register);
    }

    /**
     * 消息撤回观察者
     */
    Observer<IMMessage> revokeMessageObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            if (message == null || !mSessionId.equals(message.getSessionId())) {
                return;
            }

            deleteItem(message, false);
        }
    };

    /**
     * 刷新单条消息
     */
    private void refreshViewHolderByIndex(final int index) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (index < 0) {
                    return;
                }
                mChatAdapter.notifyItemChanged(index);
            }
        });
    }

    private void onAttachmentProgressChange(AttachmentProgress progress) {
        int index = getItemIndex(progress.getUuid());
        if (index >= 0 && index < items.size()) {
            IMMessage item = items.get(index);
            float value = (float) progress.getTransferred() / (float) progress.getTotal();
            mChatAdapter.putProgress(item, value);
            refreshViewHolderByIndex(index);
        }
    }

    private int getItemIndex(String uuid) {
        for (int i = 0; i < items.size(); i++) {
            IMMessage message = items.get(i);
            if (TextUtils.equals(message.getUuid(), uuid)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 消息附件上传/下载进度观察者
     */
    Observer<AttachmentProgress> attachmentProgressObserver = new Observer<AttachmentProgress>() {
        @Override
        public void onEvent(AttachmentProgress progress) {
            onAttachmentProgressChange(progress);
        }
    };

    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
            for (IMMessage message : messages) {
                if (TextUtils.equals(mSessionId, message.getSessionId())) {
                    mChatAdapter.appendData(messages);
                    mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount());
                } else {
                    RBus.post(new LastMessageEvent(message));
                }
            }
        }
    };
    Observer<IMMessage> mMessageObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage imMessage) {
            //消息状态发生了改变
            List<IMMessage> allDatas = mChatAdapter.getAllDatas();
            for (int i = 0; i < allDatas.size(); i++) {
                if (allDatas.get(i).isTheSame(imMessage)) {
                    mChatAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    };


    public static class Images {
        public int positon;
        public ArrayList<ImageItem> images;

    }


}
