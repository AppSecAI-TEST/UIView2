package com.hn.d.valley.main.message.chat;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseActivity;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

public class ChatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void start(Activity activity,String sessionId) {
        Intent intent = new Intent(activity,ChatActivity.class);
        intent.putExtra("sessionId",sessionId);
        activity.startActivity(intent);
    }

    @Override
    protected void onLoadView(Intent intent) {
        Intent intent1 = getIntent();
        String sessionId = intent1.getStringExtra("sessionId");
        SessionHelper.startP2PSession(mLayout,sessionId, SessionTypeEnum.P2P);
    }
}
