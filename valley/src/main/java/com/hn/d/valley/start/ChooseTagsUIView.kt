package com.hn.d.valley.start

import android.content.Context
import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.angcyo.library.utils.L
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.RRecyclerView
import com.angcyo.uiview.recycler.RecyclerViewPagerIndicator
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter
import com.angcyo.uiview.recycler.adapter.RModelAdapter
import com.angcyo.uiview.skin.SkinHelper
import com.angcyo.uiview.utils.ScreenUtil
import com.angcyo.uiview.utils.T_
import com.angcyo.uiview.widget.JumpAdProgressBar
import com.angcyo.uiview.widget.RImageView
import com.angcyo.uiview.widget.viewpager.RViewPager
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseContentUIView
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.realm.Tag
import com.hn.d.valley.control.TagsControl
import com.hn.d.valley.main.message.setImageUrl
import com.hn.d.valley.main.message.setThumbUrl
import com.hn.d.valley.service.DiscussService
import com.hn.d.valley.widget.HnButton
import com.hn.d.valley.widget.HnGlideImageView
import org.apache.lucene.util.packed.PackedInts
import rx.functions.Action1

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：注册完成选择标签页
 * 创建人员：cjh
 * 创建时间：2017/07/413:46
 * 修改人员：cjh
 * 修改时间：2017/07/4 13:46
 * 修改备注：
 * Version: 1.0.0
 */
class ChooseTagsUIView : BaseContentUIView() , Action1<Boolean>{

    var vp_tags_pager : RViewPager? = null
    var indicator : RecyclerViewPagerIndicator? = null
    var hn_button : HnButton? = null
    var adapter : ChooseTagsPagerAdapter? = null

    override fun getTitleBar(): TitleBarPattern? {
        return null
    }

    override fun getDefaultBackgroundColor(): Int {
        return Color.WHITE
    }

//    override fun getDefaultLayoutState(): LayoutState {
//        return LayoutState.LOAD
//    }

    override fun inflateContentLayout(baseContentLayout: RelativeLayout?, inflater: LayoutInflater?) {
        inflate(R.layout.view_start_choose_tags)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun canTryCaptureView(): Boolean {
        return false
    }

    override fun onViewLoad() {
        super.onViewLoad()
        vp_tags_pager = mViewHolder.v(R.id.vp_tags_pager)
        indicator = mViewHolder.v(R.id.recycler_view_pager_indicator)
        hn_button = mViewHolder.v(R.id.text_view)
        hn_button!!.isEnabled = false
        hn_button!!.setOnClickListener {
            if (adapter != null){
                val tags = adapter!!.getAllSelectTags()
                // 保存tags
                TagsControl.setMyTagString(tags)
            }
            replaceIView(RecommendUser2UIView())
        }
        loadData()

    }

    override fun initOnShowContentLayout() {
        super.initOnShowContentLayout()

    }

    override fun call(t: Boolean?) {
        if (t!!) {
            hn_button!!.isEnabled = true
            hn_button!!.text = getString(R.string.text_next_step)
        } else {
            hn_button!!.isEnabled = false
            hn_button!!.text = getString(R.string.text_choose_latest_three_tags)
        }
    }

    fun loadData() {
        RRetrofit.create(DiscussService::class.java)
                .getTags(Param.buildMap())
                .compose(Rx.transformerList(Tag::class.java))
                .subscribe(object : BaseSingleSubscriber<List<Tag>>(){
                    override fun onSucceed(bean: List<Tag>?) {
                        super.onSucceed(bean)
//                        showContentLayout()
                        adapter = ChooseTagsPagerAdapter(bean!!,this@ChooseTagsUIView)
                        vp_tags_pager!!.adapter = adapter
                        indicator!!.setUpUIViewPager(vp_tags_pager,adapter!!.count)
                    }

                    override fun onError(code: Int, msg: String?) {
                        super.onError(code, msg)
                    }

                })

    }

    class ChooseTagsPagerAdapter : PagerAdapter , Action1<Boolean>{

        var datas : List<Tag>
        var adapters : MutableList<RModelAdapter<Tag>>
        lateinit var action : Action1<Boolean>

        constructor(datas : List<Tag>,action1: Action1<Boolean>) {
            this.datas = datas
            this.adapters = ArrayList()
            this.action = action1
        }

        override fun getCount(): Int {
            return datas.size / 9 + 1
        }

        override fun getItemPosition(`object`: Any?): Int {
            return super.getItemPosition(`object`)
        }

        fun getAllSelectTags() : MutableList<Tag> {
            val data = ArrayList<Tag>()
            adapters.forEach {
                data.addAll(it.selectorData)
            }
            return data
        }

        override fun call(t: Boolean?) {
            if (t!! && getAllSelectTags().size > 2) {
                action.call(true)
            } else {
                action.call(false)
            }
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            val rv_tags = RRecyclerView(container!!.context)
            val layoutParams = ViewGroup.LayoutParams(-2,-2)
            layoutParams.width = ScreenUtil.screenWidth
            val itemHeight = layoutParams.width / 3

            val itemDecoration = SpaceItemDecoration(10)
            rv_tags.addItemDecoration(itemDecoration)
            //禁止RecyclerView 上下拖动阴影
            rv_tags.setOverScrollMode(View.OVER_SCROLL_NEVER)
            rv_tags.setLayoutManager(GridLayoutManager(container.context, 3))
            val start  = position * 9
            val end  = if((position + 1) * 9 > datas.size){
                datas.size
            }else {
                (position + 1) * 9
            }
            val adapter = ChooseTagsAdapter(container.context,datas.subList(start,end),itemHeight)
            adapter.action = this
            rv_tags.adapter = adapter
            // add adapter
            adapters.add(adapter)
            container.addView(rv_tags,ViewGroup.LayoutParams(-1,-1))
            return rv_tags
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            container!!.removeView(`object` as View)
        }


    }

    class ChooseTagsAdapter(context: Context?,datas : List<Tag>,itemHeight : Int) : RModelAdapter<Tag>(context,datas) {


        var itemHeight : Int

        lateinit var action : Action1<Boolean>

        init {
            this.itemHeight = itemHeight
            setModel(MODEL_MULTI)
        }

        override fun getItemLayoutId(viewType: Int): Int {
            return R.layout.item_start_tag_layout
        }

        override fun onBindCommonView(holder: RBaseViewHolder?, posInData: Int, dataBean: Tag?) {
            if (itemHeight != 0) {
                val layoutParams = holder!!.itemView.layoutParams
                layoutParams.height = itemHeight
                holder.itemView.layoutParams = layoutParams
            }
            val imageView = holder!!.imgV(R.id.image_view) as RImageView
//            val username = holder.tv(R.id.tv_username)
            imageView.setThumbUrl(dataBean!!.thumb)
//            username.text = dataBean!!.name
        }

        override fun onBindModelView(model: Int, isSelector: Boolean, holder: RBaseViewHolder?, position: Int, bean: Tag?) {
            super.onBindModelView(model, isSelector, holder, position, bean)
            val imageView = holder!!.imgV(R.id.image_view) as RImageView
            holder.itemView.setOnClickListener {
                setSelectorPosition(position)
                if (!isPositionSelector(position)) {
                    imageView.clearColor()
                    action.call(false)
                } else {
                    imageView.setColor(SkinHelper.getSkin().themeDarkColor)
                    action.call(true)
                }
            }

        }

    }


}