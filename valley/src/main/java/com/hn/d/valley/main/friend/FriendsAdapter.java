package com.hn.d.valley.main.friend;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewking on 2017/3/7.
 */
public class FriendsAdapter extends RBaseAdapter<AbsFriendItem> {

//    public static final int ITEM_TYPE_NORMAL = 10000;
//    public static final int ITEM_TYPE_FUNC = 10001;
//    public static final int ITEM_TYPE_LABEL = 10002;

    private FriendsControl mFrendsControl;

    public FriendsAdapter(Context context, FriendsControl mFriendsControl) {
        super(context);
        this.mFrendsControl = mFriendsControl;
    }


//    @Override
//    protected void onBindView(RBaseViewHolder holder, int position, R dataBean) {
//
//        if(holder.getItemViewType() == ITEM_TYPE_FUNC) {
//
//
//        } else if (holder.getItemViewType() == ITEM_TYPE_NORMAL) {
//
////            mFrendsControl.initItem(holder,dataBean);
//            HnGlideImageView iv_head = holder.v(R.id.iv_item_head);
//            TextView tv_friend_name = holder.tv(R.id.tv_friend_name);
//            iv_head.setImageUrl(dataBean.getAvatar());
//            tv_friend_name.setText(dataBean.getDefaultMark());
//        }
//
//    }


    @Override
    public int getItemType(int position) {
//        if(position < 2) {
//            return ItemTypes.FUNC;
//        }
//
//        return ItemTypes.FRIEND;

        //根据position获取每个AbsFriendItem 的itemtype
        return getAllDatas().get(position).getItemType();
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == ItemTypes.FUNC) {
            return R.layout.item_firends_item;
        }else if(viewType == ItemTypes.FRIEND ) {
            return R.layout.item_firends_item;
        }
        return R.layout.item_firends_item;
    }

    @Override
    protected void onBindView(RBaseViewHolder holder, int position, AbsFriendItem bean) {

        if(holder.getItemViewType() == ItemTypes.FUNC) {
            final FuncItem funcItem = (FuncItem) bean;

            TextView tv_friend_name = holder.tv(R.id.tv_friend_name);
            tv_friend_name.setText(funcItem.text);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    funcItem.onFuncClick();
                }
            });

        } else if (holder.getItemViewType() == ItemTypes.FRIEND) {

//            mFrendsControl.initItem(holder,dataBean);
            FriendItem friendItem = (FriendItem) bean;
            FriendBean friendBean = friendItem.getFriendBean();
            HnGlideImageView iv_head = holder.v(R.id.iv_item_head);
            TextView tv_friend_name = holder.tv(R.id.tv_friend_name);
            iv_head.setImageUrl(friendBean.getAvatar());
            tv_friend_name.setText(friendBean.getDefaultMark());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    //实例化重写 获取数据
    protected List<? extends AbsFriendItem> onPreProvide(){
       return null;
    }

    public void reset(List<FriendBean> beanList) {
        //checkNotNull
        if(beanList == null || beanList.size() == 0) {
            return;
        }

        getAllDatas().clear();
        //增加FUNC 类型数据
        getAllDatas().addAll(onPreProvide());
        for(FriendBean bean : beanList){
            FriendItem item = new FriendItem(bean);
            getAllDatas().add(item);
        }
        resetData(getAllDatas());
    }

    @Override
    public void resetData(List<AbsFriendItem> datas) {
        super.resetData(datas);
    }
}
