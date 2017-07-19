package com.hn.d.valley.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hewking on 2017/3/10.
 */
public class NetUtils {


    public static Bitmap createBitmapFromUrl(String s) {
//        if(NetworkStateReceiver.netType == NetworkUtils.NetworkType.NETWORK_NO){
//            return null;
//        }
        try {
            URL url = new URL(s);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.connect();
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
