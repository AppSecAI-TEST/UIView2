package com.hn.d.valley.main.friend;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.FriendListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.service.ContactService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewking on 2017/3/7.
 */
public class Friend2UIView extends BaseRecyclerUIView<String,FriendBean,String> {

//    @BindView(R.id.iv_index_bg)
//    ImageView iv_index;
//    @BindView(R.id.tv_friend_index)
//    TextView tv_index;
//    @BindView(R.id.sidebar_friend_index)
//    LetterIndexView sidebar_friend;

    private FriendsControl mFriendsControl;

    @Override
    protected void initOnShowContentLayout() {
        mFriendsControl = new FriendsControl(mActivity,mOtherILayout);
        super.initOnShowContentLayout();

    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> leftItems = new ArrayList<>();
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        leftItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.top_add_friends).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //T_.show(mActivity.getString(R.string.searchUser));
            }
        }));
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.contacts)).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T_.show(mActivity.getString(R.string.contacts));
            }
        }));

        return super.getTitleBar().setTitleString(mActivity.getString(R.string.nav_message_text)).setLeftItems(leftItems).setRightItems(rightItems);
    }

    @Override
    protected RExBaseAdapter<String, FriendBean, String> initRExBaseAdapter() {
//        return new FriendsAdapter(mActivity,mFriendsControl,mSubscriptions);
       return null;
    }

    @Override
    protected void inflateOverlayLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        super.inflateOverlayLayout(baseContentLayout, inflater);
//        inflate(R.layout.layout_message_friend);
    }

    @Override
    protected boolean hasScrollListener() {
        return false;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        RRetrofit.create(ContactService.class)
                .friends(Param.buildMap("uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(FriendListModel.class))
                .subscribe(new BaseSingleSubscriber<FriendListModel>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        T_.error(code + ":" + msg);
                    }

                    @Override
                    public void onSucceed(FriendListModel bean) {
                        super.onSucceed(bean);
                        if(bean == null ) {
                            onUILoadDataEnd();
                        }

                        List<FriendBean>  data_list = bean.getData_list();
                        onUILoadDataEnd(data_list);

                        T_.show("success");
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                    }
                });
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }
}
