package com.hn.d.valley.main.message.groupchat;


import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.event.SelectedUserNumEvent;
import com.hn.d.valley.main.friend.AbsFriendItem;
import com.hn.d.valley.main.friend.FriendItem;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.utils.RBus;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.List;

import rx.functions.Action2;

/**
 * Created by hewking on 2017/3/9.
 */
public class AddGroupAdapter extends RModelAdapter<AbsFriendItem> {

    private Action2 action;

    private List<String> mSelectedUsers;

    public AddGroupAdapter(Context context) {
        super(context);
        setModel(RModelAdapter.MODEL_MULTI);
    }

    public void setAction(Action2 action) {
        this.action = action;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if(viewType == ItemTypes.FUNC) {
            return R.layout.item_recent_search;
        }
        return R.layout.item_firends_addgroup_item;
    }

    public void setSelecteUids(List<String> selecteUids) {
        this.mSelectedUsers = selecteUids;
    }

    @Override
    protected void onBindCommonView(RBaseViewHolder holder, int position, AbsFriendItem bean) {
        if (bean == null) {
            return;
        }
        if (getItemType(position) == ItemTypes.FUNC) {

            final FuncItem item = (FuncItem) bean;

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.onFuncClick(null);
                }
            });

        } else if (getItemType(position) == ItemTypes.FRIEND){
            FriendItem item = (FriendItem) bean;

            HnGlideImageView imageView = holder.v(R.id.iv_item_head);
            TextView nickName = holder.tv(R.id.tv_friend_name);
            imageView.setImageUrl(item.getFriendBean().getAvatar());
            nickName.setText(item.getFriendBean().getDefaultMark());

            if (mSelectedUsers != null && mSelectedUsers.size() != 0) {
                CheckBox checkBox = holder.v(R.id.cb_friend_addfirend);
                FriendBean friendBean = item.getFriendBean();
                if (mSelectedUsers.contains(friendBean.getUid()) ){
//                    setSelectorPosition(position,checkBox);
                    L.i("selector position :" + friendBean.getDefaultMark());
                    holder.itemView.setEnabled(false);
                    checkBox.setChecked(true);
                    checkBox.setEnabled(false);
                }
            }
        }


    }

    @Override
    public int getItemType(int position) {
        return mAllDatas.get(position).getItemType();
    }


    @Override
    protected void onBindModelView(int model, final boolean isSelector, final RBaseViewHolder holder, final int position, final AbsFriendItem bean) {

        if (getItemType(position) == ItemTypes.FRIEND) {
            final CheckBox checkBox = holder.v(R.id.cb_friend_addfirend);
//            checkBox.setChecked(isSelector);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectorPosition(position,checkBox);
                    RBus.post(new SelectedUserNumEvent(getSelectorData().size()));
                    if(action == null){
                        return;
                    }
                    action.call(!isSelector,bean);
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
