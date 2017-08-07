package com.hn.d.valley.widget.groupView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.angcyo.uiview.kotlin.getLocationInParent
import com.angcyo.uiview.kotlin.v
import com.hn.d.valley.R

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：广场红包视图自动布局控制
 * 创建人员：Robi
 * 创建时间：2017/07/26 13:49
 * 修改人员：Robi
 * 修改时间：2017/07/26 13:49
 * 修改备注：
 * Version: 1.0.0
 */
class HnHotPackageLayout(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = (0..childCount - 2).sumBy {
            val childAt = getChildAt(it)
            if (childAt.visibility == View.VISIBLE) {
                childAt.measuredHeight +
                        (childAt.layoutParams as MarginLayoutParams).topMargin +
                        (childAt.layoutParams as MarginLayoutParams).bottomMargin
            } else {
                0
            }
        }

        setMeasuredDimension(measuredWidth, height)
    }

    private val mediaRect: Rect by lazy {
        Rect()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //super.onLayout(changed, l, t, r, b)
        var offsetTop = 0
        for (i in 0..childCount - 2) {
            getChildAt(i).apply {
                if (visibility == View.VISIBLE) {
                    offsetTop += (layoutParams as MarginLayoutParams).topMargin
                    layout(0, offsetTop, r, offsetTop + measuredHeight)
                    offsetTop += measuredHeight
                }
            }
        }
        val mediaImageView: View? = v(R.id.media_image_view)//显示视频缩略图的视图
        val hotPackageView: View? = v(R.id.hot_package_view)//红包提示视图
        if (mediaImageView != null && hotPackageView != null) {
            if (hotPackageView.visibility == View.VISIBLE) {
                getLocationInParent(mediaImageView, mediaRect)
                hotPackageView.layout(mediaRect.right - hotPackageView.measuredWidth / 2, mediaRect.top - hotPackageView.measuredHeight / 2,
                        mediaRect.right + hotPackageView.measuredWidth / 2, mediaRect.top + hotPackageView.measuredHeight / 2)
            }
        }
    }

    private var mBackgroundDrawable: Drawable? = null

    override fun draw(canvas: Canvas) {
        if (mBackgroundDrawable != null) {
            mBackgroundDrawable?.bounds = canvas.clipBounds
            mBackgroundDrawable?.draw(canvas)
        }
        super.draw(canvas)
    }

    fun setRBackgroundDrawable(@ColorInt color: Int) {
        setRBackgroundDrawable(ColorDrawable(color))
    }

    fun setRBackgroundDrawable(drawable: Drawable) {
        mBackgroundDrawable = drawable
    }
}