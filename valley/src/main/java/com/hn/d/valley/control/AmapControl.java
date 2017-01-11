package com.hn.d.valley.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.animation.Interpolator;

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
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.utils.BmpUtil;
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
import java.util.Set;
import java.util.concurrent.ExecutionException;

import rx.functions.Func1;

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
        mMarkerSize = (int) ResUtil.dpToPx(mContext, 60);
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

    //dip和px转换
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void addMarks(List<NearUserInfo> userInfos) {
        clearMarker();

        for (final NearUserInfo info : userInfos) {
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

            Rx.base(new Func1<String, Object>() {
                @Override
                public Object call(String s) {
                    try {
                        Bitmap myBitmap = Glide.with(mContext)
                                .load(info.getAvatar())
                                .asBitmap() //必须
                                .centerCrop()
                                //.transform(new GlideCircleTransform(mContext))...
                                .into(mMarkerSize, mMarkerSize)
                                .get();
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BmpUtil.getRoundedCornerBitmap(myBitmap, mMarkerSize)));

                        /**开始动画*/
                        startJumpAnimation(marker);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            });

            mMarkerMap.put(info.getUid(), marker);
        }
    }

    private void clearMarker() {
        Set<Map.Entry<String, Marker>> entries = mMarkerMap.entrySet();
        for (Map.Entry<String, Marker> entry : entries) {
            entry.getValue().destroy();
        }
        mMarkerMap.clear();
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
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation(Marker marker) {

        if (marker != null) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = marker.getPosition();
            Point point = map.getProjection().toScreenLocation(latLng);
            point.y -= dip2px(mContext, 125);
            LatLng target = map.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            //设置动画
            marker.setAnimation(animation);
            //开始动画
            marker.startAnimation();

        } else {
            Log.e("ama", "screenMarker is null");
        }
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
