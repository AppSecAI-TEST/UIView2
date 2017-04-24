package com.hn.d.valley.main.message.groupchat;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupMemberBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.MsgService;

import rx.functions.Action1;

/**
 * Created by hewking on 2017/3/20.
 */
public class GroupMemberUIVIew  extends SingleRecyclerUIView<GroupMemberBean> {

    public static final String GID = "gid";
    public static final String IS_ADMIN = "is_admin";

    private String gid;
    private boolean isAdmin;

    private Action1<Boolean> kictAction;

    public Action1<Boolean> getKictAction() {
        return kictAction;
    }

    public void setKictAction(Action1<Boolean> kictAction) {
        this.kictAction = kictAction;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_group_member));
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected RExBaseAdapter<String, GroupMemberBean, String> initRExBaseAdapter() {
        return new GroupMemberAdapter(mActivity);
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

    public class GroupMemberAdapter extends RExBaseAdapter<String,GroupMemberBean,String> {

        public GroupMemberAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_friends_item;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, final GroupMemberBean dataBean) {
            super.onBindDataView(holder, posInData, dataBean);
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

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (!isAdmin) {
                        return false;
                    }

                    UIDialog.build()
                            .setDialogContent(mContext.getString(R.string.text_is_kict_group))
                            .setOkText(mActivity.getString(R.string.ok))
                            .setCancelText(mActivity.getString(R.string.cancel))
                            .setOkListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    kickMember(dataBean.getUserId());

                                }
                            })
                            .showDialog(mOtherILayout);

                    return true;
                }
            });
        }
    }

    private void kickMember(String userId) {

        add(RRetrofit.create(GroupChatService.class)
                .kick(Param.buildMap("to_uid:" + userId, "gid:" + gid))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        if (code == 1044) {
                            T_.show("你不是群主！");
                        }
                    }

                    @Override
                    public void onSucceed(String bean) {
                        T_.show("踢出群成员成功！");
                        onUILoadData("0");
                        if (kictAction != null) {
                            kictAction.call(true);
                        }
                    }
                }));

    }

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        return super.initItemDecoration().setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi));
    }
}
