package com.hn.d.valley.base.dialog;

import android.support.annotation.CallSuper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.github.pickerview.adapter.ArrayWheelAdapter;
import com.angcyo.uiview.github.pickerview.lib.WheelView;
import com.angcyo.uiview.github.pickerview.listener.OnItemSelectedListener;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.ProvinceBean;
import com.hn.d.valley.service.AreaService;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action2;
import rx.subscriptions.CompositeSubscription;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/02/22 11:09
 * 修改人员：Robi
 * 修改时间：2017/02/22 11:09
 * 修改备注：
 * Version: 1.0.0
 */
public class CityDialog extends UIIDialogImpl {

    protected CompositeSubscription mSubscriptions;
    WheelView mWheelView1, mWheelView2;
    View mSubmitView;

    //当前的id
    String province_id, city_id;
    /*选择后的id*/
    String province_id_selector, city_id_selector;
    String pName, cName;
    boolean isFirstLoad = true;
    Action2<ProvinceBean, ProvinceBean> mProvinceBeanAction2;
    ProvinceBean mProvinceBean1, mProvinceBean2;
    private Subscription mCitiesSubscribe;
    private OnItemSelectedListener mOnCitiesListener;
    private OnItemSelectedListener mProvinceListener;

    public CityDialog(String province_id, String city_id, Action2<ProvinceBean, ProvinceBean> provinceBeanAction2) {
        this.city_id = city_id;
        this.province_id = province_id;
        this.city_id_selector = city_id;
        this.province_id_selector = province_id;
        mProvinceBeanAction2 = provinceBeanAction2;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        return inflate(com.angcyo.uiview.R.layout.pickerview_options);
    }

    @Override
    protected void initDialogContentView() {
        super.initDialogContentView();
        mWheelView1 = mViewHolder.v(R.id.options1);
        mWheelView2 = mViewHolder.v(R.id.options2);
        mViewHolder.v(R.id.options3).setVisibility(View.GONE);

        mSubmitView = mViewHolder.v(com.angcyo.uiview.R.id.btnSubmit);
        checkId();
        mSubmitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
                mProvinceBeanAction2.call(mProvinceBean1, mProvinceBean2);
            }
        });
        mViewHolder.v(com.angcyo.uiview.R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });

        List<ProvinceBean> bean = new ArrayList<>();
        ProvinceBean provinceBean = new ProvinceBean();
        provinceBean.setShort_name(" ");
        bean.add(provinceBean);
        mWheelView1.setAdapter(new ArrayWheelAdapter(bean));//用来计算高度,否则会不显示
        mWheelView2.setAdapter(new ArrayWheelAdapter(bean));

        mProvinceListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                isFirstLoad = false;
                city_id_selector = "0";
                List items = ((ArrayWheelAdapter) mWheelView1.getAdapter()).getItems();
                mProvinceBean1 = (ProvinceBean) items.get(index);
                province_id_selector = mProvinceBean1.getId();
                checkId();
                pName = mProvinceBean1.getShort_name();
                resetCities(province_id_selector);
            }
        };
        mWheelView1.setOnItemSelectedListener(mProvinceListener);

        mOnCitiesListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                List items = ((ArrayWheelAdapter) mWheelView2.getAdapter()).getItems();
                mProvinceBean2 = (ProvinceBean) items.get(index);
                city_id_selector = mProvinceBean2.getId();
                cName = mProvinceBean2.getShort_name();
                checkId();
            }
        };
        mWheelView2.setOnItemSelectedListener(mOnCitiesListener);
    }

    @Override
    public void onViewCreate(View rootView) {
        super.onViewCreate(rootView);
        mSubscriptions = new CompositeSubscription();
    }

    @CallSuper
    @Override
    public void onViewUnload() {
        super.onViewUnload();
        if (!mSubscriptions.isUnsubscribed()) {
            mSubscriptions.unsubscribe();
        }
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        mSubscriptions.add(RRetrofit.create(AreaService.class)
                .getProvinces(Param.buildMap())
                .compose(Rx.transformerList(ProvinceBean.class))
                .subscribe(new BaseSingleSubscriber<List<ProvinceBean>>() {
                    @Override
                    public void onSucceed(List<ProvinceBean> bean) {
                        super.onSucceed(bean);
                        mWheelView1.setAdapter(new ArrayWheelAdapter(bean));
                        int index = indexInList(bean, province_id);
                        mProvinceBean1 = bean.get(index);
                        mWheelView1.setCurrentItem(index);
                        //mProvinceListener.onItemSelected(index);
                        resetCities(bean.get(index).getId());
                    }
                }));
    }

    int indexInList(List<ProvinceBean> list, String id) {
        if (TextUtils.isEmpty(id) || "0".equalsIgnoreCase(id)) {
            return 0;
        }
        for (int i = 0; i < list.size(); i++) {
            if (id.equalsIgnoreCase(list.get(i).getId())) {
                return i;
            }
        }
        return 0;
    }

    void resetCities(String province_id) {
        if (mCitiesSubscribe != null) {
            mCitiesSubscribe.unsubscribe();
        }
        mCitiesSubscribe = RRetrofit.create(AreaService.class)
                .getCities(Param.buildMap("province_id:" + province_id))
                .compose(Rx.transformerList(ProvinceBean.class))
                .subscribe(new BaseSingleSubscriber<List<ProvinceBean>>() {
                    @Override
                    public void onSucceed(List<ProvinceBean> bean) {
                        super.onSucceed(bean);
                        mWheelView2.setAdapter(new ArrayWheelAdapter(bean));
                        if (isFirstLoad) {
                            int index = indexInList(bean, city_id);
                            mWheelView2.setCurrentItem(index);
                            mOnCitiesListener.onItemSelected(index);
                        } else {
                            mWheelView2.setCurrentItem(0);
                            mOnCitiesListener.onItemSelected(0);
                        }
                    }
                });
    }

    void checkId() {
        if (province_id.equalsIgnoreCase(province_id_selector) && city_id.equalsIgnoreCase(city_id_selector)
                || (TextUtils.isEmpty(province_id_selector) || TextUtils.isEmpty(city_id_selector))
                || ("0".equalsIgnoreCase(province_id_selector) || "0".equalsIgnoreCase(city_id_selector))) {
            mSubmitView.setVisibility(View.INVISIBLE);
        } else {
            mSubmitView.setVisibility(View.VISIBLE);
        }
    }
}
