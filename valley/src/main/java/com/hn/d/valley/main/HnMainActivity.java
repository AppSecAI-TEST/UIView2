package com.hn.d.valley.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hn.d.valley.R;
import com.hn.d.valley.base.inner.RSupportActivity;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/28 17:02
 * 修改人员：Robi
 * 修改时间：2016/12/28 17:02
 * 修改备注：
 * Version: 1.0.0
 */
public class HnMainActivity extends RSupportActivity {

    public static void launcher(Activity activity) {
        Intent intent = new Intent(activity, HnMainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.base_tran_to_left_enter, R.anim.base_tran_to_left_exit);
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        //overridePendingTransition(R.anim.base_tran_to_right_enter, R.anim.base_tran_to_right_exit);
    }

    @Override
    protected SupportFragment getRootFragment() {
        return MainFragment.newInstance();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mViewHolder.postDelay(new Runnable() {
            @Override
            public void run() {
                start(MainFragment.newInstance());
            }
        }, 1000);
    }
}
