package com.hn.d.valley.main.message.groupchat;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupMemberBean;
import com.hn.d.valley.bean.ListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.friend.AbsFriendItem;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action3;

/**
 * Created by hewking on 2017/3/13.
 */
public class GroupMemberModel {

    private GroupMemberModel(){

    }

    private static class Holder {
        private static final GroupMemberModel instance = new GroupMemberModel();
    }

    private RRecyclerView icoRecyclerView;
    private BaseContentUIView contentUIView;

    private ChatInfoAdapter mAdapter;

    private String mSessionId;

    public static GroupMemberModel getInstanse() {
        return Holder.instance;
    }

    public void init(RBaseViewHolder holder , Context ctx, BaseContentUIView view,String sesssionId) {

        this.contentUIView = view;
        this.mSessionId = sesssionId;

        icoRecyclerView = holder.v(R.id.rv_headimg_name_icon);

        icoRecyclerView.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
        mAdapter = new ChatInfoAdapter(ctx);

        icoRecyclerView.setAdapter(mAdapter);

        TextView tv_add_groupmembers = holder.tv(R.id.tv_add_groupmembers);
        tv_add_groupmembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<GroupMemberBean> mAdapterAllDatas = mAdapter.getAllDatas();
                List<String> uids = new ArrayList<>();
                for (GroupMemberBean bean : mAdapterAllDatas) {
                    uids.add(bean.getUserId());
                }
                ContactSelectUIVIew.start(contentUIView.getILayout(),uids,new Action3< UIBaseRxView, List< AbsFriendItem >, RequestCallback>() {
                    @Override
                    public void call(UIBaseRxView uiBaseDataView, List<AbsFriendItem> absFriendItems, RequestCallback requestCallback) {
                        TeamCreateHelper.invite(uiBaseDataView,absFriendItems,requestCallback);
                    }
                });
            }
        });

//        loadData();


    }

    public class ChatInfoAdapter extends RBaseAdapter<GroupMemberBean> {

        private static final int DEFAULT_COUNT = 5;

        public ChatInfoAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_headimg_name_view;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, GroupMemberBean bean) {
            HnGlideImageView image_view = holder.v(R.id.image_view);
            TextView username = holder.tv(R.id.tv__username);

            image_view.setImageUrl(bean.getUserAvatar());
            username.setText(bean.getDefaultNick());
        }

        @Override
        public int getItemCount() {
            return getAllDatas().size() > DEFAULT_COUNT ? DEFAULT_COUNT : getAllDatas().size();
        }
    }

    public void loadData(String gid) {
        RRetrofit.create(GroupChatService.class)
        .groupMember(Param.buildMap("uid:" + UserCache.getUserAccount(),"gid:" + gid))
        .compose(Rx.transformer(GroupMemberList.class))
                .subscribe(new BaseSingleSubscriber<GroupMemberList>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }

                    @Override
                    public void onSucceed(GroupMemberList beans) {
                       mAdapter.resetData(beans.getData_list());
                    }
                });
    }

    public static class GroupMemberList extends ListModel<GroupMemberBean>{}

}
