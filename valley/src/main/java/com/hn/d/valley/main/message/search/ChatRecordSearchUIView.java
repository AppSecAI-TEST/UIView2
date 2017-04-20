package com.hn.d.valley.main.message.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.main.message.query.TextQuery;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/28 16:57
 * 修改人员：hewking
 * 修改时间：2017/03/28 16:57
 * 修改备注：
 * Version: 1.0.0
 */
public class ChatRecordSearchUIView extends GlobalSearchUIView2 {

    protected static final String KEY_SESSION_ID = "key_account";
    protected static final String KEY_SESSION_TYPE = "key_sessiontype";

    private String mSessionId;
    private SessionTypeEnum sessionType;

    public ChatRecordSearchUIView(Options options) {
        super(options);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
//        return super.getTitleBar().setTitleString("聊天信息");
        return null;
    }

    public static void start(ILayout mLayout,Options options, String sessionId, SessionTypeEnum sessionType,int[] itemTypes) {
        Bundle bundle = new Bundle();
        bundle.putIntArray(ITEMTYPES,itemTypes);
        bundle.putString(KEY_SESSION_ID, sessionId);
        bundle.putInt(KEY_SESSION_TYPE, sessionType.getValue());
        ChatRecordSearchUIView targetView = new ChatRecordSearchUIView(options);
        mLayout.startIView(targetView, new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);

        Bundle bundle = param.mBundle;
        if (bundle != null) {
            mSessionId = bundle.getString(KEY_SESSION_ID);
            sessionType = SessionTypeEnum.typeOfValue(bundle.getInt(KEY_SESSION_TYPE));
        }
    }

    @Override
    protected void buildSearchView() {
        RxTextView.textChanges(mSearchInputView)
                .debounce(Constant.DEBOUNCE_TIME_700, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        if (TextUtils.isEmpty(charSequence)) {
                            mEmptyTipView.setText("");
                        } else {
                            TextQuery textQuery = new TextQuery(charSequence.toString());
                            textQuery.extra = new Object[]{sessionType,mSessionId};
                            mPresenter.search(textQuery, itemTypes);
                        }
                    }
                });
    }
}
