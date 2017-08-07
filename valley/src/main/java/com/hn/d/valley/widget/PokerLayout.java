package com.hn.d.valley.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.angcyo.uiview.utils.ScreenUtil;
import com.hn.d.valley.R;

/**
 * Created by 60491 on 2017/8/4.
 */

public class PokerLayout extends ViewGroup{

    public static @DrawableRes int[] poker_res = {
            R.drawable.pocker_0x00,R.drawable.pocker_0x01,R.drawable.pocker_0x02,R.drawable.pocker_0x03,R.drawable.pocker_0x04,R.drawable.pocker_0x05,
            R.drawable.pocker_0x06,R.drawable.pocker_0x07,R.drawable.pocker_0x08,R.drawable.pocker_0x09,R.drawable.pocker_0x10,R.drawable.pocker_0x11,
            R.drawable.pocker_0x12,R.drawable.pocker_0x13,R.drawable.pocker_0x14,R.drawable.pocker_0x15,R.drawable.pocker_0x16,R.drawable.pocker_0x17,
            R.drawable.pocker_0x18,R.drawable.pocker_0x19,R.drawable.pocker_0x20,R.drawable.pocker_0x21,R.drawable.pocker_0x22,R.drawable.pocker_0x23,
            R.drawable.pocker_0x24,R.drawable.pocker_0x25,R.drawable.pocker_0x26,R.drawable.pocker_0x27,R.drawable.pocker_0x28,R.drawable.pocker_0x29,
            R.drawable.pocker_0x30,R.drawable.pocker_0x31,R.drawable.pocker_0x32,R.drawable.pocker_0x33,R.drawable.pocker_0x34,R.drawable.pocker_0x35,
            R.drawable.pocker_0x36,R.drawable.pocker_0x37,R.drawable.pocker_0x38,R.drawable.pocker_0x39,R.drawable.pocker_0x40,R.drawable.pocker_0x41
            ,R.drawable.pocker_0x42,R.drawable.pocker_0x43,R.drawable.pocker_0x44,R.drawable.pocker_0x45,R.drawable.pocker_0x46,R.drawable.pocker_0x47
            ,R.drawable.pocker_0x48,R.drawable.pocker_0x49,R.drawable.pocker_0x50,R.drawable.pocker_0x51,R.drawable.pocker_0x52,R.drawable.pocker_0x53
    };

    private TypedArray taArray;
    private Context context;

    private int h;
    private int w;
    private int pokerCount;

    public PokerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        taArray = context.obtainStyledAttributes(R.styleable.PokerLayout);
        w = taArray.getDimensionPixelSize(R.styleable.PokerLayout_horizontal_spacing, 50);
        h = taArray.getDimensionPixelSize(R.styleable.PokerLayout_vertical_spacing, 0);
        taArray.recycle();
    }

    //"0x19,0x45,0x34,0x51,0x52"
    public void init(String values) {
        if (TextUtils.isEmpty(values)) {
            return;
        }

        String[] split = values.split(",");
        int[] pokers = new int[split.length];
        int index = 0;
        for (String value : split) {
            String subValue = value.substring(2);
            pokers[index ++] = poker_res[Integer.valueOf(subValue)];
        }
        pokerCount = split.length;
        initView(pokers);
    }

    private void initView(@DrawableRes int[] res) {
        removeAllViews();
        LayoutParams params = new LayoutParams(ScreenUtil.dip2px(60),ScreenUtil.dip2px(90));
        for (int i = 0 ; i < pokerCount; i ++) {
            addView(new ImageView(context),params);
        }

        for (int i = 0 ; i < pokerCount ; i ++) {
            ((ImageView)getChildAt(i)).setImageResource(res[i]);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.v("OnLayout", "L="+l+" T="+t+" R="+r+" B="+b + "w ::" + w + "h ::" + h);
        int count = getChildCount();
        for(int i= 0;i < count;i ++){
            View child = getChildAt(i);
            if(child.getVisibility() != View.GONE){
                int measureHeight = child.getMeasuredHeight();
                int measuredWidth = child.getMeasuredWidth();
                child.layout(w * i, h * i, w * i + measuredWidth, h * i + measureHeight);
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int wrapwidth = 0;
        int wrapheight = 0;
        int widthmode = MeasureSpec.getMode(widthMeasureSpec);
        int heightmode = MeasureSpec.getMode(heightMeasureSpec);
        int count = getChildCount();
        //遍历child view
        for(int i=0;i<count;i++){
            View child = getChildAt(i);
            if(child.getVisibility() != View.GONE){
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                int childwidth = child.getMeasuredWidth();
                int childheight = child.getMeasuredHeight();
                //计算整个ViewGroup所占的大小
                if(i==0){
                    wrapwidth += childwidth;
                    wrapheight += childheight;
                }else {
                    wrapheight += h;
                    wrapwidth += w;
                }
            }
        }
        setMeasuredDimension(widthmode==MeasureSpec.EXACTLY?width:wrapwidth,
                heightmode == MeasureSpec.EXACTLY?height:wrapheight);
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PokerLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }


    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray ta = c.obtainStyledAttributes(attrs, R.styleable.PokerLayout);
            ta.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
