package com.hn.d.valley.activity;

import android.app.Activity;
import android.content.Intent;

import com.angcyo.uiview.container.UILayoutImpl;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseActivity;
import com.hn.d.valley.base.constant.Action;
import com.hn.d.valley.bean.event.SwipeEvent;
import com.hn.d.valley.control.MainControl;
import com.hn.d.valley.main.MainUIView;
import com.hwangjr.rxbus.annotation.Subscribe;

public class HnUIMainActivity extends BaseActivity {

    public static void launcher(Activity activity) {
        Intent intent = new Intent(activity, HnUIMainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.base_tran_to_left_enter,
                R.anim.base_tran_to_left_exit);
    }

    @Override
    protected void onLoadView() {
        Action.app_launch();

        startIView(new MainUIView());

        MainControl.checkCrash(mLayout);
    }

    @Subscribe
    public void onEvent(SwipeEvent event) {
        ((UILayoutImpl) mLayout).translationLastView(event.offsetX);
    }

    @Override
    protected void onUIBackPressed() {
        moveTaskToBack(true);
    }
}
