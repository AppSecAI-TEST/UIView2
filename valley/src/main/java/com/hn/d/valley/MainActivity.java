package com.hn.d.valley;

import android.app.Activity;
import android.content.Intent;

import com.hn.d.valley.base.BaseActivity;

public class MainActivity extends BaseActivity {

    public static void launcher(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.default_window_tran_enter_anim, R.anim.default_window_tran_exit_anim);
    }

    @Override
    protected void onLoadView() {

    }
}
