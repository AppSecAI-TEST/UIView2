package com.hn.d.valley.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.github.WaveSideBarView;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RGroupItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.github.promeg.pinyinhelper.Pinyin;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.FriendListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.friend.AbsFriendItem;
import com.hn.d.valley.main.friend.FriendsAdapter;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.service.ContactService;

import java.util.List;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by hewking on 2017/3/7.
 */
public class FriendsControl implements RefreshLayout.OnRefreshListener{

    RBaseViewHolder mViewHolder;

    RRecyclerView rRecyclerView;
    RefreshLayout  mRefreshLayout;
    FriendsAdapter mFriendsAdapter;
    WaveSideBarView sidebar_friend;
    ILayout otherLayout;

    Context mContext;

    Action1<FriendBean> toUserDetailAction;

    private CompositeSubscription mSubscriptions;


    public Action1 getToUserDetailAction() {
        return toUserDetailAction;
    }

    public void setToUserDetailAction(Action1 toUserDetailAction) {
        this.toUserDetailAction = toUserDetailAction;
    }


    public FriendsControl(Context mContext, ILayout iLayout) {
        this.mContext = mContext;
        this.otherLayout = iLayout;
    }

    public ILayout getOtherLayout() {
        return otherLayout;
    }

    public void initItem(CompositeSubscription mSubscriptions, RBaseViewHolder holder, FriendBean dataBean) {

    }

    public void init(RelativeLayout rootview,CompositeSubscription mSubscriptions) {

        this.mSubscriptions = mSubscriptions;

        mViewHolder = new RBaseViewHolder(rootview);
        rRecyclerView = mViewHolder.v(R.id.recycler_friend);
        mRefreshLayout = mViewHolder.v(R.id.refresh_layout);
        sidebar_friend = mViewHolder.v(R.id.sidebar_friend_index);

        sidebar_friend.setOnTouchLetterChangeListener(new WaveSideBarView.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                scrollToLetter(letter);
            }
        });

        mFriendsAdapter = new FriendsAdapter(mContext,this){
            @Override
            protected List<? extends AbsFriendItem> onPreProvide() {
                return FuncItem.provide();
            }
        };

        mFriendsAdapter.setSideAction(new Action1<List<String>>() {
            @Override
            public void call(List<String> strings) {
                strings.add(0,"☆");
                sidebar_friend.setLetters(strings);
            }
        });

        mRefreshLayout.setRefreshDirection(RefreshLayout.TOP);
        mRefreshLayout.addRefreshListener(this);
        rRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        rRecyclerView.setAdapter(mFriendsAdapter);

        rRecyclerView.addItemDecoration(new RGroupItemDecoration(new GroupItemCallBack(mContext,mFriendsAdapter)));
    }

    private void scrollToLetter(String letter) {
        if (TextUtils.equals(letter, "#")) {
            ((LinearLayoutManager) rRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 0);
            return;
        }
        for (int i = 0; i < mFriendsAdapter.getAllDatas().size(); i++) {
            if (TextUtils.equals(letter, mFriendsAdapter.getAllDatas().get(i).getGroupText())) {
                ((LinearLayoutManager) rRecyclerView.getLayoutManager()).scrollToPositionWithOffset(i, 0);
                break;
            }
        }
    }


    public static char generateFirstLetter(FriendBean o2) {
        return Pinyin.toPinyin(o2.getDefaultMark().charAt(0)).toUpperCase().charAt(0);
    }

    public void resetData(List<FriendBean> data_list) {
        mFriendsAdapter.reset(data_list);
    }

    public void onUILoadFinish() {
        if(mRefreshLayout != null){
            mRefreshLayout.setRefreshEnd();
        }

    }

    @Override
    public void onRefresh(@RefreshLayout.Direction int direction) {
        if (direction == RefreshLayout.TOP) {
            //refesh
            loadData();
        } else if (direction == RefreshLayout.BOTTOM) {

        }
    }

    public void loadData() {
        mSubscriptions.add(RRetrofit.create(ContactService.class)
                .friends(Param.buildMap("uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(FriendListModel.class))
                .subscribe(new BaseSingleSubscriber<FriendListModel>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        T_.error(code + ":" + msg);
                    }

                    @Override
                    public void onSucceed(FriendListModel bean) {
                        super.onSucceed(bean);
                        if(bean == null || bean.getData_list().size() == 0 ) {
                            return;
                        }

                        List<FriendBean> data_list = bean.getData_list();
                        resetData(data_list);
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        onUILoadFinish();

                    }
                }));
    }


    public static class GroupItemCallBack extends RGroupItemDecoration.SingleGroupCallBack {

        final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        final RectF rectF = new RectF();
        final Rect rect = new Rect();

        private Context mContext;

        private RBaseAdapter<AbsFriendItem> mAdapter;

        public GroupItemCallBack(Context ctx,RBaseAdapter adapter) {
            textPaint.setTextSize(ctx.getResources().getDisplayMetrics().scaledDensity * 20);
            this.mContext = ctx;
            this.mAdapter = adapter;
        }

        @Override
        public int getGroupHeight(int position) {
            return ScreenUtil.dip2px(20);
        }

        @Override
        public String getGroupText(int position) {
            String groupText = mAdapter.getAllDatas().get(position).getGroupText();
            return groupText;
        }

        @Override
        public void onGroupDraw(Canvas canvas, View view, int position) {
            textPaint.setColor(mContext.getResources().getColor(R.color.line_color));

            rectF.set(view.getLeft(), view.getTop() - getGroupHeight(position), view.getRight(), view.getTop());
            canvas.drawRect(rectF,textPaint);
            textPaint.setColor(Color.WHITE);

            final String letter = mAdapter.getAllDatas().get(position).getGroupText();
            textPaint.getTextBounds(letter, 0, letter.length(), rect);

            canvas.drawText(letter, view.getLeft() + ScreenUtil.dip2px(10), view.getTop() - (getGroupHeight(position) - rect.height()) / 2, textPaint);

        }

        @Override
        public void onGroupOverDraw(Canvas canvas, View view, int position, int offset) {
            textPaint.setColor(mContext.getResources().getColor(R.color.line_color));

            rectF.set(view.getLeft(), -offset, view.getRight(), getGroupHeight(position) - offset);
            canvas.drawRect(rectF, textPaint);
            textPaint.setColor(Color.WHITE);

            final String letter = mAdapter.getAllDatas().get(position).getGroupText();
            textPaint.getTextBounds(letter, 0, letter.length(), rect);

            canvas.drawText(letter, view.getLeft() + ScreenUtil.dip2px(10), (getGroupHeight(position) + rect.height()) / 2 - offset, textPaint);

        }
    }

}
