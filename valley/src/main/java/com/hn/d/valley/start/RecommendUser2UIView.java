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
import com.hn.d.valley.bean.EntityResponse;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.ListModel;
import com.hn.d.valley.bean.UserListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.widget.HnLoading;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText("跳过").setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T_.show("跳过");
            }
        }));

        return super.getTitleBar().setTitleString("推荐").setRightItems(rightItems);
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

//        RRetrofit.create(ContactService.class)
//                .getFollowers(Param.buildMap("uid:" + uid, "page:" + page))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new SingleRSubscriber<EntityResponse<ListModel<LikeUserInfoBean>>>(this) {
//                    @Override
//                    protected void onResult(EntityResponse<ListModel<LikeUserInfoBean>> bean) {
//                        L.i("onresult");
//                        L.i(bean);
//                    }
//                });
    }

    private void initViewHolder(RBaseViewHolder holder) {
        final TextView tv_focus = holder.tv(R.id.tv_focus);
        RRecyclerView rRecyclerView = holder.v(R.id.recycler_view);
        initRecyclerView(rRecyclerView);
        tv_focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HnLoading.show(mILayout);

                focusRecommendUser(mUserAdapter.getAllDatas());

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
//        add(RRetrofit.create(ContactService.class)
//        .attentionn(Param.buildMap("uid:" + uid,"to_uid:" + allDatas.get(0).getUid(),"source:" + 1))
//        .)

        RRetrofit.create(ContactService.class)
                .attention(Integer.valueOf(uid),Integer.valueOf(allDatas.get(0).getUid()),1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RSubscriber<EntityResponse<Integer>>() {
                    @Override
                    public void onSucceed(EntityResponse<Integer> bean) {
                        super.onSucceed(bean);
                        L.i("focusRecommendUser : " + bean.getData());
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }
                });
    }

    private void initRecyclerView(RRecyclerView rRecyclerView) {
        ViewGroup.LayoutParams layoutParams =  rRecyclerView.getLayoutParams();
        layoutParams.width = ScreenUtil.screenWidth;
        itemHeight = layoutParams.width / 3;
        L.i("init itemHeight : " + itemHeight);

        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(10);
        mUserAdapter = new RecommendUserUIView.RecommendUserAdapter(mActivity,itemHeight);
        rRecyclerView.addItemDecoration(itemDecoration);
        //禁止RecyclerView 上下拖动阴影
        rRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rRecyclerView.setLayoutManager(new GridLayoutManager(mActivity,3));
        rRecyclerView.setAdapter(mUserAdapter);

        add(RRetrofit.create(ContactService.class)
                .followers(Param.buildMap("uid:" + uid, "page:" + page))
                .compose(Rx.transformer(UserListModel.class))
                .subscribe(new SingleRSubscriber<UserListModel>(this) {
                    @Override
                    protected void onResult(UserListModel bean) {
                        if (bean == null || bean.getData_list() == null || bean.getData_list().isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            for (LikeUserInfoBean b : bean.getData_list()) {
                                b.setIs_attention(1);
                            }
                            onRecommendUserLoadEnd(bean.getData_list());
                        }
                    }
                }));
    }

    public void onRecommendUserLoadEnd(List<LikeUserInfoBean> data_list) {
        mUserAdapter.resetData(data_list);
        L.i(data_list);
    }
}
