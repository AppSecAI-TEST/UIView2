package com.hn.d.valley.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.github.WaveSideBarView;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RGroupItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
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
    Context mContext;

    Action1<FriendBean> toUserDetailAction;

    public Action1 getToUserDetailAction() {
        return toUserDetailAction;
    }

    public void setToUserDetailAction(Action1 toUserDetailAction) {
        this.toUserDetailAction = toUserDetailAction;
    }

    private CompositeSubscription mSubscriptions;

    public FriendsControl(Context mContext) {
        this.mContext = mContext;
    }

    public void initItem(CompositeSubscription mSubscriptions, RBaseViewHolder holder, FriendBean dataBean) {

    }

    public void init(RelativeLayout rootview,CompositeSubscription mSubscriptions) {

//        characterParser = CharacterParser.getInstance();
//        pinyinComparator = PinyinComparator.getInstance();
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

        mRefreshLayout.setRefreshDirection(RefreshLayout.TOP);
        mRefreshLayout.addRefreshListener(this);
        rRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        rRecyclerView.setAdapter(mFriendsAdapter);

        final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(mContext.getResources().getDisplayMetrics().scaledDensity * 20);
        final RectF rectF = new RectF();
        final Rect rect = new Rect();

        rRecyclerView.addItemDecoration(new RGroupItemDecoration(new RGroupItemDecoration.GroupCallBack() {
            @Override
            public int getGroupHeight() {
                return ScreenUtil.dip2px(20);
            }

            @Override
            public String getGroupText(int position) {
                String groupText = mFriendsAdapter.getAllDatas().get(position).getGroupText();
                return groupText;
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onGroupDraw(Canvas canvas, View view, int position) {
                textPaint.setColor(mContext.getColor(R.color.line_color));

                rectF.set(view.getLeft(), view.getTop() - getGroupHeight(), view.getRight(), view.getTop());
                canvas.drawRect(rectF,textPaint);
                textPaint.setColor(Color.WHITE);

                final String letter = mFriendsAdapter.getAllDatas().get(position).getGroupText();
                textPaint.getTextBounds(letter, 0, letter.length(), rect);

                canvas.drawText(letter, view.getLeft() + ScreenUtil.dip2px(10), view.getTop() - (getGroupHeight() - rect.height()) / 2, textPaint);

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onGroupOverDraw(Canvas canvas, View view, int position, int offset) {
                textPaint.setColor(mContext.getColor(R.color.line_color));

                rectF.set(view.getLeft(), -offset, view.getRight(), getGroupHeight() - offset);
                canvas.drawRect(rectF, textPaint);
                textPaint.setColor(Color.WHITE);

                final String letter = mFriendsAdapter.getAllDatas().get(position).getGroupText();
                textPaint.getTextBounds(letter, 0, letter.length(), rect);

                canvas.drawText(letter, view.getLeft() + ScreenUtil.dip2px(10), (getGroupHeight() + rect.height()) / 2 - offset, textPaint);

            }
        }));
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


    public  static List<FriendBean> sort(List<FriendBean> list) {
        Collections.sort(list, new Comparator<FriendBean>() {
            @Override
            public int compare(FriendBean o1, FriendBean o2) {
                return generateFirstLetter(o1)
                        - generateFirstLetter(o2);
            }
        });
        return list;
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
                        generateIndexLetter(sort(data_list));
                        resetData(data_list);
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        onUILoadFinish();

                    }
                }));
    }

    private void generateIndexLetter(List<FriendBean> data_list) {
        List<String> letters= new ArrayList<>();
        for(FriendBean bean : data_list) {
            String letter = String.valueOf(generateFirstLetter(bean));
            if(!letters.contains(letter)){
                letters.add(letter);
            }
        }
        sidebar_friend.setLetters(letters);
    }
}
