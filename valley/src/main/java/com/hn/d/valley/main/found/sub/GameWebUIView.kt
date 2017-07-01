package com.hn.d.valley.main.found.sub

import com.angcyo.uiview.model.TitleBarPattern
import com.hn.d.valley.main.found.FoundUIView.GAME_URL
import com.hn.d.valley.x5.X5WebUIView

/**
 * Created by angcyo on 2017-07-01.
 */
class GameWebUIView : X5WebUIView(GAME_URL) {
    override fun getTitleBar(): TitleBarPattern {
        return createTitleBarPattern()
                .setShowBackImageView(true)
                .setTitleString("")
    }

    override fun onTitleBackListener(): Boolean {
        return super.onTitleBackListener()
    }
}
