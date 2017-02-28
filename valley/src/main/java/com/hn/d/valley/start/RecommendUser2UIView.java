package com.hn.d.valley.start;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.utils.Preconditions;
import com.hn.d.valley.widget.HnLoading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/2/27.
 */

public class RecommendUser2UIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo>{

    public RecommendUser2UIView(){
        this.uid = UserCache.getUserAccount();
    }

    private int itemHeight;

    private RecommendUserUIView.RecommendUserAdapter mUserAdapter;

    private String uid;

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText("跳过").setTextColor(mActivity.getResources().getColor(R.color.main_text_color_6666666))
                .setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T_.show("跳过");
            }
        }));


        return super.getTitleBar().setRightItems(rightItems)
                .setTitleBarBGColor(mActivity.getResources().getColor(R.color.white));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.view_start_recommenduser;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                initViewHolder(holder);
            }

            @Override
            public void setItemOffsets(Rect rect) {
                super.setItemOffsets(rect);
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                super.draw(canvas, paint, itemView, offsetRect, itemCount, position);
            }
        }));
    }

    @Override
    protected void onUILoadData(String page) {
    }

    @Override
    public int getDefaultBackgroundColor() {
        return mActivity.getResources().getColor(R.color.white);
    }

    private void initViewHolder(RBaseViewHolder holder) {
        final TextView tv_focus = holder.tv(R.id.tv_focus);
        RRecyclerView rRecyclerView = holder.v(R.id.recycler_view);
        initRecyclerView(rRecyclerView);
        tv_focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HnLoading.show(mILayout);
                focusRecommendUser(mUserAdapter.getSelectorData());
                tv_focus.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        HnLoading.hide();
                    }
                },3000);
            }
        });
    }

    private void focusRecommendUser(List<LikeUserInfoBean> allDatas) {
        Observable.from(allDatas)
                .map(new Func1<LikeUserInfoBean, String>() {
                    @Override
                    public String call(LikeUserInfoBean likeUserInfoBean) {
                        add(RRetrofit.create(UserInfoService.class)
                                .attention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + likeUserInfoBean.getUid()))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {
                                    @Override
                                    public void onSucceed(String bean) {
                                        T_.show(bean);
                                        L.i(bean);
                                    }
                                }));
                        return likeUserInfoBean.getUid();

                    }
                }).subscribe(new RSubscriber<String>() {
            @Override
            public void onSucceed(String bean) {

                super.onSucceed(bean);
            }
        });

    }

    private void initRecyclerView(RRecyclerView rRecyclerView) {
        ViewGroup.LayoutParams layoutParams =  rRecyclerView.getLayoutParams();
        layoutParams.width = ScreenUtil.screenWidth;
        itemHeight = layoutParams.width / 3;
        L.i("init itemHeight : " + itemHeight);

        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(10);
        mUserAdapter = new RecommendUserUIView.RecommendUserAdapter(mActivity,itemHeight,rRecyclerView);
        rRecyclerView.addItemDecoration(itemDecoration);
        //禁止RecyclerView 上下拖动阴影
        rRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rRecyclerView.setLayoutManager(new GridLayoutManager(mActivity,3));
        rRecyclerView.setAdapter(mUserAdapter);

        loadAllRecommendUserData();
    }

    private void loadAllRecommendUserData() {
        add(RRetrofit.create(ContactService.class)
                .followers(Param.buildMap("uid:" + uid, "page:" + page))
                .compose(Rx.transformer(UserListModel.class))
                .subscribe(new SingleRSubscriber<UserListModel>(this) {
                    @Override
                    protected void onResult(UserListModel bean) {
                        if (bean == null || bean.getData_list() == null || bean.getData_list().isEmpty()) {
                            onUILoadDataEnd();
                            showEmptyLayout();
                        } else {
                            for (LikeUserInfoBean b : bean.getData_list()) {
                                b.setIs_attention(1);
                        }
                            onRecommendUserLoadEnd(bean.getData_list());
                        }
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                        showNonetLayout(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadAllRecommendUserData();
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }
                }));
    }

    public void onRecommendUserLoadEnd(List<LikeUserInfoBean> data_list) {
        Preconditions.checkNotNull(data_list);
        mUserAdapter.resetData(data_list);
        L.i(data_list);
    }
}
