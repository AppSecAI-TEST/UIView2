package com.hn.d.valley.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.angcyo.uiview.widget.RecordTimeView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：00:00/10:10 时间显示格式
 * 创建人员：Robi
 * 创建时间：2017/05/08 18:23
 * 修改人员：Robi
 * 修改时间：2017/05/08 18:23
 * 修改备注：
 * Version: 1.0.0
 */
public class HnPlayTimeView extends AppCompatTextView {

    long playTime = -1;

    public HnPlayTimeView(Context context) {
        super(context);
    }

    public HnPlayTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnPlayTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ensureText();
    }

    /**
     * 调用之前, 请先调用setText设置总时长
     */
    public void setPlayTime(long playTime) {
        this.playTime = playTime;
        ensureText();
    }

    private void ensureText() {
        String textString = String.valueOf(getText());
        if (playTime < 0) {
            //播放时间小于0, 说去需要取消显示播放时间
            if (!TextUtils.isEmpty(textString)) {
                String[] strings = textString.split("/");
                if (strings.length > 1) {
                    textString = strings[1];
                }
            }
        } else {
            //需要拼接播放时长
            if (!TextUtils.isEmpty(textString)) {
                String[] strings = textString.split("/");
                if (strings.length > 1) {
                    textString = RecordTimeView.formatMMSS(playTime) + "/" + strings[1];
                } else {
                    textString = RecordTimeView.formatMMSS(playTime) + "/" + textString;
                }
            } else {
                textString = RecordTimeView.formatMMSS(playTime) + "/00:00";
            }
        }

        setText(textString);
    }
}
