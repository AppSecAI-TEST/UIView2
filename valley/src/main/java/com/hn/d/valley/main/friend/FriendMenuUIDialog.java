package com.hn.d.valley.main.friend;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.main.found.sub.ScanUIView;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter;
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.message.groupchat.TeamCreateHelper;
import com.hn.d.valley.widget.MenuPopUpWindow;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Action3;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：标签过滤选择对话框
 * 创建人员：hewking
 * 创建时间：2017-4-24
 * 修改人员：hewking
 * 修改时间：2017-4-24
 * 修改备注：
 * Version: 1.0.0
 */
public class FriendMenuUIDialog extends UIIDialogImpl {

    private LinearLayout mContentLayout;
    private RelativeLayout mRootLayout;
    private Action1<MenuPopUpWindow.MenuItem> selectorAction;

    private ILayout mLayout;

    /**
     * 当前高亮的tag
     */
    private ImageView mArrowView;
    private RRecyclerView mRecyclerView;
    private Rect clickViewRect = new Rect();

    public FriendMenuUIDialog(View clickView/**用来控制对话框显示的位置*/,
                              ILayout mLayout/**选择标签之后的回调*/) {
//        this.selectorAction = selectorAction;
        clickView.getGlobalVisibleRect(clickViewRect);
        this.mLayout = mLayout;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        //自动控制状态栏的padding
        mDialogRootLayout.fixInsertsTop();
        mRootLayout = new RelativeLayout(mActivity);

        mContentLayout = new LinearLayout(mActivity);
        mArrowView = new ImageView(mActivity);
        mArrowView.setImageResource(R.drawable.jiantou_biaoqian);
        mRecyclerView = new RRecyclerView(mActivity);
        mRecyclerView.setHasFixedSize(true);
        ResUtil.setBgDrawable(mRecyclerView, getDrawable(R.drawable.base_white_round_bg));

        mContentLayout.setOrientation(LinearLayout.VERTICAL);
        int width = (int) (density() * 137);
        LinearLayout.LayoutParams arrowParam = new LinearLayout.LayoutParams(-2, -2);
        arrowParam.leftMargin = (int) (density() * 117);
        mContentLayout.addView(mArrowView, arrowParam);
        mContentLayout.addView(mRecyclerView, new ViewGroup.LayoutParams(-2, -2));


        //int padding = (int) ResUtil.dpToPx(mActivity, 10);
        RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(width, ((int) (density() * 400)));
        contentParams.rightMargin = (int) (density() * 5);
        contentParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        L.i("FriendMenuUIDialog","arrow rightmargin :" + clickViewRect.width() / 2 + " content rightmargin : " + clickViewRect.width() / 2);
        mRootLayout.addView(mContentLayout, contentParams);

        //顶部偏移标题栏的高度
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        params.setMargins(0, mActivity.getResources().getDimensionPixelSize(R.dimen.tags_dialog_offset), 0, 0);
        dialogRootLayout.addView(mRootLayout, params);

        return mRootLayout;
    }

    @Override
    public View getAnimView() {
        return mContentLayout;
    }

    @Override
    public View getDialogDimView() {
        return mRootLayout;
    }

    @Override
    protected void initDialogContentView() {
        super.initDialogContentView();
        mRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });

        List<MenuPopUpWindow.MenuItem> items = createItems();

        mRecyclerView.setAdapter(new RBaseAdapter<MenuPopUpWindow.MenuItem>(mActivity, items) {
            @Override
            protected int getItemLayoutId(int viewType) {
                return R.layout.item_friend_menu;
            }

            @Override
            public int getItemType(int position) {
                return super.getItemType(position);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @Override
            protected void onBindView(RBaseViewHolder holder, int position, final MenuPopUpWindow.MenuItem item) {
                RTextView tipView = holder.v(R.id.tip_view);
                ImageView ivHead = holder.v(R.id.iv_item_head);
                tipView.setText(item.item);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.action.call(item.item);
                    }
                });
                ivHead.setImageResource(item.drawableRes);
            }
        });

        mRecyclerView.addItemDecoration(new RBaseItemDecoration());

    }

    private List<MenuPopUpWindow.MenuItem> createItems() {

        List<MenuPopUpWindow.MenuItem> items = new ArrayList<>();

        items.add(new MenuPopUpWindow.MenuItem("添加群聊",R.drawable.qunliao, new Action1<String>() {
            @Override
            public void call(String s) {
                ContactSelectUIVIew targetView = new ContactSelectUIVIew(new BaseContactSelectAdapter.Options());
                targetView.setSelectAction(new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                    @Override
                    public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, RequestCallback requestCallback) {
                        TeamCreateHelper.createAndSavePhoto(uiBaseDataView, absContactItems, requestCallback);
                    }
                });
                mLayout.startIView(targetView);
                finishDialog();
            }
        }));

        items.add(new MenuPopUpWindow.MenuItem("添加好友",R.drawable.tianjiahaoyou, new Action1<String>() {
            @Override
            public void call(String s) {
                mLayout.startIView(new NewFriend3UIView());
                finishDialog();
            }
        }));

        items.add(new MenuPopUpWindow.MenuItem("扫一扫",R.drawable.saoyisao, new Action1<String>() {
            @Override
            public void call(String s) {
                mLayout.startIView(new ScanUIView());
                finishDialog();
            }
        }));

        return items;

    }

    @Override
    public Animation loadStartAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        setDefaultConfig(translateAnimation, false);
        setDefaultConfig(alphaAnimation, false);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        return animationSet;
    }

    @Override
    public Animation loadFinishAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        setDefaultConfig(translateAnimation, true);
        setDefaultConfig(alphaAnimation, true);

        translateAnimation.setDuration(DEFAULT_DIALOG_FINISH_ANIM_TIME);
        alphaAnimation.setDuration(DEFAULT_DIALOG_FINISH_ANIM_TIME);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        return animationSet;
    }

    @Override
    public Animation loadLayoutAnimation() {
        Animation animation = super.loadLayoutAnimation();
        if (animation != null) {
            animation.setDuration(160);
        }
        return animation;
    }
}
