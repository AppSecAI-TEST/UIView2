package com.hn.d.valley.main.message.groupchat;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupMemberBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.widget.HnGlideImageView;

/**
 * Created by hewking on 2017/3/20.
 */
public class GroupMemberUIVIew  extends SingleRecyclerUIView<GroupMemberBean> {

    public static final String GID = "gid";
    public static final String IS_ADMIN = "is_admin";

    private String gid;

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_group_member));
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
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
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
                    mOtherILayout.startIView(new UserDetailUIView(dataBean.getUserId()));
                }
            });
        }
    }

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        return super.initItemDecoration().setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi));
    }
}
