package com.hn.d.valley.main.friend;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.main.message.groupchat.AddGroupChatUIView;
import com.hn.d.valley.main.message.groupchat.MyGroupUIView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by hewking on 2017/3/8.
 */
public class FuncItem<T> extends AbsFriendItem {

    static FuncItem newfirend = new FuncItem<>("新的朋友",new Action1<ILayout>(){
        @Override
        public void call(ILayout layout) {
            layout.startIView(new AddGroupChatUIView());
        }
    });

    static FuncItem groupMessage = new FuncItem<>("群聊",new Action1<ILayout>(){
        @Override
        public void call(ILayout layout){
            //进入群聊
            layout.startIView(new MyGroupUIView());
        }
    });


    Action1<T> action ;

    public FuncItem(String text, Action1<T> action){
        this.text = text;
        this.action = action;
        itemType = ItemTypes.FUNC;
        groupText = "";
    }

    String text ;

    public static List<FuncItem> provide(){
        List<FuncItem> data = new ArrayList<>();
        data.add(newfirend);
        data.add(groupMessage);
        return data;
    }


    public void onFuncClick(T t){
        //checkNotNull
        if (action == null) {
            return;
        }
        action.call(t);
    }
}
