package com.hn.d.valley.main.seek

import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import com.angcyo.uiview.container.ContentLayout
import com.angcyo.uiview.container.UIParam
import com.angcyo.uiview.dialog.UIBottomItemDialog
import com.angcyo.uiview.github.pickerview.DateDialog
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter
import com.angcyo.uiview.utils.RUtils
import com.angcyo.uiview.utils.T_
import com.angcyo.uiview.widget.GlideImageView
import com.angcyo.uiview.widget.RTextView
import com.hn.d.valley.R
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.SeekBean
import com.hn.d.valley.bean.realm.AmapBean
import com.hn.d.valley.service.ShowService
import com.hn.d.valley.sub.other.SingleRecyclerUIView
import com.hn.d.valley.utils.RAmap
import com.hn.d.valley.widget.HnGenderView
import com.hn.d.valley.widget.HnGlideImageView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：寻觅
 * 创建人员：Robi
 * 创建时间：2017/08/03 12:01
 * 修改人员：Robi
 * 修改时间：2017/08/03 12:01
 * 修改备注：
 * Version: 1.0.0
 */
class SeekUIView : SingleRecyclerUIView<SeekBean>() {

    private var sex = 0//	否	int	1-男 2-女 0-全部【默认0】

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar()
                .setShowBackImageView(false)
                .setTitleString("寻觅")
                .addRightItem(TitleBarPattern.TitleBarItem(R.drawable.top_shaixuan_icon) {
                    UIBottomItemDialog.build()
                            .addItem("全部") {
                                sex = 0
                                scrollToTop()
                                loadData()
                            }
                            .addItem("只看男") {
                                sex = 1
                                scrollToTop()
                                loadData()
                            }
                            .addItem("只看女") {
                                sex = 2
                                scrollToTop()
                                loadData()
                            }
                            .addItem("自定义") {
                                scrollToTop()
                            }
                            .showDialog(mParentILayout)
                })
    }

    override fun hasDecoration(): Boolean {
        return false
    }

    override fun initRecyclerView() {
        super.initRecyclerView()
        mRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun inflateOverlayLayout(baseContentLayout: ContentLayout?, inflater: LayoutInflater?) {
        inflate(R.layout.view_seek_over_layout)
    }

    override fun initOnShowContentLayout() {
        super.initOnShowContentLayout()

        val controlLayout: View? = mViewHolder.v(R.id.view_control_layout)

        mViewHolder.click(R.id.delete_view) {
            controlLayout?.let {
                it.animate()
                        .translationY((-it.measuredHeight).toFloat())
                        .setDuration(300)
                        .start()
            }
        }

        mViewHolder.click(R.id.button_view) {
            //开启秀场
            T_.info("测试....")
        }
        mViewHolder.click(R.id.xiehou_view) {
            //邂逅,打招呼
            T_.info("测试....")
        }
    }

    override fun initRExBaseAdapter(): RExBaseAdapter<String, SeekBean, String> {
        return object : RExBaseAdapter<String, SeekBean, String>(mActivity) {
            override fun getItemLayoutId(viewType: Int): Int {
                return R.layout.item_seek_layout
            }

            override fun onBindLoadMoreView(holder: RBaseViewHolder, position: Int) {
                super.onBindLoadMoreView(holder, position)
                holder.itemView.visibility = View.INVISIBLE
            }

            override fun onBindDataView(holder: RBaseViewHolder, posInData: Int, dataBean: SeekBean) {
                super.onBindDataView(holder, posInData, dataBean)

                //展示图
                val imageView: GlideImageView = holder.v(R.id.image_view)

                //排名, 魅力值
                val charmView: RTextView = holder.v(R.id.charm_view)
                val rankView: RTextView = holder.v(R.id.rank_view)

                //等级星座
                val genderView: HnGenderView = holder.v(R.id.gender_view)

                //用户头像, 昵称
                val userIcoView: HnGlideImageView = holder.v(R.id.user_ico_view)
                val userNameView: RTextView = holder.v(R.id.user_name_view)

                imageView.apply {
                    reset()
                    url = RUtils.split(dataBean.images).first()
                }

                charmView.text = dataBean.charm
                rankView.text = dataBean.rank.toString()
                genderView.setGender2(dataBean.sex, DateDialog.getBirthday(dataBean.birthday), dataBean.constellation)
                userIcoView.setImageThumbUrl(dataBean.avatar)
                userNameView.text = dataBean.username
            }
        }
    }

    override fun isLoadInViewPager(): Boolean {
        return false
    }

    override fun onViewCreate(rootView: View?, param: UIParam?) {
        super.onViewCreate(rootView, param)
    }

    override fun onViewCreate(rootView: View?) {
        super.onViewCreate(rootView)
        RAmap.startLocation(true)
    }

    override fun onViewShowNotFirst(bundle: Bundle?) {
        super.onViewShowNotFirst(bundle)
        RAmap.startLocation(true)
    }

    override fun onViewHide() {
        super.onViewHide()
        RAmap.stopLocation()
    }

    private var lastAmapBean: AmapBean? = null

    private fun getLastAmapBean(): AmapBean? {
        if (lastAmapBean == null) {
            lastAmapBean = RAmap.getLastLocation()
        }
        return lastAmapBean
    }

    private fun getLatitude(): Double {
        if (lastAmapBean == null) {
            lastAmapBean = RAmap.getLastLocation()
        }
        if (lastAmapBean == null) {
            return 116.32715863448607
        }
        return lastAmapBean!!.latitude
    }

    private fun getLongitude(): Double {
        if (lastAmapBean == null) {
            lastAmapBean = RAmap.getLastLocation()
        }
        if (lastAmapBean == null) {
            return 39.990912172420714
        }
        return lastAmapBean!!.longitude
    }

    override fun onUILoadData(page: String?) {
        super.onUILoadData(page)
        add(RRetrofit.create(ShowService::class.java)
                .list(Param.buildMap("page:$page",
                        "lng:${getLongitude()}", "lat:${getLatitude()}",
                        "distance:1",
                        "sex:$sex"))
                .compose(Rx.transformerList(SeekBean::class.java))
                .doOnSubscribe { showLoadView() }
                .subscribe(object : BaseSingleSubscriber<List<SeekBean>>() {
                    override fun onSucceed(bean: List<SeekBean>) {
                        super.onSucceed(bean)
                        if (bean.isEmpty() && "1".equals(page, true)) {
                            showEmptyLayout()
                        } else {
                            onUILoadDataEnd(bean)
                        }
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException) {
                        super.onEnd(isError, isNoNetwork, e)
                        hideLoadView()
                        if (isError) {
                            showNonetLayout { loadData() }
                        }
                    }
                }))
    }
}