package com.hn.d.valley.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.resources.ResUtil;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.realm.AmapBean;
import com.hn.d.valley.bean.realm.NearUserInfo;
import com.hn.d.valley.utils.RAmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/11 17:50
 * 修改人员：Robi
 * 修改时间：2017/01/11 17:50
 * 修改备注：
 * Version: 1.0.0
 */
public class AmapControl implements LocationSource, AMapLocationListener {

    private final int mMarkerSize;
    OnLocationChangedListener mListener;
    AMapLocationClient mLocationClient;
    List<Marker> mMarkerList;
    /**
     * 保存用户id, 和对应的marker
     */
    Map<String, Marker> mMarkerMap;
    AMap map;
    Context mContext;

    public AmapControl(Context context, AMap map) {
        mContext = context;
        this.map = map;
        mMarkerList = new ArrayList<>();
        mMarkerMap = new HashMap<>();
        mMarkerSize = (int) ResUtil.dpToPx(mContext, 100);
    }

    public static Point getPointFromLanLng(final AMap aMap, LatLng latLng) {
        return aMap.getProjection().toScreenLocation(latLng);
    }

    /**
     * 初始化本地位置
     */
    public static void initAmap(final AMap aMap, final LocationSource locationSource) {
        try {
            UiSettings uiSettings = aMap.getUiSettings();
            /**放大,缩小按钮,是否激活*/
            uiSettings.setZoomControlsEnabled(false);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

            /**是否显示比例尺*/
            uiSettings.setScaleControlsEnabled(true);

            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
            aMap.setLocationSource(locationSource);// 设置定位监听
            aMap.setMyLocationEnabled(true);
            //自己的位置 圆点-------------
            // 自定义系统定位蓝点
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            // 自定义定位蓝点图标
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                    fromResource(R.drawable.gps_point));
            // 自定义精度范围的圆形边框颜色
            myLocationStyle.strokeColor(ValleyApp.getApp().getResources().getColor(R.color.colorAccent));
            //自定义精度范围的圆形边框宽度
            myLocationStyle.strokeWidth(5);
            // 设置圆形的填充颜色
            myLocationStyle.radiusFillColor(ValleyApp.getApp().getResources().getColor(R.color.theme_color_primary_dark_tran2));
            // 将自定义的 myLocationStyle 对象添加到地图上
            aMap.setMyLocationStyle(myLocationStyle);
            //end--------------------

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test() {
//        FutureTarget<File> future = Glide.with(mContext)
//                .load("url")
//                .downloadOnly(500, 500);
//        try {
//            File cacheFile = future.get();
//            String path = cacheFile.getAbsolutePath();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

//        Bitmap myBitmap = Glide.with(applicationContext)
//                .load(yourUrl)
//                .asBitmap() //必须
//                .centerCrop()
//                .into(500, 500)
//                .get();
    }

    public void addMarks(List<NearUserInfo> userInfos) {
        for (NearUserInfo info : userInfos) {
            LatLng latLng = new LatLng(Double.valueOf(info.getLat()), Double.valueOf(info.getLng()));
            final Marker marker = map.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.default_avatar)));

//            Glide.with(mContext)
//                    .load(info.getAvatar())
//                    .asBitmap()
//                    .override(100, 100)
//                    .centerCrop()
//                    .transform(new GlideCircleTransform(mContext))
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(resource));
//                        }
//                    });

            try {
                Bitmap myBitmap = Glide.with(mContext)
                        .load(info.getAvatar())
                        .asBitmap() //必须
                        .centerCrop()
                        .into(160, 160)
                        .get();
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(myBitmap));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            mMarkerMap.put(info.getUid(), marker);
        }
    }

    public void moveToLocation(LatLng latlng) {
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(
                new CameraPosition(latlng, Constant.DEFAULT_ZOOM_LEVEL, 0, 0));
        map.animateCamera(camera);
    }

    public void moveToLocation(AmapBean bean) {
        moveToLocation(new LatLng(bean.latitude, bean.longitude));
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {

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
            mLocationClient = new AMapLocationClient(mContext);
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
