package com.hn.d.valley.main.message.groupchat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RGroupItemDecoration;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.file.AttachmentStore;
import com.angcyo.uiview.utils.storage.StorageType;
import com.angcyo.uiview.utils.storage.StorageUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.GroupInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.friend.groupchat.AddGroupAdapter;
import com.hn.d.valley.main.friend.groupchat.AddGroupDatatProvider;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.utils.NetUtils;
import com.hn.d.valley.widget.HnIcoRecyclerView;
import com.hn.d.valley.widget.HnLoading;
import com.hn.d.valley.widget.HnRefreshLayout;
import com.hn.d.valley.widget.groupView.JoinBitmaps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hewking on 2017/3/9.
 */
public class AddGroupChatUIView extends BaseUIView {

    @BindView(R.id.friend_add_refreshlayout)
    HnRefreshLayout refreshLayout;
    @BindView(R.id.friend_add_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.rv_groupchat_icon)
    HnIcoRecyclerView iconSelectedRv;

    private AddGroupAdapter mGroupAdapter;

    private AddGroupDatatProvider datatProvider;

    private Action2<Boolean,FriendBean> action = new Action2<Boolean, FriendBean>() {
        @Override
        public void call(Boolean aBoolean, FriendBean bean) {
            HnIcoRecyclerView.IcoInfo icon ;
            if (aBoolean) {
                icon = new HnIcoRecyclerView.IcoInfo(bean.getUid(),bean.getAvatar());
                iconSelectedRv.getMaxAdapter().addLastItem(icon);
            }else {
                iconSelectedRv.remove(bean.getAvatar());
            }
        }
    };

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText("确定").setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T_.show("创建群聊");
                createGroupChat();
            }
        }));
        return super.getTitleBar().setTitleString("选择好友").setRightItems(rightItems);
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_friend_addgroupchat);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        init();

    }

    private void init() {
        mGroupAdapter = new AddGroupAdapter(mActivity);
        datatProvider = new AddGroupDatatProvider();
        mGroupAdapter.setAction(action);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(mActivity.getResources().getDisplayMetrics().scaledDensity * 20);
        final RectF rectF = new RectF();
        final Rect rect = new Rect();

        recyclerView.addItemDecoration(new RGroupItemDecoration(new RGroupItemDecoration.GroupCallBack() {
            @Override
            public int getGroupHeight() {
                return ScreenUtil.dip2px(20);
            }

            @Override
            public String getGroupText(int position) {
                String groupText = mGroupAdapter.getAllDatas().get(position).getDefaultMark();
                return groupText;
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onGroupDraw(Canvas canvas, View view, int position) {
                textPaint.setColor(mActivity.getColor(R.color.line_color));

                rectF.set(view.getLeft(), view.getTop() - getGroupHeight(), view.getRight(), view.getTop());
                canvas.drawRoundRect(rectF, ScreenUtil.dip2px(2), ScreenUtil.dip2px(2), textPaint);
                textPaint.setColor(Color.WHITE);

                final String letter = String.valueOf(FriendsControl.generateFirstLetter(mGroupAdapter.getAllDatas().get(position)));
                textPaint.getTextBounds(letter, 0, letter.length(), rect);

                canvas.drawText(letter, view.getLeft() + ScreenUtil.dip2px(10), view.getTop() - (getGroupHeight() - rect.height()) / 2, textPaint);

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onGroupOverDraw(Canvas canvas, View view, int position, int offset) {
                textPaint.setColor(mActivity.getColor(R.color.line_color));

                rectF.set(view.getLeft(), -offset, view.getRight(), getGroupHeight() - offset);
                canvas.drawRoundRect(rectF, ScreenUtil.dip2px(2), ScreenUtil.dip2px(2), textPaint);
                textPaint.setColor(Color.WHITE);

                final String letter = String.valueOf(FriendsControl.generateFirstLetter(mGroupAdapter.getAllDatas().get(position)));
                textPaint.getTextBounds(letter, 0, letter.length(), rect);

                canvas.drawText(letter, view.getLeft() + ScreenUtil.dip2px(10), (getGroupHeight() + rect.height()) / 2 - offset, textPaint);

            }
        }));

        recyclerView.setAdapter(mGroupAdapter);

        datatProvider.provide(mSubscriptions, new Action1<List<FriendBean>>() {
            @Override
            public void call(List<FriendBean> beanList) {
                mGroupAdapter.resetData(beanList);
            }
        });
    }


    private void createGroupChat() {
        List<FriendBean> selectorData = mGroupAdapter.getSelectorData();
        add(RRetrofit.create(GroupChatService.class)
                .add(Param.buildMap("uid:" + UserCache.getUserAccount()
                        , "to_uid:" + RUtils.connect(selectorData)
                        ,"avatar:" + "http:\\/\\/circleimg.klgwl.com\\/77500371484917281.776834"))
                .compose(Rx.transformer(GroupInfoBean.class))
                .subscribe(new BaseSingleSubscriber<GroupInfoBean>() {
                    @Override
                    public void onSucceed(GroupInfoBean bean) {
                        T_.show(bean.getGroupAvatar());
                    }
                }));
        if (selectorData == null || selectorData.size() == 0) {
            return;
        }
        createAndSavePhoto(selectorData);
    }

    private void createAndSavePhoto(final List<FriendBean> selectorData) {
        HnLoading.show(mOtherILayout);
        add(Observable.just(selectorData)
                .flatMap(new Func1<List<FriendBean>, Observable<List<String>>>() {
                    @Override
                    public Observable<List<String>> call(List<FriendBean> beanList) {
                        List<String> urls = new ArrayList<>();
                        for(FriendBean bean : beanList){
                            urls.add(bean.getAvatar());
                        }
                        return Observable.just(urls);
                    }
                })
                .flatMap(new Func1<List<String>, Observable<List<Bitmap>>>() {
                    @Override
                    public Observable<List<Bitmap>> call(List<String> s) {
                        List<Bitmap> bitmaps = new ArrayList<>();
                        for(String url : s ) {
                            Bitmap bitmap = NetUtils.createBitmapFromUrl(url);
                            if(bitmap == null) {
                                continue;
                            }
                            bitmaps.add(bitmap);
                        }
                        return Observable.just(bitmaps);
                    }
                }).map(new Func1<List<Bitmap>, String>() {
                    @Override
                    public String call(List<Bitmap> bitmaps) {
                        String filePath = StorageUtil.getDirectoryByDirType(StorageType.TYPE_IMAGE) +"/avatar2.png";
                        //ios 目前大小为 40
                        AttachmentStore.saveBitmap(JoinBitmaps.createGroupBitCircle(bitmaps,40,40,mActivity),filePath,true);

                        File file = new File(filePath);
                        if(!file.exists()) {
                            return null;
                        }

                        return filePath;
                    }
                 })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return OssHelper.uploadAvatorImg(s);
                    }
                })
                .flatMap(new Func1<String, Observable<GroupInfoBean>>() {
                    @Override
                    public Observable<GroupInfoBean> call(String s) {
                        String circleUrl = OssHelper.getAvatorUrl(s);
                        return RRetrofit.create(GroupChatService.class)
                                .add(Param.buildMap("uid:" + UserCache.getUserAccount()
                                        , "to_uid:" + RUtils.connect(selectorData)
                                        ,"avatar:" + circleUrl))
                                .compose(Rx.transformer(GroupInfoBean.class));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSingleSubscriber<GroupInfoBean>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        HnLoading.hide();
                    }

                    @Override
                    public void onSucceed(GroupInfoBean s) {
                        HnLoading.hide();
                        AddGroupChatUIView.this.finishIView();
                        T_.show(s.getGroupAvatar());
                    }
                }));

    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }
}
