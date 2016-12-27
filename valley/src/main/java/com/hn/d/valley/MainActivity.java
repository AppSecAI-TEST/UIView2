package com.hn.d.valley;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.maps.MapView;

public class MainActivity extends AppCompatActivity {

    public static void launcher(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.base_tran_to_top, R.anim.base_tran_to_left);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_amap_layout);
        MapView mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
    }
}
