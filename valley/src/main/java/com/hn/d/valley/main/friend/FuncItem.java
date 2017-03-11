package com.hn.d.valley.main.friend;

import com.angcyo.uiview.utils.T_;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action0;

/**
 * Created by hewking on 2017/3/8.
 */
public class FuncItem extends AbsFriendItem {

    static FuncItem newfirend = new FuncItem("新的朋友",new Action0(){
        @Override
        public void call() {
            T_.show("新的朋友");

        }
    });

    static FuncItem groupMessage = new FuncItem("群聊",new Action0(){
        @Override
        public void call(){
            //进入群聊
            T_.show("进入群聊");
        }
    });

    Action0 action ;

    FuncItem(String text,Action0 action){
        this.text = text;
        this.action = action;
        itemType = ItemTypes.FUNC;
        groupText = null;
    }

    String text ;

    public static List<FuncItem> provide(){
        List<FuncItem> data = new ArrayList<>();
        data.add(newfirend);
        data.add(groupMessage);

        return data;
    }


    public void onFuncClick(){
        //checkNotNull
        action.call();
    }
}
