package com.hn.d.valley.sub.other;

import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.base.BaseRecyclerUIView;

/**
 * Created by angcyo on 2017-01-15.
 */

public abstract class SingleRSubscriber<T> extends RSubscriber<T> {

    private BaseRecyclerUIView mBaseRecyclerUIView;

    public SingleRSubscriber(BaseRecyclerUIView baseRecyclerUIView) {
        mBaseRecyclerUIView = baseRecyclerUIView;
    }

    @Override
    final public void onSucceed(T bean) {
        mBaseRecyclerUIView.showContentLayout();
        onResult(bean);
    }

    protected abstract void onResult(T bean);

    @Override
    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
        super.onEnd(isError, isNoNetwork, e);
        mBaseRecyclerUIView.hideLoadView();
        mBaseRecyclerUIView.onUILoadDataFinish();
        if (isError) {
            T_.error(e.getMsg());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBaseRecyclerUIView.showLoadView();
    }
}
