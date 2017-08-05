package com.hn.d.valley.emoji;

import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.file.FileUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.attachment.CustomExpressionMsg;
import com.hn.d.valley.main.message.redpacket.PayUIDialog;
import com.hn.d.valley.utils.MathUtils;
import com.netease.nimlib.sdk.msg.constant.StickerEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.hn.d.valley.emoji.StickerManager.CATEGORY_HN;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/8/5
 * 修改人员：cjh
 * 修改时间：2017/8/5
 * 修改备注：
 * Version: 1.0.0
 */
public class StickerUtil {

    public static CustomExpressionMsg stickerSelected(String categoryName, String stickerName){
        CustomExpressionMsg expressionMsg = new CustomExpressionMsg(FileUtil.getFileNameNoEx(stickerName));
        int type = StickerEnum.Companion.valueOfType(categoryName).getValue();
        expressionMsg.setType(type);

        JSONObject object = new JSONObject();
        // 骰子
        if (type == 3) {
            String count = expressionMsg.getMsg().split("_")[1];
            List<Integer> values = new ArrayList<>();
            for (int i = 0 ; i < Integer.valueOf(count);i++){
                int value = MathUtils.nextInt(1,6);
                values.add(value);
            }
            try {
                object.put("dicecount", RUtils.connect(values));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            expressionMsg.setExtend(object.toString());

        } else if (type == 4) {
            String count = expressionMsg.getMsg().split("_")[1];
            List<String> values = new ArrayList<>();
            for (int i = 0 ; i < Integer.valueOf(count);i++){
                int value = MathUtils.nextInt(1,54);
                if (value < 10) {
                    values.add("0x0" + value);
                }else {
                    values.add("0x" + value);
                }
            }
            try {
                object.put("pokercount", RUtils.connect(values));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            expressionMsg.setExtend(object.toString());
        }
        return expressionMsg;
    }

}
