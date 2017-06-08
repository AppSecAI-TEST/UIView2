package com.hn.d.valley.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.L;
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
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.FriendsAdapter;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.main.friend.SystemPushItem;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.message.search.GlobalSearchUIView2;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.ContactService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
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

    private ILayout otherLayout;

    private Context mContext;

    private Action1<FriendBean> toUserDetailAction;

    private RequestCallback callback;

    private CompositeSubscription mSubscriptions;


    public Action1 getToUserDetailAction() {
        return toUserDetailAction;
    }

    public void setToUserDetailAction(Action1 toUserDetailAction) {
        this.toUserDetailAction = toUserDetailAction;
    }


    public FriendsControl(Context mContext,CompositeSubscription mSubscriptions, ILayout iLayout, RequestCallback callback) {
        this.mContext = mContext;
        this.otherLayout = iLayout;
        this.callback = callback;
        this.mSubscriptions = mSubscriptions;

    }

    public ILayout getOtherLayout() {
        return otherLayout;
    }

    public void initItem(CompositeSubscription mSubscriptions, RBaseViewHolder holder, FriendBean dataBean) {

    }

    public void init(RelativeLayout rootview) {

        mViewHolder = new RBaseViewHolder(rootview);
        rRecyclerView = mViewHolder.v(R.id.recycler_friend);
        mRefreshLayout = mViewHolder.v(R.id.refresh_layout);
        sidebar_friend = mViewHolder.v(R.id.sidebar_friend_index);

        mFriendsAdapter = new FriendsAdapter(mContext,this){
            @Override
            protected List<? extends AbsContactItem> onPreProvide() {

                FriendBean bean = new FriendBean();
                bean.setDefaultMark("恐龙君");
                bean.setUid("13");
                bean.setAvatar("http://avatorimg.klgwl.com/13/13915.png");

                SystemPushItem item = new SystemPushItem(bean, new Action1<FriendBean>() {
                    @Override
                    public void call(FriendBean o) {
                        SessionHelper.startSession(getOtherLayout(),o.getUid(),SessionTypeEnum.P2P);
                    }
                });
                List<AbsContactItem> items = FuncItem.provide();
                items.add(item);

                items.add(new FuncItem<>("搜索", ItemTypes.SEARCH, new Action1<ILayout>() {
                    @Override
                    public void call(ILayout o) {
//                        getOtherLayout().startIView(new SearchUserUIView());
                        GlobalSearchUIView2.start(getOtherLayout(), GlobalSearchUIView2.Options.sOptions,new int[]{ItemTypes.FRIEND,ItemTypes.GROUP,ItemTypes.MSG,});

                    }
                }));

                return items;
            }
        };

        mFriendsAdapter.setSideAction(new Action1<List<String>>() {
            @Override
            public void call(List<String> strings) {
                strings.add(0,"↑");
                sidebar_friend.setLetters(strings);
            }
        });

        mRefreshLayout.setRefreshDirection(RefreshLayout.TOP);
        mRefreshLayout.addOnRefreshListener(this);
        rRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        rRecyclerView.setAdapter(mFriendsAdapter);

//        mFriendsAdapter.reset(null);

        sidebar_friend.setOnTouchLetterChangeListener(new WaveSideBarView.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                scrollToLetter(letter,rRecyclerView,mFriendsAdapter.getAllDatas());
            }
        });

        rRecyclerView.addItemDecoration(new RGroupItemDecoration(new GroupItemCallBack(mContext,mFriendsAdapter)));
    }

    public static void scrollToLetter(String letter, RecyclerView recyclerView, List<AbsContactItem> datas) {
        if (TextUtils.equals(letter, "↑")) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 0);
            return;
        }
        for (int i = 0; i < datas.size(); i++) {
            if (TextUtils.equals(letter, datas.get(i).getGroupText())) {
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(i, 0);
                break;
            }
        }
    }


    public static char generateFirstLetter(FriendBean o2) {
        return Pinyin.toPinyin(o2.getTrueName().charAt(0)).toUpperCase().charAt(0);
    }

    public static List<String> generateIndexLetter(List<AbsContactItem> data_list) {
        List<String> letters= new ArrayList<>();
        for(AbsContactItem bean : data_list) {
            String letter = bean.getGroupText();
            if (letter == null || "".equals(letter)) {
                continue;
            }
            if(!letters.contains(letter)){
                letters.add(letter);
            }
        }
        return letters;
    }

    public static void sort(List<AbsContactItem> items) {
        Collections.sort(items, new Comparator<AbsContactItem>() {
            @Override
            public int compare(AbsContactItem o1, AbsContactItem o2) {

                if (o1.getGroupText().equals("")) {
                    return -1;
                }

                if (o2.getGroupText().equals("")) {
                    return 1;
                }

                if (o1.getGroupText().equals("☆")) {
                    return -1;
                }

                if (o2.getGroupText().equals("☆")) {
                    return 1;
                }

                if (o1.getGroupText().equals("#")) {
                    return 1;
                }

                if (o2.getGroupText().equals("#")) {
                    return -1;
                }

                return o1.getGroupText().charAt(0) - o2.getGroupText().charAt(0);
            }
        });
    }

    public void resetData(List<FriendBean> data_list) {
        mFriendsAdapter.reset(data_list);
    }

    public void onUILoadFinish() {

        if (callback != null) {
            callback.onSuccess("");
            L.i(" onUILoadFinish friends onsucceed");
            if(mRefreshLayout != null){
                mRefreshLayout.setRefreshEnd();
            }
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
        mSubscriptions.add(RRetrofit.create(ContactService.class, RRetrofit.CacheType.MAX_AGE)
                .friends(Param.buildMap("uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(FriendListModel.class))
                .subscribe(new BaseSingleSubscriber<FriendListModel>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        callback.onStart();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        T_.error(code + ":" + msg);
                    }

                    @Override
                    public void onSucceed(FriendListModel bean) {
                        super.onSucceed(bean);
                        if(bean == null || bean.getData_list().size() == 0 ) {
                            onUILoadFinish();
                            if (bean == null) {
                                resetData(new ArrayList<FriendBean>());
                            } else {
                                resetData(bean.getData_list());
                            }
                            return;
                        }
                        onUILoadFinish();
                        List<FriendBean> data_list = bean.getData_list();

                        saveToRealm(data_list);

                        resetData(data_list);
                        L.i("friends onsucceed");
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();

                    }
                }));
    }

    private void saveToRealm(final List<FriendBean> data_list) {
        final RealmResults<FriendBean> results = RRealm.realm().where(FriendBean.class).findAll();

        RRealm.exe(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
                for(FriendBean bean : data_list) {
                    realm.copyToRealm(bean);
                }
            }
        });
    }


    public static class GroupItemCallBack extends RGroupItemDecoration.SingleGroupCallBack {

        final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        final RectF rectF = new RectF();
        final Rect rect = new Rect();

        private Context mContext;

        private RBaseAdapter<AbsContactItem> mAdapter;

        public GroupItemCallBack(Context ctx,RBaseAdapter adapter) {
            textPaint.setTextSize(ctx.getResources().getDisplayMetrics().scaledDensity * 14);
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
            textPaint.setColor(mContext.getResources().getColor(R.color.chat_bg_color));

            rectF.set(view.getLeft(), view.getTop() - getGroupHeight(position), view.getRight(), view.getTop());
            canvas.drawRect(rectF,textPaint);
            textPaint.setColor(mContext.getResources().getColor(R.color.main_text_color_dark));

            final String letter = mAdapter.getAllDatas().get(position).getGroupText();
            textPaint.getTextBounds(letter, 0, letter.length(), rect);

            canvas.drawText(letter, view.getLeft() + ScreenUtil.dip2px(10), view.getTop() - (getGroupHeight(position) - rect.height()) / 2, textPaint);

        }

        @Override
        public void onGroupOverDraw(Canvas canvas, View view, int position, int offset) {
            textPaint.setColor(mContext.getResources().getColor(R.color.chat_bg_color));

            rectF.set(view.getLeft(), -offset, view.getRight(), getGroupHeight(position) - offset);
            canvas.drawRect(rectF, textPaint);
            textPaint.setColor(mContext.getResources().getColor(R.color.main_text_color_dark));

            final String letter = mAdapter.getAllDatas().get(position).getGroupText();
            textPaint.getTextBounds(letter, 0, letter.length(), rect);

            canvas.drawText(letter, view.getLeft() + ScreenUtil.dip2px(10), (getGroupHeight(position) + rect.height()) / 2 - offset, textPaint);

        }

        @Override
        public void onItemOffsets(Rect outRect, int position) {
            super.onItemOffsets(outRect, position);
//            outRect.bottom = ScreenUtil.px2dip(1);
            outRect.bottom = mContext.getResources().getDimensionPixelSize(R.dimen.base_line);
        }

        @Override
        public void onItemDraw(Canvas canvas, View view, int position) {
            super.onItemDraw(canvas, view, position);
            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(mContext,R.color.chat_bg_color));
            int left = ScreenUtil.dip2px(55);
            int dividerSize = mContext.getResources().getDimensionPixelSize(R.dimen.base_line);
            colorDrawable.setBounds(view.getLeft() + left,view.getBottom(),view.getRight() - ScreenUtil.dip2px(10),view.getBottom() + dividerSize /*ScreenUtil.dip2px(1)*/);
            colorDrawable.draw(canvas);

        }
    }

}
