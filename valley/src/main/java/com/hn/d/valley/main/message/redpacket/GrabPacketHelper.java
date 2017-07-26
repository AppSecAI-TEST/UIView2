package com.hn.d.valley.main.message.redpacket;

import android.support.annotation.NonNull;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.wallet.WalletService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import rx.functions.Action;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/26 15:15
 * 修改人员：hewking
 * 修改时间：2017/04/26 15:15
 * 修改备注：
 * Version: 1.0.0
 */
public class GrabPacketHelper {

    @NonNull
    public static Integer parseResult(ResponseBody responseBody) {
        int code = -1;
        try {
            String body = responseBody.string();
            L.i(OpenRedPacketUIDialog.TAG,"parsebody" + body);
            JSONObject jsonObject = new JSONObject(body);
            code = jsonObject.optInt("code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public static void balanceCheck(final Action1<Integer> action) {
        RRetrofit.create(WalletService.class)
                .balanceCheck(Param.buildInfoMap("uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(Integer.class))
                .subscribe(new BaseSingleSubscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onSucceed(Integer bean) {
                        super.onSucceed(bean);
                        action.call(bean);
                    }
                });
    }



}
