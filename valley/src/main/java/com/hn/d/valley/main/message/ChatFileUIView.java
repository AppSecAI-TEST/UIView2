package com.hn.d.valley.main.message;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.skin.ISkin;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;

/**
 * Created by hewking on 2017/3/21.
 */
public class ChatFileUIView extends SingleRecyclerUIView {

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString("聊天文件");
    }

    @Override
    protected RExBaseAdapter initRExBaseAdapter() {
        return null;
    }

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);

    }
}
