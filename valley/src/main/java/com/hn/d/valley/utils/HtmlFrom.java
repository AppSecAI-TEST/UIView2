package com.hn.d.valley.utils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.angcyo.library.utils.L;
import com.hn.d.valley.main.message.groupchat.RequestCallback;

import org.jsoup.Jsoup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hn.d.valley.utils.Regex.IMAGE_TAG_PATTERN;
import static com.hn.d.valley.utils.Regex.TITLE_PATTERN2;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/13 19:27
 * 修改人员：hewking
 * 修改时间：2017/04/13 19:27
 * 修改备注：
 * Version: 1.0.0
 */
public class HtmlFrom {

    public static class LinkBean {

        String title;
        String img;
        String content;

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getImg() {
            return img;
        }

        public LinkBean(String title, String img,String content) {
            this.title = title;
            this.img = img;
            this.content = content;
        }
    }


    public static void getPageAsyc(String url, final RequestCallback<LinkBean> callback) {
        AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {
                return getStaticPageByBytes(params[0],callback);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                L.e("getPageAsyc" ,s);
                if (TextUtils.isEmpty(s)) {
                    callback.onError("error");
                    return;
                }

//                String img_s = regexMatch(s,IMAGE_TAG_PATTERN);
//                String title_s = regexMatch(s,TITLE_PATTERN2);

                String img = Jsoup.parse(s).select("img").attr("src");
                String title = Jsoup.parse(s).select("title").text();
                String content = Jsoup.parse(s).select("p").text();
                if (TextUtils.isEmpty(content)) {
                    content = s;
                }
                callback.onSuccess(new LinkBean(title,img,content));

            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }
        }.execute(url);
    }

    public static String  regexMatch(String content , String pattern ) {
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(content);
        if (matcher.find()) {
//            Log.e("regexmatch",matcher.group(0));
//            Matcher m = Pattern.compile("<title>([\\s\\S]*)</title>").matcher(matcher.group(0));
//            Matcher m2 = Pattern.compile("<img.*?\"([\\s\\S]*)\".*?/>").matcher(matcher.group(0));
//            while(m2.find()){
//                Log.e("regexmatch img",m2.group(1));
//                return m2.group(1);
//            }
//
//            while (m.find()) {
//                Log.e("regexmatch title",m.group(1));
//                return m.group(1);
//            }
            return matcher.group(0);
        }
        return "";
    }



    private static String getStaticPageByBytes(String surl, RequestCallback<LinkBean> callback){

        L.i("getStaticPageByBytes", surl );

        HttpURLConnection connection = null;
        InputStream is = null;

        ByteArrayOutputStream fos = new ByteArrayOutputStream();

        try {
            URL url = new URL(surl);
            connection = (HttpURLConnection)url.openConnection();

            int code = connection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == code) {
                connection.connect();
                is = connection.getInputStream();
                fos = new ByteArrayOutputStream();

                int i;
                while((i = is.read()) != -1){
                    fos.write(i);
                }

                is.close();
                fos.close();
            } else {
//                callback.onError(code + "");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
//            callback.onError(e.getMessage());
        } catch (IOException e) {
//            callback.onError(e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return fos.toString();
    }

}
