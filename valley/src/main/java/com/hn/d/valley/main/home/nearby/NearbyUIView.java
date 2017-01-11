package com.hn.d.valley.main.home.nearby;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.angcyo.uiview.widget.RCheckGroup;
import com.angcyo.uiview.widget.RImageCheckView;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.NearUserBean;
import com.hn.d.valley.bean.realm.AmapBean;
import com.hn.d.valley.bean.realm.NearUserInfo;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.home.NoTitleBaseRecyclerUIView;
import com.hn.d.valley.sub.user.service.UserInfoService;
import com.hn.d.valley.utils.RAmap;
import com.hn.d.valley.widget.HnGenderView;
import com.hwangjr.rxbus.annotation.Subscribe;

import butterknife.BindView;

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
    private AmapBean lastAmapBean;

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
                holder.fillView(dataBean);

                holder.v(R.id.introduce).setVisibility(View.GONE);
                holder.v(R.id.star).setVisibility(View.GONE);

                HnGenderView hnGenderView = holder.v(R.id.grade);
                hnGenderView.setGender(dataBean.getSex(), dataBean.getGrade());

                ImageView commandView = holder.v(R.id.command_item_view);
                commandView.setVisibility(View.VISIBLE);
                if (dataBean.getIs_attention() == 1) {
                    commandView.setImageResource(R.drawable.attention_fans_s);
                } else {
                    commandView.setImageResource(R.drawable.attention_fans_n);
                }
                holder.v(R.id.auth).setVisibility("1".equalsIgnoreCase(dataBean.getIs_auth()) ? View.VISIBLE : View.GONE);

            }
        };
    }

    @Override
    protected void inflateOverlayLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        super.inflateOverlayLayout(baseContentLayout, inflater);
        inflate(R.layout.layout_sex_filter);

        /**男女过滤*/
        mCheckGroupView.setOnCheckChangedListener(new RCheckGroup.OnCheckChangedListener() {
            @Override
            public void onChecked(View fromm, View to) {

            }

            @Override
            public void onReChecked(View view) {

            }
        });

        /**地图模式切换*/
        mMapCheckView.setOnCheckedChangeListener(new RImageCheckView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RImageCheckView buttonView, boolean isChecked) {

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
    }

    @Override
    public void onHideInPager(UIViewPager viewPager) {
        super.onHideInPager(viewPager);
        RAmap.stopLocation();
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
        mFilterRootLayout.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(UserInfoService.class)
                        .nearUser(Param.buildMap("uid:" + UserCache.getUserAccount(),
                                "page:" + page,
                                "lng:" + getLongitude(),
                                "lat:" + getLatitude()))
                        .compose(Rx.transformer(NearUserBean.class))
                        .subscribe(new RSubscriber<NearUserBean>() {
                            @Override
                            public void onNext(NearUserBean nearUserBean) {
                                onUILoadDataEnd();
//                        if (nearUserBean == null) {
//                        } else {
//                            onUILoadDataEnd(nearUserBean.getData_list(), nearUserBean.getData_count());
//                        }
                            }

                            @Override
                            public void onEnd() {
                                super.onEnd();
                                onUILoadDataFinish();
                            }
                        })
        );
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
}
