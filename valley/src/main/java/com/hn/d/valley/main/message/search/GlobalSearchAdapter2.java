package com.hn.d.valley.main.message.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.main.found.sub.SearchNextUIView;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.friend.GroupBean;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.main.message.query.RecordHitInfo;
import com.hn.d.valley.main.message.service.SessionHelper;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.search.model.MsgIndexRecord;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
public class GlobalSearchAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LAST_ITEM_TYPE = 999;

    private static final String PREFIX = "...";

    private Context mContext;

    private ILayout mOtherLayout;

    private SearchResultList mResultList;

    private LayoutInflater mLayoutInflater;

    public void setTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }

    private GlobalSearchUIView2.Options option;

    private OnTextChangeListener onTextChangeListener;

    public GlobalSearchAdapter2(Context context, ILayout layout, GlobalSearchUIView2.Options option) {
        this.mOtherLayout = layout;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.option = option;
        mResultList = new SearchResultList(option);
    }

    public void resetData(List<AbsContactItem> items) {
        mResultList.resetData(items);
        notifyDataSetChanged();
    }

    public AbsContactItem getItem(int pos) {
        return mResultList.getItem(pos);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            default:
            case ItemTypes.FRIEND: {
                View view = mLayoutInflater.inflate(R.layout.item_friends_item, parent, false);
                return new ContactViewHolder(view, mOtherLayout);
            }
            case ItemTypes.GROUP: {
                View view = mLayoutInflater.inflate(R.layout.item_friends_item, parent, false);
                return new GroupVH(view, mOtherLayout);
            }
            case ItemTypes.MSG: {
                View view = mLayoutInflater.inflate(R.layout.item_message_chat_search, parent, false);
                return new MsgVH(view, mOtherLayout);
            }
            case ItemTypes.SECTION: {
                View view = mLayoutInflater.inflate(R.layout.item_search_section, parent, false);
                return new SectionVH(view, mOtherLayout);
            }
            case ItemTypes.FUNC: {
                View view = mLayoutInflater.inflate(R.layout.item_search_func, parent, false);
                return new FuncVH(view, mOtherLayout);
            }
            case LAST_ITEM_TYPE: {
                View view = mLayoutInflater.inflate(R.layout.item_search_result_search_last, parent, false);
                return new SearchVH(view, mOtherLayout);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (ItemTypes.FRIEND == getItemViewType(position)) {
            ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
            contactViewHolder.onBind((RecordContactItem) mResultList.getItem(position));
        } else if (ItemTypes.GROUP == getItemViewType(position)) {
            GroupVH groupVH = (GroupVH) holder;
            groupVH.onBind((RecordGroupItem) mResultList.getItem(position));
        } else if (ItemTypes.MSG == getItemViewType(position)) {
            MsgVH msgVH = (MsgVH) holder;
            msgVH.onBind((MsgItem) mResultList.getItem(position));
        } else if (ItemTypes.SECTION == getItemViewType(position)) {
            SectionVH sectionVH = (SectionVH) holder;
            sectionVH.onBind((SectionItem) mResultList.getItem(position));
        } else if (LAST_ITEM_TYPE == getItemViewType(position)) {
            SearchVH searchVH = (SearchVH) holder;
            searchVH.onBind();
        } else if (ItemTypes.FUNC == getItemViewType(position)) {
            FuncVH funcVH = (FuncVH) holder;
            funcVH.onBind((FuncItem) mResultList.getItem(position));
        }
    }

    @Override
    public int getItemCount() {
        //搜索更多 + 1
        return mResultList.getCount() + (option.isSearchMuti() ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {

        if (!option.isSearchMuti()) {
            return mResultList.getItem(position).getItemType();
        }

        if (position == getItemCount() - 1) {
            return LAST_ITEM_TYPE;
        }
        return mResultList.getItem(position).getItemType();
    }


    public interface OnTextChangeListener {
        String textChange();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_item_head)
        HnGlideImageView ivItemHead;
        @BindView(R.id.tv_friend_name)
        TextView tvFriendName;

        private ILayout mLayout;

        public ContactViewHolder(View itemView, ILayout mLayout) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mLayout = mLayout;
        }

        public void onBind(RecordContactItem contactItem) {

            final FriendBean friendBean = contactItem.getFriendBean();
            ivItemHead.setImageUrl(friendBean.getAvatar());
//        tv_friend_name.setText(friendBean.getDefaultMark());

            display(tvFriendName, friendBean.getDefaultMark(), contactItem.getHitInfo());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SessionHelper.startP2PSession(mLayout, friendBean.getUid(), SessionTypeEnum.P2P);
                }
            });

        }
    }

    public static class GroupVH extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_item_head)
        HnGlideImageView ivItemHead;
        @BindView(R.id.tv_friend_name)
        TextView tvFriendName;

        private ILayout mLayout;

        public GroupVH(View itemView, ILayout layout) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mLayout = layout;
        }

        public void onBind(RecordGroupItem item) {
            final GroupBean groupBean = item.getBean();
            ivItemHead.setImageUrl(groupBean.getAvatar());
//        tv_friend_name.setText(groupBean.getDefaultName());
            display(tvFriendName, groupBean.getDefaultName(), item.hitInfo);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SessionHelper.startTeamSession(mLayout, groupBean.getYxGid(), SessionTypeEnum.Team);
                }
            });
        }
    }

    public static class SectionVH extends RecyclerView.ViewHolder {

        @BindView(R.id.tip_view)
        RTextView tipView;
//        @BindView(R.id.search_line1)
//        View searchLine1;

        private ILayout mLayout;

        public SectionVH(View itemView, ILayout layout) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mLayout = layout;
        }

        public void onBind(SectionItem item) {
            tipView.setText(item.getGroupText());
        }

    }


    public static class SearchVH extends RecyclerView.ViewHolder {

        @BindView(R.id.search_word_view)
        RTextView searchWordView;
        private ILayout mLayout;

        public SearchVH(View itemView, ILayout layout) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mLayout = layout;
        }

        public void onBind() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLayout.startIView(new SearchNextUIView());
                }
            });
        }

    }

    public  class FuncVH extends RecyclerView.ViewHolder {

        @BindView(R.id.search_line2)
        View searchLine2;
        @BindView(R.id.tip_more_view)
        RTextView tipMoreView;
        private ILayout mLayout;

        public FuncVH(View itemView, ILayout layout) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mLayout = layout;
        }

        public void onBind(final FuncItem<ILayout> item) {
            tipMoreView.setText(item.getText());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mLayout.startIView(new SearchNextUIView());

                    if (onTextChangeListener != null) {
                        if ("聊天信息".equals(item.getText())) {
                            GlobalSingleSearchUIView.start(mLayout,onTextChangeListener.textChange(),new GlobalSearchUIView2.Options(false),new int[]{ItemTypes.MSG});
                        } else {
                            GlobalSingleSearchUIView.start(mLayout,onTextChangeListener.textChange(),new GlobalSearchUIView2.Options(false),new int[]{ItemTypes.GROUP});
                        }
                    }
                    item.onFuncClick(mLayout);
                }
            });
        }

    }


    public static class MsgVH extends RecyclerView.ViewHolder {

        @BindView(R.id.ico_view)
        HnGlideImageView icoView;
        @BindView(R.id.recent_name_view)
        TextView recentNameView;
        @BindView(R.id.msg_content_view)
        TextView msgContentView;
        @BindView(R.id.msg_time_view)
        TextView msgTimeView;
        @BindView(R.id.item_root_layout)
        LinearLayout itemRootLayout;

        private ILayout mLayout;

        public MsgVH(View itemView, ILayout layout) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mLayout = layout;
        }

        public void onBind(MsgItem msgItem) {

            final MsgIndexRecord indexRecord = msgItem.getIndexRecord();
            final IMMessage imMessage = indexRecord.getMessage();
            final SessionTypeEnum sessionType = imMessage.getSessionType();
            final String sessionId = imMessage.getSessionId();

            recentNameView.setText(msgItem.getDisplayName());
            icoView.setBackgroundResource(R.drawable.default_avatar);

            if (msgItem.isQuerySession()) {
                msgTimeView.setVisibility(View.VISIBLE);
                msgTimeView.setText(TimeUtil.getTimeShowString(indexRecord.getTime(), false));
            } else {
                msgTimeView.setVisibility(View.GONE);
            }


            if (indexRecord.getSessionType() == SessionTypeEnum.P2P) {
                UserInfoProvider.UserInfo userInfo = DefaultUserInfoProvider.getInstance().getUserInfo(indexRecord.getSessionId());
                icoView.setImageThumbUrl(userInfo.getAvatar());
            } else {
                Team team = TeamDataCache.getInstance().getTeamById(indexRecord.getSessionId());
                icoView.setImageThumbUrl(team.getIcon());
            }

            List<com.netease.nimlib.sdk.search.model.RecordHitInfo> clone = indexRecord.cloneHitInfo(); // 计算高亮区域并clone

            if (indexRecord.getCount() > 1) {
                msgContentView.setText(String.format("%d条相关聊天记录", indexRecord.getCount()));

                itemRootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GlobalSearchDetailUIView.start(mLayout, indexRecord);
                    }
                });
                return;
            }

            String text = indexRecord.getText(); // 原串

            // 异常情况，没有高亮击中的区间，直接设置原串
            if (clone == null || clone.isEmpty()) {
                msgContentView.setText(text);
                return;
            }

            int firstIndex = clone.get(0).start; // 首个需要高亮的关键字的起始位置
            int firstHitInfoLength = clone.get(0).end - clone.get(0).start + 1; // 首个需要高亮的关键字的长度

            // 判断是否需要截取
            Object[] result = needCutText(msgContentView, indexRecord.getText(), firstIndex, firstHitInfoLength);
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

            msgContentView.setText(msgItem.getIndexRecord().getMessage().getContent());
            display(msgContentView, text, clone);

            itemRootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SessionTypeEnum.Team == sessionType) {
                        SessionHelper.startTeamSession(mLayout, sessionId, SessionTypeEnum.Team, imMessage);
                    } else if (SessionTypeEnum.P2P == sessionType) {
                        SessionHelper.startP2PSession(mLayout, sessionId, SessionTypeEnum.P2P, imMessage);
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
            int descTextViewWidth = (int) (parent.getMeasuredWidth() - desc.getPaint().measureText(PREFIX));

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
