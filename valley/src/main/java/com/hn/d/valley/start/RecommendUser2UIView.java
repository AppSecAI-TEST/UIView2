package com.hn.d.valley.start;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.ScreenUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.activity.HnUIMainActivity;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.RecommendUserBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.start.service.StartService;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.utils.Preconditions;
import com.hn.d.valley.widget.HnLoading;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hewking on 2017/2/27.
 */

public class RecommendUser2UIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private int itemHeight;
    private RecommendUserUIView.RecommendUserAdapter mUserAdapter;
    private String uid;
    private TextView tv_focus;

    public RecommendUser2UIView() {
        this.uid = UserCache.getUserAccount();
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.jump)).setVisibility(View.GONE)
                .setTextColor(mActivity.getResources().getColor(R.color.main_text_color_6666666))
                .setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HnUIMainActivity.launcher(mActivity);
                        mActivity.finish();
                    }
                }));
        return super.getTitleBar().setRightItems(rightItems).setTitleHide(true)
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
        loadAllRecommendUserData();
    }

    @Override
    public int getDefaultBackgroundColor() {
        return mActivity.getResources().getColor(R.color.white);
    }

    private void initViewHolder(RBaseViewHolder holder) {
        tv_focus = holder.tv(R.id.tv_focus);
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
                }, 3000);
            }
        });
    }

    private void focusRecommendUser(List<RecommendUserBean> allDatas) {

        add(RRetrofit.create(UserInfoService.class)
                .attentionBatch(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + RUtils.connect(allDatas)))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        HnUIMainActivity.launcher(mActivity);
                        mActivity.finish();
                    }
                }));

//        Observable.from(allDatas)
//                .map(new Func1<RecommendUserBean, String>() {
//                    @Override
//                    public String call(RecommendUserBean likeUserInfoBean) {
//                        add(RRetrofit.create(UserInfoService.class)
//                                .attention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + likeUserInfoBean.getUid()))
//                                .compose(Rx.transformer(String.class))
//                                .subscribe(new BaseSingleSubscriber<String>() {
//                                    @Override
//                                    public void onSucceed(String bean) {
//                                        T_.show(bean);
//                                        L.i(bean);
//                                    }
//                                }));
//                        return likeUserInfoBean.getUid();
//
//                    }
//                }).subscribe(new RSubscriber<String>() {
//            @Override
//            public void onSucceed(String bean) {
//
//                super.onSucceed(bean);
//            }
//        });

    }

    private void initRecyclerView(RRecyclerView rRecyclerView) {
        ViewGroup.LayoutParams layoutParams = rRecyclerView.getLayoutParams();
        layoutParams.width = ScreenUtil.screenWidth;
        itemHeight = layoutParams.width / 3;
        L.i("init itemHeight : " + itemHeight);

        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(10);
        mUserAdapter = new RecommendUserUIView.RecommendUserAdapter(mActivity, itemHeight, rRecyclerView);
        mUserAdapter.setSelectListener(new RecommendUserUIView.OnUserSelectListener() {
            @Override
            public void onSelect(boolean boo) {
                if (tv_focus == null)
                    return;
                tv_focus.setEnabled(boo);
            }
        });

        rRecyclerView.addItemDecoration(itemDecoration);
        //禁止RecyclerView 上下拖动阴影
        rRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        rRecyclerView.setAdapter(mUserAdapter);
    }

    private void loadAllRecommendUserData() {
        add(RRetrofit.create(StartService.class)
                .interestUser(Param.buildMap("uid:" + uid))
                .compose(Rx.transformerList(RecommendUserBean.class))
                .subscribe(new BaseSingleSubscriber<List<RecommendUserBean>>() {
                    @Override
                    public void onSucceed(List<RecommendUserBean> bean) {
                        if (bean == null || bean.isEmpty()) {
                            onUILoadDataEnd();
                            showEmptyLayout();
                        } else {
                            //显示titlebar 右边text
                            getUITitleBarContainer().showRightItem(0);
                            showContentLayout();
                            onRecommendUserLoadEnd(bean);
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
                        showEmptyLayout();
                    }
                }));
    }

    public void onRecommendUserLoadEnd(final List<RecommendUserBean> data_list) {
        if (mUserAdapter == null) {
            post(new Runnable() {
                @Override
                public void run() {
                    onRecommendUserLoadEnd(data_list);
                }
            });
            return;
        }
        Preconditions.checkNotNull(data_list);
        mUserAdapter.resetData(data_list);
        L.i(data_list);
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }
}
