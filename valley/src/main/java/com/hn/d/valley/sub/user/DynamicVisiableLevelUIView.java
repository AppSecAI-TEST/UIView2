package com.hn.d.valley.sub.user;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RGroupAdapter;
import com.angcyo.uiview.recycler.adapter.RGroupData;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter;
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.skin.SkinUtils;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.widget.HnCheckBox;
import com.hn.d.valley.widget.HnEmptyRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action0;
import rx.functions.Action3;

import static com.angcyo.uiview.utils.RUtils.safe;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/06/12 10:58
 * 修改人员：hewking
 * 修改时间：2017/06/12 10:58
 * 修改备注：
 * Version: 1.0.0
 */
public class DynamicVisiableLevelUIView extends SingleRecyclerUIView<DynamicVisiableLevelUIView.SubSection> {

    private RGroupAdapter<String, SubSection, String> mGroupAdapter;

    private Action3<LevelType, List<String>, List<FriendBean>> mSelectionAction;

    private LevelType mCurrentType = LevelType.PUBLIC;
    private LevelType mDefaultType = LevelType.PUBLIC;
    private List<String> mCurrentSelectedFriend = new ArrayList<>();
    private List<FriendBean> mSelectedFriendBean = new ArrayList<>();

    public DynamicVisiableLevelUIView() {
    }

