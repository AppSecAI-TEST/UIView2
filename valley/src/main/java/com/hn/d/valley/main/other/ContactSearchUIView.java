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
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.realm.AmapBean;
import com.hn.d.valley.main.message.query.ContactSearch;
import com.hn.d.valley.main.message.query.TextQuery;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
public class ContactSearchUIView extends BaseUIView<IPOISearchPresenter> implements IPOISearchView {

    //view
    ExEditText mSearchInputView;
    TextView mSearchTipView;
    LinearLayout mSearchControlLayout;
    RRecyclerView mRecyclerView;
    TextView mEmptyTipView;

    //adapter
    private ContactSearchAdapter mContactAdapter;

    //action
    private Action1<FriendBean> action;

    //str
    private String query;

    // datas
    private List<FriendBean> datas;

    private int mCurrentPage = 1;

    public ContactSearchUIView(List<FriendBean> datas, Action1<FriendBean> action) {
        this.action = action;
        this.datas = datas;
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


//        bindPresenter(new SearchPOIPresenter());
        mContactAdapter = new ContactSearchAdapter(mActivity, new ContactSearchAdapter.OnContactSelecteListener() {
            @Override
            public void onContactSelected(FriendBean item) {
                //销毁uiview
                finishIView();
                if (action != null) {
                    action.call(item);
                }
            }
        });


        mRecyclerView.setAdapter(mContactAdapter);

        mViewHolder.v(R.id.title_bar_layout).setBackgroundColor(SkinHelper.getSkin().getThemeColor());
        mViewHolder.v(R.id.cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishIView();
            }
        });

        mSearchInputView.setHint(R.string.text_search_nick);

        RSoftInputLayout.showSoftInput(mSearchInputView);

        RxTextView.textChanges(mSearchInputView)
                .debounce(Constant.DEBOUNCE_TIME_700, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .switchMap(new Func1<CharSequence, Observable<List<FriendBean>>>() {
                    @Override
                    public Observable<List<FriendBean>> call(CharSequence charSequence) {
                        List<FriendBean> result = new ArrayList<>();
                        if (datas != null) {
                            for (FriendBean bean : datas) {
                                boolean hit = ContactSearch.hitFriend(bean, new TextQuery(charSequence.toString()), null);
                                if (!hit) {
                                    continue;
                                }
                                result.add(bean);
                            }
                        }
                        return Observable.just(result);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSingleSubscriber<List<FriendBean>>() {
                    @Override
                    public void onSucceed(List<FriendBean> beans) {
                        if (beans == null || beans.size() == 0) {
                            return;
                        }
                        mContactAdapter.resetData(beans);
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

    public void onSearchTipClick() {
//        mPresenter.onSearch(latLng, 1, onPoiSearchListener, query, type);
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
