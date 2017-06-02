package com.hn.d.valley.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.UILayoutImpl;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.umeng.UM;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseActivity;
import com.hn.d.valley.base.constant.Action;
import com.hn.d.valley.bean.event.SwipeEvent;
import com.hn.d.valley.control.AutoLoginControl;
import com.hn.d.valley.control.MainControl;
import com.hn.d.valley.main.MainUIView;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.orhanobut.hawk.Hawk;

//import com.hn.d.valley.main.message.avchat.AVChatControl;
//import com.hn.d.valley.main.message.avchat.ui.AVChatUIView;

//import static com.hn.d.valley.main.message.avchat.ui.AVChatUIView.FROM_UNKNOWN;
//import static com.hn.d.valley.main.message.avchat.ui.AVChatUIView.KEY_CALL_CONFIG;
//import static com.hn.d.valley.main.message.avchat.ui.AVChatUIView.KEY_IN_CALLING;
//import static com.hn.d.valley.main.message.avchat.ui.AVChatUIView.KEY_SOURCE;

public class HnUIMainActivity extends BaseActivity {

    public static final String KEY_IS_LOGIN_SUCCESS = "key_is_login_success";

    public static void launcher(Activity activity, boolean isLoginSuccess) {
        Intent intent = new Intent(activity, HnUIMainActivity.class);
        intent.putExtra(KEY_IS_LOGIN_SUCCESS, isLoginSuccess);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.base_tran_to_left_enter,
                R.anim.base_tran_to_left_exit);
    }

//    /**
//     * incoming call
//     *
//     * @param context
//     */
//    public static void launch(Context context, AVChatData config, int source) {
////        needFinish = false;
//        Intent intent = new Intent();
//        intent.setClass(context, HnUIMainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(KEY_CALL_CONFIG, config);
//        intent.putExtra(KEY_IN_CALLING, true);
//        intent.putExtra(KEY_SOURCE, source);
//        context.startActivity(intent);
//    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity,HnUIMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
    }

    @Override
    protected void onLoadView(Intent intent) {
        Action.app_launch();
        final String versionName = RUtils.getAppVersionName(this);
        if (Hawk.get(versionName, true)) {
            toSplashActivity();
            return;
        }

//        boolean isCalling = intent.getBooleanExtra(KEY_IN_CALLING, false);
//        if (isCalling) {
//            AVChatData config = (AVChatData) intent.getSerializableExtra(KEY_CALL_CONFIG);
//            int source = intent.getIntExtra(KEY_SOURCE,FROM_UNKNOWN);
//            showAVChatUI(config,source);
//            return;
//        }

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

//    private void showAVChatUI(AVChatData config,int source) {
//        AVChatUIView.start(mLayout,config,source);
//    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void toSplashActivity() {
        HnSplashActivity.launcher(HnUIMainActivity.this, false);
        finish();
    }

    private void showMainUIView() {
        startIView(new MainUIView());
        MainControl.checkCrash(mLayout);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //RNim.initOnce(getApplication());
    }

    @Subscribe
    public void onEvent(SwipeEvent event) {
        ((UILayoutImpl) mLayout).translationLastView(event.offsetX);
    }

    @Override
    protected void onUIBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UM.onActivityResult(requestCode, resultCode, data);
    }
}
