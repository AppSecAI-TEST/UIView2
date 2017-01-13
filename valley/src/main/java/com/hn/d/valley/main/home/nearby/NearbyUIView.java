package com.hn.d.valley.main.home.nearby;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.TextureMapView;
import com.angcyo.library.utils.Anim;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.widget.RCheckGroup;
import com.angcyo.uiview.widget.RImageCheckView;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.NearUserBean;
import com.hn.d.valley.bean.realm.AmapBean;
import com.hn.d.valley.bean.realm.NearUserInfo;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.AmapControl;
import com.hn.d.valley.main.home.NoTitleBaseRecyclerUIView;
import com.hn.d.valley.sub.user.service.UserInfoService;
import com.hn.d.valley.utils.RAmap;
import com.hn.d.valley.widget.HnGenderView;
import com.hwangjr.rxbus.annotation.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：首页 下面的 附近
 * 创建人员：Robi
 * 创建时间：2016/12/16 11:18
 * 修改人员：Robi
 * 修改时间：2016/12/16 11:18
 * 修改备注：
 * Version: 1.0.0
 */
public class NearbyUIView extends NoTitleBaseRecyclerUIView<NearUserInfo> {

    @BindView(R.id.check_group_view)
    RCheckGroup mCheckGroupView;
    @BindView(R.id.map_check_view)
    RImageCheckView mMapCheckView;
    @BindView(R.id.filter_root_layout)
    LinearLayout mFilterRootLayout;
    AmapControl mAmapControl;
    @BindView(R.id.map_view)
    TextureMapView mMapView;
    @BindView(R.id.my_location)
    Button mMyLocation;
    private AmapBean lastAmapBean;
    /**
     * 1-男 2-女 0-所有
     */
    private int mSex = 0;

