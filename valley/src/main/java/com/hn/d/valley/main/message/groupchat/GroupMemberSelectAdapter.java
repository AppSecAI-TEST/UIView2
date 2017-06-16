package com.hn.d.valley.main.message.groupchat;


import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.GroupMemberBean;
import com.hn.d.valley.bean.event.SelectedUserNumEvent;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.utils.RBus;

import java.util.List;

/**
 * Created by hewking on 2017/3/9.
 */
public class GroupMemberSelectAdapter extends BaseContactSelectAdapter {

    public GroupMemberSelectAdapter(Context context, Options options) {
        super(context,options);
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

        } else if (getItemType(position) == ItemTypes.GROUPMEMBER){
            GroupMemberItem item = (GroupMemberItem) bean;

            SimpleDraweeView imageView = holder.v(R.id.iv_item_head);
            TextView nickName = holder.tv(R.id.tv_friend_name);
            DraweeViewUtil.setDraweeViewHttp(imageView,item.getMemberBean().getUserAvatar());
            nickName.setText(item.getMemberBean().getDefaultNick());
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
        final CheckBox checkBox = holder.v(R.id.cb_friend_addfirend);
        checkBox.setVisibility(options.showCheckBox?View.VISIBLE:View.GONE);
        if (getItemType(position) == ItemTypes.GROUPMEMBER) {
            checkBox.setTag(position);
            if (mSelectedUsers != null && mSelectedUsers.size() != 0) {
                GroupMemberBean memberBean = ((GroupMemberItem)bean).getMemberBean();
                if (mSelectedUsers.contains(memberBean.getUserId()) ){
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
                    action.call(selector,bean);
                }
            };

            checkBox.setOnClickListener(listener);
            holder.itemView.setOnClickListener(listener);
            checkBox.setChecked(mCheckStats.get(position,false));
        }

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
