package com.hn.d.valley.main.other;

import android.os.Bundle;
import android.support.v4.widget.ViewDragHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.NaviPara;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIContentView;
import com.angcyo.uiview.container.SwipeBackLayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.AmapBean;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.utils.RAmap;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmResults;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/27 10:06
 * 修改人员：Robi
 * 修改时间：2016/12/27 10:06
 * 修改备注：
 * Version: 1.0.0
 */
public class AmapUIView extends UIContentView implements AMap.OnCameraChangeListener, LocationSource, AMapLocationListener {

    @BindView(R.id.map_view)
    TextureMapView mMapView;
    @BindView(R.id.marker_address)
    TextView mMarkerAddress;
    @BindView(R.id.location_info_layout)
    LinearLayout mLocationInfoLayout;
    AmapBean mLastBean, mTargetBean;
    private AMap mMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private Action1<AmapBean> mBeanAction1;
    private int mState = ViewDragHelper.STATE_IDLE;
    private boolean mSend = false;//是否需要发送位置
    private LatLng mLatlng;

    public AmapUIView() {
    }

    public AmapUIView(Action1<AmapBean> beanAction1, LatLng latlng, boolean send) {
        mLatlng = latlng;
        mSend = send;
        mBeanAction1 = beanAction1;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_amap_layout);
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();
        mMapView.onCreate(null);
        initAmap();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                initLocation();
                setCameraChangeListener();
            }
        }, 500);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> items = new ArrayList<>();
        if (mSend) {
            items.add(TitleBarPattern.TitleBarItem.build().setText("发送").setListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTargetBean == null) {
                        T_.show("还未获取到有效位置信息.");
                    } else {
//                    finishIView(AmapUIView.this, new UIParam(true, false));
                        if (mBeanAction1 != null) {
                            mBeanAction1.call(mTargetBean);
                        }
                        finishIView(AmapUIView.this);
                    }
                }
            }));
        }
        return super.getTitleBar().setTitleString("我的位置").setShowBackImageView(true).setRightItems(items);
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        if (mILayout instanceof SwipeBackLayout) {
            ((SwipeBackLayout) mILayout).setOnPanelSlideListener(new SwipeBackLayout.OnPanelSlideListener() {
                @Override
                public void onStateChanged(int state) {
                    mState = state;
                    setCameraChangeListener();
                }

                @Override
                public void onRequestClose() {

                }

                @Override
                public void onRequestOpened() {

                }

                @Override
                public void onSlideChange(float percent) {

                }
            });
        }
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        mMapView.onDestroy();
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        mMapView.onPause();
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        mMapView.onResume();
    }

    @OnClick(R.id.my_location)
    public void onMyLocationClick() {
        moveToLocation(mLastBean);
    }

    private void initAmap() {
        try {
            mMap = mMapView.getMap();

            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

            mMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
            mMap.setLocationSource(this);// 设置定位监听
            mMap.setMyLocationEnabled(true);
            //自己的位置 圆点-------------
            // 自定义系统定位蓝点
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            // 自定义定位蓝点图标
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                    fromResource(R.drawable.gps_point));
            // 自定义精度范围的圆形边框颜色
            myLocationStyle.strokeColor(mActivity.getResources().getColor(R.color.colorAccent));
            //自定义精度范围的圆形边框宽度
            myLocationStyle.strokeWidth(5);
            // 设置圆形的填充颜色
            myLocationStyle.radiusFillColor(mActivity.getResources().getColor(R.color.theme_color_primary_dark_tran2));
            // 将自定义的 myLocationStyle 对象添加到地图上
            mMap.setMyLocationStyle(myLocationStyle);
            //end--------------------

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCameraChangeListener() {
        if (mState == ViewDragHelper.STATE_IDLE) {
            mMap.setOnCameraChangeListener(this);
        } else {
            mMap.setOnCameraChangeListener(null);
        }
    }

    /**
     * 定位到最后一次的位置
     */
    private void initLocation() {

        if (mLatlng == null) {
            RealmResults<AmapBean> all = RRealm.where(AmapBean.class).findAll();
            if (all.size() > 0) {
                mLastBean = all.last();
                mMarkerAddress.setText(mLastBean.address);
                moveToLocation(mLastBean);
            } else {
                mLatlng = new LatLng(39.90923, 116.397428);
                moveToLocation(mLatlng);
            }
        } else {
            moveToLocation(mLatlng);
        }
    }

    private void moveToLocation(long latitude, long longitude) {
        LatLng latlng = new LatLng(latitude, longitude);
        moveToLocation(latlng);
    }

    private void moveToLocation(LatLng latlng) {
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, Constant.DEFAULT_ZOOM_LEVEL, 0, 0));
        mMap.animateCamera(camera);
    }

    private void moveToLocation(AmapBean bean) {
        mTargetBean = mLastBean;
        moveToLocation(new LatLng(bean.latitude, bean.longitude));
    }

    /**
     * 导航到指定位置
     */
    private void naviTo(LatLng toLatlng) {

        // 构造导航参数
        NaviPara naviPara = new NaviPara();
        // 设置终点位置
        naviPara.setTargetPoint(toLatlng);
        // 设置导航策略，这里是避免拥堵
        naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
        try {
            // 调起高德地图导航
            AMapUtils.openAMapNavi(naviPara, mActivity);
        } catch (com.amap.api.maps.AMapException e) {
            // 如果没安装会进入异常，调起下载页面
            AMapUtils.getLatestAMapApp(mActivity);
        }
        mMap.clear();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mMarkerAddress.setText("正在获取...");
        mTargetBean = null;
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        RAmap.instance().queryLatLngAddress(cameraPosition.target, new Action1<AmapBean>() {
            @Override
            public void call(AmapBean bean) {
                mTargetBean = bean;
                mMarkerAddress.setText(bean.address);
            }
        });
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {

                mLastBean = RAmap.saveAmapLocation2(amapLocation);

                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                L.e("AmapErr:" + errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(mActivity);
            AMapLocationClientOption mLocationOption = RAmap.getDefaultOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }
}
