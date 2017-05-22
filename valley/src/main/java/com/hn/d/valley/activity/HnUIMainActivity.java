package com.hn.d.valley.activity;

import android.app.Activity;
import android.content.Intent;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.UILayoutImpl;
import com.angcyo.uiview.utils.RUtils;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseActivity;
import com.hn.d.valley.base.constant.Action;
import com.hn.d.valley.bean.event.SwipeEvent;
import com.hn.d.valley.control.AutoLoginControl;
import com.hn.d.valley.control.MainControl;
import com.hn.d.valley.main.MainUIView;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.orhanobut.hawk.Hawk;

public class HnUIMainActivity extends BaseActivity {

    public static final String KEY_IS_LOGIN_SUCCESS = "key_is_login_success";

    public static void launcher(Activity activity, boolean isLoginSuccess) {
        Intent intent = new Intent(activity, HnUIMainActivity.class);
        intent.putExtra(KEY_IS_LOGIN_SUCCESS, isLoginSuccess);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.base_tran_to_left_enter,
                R.anim.base_tran_to_left_exit);
    }

    @Override
    protected void onLoadView(Intent intent) {
        Action.app_launch();
        final String versionName = RUtils.getAppVersionName(this);
        if (Hawk.get(versionName, true)) {
            toSplashActivity();
            return;
        }

        boolean isLoginSuccess = intent.getBooleanExtra(KEY_IS_LOGIN_SUCCESS, false);
        if (isLoginSuccess) {
            MainControl.onLoginIn();
            showMainUIView();
        } else {
            AutoLoginControl.instance().startLogin(this, new AutoLoginControl.AutoLoginListener() {
                @Override
                public void onLoginError() {
                    L.w("自动登录失败.");
                    MainControl.onLoginOut();
                    toSplashActivity();
                }

                @Override
                public void onLoginSuccess() {
                    showMainUIView();
                    L.w("自动登录成功.");
                }
            });
        }
    }

    private void toSplashActivity() {
        HnSplashActivity.launcher(HnUIMainActivity.this, false);
        finish();
    }

    private void showMainUIView() {
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
