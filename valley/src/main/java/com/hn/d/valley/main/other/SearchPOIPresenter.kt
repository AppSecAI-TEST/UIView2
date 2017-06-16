package com.hn.d.valley.main.other

import android.text.TextUtils
import com.amap.api.fence.PoiItem
import com.amap.api.maps.model.LatLng
import com.amap.api.services.poisearch.PoiSearch
import com.angcyo.uiview.mvp.presenter.BasePresenter
import com.angcyo.uiview.mvp.presenter.IBasePresenter
import com.angcyo.uiview.mvp.view.IBaseView
import com.hn.d.valley.control.AmapControl
import com.hn.d.valley.main.message.mvp.Search
import com.hn.d.valley.main.message.mvp.Search.ISearchPresenter

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/06/13 14:47
 * 修改人员：hewking
 * 修改时间：2017/06/13 14:47
 * 修改备注：
 * Version: 1.0.0
 */

class SearchPOIPresenter : BasePresenter<IPOISearchView>(), IPOISearchPresenter{
    override fun onSearch(latLng: LatLng, currentPage: Int, listener: PoiSearch.OnPoiSearchListener?,query : String?,type : String?) {
        if (query.isNullOrEmpty()) {
            return
        }
        AmapControl.doSearchQuery(latLng,currentPage,listener,query,type)

    }


}

interface IPOISearchPresenter : IBasePresenter<IPOISearchView> {
    fun onSearch(latLng : LatLng, currentPage : Int, listener : PoiSearch.OnPoiSearchListener?,query : String?,type : String?)
}

interface IPOISearchView : IBaseView {
//    fun onSearchSuccess(result : List<PoiItem>)
}

