package com.hn.d.valley.main.message.groupchat;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupMemberBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.slide.ISlideHelper;
import com.hn.d.valley.main.message.slide.holder.OneSlideViewHolder;
import com.hn.d.valley.main.message.slide.holder.SlideViewHolder;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.widget.HnButton;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by hewking on 2017/3/20.
 */
public class GroupMemberUIVIew  extends SingleRecyclerUIView<GroupMemberBean> {

    public static final String GID = "gid";
    public static final String IS_ADMIN = "is_admin";

    private String gid;
    private boolean isAdmin;

    private Action1<Boolean> kictAction;

    private GroupMemberAdapter mGroupMemberAdapter;

    public Action1<Boolean> getKictAction() {
        return kictAction;
    }

    private LinearLayout ll_bottom;
    private TextView tv_selected;
    private HnButton btn_delete;

    public void setKictAction(Action1<Boolean> kictAction) {
        this.kictAction = kictAction;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.text_edit))
                .setVisibility(View.GONE).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开itemview checkbox animator 弹出底部button
                editItems();
            }
        }));
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_group_member)).setRightItems(rightItems);
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected RExBaseAdapter<String, GroupMemberBean, String> initRExBaseAdapter() {
        mGroupMemberAdapter = new GroupMemberAdapter(mActivity);
        return mGroupMemberAdapter;
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        if (param != null) {
            gid = param.mBundle.getString(GID);
            isAdmin = param.mBundle.getBoolean(IS_ADMIN);
        }
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        if (isAdmin) {
            getUITitleBarContainer().showRightItem(0);
        }
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);

        RRetrofit.create(GroupChatService.class)
                .groupMember(Param.buildMap("uid:" + UserCache.getUserAccount(),"gid:" + gid))
                .compose(Rx.transformer(GroupMemberModel.GroupMemberList.class))
                .subscribe(new BaseSingleSubscriber<GroupMemberModel.GroupMemberList>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                    }

                    @Override
                    public void onSucceed(GroupMemberModel.GroupMemberList beans) {
                        if (beans == null || beans.getData_list().size() == 0) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(beans.getData_list());
                        }
                    }
                });
    }


    @Override
    protected void inflateRecyclerRootLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        View root = inflater.inflate(R.layout.view_member_select,baseContentLayout);
        mRefreshLayout = (RefreshLayout) root.findViewById(R.id.refresh_layout);
        mRecyclerView = (RRecyclerView) root.findViewById(R.id.recycler_view);
        ll_bottom = (LinearLayout) root.findViewById(R.id.ll_bottom);
        tv_selected = (TextView) root.findViewById(R.id.tv_selected);
        btn_delete = (HnButton) root.findViewById(R.id.btn_send);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<GroupMemberBean> selectorData = mGroupMemberAdapter.getSelectorData();
                UIDialog.build()
                        .setDialogContent(mActivity.getString(R.string.text_is_kict_group))
                        .setOkText(mActivity.getString(R.string.ok))
                        .setCancelText(mActivity.getString(R.string.cancel))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                kickMember(selectorData);
                            }
                        })
                        .showDialog(mOtherILayout);
            }
        });
    }

    private void animBottom(boolean show) {
        if (ll_bottom.getVisibility() == View.GONE) {
            ll_bottom.setVisibility(View.VISIBLE);
        }

        float start = show? ScreenUtil.dip2px(48):0;
        float end = show?0:ScreenUtil.dip2px(48);
        ObjectAnimator animator = ObjectAnimator.ofFloat(ll_bottom,"translationY",start,end);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    private class GroupMemberAdapter extends RExBaseAdapter<String,GroupMemberBean,String> {

        private SparseBooleanArray mCheckStats = new SparseBooleanArray();

        private ISlideHelper mISlideHelper = new ISlideHelper();

        private boolean isEditable = true;

        private GroupMemberAdapter(Context context) {
            super(context);
            setModel(RModelAdapter.MODEL_MULTI);
        }

        public void slideOpen() {
            isEditable = true;
            mISlideHelper.slideOpen();
            animBottom(true);
        }

        public void slideClose() {
            tv_selected.setText(String.format(getString(R.string.text_already_selected_number),0));
            mISlideHelper.slideClose();
            isEditable = true;
            animBottom(false);
            mCheckStats.clear();
            unSelectorAll(true);
        }

        @Override
        public void resetAllData(List<GroupMemberBean> allDatas) {
            //去除群成员中的群主 不会被踢出
            List<GroupMemberBean> filterList = new ArrayList<>();
            for (GroupMemberBean member : allDatas) {
                if (member.getDefaultNick().equals(UserCache.instance().getUserInfoBean().getUsername())) {
                    continue;
                }
                filterList.add(member);
            }
            super.resetAllData(filterList);
        }

        @NonNull
        @Override
        protected RBaseViewHolder createBaseViewHolder(int viewType, View itemView) {
            SlideViewHolder slideViewHolder = new OneSlideViewHolder(itemView,viewType);
            mISlideHelper.add(slideViewHolder);
            return slideViewHolder;
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_member_select_item;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, final GroupMemberBean dataBean) {
            super.onBindDataView(holder, posInData, dataBean);

            ((OneSlideViewHolder) holder).bind();

            HnGlideImageView iv_head = holder.v(R.id.iv_item_head);
            TextView tv_friend_name = holder.tv(R.id.tv_friend_name);

            iv_head.setImageUrl(dataBean.getUserAvatar());
            tv_friend_name.setText(dataBean.getDefaultNick());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOtherILayout.startIView(new UserDetailUIView2(dataBean.getUserId()));
                }
            });

        }

        @Override
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, final int position, GroupMemberBean bean) {
            super.onBindModelView(model, isSelector, holder, position, bean);
//            if(!isAdmin || !isEditable) {
//                return;
//            }
            if (!isEditable) {
                return;
            }

            final CheckBox checkBox = holder.v(R.id.cb_friend_addfirend);
            checkBox.setTag(position);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectorPosition(position,checkBox);
                    int tag = (int) checkBox.getTag();
                    boolean selector = isPositionSelector(position);
                    if (selector) {
                        mCheckStats.put(tag,true);
                    } else {
                        mCheckStats.delete(tag);
                    }
                    tv_selected.setText(String.format(getString(R.string.text_already_selected_number),mCheckStats.size()));
                }
            };

            checkBox.setOnClickListener(listener);
