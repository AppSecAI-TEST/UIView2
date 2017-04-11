package com.hn.d.valley.main.message.groupchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.ListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.friend.GroupBean;
import com.hn.d.valley.main.message.service.SessionHelper;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by hewking on 2017/3/13.
 */
public class MyGroupUIView extends SingleRecyclerUIView<GroupBean> {


    @Override
    protected RExBaseAdapter<String, GroupBean, String> initRExBaseAdapter() {

        return new GroupListAdapter(mActivity);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_groupchat));
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(GroupChatService.class)
                .myGroup(Param.buildMap("uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(GroupList.class))
                .subscribe(new BaseSingleSubscriber<GroupList>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        showNonetLayout(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadData();
                            }
                        });
                    }

                    @Override
                    public void onSucceed(GroupList beans) {
                        if (beans == null || beans.getData_list() == null || beans.getData_list().isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(beans.getData_list());
                            onUILoadDataFinish();
                            mRExBaseAdapter.setEnableLoadMore(false);
                            //save to realm
                            saveToReaml(beans.getData_list());
                        }
                    }
                }));
    }

    @Override
    public void loadMoreData() {
        super.loadMoreData();
    }

    private void saveToReaml(final List<GroupBean> data_list) {
        final RealmResults<GroupBean> results = RRealm.realm().where(GroupBean.class).findAll();

        RRealm.exe(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
                for (GroupBean bean : data_list) {
                    realm.copyToRealm(bean);
                }
            }
        });
    }

    public static class GroupList extends ListModel<GroupBean> {}


    public class GroupListAdapter extends RExBaseAdapter<String,GroupBean,String>  {

        public GroupListAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_friends_item;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, GroupBean dataBean) {

            //checkNotNull

            HnGlideImageView glideImageView = holder.v(R.id.iv_item_head);
            TextView tv = holder.tv(R.id.tv_friend_name);

            glideImageView.setImageUrl(dataBean.getDefaultAvatar());
            tv.setText(dataBean.getDefaultName());

            final String yxGid = dataBean.getYxGid();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SessionHelper.startTeamSession(mOtherILayout,yxGid, SessionTypeEnum.Team);
                }
            });

        }

        @Override
        public boolean isEnableLoadMore() {
            return false;
        }
    }

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        return new RBaseItemDecoration(getItemDecorationHeight()).setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi));
    }


    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }
}
