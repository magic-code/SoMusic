package com.magic.somusic.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2015/9/30.
 */
public class MyScrollView extends ScrollView {
    private OnScrollListener listener;

    public MyScrollView(Context context) {
        super(context);
    }
    public MyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener!=null){
            listener.onScroll(this,l,t,oldl,oldt);
        }
    }

    public void setOnScrollListener(OnScrollListener listener){
        this.listener = listener;
    }
    public interface OnScrollListener{
        public void onScroll(MyScrollView view,int x,int y,int oldx,int oldy);
    }
}
