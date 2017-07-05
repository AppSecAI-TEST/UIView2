package com.hn.d.valley.main.friend;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.control.FriendsControl;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by hewking on 2017/3/7.
 */
public class FriendsAdapter extends RBaseAdapter<AbsContactItem> {

//    public static final int ITEM_TYPE_NORMAL = 10000;
//    public static final int ITEM_TYPE_FUNC = 10001;
//    public static final int ITEM_TYPE_LABEL = 10002;

    private FriendsControl mFrendsControl;

    private Action1<List<String>> mSideAction;

    public FriendsAdapter(Context context, FriendsControl mFriendsControl) {
        super(context);
        this.mFrendsControl = mFriendsControl;
    }



    @Override
    public int getItemType(int position) {
        //根据position获取每个AbsFriendItem 的itemtype
        return getAllDatas().get(position).getItemType();
    }

    @Override
    protected int getItemLayoutId(int viewType) {

        if (viewType == ItemTypes.SEARCH) {
            return R.layout.item_recent_search;
        }
        if (viewType == ItemTypes.SYSTEMPUSH) {
            return R.layout.item_friends_item;
        }
        if (viewType == ItemTypes.FUNC) {
            return R.layout.item_friends_item;
        }else if(viewType == ItemTypes.FRIEND ) {
            return R.layout.item_friends_item;
        }
        return R.layout.item_friends_item;
    }

    @Override
    protected void onBindView(RBaseViewHolder holder, int position, AbsContactItem bean) {

        if (holder.getItemViewType() == ItemTypes.SEARCH) {
            final FuncItem funcItem = (FuncItem) bean;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    funcItem.onFuncClick(mFrendsControl.getOtherLayout());
                }
            });
        }

        SimpleDraweeView iv_head = holder.v(R.id.iv_item_head);
        TextView tv_friend_name = holder.tv(R.id.tv_friend_name);
        iv_head.setOnClickListener(null);
        iv_head.setClickable(false);

        if (holder.getItemViewType() == ItemTypes.SYSTEMPUSH) {
            final SystemPushItem pushItem = (SystemPushItem) bean;
            final FriendBean friendBean = pushItem.getFriendBean();

//            iv_head.setImageUrl(friendBean.getAvatar());
            DraweeViewUtil.setDraweeViewHttp(iv_head,friendBean.getAvatar());
            tv_friend_name.setText(friendBean.getTrueName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pushItem.onFuncClick(pushItem.getFriendBean());
                }
            });
        }

        if(holder.getItemViewType() == ItemTypes.FUNC) {
            final FuncItem funcItem = (FuncItem) bean;
            iv_head.setImageResource(funcItem.getDrawableRes());
            tv_friend_name.setText(funcItem.text);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFrendsControl.getOtherLayout() != null ){
                        funcItem.onFuncClick(mFrendsControl.getOtherLayout());
                    }
                }
            });

        } else if (holder.getItemViewType() == ItemTypes.FRIEND) {
            ContactItem friendItem = (ContactItem) bean;
            final FriendBean friendBean = friendItem.getFriendBean();
//            iv_head.setImageThumbUrl(friendBean.getAvatar());
            DraweeViewUtil.setDraweeViewHttp(iv_head,friendBean.getAvatar());
            tv_friend_name.setText(friendBean.getTrueName());

            iv_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUserIcoClick(friendBean.getUid());
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mFrendsControl.getToUserDetailAction() != null ) {
                        mFrendsControl.getToUserDetailAction().call(friendBean);
                    }
                }
            });
        }
    }

    protected void onUserIcoClick(String uid) {

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    //实例化重写 获取数据
    protected List<? extends AbsContactItem> onPreProvide(){
       return null;
    }

    public void reset(List<FriendBean> beanList) {
        //checkNotNull
        if(beanList == null || beanList.size() == 0) {
            getAllDatas().clear();
            getAllDatas().addAll(onPreProvide());
            FriendsControl.sort(getAllDatas());
            notifyDataSetChanged();
            return;
        }

        getAllDatas().clear();
        //增加FUNC 类型数据
        getAllDatas().addAll(onPreProvide());
        for(FriendBean bean : beanList){
            ContactItem item = new ContactItem(bean);
            getAllDatas().add(item);
        }
        FriendsControl.sort(getAllDatas());

        if (mSideAction != null ) {
            mSideAction.call(FriendsControl.generateIndexLetter(getAllDatas()));
        }

        resetData(getAllDatas());
    }



    @Override
    public void resetData(List<AbsContactItem> datas) {
        notifyDataSetChanged();
//        super.resetData(datas);
//        notifyItemRangeChanged(onPreProvide().size(),datas.size() - onPreProvide().size());
    }

    public void setSideAction(Action1<List<String>> action) {
        this.mSideAction = action;
    }

}
