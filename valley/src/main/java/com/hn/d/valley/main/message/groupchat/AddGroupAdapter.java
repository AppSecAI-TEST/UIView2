package com.hn.d.valley.main.message.groupchat;


import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.event.SelectedUserNumEvent;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.utils.RBus;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action2;

/**
 * Created by hewking on 2017/3/9.
 */
public class AddGroupAdapter extends RModelAdapter<AbsContactItem> {

    private Action2 action;

    private List<String> mSelectedUsers;

    private ContactSelectUIVIew.Options options;

    private SparseBooleanArray mCheckStats = new SparseBooleanArray();

    public AddGroupAdapter(Context context, ContactSelectUIVIew.Options options) {
        super(context);
        this.options = options;
        setModel(options.mode);

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
    protected void onBindCommonView(RBaseViewHolder holder, int position, AbsContactItem bean) {
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
            ContactItem item = (ContactItem) bean;

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
    protected boolean onUnSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
        final CheckBox checkBox = viewHolder.v(R.id.cb_friend_addfirend);
        checkBox.setChecked(false);
        checkBox.setTag(position);
        mCheckStats.delete(position);
        return true;
    }

    @Override
    protected void onBindModelView(int model, final boolean isSelector, final RBaseViewHolder holder, final int position, final AbsContactItem bean) {

        if (getItemType(position) == ItemTypes.FRIEND) {
            final CheckBox checkBox = holder.v(R.id.cb_friend_addfirend);
            checkBox.setTag(position);

            if (mSelectedUsers != null && mSelectedUsers.size() != 0) {
                FriendBean friendBean = ((ContactItem)bean).getFriendBean();
                if (mSelectedUsers.contains(friendBean.getUid()) ){
                    mCheckStats.put(position,true);
                    holder.itemView.setEnabled(false);
                    checkBox.setEnabled(false);
                } else {
                    holder.itemView.setEnabled(true);
                    checkBox.setEnabled(true);
                }
            }

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getAllSelectorList().size() >= options.selectCountLimit) {
                        if (!isPositionSelector(position)) {
                            T_.show("已达到选中限制");
                            return;
                        }
                    }

                    setSelectorPosition(position,checkBox);

                    int tag = (int) checkBox.getTag();

                    boolean selector = isPositionSelector(position);
                    if (selector) {
                        mCheckStats.put(tag,true);
                    } else {
                        mCheckStats.delete(tag);
                    }

                    RBus.post(new SelectedUserNumEvent(getSelectorData().size()));
                    if(action == null){
                        return;
                    }
                    action.call(!isSelector,bean);
                }
            };

            checkBox.setOnClickListener(listener);
            holder.itemView.setOnClickListener(listener);
            checkBox.setChecked(mCheckStats.get(position,false));
        }

    }

    @Override
    public void resetData(List<AbsContactItem> datas) {
//        if (datas == null) {
//            this.mAllDatas = new ArrayList<>();
//        } else {
//            this.mAllDatas = datas;
//        }
        mAllDatas.clear();
        mAllDatas.addAll(datas);
        notifyDataSetChanged();
        //notifyItemRangeChanged(1,datas.size() - 1);
    }

    @Override
    protected void onBindNormalView(RBaseViewHolder holder, int position, AbsContactItem bean) {

    }

}
