package com.hn.d.valley.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;

import com.angcyo.uiview.container.SwipeBackLayout;
import com.angcyo.uiview.container.UILayoutImpl;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseActivity;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.event.SwipeEvent;
import com.hn.d.valley.main.message.P2PChatUIView;
import com.hn.d.valley.utils.RBus;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：聊天界面
 * 创建人员：Robi
 * 创建时间：2016/12/12 17:19
 * 修改人员：Robi
 * 修改时间：2016/12/12 17:19
 * 修改备注：
 * Version: 1.0.0
 */
public class HnChatActivity extends BaseActivity {

    String mAccount;

    public static void launcher(Activity activity, String account) {
        Intent intent = new Intent(activity, HnChatActivity.class);
        intent.putExtra(Constant.USER_ACCOUNT, account);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.base_tran_to_left_enter,
                R.anim.base_tran_to_left_exit);
    }

    private int getOffsetX() {
        return (int) (getResources().getDisplayMetrics().widthPixels * 0.3f);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccount = getIntent().getStringExtra(Constant.USER_ACCOUNT);
    }

    @Override
    protected void onLoadView() {
        ((UILayoutImpl) mLayout).setEnableRootSwipe(true);
        ((UILayoutImpl) mLayout).setOnPanelSlideListener(new SwipeBackLayout.OnPanelSlideListener() {
            @Override
            public void onStateChanged(int state) {
                if (state == ViewDragHelper.STATE_DRAGGING) {
                    //将要开始拖动
                    RBus.post(new SwipeEvent(-getOffsetX()));
                }
            }

            @Override
            public void onRequestClose() {

            }

            @Override
            public void onRequestOpened() {
                //恢复默认
                RBus.post(new SwipeEvent(0));
            }

            @Override
            public void onSlideChange(float percent) {
                RBus.post(new SwipeEvent((int) (-getOffsetX() * percent)));
            }
        });
        P2PChatUIView.start(mLayout, mAccount, SessionTypeEnum.P2P);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.base_tran_to_right_enter,
                R.anim.base_tran_to_right_exit);
    }
}