//            holder.itemView.setOnClickListener(listener);
            checkBox.setChecked(mCheckStats.get(position,false));
        }

        @Override
        protected boolean onUnSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
            final CheckBox checkBox = viewHolder.v(R.id.cb_friend_addfirend);
            checkBox.setChecked(false);
            checkBox.setTag(position);
            mCheckStats.delete(position);
            return true;
        }

    }

    private void kickMember(List<GroupMemberBean> selectedData) {
                Observable.from(selectedData)
                .map(new Func1<GroupMemberBean, String>() {
                    @Override
                    public String call(GroupMemberBean bean) {
                        add(RRetrofit.create(GroupChatService.class)
                                .kick(Param.buildMap("to_uid:" + bean.getUserId(), "gid:" + gid))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {
                                    @Override
                                    public void onError(int code, String msg) {
                                        super.onError(code, msg);
                                        if (code == 1044) {
                                            T_.show(mActivity.getString(R.string.text_you_are_not_host));
                                        }
                                    }

                                    @Override
                                    public void onSucceed(String bean) {
                                        T_.show(getString(R.string.text_kick_out_success));
                                        onUILoadData("0");
                                        if (kictAction != null) {
                                            kictAction.call(true);
                                        }
                                    }
                                }));
                        return bean.getUserId();

                    }
                }).subscribe(new RSubscriber<String>() {
            @Override
            public void onSucceed(String bean) {
                super.onSucceed(bean);
            }
        });

    }

    private void editItems() {
        TextView selectNum = (TextView) getUITitleBarContainer().getRightControlLayout().getChildAt(0);
        if (getString(R.string.text_edit).equals(selectNum.getText().toString())) {
            selectNum.setText(getString(R.string.cancel));
            mGroupMemberAdapter.slideOpen();
        } else if (getString(R.string.cancel).equals(selectNum.getText().toString())) {
            selectNum.setText(getString(R.string.text_edit));
            mGroupMemberAdapter.slideClose();
        }
    }

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        return super.initItemDecoration().setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi));
    }
}
