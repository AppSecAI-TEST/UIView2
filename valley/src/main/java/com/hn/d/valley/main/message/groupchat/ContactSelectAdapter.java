package com.hn.d.valley.main.message.groupchat;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.utils.T_;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.event.SelectedUserNumEvent;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.utils.RBus;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by hewking on 2017/3/9.
 */
public class ContactSelectAdapter extends BaseContactSelectAdapter {

    private RecyclerView mRecyclerView;

    public ContactSelectAdapter(Context context, Options options,RecyclerView recyclerView) {
        super(context,options);
        this.mRecyclerView = recyclerView;
    }

    @Override
    protected void onBindCommonView(RBaseViewHolder holder, int position, AbsContactItem bean) {
        if (bean == null) {
            return;
        }
        if (getItemType(position) == ItemTypes.SEARCH) {
            final FuncItem item = (FuncItem) bean;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.onFuncClick(null);
                }
            });

        } else if (getItemType(position) == ItemTypes.FUNC) {
            final FuncItem item = (FuncItem) bean;
            TextView tv_friend_name = holder.tv(R.id.tv_friend_name);
            SimpleDraweeView iv_head = holder.v(R.id.iv_item_head);
            iv_head.setImageResource(item.getDrawableRes());
            tv_friend_name.setText(item.getText());
            CheckBox checkBox = holder.v(R.id.cb_friend_addfirend);
            checkBox.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.onFuncClick(null);
                }
            });


        } else if (getItemType(position) == ItemTypes.FRIEND){
            ContactItem item = (ContactItem) bean;

            SimpleDraweeView imageView = holder.v(R.id.iv_item_head);
            TextView nickName = holder.tv(R.id.tv_friend_name);
            DraweeViewUtil.setDraweeViewHttp(imageView,item.getFriendBean().getAvatar());
            nickName.setText(item.getFriendBean().getTrueName());
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

            if (!options.isSelectFinalUids && mSelectedFinalUsers != null && mSelectedFinalUsers.size() != 0) {
                FriendBean friendBean = ((ContactItem)bean).getFriendBean();
                if (mSelectedFinalUsers.contains(friendBean.getUid()) ){
                    mCheckStats.put(position,true);
                    holder.itemView.setEnabled(false);
                    holder.itemView.setBackgroundResource(R.color.default_base_bg_dark2);
                    checkBox.setEnabled(false);
                } else {
                    holder.itemView.setEnabled(true);
                    holder.itemView.setBackgroundResource(R.drawable.base_bg_selector);
                    checkBox.setEnabled(true);
                }
            }

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAllSelectorList().size() >= options.selectCountLimit) {
                        if (!isPositionSelector(position)) {
                            T_.show(mContext.getString(R.string.text_selected_limit));
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

        showSelectUsers();
        //notifyItemRangeChanged(1,datas.size() - 1);
    }

    public void showSelectUsers() {
        if (options.isSelectUids && mSelectedUsers != null && mSelectedUsers.size() != 0) {
            List<Integer> indexList = new ArrayList<>();
            // j == 0 为 搜索项 不参与查找
            for (int j = 1 ; j < mAllDatas.size() ; j ++) {
                FriendBean friendBean = ((ContactItem)mAllDatas.get(j)).getFriendBean();
                for (int k = 0 ; k < mSelectedUsers.size() ; k ++) {
                    if (friendBean.getUid().equals(mSelectedUsers.get(k))) {
                        indexList.add(j);
                        break;
                    }
                }
            }
            for (int i = 0 ; i < indexList.size() ; i ++) {
                mCheckStats.put(indexList.get(i),true);
            }
            // 选中 indexlist 标记的item
            setSelectIndexs((RRecyclerView) mRecyclerView, R.id.cb_friend_addfirend,indexList);
            // post 选中数量显示
            RBus.post(new SelectedUserNumEvent(getSelectorData().size()));
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onBindNormalView(RBaseViewHolder holder, int position, AbsContactItem bean) {

    }

}
