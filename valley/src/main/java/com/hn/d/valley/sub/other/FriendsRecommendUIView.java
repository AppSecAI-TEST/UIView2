package com.hn.d.valley.sub.other;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.service.ContactService;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class FriendsRecommendUIView extends UserInfoRecyclerUIView {

    boolean isInSubUIView = true;

    public FriendsRecommendUIView(boolean isInSubUIView) {
        this.isInSubUIView = isInSubUIView;
    }

    public FriendsRecommendUIView(String uid) {
        this(true);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        if (isInSubUIView) {
            return null;
        } else {
            return super.getTitleBar().setTitleString(mActivity, R.string.frient_tip);
        }
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(ContactService.class)
                .recommendUser(Param.buildMap("page:" + page))
                .compose(Rx.transformerList(LikeUserInfoBean.class))
                .subscribe(new SingleRSubscriber<List<LikeUserInfoBean>>(this) {
                    @Override
                    protected void onResult(List<LikeUserInfoBean> bean) {
                        if (bean == null || bean.isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            for (LikeUserInfoBean b : bean) {
//                                b.setIs_attention(1);
                            }
                            onUILoadDataEnd(bean);
                        }
                    }
                }));
    }

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        return super.initItemDecoration()
                .setDividerSize(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_line))
                .setMarginStart(mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi))
                .setDrawLastLine(true);
    }

    @Override
    protected boolean isContact(LikeUserInfoBean dataBean) {
        return dataBean.getIs_contact() == 1;
    }

    @Override
    protected boolean isAttention(LikeUserInfoBean dataBean) {
        return dataBean.getIs_attention() == 1;
    }

    @Override
    protected void onSetDataBean(LikeUserInfoBean dataBean, boolean value) {
        if (!value) {
            dataBean.setIs_contact(0);
        }
        dataBean.setIs_attention(value ? 1 : 0);
    }
}
