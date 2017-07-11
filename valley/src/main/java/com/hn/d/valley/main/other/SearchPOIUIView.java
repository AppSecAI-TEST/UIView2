package com.hn.d.valley.main.other;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.realm.AmapBean;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.hn.d.valley.control.AmapControl.POI_TYPE;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：搜索添加好友
 * 创建人员：Robi
 * 创建时间：2016/12/26 9:04
 * 修改人员：Robi
 * 修改时间：2016/12/26 9:04
 * 修改备注：
 * Version: 1.0.0
 */
public class SearchPOIUIView extends BaseUIView<IPOISearchPresenter> implements IPOISearchView {

    //view
    ExEditText mSearchInputView;
    TextView mSearchTipView;
    LinearLayout mSearchControlLayout;
    RRecyclerView mRecyclerView;
    TextView mEmptyTipView;

    //adapter
    private PoiItemAdapter mPoiAdapter;

    //latlng
    private LatLng latLng;

    AmapBean mLastBean, mTargetBean;

    //listener
    private PoiSearch.OnPoiSearchListener onPoiSearchListener;

    //action
    private Action1<AmapBean> action;

    //str
    private String query;
    private String type = POI_TYPE;

    private int mCurrentPage = 1;

    public SearchPOIUIView(Action1<AmapBean> action,LatLng latLng){
        this.latLng = latLng;
        this.action = action;
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_search_layout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    public Animation loadLayoutAnimation() {
        return null;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mSearchInputView = v(R.id.edit_text_view);
        mSearchTipView = v(R.id.search_tip_view);
        mSearchControlLayout = v(R.id.search_control_layout);
        mRecyclerView = v(R.id.recycler_view);
        mEmptyTipView = v(R.id.empty_tip_view);

        click(R.id.search_tip_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchTipClick();
            }
        });


        bindPresenter(new SearchPOIPresenter());
        mPoiAdapter = new PoiItemAdapter(mActivity, new PoiItemAdapter.OnPoiItemListener() {
            @Override
            public void onPoiItemSelector(com.amap.api.services.core.PoiItem item) {
                // 选中
                AmapBean amapBean = new AmapBean();
                LatLonPoint latLonPoint = item.getLatLonPoint();

                amapBean.title = item.getTitle();
                amapBean.latitude = latLonPoint.getLatitude();
                amapBean.longitude = latLonPoint.getLongitude();
                amapBean.address = PoiItemAdapter.getAddress(item);
                amapBean.city = item.getCityName();
                amapBean.province = item.getProvinceName();
                amapBean.district = item.getAdName();
                mTargetBean = amapBean;

                //销毁uiview
                finishIView();
                // 销毁ampauiview
                mILayout.finishIView(AmapUIView.class);
                if (action != null) {
                    action.call(mTargetBean);
                }
            }

            @Override
            public void onPoiLoadMore() {
                mCurrentPage ++;
                mPresenter.onSearch(latLng,mCurrentPage,onPoiSearchListener,query,type);
            }
        });

        mPoiAdapter.setEnableLoadMore(false);
        onPoiSearchListener = new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int code) {
                if (code != 1000) {
                    return;
                }

                if (mCurrentPage == 1) {
                    mPoiAdapter.unSelectorAll(false);
                    mPoiAdapter.resetData(poiResult.getPois());
                } else {
                    mPoiAdapter.appendData(poiResult.getPois());
                }

                mPoiAdapter.setLoadMoreEnd();

                mEmptyTipView.setVisibility(View.GONE);
                if (poiResult.getPois().size() == 0) {
                    mPoiAdapter.setEnableLoadMore(false);
                    mEmptyTipView.setVisibility(View.VISIBLE);
                    mEmptyTipView.setText("该地点不存在");
                }
            }

            @Override
            public void onPoiItemSearched(com.amap.api.services.core.PoiItem poiItem, int i) {

            }
        };

        mRecyclerView.setAdapter(mPoiAdapter);

        mViewHolder.v(R.id.title_bar_layout).setBackgroundColor(SkinHelper.getSkin().getThemeColor());
        mViewHolder.v(R.id.cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishIView();
            }
        });

        mSearchInputView.setHint(R.string.text_search_place);

        RSoftInputLayout.showSoftInput(mSearchInputView);

        RxTextView.textChanges(mSearchInputView)
                .debounce(Constant.DEBOUNCE_TIME_700, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        if (TextUtils.isEmpty(charSequence)) {
                            mEmptyTipView.setText("");
                        } else {
                            query = charSequence.toString();
                            mCurrentPage = 1;
                            mPresenter.onSearch(latLng,mCurrentPage,onPoiSearchListener,charSequence.toString(),type);
                        }
                    }
                });
    }

    @Override
    public int getDefaultBackgroundColor() {
        return mActivity.getResources().getColor(R.color.default_base_bg_dark2);
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }



    @Override
    public void onRequestStart() {
        super.onRequestStart();
        mEmptyTipView.setText("");
    }

    /**
     * 开始搜索地点
     */
    public void onSearchTipClick() {
        mPresenter.onSearch(latLng,1,onPoiSearchListener,query,type);
    }


//    @Override
//    public void onSearchSuccess(@NotNull List<? extends PoiItem> result) {
//        if (bean == null) {
//            mEmptyTipView.setText("该地点不存在");
//        } else {
//            mEmptyTipView.setText("");
//        }
//    }
}
