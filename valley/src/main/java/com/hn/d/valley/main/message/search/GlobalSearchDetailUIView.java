package com.hn.d.valley.main.message.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.message.query.TextQuery;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.search.model.MsgIndexRecord;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import java.util.List;

/**
 * Created by hewking on 2017/3/30.
 */
public class GlobalSearchDetailUIView extends SingleRecyclerUIView<AbsContactItem> implements GlobalSearch.ISearchDetailView{

    private static final String EXTRA_SESSION_TYPE = "EXTRA_SESSION_TYPE";
    private static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    private static final String EXTRA_QUERY = "EXTRA_QUERY";
    private static final String EXTRA_RESULT_COUNT = "EXTRA_RESULT_COUNT";

    private String sessionId;
    private SessionTypeEnum sessionType;
    private String query;
    private int resultCount;

    private GlobalSearchDetailPresenter mPresenter;

    public static void start(ILayout mLayout , MsgIndexRecord indexRecord) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_SESSION_ID,indexRecord.getSessionId());
        bundle.putInt(EXTRA_SESSION_TYPE,indexRecord.getSessionType().getValue());
        bundle.putString(EXTRA_QUERY,indexRecord.getQuery());
        bundle.putInt(EXTRA_RESULT_COUNT,indexRecord.getCount());

        GlobalSearchDetailUIView targetView = new GlobalSearchDetailUIView();
        mLayout.startIView(targetView, new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }



    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString("群消息记录");
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);

        mPresenter = new GlobalSearchDetailPresenter();
        mPresenter.bindView(this);
        if (param != null) {
            sessionId = param.mBundle.getString(EXTRA_SESSION_ID);
            sessionType = SessionTypeEnum.typeOfValue(param.mBundle.getInt(EXTRA_SESSION_TYPE));
            query = param.mBundle.getString(EXTRA_QUERY);
            resultCount = param.mBundle.getInt(EXTRA_RESULT_COUNT);
        }
    }



    @Override
    protected RExBaseAdapter<String, AbsContactItem, String> initRExBaseAdapter() {
        return new SearchMessageListAdatper(mActivity);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        TextQuery textQuery = new TextQuery(query);
        textQuery.extra = new Object[]{sessionType, sessionId, new MsgIndexRecord(null, query)};
        mPresenter.search(textQuery);

    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    public void onSearchSuccess(List<AbsContactItem> items) {
        showContentLayout();
        mRExBaseAdapter.resetData(items);
    }

    @Override
    public void onRequestStart() {

    }

    @Override
    public void onRequestFinish() {
        onUILoadDataFinish();
    }

    @Override
    public void onRequestCancel() {

    }

    @Override
    public void onRequestError(int code, @NonNull String msg) {

    }

    @Override
    protected RecyclerView.ItemDecoration initItemDecoration() {
        return super.createBaseItemDecoration().setMarginStart(getResources().getDimensionPixelSize(R.dimen.base_xhdpi))
                .setMarginEnd(getResources().getDimensionPixelOffset(R.dimen.base_xhdpi));
    }

    public class SearchMessageListAdatper extends RExBaseAdapter<String,AbsContactItem,String>{

        public int SECTION_TYPE = 10001;
        public int MSGITEM_TYPE = 10000;

        public SearchMessageListAdatper(Context context) {
            super(context);
        }

        @Override
        public void resetData(List<AbsContactItem> datas) {
            datas.add(0,new MsgItem(null,false));
            super.resetData(datas);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, AbsContactItem item) {
            super.onBindDataView(holder, posInData, item);

            if (SECTION_TYPE == holder.getViewType()) {
                TextView textView = holder.tv(R.id.text_view);
                String tip = String.format("共%d条与\"%s\"相关的聊天记录", resultCount, query);
                textView.setText(tip);
            }

            if (MSGITEM_TYPE == holder.getViewType()) {

                final MsgItem msgItem = (MsgItem) item;

                HnGlideImageView iv_icon = holder.v(R.id.ico_view);
                TextView recent_name_view = holder.tv(R.id.recent_name_view);
                TextView msg_content_view = holder.tv(R.id.msg_content_view);
                TextView msg_time_view = holder.tv(R.id.msg_time_view);

                MsgIndexRecord indexRecord = msgItem.getIndexRecord();
                final IMMessage imMessage = indexRecord.getMessage();
                final SessionTypeEnum sessionType = imMessage.getSessionType();
                final String sessionId = imMessage.getSessionId();

                recent_name_view.setText(msgItem.getDisplayName());
                iv_icon.setImageResource(R.drawable.default_avatar);

                if (msgItem.isQuerySession()) {
                    msg_time_view.setVisibility(View.VISIBLE);
                    msg_time_view.setText(TimeUtil.getTimeShowString(indexRecord.getTime(), false));
                } else {
                    msg_time_view.setVisibility(View.GONE);
                }


                if (indexRecord.getSessionType() == SessionTypeEnum.P2P) {
                    UserInfoProvider.UserInfo userInfo = DefaultUserInfoProvider.getInstance().getUserInfo(indexRecord.getSessionId());
                    iv_icon.setImageUrl(userInfo.getAvatar());
                } else {
                    Team team = TeamDataCache.getInstance().getTeamById(indexRecord.getSessionId());
                    iv_icon.setImageUrl(team.getIcon());
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SessionTypeEnum.Team == sessionType) {
                            SessionHelper.startTeamSession(mOtherILayout, sessionId, SessionTypeEnum.Team, imMessage);
                        } else if (SessionTypeEnum.P2P == sessionType) {
                            SessionHelper.startP2PSession(mOtherILayout, sessionId, SessionTypeEnum.P2P, imMessage);
                        }
                    }
                });

                msg_content_view.setText(imMessage.getContent());

            }

        }

        @Override
        protected int getItemLayoutId(int viewType) {

            if (viewType == SECTION_TYPE) {
                return R.layout.item_single_text_view;
            }

            return R.layout.item_message_chat_search;
        }

        @Override
        public int getItemType(int position) {
            if (position == 0) {
                return SECTION_TYPE;
            }
            return MSGITEM_TYPE;
        }
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }
}