    public static void setAttentionView(final ImageView view, final NearUserInfo dataBean, final String to_uid) {
        if (dataBean.getIs_attention() == 1) {
            //已经是关注
            view.setImageResource(R.drawable.attention_fans_s);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    RRetrofit.create(UserInfoService.class)
                            .unAttention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {

                                @Override
                                public void onNext(String bean) {
                                    dataBean.setIs_attention(0);
                                    if (view != null) {
                                        setAttentionView(view, dataBean, to_uid);
                                    }
                                }
                            });
                }
            });
        } else {
            view.setImageResource(R.drawable.attention_fans_n);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RRetrofit.create(UserInfoService.class)
                            .attention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {

                                @Override
                                public void onNext(String bean) {
                                    dataBean.setIs_attention(1);
                                    if (view != null) {
                                        setAttentionView(view, dataBean, to_uid);
                                    }
                                }
                            });
                }
            });
        }
    }

    @Override
    protected RExBaseAdapter<String, NearUserInfo, String> initRExBaseAdapter() {
        return new RExBaseAdapter<String, NearUserInfo, String>(mActivity) {
            @Override
            protected int getItemLayoutId(int viewType) {
                return R.layout.item_near_user_layout;
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, NearUserInfo dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                holder.fillView(dataBean, true);

                holder.v(R.id.introduce).setVisibility(View.GONE);
                holder.v(R.id.star).setVisibility(View.GONE);

                HnGenderView hnGenderView = holder.v(R.id.grade);
                hnGenderView.setGender(dataBean.getSex(), dataBean.getGrade());

                ImageView commandView = holder.v(R.id.command_item_view);
                commandView.setVisibility(View.VISIBLE);

                setAttentionView(commandView, dataBean, dataBean.getUid());

                holder.v(R.id.auth).setVisibility("1".equalsIgnoreCase(dataBean.getIs_auth()) ? View.VISIBLE : View.GONE);

            }
        };
    }

    @Override
    protected void inflateOverlayLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        super.inflateOverlayLayout(baseContentLayout, inflater);
        inflate(R.layout.layout_sex_filter);
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ViewCompat.animate(mFilterRootLayout)
                            .alpha(1f)
                            .setDuration(UIBaseView.DEFAULT_ANIM_TIME)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                } else {
                    ViewCompat.animate(mFilterRootLayout)
                            .alpha(0f)
                            .setDuration(UIBaseView.DEFAULT_ANIM_TIME)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                }
            }
        });
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        /**男女过滤*/
        mCheckGroupView.setOnCheckChangedListener(new RCheckGroup.OnCheckChangedListener() {
            @Override
            public void onChecked(View fromm, View to) {
                if (to.getId() == R.id.boy_check_view) {
                    mSex = 1;
                } else if (to.getId() == R.id.girl_check_view) {
                    mSex = 2;
                } else if (to.getId() == R.id.all_check_view) {
                    mSex = 0;
                }
                L.e("开始刷新...");
                mRecyclerView.scrollToPosition(0);
                //开始刷新
                mRefreshLayout.setRefreshState(RefreshLayout.TOP);
            }

            @Override
            public void onReChecked(View view) {

            }
        });

        /**地图模式切换*/
        mMapCheckView.setOnCheckedChangeListener(new RImageCheckView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RImageCheckView buttonView, boolean isChecked) {
                mMapCheckView.setImageResource(isChecked ? R.drawable.switch_near : R.drawable.switch_map);
                if (isChecked) {
                    showMapView();
                } else {
                    hideMapView();
                }
            }
        });
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }

    @Override
    public void onShowInPager(UIViewPager viewPager) {
        super.onShowInPager(viewPager);
        RAmap.startLocation();
        if (isMapMode()) {
            UIViewPager.interceptTouch = false;
            mMapView.onResume();
        }
    }

    @Override
    public void onHideInPager(UIViewPager viewPager) {
        super.onHideInPager(viewPager);
        UIViewPager.interceptTouch = true;

        RAmap.stopLocation();
        if (isMapMode()) {
            mMapView.onPause();
        }
    }

    @Subscribe()
    public void onEvent(AmapBean bean) {
        if (bean.result) {
            lastAmapBean = bean;
            RAmap.stopLocation();
        }
    }

    @Override
    protected int getEmptyTipStringId() {
        return R.string.default_empty_nearby_tip;
    }

    @Override
    protected void onEmptyData(boolean isEmpty) {
        super.onEmptyData(isEmpty);
//        if (!isMapMode()) {
//            mFilterRootLayout.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
//        }
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        L.e("加载数据..." + page);
        RAmap.startLocation();

        add(RRetrofit.create(UserInfoService.class)
                .nearUser(Param.buildMap("uid:" + UserCache.getUserAccount(),
                        "page:" + page,
                        "sex:" + mSex,
                        "lng:" + getLongitude(),
                        "lat:" + getLatitude()))
                .compose(Rx.transformer(NearUserBean.class))
                .subscribe(new RSubscriber<NearUserBean>() {
                    @Override
                    public void onNext(NearUserBean nearUserBean) {
                        if (nearUserBean == null) {
                            onUILoadDataEnd();
                        } else {
                            if (isMapMode()) {
                                mAmapControl.addMarks(nearUserBean.getData_list());
                            }
                            onUILoadDataEnd(nearUserBean.getData_list(), nearUserBean.getData_count());
                        }
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        onUILoadDataFinish();
                    }
                })
        );
    }

    private AmapBean getLastAmapBean() {
        if (lastAmapBean == null) {
            lastAmapBean = RAmap.getLastLocation();
        }
        return lastAmapBean;
    }

    private double getLatitude() {
        if (lastAmapBean == null) {
            lastAmapBean = RAmap.getLastLocation();
        }
        if (lastAmapBean == null) {
            return 116.32715863448607;
        }
        return lastAmapBean.latitude;
    }

    private double getLongitude() {
        if (lastAmapBean == null) {
            lastAmapBean = RAmap.getLastLocation();
        }
        if (lastAmapBean == null) {
            return 39.990912172420714;
        }
        return lastAmapBean.longitude;
    }


    @Override
    public void onViewHide() {
        super.onViewHide();
        if (isMapMode()) {
            mMapView.onPause();
        }
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        if (isMapMode()) {
            mMapView.onResume();
        }
    }


    @Override
    public void onViewUnload() {
        super.onViewUnload();
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
        mAmapControl = null;
    }

    @OnClick(R.id.my_location)
    public void onMyLocationClick() {
        initLocation();
    }

    /**
     * 判断是否是地图模式
     */
    private boolean isMapMode() {
        if (mMapCheckView == null) {
            return false;
        }
        return mMapCheckView.isChecked();
    }

    /**
     * 显示地图模式
     */
    private void showMapView() {
        UIViewPager.interceptTouch = false;
        mMapView.setVisibility(View.VISIBLE);
        ViewCompat.setAlpha(mMapView, 0);
        Anim.anim(mMapView).alpha(1).withEndAction(new Runnable() {
            @Override
            public void run() {
                mMyLocation.setVisibility(View.VISIBLE);
            }
        }).start();

        if (mAmapControl == null) {
            mMapView.onCreate(null);
            AMap map = mMapView.getMap();
            mAmapControl = new AmapControl(mActivity, map);
            mAmapControl.initAmap(map, false, mAmapControl);
            map.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {
                    initLocation();
                    mAmapControl.addMarks(mRExBaseAdapter.getAllDatas());
                }
            });
        } else {
            mAmapControl.addMarks(mRExBaseAdapter.getAllDatas());
        }

        mMapView.onResume();

        initLocation();
    }

    /**
     * 隐藏地图模式
     */
    private void hideMapView() {
        UIViewPager.interceptTouch = true;
        mMapView.onPause();
        mMyLocation.setVisibility(View.GONE);
        Anim.anim(mMapView).alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                mMapView.setVisibility(View.GONE);
            }
        }).start();
    }

    /**
     * 定位到最后一次的位置
     */
    private void initLocation() {
        AmapBean lastAmapBean = getLastAmapBean();
        if (lastAmapBean == null) {
            return;
        }
        mAmapControl.moveToLocation(lastAmapBean);
    }
}
