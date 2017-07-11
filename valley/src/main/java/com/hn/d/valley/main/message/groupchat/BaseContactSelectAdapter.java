package com.hn.d.valley.main.message.groupchat;


import android.content.Context;
import android.util.SparseBooleanArray;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ItemTypes;

import java.util.List;

import rx.functions.Action2;

/**
 * Created by hewking on 2017/3/9.
 */
public class BaseContactSelectAdapter extends RModelAdapter<AbsContactItem> {

    protected Action2 action;

    protected List<String> mSelectedUsers;

    protected Options options;

    protected SparseBooleanArray mCheckStats = new SparseBooleanArray();

    public BaseContactSelectAdapter(Context context, Options options) {
        super(context);
        this.options = options;
        setModel(options.mode);

    }

    public void setAction(Action2 action) {
        this.action = action;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == ItemTypes.SEARCH) {
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
        return super.onUnSelectorPosition(viewHolder, position, isSelector);
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

    public static class Options {


        public static final int DEFALUT_LIMIT = 5;

        /**
         * 单选/多选
         */
        int mode;
        /**
         * 最多选择多少个联系人
         */
        int selectCountLimit = DEFALUT_LIMIT;

        /**
         * 显示自己
         */
        boolean showMe = false;
        /**
         * 显示checkBox
         */
        boolean showCheckBox = false;
        /**
         * 是否默认选中
         */
        boolean isSelectUids = true;

        /**
         * 是否显示dialog
         */
        boolean showDialog = false;

        public void setShowMe(boolean showMe) {
            this.showMe = showMe;
        }

        public Options() {
            this(MODEL_MULTI);
        }

        public Options(int mode) {
            this(mode, DEFALUT_LIMIT);
        }

        public Options(int mode, int limit) {
            this(mode, limit, false);
        }

        public Options(int mode, int limit, boolean showCheckBox) {
            this(mode,limit,showCheckBox,true);
        }

        public Options(int mode, int limit,boolean showCheckBox,boolean isSelectUids) {
            this(mode,limit,showCheckBox,isSelectUids,false);
        }

        public Options(int mode, int limit,boolean showCheckBox,boolean isSelectUids,boolean showDialog) {
            this.showDialog = showDialog;
            this.mode = mode;
            this.selectCountLimit = limit;
            this.showCheckBox = showCheckBox;
            this.isSelectUids = isSelectUids;
        }

    }
}
