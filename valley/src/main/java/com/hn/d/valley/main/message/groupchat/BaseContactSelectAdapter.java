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

import java.util.List;

import rx.functions.Action2;

/**
 * Created by hewking on 2017/3/9.
 */
public class BaseContactSelectAdapter extends RModelAdapter<AbsContactItem> {

    protected Action2 action;

    protected List<String> mSelectedUsers;

    protected ContactSelectUIVIew.Options options;

    protected SparseBooleanArray mCheckStats = new SparseBooleanArray();

    public BaseContactSelectAdapter(Context context, ContactSelectUIVIew.Options options) {
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


    }

    @Override
    public int getItemType(int position) {
        return mAllDatas.get(position).getItemType();
    }

    @Override
    protected boolean onUnSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
        return super.onUnSelectorPosition(viewHolder,position,isSelector);
    }

    @Override
    protected void onBindModelView(int model, final boolean isSelector, final RBaseViewHolder holder, final int position, final AbsContactItem bean) {



    }

    @Override
    public void resetData(List<AbsContactItem> datas) {
        mAllDatas.clear();
        mAllDatas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    protected void onBindNormalView(RBaseViewHolder holder, int position, AbsContactItem bean) {

    }

}