    public DynamicVisiableLevelUIView(LevelType currentType, List<FriendBean> selectedFriendBean) {
        mCurrentType = currentType;
        mDefaultType = currentType;
        mSelectedFriendBean.addAll(selectedFriendBean);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.ok)).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelected();
            }
        }));
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_dynamic_visiable)).setRightItems(rightItems);
    }

    public DynamicVisiableLevelUIView setSelectionAction(Action3<LevelType, List<String>, List<FriendBean>> selectionAction) {
        mSelectionAction = selectionAction;
        return this;
    }

    private void onSelected() {
        if (mSelectionAction != null) {
            mSelectionAction.call(mCurrentType, mCurrentSelectedFriend, mSelectedFriendBean);
            finishIView();
        }
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        initData();
    }

    @Override
    protected void inflateRecyclerRootLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        mRootSoftInputLayout = new RSoftInputLayout(mActivity);
        mRefreshLayout = new HnEmptyRefreshLayout(mActivity);
        mRecyclerView = new RRecyclerView(mActivity);
        mRecyclerView.setHasFixedSize(true);
        mRefreshLayout.addView(mRecyclerView, new ViewGroup.LayoutParams(-1, -1));
        //baseContentLayout.addView(mRefreshLayout, new ViewGroup.LayoutParams(-1, -1));
        mRootSoftInputLayout.addView(mRefreshLayout, new ViewGroup.LayoutParams(-1, -1));
        baseContentLayout.addView(mRootSoftInputLayout, new ViewGroup.LayoutParams(-1, -1));
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        mRExBaseAdapter.setEnableLoadMore(false);
    }

    @Override
    protected RExBaseAdapter<String, SubSection, String> initRExBaseAdapter() {
        mGroupAdapter = new VisiableLevelAdpater(mActivity);
        return mGroupAdapter;
    }

    public void initData() {

        List<SubSection> datas = new ArrayList<>();
        datas.add(new SubSection(LevelType.PUBLIC));
        datas.add(new SubSection(LevelType.PRIVATE));
        datas.add(new SubSection(LevelType.ONLY_FIRENDS_VISIABLE));

        SubSection visiableSub = new SubSection(LevelType.PARTIALLY_VISIABLE).setClickListener(true);
        if (mCurrentType == LevelType.PARTIALLY_VISIABLE) {
            visiableSub.setFriendList(mSelectedFriendBean);
            visiableSub.addSubBean();
            visiableSub.setClickListener(false);
        }
        datas.add(visiableSub);

        SubSection invisibleSub = new SubSection(LevelType.PARTIALLY_INVISIABLE).setClickListener(true);
        if (mCurrentType == LevelType.PARTIALLY_INVISIABLE) {
            invisibleSub.setFriendList(mSelectedFriendBean);
            invisibleSub.addSubBean();
            visiableSub.setClickListener(false);
        }
        datas.add(invisibleSub);

        mGroupAdapter.addSelectorPosition(mCurrentType.getId() - 1);//默认选中位置
        mGroupAdapter.setAllDatas(datas);
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    /**
     * 1-公开 2-私密 3-部分好友可见 4-不给谁看,5-仅好友可见】
     */
    public enum LevelType {
        PUBLIC(1, ValleyApp.getApp().getResources().getString(R.string.text_public), "所有人可见"),
        PRIVATE(2, ValleyApp.getApp().getResources().getString(R.string.text_private), "仅自己可见"),
        ONLY_FIRENDS_VISIABLE(5, ValleyApp.getApp().getResources().getString(R.string.text_only_friends_visiable), "所有朋友可见"),
        PARTIALLY_VISIABLE(3, ValleyApp.getApp().getResources().getString(R.string.text_partially_visiable), "选中的朋友可见"),
        PARTIALLY_INVISIABLE(4, ValleyApp.getApp().getResources().getString(R.string.text_partially_invisiable), "选中的朋友不可见");

        int id;
        String des;
        String level;

        LevelType(int id, String level, String des) {
            this.id = id;
            this.des = des;
            this.level = level;
        }

        static LevelType valueOf(int id) {
            switch (id) {
                case 1:
                    return PUBLIC;
                case 2:
                    return PRIVATE;
                case 4:
                    return PARTIALLY_VISIABLE;
                case 5:
                    return PARTIALLY_INVISIABLE;
                case 3:
                    return ONLY_FIRENDS_VISIABLE;
                default:
                    return null;
            }
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public int getId() {
            return id;
        }

        public String getDes() {
            return des;
        }
    }

    private class VisiableLevelAdpater extends RGroupAdapter<String, SubSection, String> {

        public VisiableLevelAdpater(Context context) {
            super(context);
            setModel(MODEL_SINGLE);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            if (viewType == TYPE_GROUP_HEAD) {
                return R.layout.item_checkbox_view;
            } else if (viewType == TYPE_GROUP_DATA) {
                return R.layout.item_single_add_friend;
            }
            return super.getItemLayoutId(viewType);
        }

        @Override
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, SubSection bean) {
            super.onBindModelView(model, isSelector, holder, position, bean);
            if (isInGroup(position)) {
                HnCheckBox cb = holder.v(R.id.radio_view);
                cb.setChecked(isSelector);
                if (isSelector) {
                    bean = (SubSection) getGroupDataFromPosition(position);

                    mCurrentType = bean.section;
                    mCurrentSelectedFriend = bean.atUsers;
                    mSelectedFriendBean = bean.mFriendList;
                    if ((mCurrentType == LevelType.PARTIALLY_VISIABLE || mCurrentType == LevelType.PARTIALLY_INVISIABLE)
                            && mCurrentSelectedFriend.size() == 0) {
                        mCurrentType = LevelType.PUBLIC;
                    }
                }
            }
        }
    }

    protected class SubSection extends RGroupData<FriendBean> {

        private LevelType section;

        //private List<AbsContactItem> subItems;//局部变量

        private List<String> atUsers = new ArrayList<>();//@的用户
        private List<FriendBean> mFriendList = new ArrayList<>();

        private boolean canClick;

        public SubSection(LevelType section) {
            this.section = section;
        }

        public SubSection setClickListener(boolean listener) {
            this.canClick = listener;
            return this;
        }

        public void setFriendList(List<FriendBean> friendList) {
            mFriendList.addAll(friendList);

            atUsers.clear();
            for (FriendBean item : friendList) {
                atUsers.add(item.getUid());
            }
        }

        @Override
        public int getDataItemType(int indexInData) {
            return super.getDataItemType(indexInData);
        }

        @Override
        public int getGroupCount() {
            return super.getGroupCount();
        }

        @Override
        public int getDataCount() {
            return super.getDataCount();
        }


        @Override
        protected void onBindGroupView(RBaseViewHolder holder, final int position, int indexInGroup) {
            TextView tv_name = holder.tv(R.id.username);
            TextView signature = holder.tv(R.id.signature);
            tv_name.setText(section.getLevel());
            signature.setText(section.getDes());

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGroupAdapter.setSelectorPosition(position);
                    if (canClick) {
                        ContactSelectUIVIew.start(getILayout(), new BaseContactSelectAdapter.Options(RModelAdapter.MODEL_MULTI)
                                , atUsers, new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                                    @Override
                                    public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, com.hn.d.valley.main.message.groupchat.RequestCallback requestCallback) {
                                        requestCallback.onSuccess("");
                                        if (absContactItems.size() > 0) {
                                            canClick = false;
                                        }
                                        // 增加sub 选项
                                        addSubBean();
                                        processSelectedData(absContactItems);
                                    }
                                })
                                .setOnCancelAction(new Action0() {
                                    @Override
                                    public void call() {
                                        mGroupAdapter.setSelectorPosition(mDefaultType.getId() - 1);
                                    }
                                });

                    }
                }
            };

            holder.v(R.id.radio_view).setOnClickListener(listener);
            holder.itemView.setOnClickListener(listener);
        }

        public void addSubBean() {
            List<FriendBean> sub = new ArrayList<>();
            sub.add(new FriendBean());
            resetDatas(sub);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int position, int indexInData) {
            super.onBindDataView(holder, position, indexInData);
            TextView tv_name = holder.tv(R.id.text_view);

            switch (SkinUtils.getSkin()) {
                case SkinManagerUIView.SKIN_BLACK:
                    tv_name.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.add_black), null, null, null);
                    break;
                case SkinManagerUIView.SKIN_GREEN:
                    tv_name.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.add_green), null, null, null);
                    break;
                case SkinManagerUIView.SKIN_BLUE:
                    tv_name.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.add_blue), null, null, null);
                    break;
            }
            // 设置已添加 name
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactSelectUIVIew.start(getILayout(), new BaseContactSelectAdapter.Options(RModelAdapter.MODEL_MULTI)
                            , atUsers, new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                                @Override
                                public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems,
                                                 com.hn.d.valley.main.message.groupchat.RequestCallback requestCallback) {
                                    requestCallback.onSuccess("");
                                    processSelectedData(absContactItems);
                                }
                            });
                }
            });
            tv_name.setText(SpannableStringUtils.getBuilder(connect(mFriendList))
                    .setForegroundColor(SkinHelper.getSkin().getThemeSubColor())
                    .create()
            );
        }

        private void processSelectedData(List<AbsContactItem> list) {
            atUsers.clear();
            mFriendList.clear();
            for (AbsContactItem item : list) {
                if (item instanceof ContactItem) {
                    FriendBean friendBean = ((ContactItem) item).getFriendBean();
                    atUsers.add(friendBean.getUid());
                    mFriendList.add(friendBean);
                }
            }
            mGroupAdapter.notifyDataSetChanged();
        }

        public String connect(List<FriendBean> list) {
            if (list == null) {
                return "";
            }
            StringBuilder builder = new StringBuilder();
            for (FriendBean bean : list) {
                builder.append(bean.getTrueName());
                builder.append(",");
            }

            return safe(builder);
        }


    }
}
