package com.hn.d.valley.main.found.sub

import com.angcyo.uiview.model.TitleBarPattern
import com.hn.d.valley.x5.X5WebUIView

/**
 * Created by angcyo on 2017-07-01.
 */
class GameWebUIView(gameUrl: String) : X5WebUIView(gameUrl) {
    override fun getTitleBar(): TitleBarPattern {
        return createTitleBarPattern()
                .setShowBackImageView(true)
                .setTitleString("")
    }

    override fun onTitleBackListener(): Boolean {
        return super.onTitleBackListener()
    }
}
