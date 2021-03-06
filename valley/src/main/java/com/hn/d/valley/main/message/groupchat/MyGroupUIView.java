package com.hn.d.valley.main.message.groupchat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.utils.T_;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.ListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.hn.d.valley.main.friend.GroupBean;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnLoading;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action2;

/**
 * Created by hewking on 2017/3/13.
 */
public class MyGroupUIView extends SingleRecyclerUIView<GroupBean> {

    private Action2<GroupBean, RequestCallback> selectAction;
    private BaseContactSelectAdapter.Options option;


    public MyGroupUIView(BaseContactSelectAdapter.Options options) {
        this.option = options;
    }

    public static void start(ILayout mLayout, BaseContactSelectAdapter.Options options, Action2<GroupBean, RequestCallback> selectAction) {
        Bundle bundle = new Bundle();
        MyGroupUIView targetView = new MyGroupUIView(options);
        targetView.setSelectAction(selectAction);
        mLayout.startIView(targetView, new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    public void setSelectAction(Action2<GroupBean, RequestCallback> setSelectAction) {
        this.selectAction = setSelectAction;
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

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

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        return new RBaseItemDecoration(getItemDecorationHeight()).setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.base_60dpi));
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    public static class GroupList extends ListModel<GroupBean> {
    }

    public class GroupListAdapter extends RExBaseAdapter<String, GroupBean, String> {

        protected SparseBooleanArray mCheckStats = new SparseBooleanArray();

        public GroupListAdapter(Context context) {
            super(context);
            setModel(RModelAdapter.MODEL_SINGLE);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_firends_addgroup_item;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, GroupBean dataBean) {

            //checkNotNull

            HnGlideImageView glideImageView = holder.v(R.id.iv_item_head);
            TextView tv = holder.tv(R.id.tv_friend_name);

//            DraweeViewUtil.setDraweeViewHttp(glideImageView, dataBean.getDefaultAvatar());
            glideImageView.setImageUrl(dataBean.getDefaultAvatar());
            tv.setText(dataBean.getTrueName());

            final String yxGid = dataBean.getYxGid();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SessionHelper.startTeamSession(mParentILayout, yxGid, SessionTypeEnum.Team);
                }
            });

        }


        @Override
        protected void onBindModelView(int model, final boolean isSelector, RBaseViewHolder holder, final int position, final GroupBean bean) {
            final CheckBox checkBox = holder.v(R.id.cb_friend_addfirend);
//            checkBox.setVisibility(option.showCheckBox?View.VISIBLE:View.GONE);
            checkBox.setVisibility(View.GONE);
            if (!option.showCheckBox) {
                return;
            }
            checkBox.setTag(position);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAllSelectorList().size() >= option.selectCountLimit) {
                        if (!isPositionSelector(position)) {
                            T_.show("已达到选中限制");
                            return;
                        }
                    }

                    setSelectorPosition(position, checkBox);

                    int tag = (int) checkBox.getTag();

                    boolean selector = isPositionSelector(position);
                    if (selector) {
                        mCheckStats.put(tag, true);
                    } else {
                        mCheckStats.delete(tag);
                    }

                    if (selectAction == null) {
                        return;
                    }
                    selectAction.call(getSelectorData().get(0), new RequestCallback() {
                        @Override
                        public void onStart() {
                            HnLoading.show(mParentILayout);
                        }

                        @Override
                        public void onSuccess(Object groupInfoBean) {
                            HnLoading.hide();
                            MyGroupUIView.this.finishIView();
                        }

                        @Override
                        public void onError(String msg) {
                            HnLoading.hide();
                        }
                    });
                }
            };

            checkBox.setOnClickListener(listener);
            holder.itemView.setOnClickListener(listener);
            checkBox.setChecked(mCheckStats.get(position, false));

        }

        @Override
        protected boolean onUnSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
            if (!option.showCheckBox) {
                return false;
            }
            final CheckBox checkBox = viewHolder.v(R.id.cb_friend_addfirend);
            checkBox.setChecked(false);
            checkBox.setTag(position);
            mCheckStats.delete(position);
            return true;
        }

        @Override
        public boolean isEnableLoadMore() {
            return false;
        }
    }
}
