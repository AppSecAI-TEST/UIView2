package com.hn.d.valley.main.message.groupchat;


import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.main.friend.AbsFriendItem;
import com.hn.d.valley.main.friend.FriendItem;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.widget.HnGlideImageView;

import rx.functions.Action2;

/**
 * Created by hewking on 2017/3/9.
 */
public class AddGroupAdapter extends RModelAdapter<AbsFriendItem> {



    public AddGroupAdapter(Context context) {
        super(context);
        setModel(RModelAdapter.MODEL_MULTI);
    }

//    private Action2 action;
//
//    public void setAction(Action2 action) {
//        this.action = action;
//    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if(viewType == ItemTypes.FUNC) {
            return R.layout.item_recent_search;
        }
        return R.layout.item_firends_addgroup_item;
    }

    @Override
    protected void onBindCommonView(RBaseViewHolder holder, int position, AbsFriendItem bean) {
        if (bean == null) {
            return;
        }

        if (getItemType(position) == ItemTypes.FUNC) {


        } else if (getItemType(position) == ItemTypes.FRIEND){

            FriendItem item = (FriendItem) bean;

            HnGlideImageView imageView = holder.v(R.id.iv_item_head);
            TextView nickName = holder.tv(R.id.tv_friend_name);
            imageView.setImageUrl(item.getFriendBean().getAvatar());
            nickName.setText(item.getFriendBean().getDefaultMark());
        }


    }

    @Override
    public int getItemType(int position) {
        return mAllDatas.get(position).getItemType();
    }


    @Override
    protected void onBindModelView(int model, final boolean isSelector, RBaseViewHolder holder, final int position, final AbsFriendItem bean) {

        if (getItemType(position) == ItemTypes.FRIEND) {
            final CheckBox checkBox = holder.v(R.id.cb_friend_addfirend);
//        checkBox.setChecked(isSelector);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                if(action == null){
//                    return;
//                }
//                action.call(!isSelector,bean);
                    setSelectorPosition(position,checkBox);
                }
            };

            checkBox.setOnClickListener(listener);
            holder.itemView.setOnClickListener(listener);
        }

    }

    @Override
    protected void onBindNormalView(RBaseViewHolder holder, int position, AbsFriendItem bean) {

    }

}
