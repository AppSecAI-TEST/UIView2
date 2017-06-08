package com.hn.d.valley.main.friend;

import android.support.annotation.DrawableRes;

import com.angcyo.uiview.container.ILayout;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter;
import com.hn.d.valley.main.message.groupchat.MyGroupUIView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by hewking on 2017/3/8.
 */
public class FuncItem<T> extends AbsContactItem {


    Action1<T> action;

    String text;

    @DrawableRes
    int drawableRes;


    public FuncItem(String text, Action1<T> action) {
        this.text = text;
        this.action = action;
        itemType = ItemTypes.FUNC;
        groupText = "";
    }

    public FuncItem(String text, int type, Action1<T> action) {
        this(text, action);
        itemType = type;
    }

    public String getText() {
        return text;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }


    public FuncItem(String text,Action1<T> action,  int drawableRes) {
        this(text,action);
        this.drawableRes = drawableRes;
    }

    public void onFuncClick(T t) {

        //checkNotNull
        if (action == null) {
            return;
        }
        action.call(t);
    }


    static FuncItem newfirend = new FuncItem<>("新的朋友",new Action1<ILayout>() {
        @Override
        public void call(ILayout layout) {
            layout.startIView(new FriendNewUIView2());
        }
    },R.drawable.new_friend);


    public static FuncItem groupMessage = new FuncItem<>("群聊", new Action1<ILayout>() {
        @Override
        public void call(ILayout layout) {
            //进入群聊
            layout.startIView(new MyGroupUIView(new BaseContactSelectAdapter.Options()));
        }
    },R.drawable.group_chat_n);


    public static List<AbsContactItem> provide() {
        List<AbsContactItem> data = new ArrayList<>();
        data.add(groupMessage);
        data.add(newfirend);
        return data;
    }



}
