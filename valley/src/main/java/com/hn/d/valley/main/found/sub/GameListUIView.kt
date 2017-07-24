package com.hn.d.valley.main.found.sub

import android.support.v4.view.MotionEventCompat
import android.support.v4.view.ViewCompat
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter
import com.angcyo.uiview.view.RClickListener
import com.angcyo.uiview.widget.GlideImageView
import com.hn.d.valley.R
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.GameBean
import com.hn.d.valley.bean.GameModel
import com.hn.d.valley.service.GameService
import com.hn.d.valley.sub.other.SingleRecyclerUIView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：游戏列表
 * 创建人员：Robi
 * 创建时间：2017/07/24 10:38
 * 修改人员：Robi
 * 修改时间：2017/07/24 10:38
 * 修改备注：
 * Version: 1.0.0
 */
class GameListUIView : SingleRecyclerUIView<GameBean>() {

    override fun getTitleString(): String {
        return getString(R.string.game_list_title)
    }

    override fun initRExBaseAdapter(): RExBaseAdapter<String, GameBean, String> {
        return object : RExBaseAdapter<String, GameBean, String>(mActivity) {

            override fun getItemLayoutId(viewType: Int): Int {
                return R.layout.item_game_layout
            }

            override fun onBindDataView(holder: RBaseViewHolder, posInData: Int, dataBean: GameBean?) {
                super.onBindDataView(holder, posInData, dataBean)
                val glideView: GlideImageView = holder.v(R.id.image_view)
                glideView.apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    url = dataBean?.thumb
                }

                holder.click(R.id.start_view, object : RClickListener(2000, true) {
                    override fun onRClick(view: View?) {
                        add(RRetrofit
                                .create(GameService::class.java)
                                .getToken(Param.buildMap("app_id:${dataBean?.app_id}"))
                                .compose(Rx.transformer(String::class.java))
                                .subscribe(object : BaseSingleSubscriber<String>() {

                                    override fun onSucceed(bean: String?) {
                                        bean?.let {
                                            startIView(GameWebUIView("${dataBean?.url}/?token=$it"))
                                        }
                                    }
                                })
                        )
                    }
                })

                val startButton: ImageView = holder.v(R.id.start_view)
                startButton.setOnTouchListener { v, event ->
                    when (MotionEventCompat.getActionMasked(event)) {
                        MotionEvent.ACTION_DOWN -> {
                            ViewCompat.setScaleX(v, 1.2f)
                            ViewCompat.setScaleY(v, 1.2f)
                        }
                        MotionEvent.ACTION_UP -> {
                            ViewCompat.setScaleX(v, 1f)
                            ViewCompat.setScaleY(v, 1f)
                        }
                        MotionEvent.ACTION_CANCEL -> {
                            ViewCompat.setScaleX(v, 1f)
                            ViewCompat.setScaleY(v, 1f)
                        }
                    }
                    return@setOnTouchListener false
                }
            }
        }
    }

    override fun isLoadInViewPager(): Boolean {
        return false
    }

    override fun hasDecoration(): Boolean {
        return false
    }

    override fun onUILoadData(page: String?) {
        super.onUILoadData(page)

        add(RRetrofit
                .create(GameService::class.java)
                .list(Param.buildMap("page:$page"))
                .compose(Rx.transformer(GameModel::class.java))
                .subscribe(object : BaseSingleSubscriber<GameModel>() {

                    override fun onStart() {
                        super.onStart()
                        showLoadView()
                    }

                    override fun onSucceed(bean: GameModel?) {
                        if (bean == null || bean.data_list.isEmpty()) {
                            showEmptyLayout()
                        } else {
                            onUILoadDataEnd(bean.data_list)
                        }
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException) {
                        super.onEnd(isError, isNoNetwork, e)
                        onUILoadDataFinish()
                        if (isError) {
                            showNonetLayout { loadData() }
                        }
                    }
                })
        )
    }
}