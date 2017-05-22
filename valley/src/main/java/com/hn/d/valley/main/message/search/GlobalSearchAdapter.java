package com.hn.d.valley.main.message.search;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.GroupBean;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.main.message.query.RecordHitInfo;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.search.model.MsgIndexRecord;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/27 18:24
 * 修改人员：hewking
 * 修改时间：2017/03/27 18:24
 * 修改备注：
 * Version: 1.0.0
 */
public class GlobalSearchAdapter extends RBaseAdapter<AbsContactItem> {

    private static final String PREFIX = "...";

    protected int descTextViewWidth; // 当前ViewHolder desc TextView 最大的宽度px

    private ILayout mOtherLayout;

    public GlobalSearchAdapter(Context context, ILayout layout) {
        super(context);
        this.mOtherLayout = layout;
    }

    @Override
    public void resetData(List<AbsContactItem> datas) {



        super.resetData(datas);
    }

    @Override
    public int getItemType(int position) {
        return mAllDatas.get(position).getItemType();
    }


    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == ItemTypes.MSG) {
            return R.layout.item_message_chat_search;
        }

        if (viewType == ItemTypes.GROUP) {
            return R.layout.item_friends_item;
        }

        if (viewType == ItemTypes.FRIEND) {
            return R.layout.item_friends_item;
        }

        return R.layout.item_search_user_contacts;
    }

    @Override
    protected void onBindView(RBaseViewHolder holder, int position, final AbsContactItem bean) {

        if (getItemType(position) == ItemTypes.MSG) {
            bindMsg(holder, (MsgItem) bean);
        } else if (getItemType(position) == ItemTypes.GROUP) {
            bindGroup(holder, (RecordGroupItem) bean);
        } else if (getItemType(position) == ItemTypes.FRIEND) {
            bindContact(holder, (RecordContactItem) bean);
        }
    }



    private void bindContact(RBaseViewHolder holder, RecordContactItem bean) {
        RecordContactItem friendItem = bean;
        final FriendBean friendBean = friendItem.getFriendBean();
        HnGlideImageView iv_head = holder.v(R.id.iv_item_head);
        TextView tv_friend_name = holder.tv(R.id.tv_friend_name);
        iv_head.setImageUrl(friendBean.getAvatar());
//        tv_friend_name.setText(friendBean.getDefaultMark());

        display(tv_friend_name, friendBean.getDefaultMark(), friendItem.getHitInfo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionHelper.startP2PSession(mOtherLayout, friendBean.getUid(), SessionTypeEnum.P2P);
            }
        });
    }

    private void bindGroup(RBaseViewHolder holder, RecordGroupItem bean) {
        RecordGroupItem groupItem = bean;
        final GroupBean groupBean = groupItem.getBean();
        HnGlideImageView iv_head = holder.v(R.id.iv_item_head);
        TextView tv_friend_name = holder.tv(R.id.tv_friend_name);
        iv_head.setImageUrl(groupBean.getAvatar());
//        tv_friend_name.setText(groupBean.getDefaultName());

        display(tv_friend_name, groupBean.getDefaultName(), groupItem.hitInfo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionHelper.startTeamSession(mOtherLayout, groupBean.getYxGid(), SessionTypeEnum.Team);
            }
        });
    }

    private void bindMsg(RBaseViewHolder holder, MsgItem bean) {
        final MsgItem item = bean;

        HnGlideImageView iv_icon = holder.v(R.id.ico_view);
        TextView recent_name_view = holder.tv(R.id.recent_name_view);
        TextView msg_content_view = holder.tv(R.id.msg_content_view);
        TextView msg_time_view = holder.tv(R.id.msg_time_view);

        final MsgIndexRecord indexRecord = item.getIndexRecord();
        final IMMessage imMessage = indexRecord.getMessage();
        final SessionTypeEnum sessionType = imMessage.getSessionType();
        final String sessionId = imMessage.getSessionId();

        recent_name_view.setText(item.getDisplayName());
        iv_icon.setBackgroundResource(R.drawable.default_avatar);

        if (item.isQuerySession()) {
            msg_time_view.setVisibility(View.VISIBLE);
            msg_time_view.setText(TimeUtil.getTimeShowString(indexRecord.getTime(), false));
        } else {
            msg_time_view.setVisibility(View.GONE);
        }


        if (indexRecord.getSessionType() == SessionTypeEnum.P2P) {
            UserInfoProvider.UserInfo userInfo = DefaultUserInfoProvider.getInstance().getUserInfo(indexRecord.getSessionId());
            iv_icon.setImageThumbUrl(userInfo.getAvatar());
        } else {
            Team team = TeamDataCache.getInstance().getTeamById(indexRecord.getSessionId());
            iv_icon.setImageThumbUrl(team.getIcon());
        }

        List<com.netease.nimlib.sdk.search.model.RecordHitInfo> clone = indexRecord.cloneHitInfo(); // 计算高亮区域并clone

        if (indexRecord.getCount() > 1) {
            msg_content_view.setText(String.format("%d条相关聊天记录", indexRecord.getCount()));

            holder.v(R.id.item_root_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalSearchDetailUIView.start(mOtherLayout,indexRecord);
                }
            });
            return;
        }


        String text = indexRecord.getText(); // 原串

        // 异常情况，没有高亮击中的区间，直接设置原串
        if (clone == null || clone.isEmpty()) {
            msg_content_view.setText(text);
            return;
        }

        int firstIndex = clone.get(0).start; // 首个需要高亮的关键字的起始位置
        int firstHitInfoLength = clone.get(0).end - clone.get(0).start + 1; // 首个需要高亮的关键字的长度

        // 判断是否需要截取
        Object[] result = needCutText(msg_content_view, indexRecord.getText(), firstIndex, firstHitInfoLength);
        Boolean needCut = (Boolean) result[0];
        int extractPreStrNum = (Integer) result[1];
        if (needCut) {
            // 文本截取
            int newStartIndex = firstIndex - extractPreStrNum;
            text = PREFIX + text.substring(newStartIndex);

            // 矫正hitInfo
            int delta = newStartIndex - PREFIX.length();
            for (com.netease.nimlib.sdk.search.model.RecordHitInfo rh : clone) {
                rh.start -= delta;
                rh.end -= delta;
            }
        }

        msg_content_view.setText(item.getIndexRecord().getMessage().getContent());
        display(msg_content_view, text, clone);

        holder.v(R.id.item_root_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SessionTypeEnum.Team == sessionType) {
                    SessionHelper.startTeamSession(mOtherLayout, sessionId, SessionTypeEnum.Team, imMessage);
                } else if (SessionTypeEnum.P2P == sessionType) {
                    SessionHelper.startP2PSession(mOtherLayout, sessionId, SessionTypeEnum.P2P, imMessage);
                }
            }
        });
    }

    /**
     * 假如第一个索引之前的文字宽度>layout宽度，就需要把text截断
     *
     * @param text               原文本
     * @param firstIndex         首个需要高亮的关键字的起始位置
     * @param firstHitInfoLength 首个需要高亮的关键字的长度
     * @return [0]是否需要截取字符串;[1]如果要截取，那么需要提取前面几个字符
     */
    private Object[] needCutText(TextView desc, String text, int firstIndex, int firstHitInfoLength) {
        Boolean r0;
        Integer r1;

        // calculate
        View parent = (View) desc.getParent();
        if (parent.getMeasuredWidth() == 0) {
            // xml中：50dp，包括头像的长度还有左右间距大小
            parent.measure(View.MeasureSpec.makeMeasureSpec(ScreenUtil.getDisplayWidth() - ScreenUtil.dip2px(50.0f), View.MeasureSpec.EXACTLY), 0);
        }
        descTextViewWidth = (int) (parent.getMeasuredWidth() - desc.getPaint().measureText(PREFIX));

        float descLength = desc.getPaint().measureText(text.substring(0, firstIndex + firstHitInfoLength));
        float avg = descLength / (firstIndex + firstHitInfoLength); // 前firstIndex+firstHitInfoLength个字符，平均每个字符的长度

        if (descLength >= descTextViewWidth) {
            r0 = true;
            int extractPreStrNum = (int) (1.0f * descTextViewWidth / avg); // 当前viewHolder desc TextView能容纳多少个字符（前firstIndex区间的字符）
            extractPreStrNum = extractPreStrNum - PREFIX.length() - firstHitInfoLength; // 可以提取前面几个字符
            if (extractPreStrNum < 0) {
                extractPreStrNum = 0; // 有可能为负数，例如firstHitInfo特别长
            }

            // 新的文本
            int newStartIndex = firstIndex - extractPreStrNum;
            text = PREFIX + text.substring(newStartIndex, firstIndex + firstHitInfoLength);

            // extractPreStrNum 校准
            if (extractPreStrNum > 0) {
                descLength = desc.getPaint().measureText(text);
                if (descLength > descTextViewWidth) {
                    int delta = (int) ((descLength - descTextViewWidth) / (descLength / text.length())) + 1;
                    extractPreStrNum -= delta; // 減少提取的字符数
                }

                if (extractPreStrNum < 0) {
                    extractPreStrNum = 0; // 修正
                }
            }

            r1 = extractPreStrNum;
        } else {
            r0 = false;
            r1 = 0;
        }
        return new Object[]{r0, r1};
    }

    public static void display(TextView tv, String text, RecordHitInfo hitInfo) {
        if (hitInfo == null || hitInfo.isInvalied()) {
            tv.setText(text);
            return;
        }

        SpannableStringBuilder sb = new SpannableStringBuilder();
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(tv.getResources().getColor(R.color.colorAccent)), hitInfo.start, hitInfo.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        sb.append(ss);
        tv.setText(sb);
    }

    public static final void display(TextView tv, String text, List<com.netease.nimlib.sdk.search.model.RecordHitInfo> hitInfos) {
        if (hitInfos == null || hitInfos.isEmpty()) {
            tv.setText(text);
            return;
        }

        SpannableStringBuilder sb = new SpannableStringBuilder();
        SpannableString ss = new SpannableString(text);
        for (com.netease.nimlib.sdk.search.model.RecordHitInfo r : hitInfos) {
            ss.setSpan(new ForegroundColorSpan(tv.getResources().getColor(R.color.colorAccent)), r.start, r.end + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        sb.append(ss);
        tv.setText(sb);
    }


}
