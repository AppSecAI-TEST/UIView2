package com.hn.d.valley.main.message.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderVideo;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/01 16:23
 * 修改人员：hewking
 * 修改时间：2017/04/01 16:23
 * 修改备注：
 * Version: 1.0.0
 */
public class ChatAdapter2 extends BaseMultiAdapter<RBaseViewHolder> {

    //viewtype
    private Map<Class<? extends MsgViewHolderBase>,Integer> vh2type;

    //value
    private Map<String, Float> progresses; // 有文件传输，需要显示进度条的消息ID map
    private String messageId;

    //ui status
    private boolean mUIViewHasOnShow;

    // ui observable
    private MsgUIObservable mMsgUIObservable;

    //listener
    private ViewHolderEventListener eventListener;


    public ChatAdapter2(RecyclerView recyclerView, List<IMMessage> data,RBaseViewHolder viewHolder, UIBaseView uIBaseView){
        super(recyclerView, data,viewHolder,uIBaseView);

        mMsgUIObservable = new MsgUIObservable();

        progresses = new HashMap<>();
        timedItems = new HashSet<>();

        vh2type = new HashMap<>();

        List<Class<? extends MsgViewHolderBase>> holders =  MsgViewHolderFactory.getAllViewHolder();

        int viewType = 0;
        for (Class<? extends MsgViewHolderBase> holder : holders) {
            addItemType(viewType, R.layout.item_chat_msg_base_layout, holder);
            vh2type.put(holder,viewType ++);
        }
    }

    public void setUuid(String messageId) {
        this.messageId = messageId;
    }

    public void deleteItem(IMMessage message, boolean isRelocateTime) {
        if (message == null) {
            return;
        }

        int index = 0;
        for (IMMessage item : getAllDatas()) {
            if (item.isTheSame(message)) {
                break;
            }
            ++index;
        }

        if (index < getDataSize()) {
            remove(index);
            if (isRelocateTime) {
                relocateShowTimeItemAfterDelete(message, index);
            }
            notifyDataSetChanged(); // 可以不要！！！
        }
    }

    public void setEventListener(ViewHolderEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public ViewHolderEventListener getEventListener() {
        return eventListener;
    }

    public void onViewShow() {
        mUIViewHasOnShow = true;
        mMsgUIObservable.notifyOnViewShow();
    }

    public void registerUIObserver(MsgUIObserver observer) {
        mMsgUIObservable.registerObserver(observer);
    }

    public void unregisterUIObserver(MsgUIObserver observer) {
        mMsgUIObservable.unregisterObserver(observer);
    }

    public interface ViewHolderEventListener {
        // 长按事件响应处理
        boolean onViewHolderLongClick(View clickView, View viewHolderView, IMMessage item);

        // 发送失败或者多媒体文件下载失败指示按钮点击响应处理
        void onFailedBtnClick(IMMessage resendMessage);
    }


    /**
     * *********************** 时间显示处理 ***********************
     */

    private Set<String> timedItems; // 需要显示消息时间的消息ID
    private IMMessage lastShowTimeItem; // 用于消息时间显示,判断和上条消息间的时间间隔

    public boolean needShowTime(IMMessage message) {
        return timedItems.contains(message.getUuid());
    }

    /**
     * 列表加入新消息时，更新时间显示
     */
    public void updateShowTimeItem(List<IMMessage> items, boolean fromStart, boolean update) {
        IMMessage anchor = fromStart ? null : lastShowTimeItem;
        for (IMMessage message : items) {
            if (setShowTimeFlag(message, anchor)) {
                anchor = message;
            }
        }

        if (update) {
            lastShowTimeItem = anchor;
        }
    }

    public String getUuid() {
        return messageId;
    }


    /**
     * 是否显示时间item
     */
    private boolean setShowTimeFlag(IMMessage message, IMMessage anchor) {
        boolean update = false;

        if (hideTimeAlways(message)) {
            setShowTime(message, false);
        } else {
            if (anchor == null) {
                setShowTime(message, true);
                update = true;
            } else {
                long time = anchor.getTime();
                long now = message.getTime();

                if (now - time == 0) {
                    // 消息撤回时使用
                    setShowTime(message, true);
                    lastShowTimeItem = message;
                    update = true;
                } else if (now - time < (long) (5 * 60 * 1000)) {
                    setShowTime(message, false);
                } else {
                    setShowTime(message, true);
                    update = true;
                }
            }
        }

        return update;
    }

    private void setShowTime(IMMessage message, boolean show) {
        if (show) {
            timedItems.add(message.getUuid());
        } else {
            timedItems.remove(message.getUuid());
        }
    }

    private void relocateShowTimeItemAfterDelete(IMMessage messageItem, int index) {
        // 如果被删的项显示了时间，需要继承
        if (needShowTime(messageItem)) {
            setShowTime(messageItem, false);
            if (getDataSize() > 0) {
                IMMessage nextItem;
                if (index == getDataSize()) {
                    //删除的是最后一项
                    nextItem = getItem(index - 1);
                } else {
                    //删除的不是最后一项
                    nextItem = getItem(index);
                }

                // 增加其他不需要显示时间的消息类型判断
                if (hideTimeAlways(nextItem)) {
                    setShowTime(nextItem, false);
                    if (lastShowTimeItem != null && lastShowTimeItem != null
                            && lastShowTimeItem.isTheSame(messageItem)) {
                        lastShowTimeItem = null;
                        for (int i = getDataSize() - 1; i >= 0; i--) {
                            IMMessage item = getItem(i);
                            if (needShowTime(item)) {
                                lastShowTimeItem = item;
                                break;
                            }
                        }
                    }
                } else {
                    setShowTime(nextItem, true);
                    if (lastShowTimeItem == null
                            || (lastShowTimeItem != null && lastShowTimeItem.isTheSame(messageItem))) {
                        lastShowTimeItem = nextItem;
                    }
                }
            } else {
                lastShowTimeItem = null;
            }
        }
    }

    private boolean hideTimeAlways(IMMessage message) {
        if (message.getSessionType() == SessionTypeEnum.ChatRoom) {
            return true;
        }
        switch (message.getMsgType()) {
            case notification:
                return true;
            default:
                return false;
        }
    }

    public float getProgress(IMMessage message) {
        Float progress = progresses.get(message.getUuid());
        return progress == null ? 0 : progress;
    }

    public void putProgress(IMMessage message, float progress) {
        progresses.put(message.getUuid(), progress);
    }


    @Override
    protected int getViewType(IMMessage message) {
        return vh2type.get(MsgViewHolderFactory.getViewHolderByType(message));
    }


    @Override
    protected String getItemKey(IMMessage item) {
        return item.getUuid();
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public void onViewHide() {
        mUIViewHasOnShow = false;
        mMsgUIObservable.notifyOnViewHide();
    }

    public boolean hasOnShow() {
        return mUIViewHasOnShow;
    }



}
