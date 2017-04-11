package com.hn.d.valley.main.friend;

import com.angcyo.uiview.container.ILayout;
import com.hn.d.valley.main.message.groupchat.MyGroupUIView;
import com.hn.d.valley.sub.user.NewFriend2UIView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by hewking on 2017/3/8.
 */
public class FuncItem<T> extends AbsContactItem {

    static FuncItem newfirend = new FuncItem<>("新的朋友",new Action1<ILayout>(){
        @Override
        public void call(ILayout layout) {
            layout.startIView(new NewFriend2UIView());
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

    public String getText() {
        return text;
    }

    String text ;

    public static List<AbsContactItem> provide(){
        List<AbsContactItem> data = new ArrayList<>();
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
