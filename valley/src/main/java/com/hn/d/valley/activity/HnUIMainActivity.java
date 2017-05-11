package com.hn.d.valley.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.View;

import com.angcyo.uiview.RCrashHandler;
import com.angcyo.uiview.container.UILayoutImpl;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.utils.RUtils;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseActivity;
import com.hn.d.valley.bean.event.SwipeEvent;
import com.hn.d.valley.main.MainUIView;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.orhanobut.hawk.Hawk;

import java.io.File;

public class HnUIMainActivity extends BaseActivity {

    public static void launcher(Activity activity) {
        Intent intent = new Intent(activity, HnUIMainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.base_tran_to_left_enter,
                R.anim.base_tran_to_left_exit);
    }

    @Override
    protected void onLoadView() {
        startIView(new MainUIView());

        if (BuildConfig.DEBUG) {
            if (Hawk.get(RCrashHandler.KEY_IS_CRASH, false)) {
                //异常退出了

                UIDialog.build()
                        .setDialogTitle("程序异常退出")
                        .setDialogContent(Hawk.get(RCrashHandler.KEY_CRASH_MESSAGE, "-"))
                        .setOkText("打开文件")
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    RUtils.openFile(HnUIMainActivity.this,
                                            new File(Hawk.get(RCrashHandler.KEY_CRASH_FILE, "-")));
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .showDialog(mLayout);
            }
        }

        Hawk.put(RCrashHandler.KEY_IS_CRASH, false);
    }

    @Subscribe
    public void onEvent(SwipeEvent event) {
        ((UILayoutImpl) mLayout).translationLastView(event.offsetX);
    }

}
